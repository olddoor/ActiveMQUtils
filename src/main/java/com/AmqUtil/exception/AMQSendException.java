package com.AmqUtil.exception;

/**
 * AMQ消息发送自定义异常类
 * @author duxianchao
 * @version 1.0
 * @updated 08-七月-2016 16:00:22
 */
public class AMQSendException extends Exception{
	private static final long serialVersionUID = 1L;
	public AMQSendException(String message){
		super(message);
	}
	public AMQSendException(String message,Exception e){
		super(message,e);
	}
}
