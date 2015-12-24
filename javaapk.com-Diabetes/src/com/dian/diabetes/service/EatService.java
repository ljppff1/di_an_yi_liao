package com.dian.diabetes.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.dian.diabetes.db.dao.Eat;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;

public class EatService {
	
	public Eat convertJson(JSONObject data) throws JSONException{
		Eat eat = new Eat();
		eat.setFoodName(data.getString("foodName"));
		eat.setMark(data.getString("note"));
		eat.setFoodType(data.getString("foodType"));
		eat.setCookType(data.getString("cooking"));
		eat.setCaloreType(data.getString("calorieType"));
		eat.setNutriType(data.getString("nutriType"));
		eat.setDinnerType(data.getInt("stage"));
		eat.setCreate_time(data.getLong("createTime"));
		eat.setTotal((float) data.getDouble("heat"));
		eat.setUpdate_time(data.getLong("updateTime"));
		eat.setSurport((float) data.getDouble("support"));
		eat.setFoodWeight((float) data.getDouble("weight"));
		eat.setServerid(data.getString("id"));
		eat.setDay(DateUtil.parseToDate(eat.getCreate_time()));
		eat.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
		eat.setStatus(ContantsUtil.SERVER);
		return eat;
	}

}
