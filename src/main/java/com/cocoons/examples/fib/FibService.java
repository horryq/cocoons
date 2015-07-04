package com.cocoons.examples.fib;

import org.apache.log4j.Logger;

import com.cocoons.actor.Actor;
import com.cocoons.actor.MessageEntity;

public class FibService extends Actor {
	static final Logger logger = Logger.getLogger(FibService.class);

	public int dofib(int n) {
		if (n == 0) {
			return 0;
		} else if (n == 1) {
			return 1;
		}
		return dofib(n - 1) + dofib(n - 2);
	}

	public void fib(int n) {
		int res = dofib(n);
		getSender().send(getSelfName(), new MessageEntity("count"));
//		logger.debug(System.currentTimeMillis() + ":" + res);
	}
}
