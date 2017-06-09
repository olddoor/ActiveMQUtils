package com.AmqUtil.common;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
/**
 * 消息转换处理监听类
 * @author duxianchao
 * @version 1.0
 * @updated 08-七月-2016 16:00:20
 */
public abstract class AbsListener implements MessageListener
{
	private Logger log = Logger.getLogger(AbsListener.class);
	public abstract void DealWithMsgMapValue(Map<String, Object> dicMap);
    public abstract void DealWithMsgTextValue(String text);
    @Override
    @SuppressWarnings("unchecked")
	public void onMessage(Message message){
    	 try {
 	        if (message instanceof MapMessage){
 	        	 MapMessage mapMsg = (MapMessage) message;
 	            Map<String, Object> dicMap = new HashMap<String, Object>();
 	            
 					for (Enumeration<String> em = mapMsg.getMapNames();em.hasMoreElements();)
 					{
 						String key = em.nextElement();
 					    dicMap.put(key, mapMsg.getObject(key));
 					}
 	            //Map消息监听接口
 	            this.DealWithMsgMapValue(dicMap);
 	            return;
 	        }else if (message instanceof TextMessage){
 	        	TextMessage textMsg = (TextMessage) message;
 	            String text;
 					text = textMsg.getText();
 	            //Text消息监听接口
 	            this.DealWithMsgTextValue(text);
 	            return;
 	        }
         }catch (JMSException e) {
        	 log.error("解析消息出错", e.getCause());
 		}
	}
}