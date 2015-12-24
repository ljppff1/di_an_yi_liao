package com.dian.diabetes.activity.indicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import jiuan.androidnin1.bluetooth.BP.BluetoothComm;
import jiuan.androidnin1.bluetooth.BP.BluetoothControlForBP;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicFragmentDialog;
import com.dian.diabetes.activity.sugar.adapter.DeviceAdapter;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

public class EquipListDialog extends BasicFragmentDialog implements OnClickListener{

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.device)
	private ListView bluthList;

	private CallBack callBack;
	private PressImpFragment activity;
	private DeviceAdapter adapter;
	private List<Map<String, String>> data;
	
	private BluetoothAdapter mAdapter;
	private BluetoothComm myComm;
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
						mAdapter.cancelDiscovery();
						myComm.startConnect(device);
						initDeviceList();
						isConnecting = false;
					}
				}
			}
		}
	};

	public static EquipListDialog getInstance() {
		return new EquipListDialog();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (PressImpFragment) context;
		data = new ArrayList<Map<String, String>>();
		adapter = new DeviceAdapter(activity, data);
		myComm = new BluetoothComm(activity,ContantsUtil.bluthUserId);
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
					BluetoothControlForBP bp = null;
					@SuppressWarnings("unchecked")
					Set<HashMap.Entry<BluetoothDevice, BluetoothControlForBP>> set = BluetoothComm.mapBPDeviceConnected.entrySet();
					for (Iterator<Map.Entry<BluetoothDevice, BluetoothControlForBP>> it = set.iterator(); it.hasNext();) {
						Map.Entry<BluetoothDevice, BluetoothControlForBP> entry = (Map.Entry<BluetoothDevice, BluetoothControlForBP>) it.next();
						if (entry.getKey().getName().equals(data.get(position).get("name"))) {
							bp = entry.getValue();
							break;
						}
					}
					if(bp == null){
						Toast.makeText(context, "设备不存在", Toast.LENGTH_SHORT).show();
					}else{
						callBack.callBack(bp);
					}
				}
				dismiss();
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
				Log.d("start", "startDiscovery");
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
		data.clear();
		Set<HashMap.Entry<BluetoothDevice, BluetoothControlForBP>> set = BluetoothComm.mapBPDeviceConnected
				.entrySet();
		for (Iterator<Map.Entry<BluetoothDevice, BluetoothControlForBP>> it = set
				.iterator(); it.hasNext();) {
			Map.Entry<BluetoothDevice, BluetoothControlForBP> entry = (Map.Entry<BluetoothDevice, BluetoothControlForBP>) it
					.next();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", entry.getKey().getName());
			map.put("address", entry.getKey().getAddress());
			data.add(map);
		}
		adapter.notifyDataSetChanged();
	}

	private void initBluthReceiver() {
		// 注册广播接收信号
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED);
		intentFilter.addAction(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		activity.registerReceiver(BluetoothReceiver, intentFilter);
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
		void callBack(BluetoothControlForBP bpControl);
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
	
	public void setConnect(BluetoothComm connector){
		this.myComm = connector;
	}
}
