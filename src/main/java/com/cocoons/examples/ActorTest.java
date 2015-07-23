package com.cocoons.examples;

import com.cocoons.actor.Actor;
import com.cocoons.actor.ActorRef;
import com.cocoons.actor.ActorSystem;
import com.cocoons.actor.MessageEntity;

/**
 * @author qinguofeng
 */
public class ActorTest {
	public static void main(String[] args) {
		ActorSystem system = new ActorSystem("TestSystem");
		ActorRef ping = system.actor("ping", new Actor() {
			public void ping(String abc, String def) {
				System.out.println(abc + def);
				getSender().send(getSelfName(),
						new MessageEntity("pong", "Pong", "..."));
			}
		});
		ActorRef ping2 = system.actor("ping2", new Actor() {
			public void ping(String abc, String def) {
				System.out.println(abc + def);
				getSender().send(getSelfName(),
						new MessageEntity("pong", "Pong2", "..."));
			}
		});
		ActorRef pong = system.actor("pong", new Actor() {
			public void pong(String abc, String def) {
				System.out.println(abc + def);
				getSender().send(getSelfName(),
						new MessageEntity("ping", "Ping", "..."));
			}
		});

		ping.send(pong.getName(), new MessageEntity("ping", "Ping", "..."));
		ping2.send(pong.getName(), new MessageEntity("ping", "Ping2", "..."));

		system.start(4);
	}
}
