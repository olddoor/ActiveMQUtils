package com.funo.mq.listener;

import java.io.Serializable;
import java.util.Map;
/**
 * queue消息监听,消费v3.0
 * 支持消息转换
 * 单纯的一个pojo类,作为参数构造实现到MessageListenerAdapter作为消息监听器
 */
public class MessageDelegateBean {
	int i=0;
	public void handleMessage(String message){
//		i++;
//		System.out.println("在线程ID是"+Thread.currentThread());  
		System.out.println("String:--------->"+i);
	}
	  
	public void handleMessage(Map message){
		System.out.println("Map");
    }
  
	public void handleMessage(byte[] message){
		System.out.println("byte");
    }
  
	public void handleMessage(Serializable message){
		System.out.println("Serializable");
    }
	
	public String toString(){
		return null;
	}
}
