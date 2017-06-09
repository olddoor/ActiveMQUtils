package com.AmqUtil.poolFactory.pool;


import javax.jms.Connection;
/**
 * Connection池对象
 * @author my
 * @version 1.0
 * @updated 08-七月-2016 16:00:22
 */
public class ConnectionPool {

	private Connection connection;
	private int activeSessions;

	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public int getActiveSessions() {
		return activeSessions;
	}
	public void setActiveSessions(int activeSessions) {
		this.activeSessions = activeSessions;
	}
}
