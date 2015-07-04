package com.cocoons.actor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * @author qinguofeng
 */
public class ActorDispatcher implements Runnable {
	private LinkedBlockingQueue<Actor> actors = null;

	public ActorDispatcher(LinkedBlockingQueue<Actor> actors) {
		this.actors = actors;
	}

	public void addActor(Actor actor) {
		actors.add(actor);
	}

	@Override
	public void run() {
		int index = 0;
		boolean hasmsg = false;
		for (;;) {
			try {
				Actor actor = actors.poll();
				if (actor != null) {
					try {
						hasmsg = actor.dispatch() || hasmsg;
					} finally {
						actors.add(actor);
					}
				}
				if (((index++ & 7) == 0) && !hasmsg) {
					hasmsg = false;
					LockSupport.parkNanos(1L);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
