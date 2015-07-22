package com.cocoons.examples;

public final class FalseSharingTest implements Runnable {
	public final static int NUM_THREADS = 4; // change
	public final static long ITERATIONS = 500L * 1000L * 1000L;
	private final int arrayIndex;

	private static VolatileLong[] longs = new VolatileLong[NUM_THREADS];

	static {
		for (int i = 0; i < longs.length; i++) {
			longs[i] = new VolatileLong();
		}
	}

	public FalseSharingTest(final int arrayIndex) {
		this.arrayIndex = arrayIndex;
	}

	public static void main(final String[] args) throws Exception {
		final long start = System.nanoTime();
		runTest();
		System.out.println("duration = " + (System.nanoTime() - start));
	}

	private static void runTest() throws InterruptedException {
		Thread[] threads = new Thread[NUM_THREADS];

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new FalseSharingTest(i));
		}

		for (Thread t : threads) {
			t.start();
		}

		for (Thread t : threads) {
			t.join();
		}
	}

	public void run() {
		long i = ITERATIONS + 1;
		while (0 != --i) {
			longs[arrayIndex].value = i;
		}
	}

	public final static class VolatileLong {
		public long p11, p22, p33, p44, p55, p66, p77; // comment out
		public volatile long value = 0L;
		public long p1, p2, p3, p4, p5, p6, p7; // comment out
		// public final long p1 = 0;
		// public final long p2 = 0;
		// public final long p3 = 0;
		// public final long p4 = 0;
		// public final long p5 = 0;
		// public final long p6 = 0;
		// public final long p7 = 0; // comment out
	}
}