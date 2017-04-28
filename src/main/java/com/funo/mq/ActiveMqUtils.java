package com.funo.mq;

import javax.annotation.PostConstruct;
import javax.jms.Destination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.funo.mq.service.ConsumerService;
import com.funo.mq.service.ProducerService;
/**
 * activeMQ工具类
 * @author zhanyl
 */
@Component
public class ActiveMqUtils  {
	private static Logger logger = LoggerFactory.getLogger(ActiveMqUtils.class); 
	
	@Autowired  
	private ConsumerService consumerService;
	@Autowired 
	private ProducerService producerService;
	
	public static ProducerService producer; 
	
	public static ConsumerService consumer;
	
//	static{
//		logger.debug("ActiveMqUtils 初始化init");
//		System.out.println("init----------");
//	}
	@PostConstruct
	public void init() {
		logger.debug("[ActiveMqUtils] Spring.PostConstruct初始化init----------");
		System.out.println("[ActiveMqUtils] Spring.PostConstruct初始化init----------");
		producer=producerService;
		consumer=consumerService;
	}
	  
	/**
	 * 发送queue队列消息
	 * @param destination 目标queue-name
	 * @param msg 消息
	 */
	public static void setQueue(Destination destination, String msg){
//		logger.debug("[ActiveMqUtils] 开始发送Queue");
		System.out.println("[ActiveMqUtils] 开始发送Queue");
		producer.sendMessage(destination, msg);
	}
	
	/**
	 * 接收queue队列消息[仅供测试, 建议使用额外的监听器类接收消息]
	 * @param destination 目标queue-name
	 * @param msg 消息
	 */
	public static void getQueue(Destination destination){
		consumer.receive(destination);
	}
	
	
	public ProducerService getProducerService() {
		return producerService;
	}


	public void setProducerService(ProducerService producerService) {
		this.producerService = producerService;
	}


	public ConsumerService getConsumerService() {
		return consumerService;
	}
	public void setConsumerService(ConsumerService consumerService) {
		this.consumerService = consumerService;
	}

//	/**
//	 * 向指定的topic发布消息
//	 * @param topic
//	 * @param msg
//	 */
//	public void sendTopic(final Destination topic, final String msg){
//		topicJmsTemplate.send(topic, new MessageCreator() {
//			public Message createMessage(Session session) throws JMSException {
//				System.out.println("topic name 是" + topic.toString()
//						+ "，发布消息内容为:\t" + msg);
//				return session.createTextMessage(msg);
//			}
//		});
//	}

	
	//getter/setter
//	public JmsTemplate getTopicJmsTemplate() {
//		return topicJmsTemplate;
//	}
//
//	public void setTopicJmsTemplate(JmsTemplate topicJmsTemplate) {
//		this.topicJmsTemplate = topicJmsTemplate;
//	}
	public String toString(){
		return "11111";
	}
}
