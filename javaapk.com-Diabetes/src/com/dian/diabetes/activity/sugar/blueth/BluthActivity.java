package com.dian.diabetes.activity.sugar.blueth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.widget.anotation.ViewInject;

public class BluthActivity extends BasicActivity {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.device)
	private ListView bluthList;

	private BluetoothAdapter mAdapter;
	private BluthManager connector;
	private BroadcastReceiver BluetoothReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action
					.equals(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
				
			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				final BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
					
				} else {
				}
			}
			if (action.equals(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_list);
		initActivity();
		initBluthReceiver();
	}

	private void initActivity() {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		connector = BluthManager.getInstance(context);
	}

	private void initBluthReceiver() {
		// 注册广播接收信号
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
		intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED); // 每当扫描模式变化的时候，应用程序可以为通过ACTION_SCAN_MODE_CHANGED值来监听全局的消息通知。比如，当设备停止被搜寻以后，该消息可以被系统通知給应用程序。
		intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); // 每当蓝牙模块被打开或者关闭，应用程序可以为通过ACTION_STATE_CHANGED值来监听全局的消息通知。
		registerReceiver(BluetoothReceiver, intent);
	}

}
