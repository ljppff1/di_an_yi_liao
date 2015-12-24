package com.dian.diabetes.dto;

public class CheckModel {

	public String key;
	public String value;
	public boolean isCheck;

	public CheckModel(String key, String value, boolean isCheck) {
		this.key = key;
		this.value = value;
		this.isCheck = false;
	}
}
