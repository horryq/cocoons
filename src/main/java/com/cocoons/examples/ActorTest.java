package com.cocoons.examples;

import com.cocoons.actor.Actor;
import com.cocoons.actor.ActorRef;
import com.cocoons.actor.ActorSystem;

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
			}
		});
		ActorRef pong = system.actor("pong", new Actor() {

		});
	}
}
