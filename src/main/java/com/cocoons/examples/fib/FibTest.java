package com.cocoons.examples.fib;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.cocoons.actor.ActorRef;
import com.cocoons.actor.ActorSystem;
import com.cocoons.actor.MessageEntity;

public class FibTest {
	static final Logger logger = Logger.getLogger(FibService.class);

	static final int COUNT = 1000;

	public static void main(String[] args) {
		ActorSystem system = new ActorSystem("fibsystem");
		final int actorcount = 4;
		ActorRef[] refs = new ActorRef[actorcount];
		for (int i = 0; i < actorcount; i++) {
			ActorRef ref = system.actor("actor-" + i, new FibService());
			refs[i] = ref;
		}

		system.start(4);
		logger.warn("start:" + System.currentTimeMillis());
		for (int i = 0; i < COUNT; i++) {
			refs[i & (actorcount-1)].send(null, new MessageEntity("fib", 36));
		}
	}
}
