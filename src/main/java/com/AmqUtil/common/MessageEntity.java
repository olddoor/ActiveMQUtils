package com.AmqUtil.common;

import java.util.Map;
/**
 * 消息类
 * @author duxianchao
 * @version 1.0
 * @updated 08-七月-2016 16:00:25
 */
public class MessageEntity {

	private Map<String,Object> headMap;
	private Map<String,Object> bodyMap;
	public Map<String, Object> getHeadMap() {
		return headMap;
	}
	public void setHeadMap(Map<String, Object> headMap) {
		this.headMap = headMap;
	}
	public Map<String, Object> getBodyMap() {
		return bodyMap;
	}
	public void setBodyMap(Map<String, Object> bodyMap) {
		this.bodyMap = bodyMap;
	}
	
}
