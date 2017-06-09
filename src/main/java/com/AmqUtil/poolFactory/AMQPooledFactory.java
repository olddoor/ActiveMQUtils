package com.AmqUtil.poolFactory;

import java.util.Properties;

import javax.jms.JMSException;

import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.jms.pool.PooledConnection;
import org.apache.activemq.jms.pool.PooledSession;
import org.apache.activemq.pool.PooledConnectionFactory;

import com.AmqUtil.util.DESUtil;

/**
 * AMQ本身自带的连接池用法
 * @author duxianchao
 * @since activemq-all-5.13.2(需要JDK1.7及以上) 暂未使用
 */
@SuppressWarnings("unused")
public class AMQPooledFactory {
	private String URL="tcp://127.0.0.1:61616";
	private String userName="admin";
	private String password="admin";
	private int maxConnections=5;
	private int maxSessionPerConnection = 10;
    private boolean reconnectOnException = true;
	
	private PooledConnectionFactory pcFactory;
	
	private void loadConfig(Properties ppt){
		this.URL = ppt.getProperty("URL");
		this.userName = ppt.getProperty("userName");
		this.password = DESUtil.deCode(ppt.getProperty("password"));
		this.maxConnections = Integer.parseInt(ppt.getProperty("maxConnections"));
		this.maxSessionPerConnection = Integer.parseInt(ppt.getProperty("maxSessionPerConnection"));
		this.reconnectOnException = Boolean.parseBoolean(ppt.getProperty("reconnectOnException"));
	}
	
	public boolean init(Properties ppt){
		loadConfig(ppt);
		pcFactory = new PooledConnectionFactory();
		pcFactory.setProperties(ppt);
		return true;
	}
	
	private PooledConnection getPooledConnection(){
		PooledConnection pooledConnection = null;
		try {
			 pooledConnection =  (PooledConnection) pcFactory.createConnection(userName, password);
		} catch (JMSException e) {
			return null;
		}
		return pooledConnection;
	}
	
	public PooledSession getPooledSession(){
		PooledSession pooledSession = null;
		try {
			pooledSession = (PooledSession) getPooledConnection().createSession(false, ActiveMQSession.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			return null;
		}
		return pooledSession;
	}
	
	
}
