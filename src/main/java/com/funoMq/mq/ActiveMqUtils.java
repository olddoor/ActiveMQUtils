package com.funoMq.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.funoMq.mq.service.ConsumerService;
import com.funoMq.mq.service.ProducerService;
import com.funoMq.mq.util.SpringContextUtil;

/**
 * activeMQ工具类
 * @author olddoor
 */
//@Component
public class ActiveMqUtils  {
	
	/**
	原先规划对多消费者监听同一队列的情况下,增加算法起到消费者负载均衡的效果.
	现实中考虑到多消费者监听同一个队列事实上在消费者一簇也起了负载均衡的效果. 本部分规划停止.
	public static String strategy_Round_Robin="Round_Robin";
	public static String strategy_Random="Random";
	public static String strategy_Hash="Hash";
	public static String strategy_Weight_Round_Robin="Weight_Round_Robin";
	public static String strategy_Weight_Random="Weight_Random";
	 */
	
	private static Logger logger = LoggerFactory.getLogger(ActiveMqUtils.class); 
	
	private static ConsumerService consumerService = ((ConsumerService)SpringContextUtil.getBean("consumerServiceImpl"));
	
	private static ProducerService producerService = ((ProducerService)SpringContextUtil.getBean("producerServiceImpl"));

	public static ConsumerService getConsumerService() {
		return consumerService;
	}

	public static void setConsumerService(ConsumerService consumerService) {
		ActiveMqUtils.consumerService = consumerService;
	}

	public static ProducerService getProducerService() {
		return producerService;
	}

	public static void setProducerService(ProducerService producerService) {
		ActiveMqUtils.producerService = producerService;
	}
	
	
}
