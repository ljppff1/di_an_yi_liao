package com.dian.diabetes.service;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dian.diabetes.db.dao.Alarm;
import com.dian.diabetes.db.dao.Medicine;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.StringUtil;

public class MedicineService {

	public Medicine convertJson(List<Alarm> alarms,JSONObject data) throws JSONException{
		Medicine medicine = new Medicine();
		medicine.setName(data.getString("mdc"));
		medicine.setType(data.getString("type"));
		medicine.setCreateTime(data.getLong("createTime"));
		medicine.setIsOut(false);
		medicine.setIstoast(data.getBoolean("remindOn"));
		medicine.setEatDay(data.getInt("duringDays"));
		if(data.has("dose")){
			medicine.setWeight(data.getString("dose"));
		}
		if(data.has("unit")){
			medicine.setUnit(data.getString("unit"));
		}else{
			medicine.setUnit("mg");
		}
		medicine.setStage(data.getInt("dinOrder"));
		medicine.setServerid(data.getString("id"));
		medicine.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
		medicine.setNumType(data.getInt("times"));
		if(data.has("rmdBreakfast")){
			medicine.setRmdBreakfast(data.getString("rmdBreakfast"));
			int alarmType = StringUtil.toInt("" + 0 + medicine.getStage());
			alarms.add(getAlarm(alarmType,medicine.getRmdBreakfast(),medicine.getIstoast(),medicine.getEatDay(),medicine.getCreateTime()));
		}
		if(data.has("rmdLunch")){
			medicine.setRmdLunch(data.getString("rmdLunch"));
			int alarmType = StringUtil.toInt("" + 1 + medicine.getStage());
			alarms.add(getAlarm(alarmType,medicine.getRmdLunch(),medicine.getIstoast(),medicine.getEatDay(),medicine.getCreateTime()));
		}
		if(data.has("rmdsupper")){
			medicine.setRmdsupper(data.getString("rmdsupper"));
			int alarmType = StringUtil.toInt("" + 2 + medicine.getStage());
			alarms.add(getAlarm(alarmType,medicine.getRmdsupper(),medicine.getIstoast(),medicine.getEatDay(),medicine.getCreateTime()));
		}
		if(data.has("rmdSleep")){
			medicine.setRmdsupper(data.getString("rmdSleep"));
			int alarmType = StringUtil.toInt("" + 2 + medicine.getStage());
			alarms.add(getAlarm(alarmType,medicine.getRmdsupper(),medicine.getIstoast(),medicine.getEatDay(),medicine.getCreateTime()));
		}
		return medicine;
	}
	
	private Alarm getAlarm(int alarmType,String time,boolean isCheck,int eatDay,long createTime){
		Alarm alarm = new Alarm();
		String timeArray[] = time.split(":");
		alarm.setHour(StringUtil.toShort(timeArray[0]));
		alarm.setMinite(StringUtil.toShort(timeArray[1]));
		alarm.setCreateTime(createTime);
		alarm.setMessage("您设定的服药时间到了");
		alarm.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
		alarm.setUid(ContantsUtil.curUser.getService_uid());
		alarm.setSub_type((short) alarmType);
		alarm.setType((short) ContantsUtil.MEDICINE_ALARM);
		alarm.setTitle("迪安血糖提醒您");
		alarm.setEnable(isCheck);
		alarm.setRepeat(eatDay);
		return alarm;
	}
	
}
