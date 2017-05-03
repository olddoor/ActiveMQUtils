package com.funo.mq.service.impl;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.funo.mq.service.ConsumerService;
@Service("consumerServiceImpl")
public class ConsumerServiceImpl implements ConsumerService {
	private static Logger logger = LoggerFactory.getLogger(ConsumerServiceImpl.class); 
	
	@Autowired
	@Qualifier("queueJmsTemplate")
	private JmsTemplate queueJmsTemplate;
//	@Autowired
//	private MessageDelegateBean bean;
	
	public void getMessage(){
		
	}
	
	@Override
	public String receive(Destination queueDestination) {
		TextMessage tm = (TextMessage) queueJmsTemplate.receive(queueDestination);
		String result=null;
		try {
		if(tm!=null){
			logger.debug("ConsumerService从队列" + queueDestination.toString() + "收到了消息：\t" + tm.getText());
			result=tm.getText();
		}
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public String receive(String destinationName) {
		TextMessage tm = (TextMessage)queueJmsTemplate.receive(destinationName);
		String result=null;
		try {
		if(tm!=null){
			logger.debug("ConsumerService从队列" + destinationName+ "收到了消息：\t" + tm.getText());
			result=tm.getText();
		}
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return result;
	}

	public JmsTemplate getQueueJmsTemplate() {
		return queueJmsTemplate;
	}

	public void setQueueJmsTemplate(JmsTemplate queueJmsTemplate) {
		this.queueJmsTemplate = queueJmsTemplate;
	}

}
