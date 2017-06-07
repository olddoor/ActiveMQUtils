package com.funo.mq;

import java.util.Date;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.funo.framework.extension.edy.entity.DemoBean;
import com.funoMq.mq.ActiveMqUtils;
import com.funoMq.mq.util.SpringContextUtil;


//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath*:funo-ActiveMQ*.xml")
public class SpringJmsTest {
	/**
	 * 测试生产者向queue1发送消息
	 */
	@Test
	public void testProduce() {
		SpringContextUtil m=new SpringContextUtil();
//		m.say();
		
		for(int i=0;i<5;i++){
			DemoBean db=new DemoBean();
			db.setAge(i);
			db.setBrithday(new Date());
			db.setName("name"+i);
			db.setScore(i*20.1);
//			ActiveMqUtils.getProducerService().sendMessage("ppks", db);
//			ActiveMqUtils.getProducerService().sendMessage("nba", i);
			ActiveMqUtils.getProducerService().sendMessage("object-test",db);
//			ActiveMqUtils.getConsumerService().receive("queue1");
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
	

	
	
}
