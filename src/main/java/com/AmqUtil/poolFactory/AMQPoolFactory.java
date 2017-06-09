package com.AmqUtil.poolFactory;

import java.util.LinkedList;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.AmqUtil.common.MessageType;
import com.AmqUtil.exception.AMQFactoryException;
import com.AmqUtil.poolFactory.pool.ConnectionPool;
import com.AmqUtil.poolFactory.pool.ConsumerPool;
import com.AmqUtil.poolFactory.pool.ProducerPool;
import com.AmqUtil.poolFactory.pool.SessionPool;
import com.AmqUtil.util.DESUtil;
import com.AmqUtil.util.IxpPropertyManager;


/**
 * AMQ连接池管理类
 * @author duxianchao
 * @version 1.0
 * @updated 08-七月-2016 16:00:21
 */
@SuppressWarnings("unused")
public class AMQPoolFactory {
	private IxpPropertyManager pptUtil = IxpPropertyManager.getInstance();
	private String URL = "tcp://127.0.0.1:61616";
	private String userName = "admin";
	private String password = "admin";
	private int maxConnection = 10;//connection最大连接数
	private int minConnection = 1;//connection最小连接数
	private int maxSesstionPerConnection = 100;//每个connection可建的最大session数
	private int minSesstionPerConnection = 10;//每个connection可建的最小session数
	private int maxProducerPerSession = 10;//每个session可建的最大producer数
	private int minProducerPerSession = 1;//暂未使用
	private int maxConsumerPerSession = 10;//每个session可建的最大consumer数
	private int minConsumerPerSession = 1;//暂未使用
	private ConnectionFactory factory;
	private LinkedList<ConnectionPool> pooledConnection = new LinkedList<ConnectionPool>();
	private LinkedList<SessionPool> pooledSession = new LinkedList<SessionPool>();
	
	/**
	 * 使用配置文件
	 * @param configFilePath
	 * @throws AMQFactoryException
	 */
	public AMQPoolFactory(String configFilePath) throws AMQFactoryException{
		LoadConfig(configFilePath);
	}
	
	/**
	 * 缺省设置
	 * @throws AMQFactoryException
	 */
	public AMQPoolFactory() throws AMQFactoryException{
		initFactory();
	}
	
	/**
	 * 初始化配置
	 * @param filePath
	 * @throws AMQFactoryException
	 */
	private void LoadConfig(String filePath) throws AMQFactoryException{
		Properties config = new Properties();
		config = pptUtil.getProperty(filePath);
		URL = config.getProperty("URL");
		userName = config.getProperty("userName");
		password = DESUtil.deCode(config.getProperty("password"));//密码解密
		maxConnection = Integer.parseInt(config.getProperty("maxConnections"));
		minConnection = Integer.parseInt(config.getProperty("minConnections"));
		maxSesstionPerConnection = Integer.parseInt(config.getProperty("maxSessionPerConnection"));
		minSesstionPerConnection = Integer.parseInt(config.getProperty("minSessionPerConnection"));
		maxProducerPerSession = Integer.parseInt(config.getProperty("maxProducer"));
		minProducerPerSession = Integer.parseInt(config.getProperty("minProducer"));
		maxConsumerPerSession = Integer.parseInt(config.getProperty("maxConsumer"));
		minConsumerPerSession = Integer.parseInt(config.getProperty("minConsumer"));
		initFactory();
	}
	
	/**
	 * 初始化AMQ
	 * @throws AMQFactoryException
	 */
	private void initFactory() throws AMQFactoryException{
		try {
			factory = new ActiveMQConnectionFactory (URL);
			if(minConnection>0 && minConnection<=maxConnection){
				for(int i=0;i<minConnection;i++){
					Connection connection = factory.createConnection(userName,password);
					connection.start();
					ConnectionPool connPool = new ConnectionPool();
					connPool.setConnection(connection);//存放Conn连接
					if(minSesstionPerConnection>0 && minSesstionPerConnection<=maxSesstionPerConnection){
						connPool.setActiveSessions(minSesstionPerConnection);//设置当前存在session数目
						for (int j = 0; j < minSesstionPerConnection; j++) {
							Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
							SessionPool sessionPool = new SessionPool();
							sessionPool.setConnection(connection);
							sessionPool.setSession(session);
							pooledSession.addLast(sessionPool);
						}
						pooledConnection.addLast(connPool);
					}else {
						throw new AMQFactoryException("AMQ配置minSessionPerConnection和maxSessionPerConnection错误");
					}
				}
			}else{
				throw new AMQFactoryException("AMQ配置minConnections和maxConnections错误");
			}
		} catch (JMSException e) {
			throw new AMQFactoryException("AMQ初始化异常",e);
		}
	}
	
