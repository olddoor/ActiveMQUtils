package com.funo.mq;


import javax.jms.Destination;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.funo.mq.util.MyApplicationContextUtil;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-*.xml")
public class SpringJmsTest {
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
		for(int i=0;i<10;i++){
			String msg = "id"+i;
			mq.producer.sendMessage(queueDestination, msg);
		}

		
		/**
		  // 打印读取的配置文件.测试
		 
		 ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
			     "classpath*:spring-*.xml");
				 ClassPathResource testfile02 = new ClassPathResource("spring-context.xml");
				 if (testfile02 != null) {
			            try {
			                InputStream inputStream = testfile02.getInputStream();
			                Reader streamReader = new InputStreamReader(inputStream,
			                        "UTF-8");
			                BufferedReader bufferedReader = new BufferedReader(streamReader);
			                String line;
			                while ((line = bufferedReader.readLine()) != null) {
			                    System.out.println(line);
			                }
			            } catch (IOException exc) {
			                exc.printStackTrace();
			            }
			        }
		*/
	}

	public Destination getQueueDestination() {
		return queueDestination;
	}

	public void setQueueDestination(Destination queueDestination) {
		this.queueDestination = queueDestination;
	}


	
	
}
