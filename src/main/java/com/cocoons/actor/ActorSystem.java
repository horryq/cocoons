package com.cocoons.actor;

import java.text.MessageFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.LockSupport;

import com.cocoons.harbor.HarborServer;

/**
 *
 * @author qinguofeng
 */
public class ActorSystem {
	// private int threadNum = 0;

	private Map<String, ActorRef> actorsRefMap = new ConcurrentHashMap<>();
	private Dispatcher dispatcher;

	private String systemName;
	private String harborName;

	public ActorSystem(String name) {
		this.systemName = name;
	}

	private String wrapActorName(String actorName) {
		return MessageFormat.format("{0}:{1}", systemName, actorName);
	}

	public ActorRef actor(String name, Actor actor) {
		name = wrapActorName(name);

		actor.setContext(name, this);
		dispatcher.addActor(name, actor);
		ActorRef ref = new ActorRef(name, this);
		actorsRefMap.put(name, ref);
		return ref;
	}

	public ActorRef remoteActorOf(String remoteName) {
		ActorRef ref = actorsRefMap.get(remoteName);
		if (ref == null) {
			ref = new ActorRef(remoteName, this);
			actorsRefMap.put(remoteName, ref);
		}
		return ref;
	}

	private boolean isLocalActor(String name) {
		final String systemNamePrefix = MessageFormat
				.format("{0}:", systemName);
		return name.startsWith(systemNamePrefix);
	}

	public ActorRef getActorRefOf(String name) {
		if (name == null) {
			return null;
		}

		ActorRef ref = actorsRefMap.get(name);
		if (ref == null && !isLocalActor(name)) {
			ref = remoteActorOf(name);
		}
		return ref;
	}

	public void sendMsgTo(ActorMessage msg) {
		dispatcher.sendMsg(msg);
	}

	public String getSid() {
		return UUID.randomUUID().toString();
	}

	public void startHarborService(int port) {
		harborName = wrapActorName(HarborServer.HARBOR);
		ActorRef ref = actor(HarborServer.HARBOR, new HarborServer());
		ref.send(null, new MessageEntity("startHarbor", port));
	}

	public void start(int threadNum) {
		dispatcher = new Dispatcher(this, threadNum);
		ActorDispatcher[] actorDispatcher = dispatcher.getActorDispatchers();
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(dispatcher);
		ExecutorService actorExecutors = Executors
				.newFixedThreadPool(threadNum);
		for (int i = 0; i < threadNum; i++) {
			actorExecutors.submit(actorDispatcher[i]);
		}
	}

	static class Dispatcher implements Runnable {
		private ActorSystem system;
		private int cursor = 0;
		private int threadNum;
		private int threadMask;
		private ActorDispatcher[] dispatchers;
		private LinkedBlockingQueue<Actor> actors = new LinkedBlockingQueue<>();
		private LinkedBlockingQueue<ActorMessage> msgQueue = new LinkedBlockingQueue<>();
		private Map<String, Actor> actorsMap = new ConcurrentHashMap<>();

		public Dispatcher(ActorSystem system, int threadNum) {
			this.system = system;
			this.threadNum = threadNum;
			this.threadMask = threadNum - 1;
			dispatchers = new ActorDispatcher[threadNum];
			for (int i = 0; i < threadNum; i++) {
				dispatchers[i] = new ActorDispatcher(
						new LinkedBlockingQueue<>());
			}
		}

		public void sendMsg(ActorMessage msg) {
			msgQueue.add(msg);
		}

		public void addActor(String name, Actor actor) {
			actorsMap.put(name, actor);
			actors.add(actor);
		}

		private boolean dispatchActors() {
			Actor actor = null;
			boolean hasActor = false;
			while ((actor = actors.poll()) != null) {
				dispatchers[cursor++ & threadMask].addActor(actor);
				if (!hasActor) {
					hasActor = true;
				}
			}
			return hasActor;
		}

		private boolean dispatchActorMsgs() {
			ActorMessage msg = null;
			boolean hasMsg = false;
			while ((msg = msgQueue.poll()) != null) {
				String name = msg.getReceiver();
				if (system.isLocalActor(name)) { // local message
					Actor actor = actorsMap.get(name);
					if (actor == null) {
						throw new IllegalStateException(name
								+ " actor not exist.");
					}
					actor.addMessage(msg);
				} else { // remote message
					Actor harbor = actorsMap.get(system.harborName);
					if (harbor == null) {
						throw new IllegalStateException("no harbor started.");
					}
					harbor.addMessage(ActorMessage.wrapHarborMessage(
							system.harborName, "sendRemote", msg));
				}
				if (!hasMsg) {
					hasMsg = true;
				}
			}
			return hasMsg;
		}

		public ActorDispatcher[] getActorDispatchers() {
			return dispatchers;
		}

		@Override
		public void run() {
			for (;;) {
				boolean hasactor = dispatchActors();
				boolean hasmsg = dispatchActorMsgs();
				if (!hasactor && !hasmsg) {
					LockSupport.parkNanos(1L);
				}
			}
		}
	}
}
