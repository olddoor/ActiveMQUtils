package com.AmqUtil.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.AmqUtil.common.MessageType;
import com.AmqUtil.exception.AMQFactoryException;
import com.AmqUtil.util.DESUtil;
import com.AmqUtil.util.IxpPropertyManager;

/**
 * AMQ实现工厂
 * @author duxianchao
 * @version 1.0
 * @updated 08-七月-2016 16:00:20
 */
public class AMQFactory {
	 private IxpPropertyManager pptUtil = IxpPropertyManager.getInstance();
	 private ConnectionFactory  factory;
     private Connection connection;
     private Session producerSession;//生产者session
     private Session consumerSession;//消费者session
     boolean ifCreateP = false;//生产者session是否建立的标志
     boolean ifCreateC = false;//消费者session是否建立的标志
     private String URL = "tcp://127.0.0.1:61616";
     private String userName = "admin";
     private String password = "admin";
     //producer连接池,一个topicName生产一个producer
     private Map<String, MessageProducer> producerPool = new HashMap<String, MessageProducer>();
     //consumer连接池,一个topicName生产一个consumer
     private Map<String, MessageConsumer> consumerPool = new HashMap<String, MessageConsumer>();
    
     public AMQFactory(String configPath) throws AMQFactoryException{
    	 loadConfig(configPath);
    	 initFactory();
     }
     
     public AMQFactory() throws AMQFactoryException{
    	 initFactory();
     }
     
     private void loadConfig(String configPath){
	    Properties config = new Properties();
 		config = pptUtil.getProperty(configPath);
 		URL = config.getProperty("URL");
 		userName = config.getProperty("userName");
 		password = DESUtil.deCode(config.getProperty("password"));//密码解密
     }
     
     private void initFactory() throws AMQFactoryException{
    	 try{
             factory = new ActiveMQConnectionFactory (URL);
             connection = factory.createConnection(userName, password);
             connection.start();
         }catch (Exception e){   
             throw new AMQFactoryException("ActiveMQ Connectioned is failed",e);
         }
     }

     /**
      * 获取mapMessage
      * @return
      * @throws AMQFactoryException
      */
     public MapMessage getMapMessage() throws AMQFactoryException{
         if (null == producerSession){
             throw new AMQFactoryException("producerSession is null");
         }
         try {
			return producerSession.createMapMessage();
		} catch (JMSException e) {
			throw new AMQFactoryException("createMapMessage error!",e);
		}
     }
     
     /**
      * 获取streamMessage
      * @return
      * @throws AMQFactoryException
      */
     public StreamMessage getStreamMessage() throws AMQFactoryException{
         if (null == producerSession){
             throw new AMQFactoryException("producerSession is null");
         }
         try {
			return producerSession.createStreamMessage();
		} catch (JMSException e) {
			throw new AMQFactoryException("createStreamMessage error!",e);
		}
     }

    /**
     * 获取TextMessage
     * @return
     * @throws AMQFactoryException
     */
     public TextMessage getTextMessage() throws AMQFactoryException{
         if (null == producerSession){
             throw new AMQFactoryException("producerSession is null");
         }
         try {
			return producerSession.createTextMessage();
		} catch (JMSException e) {
			throw new AMQFactoryException("createTextMessage error!",e);
		}
     }


