package com.cocoons.examples;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.cocoons.actor.Actor;
import com.cocoons.actor.ActorRef;
import com.cocoons.actor.ActorSystem;
import com.cocoons.actor.MessageEntity;

/**
 * @author qinguofeng
 */
public class ActorSyncTest {
	public static void main(String[] args) {
		ActorSystem system = new ActorSystem("TestSystem");
		ActorRef ping = system.actor("ping", new Actor() {
			public void ping(String abc, String def) {
				System.out.println("start...");
				for (;;) {
					Future<String> future = getSender().call(getSelfName(),
							new MessageEntity("pong", "Pong", "..."));
					try {
						String str = future.get();
						System.out.println("sync : " + str);
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				}
			}
		});
		ActorRef pong = system.actor("pong", new Actor() {
			public void pong(String abc, String def) {
				System.out.println(abc + ":" + def);
				getSender().response(getResponseSid(), getSelfName(), "ping");
			}
		});

		ping.send(pong.getName(), new MessageEntity("ping", "Ping", "..."));

		system.start(4);
	}
}
