package com.dian.diabetes.service;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.dian.diabetes.R;
import com.dian.diabetes.db.IndicateBo;
import com.dian.diabetes.db.dao.Indicate;
import com.dian.diabetes.db.dao.IndicateValue;
import com.dian.diabetes.utils.ContantsUtil;

public class IndicateService {

	public IndicateValue convertJson(JSONObject data) throws JSONException{
		IndicateValue indicate = new IndicateValue();
		indicate.setValue((float) data.getDouble("value"));
		indicate.setLevel(data.getInt("level"));
		indicate.setKey(data.getString("key"));
		indicate.setGroup(data.getString("group"));
		indicate.setMarkNo(data.getString("markNo"));
		indicate.setCreate_time(data.getLong("createTime"));
		indicate.setUpdate_time(data.getLong("updateTime"));
		indicate.setService_mid(data.getString("mid"));
		indicate.setServerid(data.getString("id"));
		if(data.has("value1")){
			indicate.setValue1((float) data.getDouble("value1"));
			indicate.setLevel1(data.getInt("level1"));
		}
		return indicate;
	}
	
	public static void initIndicate(Context context,String mid){
		IndicateBo bo = new IndicateBo(context);
		Map<String, Indicate> maps = bo.getMapIndicate(mid);
		Indicate indicate = maps.get("weight");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("weight");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("体重");
			indicate.setUpdate_time(0);
			indicate.setUnion("kg");
			indicate.setUp_down(-1);
			indicate.setService_mid(mid);
			indicate.setImg(R.drawable.indicate_weight);
			bo.saveIndicate(indicate);
		}
		

		indicate = maps.get("waist");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("waist");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("腰围");
			indicate.setUpdate_time(0);
			indicate.setUnion("cm");
			indicate.setUp_down(-1);
			indicate.setService_mid(mid);
			indicate.setImg(R.drawable.indicate_yao);
			bo.saveIndicate(indicate);
		}

		indicate = maps.get("bmi");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("bmi");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("BMI");
			indicate.setUpdate_time(0);
			indicate.setUnion("kg/m^2");
			indicate.setUp_down(-1);
			indicate.setService_mid(mid);
			indicate.setImg(R.drawable.indicate_bmi);
			bo.saveIndicate(indicate);
		}

		indicate = maps.get("openPress");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("openPress");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("血压");
			indicate.setUpdate_time(0);
			indicate.setUnion("mmHg");
			indicate.setUp_down(-1);
			indicate.setService_mid(mid);
			indicate.setImg(R.drawable.indicate_xueya);
			bo.saveIndicate(indicate);
		}

		indicate = maps.get("ch");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("ch");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("血脂");
			indicate.setUpdate_time(0);
			indicate.setUnion("mmol/L");
			indicate.setUp_down(-1);
			indicate.setService_mid(mid);
			indicate.setImg(R.drawable.indicate_xuezhi);
			bo.saveIndicate(indicate);
		}
		
		// 心率
		indicate = maps.get("heart");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("heart");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("心率");
			indicate.setUpdate_time(0);
			indicate.setUnion("次/min");
			indicate.setUp_down(-1);
			indicate.setService_mid(mid);
			indicate.setImg(R.drawable.indicate_heart);
			bo.saveIndicate(indicate);
		}
		
		// 血红蛋白
		indicate = maps.get("protein");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("protein");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("糖化血红蛋白");
			indicate.setUpdate_time(0);
			indicate.setUnion("%");
			indicate.setUp_down(-1);
			indicate.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
			indicate.setImg(R.drawable.icon_danbai);
			bo.saveIndicate(indicate);
		}
		
		//血脂其他指标
		indicate = maps.get("tg");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("tg");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("甘油三脂");
			indicate.setUpdate_time(0);
			indicate.setUnion("mmol/L");
			indicate.setUp_down(-1);
			indicate.setService_mid(mid);
			bo.saveIndicate(indicate);
		}
		
		indicate = maps.get("hdl");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("hdl");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("高密度脂蛋白胆固醇");
			indicate.setUpdate_time(0);
			indicate.setUnion("mmol/L");
			indicate.setUp_down(-1);
			indicate.setService_mid(mid);
			bo.saveIndicate(indicate);
		}
		
		indicate = maps.get("ldl");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("ldl");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("低密度脂蛋白胆固醇");
			indicate.setUpdate_time(0);
			indicate.setUnion("mmol/L");
			indicate.setUp_down(-1);
			indicate.setService_mid(mid);
			bo.saveIndicate(indicate);
		}
	
	}
}
