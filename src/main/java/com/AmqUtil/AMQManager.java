package com.AmqUtil;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.AmqUtil.common.MessageEntity;
import com.AmqUtil.exception.AMQFactoryException;
import com.AmqUtil.exception.AMQReceiverException;
import com.AmqUtil.exception.AMQSendException;
import com.AmqUtil.factory.AMQFactory;
import com.AmqUtil.factory.AMQReceiver;
import com.AmqUtil.factory.AMQSender;
import com.AmqUtil.poolFactory.AMQPoolFactory;
import com.AmqUtil.poolFactory.AMQPoolReceiver;
import com.AmqUtil.poolFactory.AMQPoolSender;


/**
 * AMQ调度管理器
 * @author duxianchao 该调度器实现了2种方式的AMQ调度： 1、使用连接池方式的发送接收消息 2、使用非连接池方式的发送接收消息
 * 目前只支持发送Map和Text消息，如果需要其他类型的消息可在此基础上进行扩展
 * @version 1.0
 * @updated 08-七月-2016 16:00:21
 */
public class AMQManager {

	 private static volatile AMQManager instance = null;
	 private ReentrantLock lock = new ReentrantLock();//互斥锁
     private AMQFactory factory = null;
     private AMQPoolFactory poolFactory = null;
     private AMQReceiver rece = new AMQReceiver();
     private AMQSender sender = new AMQSender();
     private AMQPoolSender poolSender = new AMQPoolSender();
     private AMQPoolReceiver poolReceiver = new AMQPoolReceiver();
     public boolean ifInitalized = false;//是否初始化标识
     private AMQManager(){};
     
     /**
      * 线程安全的单例模式实例化
      * @return
      */
     public static AMQManager getInstance(){
    	 if(instance==null){
    		 synchronized(AMQManager.class){
    			 if (instance == null){
    				 instance = new AMQManager();
    			 }
    		 }
    	 }
         return instance;
     }
     
    /**
     * 使用配置文件初始化AMQ连接
     * @param configFilePath
     * @param ifUsePool 是否使用连接池
     * @throws AMQFactoryException
     */
     public void init(String configFilePath,boolean ifUsePool) throws AMQFactoryException{
    	 //避免多次实例化工厂类
    	 lock.lock();
         try{
             if (!ifInitalized){
            	 if(ifUsePool){
            		 poolFactory = new AMQPoolFactory(configFilePath);
                     ifInitalized = true;
            	 }else {
            		 factory = new AMQFactory(configFilePath);
                     ifInitalized = true;
				}
                 
             }
         }catch (AMQFactoryException e){
             throw e;
         }finally{
        	 lock.unlock();
         }
     }
     
     /**
      * 使用缺省设置初始化AMQ连接
      * @param ifUsePool 是否使用连接池
      * @throws AMQFactoryException
      */
     public void init(boolean ifUsePool) throws AMQFactoryException{
         //避免多次实例化工厂类
    	 lock.lock();
         try{
             if (!ifInitalized){
            	 if(ifUsePool){
            		 poolFactory = new AMQPoolFactory();
                     ifInitalized = true;
            	 }else {
            		 factory = new AMQFactory();
                     ifInitalized = true;
				}
                 
             }
         }catch (AMQFactoryException e){
             throw e;
         }finally{
        	 lock.unlock();
         }
     }

     /**
      * 连接池方式发送带Head的Map消息，使用默认发送次数
      * @param msgEntity 消息
      * @param name 队列或topic名称
      * @param messageType 消息类型
      * @param ifVIP 是否使用专线
      * @param OnUse 是否一直占用
      * @return
      * @throws AMQSendException
      * @throws AMQFactoryException
      */
     public boolean sendMapMsg(MessageEntity msgEntity, String name,String messageType,boolean ifVIP,boolean OnUse) throws AMQSendException, AMQFactoryException
     {
         return sendMapMsg(msgEntity, name, 50,messageType,ifVIP,OnUse);   
     }
     
     /**
      * 使用连接池方式发送不带head的Map消息，使用默认发送次数
      * @param dicMap
      * @param topicName
      * @param messageType
      * @param ifVIP
      * @param OnUse
      * @return
      * @throws AMQSendException
      * @throws AMQFactoryException
      */
     public boolean sendMapMsg(Map<String, Object> dicMap, String topicName,String messageType,boolean ifVIP,boolean OnUse) throws AMQSendException, AMQFactoryException
     {
         return sendMapMsg(dicMap, topicName, 50,messageType);   
     }
     
