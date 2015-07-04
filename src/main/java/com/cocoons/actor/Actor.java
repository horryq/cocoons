package com.cocoons.actor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

/**
 * @author qinguofeng
 */
public abstract class Actor {
	private static final Logger logger = Logger.getLogger(Actor.class);

	private String name;
	private LinkedBlockingQueue<ActorMessage> msgList = new LinkedBlockingQueue<>();
	private Map<String, Method> methodMap = new HashMap<>();

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
		int count = msgList.size();
		for (int i = 0; i < count; i++) {
			ActorMessage msg = null;
			if ((msg = msgList.poll()) != null) {
				hasMessage = true;
				lastMessage = msg;
				MessageEntity entity = msg.getMsg();
				String funcName = entity.getFuncName();
				Object[] params = entity.getParams();
				Method method = methodMap.get(funcName);
				try {
					if (method == null) {
						List<Class<?>> clazzList = new ArrayList<Class<?>>();
						if (params != null) {
							for (Object obj : params) {
								clazzList.add(obj.getClass());
							}
						}
						Class<?>[] clazz = clazzList.size() <= 0 ? null
								: clazzList.toArray(new Class<?>[0]);
						// TODO ... optimize...
						try {
							method = getClass().getDeclaredMethod(funcName,
									clazz);
						} catch (NoSuchMethodException e) {
							// logger.warn("no method match accurate with name "
							// + funcName + " in " + getClass().getName());
						}
						if (method == null) {
							Method[] ms = getClass().getMethods();
							if (ms != null) {
								for (Method m : ms) {
									if (m.getName().equals(funcName)) {
										method = m;
										break;
									}
								}
							}
						}
						if (method == null) {
							throw new IllegalStateException(
									"No Function named " + funcName
											+ " in class "
											+ this.getClass().getName());
						} else {
							methodMap.put(funcName, method);
						}
					}
					method.setAccessible(true);
					method.invoke(this, params);
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

	public final void giveBackMessage() {
		msgList.add(lastMessage);
	}
}