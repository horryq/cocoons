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
//				getSender().send(getSelfName(),
//						new MessageEntity("pong", "Pong", "..."));
				getSender().callWithCb(this, new MessageEntity("pong", "Pong", "..."), (params)->{
					System.out.println("in cb...");
					System.out.println(params.length + ":" + params[0] + ":" + params[1]);
				}); 
			}
		});
		ActorRef ping2 = system.actor("ping2", new Actor() {
			public void ping(String abc, String def) {
				System.out.println(abc + def);
				getSender().send(getSelfName(),
						new MessageEntity("pong2", "Pong2", "..."));
			}
		});
		ActorRef pong = system.actor("pong", new Actor() {
			public void pong(String abc, String def) {
				System.out.println(abc + def);
//				getSender().send(getSelfName(),
//						new MessageEntity("ping", "Ping", "..."));
				getSender().responseCb(getResponseSid(), getSelfName(),
						new MessageEntity("ping", "Ping", "..."));
			}
			
			public void pong2(String abc, String def) {
				System.out.println(abc + def);
				getSender().send(getSelfName(),
						new MessageEntity("ping", "Ping", "..."));
			}
		});

		ping.send(pong.getName(), new MessageEntity("ping", "Ping", "..."));
		pong.send(ping2.getName(), new MessageEntity("pong2", "Pong2", "..."));

		system.start(4);
	}
}
