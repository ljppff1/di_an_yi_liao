package com.dian.diabetes.activity.indicator;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jiuan.androidnin1.bluetooth.BP.BluetoothComm;
import jiuan.androidnin1.bluetooth.BP.BluetoothControlForBP;
import jiuan.androidnin1.bluetooth.BPObserver.JiuanBPObserver;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.db.IndicateBo;
import com.dian.diabetes.db.dao.IndicateValue;
import com.dian.diabetes.dialog.DayDialog;
import com.dian.diabetes.dialog.TimeDialog;
import com.dian.diabetes.tool.CallBack;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 手工录入高血压，低血压，心率等值
 * 
 * @author longbh
 * 
 */
public class PressImpFragment extends BasicActivity implements OnClickListener,
		JiuanBPObserver {

	@ViewInject(id = R.id.delete)
	private ImageButton deleteBtn;
	@ViewInject(id = R.id.save_btn)
	private ImageButton saveBtn;
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.day)
	private LinearLayout day;
	@ViewInject(id = R.id.add_entry_day)
	private TextView dayView;
	@ViewInject(id = R.id.close_value)
	private EditText closeView; // 高血压
	@ViewInject(id = R.id.open_value)
	private EditText openView; // 低血压
	@ViewInject(id = R.id.heart)
	private EditText heartView;
	@ViewInject(id = R.id.high_con)
	private RelativeLayout highCon;
	@ViewInject(id = R.id.low_con)
	private RelativeLayout lowCon;
	@ViewInject(id = R.id.heart_con)
	private RelativeLayout heartCon;
	@ViewInject(id = R.id.add_entry_time)
	private TextView timeView;
	@ViewInject(id = R.id.sycn_equip_button)
	private LinearLayout equipBtn;
	@ViewInject(id = R.id.start_mesure)
	private Button startMesure;
	@ViewInject(id = R.id.mesure_line)
	private ImageView mesurLine;

	private PressImpFragment activity;
	private DayDialog dayDialog;
	private TimeDialog timeDialog;
	private CallBack callBack;
	private InputMethodManager imm;
	private BluetoothAdapter mAdapter;

	private DecimalFormat format;
	private IndicateValue openValue;
	private IndicateValue heartValue;
	private boolean isAdd = true;
	private Date selectDate;
	private IndicateBo bo;
	private long indicateId;
	private String key;
	private String group;

	private BluetoothControlForBP bpControl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_press_impl);
		activity = (PressImpFragment) context;
		Bundle bundle = getIntent().getExtras();
		isAdd = bundle.getBoolean("isAdd");
		key = bundle.getString("key");
		indicateId = bundle.getLong("indicateId");
		format = new DecimalFormat("00");
		bo = new IndicateBo(activity);
		if (!isAdd) {
			group = bundle.getString("group");
			setIndicateValue(group);
		}
		selectDate = new Date();
		imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mAdapter = BluetoothAdapter.getDefaultAdapter();

		initActivity();
	}

	private void initActivity() {
		day.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		highCon.setOnClickListener(this);
		lowCon.setOnClickListener(this);
		heartCon.setOnClickListener(this);
		equipBtn.setOnClickListener(this);
		timeView.setOnClickListener(this);
		startMesure.setOnClickListener(this);

		if (isAdd) {
			deleteBtn.setVisibility(View.GONE);
		} else {
			setIndicateView();
		}
		dayView.setText(DateUtil.parseToString(selectDate, DateUtil.yyyymmdd));
		timeView.setText(DateUtil.parseToString(selectDate, DateUtil.HHmm));
	}

	private void setIndicateView() {
		openView.setText(openValue.getValue1() + "");
		openView.setSelection(openView.getText().length());
		closeView.setText(openValue.getValue() + "");
		closeView.setSelection(closeView.getText().length());
		if (heartValue != null) {
			heartView.setText(heartValue.getValue() + "");
			heartView.setSelection(heartView.getText().length());
		}
		selectDate.setTime(openValue.getCreate_time());
		dayView.setText(DateUtil.parseToString(selectDate, DateUtil.yyyymmdd));
		timeView.setText(DateUtil.parseToString(selectDate, DateUtil.HHmm));
	}

	public void setIndicateValue(String group) {
		Map<String, IndicateValue> datas = bo.keyMapIndicate(group);
		this.openValue = datas.get("openPress");
		this.heartValue = datas.get("heart");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.day:
			openDayDialog();
			break;
		case R.id.back_btn:
			finish();
			break;
		case R.id.delete:
			delete();
			ContantsUtil.SELF_DETAIL = false;
			break;
		case R.id.save_btn:
			if (CheckUtil.isNull(openView.getText())) {
				Toast.makeText(activity, "舒张压不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (CheckUtil.isNull(closeView.getText())) {
				Toast.makeText(activity, "收缩压不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!isAdd) {
				if (CheckUtil.isNull(heartView.getText())) {
					Toast.makeText(activity, "心率不能为空", Toast.LENGTH_SHORT)
							.show();
					return;
				}
			}
			saveValue();
			ContantsUtil.SELF_DETAIL = false;
			finish();
			break;
		case R.id.high_con:
			closeView.requestFocus();
			imm.showSoftInput(closeView, InputMethodManager.SHOW_FORCED);
			closeView.setSelection(closeView.getText().length());
			break;
		case R.id.low_con:
			openView.requestFocus();
			openView.setSelection(openView.getText().length());
			imm.showSoftInput(openView, InputMethodManager.SHOW_FORCED);
			break;
		case R.id.heart_con:
			heartView.requestFocus();
			heartView.setSelection(heartView.getText().length());
			imm.showSoftInput(heartView, InputMethodManager.SHOW_FORCED);
			break;
		case R.id.sycn_equip_button:
			startMeansure();
			break;
		case R.id.add_entry_time:
			openTimeDialog();
			break;
		case R.id.start_mesure:
			startMeansure();
			break;
		}
	}

	private void saveValue() {
		float open = StringUtil.toFloat(openView.getText());
		float close = StringUtil.toFloat(closeView.getText());
		if (isAdd) {
			// 舒张压
			String group = UUID.randomUUID().toString();
			bo.savePress("openPress", "press", group, close, open, indicateId,
					ContantsUtil.DEFAULT_TEMP_UID, Config.getIndicateLevel(
							close, "lowClosePress", "highClosePress"), Config
							.getIndicateLevel(open, "lowOpenPress",
									"highOpenPress"), selectDate.getTime());
			// 心率
			if (!CheckUtil.isNull(heartView.getText())) {
				float heat = StringUtil.toFloat(heartView.getText());
				bo.saveValue("heart", "press", group, heat, indicateId,
						ContantsUtil.DEFAULT_TEMP_UID,
						Config.getIndicateLevel(heat, "lowHeart", "highHeart"),
						selectDate.getTime());
			}
		} else {
			bo.updatePress(close, open, openValue, indicateId,
					Config.getIndicateLevel(close, "lowClosePress",
							"highClosePress"), Config.getIndicateLevel(open,
							"lowOpenPress", "highOpenPress"), selectDate
							.getTime());
			// 心率
			if (heartValue == null && !CheckUtil.isNull(heartView.getText())) {
				float heat = StringUtil.toFloat(heartView.getText());
				bo.saveValue("heart", "press", group, heat, indicateId,
						ContantsUtil.DEFAULT_TEMP_UID,
						Config.getIndicateLevel(heat, "lowHeart", "highHeart"),
						selectDate.getTime());
			} else if (heartValue != null
					&& !CheckUtil.isNull(heartView.getText())) {
				float heat = StringUtil.toFloat(heartView.getText());
				bo.updateValue(heat, heartValue, indicateId,
						Config.getIndicateLevel(heat, "lowHeart", "highHeart"),
						selectDate.getTime());
			}
		}
		Preference.instance(activity).putBoolean(Preference.HAS_UPDATE, true);
	}

	private void delete() {
		bo.deleteByGroup(openValue.getMarkNo(), indicateId,
				ContantsUtil.DEFAULT_TEMP_UID);
		if (callBack != null) {
			callBack.callBack();
		}
		finish();
	}

	public String getRandom(int length) {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[length];
		random.nextBytes(bytes);
		return new String(bytes);
	}

	private void openDayDialog() {
		if (dayDialog == null) {
			dayDialog = new DayDialog(activity, "日期选择");
			dayDialog.setCallBack(new DayDialog.CallBack() {
				@Override
				public boolean callBack(int year, int month, int day) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(selectDate);
					calendar.set(Calendar.YEAR, year);
					calendar.set(Calendar.MONTH, month - 1);
					calendar.set(Calendar.DATE, day);
					Date temp = calendar.getTime();
					if (temp.compareTo(new Date()) > 0) {
						Toast.makeText(context, R.string.toast_time_after,
								Toast.LENGTH_SHORT).show();
						return false;
					}
					selectDate = calendar.getTime();
					dayView.setText(year + "-" + format.format(month) + "-"
							+ format.format(day));
					return true;
				}
			});
		}
		dayDialog.show();
	}

	private void openTimeDialog() {
		if (timeDialog == null) {
			timeDialog = new TimeDialog(activity, "时间选择");
			timeDialog.setCallBack(new TimeDialog.CallBack() {
				@Override
				public boolean callBack(int hour, int mini) {
					Date temp = new Date(selectDate.getTime());
					temp.setHours(hour);
					temp.setMinutes(mini);
					if (temp.compareTo(new Date()) > 0) {
						Toast.makeText(context, R.string.toast_time_after,
								Toast.LENGTH_SHORT).show();
						return false;
					}
					selectDate.setHours(hour);
					selectDate.setMinutes(mini);
					timeView.setText(format.format(hour) + ":"
							+ format.format(mini));
					return true;
				}
			});
		}
		timeDialog.show();
	}

	public void setCallback(CallBack callBack) {
		this.callBack = callBack;
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
				public void callBack(BluetoothControlForBP tempControll) {
					bpControl = tempControll;
					bpControl.controlSubject.attach(activity);
					mesurLine.setImageResource(R.drawable.heart_animation);
					AnimationDrawable drawable = (AnimationDrawable) mesurLine
							.getDrawable();
					drawable.start();
					startMesure.setVisibility(View.GONE);
					startMeansure();

				}
			});
		}
		if (!tempFragment.isAdded()) {
			tempFragment.show(manager, tag);
		}
	}

	private void startMeansure() {
		if (bpControl == null) {
			openEquipList();
		} else {
			bpControl.start(activity, ContantsUtil.clientID,
					ContantsUtil.clientSecret);
		}
	}

	private void stopMeansure() {
		mesurLine.clearAnimation();
		startMesure.setVisibility(View.VISIBLE);
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				Bundle bundle = (Bundle) msg.obj;
				int[] result = bundle.getIntArray("bp");
				closeView.setText((result[0] + result[1]) + "");
				closeView.setEnabled(false);
				openView.setText(result[1] + "");
				openView.setEnabled(false);
				heartView.setText(result[2] + "");
				heartView.setEnabled(false);
				highCon.setEnabled(false);
				lowCon.setEnabled(false);
				heartCon.setEnabled(false);
				break;
			case 2:
				Bundle bundle2 = (Bundle) msg.obj;
				int state = bundle2.getInt("num");
				ToastTool.toastPress(state);
				stopMeansure();
				break;
			case 3:
				ToastTool.toastPress(-1);
				stopMeansure();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void msgAngle(int arg0) {

	}

	@Override
	public void msgBattery(int arg0) {

	}

	@Override
	public void msgError(int num) {
		try {
			Message message = new Message();
			message.what = 2;
			Bundle bundle = new Bundle();
			bundle.putInt("error", num);
			message.obj = bundle;
			handler.sendMessage(message);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void msgInden() {

	}

	@Override
	public void msgMeasure(int arg0, int[] arg1, boolean arg2) {

	}

	@Override
	public void msgPowerOff() {
		handler.sendEmptyMessage(3);
	}

	@Override
	public void msgPressure(int arg0) {

	}

	@Override
	public void msgResult(int[] result) {
		Message message = new Message();
		message.what = 1;
		Bundle bundle = new Bundle();
		bundle.putIntArray("bp", result);
		message.obj = bundle;
		handler.sendMessage(message);
	}

	@Override
	public void msgUserStatus(int arg0) {

	}

	@Override
	public void msgZeroIng() {

	}

	@Override
	public void msgZeroOver() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mAdapter.isEnabled()) {
			mAdapter.disable();
		}
		Set<HashMap.Entry<BluetoothDevice, BluetoothControlForBP>> set = BluetoothComm.mapBPDeviceConnected
				.entrySet();
		for (Iterator<Map.Entry<BluetoothDevice, BluetoothControlForBP>> it = set
				.iterator(); it.hasNext();) {
			Map.Entry<BluetoothDevice, BluetoothControlForBP> entry = (Map.Entry<BluetoothDevice, BluetoothControlForBP>) it
					.next();
			entry.getValue().stop();
		}
	}

}
