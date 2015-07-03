package com.cocoons.actor;

/**
 *
 * @author qinguofeng
 */
public class ActorMessage {
	public static final class TYPE {
		public static final int TREQ = 0; // 请求
		public static final int TRESP = 1; // 响应
	}

	private int type = TYPE.TREQ;
	private String sid;

	private String sender;
	private String receiver;

	private MessageEntity msg;

	public ActorMessage() {
	}

	public ActorMessage(int type, String sid, String sender, String receiver,
			MessageEntity msg) {
		this.type = type;
		this.sid = sid;
		this.sender = sender;
		this.receiver = receiver;
		this.msg = msg;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the sid
	 */
	public String getSid() {
		return sid;
	}

	/**
	 * @param sid
	 *            the sid to set
	 */
	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	/**
	 * @return the msg
	 */
	public MessageEntity getMsg() {
		return msg;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(MessageEntity msg) {
		this.msg = msg;
	}

	public static ActorMessage wrapHarborMessage(String harborName,
			String funcName, ActorMessage msg) {
		return new ActorMessage(TYPE.TREQ, msg.getSid(), msg.getSender(),
				harborName, new MessageEntity(funcName, msg));
	}
}
