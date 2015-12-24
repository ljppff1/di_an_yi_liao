package com.dian.diabetes;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.service.LoadingService;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.utils.NetworkUtil;
import com.zdp.aseo.content.AseoZdpAseo;

public class StartActivity extends FragmentActivity {

	private Preference preference;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_layout);
		context = this;
		preference = Preference.instance(this);
		AseoZdpAseo.initType(this, AseoZdpAseo.SCREEN_TYPE);
		loadSystemProperty();
	}

	private void loadSystemProperty() {
		if (NetworkUtil.checkConnection(this)) {
			startLoadData();
		} else {
			startHandler();
		}
	}

	private void startLoadData() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("timestamp", preference.getLong(Preference.SYS_UPDATE_TIME));
		HttpServiceUtil.request(ContantsUtil.PRED_UPDATE, "post", params,
				new HttpServiceUtil.CallBack() {
					@Override
					public void callback(String json) {
						LoadingService.sycnData(preference, context, json);
						ContantsUtil.isSycnSystem = true;
						if(!CheckUtil.isNull(ContantsUtil.DEFAULT_TEMP_UID)){
							Intent playAlarm = new Intent(ContantsUtil.SYCN_DATA);
							playAlarm.putExtra("state", false);
							context.startService(playAlarm);
						}
						toMainActivity();
					}
				});
	}

	private void startHandler() {
		Handler handle = new Handler();
		handle.postDelayed(new Runnable() {
			@Override
			public void run() {
				toMainActivity();
			}
		}, 2000);
	}

	private void toMainActivity() {
		if (!preference.getBoolean(Preference.FIRST_IN)) {
			Intent intent = new Intent();
			intent.setClass(context, WelcomeActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent();
			intent.setClass(context, MainActivity.class);
			startActivity(intent);
		}
		finish();
	}

	public void onBackPressed() {

	}
}
