package com.cocoons.examples;

import com.cocoons.actor.Actor;
import com.cocoons.actor.ActorRef;
import com.cocoons.actor.ActorSystem;
import com.cocoons.actor.MessageEntity;

/**
 *
 * @author qinguofeng
 */
public class ActorTest {
	public static void main(String[] args) {
		ActorSystem system = new ActorSystem();
		ActorRef ping = system.actor("ping", new Actor() {
			public void test(String abc) {
				System.out.println("Ping:" + abc);
				getSender().send(getSelfName(),
						new MessageEntity("test", "World"));
			}
		});
		ActorRef pong = system.actor("pong", new Actor() {
			public void test(String abc) {
				System.out.println("Pong:" + abc);
				getSender().send(getSelfName(),
						new MessageEntity("test", "World"));
			}
		});

		ping.send(pong.getName(), new MessageEntity("test", "Hello"));

		system.start(4);
	}
}
