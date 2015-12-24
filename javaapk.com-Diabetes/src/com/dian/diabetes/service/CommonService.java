package com.dian.diabetes.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dian.diabetes.db.dao.Common;
import com.dian.diabetes.db.dao.DefSet;
import com.dian.diabetes.utils.CheckUtil;

public class CommonService {

	public List<Common> convertCommon(String data,String group) throws JSONException {
		List<Common> listData = new ArrayList<Common>();
		if (!CheckUtil.isNull(data)) {
			JSONArray dataArray = new JSONArray(data);
			for (int i = 0; i < dataArray.length(); i++) {
				listData.add(convertModel(dataArray.getJSONObject(i),group));
			}
		}
		return listData;
	}
	
	public Common convertModel(JSONObject data,String group) throws JSONException{
		Common common = new Common();
		common.setGroup(group);
		common.setImage(data.getString("image"));
		common.setImage_b(data.getString("imageB"));
		common.setName(data.getString("name"));
		common.setServerid(data.getString("id"));
		return common;
	}

}
