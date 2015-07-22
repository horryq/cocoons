package com.cocoons.util;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import sun.misc.Unsafe;

/**
 * @author qinguofeng
 */
public class UnsafeUtil {
	private static final Unsafe UNSAFE;

	static {
		try {
			final PrivilegedExceptionAction<Unsafe> action = new PrivilegedExceptionAction<Unsafe>() {

				@Override
				public Unsafe run() throws Exception {
					Field unsafe = Unsafe.class.getDeclaredField("theUnsafe");
					unsafe.setAccessible(true);
					return (Unsafe) unsafe.get(null);
				}
			};
			UNSAFE = AccessController.doPrivileged(action);
		} catch (Exception e) {
			throw new RuntimeException("unable to load unsafe", e);
		}
	}

	public static Unsafe getUnsafe() {
		return UNSAFE;
	}
}
