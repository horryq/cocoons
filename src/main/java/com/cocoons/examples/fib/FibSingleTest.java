package com.cocoons.examples.fib;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.esotericsoftware.minlog.Log;

public class FibSingleTest {
	static final Logger logger = Logger.getLogger(FibSingleTest.class);
	
	static final int COUNT = 1000000;

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
//		logger.debug(System.currentTimeMillis() + ":" + res);
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		LinkedBlockingQueue<String> str = new LinkedBlockingQueue<>();
		for (int i = 0; i < COUNT; i++) {
			str.add(i + ":str");
			fib(10);
			str.poll();
		}
		long end = System.currentTimeMillis();
		Log.warn("total:" + (end - start));
	}
}
