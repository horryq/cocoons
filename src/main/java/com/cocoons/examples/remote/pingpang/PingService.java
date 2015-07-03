package com.cocoons.examples.remote.pingpang;

import com.cocoons.actor.Actor;
import com.cocoons.actor.MessageEntity;

/**
 * @author qinguofeng
 */
public class PingService extends Actor {
	public void ping(String abc, String def) {
		System.out.println(abc + def);
		getSender().send(getSelfName(),
				new MessageEntity("pong", "Pong", "..."));
	}
}
