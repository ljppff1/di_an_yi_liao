package com.dian.diabetes.dto;

import com.dian.diabetes.db.dao.Eat;
import com.dian.diabetes.db.dao.IndicateValue;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;

public class SycnIndicateDto {

	public String id;
	public String group;
	public String markNo;
	public String key;
	public float value;
	public int level;
	public float value1;
	public int level1;
	public long createTime;
	public long updateTime;
	public int status;

	public void of(IndicateValue value) {
		if (value.getStatus() == ContantsUtil.DELETE) {
			this.id = value.getServerid();
			this.status = 1;
		} else {
			this.id = value.getServerid();
			this.group = value.getGroup();
			this.markNo = value.getMarkNo();
			this.key = value.getKey();
			this.value = value.getValue();
			this.value1 = value.getValue1();
			this.level1 = value.getLevel1();
			this.level = value.getLevel();
			this.createTime = value.getCreate_time();
			this.updateTime = value.getUpdate_time();
			this.status = 0;
			if (!CheckUtil.isNull(value.getServerid())) {
				this.id = value.getServerid();
			}
		}
	}

}
