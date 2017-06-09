package com.AmqUtil.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import com.AmqUtil.common.MessageEntity;
import com.AmqUtil.exception.AMQFactoryException;
import com.AmqUtil.exception.AMQSendException;

/**
 * AMQ发送消息管理类
 * @author duxianchao
 * @version 1.0
 * @updated 08-七月-2016 16:00:22
 */
public class AMQSender {
	 private ReentrantLock lock = new ReentrantLock();//互斥锁
	 /**
	  * 发送Map消息（带head和body）
	  * @param amqFactory
	  * @param entity
	  * @param topicName
	  * @param NUM
	  * @param messageType
	  * @return
	  * @throws AMQSendException
	  * @throws AMQFactoryException
	  */
      public boolean sendMapMsg(AMQFactory amqFactory, MessageEntity entity, String topicName, int NUM,String messageType) throws AMQSendException, AMQFactoryException
      {
          if (null == amqFactory){
              throw new AMQSendException("AMQFactory is null");
          }
          if (entity == null) {
              throw new AMQSendException("Message Content must be not null");
          }
          if (null==topicName || "".equals(topicName))
          {
              throw new AMQSendException("topicName must be not null");
          }
          if(NUM == 0 || NUM<0){
              throw new AMQSendException("NUM must greater than 0");
          }
          //进入锁定状态，防止狸猫换太子
          lock.lock();
          MessageProducer producer = amqFactory.getProducer(topicName,messageType);
          MapMessage mapMsg = amqFactory.getMapMessage();
          Map<String, Object> headMap = entity.getHeadMap();
          Map<String, Object> bodyMap = entity.getBodyMap();
          List<String> keyList = new ArrayList<String>(bodyMap.keySet());
          boolean ifHasMsg = false;//是否还有剩余没法送消息标识
          try{
        	  //存放head信息
        	  if(headMap.size()>0 && !headMap.isEmpty()){
        		  for (Iterator<String> it = headMap.keySet().iterator();it.hasNext();) {
        			  String key = it.next();
        			  mapMsg.setObjectProperty(key, headMap.get(key));
        		  }
        	  }
        	  //存放body信息
              for (int i = 0; i < keyList.size(); i++){
            	  ifHasMsg = true;
                  mapMsg.setObject(keyList.get(i), bodyMap.get(keyList.get(i)));
                  if ((i + 1) % NUM == 0)
                  {
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
              throw new AMQSendException("send Map Message Error", e);
          }finally{
        	  lock.unlock();
          }
      }
      
      /**
       * 发送Map消息
       * @param amqFactory
       * @param dictMap
       * @param topicName
       * @param NUM
       * @param messageType
       * @return
       * @throws AMQSendException
       * @throws AMQFactoryException
       */
      public boolean sendMapMsg(AMQFactory amqFactory, Map<String, Object> dictMap, String topicName, int NUM,String messageType) throws AMQSendException, AMQFactoryException
      {
          if (null == amqFactory){
              throw new AMQSendException("AMQFactory is null");
          }
          if (dictMap.size() == 0) {
              throw new AMQSendException("Message Content must be not null");
          }
          if (null==topicName || "".equals(topicName)){
              throw new AMQSendException("topicName must be not null");
          }
          if(NUM==0 || NUM<0){
              throw new AMQSendException("NUM must greater than 0");
          }
          //进入锁定状态，防止狸猫换太子
          lock.lock();
          MessageProducer producer = amqFactory.getProducer(topicName,messageType);
          MapMessage mapMsg = amqFactory.getMapMessage();
          List<String> keyList = new ArrayList<String>(dictMap.keySet());
          boolean ifHasMsg = false;//是否还有剩余没法送消息标识
          try{
        	  //存放body信息
              for (int i = 0; i < keyList.size(); i++){
            	  ifHasMsg = true;
                  mapMsg.setObject(keyList.get(i), dictMap.get(keyList.get(i)));
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
              throw new AMQSendException("send Map Message Error", e);
          }finally{
        	  lock.unlock();
          }
      }

      /**
       * 发送文本消息（带head和body的）
       * @param amqFactory
       * @param entity
       * @param topicName
       * @param messageType
       * @return
       * @throws AMQSendException
       * @throws AMQFactoryException
       */
      public boolean sendTextMsg(AMQFactory amqFactory, MessageEntity entity, String topicName,String messageType) throws AMQSendException, AMQFactoryException
      {
          if (null == amqFactory){
              throw new AMQSendException("AMQFactory is null");
          }
          if (null == entity ) {
              throw new AMQSendException("Message Content must be not null");
          }
          if (null == topicName || "".equals(topicName)) {
              throw new AMQSendException("parameter topicName must be not null");
          }
          //进入锁定状态，防止狸猫换太子
          lock.lock();
          MessageProducer producer = amqFactory.getProducer(topicName,messageType);
          Map<String, Object> headMap = entity.getHeadMap();
          Map<String, Object> bodyMap = entity.getBodyMap();
          try{
        	  TextMessage textMsg = amqFactory.getTextMessage();
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
              throw new AMQSendException("send text Message error", e);
          }finally{
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
      public boolean sendTextMsg(AMQFactory amqFactory, String text, String topicName,String messageType) throws AMQSendException, AMQFactoryException
      {
          if (null == amqFactory)
          {
              throw new AMQSendException("AMQFactory is null");
          }
          if (null == text || "".equals(text)) {
              throw new AMQSendException("Message Content must be not null");
          }
          if (null == topicName || "".equals(topicName)) {
              throw new AMQSendException("parameter topicName must be not null");
          }
          //进入锁定状态，防止狸猫换太子
          lock.lock();
          MessageProducer producer = amqFactory.getProducer(topicName,messageType);
          try{
              TextMessage textMsg = amqFactory.getTextMessage();
              textMsg.setText(text);
              producer.send(textMsg);
              return true;
          }catch (Exception e){
              throw new AMQSendException("send text Message error", e);
          }finally{
        	  lock.unlock();
          }
          
        
      }
}
