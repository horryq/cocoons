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

	public void callWithCb(Actor sender, MessageEntity msg, ActorCallback cb) {
		String sid = system.getSid();
		ActorMessage actorMsg = new ActorMessage(TYPE.TCALL, sid,
				sender.getSelfName(), name, msg);
		sender.addCallback(sid, cb);
		system.sendMsgTo(name, actorMsg);
	}

	public <V> Future<V> call(String sender, MessageEntity msg) {
		ActorMessage actorMsg = new ActorMessage(TYPE.TREQ, "", sender, name,
				msg);
		return system.sendMsgToSync(name, actorMsg);
	}

	public void response(String sid, String sender, MessageEntity msg) {
		ActorMessage actorMsg = new ActorMessage(TYPE.TRESP, sid, sender, name,
				msg);
		system.sendMsgTo(name, actorMsg);
	}

	/**
	 * 回应带有Callback的消息
	 * */
	public void responseCb(String sid, String sender, MessageEntity msg) {
		ActorMessage actorMsg = new ActorMessage(TYPE.TCALLRESP, sid, sender,
				name, msg);
		system.sendMsgTo(name, actorMsg);
	}
}