     /**
      * 非连接池方式发送带head的Map消息，使用默认发送次数
      * @param msgEntity
      * @param name 队列或topic名称
      * @param messageType 消息类型
      * @return
      * @throws AMQSendException
      * @throws AMQFactoryException
      */
     public boolean sendMapMsg(MessageEntity msgEntity, String name,String messageType) throws AMQSendException, AMQFactoryException
     {
         return sendMapMsg(msgEntity, name, 50,messageType);   
     }
     
     /**
      * 使用非连接池方式发送不带head的Map消息，使用默认发送次数
      * @param dicMap
      * @param name 队列或topic名称
      * @param messageType 消息类型
      * @return
      * @throws AMQSendException
      * @throws AMQFactoryException
      */
     public boolean sendMapMsg(Map<String, Object> dicMap, String name,String messageType) throws AMQSendException, AMQFactoryException
     {
         return sendMapMsg(dicMap, name, 50,messageType);   
     }
     
    /**
     * 使用连接池方式发送带Head的Map消息，使用指定发送次数
     * @param msgEntity
     * @param name
     * @param NUM 发送次数（每多少条发送一次）
     * @param messageType 消息类型
     * @param ifVIP 是否使用专线
     * @param OnUse 是否一直占用
     * @return
     * @throws AMQSendException
     * @throws AMQFactoryException
     */
     public boolean sendMapMsg(MessageEntity msgEntity, String name,int NUM,String messageType,boolean ifVIP,boolean OnUse) throws AMQSendException, AMQFactoryException
     {
         if (ifInitalized){
    		 return poolSender.sendMapMsg(poolFactory, msgEntity, name, NUM, messageType, ifVIP, OnUse);
         }else{
             return false;
         }
     }
     
     /**
      * 使用连接池方式发送不带Head的Map消息，使用指定发送次数
      * @param dicMap
      * @param name 队列或topic名称
      * @param NUM 发送次数（每多少条发送一次）
      * @param messageType 消息类型
      * @param ifVIP 是否使用专线
      * @param OnUse 是否一直占用
      * @return
      * @throws AMQSendException
      * @throws AMQFactoryException
      */
     public boolean sendMapMsg(Map<String, Object> dicMap, String name,int NUM,String messageType,boolean ifVIP,boolean OnUse) throws AMQSendException, AMQFactoryException
     {
         if (ifInitalized){
    		 return poolSender.sendMapMsg(poolFactory, dicMap, name, NUM, messageType, ifVIP, OnUse);
         }else{
             return false;
         }
     }
     
     /**
      * 使用非连接池方式发送带Head的Map消息，使用指定发送次数
      * @param msgEntity
      * @param name
      * @param NUM
      * @param messageType
      * @return
      * @throws AMQSendException
      * @throws AMQFactoryException
      */
     public boolean sendMapMsg(MessageEntity msgEntity, String name,int NUM,String messageType) throws AMQSendException, AMQFactoryException
     {
         if (ifInitalized){
    		 return sender.sendMapMsg(factory, msgEntity, name,NUM,messageType);
         }else{
             return false;
         }

     }
     
     /**
      * 使用非连接池方式发送不带Head的Map消息，使用指定发送次数
      * @param dicMap
      * @param name
      * @param NUM
      * @param messageType
      * @return
      * @throws AMQSendException
      * @throws AMQFactoryException
      */
     public boolean sendMapMsg(Map<String, Object> dicMap, String name,int NUM,String messageType) throws AMQSendException, AMQFactoryException
     {
         if (ifInitalized){
    		 return sender.sendMapMsg(factory, dicMap, name,NUM,messageType);
         }else{
             return false;
         }

     }
     
     /**
      * 使用连接池方式发送带head的Text消息
      * @param msgEntity
      * @param name
      * @param messageType
      * @param ifVIP
      * @param OnUse
      * @return
      * @throws AMQSendException
      * @throws AMQFactoryException
      */
     public boolean sendTextMsg(MessageEntity msgEntity, String name,String messageType,boolean ifVIP,boolean OnUse) throws AMQSendException, AMQFactoryException
     {
         if (ifInitalized){
             return poolSender.sendTextMsg(poolFactory, msgEntity, name, messageType, ifVIP, OnUse);
         }else{
             return false;
         }
     }
     
     /**
      * 使用连接池方式发送不带head的Text消息
      * @param text
      * @param name
      * @param messageType		消息发送类型queen还是topic  
      * @param ifVIP			
      * @param OnUse
      * @return
      * @throws AMQSendException
      * @throws AMQFactoryException
      */
     public boolean sendTextMsg(String text, String name,String messageType,boolean ifVIP,boolean OnUse) throws AMQSendException, AMQFactoryException
     {
         if (ifInitalized){
             return poolSender.sendTextMsg(poolFactory, text, name, messageType, ifVIP, OnUse);
         }else{
             return false;
         }
     }
     
