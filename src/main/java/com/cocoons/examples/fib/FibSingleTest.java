package com.cocoons.examples.fib;

import org.apache.log4j.Logger;

import com.esotericsoftware.minlog.Log;

public class FibSingleTest {
	static final Logger logger = Logger.getLogger(FibSingleTest.class);

	static final int COUNT = 100000;

	public static int dofib(int n) {
		if (n == 0) {
			return 0;
		} else if (n == 1) {
			return 1;
		}
		return dofib(n - 1) + dofib(n - 2);
	}

	public static void fib(int n) {
		int res = dofib(n);
		// logger.debug(System.currentTimeMillis() + ":" + res);
	}

	public static void cacl(int num) {
		for (int i = 0; i < num; i++) {
			fib(10);
		}
	}

	public static void main(String[] args) {
		int threadCount = 1;
		long start = System.currentTimeMillis();
		Thread t[] = new Thread[threadCount];
		for (int i = 0; i < threadCount; i++) {
			t[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					cacl(COUNT / threadCount);
				}
			});
			t[i].start();
		}
		for (Thread it : t) {
			try {
				it.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		Log.warn("total:" + (end - start));
	}
}
