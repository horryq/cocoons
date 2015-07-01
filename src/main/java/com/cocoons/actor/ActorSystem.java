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
	private Map<String, Actor> actorsMap = new ConcurrentHashMap<>();
	private LinkedBlockingQueue<Actor> actors = new LinkedBlockingQueue<>();

	private void doWork() {
		for (;;) {
			try {
				System.out.println("dispatch in "
						+ Thread.currentThread().getId());
				Actor actor = actors.take();
				try {
					if (actor != null) {
						actor.dispatch();
					}
				} finally {
					actors.add(actor);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public ActorRef actor(String name, Actor actor) {
		actorsMap.put(name, actor);
		actors.add(actor);
		return new ActorRef(name, this);
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
