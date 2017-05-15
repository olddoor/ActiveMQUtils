package com.funo.mq.demo.AMQinAction.ch4.portfolio;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;


public class RetroactiveConsumer {
    
    public void createRetroactiveConsumer() throws JMSException  {
        
        ConnectionFactory fac = new ActiveMQConnectionFactory();        
        Connection connection = fac.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic("TEST.TOPIC?consumer.retroactive=true");
        MessageConsumer consumer = session.createConsumer(topic);
    }
}
