package com.funoMq.mq.service;

import javax.jms.Destination;

import org.springframework.stereotype.Service;
public interface ConsumerService {
	/**
	 * 消费消息
	 * @param queueDestination
	 */
	 public String receive(Destination queueDestination);
	 /**
	  * 直接通过destinationName的名字进行消费
	  * @param queueDestination
	  */
	 public String receive(String queueDestination);
}
