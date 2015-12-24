package com.dian.diabetes.activity.sugar;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import jiuan.androidBg.Bluetooth.BG5Control;
import jiuan.androidBg.Comm.BGCommManager;
import jiuan.androidBg.Observer.Interface_Observer_BG;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.dialog.AlertDialog;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.widget.BubbleButton;
import com.dian.diabetes.widget.ProgressWidget;
import com.dian.diabetes.widget.anotation.ViewInject;
import com.dian.diabetes.zxing.CaptureActivity;

/**
 * 连接与血糖仪设备连接的activity，先列出所有的设备列表，弹出设备列表界面，选择设备，再进行连接
 * @author hua
 *
 */
public class SugarEquipActivity extends BasicActivity implements
		OnClickListener, ViewFactory, Interface_Observer_BG {
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.img_switcher)
	private ImageSwitcher switcher;
	@ViewInject(id = R.id.edit_btn)
	private LinearLayout editBtn;
	@ViewInject(id = R.id.sycn_cache)
	private BubbleButton sycnBtn;
	@ViewInject(id = R.id.blood_input)
	private TextView inputView;
	@ViewInject(id = R.id.progress)
	private ProgressWidget bloodProgress;
	@ViewInject(id = R.id.cp_progress)
	private ProgressBar progress;
	@ViewInject(id = R.id.toast_label)
	private TextView toastView;
	@ViewInject(id = R.id.eat)
	private Button eatType;
	@ViewInject(id = R.id.add_entry_time)
	private TextView entryTime;
	@ViewInject(id = R.id.add_entry_day)
	private TextView entryDay;
	@ViewInject(id = R.id.scance_zxing)
	private ImageButton scancer;

	// 蓝牙
	private BGCommManager connector;
	private BG5Control bg5Control;
	private BluetoothAdapter mAdapter;
	private MealsPopDialog mealDialog;

	private BasicActivity activity;
	private AlertDialog codeAlert;
	
	private Preference preference;
	private boolean isregistor = false;
	private int testType = ContantsUtil.EAT_PRE;
	private int[] enableIcon = { R.drawable.equip_enable_step1,
			R.drawable.equip_enable_step2, R.drawable.equip_enable_step3,
			R.drawable.equip_enable_step4, R.drawable.equip_enable_step5 };
	private int[] disableIcon = { R.drawable.equip_disable_step1,
			R.drawable.equip_disable_step2, R.drawable.equip_disable_step3,
			R.drawable.equip_disable_step4 };
	private int[] descImg = { R.drawable.main_topbg, R.drawable.main_topbg,
			R.drawable.main_topbg, R.drawable.main_topbg, R.drawable.main_topbg };
	private List<ImageView> images;
	private String qr;
	private int curentStep = 0;
	private final int SCAN_CODE = 10223;
	private final int SCAN_MAIN = 10224;

	private String tempDay = "";
	private String tempTime = "";
	private String tempType = "";

	private BroadcastReceiver BluetoothReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				step1();
			}
		}
	};

	// public static SugarEquipDialog getInstance() {
	// return new SugarEquipDialog();
	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_sugar_equip);
		activity = (BasicActivity) context;
		preference = Preference.instance(activity);
		images = new ArrayList<ImageView>();
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		qr = preference.getString(Preference.DEVICE_CODE);
		connector = new BGCommManager(activity);
		codeAlert = new AlertDialog(activity, "您还未扫描瓶盖上的二维码,确定要去扫二维码吗？");
		codeAlert.setCallBack(new AlertDialog.CallBack() {
			@Override
			public void cancel() {
				
			}
			
			@Override
			public void callBack() {
				Intent intent = new Intent();
				intent.setClass(context, CaptureActivity.class);
				startActivityForResult(intent, SCAN_CODE);
			}
		});
		initActivity();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		Bundle bundle = data.getExtras();
		switch (requestCode) {
		case SCAN_CODE:
			qr = bundle.getString("result");
			if(CheckUtil.isNull(qr)){
				Toast.makeText(context, "未扫描到正确的二维码", Toast.LENGTH_SHORT).show();
			}else{
				preference.putString(Preference.DEVICE_CODE,qr);
				images.get(1).setImageResource(enableIcon[curentStep]);
				toastView.setText("扫描到二维码，正在连接设备");
				thread.start();
			}
			break;
		case SCAN_MAIN:
			qr = bundle.getString("result");
			if(CheckUtil.isNull(qr)){
				Toast.makeText(context, "未扫描到正确的二维码", Toast.LENGTH_SHORT).show();
			}else{
				preference.putString(Preference.DEVICE_CODE,qr);
				Toast.makeText(context, "成功描到瓶盖的二维码，已缓存到本地", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	public void onResume() {
		super.onResume();
		sycnBtn.setBubleValue(preference.getInt(Preference.CACHE_NUM));
	}

	private void initActivity() {
		Bundle bundle = getIntent().getExtras();
		tempDay = bundle.getString("day");
		tempTime = bundle.getString("time");
		tempType = bundle.getString("eat");
		
		backBtn.setOnClickListener(this);
		editBtn.setOnClickListener(this);
		sycnBtn.setOnClickListener(this);
		scancer.setOnClickListener(this);
		switcher.setFactory(this);
		switcher.setOutAnimation(activity, R.anim.slide_left_in);
		switcher.setOutAnimation(activity, R.anim.slide_right_out);

		images.add((ImageView) findViewById(R.id.sycn_step1));
		images.add((ImageView) findViewById(R.id.sycn_step2));
		images.add((ImageView) findViewById(R.id.sycn_step3));
		images.add((ImageView) findViewById(R.id.sycn_step4));
		images.add((ImageView) findViewById(R.id.sycn_step5));

		switcher.setImageResource(R.drawable.main_topbg);
		openBluth();
		eatType.setText(tempType);
		entryTime.setText(tempTime);
		entryDay.setText(tempDay);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.edit_btn:
			finish();
			break;
		case R.id.sycn_cache:
			toCacheFragment();
			break;
		case R.id.scance_zxing:
			Intent intent = new Intent();
			intent.setClass(context, CaptureActivity.class);
			startActivityForResult(intent, SCAN_MAIN);
			break;
		case R.id.eat:
			if (mealDialog == null) {
				mealDialog = new MealsPopDialog(activity);
				mealDialog.setCallBack(new MealsPopDialog.CallBack() {
					@Override
					public void callBack(int model) {
						if (model == 0) {
							testType = ContantsUtil.EAT_PRE;
						} else {
							testType = ContantsUtil.EAT_AFTER;
						}
					}
				});
			}
			mealDialog.showAsDropDown(view);
			break;
		}
	}

	private void openBluth() {
		if (!mAdapter.isEnabled()) {
			mAdapter.enable();
			IntentFilter intent = new IntentFilter();
			intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
			activity.registerReceiver(BluetoothReceiver, intent);
			isregistor = true;
		} else {
			step1();
		}
	}

	private void startConnect(String device) {
		bg5Control = BGCommManager.getBG5Control(device);
		if (bg5Control != null) {
			bg5Control.bg5subject.attach(this);
			bg5Control.connect(activity, ContantsUtil.bluthUserId,
					ContantsUtil.clientID, ContantsUtil.clientSecret);
		} else {
			openEquipList();
		}
	}

	private void toCacheFragment() {
		String tag = "sugar_sycn_cache";
		FragmentManager manager = context.getSupportFragmentManager();
		SugarCacheFragment tempFragment = (SugarCacheFragment) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = SugarCacheFragment.Instance();
		}
		tempFragment.setConnect(connector);
		if (!tempFragment.isAdded()) {
			tempFragment.show(manager, tag);
		}
	}

	private void openEquipList() {
		String tag = "equip_list_dialog";
		FragmentManager manager = context.getSupportFragmentManager();
		EquipListDialog tempFragment = (EquipListDialog) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = EquipListDialog.getInstance();
			tempFragment.setCallBack(new EquipListDialog.CallBack() {
				@Override
				public void callBack(String device) {
					startConnect(device);
				}
			});
		}
		tempFragment.setConnect(connector);
		if (!tempFragment.isAdded()) {
			tempFragment.show(manager, tag);
		}
	}

	@Override
	public View makeView() {
		return new ImageView(context);
	}

	public interface CallBack {
		void callBack(int position);
	}

	@Override
	public void msgBGError(int state) {
		Log.d("data", "msgBGError" + state);
		Message msg = new Message();
		msg.arg1 = state;
		msg.what = 3;
		handler.sendMessage(msg);
	}

	@Override
	public void msgBGGetBlood() {
		Log.d("data", "msgBGGetBlood");
		Message msg = new Message();
		msg.arg1 = 3;
		msg.what = 2;
		handler.sendMessage(msg);
	}

	@Override
	public void msgBGPowerOff() {
		Log.d("data", "msgBGPowerOff");
		handler.sendEmptyMessage(11);
	}

	@Override
	public void msgBGResult(int result) {
		Log.d("data", "msgBGResult");
		Message msg = new Message();
		msg.arg1 = result;
		msg.what = 4;
		handler.sendMessage(msg);
	}

	@Override
	public void msgBGStripIn() {
		Log.d("data", "msgBGStripIn");
		handler.sendEmptyMessage(10);
	}

	@Override
	public void msgBGStripOut() {
		Log.e("data", "msgBGStripOut");
		Message msg = new Message();
		msg.arg1 = 2;
		msg.what = 2;
		handler.sendMessage(msg);
	}

	@Override
	public void msgUserStatus(int status) {
		Log.d("data", "msgUserStatus");
		Message msg = new Message();
		msg.arg1 = status;
		msg.what = 1;
		handler.sendMessage(msg);
	}

	private Thread thread = new Thread() {
		public void run() {
			if (bg5Control.setParams(1, 1)) {
				// //清空先
				// if(bg5Control.deleteOfflineData()){
				// Log.d("data", "deleteOfflineData");
				// Message message3 = new Message();
				// message3.what = 6;
				// handler.sendMessage(message3);
				// }
				// //读取离线数据
				// String offline = bg5Control.readOfflineData();
				// Message message3 = new Message();
				// message3.what = 6;
				// message3.obj = offline;
				// handler.sendMessage(message3);
				String info = bg5Control.getBottleInfoByErWeiMa(qr);
				try {
					JSONObject infoObj = new JSONObject(info).getJSONArray(
							"bottleInfo").getJSONObject(0);
					String date = infoObj.getString("overDate");
					int stripNum = infoObj.getInt("stripNum");
					if(stripNum == 0){
						handler.sendEmptyMessage(12);
					}else{
						// sendCode
						Message msg = new Message();
						msg.what = 9;
						handler.sendMessage(msg);
						bg5Control.sendCode(qr, date, stripNum);
					}
				} catch (JSONException e) {
					Message msg = new Message();
					msg.what = 8;
					handler.sendMessage(msg);
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
				String msgStr = ToastTool.toastBluth(msg.arg1);
				if (msg.arg1 > 0 && msg.arg1 < 5) {
					if (!CheckUtil.isNull(qr)) {
						thread.start();
					} else {
						images.get(1).setImageResource(disableIcon[curentStep]);
						toastView.setText("请先扫描瓶盖上的二维码信息");
						switcher.setImageResource(descImg[curentStep]);
						codeAlert.show();
					}
				} else {
					images.get(curentStep).setImageResource(
							disableIcon[curentStep]);
					toastView.setText(msgStr);
				}
				break;
			case 2:
				curentStep = 3;
				images.get(curentStep).setImageResource(enableIcon[curentStep]);
				progress.setVisibility(View.VISIBLE);
				images.get(curentStep + 1).setVisibility(View.GONE);
				toastView.setText(R.string.get_result);
				switcher.setImageResource(descImg[curentStep]);
				break;
			case 3:
				String msgStr3 = ToastTool.toastErrorBluth(msg.arg1);
				images.get(curentStep)
						.setImageResource(disableIcon[curentStep]);
				toastView.setText(msgStr3);
				switcher.setImageResource(descImg[curentStep]);
				break;
			case 4:
				curentStep = 4;
				if (msg.arg1 < 20 || msg.arg1 > 600) {
					inputView.setText("测量数据错误，请重新测量");
					progress.setVisibility(View.GONE);
					images.get(curentStep).setVisibility(View.VISIBLE);
				} else {
					int mml = msg.arg1 / 18;
					inputView.setText(mml + "");
					bloodProgress.setValue(msg.arg1, testType);
					images.get(curentStep).setVisibility(View.VISIBLE);
					images.get(curentStep).setImageResource(
							enableIcon[curentStep]);
					progress.setVisibility(View.GONE);
					handler.sendEmptyMessageDelayed(5, 1000);
				}
				break;
			case 5:
//				if (call != null) {
//					call.callBack(StringUtil.toInt(inputView.getText()));
//				}
				Intent resultIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("value", inputView.getText() + "");
				resultIntent.putExtras(bundle);
				setResult(RESULT_OK, resultIntent);
				finish();
				break;
			case 6:
				String offLine = msg.obj + "";
				Toast.makeText(context, offLine, Toast.LENGTH_LONG).show();
				break;
			case 7:
				images.get(curentStep)
						.setImageResource(disableIcon[curentStep]);
				toastView.setText("请先扫描瓶盖上的二维码信息");
				break;
			case 8:
				images.get(curentStep).setImageResource(disableIcon[curentStep]);
				toastView.setText("数据出错，请重新测量");
				break;
			case 9:
				curentStep = 1;
				images.get(curentStep).setImageResource(enableIcon[curentStep]);
				toastView.setText(R.string.trip_in);
				switcher.setImageResource(descImg[curentStep]);
				break;
			case 10:
				curentStep = 2;
				images.get(curentStep).setImageResource(enableIcon[curentStep]);
				toastView.setText(R.string.get_blood);
				switcher.setImageResource(descImg[curentStep]);
				break;
			case 11:
				images.get(curentStep).setImageResource(disableIcon[curentStep]);
				toastView.setText("血糖仪已关机，测量终止");
				switcher.setImageResource(descImg[curentStep]);
				break;
			case 12:
				images.get(curentStep).setImageResource(disableIcon[curentStep]);
				toastView.setText("该瓶试纸已用完，请重新扫描瓶盖上的二维码");
				codeAlert.show();
				break;
			}
		}
	};

	private void step1() {
		images.get(0).setImageResource(enableIcon[0]);
		toastView.setText(R.string.connect_device);
		switcher.setImageResource(descImg[0]);
		startConnect("");
	}

	public void onDestroy() {
		super.onDestroy();
		if (isregistor) {
			activity.unregisterReceiver(BluetoothReceiver);
		}
		if (bg5Control != null) {
			bg5Control.stop();
		}
		if (mAdapter.isEnabled()) {
			mAdapter.disable();
		}
		connector.stop();
	}
}
