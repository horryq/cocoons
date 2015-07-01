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

	private boolean finished = false;

	public final void dispatch() {
		for (;;) {
			ActorMessage msg = null;
			if ((msg = msgList.poll()) != null) {
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
	}

	public final void addMessage(ActorMessage msg) {
		msgList.add(msg);
	}
}