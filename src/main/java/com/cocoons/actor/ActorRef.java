package com.cocoons.actor;

import java.util.concurrent.Future;

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
		ActorMessage actorMsg = new ActorMessage(TYPE.TREQ, "", sender, name,
				msg);
		system.sendMsgTo(name, actorMsg);
	}

	public <V> Future<V> call(String sender, MessageEntity msg) {
		ActorMessage actorMsg = new ActorMessage(TYPE.TREQ, "", sender, name,
				msg);
		return system.sendMsgToSync(name, actorMsg);
	}

	public void response(String sid, String sender, Object msg) {
		MessageEntity entity = new MessageEntity("pong", msg);
		ActorMessage actorMsg = new ActorMessage(TYPE.TRESP, sid, sender, name,
				entity);
		system.sendMsgTo(name, actorMsg);
	}
}
