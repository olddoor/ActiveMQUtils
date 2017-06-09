package com.AmqUtil.poolFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.AmqUtil.common.MessageEntity;
import com.AmqUtil.exception.AMQFactoryException;
import com.AmqUtil.exception.AMQReceiverException;
import com.AmqUtil.exception.AMQSendException;
import com.AmqUtil.poolFactory.pool.ProducerPool;


public class AMQPoolSender {
	 private ReentrantLock lock = new ReentrantLock();//互斥锁
	 private Map<String,ProducerPool> mapProducer = new HashMap<String,ProducerPool>();
      /**
       * 发送Map消息
       * @param amqFactory amq工厂实例
       * @param dicMap map消息
       * @param topicName 目的地名称
       * @param NUM 消息发送频率，多少条消息发送一次
       * @return
       * @throws AMQSendException
       * @throws AMQFactoryException
       */
      public boolean sendMapMsg(AMQPoolFactory amqFactory, Map<String, Object> dicMap, String name, int NUM,String messageType,boolean ifVIP,boolean OnUse) 
		  		throws AMQSendException, AMQFactoryException{
          if (null == amqFactory){
              throw new AMQSendException("AMQPoolFactory is null");
          }
          if (dicMap.size() == 0) {
              throw new AMQSendException("Message Content must be not null");
          }
          if (null==name || "".equals(name)){
              throw new AMQSendException("name must be not null");
          }
          if(NUM==0 || NUM<0){
              throw new AMQSendException("NUM must greater than 0");
          }
          //进入锁定状态，防止狸猫换太子
          lock.lock();
          List<String> keyList = new ArrayList<String>(dicMap.keySet());
          boolean ifHasMsg = false;//是否还有剩余没法送消息标识
          ProducerPool producerPool = null;
          if(OnUse){//一直占用的把之前的获取到
        	  if(mapProducer.containsKey(name)){
        		  producerPool = mapProducer.get(name);
        	  }
          }
          if(producerPool == null){//没有获取到则新创建一个(首次使用则获取不到)
        	  producerPool = amqFactory.getProducer(name,messageType,ifVIP);
          }
          Session session = producerPool.getSession();
          MessageProducer producer = producerPool.getProducer();
          try{
        	  MapMessage mapMsg = session.createMapMessage();
              for (int i = 0; i < keyList.size(); i++){
            	  ifHasMsg = true;
                  mapMsg.setObject(keyList.get(i), dicMap.get(keyList.get(i)));
                  if ((i + 1) % NUM == 0){
                      producer.send(mapMsg);
                      mapMsg.clearBody();
                      ifHasMsg = false;
                  }
              }
              //如果还存在剩余没有发送的消息则把剩余消息发送
              if(ifHasMsg){
            	  producer.send(mapMsg);
                  mapMsg.clearBody();
              }
              return true;
          }catch (Exception e){
        	  amqFactory.disposeProducer(producer);
        	  amqFactory.producerDecreament(producerPool);
              throw new AMQSendException("send Map Message Error", e);
          }finally{
        	  if(!OnUse){//非专用通道则用完释放
        		  amqFactory.disposeProducer(producer);
        		  amqFactory.producerDecreament(producerPool);
        	  }else {
        		  mapProducer.put(name, producerPool);
			}
        	  lock.unlock();
          }
      }
      
