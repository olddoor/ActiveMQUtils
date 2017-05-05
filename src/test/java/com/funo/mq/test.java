package com.funo.mq;

import com.funo.mq.util.SpringContextUtil;

public class test {
	private static boolean bb;
	public static void main(String[] args) {
//		 SpringContextUtil m=new SpringContextUtil();
//		 m.say();

		// 打印读取的配置文件.测试
		//
		// ClassPathXmlApplicationContext context = new
		// ClassPathXmlApplicationContext(
		// "classpath*:funo-*.xml");
		// ClassPathResource testfile02 = new
		// ClassPathResource("funo-jmsActiveMQ-context.xml");
		// if (testfile02 != null) {
		// try {
		// InputStream inputStream = testfile02.getInputStream();
		// Reader streamReader = new InputStreamReader(inputStream,
		// "UTF-8");
		// BufferedReader bufferedReader = new BufferedReader(streamReader);
		// String line;
		// while ((line = bufferedReader.readLine()) != null) {
		// System.out.println(line);
		// }
		// } catch (IOException exc) {
		// exc.printStackTrace();
		// }
		// }

		for (int i = 0; i < 10; i++) {
			ActiveMqUtils.getProducerService().sendMessage("zyl", "cmcc" + i);
		}
	}

}
