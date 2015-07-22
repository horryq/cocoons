/**
 * 参考 LMAX disruptor 的实现
 * 
 * @author qinguofeng
 */
package com.cocoons.util;

import sun.misc.Unsafe;

class LvPadding {
	protected long p1, p2, p3, p4, p5, p6, p7;
}

// 为了消除伪共享(False Sharing)对性能的影响
class Value extends LvPadding {
	protected volatile long value;
}

class RvPadding extends Value {
	protected long p9, p10, p11, p12, p13, p14, p15;
}

/**
 * <p>
 * Concurrent sequence class used for tracking the progress of the ring buffer
 * and event processors. Support a number of concurrent operations including CAS
 * and order writes.
 * 
 * <p>
 * Also attempts to be more efficient with regards to false sharing by adding
 * padding around the volatile field.
 */
public class Sequence extends RvPadding {
	static final long INITAL_VALUE = -1L;

	private static final Unsafe UNSAFE;
	private static final long VALUE_OFFSET;

	static {
		UNSAFE = UnsafeUtil.getUnsafe();
		try {
			VALUE_OFFSET = UNSAFE.objectFieldOffset(Value.class
					.getDeclaredField("value"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Sequence() {
		this(INITAL_VALUE);
	}

	public Sequence(final long initialValue) {
		UNSAFE.putOrderedLong(this, VALUE_OFFSET, initialValue);
	}

	public long get() {
		return value;
	}

	/**
	 * Perform an ordered write of this sequence. The intent is a Store/Store
	 * barrier between this write and any previous store.
	 *
	 * @param value
	 *            The new value for the sequence.
	 */
	public void set(final long value) {
		UNSAFE.putOrderedLong(this, VALUE_OFFSET, value);
	}

	/**
	 * Performs a volatile write of this sequence. The intent is a Store/Store
	 * barrier between this write and any previous write and a Store/Load
	 * barrier between this write and any subsequent volatile read.
	 *
	 * @param value
	 *            The new value for the sequence.
	 */
	public void setVolatile(final long value) {
		UNSAFE.putLongVolatile(this, VALUE_OFFSET, value);
	}

	/**
	 * Perform a compare and set operation on the sequence.
	 *
	 * @param expectedValue
	 *            The expected current value.
	 * @param newValue
	 *            The value to update to.
	 * @return true if the operation succeeds, false otherwise.
	 */
	public boolean compareAndSet(final long expectedValue, final long newValue) {
		return UNSAFE.compareAndSwapLong(this, VALUE_OFFSET, expectedValue,
				newValue);
	}

	public long incrementAndGet() {
		return addAndGet(1);
	}

	public long addAndGet(final long increment) {
		long currentValue;
		long newValue;

		do {
			currentValue = get();
			newValue = currentValue + increment;
		} while (!compareAndSet(currentValue, newValue));

		return newValue;
	}

	@Override
	public String toString() {
		return Long.toString(get());
	}

	/**
	 * Test Function.
	 * */
	public static void main(String[] args) {
		final Sequence seq = new Sequence(0L);
		final int COUNT = 100000;
		Runnable r = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < COUNT; i++) {
					seq.incrementAndGet();
				}
			}
		};

		Thread t[] = new Thread[10];
		for (int i = 0; i < 10; i++) {
			t[i] = new Thread(r);
			t[i].start();
		}

		for (int i = 0; i < 10; i++) {
			try {
				t[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println(seq.get());
	}
}