	/**
	 * 池中获取connection连接
	 * @return
	 * @throws AMQFactoryException
	 */
	private ConnectionPool getConnection() throws AMQFactoryException{
		ConnectionPool connPool = null;
		if(pooledConnection!=null && pooledConnection.size()>0){
			for(ConnectionPool connectionPool : pooledConnection){
				int poolSessionSize = connectionPool.getActiveSessions();
				if(poolSessionSize<maxSesstionPerConnection){
					connPool = connectionPool;
				}
			}
			if(connPool==null && pooledConnection.size()<maxConnection){
				try {
					Connection conn = factory.createConnection(userName, password);
					conn.start();
					connPool = new ConnectionPool();
					connPool.setConnection(conn);//存放Conn连接
					if(minSesstionPerConnection>0 && minSesstionPerConnection<=maxSesstionPerConnection){
						connPool.setActiveSessions(minSesstionPerConnection);//设置当前存在session数目
						for (int j = 0; j < minSesstionPerConnection; j++) {
							Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
							SessionPool sessionPool = new SessionPool();
							sessionPool.setSession(session);
							sessionPool.setConnection(conn);
							pooledSession.addLast(sessionPool);
						}
					}
					pooledConnection.addLast(connPool);
				} catch (JMSException e) {
					throw new AMQFactoryException("getConnection方法创建Connection异常",e);
				}
			}
		}
		return connPool;
	}
	
	/**
	 * 池中获取producer的session
	 * @param ifVIP
	 * @return
	 * @throws AMQFactoryException
	 */
	private SessionPool getProducerSession(boolean ifVIP) throws AMQFactoryException{
		SessionPool sesPool = null;
		if(pooledSession!=null && pooledSession.size()>0){
			ConnectionPool connPool = getConnection();
			for(SessionPool sessionPool : pooledSession){
				if(sessionPool.getConnection()==connPool.getConnection()){
					int poolProducerSize = sessionPool.getAvailableProducer();
					if(ifVIP){
						if(poolProducerSize == 0){
							sesPool = sessionPool;
						}
					}else{
						if(poolProducerSize<maxProducerPerSession){
							sesPool = sessionPool;
						}
					}
				}
			}
			//session已被占满，新建一个session
			if(sesPool == null && connPool.getActiveSessions()<maxSesstionPerConnection){
				try {
					Connection conn = connPool.getConnection();
					Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
					sesPool = new SessionPool();
					sesPool.setConnection(conn);
					sesPool.setSession(session);
					pooledSession.addLast(sesPool);
				} catch (JMSException e) {
					throw new AMQFactoryException("getProducerSession方法创建session出错",e);
				}
			}
		}
		return sesPool;
	}
	
	/**
	 * 池中获取consumer的session
	 * @param ifVIP
	 * @return
	 * @throws AMQFactoryException
	 */
	private SessionPool getConsumerSession(boolean ifVIP) throws AMQFactoryException{
		SessionPool sespool = null;
		if(pooledSession!=null && pooledSession.size()>0){
			ConnectionPool connPool = getConnection();
			for(SessionPool sessionPool : pooledSession){
				if(sessionPool.getConnection()==connPool.getConnection()){
					int poolConsumerSize = sessionPool.getAvailableConsumer();
					if(ifVIP){
						if(poolConsumerSize == 0){
							sespool = sessionPool;
						}
					}else{
						if(poolConsumerSize<maxConsumerPerSession){
							sespool = sessionPool;
						}
					}
				}
			}
			//session已被占满，新建一个session
			if(sespool == null && connPool.getActiveSessions()<maxSesstionPerConnection){
				try {
					Connection conn = connPool.getConnection();
					Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
					sespool = new SessionPool();
					sespool.setConnection(conn);
					sespool.setSession(session);
					pooledSession.addLast(sespool);
				} catch (JMSException e) {
					throw new AMQFactoryException("getConsumerSession方法创建session出错",e);
				}
			}
		}
		return sespool;
	}
	
	/**
	 * 获取producer连接
	 * @param name
	 * @param messageType
	 * @param ifVIP 是否使用专线
	 * @return
	 * @throws AMQFactoryException
	 */
	public ProducerPool getProducer(String name,String messageType,boolean ifVIP) throws AMQFactoryException{
		SessionPool sessionPool = getProducerSession(ifVIP);
		Session session = sessionPool.getSession();
		try {
			Destination ds = null;
			if(messageType.equals(MessageType.Queue)){
				ds = session.createQueue(name);
			}
			if(messageType.equals(MessageType.Topic)){
				ds = session.createTopic(name);
			}
			MessageProducer producer = session.createProducer(ds);
			ProducerPool producerPool = new ProducerPool();
			producerPool.setProducer(producer);
			producerPool.setConnection(sessionPool.getConnection());
			producerPool.setSession(session);
			producerIncreament(sessionPool);//producer增长1
			return producerPool;
		} catch (JMSException e) {
			throw new AMQFactoryException("获取Producer出错",e);
		}
	}
	
