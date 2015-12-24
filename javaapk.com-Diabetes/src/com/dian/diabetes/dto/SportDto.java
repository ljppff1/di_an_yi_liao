package com.dian.diabetes.dto;

import com.dian.diabetes.db.dao.Sport;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;

public class SportDto {
	
	public String id;
	public String issue;
	public String feeling;
	public String strength;
	public float heat;
	public int heartRate;
	public int during;
	public long createTime;
	public long updateTime;
	public String note;
	public int status;
	public float support;
	public String extField;
	
	public void of(Sport sport){
		if (sport.getStatus() == ContantsUtil.DELETE) {
			this.id = sport.getServerid();
			this.status = 1;
		} else {
			this.issue = sport.getSportName();
			this.feeling = sport.getSportFeel();
			this.strength = sport.getStrength();
			this.heat = sport.getTotal();
			this.during = sport.getSportTime();
			this.heartRate = sport.getHeart();
			this.createTime = sport.getCreate_time();
			this.updateTime = sport.getUpdate_time();
			this.note = sport.getMark();
			this.extField = sport.getSportUnit();
			this.support = sport.getSuport();
			this.status = 0;
			if (!CheckUtil.isNull(sport.getServerid())) {
				this.id = sport.getServerid();
			}
		}
	}

}