      /**
       * 发送Map消息（带head和body）
       * @param amqFactory
       * @param entity
       * @param name
       * @param NUM
       * @param messageType
       * @param ifVIP
       * @param OnUse
       * @return
       * @throws AMQSendException
       * @throws AMQFactoryException
       */
      public boolean sendMapMsg(AMQPoolFactory amqFactory, MessageEntity entity, String name, int NUM,String messageType,boolean ifVIP,boolean OnUse)
    		  throws AMQSendException, AMQFactoryException{
          if (null == amqFactory){
              throw new AMQSendException("AMQPoolFactory is null");
          }
          if (entity == null) {
              throw new AMQSendException("Message Content must be not null");
          }
          if (null==name || "".equals(name)){
              throw new AMQSendException("name must be not null");
          }
          if(NUM==0 || NUM<0){
              throw new AMQSendException("NUM must greater than 0");
          }
          Map<String, Object> headMap = entity.getHeadMap();
          Map<String, Object> bodyMap = entity.getBodyMap();
          List<String> keyList = new ArrayList<String>(bodyMap.keySet());
          ProducerPool producerPool = null;
          //进入锁定状态，防止狸猫换太子
          lock.lock();
          if(OnUse){//一直占用的把之前的获取到
        	  if(mapProducer.containsKey(name)){
        		  producerPool = mapProducer.get(name);
        	  }
          }
          if(producerPool == null){//没有获取到则新创建一个(首次使用则获取不到)
        	  producerPool = amqFactory.getProducer(name,messageType,ifVIP);
          }
          Session session = producerPool.getSession();
          MessageProducer producer = producerPool.getProducer();
          boolean ifHasMsg = false;//是否还有剩余没法送消息标识
          try{
        	  MapMessage mapMsg = session.createMapMessage();
        	  if(!headMap.isEmpty() && headMap.size()>0){
        		  for (Iterator<String> it = headMap.keySet().iterator();it.hasNext();) {
        			  String key = it.next();
        			  mapMsg.setObjectProperty(key, headMap.get(key));
				}
        	  }
              for (int i = 0; i < keyList.size(); i++){
            	  ifHasMsg = true;
                  mapMsg.setObject(keyList.get(i), bodyMap.get(keyList.get(i)));
                  if ((i + 1) % NUM == 0){
                      producer.send(mapMsg);
                      mapMsg.clearBody();
                      ifHasMsg = false;
                  }
              }
              //如果还存在剩余没有发送的消息则把剩余消息发送
              if(ifHasMsg){
            	  producer.send(mapMsg);
                  mapMsg.clearBody();
              }
              return true;
          }catch (Exception e){
        	  amqFactory.disposeProducer(producer);
        	  amqFactory.producerDecreament(producerPool);
              throw new AMQSendException("send Map Message Error", e);
          }finally{
        	  if(!OnUse){//非专用通道则用完释放
        		  amqFactory.disposeProducer(producer);
        		  amqFactory.producerDecreament(producerPool);
        	  }else {
        		  mapProducer.put(name, producerPool);
			}
        	  lock.unlock();
          }
      }

      /**
       * 发送文本消息（带head和body）
       * @param amqFactory
       * @param entity
       * @param name
       * @param messageType
       * @param ifVIP
       * @param OnUse
       * @return
       * @throws AMQSendException
       * @throws AMQFactoryException
       */
      public boolean sendTextMsg(AMQPoolFactory amqFactory, MessageEntity entity, String name,String messageType,boolean ifVIP,boolean OnUse) 
    		  throws AMQSendException, AMQFactoryException{
          if (null == amqFactory){
              throw new AMQSendException("AMQFactory is null");
          }
          if (null == entity) {
              throw new AMQSendException("Message Content must be not null");
          }
          if (null == name || "".equals(name)) {
              throw new AMQSendException("parameter name must be not null");
          }
          //进入锁定状态，防止狸猫换太子
          lock.lock();
          Map<String, Object> headMap = entity.getHeadMap();
          Map<String, Object> bodyMap = entity.getBodyMap();
          
          ProducerPool producerPool = null;
          if(OnUse){//一直占用的把之前的获取到
        	  if(mapProducer.containsKey(name)){
        		  producerPool = mapProducer.get(name);
        	  }
          }
          if(producerPool == null){//没有获取到则新创建一个(首次使用则获取不到)
        	  producerPool = amqFactory.getProducer(name,messageType,ifVIP);
          }
          Session session = producerPool.getSession();
          MessageProducer producer = producerPool.getProducer();
          try{
              TextMessage textMsg = session.createTextMessage();
        	  if(!headMap.isEmpty() && headMap.size()>0){
        		  for(Iterator<String> it = headMap.keySet().iterator();it.hasNext();){
        			  String key = it.next();
        			  textMsg.setObjectProperty(key, headMap.get(key));
        		  }
        	  }
        	  for (Iterator<String> it = bodyMap.keySet().iterator();it.hasNext();) {
				String key = it.next();
				textMsg.setText(String.valueOf(bodyMap.get(key)));
			}
        	  producer.send(textMsg);
        	  return true;
          }catch (Exception e){
        	  amqFactory.disposeProducer(producer);
        	  amqFactory.producerDecreament(producerPool);
              throw new AMQSendException("send text Message error", e);
          }finally{
        	  if(!OnUse){
        		  amqFactory.disposeProducer(producer);
        		  amqFactory.producerDecreament(producerPool);
        	  }else {
        		  mapProducer.put(name, producerPool);
        	  }
        	  lock.unlock();
          }
      }
      