     /**
      * 在内存中获取topic的producer，如果不存在则创建一个
      * @param topicName
      * @return
      * @throws AMQFactoryException
      */
     public MessageProducer getProducer(String topicName,String messageType) throws AMQFactoryException{
         if (!ifCreateP){
             try {
				producerSession = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);//不支持事务，消息自动响应
	             ifCreateP = true;
			} catch (JMSException e) {
				throw new AMQFactoryException("create AMQ session error!",e);
			}
         }
         MessageProducer producer = null;
         if (producerPool.size()>0){
             for(Iterator<String> it = producerPool.keySet().iterator();it.hasNext();)
             {
            	 String key = it.next();
                 if (topicName.equals(key))
                 {
                     producer = producerPool.get(key);
                     break;
                 }
             }
         }
         if (producer == null){
			try {
				Destination destination = null;
				if(MessageType.Topic.equals(messageType)){
					destination = producerSession.createTopic(topicName);
				}
				if(MessageType.Queue.equals(messageType)){
					destination = producerSession.createQueue(topicName);
				}
				 producer = producerSession.createProducer(destination);
				 //设置消息持久化
				 producer.setDeliveryMode(DeliveryMode.PERSISTENT);
				 if (!producerPool.containsKey(topicName)){
					producerPool.put(topicName, producer);
				 }
			} catch (JMSException e) {
				throw new AMQFactoryException("create AMQ producer error!",e);
			}
         }
         return producer;
     }

     /**
      * 在内存中获取topic的consumer，如果不存在则创建一个
      * @param topicName
      * @return
      * @throws AMQFactoryException
      */
     public MessageConsumer getConsumer(String topicName,String messageType) throws AMQFactoryException{
         if (!ifCreateC){
             try {
				consumerSession = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			} catch (JMSException e) {
				throw new AMQFactoryException("create AMQ session error!",e);
			}
             ifCreateC = true;
         }
         MessageConsumer consumer = null;
         if (consumerPool.size() > 0){
             for(Iterator<String> it=consumerPool.keySet().iterator();it.hasNext();){
            	 String key = it.next();
                 if (topicName.equals(key)){
                     //consumer = consumerPool.get(key);//使用同一个consumer只能启用一个监听器
                     break;
                 }
             }
         }
         if (consumer == null){
			try {
				Destination destination = null;
				if(MessageType.Topic.equals(messageType)){
					destination =consumerSession.createTopic(topicName);
				}
				if(MessageType.Queue.equals(messageType)){
					destination =consumerSession.createQueue(topicName);
				}
				consumer = consumerSession.createConsumer(destination);
				if (!consumerPool.containsKey(topicName)){
					consumerPool.put(topicName, consumer);
				}
			} catch (JMSException e) {
				throw new AMQFactoryException("create AMQ consumer error!",e);
			}
         }
        return consumer;
     }

     /**
      * 释放所有连接
      * @return
      * @throws AMQFactoryException
      */
     public boolean DisposeAll() throws AMQFactoryException {
         try{
             if (producerPool.size() > 0){
                 List<String> keyL = new ArrayList<String>(producerPool.keySet());
                 //keyL.addAll(producerPool.keySet());
                 for(String key : keyL){
                     producerPool.get(key).close();
                     producerPool.remove(key);
                 }
             }
             if (consumerPool.size() > 0){
                 List<String> keyC = new ArrayList<String>(consumerPool.keySet());
                 //keyC.addAll(consumerPool.keySet());
                 for(String key : keyC){
                     consumerPool.get(key).close();
                     consumerPool.remove(key);
                 }
             }
             if (producerSession != null){
                 producerSession.close();
             }
             if (consumerSession != null){
                 consumerSession.close();
             }
             if (connection != null){
                 connection.close();
             }
             return true;
         }catch (Exception e){
             throw new AMQFactoryException("Dispose AMQ All Connection Error",e);
         }
     }
     
     /**
      * 释放consumer连接
      * @param consumerName
      * @return
      * @throws AMQFactoryException
      */
     public boolean disposeConsumerByName(String consumerName) throws AMQFactoryException{
    	 if (consumerPool.size() > 0){
             List<String> keyC = new ArrayList<String>(consumerPool.keySet());
             try {
	             for(String key : keyC){
	            	 if(key.equals(consumerName)){
	            		 consumerPool.get(key).close();
	            		 consumerPool.remove(key);
	            		 return true;
	            	 }
	             }
             } catch (JMSException e) {
            	 throw new AMQFactoryException("Dispose Consumer Error",e);
             }
         }
    	 return false;
     }
}
