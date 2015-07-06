package com.cocoons.actor;

import java.text.MessageFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;

import com.cocoons.harbor.HarborServer;

/**
 *
 * @author qinguofeng
 */
public class ActorSystem {
	private Map<String, ActorRef> actorsRefMap = new ConcurrentHashMap<>();
	private Map<String, Actor> actorsMap = new ConcurrentHashMap<>();
	private ConcurrentLinkedQueue<Actor> actors = new ConcurrentLinkedQueue<>();

	private String systemName;
	private String harborName;

	private java.util.concurrent.ForkJoinPool pool;

	public ActorSystem(String name, int threadNum) {
		this.systemName = name;
		pool = new ForkJoinPool(threadNum);
	}

	// private void doWork() {
	// for (;;) {
	// try {
	// // System.out.println("dispatch in "
	// // + Thread.currentThread().getId());
	// Actor actor = actors.take();
	// try {
	// if (actor != null) {
	// actor.dispatch();
	// }
	// } finally {
	// actors.add(actor);
	// }
	// // TODO ... 当所有actor的邮箱都为空的时候，这里会空转，待判断是否会造成CPU负载空高.
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	private String wrapActorName(String actorName) {
		return MessageFormat.format("{0}:{1}", systemName, actorName);
	}

	public ActorRef actor(String name, Actor actor) {
		name = wrapActorName(name);

		actor.setContext(name, this);
		actorsMap.put(name, actor);
		actors.add(actor);
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

	public void sendMsgTo(String name, ActorMessage msg) {
		if (isLocalActor(name)) { // local message
			Actor actor = actorsMap.get(name);
			if (actor == null) {
				throw new IllegalStateException(name + " actor not exist.");
			}
			actor.addMessage(msg);
			if (!actor.running()) {
				pool.execute(actor);
			}
		} else { // remote message
			Actor harbor = actorsMap.get(harborName);
			if (harbor == null) {
				throw new IllegalStateException("no harbor started.");
			}
			harbor.addMessage(ActorMessage.wrapHarborMessage(harborName,
					"sendRemote", msg));
			if (!harbor.running()) {
				pool.execute(harbor);
			}
		}
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
		// ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		// for (int i = 0; i < threadNum; i++) {
		// executor.submit(this::doWork);
		// }
	}
}
