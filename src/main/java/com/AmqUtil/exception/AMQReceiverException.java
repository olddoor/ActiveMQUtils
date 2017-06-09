package com.AmqUtil.exception;

/**
 * AMQ消息接受自定义异常类
 * @author duxianchao
 * @version 1.0
 * @updated 08-七月-2016 16:00:21
 */
public class AMQReceiverException extends Exception{
	private static final long serialVersionUID = 1L;
	public AMQReceiverException(String message){
		super(message);
	}
	public AMQReceiverException(String message,Exception e){
		super(message,e);
	}
}

