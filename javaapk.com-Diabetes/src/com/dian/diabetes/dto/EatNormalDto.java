package com.dian.diabetes.dto;

import org.json.JSONException;
import org.json.JSONObject;

import com.dian.diabetes.utils.StringUtil;

public class EatNormalDto {

	public long foodId;
	public String heatLevel;
	public float heat;
	public String cookingName;
	public long cookId;
	public String category;

	public void of(JSONObject data) throws JSONException {
		this.foodId = data.getLong("foodId");
		this.heat = StringUtil.toFloat(data.get("heat"));
		this.heatLevel = data.getString("heatLevel");
		JSONObject cooking = data.getJSONObject("cooking");
		this.cookingName = cooking.getString("name");
		this.cookId = cooking.getLong("id");
		this.category = data.getString("category");
	}
	
}
