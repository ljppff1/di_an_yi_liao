package com.dian.diabetes.dto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dian.diabetes.utils.DateUtil;

public class InfoDto {

	public String patient_name;
	public String sex;
	public String age;
	public String dia_doctor;
	public String seeinfo;
	public String rap_idea;
	public String date;
	public String matrixCode;
	public String ageunit;

	public List<IndicateDto> datas = new ArrayList<IndicateDto>();

	public void of(JSONObject data) throws JSONException {
		datas.clear();
		patient_name = data.getString("patient_name");
		sex = data.getString("sex");
		age = data.getString("age");
		dia_doctor = data.getString("sample_from");
		seeinfo = data.getString("seeinfo");
		rap_idea = data.getString("rap_idea");
		date = DateUtil.parseToDate(data.getLong("report_date2"));
		matrixCode = data.getString("bar_code");
	}

	public void ofDes(JSONObject data) throws JSONException {
		matrixCode = data.getString("bar_code");
		patient_name = data.getString("patient_name");
		age = data.getString("age");
		sex = data.getString("sex");
		ageunit = data.getString("ageunit");
		date = DateUtil.parseToDate(data.getLong("appr_date2"));
		dia_doctor = data.getString("sample_from");
	}

	public void ofIndicate(JSONArray indicates) throws JSONException {
		datas.clear();
		for (int i = 0; i < indicates.length(); i++) {
			IndicateDto item = new IndicateDto();
			item.of(indicates.getJSONObject(i));
			if("血糖蛋白".equals(item.item_name)){
				datas.add(0, item);
			}else{
				datas.add(item);
			}
		}
	}
	
	public void clear(){
		datas.clear();
	}
}
