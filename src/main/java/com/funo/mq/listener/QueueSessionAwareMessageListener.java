package com.funo.mq.listener;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.listener.SessionAwareMessageListener;
/**
 * queue消息监听,消费v2.0
 * 支持回复消息(使用SessionAwareMessageListener替代MessageListener接口)
 * 暂不启用.代码为demo
 */
public class QueueSessionAwareMessageListener implements SessionAwareMessageListener<TextMessage>{
	
	/** 回复消息的目的地 */
    private Destination destination; //后续可考虑支持灵活替换
    /**
     * 我们定义了一个SessionAwareMessageListener，在这个Listener中我们在接收到了一个消息之后，
     * 利用对应的Session创建了一个到destination的生产者和对应的消息，然后利用创建好的生产者发送对应的消息。
     */
	@Override
	public void onMessage(TextMessage message, Session session) throws JMSException {
		System.out.println("监听消息内容：" + message.getText());
        MessageProducer messageProducer = session.createProducer(destination);
        TextMessage replyMessage = session.createTextMessage("已收到消息：" + message.getJMSMessageID());
        messageProducer.send(replyMessage);
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}
	
}