	/**
	 * 获取consumer连接
	 * @param name
	 * @param messageType
	 * @param ifVIP 是否使用专线
	 * @return
	 * @throws AMQFactoryException
	 */
	public ConsumerPool getConsumer(String name,String messageType,boolean ifVIP) throws AMQFactoryException{
		SessionPool sessionPool = getConsumerSession(ifVIP);
		Session session = sessionPool.getSession();
		try {
			Destination ds = null;
			if(messageType.equals(MessageType.Queue)){
				ds = session.createQueue(name);
			}
			if(messageType.equals(MessageType.Topic)){
				ds = session.createTopic(name);
			}
			MessageConsumer consumer = session.createConsumer(ds);
			ConsumerPool consumerPool = new ConsumerPool();
			consumerPool.setConnection(sessionPool.getConnection());
			consumerPool.setSession(session);
			consumerPool.setConsumer(consumer);
			consumerIncreament(sessionPool);
			return consumerPool;
		} catch (JMSException e) {
			throw new AMQFactoryException("获取Consumer出错",e);
		}
	}
	
	/**
	 * 池中可用producer递增1
	 * @param sessionPool
	 */
	private void producerIncreament(SessionPool sessionPool){
		if(sessionPool!=null){
			for(SessionPool sePool : pooledSession){
				if(sePool==sessionPool){
					int cnt = sePool.getAvailableProducer();
					cnt++;
					sePool.setAvailableProducer(cnt);
				}
			}
		}
	}
	
	/**
	 * 池中可用producer递减1
	 * @param producerPool
	 */
	public void producerDecreament(ProducerPool producerPool){
		if(producerPool!=null){
			for(SessionPool sessionPool : pooledSession){
				if(sessionPool.getConnection()==producerPool.getConnection() 
						&& sessionPool.getSession()==producerPool.getSession()){
					int cnt = sessionPool.getAvailableProducer();
					cnt--;
					sessionPool.setAvailableProducer(cnt);
				}
			}
		}
	}
	
	/**
	 * 池中可用consumer递增1
	 * @param sessionPool
	 */
	private void consumerIncreament(SessionPool sessionPool){
		if(sessionPool!=null){
			for(SessionPool sePool : pooledSession){
				if(sePool==sessionPool){
					int cnt = sePool.getAvailableConsumer();
					cnt++;
					sePool.setAvailableConsumer(cnt);
				}
			}
		}
	}
	
	/**
	 * 池中可用consumer递减1
	 * @param consumerPool
	 */
	public void consumerDecreament(ConsumerPool consumerPool){
		if(consumerPool!=null){
			for(SessionPool sessionPool : pooledSession){
				if(sessionPool.getConnection()==consumerPool.getConnection() 
						&& sessionPool.getSession()==consumerPool.getSession()){
					int cnt = sessionPool.getAvailableConsumer();
					cnt--;
					sessionPool.setAvailableConsumer(cnt);
				}
			}
		}
	}
	
	/**
	 * 释放所有连接
	 * @return
	 * @throws AMQFactoryException
	 */
	public boolean disposeAll() throws AMQFactoryException{
		try {
			if(pooledSession!=null && pooledSession.size()>0){
				for (SessionPool sessionPool : pooledSession) {
					sessionPool.getSession().close();
				}
				pooledSession.clear();
			}
			if(pooledConnection!=null && pooledConnection.size()>0){
				for(ConnectionPool connectionPool : pooledConnection){
					connectionPool.getConnection().stop();
					connectionPool.getConnection().close();
				}
				pooledConnection.clear();
			}
			return true;
		} catch (JMSException e) {
			throw new AMQFactoryException("释放连接出错",e);
		}
	}
	
	/**
	 * 释放producer连接
	 * @param producer
	 * @throws AMQFactoryException
	 */
	public void disposeProducer(MessageProducer producer) throws AMQFactoryException{
		if(producer!=null){
			try {
				producer.close();
			} catch (JMSException e) {
				throw new AMQFactoryException("释放producer连接出错",e);
			}
		}
	}
	
	/**
	 * 释放consumer连接
	 * @param consumer
	 * @throws AMQFactoryException
	 */
	public void disposeConsumer(MessageConsumer consumer) throws AMQFactoryException{
		if(consumer!=null){
			try {
				consumer.close();
			} catch (JMSException e) {
				throw new AMQFactoryException("释放consumer连接出错",e);
			}
		}
	}
	
}
