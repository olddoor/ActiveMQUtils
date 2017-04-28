package com.funo.mq.service.impl;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.funo.mq.service.ConsumerService;
@Service
public class ConsumerServiceImpl implements ConsumerService {
	@Autowired
	@Qualifier("queueJmsTemplate")
	private JmsTemplate queueJmsTemplate;

	@Override
	public void receive(Destination queueDestination) {
		TextMessage tm = (TextMessage) queueJmsTemplate.receive(queueDestination);
		try {
			System.out.println("ConsumerService从队列" + queueDestination.toString() + "收到了消息：\t" + tm.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public JmsTemplate getQueueJmsTemplate() {
		return queueJmsTemplate;
	}

	public void setQueueJmsTemplate(JmsTemplate queueJmsTemplate) {
		this.queueJmsTemplate = queueJmsTemplate;
	}

}
