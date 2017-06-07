package com.funoMq.mq.listener;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * queue消息监听,消费v1.0
 * 官方说明: A MessageListener object is used to receive asynchronously delivered message
 * 注意这个是接受异步消息的接口, 正常情况下我们也是用异步队列.
 */
public class QueueMessageListener implements MessageListener{
	private static Logger logger = LoggerFactory.getLogger(QueueMessageListener.class); 
	@Override
	public void onMessage(Message message) {
		try {
		if(message instanceof TextMessage){
			TextMessage tm = (TextMessage) message;
		      System.out.println("JMSMessageID: "+message.getJMSMessageID());
		      System.out.println("在onMessage中线程名字是"+Thread.currentThread().getName());  
		      System.out.println("ConsumerMessageListener收到了文本消息：\t" + tm.getText());
		      System.out.println("---------");
		}else if(message instanceof ObjectMessage){
			ObjectMessage tm=(ObjectMessage)message;
		      System.out.println("JMSMessageID: "+message.getJMSMessageID());
		      System.out.println("在onMessage中线程名字是"+Thread.currentThread().getName());  
		      System.out.println("ConsumerMessageListener收到了文本消息：\t" + tm.getObject().toString());
		      System.out.println("---------");
		}
	    } catch (JMSException e) {
	      e.printStackTrace();
	    }
	  }
	
	

}
