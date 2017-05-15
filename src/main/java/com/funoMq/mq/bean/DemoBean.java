package com.funoMq.mq.bean;

import java.io.Serializable;
import java.util.Date;

public class DemoBean implements Serializable {

	private static final long serialVersionUID = -1499263479765337380L;

	private String name;
	private double score;
	private int age;
	private Date brithday;

	public DemoBean() {
	}

	public DemoBean(String name, double score, int age, Date brithday) {
		super();
		this.name = name;
		this.score = score;
		this.age = age;
		this.brithday = brithday;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getBrithday() {
		return brithday;
	}

	public void setBrithday(Date brithday) {
		this.brithday = brithday;
	}

	@Override
	public String toString() {
		return "DemoBean [name=" + name + ", score=" + score + ", age=" + age + ", brithday=" + brithday + "]";
	}

}
