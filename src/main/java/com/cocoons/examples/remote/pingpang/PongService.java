package com.cocoons.examples.remote.pingpang;

import com.cocoons.actor.Actor;
import com.cocoons.actor.MessageEntity;

/**
 * @author qinguofeng
 */
public class PongService extends Actor {
	public void pong(String abc, String def) {
		System.out.println(abc + def);
		getSender().send(getSelfName(),
				new MessageEntity("ping", "Ping", "..."));
	}
}
