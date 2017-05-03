package com.funo.mq.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候取出ApplicaitonContext.
 */
public class SpringContextUtil {
	public static ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:conf/funo-*.xml");

	public static Object getBean(String serviceName) {
		return context.getBean(serviceName);
	}
	
	public void say(){
		System.out.println(context.toString());
	}
}