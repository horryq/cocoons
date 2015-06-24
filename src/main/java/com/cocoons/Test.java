package com.cocoons;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test
 * 
 * @author qinguofeng
 */
public class Test {
	public static void main(String[] args) {
		ApplicationContext appCtx = new ClassPathXmlApplicationContext(
				new String[] { "application.xml" });
	}
}