      /**
       * 发送文本消息
       * @param amqFactory amq工厂实例
       * @param text 文本消息
       * @param topicName 目的地名称
       * @return
       * @throws AMQSendException
       * @throws AMQFactoryException
       */
      public boolean sendTextMsg(AMQPoolFactory amqFactory, String text, String name,String messageType,boolean ifVIP,boolean OnUse) 
    		  throws AMQSendException, AMQFactoryException{
          if (null == amqFactory){
              throw new AMQSendException("AMQFactory is null");
          }
          if (null == text || "".equals(text)) {
              throw new AMQSendException("Message Content must be not null");
          }
          if (null == name || "".equals(name)) {
              throw new AMQSendException("parameter name must be not null");
          }
          //进入锁定状态，防止狸猫换太子
          lock.lock();
          ProducerPool producerPool = null;
          if(OnUse){//一直占用的把之前的获取到
        	  if(mapProducer.containsKey(name)){
        		  producerPool = mapProducer.get(name);
        	  }
          }
          if(producerPool == null){//没有获取到则新创建一个(首次使用则获取不到)
        	  producerPool = amqFactory.getProducer(name,messageType,ifVIP);
          }
          Session session = producerPool.getSession();
          MessageProducer producer = producerPool.getProducer();
          try{
              TextMessage textMsg = session.createTextMessage();
              textMsg.setText(text);
              producer.send(textMsg);
              return true;
          }catch (Exception e){
        	  amqFactory.disposeProducer(producer);
        	  amqFactory.producerDecreament(producerPool);
              throw new AMQSendException("send text Message error", e);
          }finally{
        	  if(!OnUse){
        		  amqFactory.disposeProducer(producer);
        		  amqFactory.producerDecreament(producerPool);
        	  }else {
        		  mapProducer.put(name, producerPool);
			}
        	  lock.unlock();
          }
      }
      
      /**
       * 释放所有producer连接
       * @return
       * @throws AMQSendException
       */
      public boolean disposeAllProducer() throws AMQSendException{
    	  if(mapProducer.size()>0){
  			try {
  				for (Iterator<String> it = mapProducer.keySet().iterator();it.hasNext();) {
  					String key = it.next();
  					MessageProducer producer = mapProducer.get(key).getProducer();
  					producer.close();
  				}
  				mapProducer.clear();
  			} catch (JMSException e) {
  				throw new AMQSendException("释放producer连接出错", e);
  			}
      	}
      	return true;
      }
      
      /**
       * 释放单个producer连接
       * @param name
       * @return
       * @throws AMQReceiverException
       */
      public boolean disposeProducer(AMQPoolFactory amqFactory,String name) throws AMQReceiverException{
      	if(mapProducer.size()>0){
      		try {
      			if(mapProducer.containsKey(name)){
      				ProducerPool producerPool = mapProducer.get(name);
      				producerPool.getProducer().close();//释放producer连接
          			amqFactory.producerDecreament(producerPool);//池中可用producer减1
          			mapProducer.remove(name);
          		}
  			} catch (JMSException e) {
  				throw new AMQReceiverException("释放producer连接出错", e);
  			}
      	}	
      	return true;
      }
}
