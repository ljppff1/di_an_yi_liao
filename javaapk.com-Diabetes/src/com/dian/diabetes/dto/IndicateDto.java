package com.dian.diabetes.dto;

import org.json.JSONException;
import org.json.JSONObject;

import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.StringUtil;

public class IndicateDto {

	public String bar_code;
	public float result;
	public String item_name;
	public String units;
	public float min;
	public float max;
	public long date;
	public Float lastValue;
	public int level;

	public String stringValue;
	public String surport;
	public boolean isTxt;
	public boolean hasChart;
	
	public String sense;
	public String valueLow;
	public String valueHigh;

	public void of(JSONObject data) throws JSONException {
		bar_code = data.getString("bar_code");
		item_name = data.getString("item_name");
		if(data.has("sense")){
			sense = data.getString("sense");
		}
		if(data.has("valueLow")){
			valueLow = data.getString("valueLow");
		}
		if(data.has("valueHigh")){
			valueHigh = data.getString("valueHigh");
		}
		if (data.has("units")) {
			units = data.getString("units");
		}
		if (!data.has("rn10")) {
			level = 0;
			isTxt = true;
		} else {
			String rn10 = data.getString("rn10");
			if ("H".equals(rn10)) {
				level = 2;
			} else if ("L".equals(rn10)) {
				level = 1;
			} else {
				level = 0;
			}
			isTxt = false;
		}
		try {
			stringValue = data.getString("result");
			result = StringUtil.toFloat(data.getString("result"));
			String displowhigh = data.getString("displowhigh");
			if (!CheckUtil.isNull(displowhigh)) {
				String[] sub = displowhigh.split("-");
				if (sub.length > 1) {
//					sub = displowhigh.split(">");
//					if (sub.length < 2) {
//						sub = displowhigh.split("<");
//						if (sub.length < 2) {
//							min = 0;
//							max = 0;
//						} else {
//							min = Integer.MIN_VALUE;
//							max = StringUtil.toFloat(sub[1]);
//						}
//					} else {
//						min = StringUtil.toFloat(sub[0]);
//						max = Integer.MAX_VALUE;
//					}
//				} else {
					if (!CheckUtil.isNull(sub[0])) {
						min = StringUtil.toFloat(sub[0]);
					} else {
						min = Integer.MIN_VALUE;
					}
					if (sub.length > 1 && !CheckUtil.isNull(sub[1])) {
						max = StringUtil.toFloat(sub[1]);
					} else {
						max = Integer.MAX_VALUE;
					}
				}
				if (data.has("last")) {
					lastValue = StringUtil.toFloat(data.get("last"));
				}
				hasChart = true;
			}else{
				stringValue = data.getString("result");
				surport = data.getString("displowhigh");
				hasChart = false;
			}
		} catch (NumberFormatException e) {
			stringValue = data.getString("result");
			surport = data.getString("displowhigh");
			hasChart = false;
		}
		// this.max = Math.max(max, result);
		if(data.has("appr_date2")){
			this.date = data.getLong("appr_date2");
		}
	}

	public void ofMain(JSONObject data) throws JSONException {
		bar_code = data.getString("bar_code");
		item_name = data.getString("item_name");
		units = data.getString("units");
		if (!data.has("rn10")) {
			level = 0;
		} else {
			String rn10 = data.getString("rn10");
			if ("H".equals(rn10)) {
				level = 2;
			} else if ("L".equals(rn10)) {
				level = 1;
			} else {
				level = 0;
			}
		}
		try {
			stringValue = data.getString("result");
			result = StringUtil.toFloat(stringValue);
			String[] sub = data.getString("displowhigh").split("-");
			try {
				if (!CheckUtil.isNull(sub[0])) {
					min = StringUtil.toFloat(sub[0]);
				} else {
					min = Integer.MIN_VALUE;
				}
				if (sub.length > 1 && !CheckUtil.isNull(sub[1])) {
					max = StringUtil.toFloat(sub[1]);
				} else {
					max = Integer.MAX_VALUE;
				}
			} catch (NumberFormatException e) {
				min = 0;
				max = 0;
			}
			if (data.has("last_value")) {
				lastValue = StringUtil.toFloat(data.get("last_value"));
			}
			isTxt = false;
		} catch (NumberFormatException e) {
			stringValue = data.getString("result");
			surport = data.getString("displowhigh");
			isTxt = true;
		}
		// this.max = Math.max(max, result);
		if(data.has("appr_date2")){
			this.date = data.getLong("appr_date2");
		}
	}
}
