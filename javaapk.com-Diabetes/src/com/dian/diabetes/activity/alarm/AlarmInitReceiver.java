package com.dian.diabetes.activity.alarm;

import com.dian.diabetes.db.AlarmBo;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.ContantsUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 开机启动与时区改变监听
 * 
 * @author longbh
 * 
 */
public class AlarmInitReceiver extends BroadcastReceiver {

	/**
	 * 接受开机启动完成的广播， 设置闹钟，当时区改变也设置
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Preference preference = Preference.instance(context);
		AlarmBo bo = new AlarmBo(context);
		Long uid = preference.getLong(Preference.USER_ID);
		bo.setNextAlarm(String.valueOf(uid),ContantsUtil.curUser.getService_uid());
	}
}
