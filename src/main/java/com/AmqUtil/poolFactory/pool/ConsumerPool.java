package com.AmqUtil.poolFactory.pool;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;
/**
 * consumer池对象
 * @author duxianchao
 * @version 1.0
 * @updated 08-七月-2016 16:00:22
 */
public class ConsumerPool {

	private Connection connection;
	private Session session;
	private MessageConsumer consumer;
	
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public MessageConsumer getConsumer() {
		return consumer;
	}
	public void setConsumer(MessageConsumer consumer) {
		this.consumer = consumer;
	}
	
	
}
