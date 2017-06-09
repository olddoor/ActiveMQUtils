package com.AmqUtil.util;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
/**
 * 加密解密工具类
 * @author duxianchao
 */
public class DESUtil {
	  private static final String strKey = "Middle and re assets";//字符串密钥写死，用于加密解密使用
	  private Key key;
	  private byte[] byteMi = null;
	  private byte[] byteMing = null;
	  private String strMi= "";
	  private String strM= ""; 
	  //  根据参数生成KEY   
	  private void setKey(String strKey){ 
	   try{  
	        KeyGenerator _generator = KeyGenerator.getInstance("DES");  
	        _generator.init(new SecureRandom(strKey.getBytes()));  
	        this.key = _generator.generateKey();  
	        _generator=null;
	    }catch(Exception e){
	    	e.printStackTrace();
	     }
	   
	    }  
	  //  加密String明文输入,String密文输出  
	  private void setEncString(String strMing){
	    BASE64Encoder base64en = new BASE64Encoder();  
	    try {
		      this.byteMing = strMing.getBytes("UTF8");  
		      this.byteMi = this.getEncCode(this.byteMing);  
		      this.strMi = base64en.encode(this.byteMi);
	     }catch(Exception e){
	    	 e.printStackTrace();
	     }finally{
	      this.byteMing = null;  
	      this.byteMi = null;
	      }
	  }  
	  // 解密:以String密文输入,String明文输出   
	  private void setDesString(String strMi){  
		  BASE64Decoder base64De = new BASE64Decoder();   
	      try{
		      this.byteMi = base64De.decodeBuffer(strMi);  
		      this.byteMing = this.getDesCode(byteMi);  
		      this.strM = new String(byteMing,"UTF8");  
	      }catch(Exception e){
	          e.printStackTrace();
	      }finally{
		      base64De = null;  
		      byteMing = null;  
		      byteMi = null;
	      }  
	  
	  }
	  //加密以byte[]明文输入,byte[]密文输出    
	  private byte[] getEncCode(byte[] byteS){
		   byte[] byteFina = null;  
		   Cipher cipher;  
	      try{
		      cipher = Cipher.getInstance("DES");  
		      cipher.init(Cipher.ENCRYPT_MODE,key);  
		      byteFina = cipher.doFinal(byteS);
	      }catch(Exception e){
	    	  e.printStackTrace();
	      }finally{
	    	  cipher = null;
	     }
	      return byteFina;
	  } 

	  // 解密以byte[]密文输入,以byte[]明文输出    
	 private byte[] getDesCode(byte[] byteD){
	    Cipher cipher;  
	    byte[] byteFina=null;  
	    try{
		      cipher = Cipher.getInstance("DES");  
		      cipher.init(Cipher.DECRYPT_MODE,key);  
		      byteFina = cipher.doFinal(byteD);
	    }catch(Exception e){
		   System.out.println("该密文不是DES加密，getDescCode(byte[] byteD)方法解密失败");
		   e.printStackTrace();
	    }finally{
	      cipher=null;
	      }  
	    return byteFina;
	  } 
	  //返回加密后的密文strMi  
	 private String getStrMi()
	  {
	   return strMi;
	  }
	  //返回解密后的明文
	 private String getStrM()
	  {
	   return strM;
	  }
	  
	  /**
	   * 加密方法
	   * @param strM 要加密的明文
	   * @return
	   */
	  public static String enCode(String strM){
		  DESUtil des = new DESUtil();
		  des.setKey(strKey);
		  des.setEncString(strM);//将要加密的明文传送给Encrypt.java进行加密计算。
		  return des.getStrMi();
	  }
	  
	  /**
	   * 解密方法
	   * @param strMi 要解密的密文
	   * @return
	   */
	  public static String deCode(String strMi){
		  DESUtil des = new DESUtil();
		  des.setKey(strKey);
		  des.setDesString(strMi);   //将要解密的密文
		  return des.getStrM();
	  }
	  
}
