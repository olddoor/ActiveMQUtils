package com.funo.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:funo-jmsActiveMQ-*.xml")
public class SpringJmsTest {
	/**
	 * 测试生产者向queue1发送消息
	 */
	@Test
	public void testProduce() {
		for(int i=0;i<10;i++){
			ActiveMqUtils.getProducerService().sendMessage("zyl", "cmcc"+i);
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
