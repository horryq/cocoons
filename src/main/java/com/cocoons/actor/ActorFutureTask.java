package com.cocoons.actor;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author qinguofeng
 */
public class ActorFutureTask<V> extends FutureTask<V> {

	private ActorFutureCallable<V> _cb;

	public ActorFutureTask(ActorFutureCallable<V> callable) {
		super(callable);
		_cb = callable;
	}

	public static <V> ActorFutureTask<V> future() {
		return new ActorFutureTask<>(new ActorFutureCallable<>());
	}

	public void finish(V v) {
		_cb.setResult(v);

		run();
	}

	static class ActorFutureCallable<V> implements Callable<V> {
		private V v;

		@Override
		public V call() throws Exception {
			return v;
		}

		public void setResult(V v) {
			this.v = v;
		}

	}
}
