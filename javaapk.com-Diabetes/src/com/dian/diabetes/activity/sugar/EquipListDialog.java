package com.dian.diabetes.activity.sugar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import jiuan.androidBg.Bluetooth.BG5Control;
import jiuan.androidBg.Comm.BGCommManager;
import jiuan.androidBg.Observer.Interface_Observer_CoomMsg;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.BasicFragmentDialog;
import com.dian.diabetes.activity.sugar.adapter.DeviceAdapter;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 蓝牙设备列表界面
 * @author hua
 *
 */
public class EquipListDialog extends BasicFragmentDialog implements OnClickListener{

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.device)
	private ListView bluthList;

	private CallBack callBack;
	private BasicActivity activity;
	private DeviceAdapter adapter;
	private List<Map<String, String>> data;
	private BluetoothAdapter mAdapter;
	private BGCommManager connector;
	private boolean isConnecting;
	private Timer timer;
	private BroadcastReceiver BluetoothReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				final BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
					if (!isConnecting) {
						isConnecting = true;
						Log.d("data", device.getName());
						mAdapter.cancelDiscovery();
						connector.ConnectBluetoothDevice(device);
						isConnecting = false;
					}
				}
			}
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				initDeviceList();
				adapter.notifyDataSetChanged();
				break;
			case 2:

				break;
			case 3:

				break;
			case 4:

				break;
			}
		}
	};

	public static EquipListDialog getInstance() {
		return new EquipListDialog();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (BasicActivity) context;
		data = new ArrayList<Map<String, String>>();
		adapter = new DeviceAdapter(activity, data);
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		initBluthReceiver();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_bluetooth_list,
				container, false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		bluthList.setAdapter(adapter);
		bluthList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				if (callBack != null) {
					callBack.callBack(data.get(position).get("address"));
				}
				dismiss();
			}
		});
		connector.msgSubject.attach(new Interface_Observer_CoomMsg() {
			@Override
			public void msgHeadsetPullOut() {
				handler.sendEmptyMessage(3);
				Log.e("data", "msgHeadsetPullOut");
			}

			@Override
			public void msgHeadsetPluIn() {
				handler.sendEmptyMessage(2);
				Log.d("data", "msgHeadsetPullOut");
			}

			@Override
			public void msgBluetoothDeviceDisconnect(String arg0) {
				handler.sendEmptyMessage(4);
				Log.d("data", "msgBluetoothDeviceDisconnect" + arg0);
			}

			@Override
			public void msgBluetoothDeviceConnect(String arg0) {
				handler.sendEmptyMessage(1);
				Log.d("data", "msgBluetoothDeviceConnect" + arg0);
			}
		});
		mAdapter.startDiscovery();
		buletooth_timer();
	}

	public void buletooth_timer() {
		timer = new Timer();
		TimerTask btTask;
		btTask = new TimerTask() {
			public void run() {
				Log.e("start", "startDiscovery");
				mAdapter.startDiscovery();
			}
		};
		try {
			timer.schedule(btTask, 100, 5000);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private void initDeviceList() {
		Set<HashMap.Entry<String, BG5Control>> set = BGCommManager
				.getBluetoothControlMap().entrySet();
		for (Iterator<Map.Entry<String, BG5Control>> it = set.iterator(); it
				.hasNext();) {
			Map.Entry<String, BG5Control> entry = (Map.Entry<String, BG5Control>) it
					.next();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", "BG5");
			map.put("address", entry.getKey());
			data.add(map);
		}
	}

	private void initBluthReceiver() {
		// 注册广播接收信号
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
		intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED); // 每当扫描模式变化的时候，应用程序可以为通过ACTION_SCAN_MODE_CHANGED值来监听全局的消息通知。比如，当设备停止被搜寻以后，该消息可以被系统通知給应用程序。
		intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); // 每当蓝牙模块被打开或者关闭，应用程序可以为通过ACTION_STATE_CHANGED值来监听全局的消息通知。
		activity.registerReceiver(BluetoothReceiver, intent);
		if(!mAdapter.isEnabled()){
			mAdapter.enable();
		}
	}

	public void dismiss() {
		activity.unregisterReceiver(BluetoothReceiver);
		timer.cancel();		
		super.dismiss();
	}

	public interface CallBack {
		void callBack(String device);
	}

	public void setCallBack(CallBack callBack) {
		this.callBack = callBack;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.back_btn:
			dismiss();
			break;
		}
	}
	
	public void setConnect(BGCommManager connector){
		this.connector = connector;
	}
}
