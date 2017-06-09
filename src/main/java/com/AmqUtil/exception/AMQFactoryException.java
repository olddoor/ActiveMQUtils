package com.AmqUtil.exception;

/**
 * AMQ工厂自定义异常类
 * @author duxianchao
 * @version 1.0
 * @updated 08-七月-2016 16:00:20
 */
public class AMQFactoryException extends Exception{
	private static final long serialVersionUID = 1L;

	public AMQFactoryException(String message){
		super(message);
	}
	
	public AMQFactoryException(String message,Exception e){
		super(message,e);
	}
	
}

