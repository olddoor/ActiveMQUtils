package com.funo.mq.service.impl;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.funo.mq.service.ProducerService;
@Service
public class ProducerServiceImpl implements ProducerService {
	@Autowired
	@Qualifier("queueJmsTemplate")
	private JmsTemplate queueJmsTemplate;

	/**
	 * 向指定队列发送消息
	 */
	@Override
	public void sendMessage(Destination destination, final String msg) {
		System.out.println("ProducerService向队列" + destination.toString() + "发送了消息：\t" + msg);
		queueJmsTemplate.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(msg);
			}
		});
	}

	/**
	 * 向默认队列发送消息
	 */
	@Override
	public void sendMessage(final String msg) {
		String destination = queueJmsTemplate.getDefaultDestination().toString();
		System.out.println("ProducerService向队列" + destination + "发送了消息：\t" + msg);
		queueJmsTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(msg);
			}
		});

	}
	
	/**
	 * 使用消息回执的功能
	 */
	@Override
	public void sendMessage(Destination destination, final String msg, final Destination response) {
		System.out.println("ProducerService向队列" + destination + "发送了消息：\t" + msg);
		queueJmsTemplate.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(msg);
				textMessage.setJMSReplyTo(response);
				return textMessage;
			}
		});
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.queueJmsTemplate = jmsTemplate;
	}
}
