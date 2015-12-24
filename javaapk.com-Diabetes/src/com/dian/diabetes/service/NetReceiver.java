package com.dian.diabetes.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.NetworkUtil;

/**
 * 监听网络变化用于同步数据
 * 
 * @author longbh
 * 
 */
public class NetReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)
				&& NetworkUtil.checkConnection(context)) {
			Preference preference = Preference.instance(context);
			if (preference.getBoolean(Preference.HAS_UPDATE) && Config.canUpdate()) {
				Intent playAlarm = new Intent(ContantsUtil.SYCN_DATA);
				playAlarm.putExtra("state", true);
				context.startService(playAlarm);
			}
		}
	}
}
