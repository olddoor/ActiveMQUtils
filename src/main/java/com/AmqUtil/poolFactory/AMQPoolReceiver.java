package com.AmqUtil.poolFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import com.AmqUtil.common.AbsListener;
import com.AmqUtil.exception.AMQFactoryException;
import com.AmqUtil.exception.AMQReceiverException;
import com.AmqUtil.poolFactory.pool.ConsumerPool;


public class AMQPoolReceiver {
	private ReentrantLock lock = new ReentrantLock();//互斥锁
	private Map<String,ConsumerPool> mapConsumer = new HashMap<String,ConsumerPool>();
    /**
     * 关联用户订阅消息与用户自定义业务监听类
     * @param name
     * @param factory
     * @param className 监听类
     * @param messageType 消息类型
     * @param ifVIP 是否使用专线
     * @return
     * @throws AMQReceiverException
     * @throws AMQFactoryException
     */
    public boolean setListener(String name, AMQPoolFactory factory, Class<?> className,String messageType,boolean ifVIP) 
    		throws AMQReceiverException, AMQFactoryException{
        if (null == factory) {
            throw new AMQReceiverException("AMQFactory is null");
        }
        if (null == className) {
            throw new AMQReceiverException("user Listener must be not null");
        }
        if (null == name || "".equals(name)) {
            throw new AMQReceiverException("parameter name must be not null");
        }
        //进入写模式锁定状态，防止误监听
       lock.lock();
        //利用反射机制实例化用户自定义监听类
       ConsumerPool consumerPool = factory.getConsumer(name, messageType, ifVIP);
       MessageConsumer consumer = null;
		try {
			MessageListener rece = (MessageListener) className.newInstance();
			consumer = consumerPool.getConsumer();
			mapConsumer.put(name, consumerPool);
			consumer.setMessageListener(rece);
			return true;
		} catch (InstantiationException e) {
			factory.disposeConsumer(consumer);
			factory.consumerDecreament(consumerPool);
			throw new AMQReceiverException("newInstance listener error!",e);
		} catch (IllegalAccessException e) {
			factory.disposeConsumer(consumer);
			factory.consumerDecreament(consumerPool);
			throw new AMQReceiverException("newInstance listener error!",e);
		} catch (JMSException e) {
			factory.disposeConsumer(consumer);
			factory.consumerDecreament(consumerPool);
			throw new AMQReceiverException("set listener error!",e);
		}finally{
			lock.unlock();
		}
    }   
    
  /**
   * 关联用户订阅消息与用户自定义业务监听类
   * @param name
   * @param factory
   * @param className 监听类
   * @param messageType 消息类型
   * @param ifVIP 是否使用专线
   * @return
   * @throws AMQReceiverException
   * @throws AMQFactoryException
   */
    public boolean setReceive(String name, AMQPoolFactory factory, Class<?> className,String messageType,boolean ifVIP) 
    		throws AMQReceiverException, AMQFactoryException{
        if (null == factory) {
            throw new AMQReceiverException("AMQFactory is null");
        }
        if (null == className) {
            throw new AMQReceiverException("user Listener must be not null");
        }
        if (null == name || "".equals(name)) {
            throw new AMQReceiverException("parameter name must be not null");
        }
        //进入写模式锁定状态，防止误监听
       lock.lock();
       ConsumerPool consumerPool = factory.getConsumer(name, messageType, ifVIP);
        //利用反射机制实例化用户自定义监听类
       MessageConsumer consumer = null;
		try {
			AbsListener rece = (AbsListener)className.newInstance();
		    consumer = consumerPool.getConsumer();
		    mapConsumer.put(name, consumerPool);
			consumer.setMessageListener(rece);
			return true;
		} catch (InstantiationException e) {
			factory.disposeConsumer(consumer);
			factory.consumerDecreament(consumerPool);
			throw new AMQReceiverException("newInstance listener error!",e);
		} catch (IllegalAccessException e) {
			factory.disposeConsumer(consumer);
			factory.consumerDecreament(consumerPool);
			throw new AMQReceiverException("newInstance listener error!",e);
		} catch (JMSException e) {
			factory.disposeConsumer(consumer);
			factory.consumerDecreament(consumerPool);
			throw new AMQReceiverException("set listener error!",e);
		}finally{
			lock.unlock();
		}
      
    }
    
    /**
     * 释放所有consumer连接
     * @return
     * @throws AMQReceiverException
     */
    public boolean disposeAllConsumer() throws AMQReceiverException{
    	if(mapConsumer.size()>0){
			try {
				for (Iterator<String> it = mapConsumer.keySet().iterator();it.hasNext();) {
					String key = it.next();
					ConsumerPool consumerPool = mapConsumer.get(key);
					consumerPool.getConsumer().close();
				}
				mapConsumer.clear();
			} catch (JMSException e) {
				throw new AMQReceiverException("释放consumer连接出错", e);
			}
    	}
    	return true;
    }
    
    /**
     * 释放单个consumer连接
     * @param name
     * @return
     * @throws AMQReceiverException
     */
    public boolean disposeConsumer(AMQPoolFactory poolFactory,String name) throws AMQReceiverException{
    	if(mapConsumer.size()>0){
    		try {
    			if(mapConsumer.containsKey(name)){
    				ConsumerPool consumerPool = mapConsumer.get(name);
    				consumerPool.getConsumer().close();//释放consumer连接
    				poolFactory.consumerDecreament(consumerPool);//池中可用consumer减1
        			mapConsumer.remove(name);
        		}
			} catch (JMSException e) {
				throw new AMQReceiverException("释放consumer连接出错", e);
			}
    	}	
    	return true;
    }
}
