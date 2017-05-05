package com.funo.mq.vmBrokerDemo;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
 
import org.apache.activemq.ActiveMQConnectionFactory;
/**
 * 代码参考http://blog.csdn.net/dayuguohou2008/article/details/4930005
 * 本模块代码用于演示内嵌broker到虚拟机中的使用
 * @editor olddoor
 */
public class Consumer {
	   private static String brokerURL = "vm:broker:(tcp://localhost:6000)?brokerName=embeddedbroker&persistent=false";
	    //private static String brokerURL = "vm://localhost?brokerConfig=xbean:activemq.xml";
	    private static transient ConnectionFactory factory;
	    private transient Connection connection;
	    private transient Session session;
	    
	    private String jobs[] = new String[]{"Queue-1", "Queue-2"};
	    
	    public Consumer() throws JMSException {
	     factory = new ActiveMQConnectionFactory(brokerURL);
	     connection = factory.createConnection();
	        connection.start();
	        //采用非事务、自动应答的策略
	        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    }
	    
	    public void close() throws JMSException {
	        if (connection != null) {
	            connection.close();
	        }
	    }    
	    
	    public static void main(String[] args) throws JMSException, InterruptedException {
	     Consumer consumer = new Consumer();
	     
	     for (String job : consumer.jobs) {
	    	 //创建queue队列
	      Destination destination = consumer.getSession().createQueue("JOBS." + job);
	      MessageConsumer messageConsumer = consumer.getSession().createConsumer(destination);
	      	//添加队列监听
	      messageConsumer.setMessageListener(new Listener(job));
	     }
	    }
	 
	 public Session getSession() {
	  return session;
	 }
}
