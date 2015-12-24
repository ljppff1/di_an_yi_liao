package com.dian.diabetes.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dian.diabetes.db.dao.UserSet;
import com.dian.diabetes.utils.CheckUtil;

public class PropertyService {

	public List<UserSet> convertCommon(String data,String group) throws JSONException {
		List<UserSet> listData = new ArrayList<UserSet>();
		if (!CheckUtil.isNull(data)) {
			JSONArray dataArray = new JSONArray(data);
			for (int i = 0; i < dataArray.length(); i++) {
				listData.add(convertModel(dataArray.getJSONObject(i),group));
			}
		}
		return listData;
	}
	
	public UserSet convertModel(JSONObject data,String group) throws JSONException{
		UserSet set = new UserSet();
		set.setKey(data.getString(""));
		set.setServerid(data.getString("id"));
		return set;
	}
}
