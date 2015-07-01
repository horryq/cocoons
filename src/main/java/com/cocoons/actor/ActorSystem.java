package com.cocoons.actor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author qinguofeng
 */
public class ActorSystem {
	private Map<String, ActorRef> actorsRefMap = new ConcurrentHashMap<>();
	private Map<String, Actor> actorsMap = new ConcurrentHashMap<>();
	private LinkedBlockingQueue<Actor> actors = new LinkedBlockingQueue<>();

	private void doWork() {
		for (;;) {
			try {
				// System.out.println("dispatch in "
				// + Thread.currentThread().getId());
				Actor actor = actors.take();
				try {
					if (actor != null) {
						actor.dispatch();
					}
				} finally {
					actors.add(actor);
				}
				// TODO ... 当所有actor的邮箱都为空的时候，这里会空转，待判断是否会造成CPU负载空高.
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public ActorRef actor(String name, Actor actor) {
		actor.setContext(name, this);
		actorsMap.put(name, actor);
		actors.add(actor);
		ActorRef ref = new ActorRef(name, this);
		actorsRefMap.put(name, ref);
		return ref;
	}

	public ActorRef getActorRefOf(String name) {
		return actorsRefMap.get(name);
	}

	public void sendMsgTo(String name, ActorMessage msg) {
		Actor actor = actorsMap.get(name);
		if (actor == null) {
			throw new IllegalStateException(name + " actor not exist.");
		}
		actor.addMessage(msg);
	}

	public String getSid() {
		return UUID.randomUUID().toString();
	}

	public void start(int threadNum) {
		ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		for (int i = 0; i < threadNum; i++) {
			executor.submit(this::doWork);
		}
	}
}
