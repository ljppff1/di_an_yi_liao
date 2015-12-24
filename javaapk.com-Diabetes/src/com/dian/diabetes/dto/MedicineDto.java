package com.dian.diabetes.dto;

import com.dian.diabetes.db.dao.Medicine;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;

public class MedicineDto {

	public String id;
	public String type;
	public String mdc;
	public String times;
	public int dinOrder;
	public int duringDays;
	public long createTime;
	public long updateTime;
	public boolean remindOn;
	public String rmdBreakfast;
	public String rmdLunch;
	public String rmdsupper;
	public String rmdSleep;
	public String dose;
	public String unit;
	public int status;
	
	public void of(Medicine medicine){
		if(medicine.getStatus() == ContantsUtil.DELETE){
			this.id = medicine.getServerid();
			this.status = 1;
		}else{
			this.type = medicine.getType();
			this.mdc = medicine.getName();
			this.times = medicine.getNumType()+"";
			this.dinOrder = medicine.getStage();
			this.duringDays = medicine.getEatDay();
			this.createTime = medicine.getCreateTime();
			this.updateTime = medicine.getUpdateTime();
			this.remindOn = medicine.getIstoast();
			this.rmdBreakfast = medicine.getRmdBreakfast();
			this.rmdLunch = medicine.getRmdLunch();
			this.rmdsupper = medicine.getRmdsupper();
			this.rmdSleep = medicine.getRmdSleep();
			unit = medicine.getUnit();	
			this.status = 0;
			this.dose = medicine.getWeight();
			if (!CheckUtil.isNull(medicine.getServerid())) {
				this.id = medicine.getServerid();
			}
		}
	}
}