     /**
      * 使用非连接池方式发送带head的Text消息
      * @param msgEntity
      * @param name
      * @param messageType
      * @return
      * @throws AMQSendException
      * @throws AMQFactoryException
      */
     public boolean sendTextMsg(MessageEntity msgEntity, String name,String messageType) throws AMQSendException, AMQFactoryException
     {
         if (ifInitalized){
             return sender.sendTextMsg(factory, msgEntity, name,messageType);
         }else{
             return false;
         }
     }
     
     /**
      * 使用非连接池方式发送不带head的Text消息
      * @param text
      * @param name
      * @param messageType
      * @return
      * @throws AMQSendException
      * @throws AMQFactoryException
      */
     public boolean sendTextMsg(String text, String name,String messageType) throws AMQSendException, AMQFactoryException
     {
         if (ifInitalized){
             return sender.sendTextMsg(factory, text, name,messageType);
         }else{
             return false;
         }
     }
     
     /**
      * 非连接池方式用户订阅消息与用户自定义消息处理类关联
      * @param topicName
      * @param cls
      * @param messageType 消息目的地类型
      * @return
      * @throws AMQReceiverException
      * @throws AMQFactoryException
      */
     public boolean setReceive(String topicName, Class<?> cls,String messageType) throws AMQReceiverException, AMQFactoryException{
         if (ifInitalized){
             return rece.setReceive(topicName, factory, cls,messageType);
         }else{
             return false;
         }
     }
     
     /**
      * 非连接池方式用户订阅消息与用户自定义消息处理类关联
      * @param topicName
      * @param cls
      * @return
      * @throws AMQReceiverException
      * @throws AMQFactoryException
      */
     public boolean setListener(String topicName, Class<?> cls,String messageType) throws AMQReceiverException, AMQFactoryException{
         if (ifInitalized){
             return rece.setListener(topicName, factory, cls,messageType);
         }else{
             return false;
         }
     }
     
     /**
      * 连接池方式设置监听
      * @param topicName
      * @param cls
      * @param messageType
      * @param ifVIP
      * @return
      * @throws AMQReceiverException
      * @throws AMQFactoryException
      */
     public boolean setReceive(String topicName, Class<?> cls,String messageType,boolean ifVIP) throws AMQReceiverException, AMQFactoryException{
         if (ifInitalized){
             return poolReceiver.setReceive(topicName, poolFactory, cls,messageType,ifVIP);
         }else{
             return false;
         }
     }
     
     /**
      * 连接池方式设置监听
      * @param topicName
      * @param cls
      * @param messageType
      * @param ifVIP
      * @return
      * @throws AMQReceiverException
      * @throws AMQFactoryException
      */
     public boolean setListener(String topicName, Class<?> cls,String messageType,boolean ifVIP) throws AMQReceiverException, AMQFactoryException{
         if (ifInitalized){
             return poolReceiver.setListener(topicName, poolFactory, cls,messageType,ifVIP);
         }else{
             return false;
         }
     }

     /**
      * 释放所有连接
      * @return
      * @throws AMQFactoryException
      */
     public boolean disposeAll() throws AMQFactoryException {
        return factory.DisposeAll();
     }
     
     public boolean disposePoolAll() throws AMQFactoryException, AMQSendException, AMQReceiverException {
         return  poolSender.disposeAllProducer() && poolReceiver.disposeAllConsumer() && poolFactory.disposeAll();
      }
     
     /**
      * 释放consumer连接
      * @param consumerName
      * @return
      * @throws AMQFactoryException
      */
     public boolean disposeConsumerByName(String consumerName) throws AMQFactoryException{
    	 return factory.disposeConsumerByName(consumerName);
     }
     
     /**
      * 释放池中单个consume连接
      * @param consumerName
      * @return
      * @throws AMQReceiverException
      */
     public boolean disposePoolConsumerByName(String consumerName) throws  AMQReceiverException{
    	 return poolReceiver.disposeConsumer(poolFactory,consumerName);
     }
     
     /**
      * 释放池中单个producer连接
      * @param producerName
      * @return
      * @throws AMQReceiverException
      */
     public boolean disposePoolProducerByName(String producerName) throws  AMQReceiverException{
    	 return poolSender.disposeProducer(poolFactory,producerName);
     }

}
