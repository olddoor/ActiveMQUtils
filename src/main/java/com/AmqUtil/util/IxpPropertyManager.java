package com.AmqUtil.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author duxianchao
 * @version 1.0
 * @created 08-七月-2016 16:01:41
 */
public class IxpPropertyManager {

	private static Logger log =  Logger.getLogger(IxpPropertyManager.class);
	
	private static volatile IxpPropertyManager propertyManager = null;

	private IxpPropertyManager(){}

	public static IxpPropertyManager getInstance(){
		if(propertyManager==null){
			synchronized(IxpPropertyManager.class){
				if(propertyManager==null){
					propertyManager = new IxpPropertyManager();
				}
			}
		}
		return propertyManager;
	}

	/**
	 * 给据文件全路径获取该文件
	 * @param filePath
	 * @return
	 */
	public Properties getProperty(String filePath){
		return loadProperty(filePath);
	}

	private Properties loadProperty(String filePath){
		if (null == filePath || "".equals(filePath)){
			log.error("文件全路径不能为空");
		}
		BufferedReader reader = null;
		Properties ppt = new Properties();
		try {
			reader = new BufferedReader(new FileReader(new File(filePath)));
			ppt.load(reader);
		} catch (FileNotFoundException e) {
			log.error("文件["+filePath+"]不存在", e.getCause());
		} catch (IOException e) {
			log.error("文件["+filePath+"]加载出错", e.getCause());
		} finally {
			try {
				if (reader != null){
					reader.close();
				}
			} catch (IOException e) {
				log.error("关闭BufferReader出错", e.getCause());
			}
		}
		return ppt;
	}
	
	public Properties loadFileProperty(String fileName){
		if (null == fileName || "".equals(fileName)){
			log.error("文件名不能为空");
		}
		InputStream inputStream = null;
		Properties ppt = new Properties();
		try {
			inputStream = IxpPropertyManager.class.getClassLoader().getResourceAsStream(fileName);
			ppt.load(inputStream);
		} catch (FileNotFoundException e) {
			log.error("文件["+fileName+"]不存在", e.getCause());
		} catch (IOException e) {
			log.error("文件["+fileName+"]加载出错", e.getCause());
		} finally {
			try {
				if (inputStream != null){
					inputStream.close();
				}
			} catch (IOException e) {
				log.error("关闭输入流出错", e.getCause());
			}
		}
		return ppt;
	}
	
	/**
	 * 根据指定配置文件获取class
	 * jarFilePath class文件全路径
	 * classPath class文件所在包的全路径
	 * @param ppt
	 * @return
	 */
	public Class<?> loadClass(Properties ppt){
		Class<?> cls = null;
		try {
			URL url = new URL(ppt.getProperty("jarFilePath"));
			URLClassLoader classLoader = new URLClassLoader(new URL[]{url});
			cls = classLoader.loadClass(ppt.getProperty("classPath"));
		} catch (MalformedURLException e) {
			log.error("获取jar包URL错误",e.getCause());
		} catch (ClassNotFoundException e) {
			log.error("获取jar包中的类错误，请检查包路径配置是否正确",e.getCause());
		}
		return cls;
	}
	
	/**
	 * 加载class文件
	 * @param jarFilePath class文件全路径
	 * @param classPath class文件所在包的全路径
	 * @return
	 */
	public Class<?> loadClass(String jarFilePath,String classPath){
		Class<?> cls = null;
		try {
			URL url = new URL(jarFilePath);
			URLClassLoader classLoader = new URLClassLoader(new URL[]{url});
			cls = classLoader.loadClass(classPath);
		} catch (MalformedURLException e) {
			log.error("获取jar包URL错误",e.getCause());
		} catch (ClassNotFoundException e) {
			log.error("获取jar包中的类错误，请检查包路径配置是否正确",e.getCause());
		}
		return cls;
	}

}