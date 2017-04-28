package com.funo.mq;

import javax.jms.Destination;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-*.xml")
public class SpringJmsTest2 {
	/**
	 * 队列名queue1
	 */
	@Autowired
	private Destination queueDestination;
	@Autowired
	private ActiveMqUtils mq;
	
	  /**
	   * 队列消息生产者
	   */
//	@Autowired
//	private ProducerService producer;
	
	/**
	 * 测试生产者向queue1发送消息
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testProduce() {
//		 ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
//	     "classpath*:spring-*.xml");
//		 ClassPathResource testfile02 = new ClassPathResource("spring-context.xml");
//		 if (testfile02 != null) {
//	            try {
//	                InputStream inputStream = testfile02.getInputStream();
//	                Reader streamReader = new InputStreamReader(inputStream,
//	                        "UTF-8");
//	                BufferedReader bufferedReader = new BufferedReader(streamReader);
//	                String line;
//	                while ((line = bufferedReader.readLine()) != null) {
//	                    System.out.println(line);
//	                }
//	            } catch (IOException exc) {
//	                exc.printStackTrace();
//	            }
//	        }
		String msg = "Hello world!";
//		producer.sendMessage(queueDestination, "Hello China~~~~~~~~~~~~~~~");
		mq.producer.sendMessage(queueDestination, msg);
	}

	public Destination getQueueDestination() {
		return queueDestination;
	}

	public void setQueueDestination(Destination queueDestination) {
		this.queueDestination = queueDestination;
	}

	public ActiveMqUtils getMq() {
		return mq;
	}

	public void setMq(ActiveMqUtils mq) {
		this.mq = mq;
	}

	
	
}
