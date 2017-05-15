package com.funoMq.mq.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候取出ApplicaitonContext.
 */
public class SpringContextUtil {
	//使用jar/conf下的配置文件
//	public static ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:conf/funo-*.xml");
	//使用外部配置文件的
	public static ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:funo-*.xml");//("classpath*:funo-*.xml");

	public static Object getBean(String serviceName) {
		return context.getBean(serviceName);
	}
	
	public void say(){
		System.out.println(context.toString());
	}
	
	public static void main(String args[]){
		SpringContextUtil m=new SpringContextUtil();
		m.say();
	}
}