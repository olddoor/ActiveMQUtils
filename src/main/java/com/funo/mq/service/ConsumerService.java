package com.funo.mq.service;

import javax.jms.Destination;

import org.springframework.stereotype.Service;
public interface ConsumerService {
	/**
	 * 消费消息
	 * @param queueDestination
	 */
	 public void receive(Destination queueDestination);
}
