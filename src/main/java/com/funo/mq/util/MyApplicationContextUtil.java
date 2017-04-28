package com.funo.mq.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MyApplicationContextUtil implements ApplicationContextAware {

	private static ApplicationContext context;

	public void setApplicationContext(ApplicationContext contex) throws BeansException {
		MyApplicationContextUtil.context = contex;
	}

	public static ApplicationContext getContext() {
		return context;
	}
}