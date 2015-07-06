package com.cocoons.examples.remote.pingpang;

import com.cocoons.actor.ActorSystem;
import com.cocoons.common.Constants;

/**
 * @author qinguofeng
 */
public class Pong {
	public static void main(String[] args) {
		initURLMap();
		ActorSystem system = new ActorSystem("PongSystem", 4);
		system.startHarborService(9999);
		system.actor("Pong", new PongService());
		system.start(4);
	}

	private static void initURLMap() {
		Constants.URL_MAP.put("PingSystem:Ping", "127.0.0.1:8999");
		Constants.URL_MAP.put("PongSystem:Pong", "127.0.0.1:9999");
	}
}
