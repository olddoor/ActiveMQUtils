package com.funo.zyl.ActiveMqUtil;

/**
 * Hello world!
 *
 */
public class App 
{
	private static int cou=0;
	private String name;
	public static String pwd;
	static {
        for(int i=0;i<10;i++){
        	System.out.println( "Hello World!" );
        }
        cou=1;
        pwd="new world";
	}
    public static void main( String[] args )
    {
        for(int i=0;i<10;i++){
        	System.out.println( "Hello World!" );
        }
    }
    
	public int addCou() {
		cou++;
		return cou;
	}
	public void setCou() {
		cou++;
	}

	public void setCou(int cou) {
		this.cou = cou;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static String getPwd() {
		return pwd;
	}

	public static void setPwd(String pwd) {
		App.pwd = pwd;
	}

	public static int getCou() {
		return cou;
	}
    
}
