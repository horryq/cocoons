package com.cocoons.actor;

import java.util.concurrent.LinkedBlockingQueue;

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
		for (;;) {
			try {
				Actor actor = actors.take();
				try {
					if (actor != null) {
						actor.dispatch();
					}
				} finally {
					actors.add(actor);
				}
				// TODO ... 当所有actor的邮箱都为空的时候，这里会空转，待判断是否会造成CPU负载空高.
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
