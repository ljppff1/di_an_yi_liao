package com.dian.diabetes.dto;

import java.util.ArrayList;
import java.util.List;

public class ListDto {

	public Long time;
	public List<String> datas = new ArrayList<String>();

	public void addData(String temp) {
		datas.add(temp);
	}

}
