package com.dian.diabetes.dto;

import org.json.JSONException;
import org.json.JSONObject;

import com.dian.diabetes.utils.StringUtil;

public class EatPlanDto {
	
	public int first;
	public int index;
	public String parent;
	public String category;
	public String code;
	public String name;
	public float num;
	public String unit;
	
	public void of(JSONObject data,int first,String name,int index) throws JSONException{
		this.first = first;
		this.parent = name;
		this.index = index;
		category = data.getString("category_name");
		code = data.getString("code");
		name = data.getString("name");
		unit = data.getString("unit");
		num = StringUtil.toFloat(data.get("num"));
	}

}
