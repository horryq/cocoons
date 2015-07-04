package com.cocoons.actor;

import com.cocoons.actor.ActorMessage.TYPE;

/**
 *
 * @author qinguofeng
 */
public class ActorRef {

	private String name;
	private ActorSystem system;

	public ActorRef(String name, ActorSystem system) {
		this.name = name;
		this.system = system;
	}

	public String getName() {
		return name;
	}

	public void send(String sender, MessageEntity msg) {
		ActorMessage actorMsg = new ActorMessage(TYPE.TREQ, system.getSid(),
				sender, name, msg);
		system.sendMsgTo(actorMsg);
	}

	public void response() {
		// TODO ...
	}
}
