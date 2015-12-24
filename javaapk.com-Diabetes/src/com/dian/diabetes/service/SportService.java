package com.dian.diabetes.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dian.diabetes.db.dao.Normal;
import com.dian.diabetes.db.dao.Sport;
import com.dian.diabetes.dto.GroupDto;
import com.dian.diabetes.dto.NormalDto;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;

public class SportService {

	public Sport convertJson(JSONObject data) throws JSONException {
		Sport sport = new Sport();
		sport.setCreate_time(data.getLong("createTime"));
		sport.setSportTime(data.getInt("during"));
		sport.setUpdate_time(data.getLong("updateTime"));
		sport.setDay(DateUtil.parseToDate(sport.getCreate_time()));
		sport.setHeart(data.getInt("heartRate"));
		sport.setTotal(data.getInt("heat"));
		sport.setSportFeel(data.getString("feeling"));
		sport.setSportName(data.getString("issue"));
		sport.setServerid(data.getString("id"));
		sport.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
		sport.setStrength(data.getString("strength"));
		sport.setStatus(ContantsUtil.SERVER);
		sport.setSportUnit(data.getString("extField"));
		sport.setSuport(data.getInt("support"));
		return sport;
	}

	public void convertGroupArray(List<GroupDto> lists,List<GroupDto> curs,JSONArray data,long parent) throws JSONException{
		for(int i = 0;i<data.length();i++){
			JSONObject item = data.getJSONObject(i);
			GroupDto dto = new GroupDto();
			dto.of(item);
			if(dto.parentId == parent){
				curs.add(dto);
			}
			lists.add(dto);
		}
	}
	
	public void convertArray(List<Normal> lists,JSONArray data,int type) throws JSONException{
		for(int i = 0;i<data.length();i++){
			JSONObject item = data.getJSONObject(i);
			Normal dto = new Normal();
			dto.setIssueId(item.getLong("id"));
			dto.setName(item.getString("name"));
			dto.setNum(item.getInt("idx"));
			dto.setParent(type);
			lists.add(dto);
		}
	}
	
	public void convertMedicine(List<Normal> lists,JSONArray data,int type,String group) throws JSONException{
		for(int i = 0;i<data.length();i++){
			JSONObject item = data.getJSONObject(i);
			Normal dto = new Normal();
			dto.setIssueId(item.getLong("id"));
			dto.setName(item.getString("name"));
			dto.setNum(item.getInt("idx"));
			dto.setDoes(item.getString("dose"));
			dto.setUnit(item.getString("unit"));
			dto.setTimes(item.getString("times"));
			dto.setCategory(group);
			dto.setParent(type);
			lists.add(dto);
		}
	}
	
	public void convertMedicineSearch(List<Normal> lists,JSONArray data,int type) throws JSONException{
		for(int i = 0;i<data.length();i++){
			JSONObject item = data.getJSONObject(i);
			Normal dto = new Normal();
			dto.setIssueId(item.getLong("id"));
			dto.setName(item.getString("name"));
			dto.setNum(item.getInt("idx"));
			dto.setDoes(item.getString("dose"));
			dto.setUnit(item.getString("unit"));
			dto.setTimes(item.getString("times"));
			dto.setCategory(item.getString("category"));
			dto.setParent(type);
			lists.add(dto);
		}
	}
}
