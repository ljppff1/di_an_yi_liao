package com.dian.diabetes.dto;

import com.dian.diabetes.db.dao.Eat;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;

public class EatDto {

	public String id;
	public String foodName;
	public String foodType;
	public String calorieType;
	public String nutriType;
	public float heat;
	public int stage;
	public String cooking;
	public float weight;
	public String note;
	public long createTime;
	public long updateTime;
	public float recommondHeat;
	public int status;
	public float support;

	public void of(Eat eat) {
		if (eat.getStatus() == ContantsUtil.DELETE) {
			this.id = eat.getServerid();
			this.status = 1;
		} else {
			this.foodName = eat.getFoodName();
			this.foodType = eat.getFoodType();
			this.calorieType = eat.getCaloreType();
			this.nutriType = eat.getNutriType();
			this.heat = eat.getTotal();
			this.cooking = eat.getCookType();
			this.weight = eat.getFoodWeight();
			this.note = eat.getMark();
			this.recommondHeat = eat.getSurport();
			this.createTime = eat.getCreate_time();
			this.updateTime = eat.getUpdate_time();
			this.stage = eat.getDinnerType();
			this.support = eat.getSurport();
			this.status = 0;
			if (!CheckUtil.isNull(eat.getServerid())) {
				this.id = eat.getServerid();
			}
		}
	}
}
