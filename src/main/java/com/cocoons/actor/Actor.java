package com.cocoons.actor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author qinguofeng
 */
public abstract class Actor {
	private String name;
	private LinkedBlockingQueue<ActorMessage> msgList = new LinkedBlockingQueue<>();

	private ActorSystem system;

	private boolean finished = false;

	private ActorMessage lastMessage;

	public void setContext(String name, ActorSystem system) {
		this.name = name;
		this.system = system;
	}

	public ActorSystem getSystem() {
		return system;
	}

	public ActorRef getSender() {
		if (lastMessage != null) {
			return system.getActorRefOf(lastMessage.getSender());
		}
		return null;
	}

	public String getSelfName() {
		return name;
	}

	public ActorRef getSelf() {
		return system.getActorRefOf(name);
	}

	public final boolean dispatch() {
		boolean hasMessage = false;
		for (;;) {
			ActorMessage msg = null;
			if ((msg = msgList.poll()) != null) {
				hasMessage = true;
				lastMessage = msg;
				MessageEntity entity = msg.getMsg();
				String funcName = entity.getFuncName();
				Object[] params = entity.getParams();
				List<Class<?>> clazzList = new ArrayList<Class<?>>();
				if (params != null) {
					for (Object obj : params) {
						clazzList.add(obj.getClass());
					}
				}
				Class<?>[] clazz = clazzList.size() <= 0 ? null : clazzList
						.toArray(new Class<?>[0]);
				try {
					Method method = getClass().getDeclaredMethod(funcName,
							clazz);
					method.setAccessible(true);
					method.invoke(this, params);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			} else {
				break;
			}
		}
		return hasMessage;
	}

	public final void addMessage(ActorMessage msg) {
		msgList.add(msg);
	}
}