package com.dian.diabetes.activity.sugar.blueth;

import jiuan.androidBg.Comm.BGCommManager;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

public class BluthManager {

	private static BluthManager manager;

	private Context context;
	private BGCommManager bgCommManager;

	public static BluthManager getInstance(Context context) {
		if (manager == null) {
			manager = new BluthManager(context);
		}
		return manager;
	}

	public BluthManager(Context context) {
		this.context = context.getApplicationContext();
		bgCommManager = new BGCommManager(this.context);
	}

	public void connect(BluetoothDevice device){
		bgCommManager.ConnectBluetoothDevice(device);
	}
	
	public void stop(){
		bgCommManager.stop();
	}
}
