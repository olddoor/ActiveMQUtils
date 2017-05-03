package com.funo.mq.service;

import java.util.List;

import javax.jms.Destination;

import org.springframework.stereotype.Service;

/**
 * 暂时只提供发送String类型的数据.
 * jms api支持更多的数据类型可在后续版本中拓展: 
 *  |---- BytesMessage </br>
    |---- MapMessage </br>
    |---- ObjectMessage </br>
    |---- StreamMessage </br>
    |---- TextMessage </br>
    {eusername: 11, pwwdd}
 *
 * @author olddoor
 */
public interface ProducerService {

	  /**
	   * 发消息，向默认的 destination
	   * @param msg String 消息内容
	   */
	  public void sendMessage(String msg);

	  /**
	   * 发消息，向指定的 destination
	   * 
	   * @param destination 目的地
	   * @param msg String 消息内容
	   */
	  public void sendMessage(Destination destination, String msg);

	  /**
	   * 发消息，向指定的 destination
	   * 
	   * @param destination 目的地
	   * @param msg String 消息内容
	   */

	  /**
	   * 向指定的destination发送消息，消费者接受消息后，把回复的消息写到response队列
	   * 
	   * @param destination 目的地
	   * @param msg String 消息内容
	   * @param response 回复消息的队列
	   */
	  public void sendMessage(Destination destination, String msg, Destination response);
	  
	  /**
	   * 向指定的destination发送消息
	   */
	  public void sendMessage(String destinationName, final String msg);
	  
	  /**
	   * 向指定的多个destination发送消息
	   */
	  public void sendMessage(List<String> destinationName, final String msg,String strategy);
}
