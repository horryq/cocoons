package com.cocoons.examples.fib;

import org.apache.log4j.Logger;

import com.cocoons.actor.Actor;
import com.cocoons.actor.ActorRef;
import com.cocoons.actor.ActorSystem;
import com.cocoons.actor.MessageEntity;

public class FibTest {
	static final Logger logger = Logger.getLogger(FibService.class);

	static final int COUNT = 1000000;

	public static void main(String[] args) {
		ActorSystem system = new ActorSystem("fibsystem");
		system.start(4);
		final int actorcount = 4;
		ActorRef[] refs = new ActorRef[actorcount];
		for (int i = 0; i < actorcount; i++) {
			ActorRef ref = system.actor("actor-" + i, new FibService());
			refs[i] = ref;
		}

		ActorRef mainRef = system.actor("atctor-m", new Actor() {
			int count = 0;
			long start = System.currentTimeMillis();

			public void count() {
				count++;
				if (count >= COUNT) {
					long end = System.currentTimeMillis();
					logger.warn("finished: " + (end - start));
				}
			}

			public void startCount() {
				for (int i = 0; i < COUNT; i++) {
					refs[i & (actorcount - 1)].send(getSelfName(),
							new MessageEntity("fib", 10));
				}
//				System.out.println("start end....");
			}
		});

		logger.warn("start:" + System.currentTimeMillis());

		mainRef.send(null, new MessageEntity("startCount"));
	}
}
