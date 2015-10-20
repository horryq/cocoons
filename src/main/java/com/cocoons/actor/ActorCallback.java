package com.cocoons.actor;

/**
 * @author guofeng.qin
 */
@FunctionalInterface
public interface ActorCallback {
	void onResp(Object... params);
}
