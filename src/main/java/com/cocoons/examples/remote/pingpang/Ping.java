package com.cocoons.examples.remote.pingpang;

import com.cocoons.actor.ActorRef;
import com.cocoons.actor.ActorSystem;
import com.cocoons.actor.MessageEntity;
import com.cocoons.common.Constants;

/**
 * @author qinguofeng
 */
public class Ping {
	public static void main(String[] args) {
		initURLMap();
		ActorSystem system = new ActorSystem("PingSystem", 4);
		system.startHarborService(8999);
		ActorRef pingRef = system.actor("Ping", new PingService());
		ActorRef pongRef = system.remoteActorOf("PongSystem:Pong");
		pongRef.send(pingRef.getName(),
				new MessageEntity("pong", "Pong", "..."));
		system.start(4);
	}

	private static void initURLMap() {
		Constants.URL_MAP.put("PingSystem:Ping", "127.0.0.1:8999");
		Constants.URL_MAP.put("PongSystem:Pong", "127.0.0.1:9999");
	}
}
