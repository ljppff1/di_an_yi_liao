package com.dian.diabetes.activity.medicine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicFragmentDialog;
import com.dian.diabetes.activity.medicine.adapter.AlarmAdapter;
import com.dian.diabetes.activity.tool.MedicineFragment;
import com.dian.diabetes.db.AlarmBo;
import com.dian.diabetes.db.MedicineBo;
import com.dian.diabetes.db.dao.Alarm;
import com.dian.diabetes.db.dao.Medicine;
import com.dian.diabetes.db.dao.Normal;
import com.dian.diabetes.dialog.AlertDialog;
import com.dian.diabetes.dialog.CheckListDialog;
import com.dian.diabetes.dialog.TimeDialog;
import com.dian.diabetes.dto.CheckModel;
import com.dian.diabetes.tool.CallBack;
import com.dian.diabetes.tool.CommonUtil;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.NListView;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 添加药品功能界面
 * 
 * @author longbh
 * 
 */
public class AddMedicineFragment extends BasicFragmentDialog implements
		OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.weight_contro)
	private RelativeLayout weightController;
	@ViewInject(id = R.id.day_contro)
	private RelativeLayout dayController;
	@ViewInject(id = R.id.medicine_time_contro)
	private RelativeLayout timeController;
	@ViewInject(id = R.id.medicine_numcontro)
	private RelativeLayout numController;
	@ViewInject(id = R.id.class_controller)
	private RelativeLayout classController;
	@ViewInject(id = R.id.name_contro)
	private RelativeLayout nameController;
	@ViewInject(id = R.id.delete)
	private ImageButton deleteBtn;
	@ViewInject(id = R.id.save_btn)
	private Button saveBtn;
	@ViewInject(id = R.id.medicne_class)
	private TextView medicineClass;
	@ViewInject(id = R.id.medicine_name)
	private TextView medicneName;
	@ViewInject(id = R.id.medicine_num)
	private TextView medicneNum;
	@ViewInject(id = R.id.medicine_time)
	private TextView medicneTime;
	@ViewInject(id = R.id.medicine_weight)
	private EditText medicineWeight;
	@ViewInject(id = R.id.medicine_day)
	private EditText medicineDay;
	@ViewInject(id = R.id.medicine_list)
	private NListView alarmsList;
	@ViewInject(id = R.id.alarm_eable)
	private CheckBox checkBox;
	@ViewInject(id = R.id.g_nion)
	private TextView unitView;

	// 弹出框
	private DinnerTimeDialog stageDialog;
	private CheckListDialog listDialog;
	private TimeDialog timeDialog;
	private AlertDialog alert;

	private MedicineActivity activity;
	private List<Alarm> alarms;
	private AlarmAdapter alarmAdapter;
	private Medicine medicine;
	private boolean isAdd = false;
	private boolean updateAlarm = false;
	private MedicineBo bo;
	private AlarmBo alarmBo;
	private InputMethodManager imm;
	private int stage = ContantsUtil.EAT_PRE;
	private int numType = 0;
	private CallBack callBack;

	public static AddMedicineFragment getInstance(boolean isAdd) {
		AddMedicineFragment medicineAdd = new AddMedicineFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isAdd", isAdd);
		medicineAdd.setArguments(bundle);
		return medicineAdd;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (MedicineActivity) context;
		Bundle bundle = getArguments();
		isAdd = bundle.getBoolean("isAdd");
		bo = new MedicineBo(activity);
		imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		alarmBo = new AlarmBo(activity);
		if (isAdd) {
			alarms = new ArrayList<Alarm>();
		} else {
			alarms = alarmBo.listAlarms(medicine.getId());
		}
		alarmAdapter = new AlarmAdapter(activity, alarms);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_medicine_edit,
				container, false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		weightController.setOnClickListener(this);
		dayController.setOnClickListener(this);
		timeController.setOnClickListener(this);
		numController.setOnClickListener(this);
		classController.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		if (isAdd) {
			deleteBtn.setVisibility(View.GONE);
			medicine = new Medicine();
		} else {
			setMedicineView();
		}
		alarmsList.setAdapter(alarmAdapter);
		alarmsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View parent,
					int position, long id) {
				openTimeDialog(position, alarms.get(position));
			}
		});
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean state) {
				if (state) {
					alarmsList.setVisibility(View.VISIBLE);
					checkBox.setText("打开");
				} else {
					alarmsList.setVisibility(View.GONE);
					checkBox.setText("关闭");
				}
			}
		});
	}

	private void setMedicineView() {
		numType = medicine.getNumType();
		medicneNum.setText(CommonUtil.getValue("medicine" + numType));
		medicineWeight.setText(medicine.getWeight() + "");
		medicineDay.setText(medicine.getEatDay() + "");
		medicineClass.setText(medicine.getType());
		medicneName.setText(medicine.getName());
		unitView.setText(medicine.getUnit());
		medicneTime.setText(CommonUtil.getValue("stage" + medicine.getStage()));
		if (medicine.getIstoast()) {
			checkBox.setText("打开");
			checkBox.setChecked(true);
			alarmsList.setVisibility(View.VISIBLE);
		} else {
			checkBox.setText("关闭");
			checkBox.setChecked(false);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			dismiss();
			break;
		case R.id.weight_contro:
			medicineWeight.requestFocus();
			imm.showSoftInput(medicineWeight, InputMethodManager.SHOW_FORCED);
			break;
		case R.id.day_contro:
			medicineDay.requestFocus();
			imm.showSoftInput(medicineDay, InputMethodManager.SHOW_FORCED);
			break;
		case R.id.medicine_numcontro:
			if (listDialog == null) {
				listDialog = new CheckListDialog(activity);
				listDialog.setCall(new CheckListDialog.CallBack() {

					@Override
					public void callBack(List<CheckModel> data) {
						numType = 0;
						alarms.clear();
						updateAlarm = true;
						for (CheckModel item : data) {
							if (item.isCheck) {
								Alarm alarm = new Alarm();
								int alarmType;
								if (!"3".equals(item.key)) {
									alarmType = StringUtil.toInt(item.key
											+ stage);
								} else {
									alarmType = StringUtil.toInt(item.key + 0);
								}
								String time = (String) Config
										.getProperty("clock" + alarmType);
								String timeArray[] = time.split(":");
								alarm.setHour(StringUtil.toShort(timeArray[0]));
								alarm.setMinite(StringUtil
										.toShort(timeArray[1]));
								alarm.setCreateTime(System.currentTimeMillis());
								alarm.setMessage("您设定的服药时间到了");
								alarm.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
								alarm.setUid(ContantsUtil.curUser.getService_uid());
								alarm.setSub_type((short) alarmType);
								alarm.setType((short) ContantsUtil.MEDICINE_ALARM);
								alarm.setTitle("迪安血糖提醒您");
								alarms.add(alarm);
								numType++;
							}
						}
						if (numType != 0) {
							alarmAdapter.notifyDataSetChanged();
							medicneNum.setText(CommonUtil.getValue("medicine"
									+ numType));
						}
					}
				});
				// listDialog.setCall(new DayListDialog.CallBack() {
				// @Override
				// public void callBack(int position, String model) {
				// medicneNum.setText(model);
				// numType = position + 1;
				// alarms.clear();
				// updateAlarm = true;
				// for (int i = 0; i < numType; i++) {
				// Alarm alarm = new Alarm();
				// int alarmType = StringUtil.toInt("" + i + stage);
				// String time = (String) Config.getProperty("clock"
				// + alarmType);
				// String timeArray[] = time.split(":");
				// alarm.setHour(StringUtil.toShort(timeArray[0]));
				// alarm.setMinite(StringUtil.toShort(timeArray[1]));
				// alarm.setCreateTime(System.currentTimeMillis());
				// alarm.setMessage("您设定的服药时间到了");
				// alarm.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
				// alarm.setSub_type((short) alarmType);
				// alarm.setType((short) ContantsUtil.MEDICINE_ALARM);
				// alarm.setTitle("迪安血糖提醒您");
				// alarms.add(alarm);
				// }
				// alarmAdapter.notifyDataSetChanged();
				// }
				// });
			}
			listDialog.show();
			break;
		case R.id.medicine_time_contro:
			if (stageDialog == null) {
				stageDialog = new DinnerTimeDialog(activity);
				stageDialog.setCall(new DinnerTimeDialog.CallBack() {
					@Override
					public void callBack(int position, String model) {
						medicneTime.setText(model);
						if (position == 0) {
							stage = ContantsUtil.EAT_PRE;
						} else {
							stage = ContantsUtil.EAT_AFTER;
						}
						for (int i = 0; i < alarms.size(); i++) {
							Alarm alarm = alarms.get(i);
							int dinner = alarm.getSub_type() / 10;
							if (dinner == 3) {
								stage = ContantsUtil.EAT_PRE;
							}
							int alarmType = StringUtil.toInt(""
									+ alarm.getSub_type() / 10 + stage);
							alarm.setSub_type((short) alarmType);
							String time = (String) Config.getProperty("clock"
									+ alarmType);
							String timeArray[] = time.split(":");
							alarm.setHour(StringUtil.toShort(timeArray[0]));
							alarm.setMinite(StringUtil.toShort(timeArray[1]));
						}
						alarmAdapter.notifyDataSetChanged();
					}
				});
			}
			stageDialog.show();
			break;
		case R.id.class_controller:
			showToolFragment();
			break;
		case R.id.delete:
			if (alert == null) {
				alert = new AlertDialog(activity, "您确定要删除这条记录吗");
				alert.setCallBack(new AlertDialog.CallBack() {

					@Override
					public void cancel() {
					}

					@Override
					public void callBack() {
						alarmBo.deleteAlarm(medicine.getId(),
								medicine.getService_mid(),
								ContantsUtil.curUser.getService_uid());
						bo.deleteLocal(medicine);
						dismiss();
						if (callBack != null) {
							callBack.callBack();
						}
						activity.getPreference().putBoolean(
								Preference.HAS_UPDATE, true);
					}
				});
			}
			alert.show();
			break;
		case R.id.save_btn:
			if (CheckUtil
					.isNull(medicneName.getText(), medicineClass.getText())) {
				Toast.makeText(activity, "请选择药名", Toast.LENGTH_SHORT).show();
				return;
			}
			if (CheckUtil.isNull(medicineDay.getText())) {
				Toast.makeText(activity, "服用天数不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (CheckUtil.isNull(medicineWeight.getText())) {
				Toast.makeText(activity, "服用剂量不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			saveMedicine();
			dismiss();
			if (callBack != null) {
				callBack.callBack();
			}
			break;
		}
	}

	private void showToolFragment() {
		String tag = "show_tool_fragment";
		FragmentManager manager = context.getSupportFragmentManager();
		MedicineFragment tempFragment = (MedicineFragment) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = MedicineFragment.getInstance();
			tempFragment.setCallBack(new MedicineFragment.CallBack() {
				@Override
				public void callBack(Normal normal) {
					medicneName.setText(normal.getName());
					medicineClass.setText(normal.getCategory());
					unitView.setText(normal.getUnit());
					// medicineWeight.setText(normal.getDoes());
				}
			});
		}
		if (!tempFragment.isAdded()) {
			tempFragment.show(manager, tag);
		}
	}

	private void openTimeDialog(int position, final Alarm alarm) {
		if (timeDialog == null) {
			timeDialog = new TimeDialog(activity, "时间选择");
			timeDialog.setCallBack(new TimeDialog.CallBack() {
				@Override
				public boolean callBack(int hour, int mini) {
					alarm.setHour((short) hour);
					alarm.setMinite((short) mini);
					alarmAdapter.notifyDataSetChanged();
					return true;
				}
			});
		}
		timeDialog.show(alarm.getHour(), alarm.getMinite());
	}

	//这里存放了冗余的用药时间提醒数据，即用药记录里边有提醒时间，同时在闹钟里边也新建了闹钟
	private void saveMedicine() {
		medicine.setName(medicneName.getText() + "");
		medicine.setType(medicineClass.getText() + "");
		medicine.setCreateTime(System.currentTimeMillis());
		medicine.setIsOut(false);
		medicine.setIstoast(checkBox.isChecked());
		int eatDay = StringUtil.toInt(medicineDay.getText());
		medicine.setEatDay(eatDay);
		medicine.setWeight(medicineWeight.getText() + "");
		medicine.setStage(stage);
		medicine.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
		medicine.setNumType(numType);
		medicine.setUnit(unitView.getText() + "");
		medicine.setStatus(ContantsUtil.NO_SERVER);
		if (alarms.size() >= 1) {
			medicine.setRmdBreakfast(alarms.get(0).getHour() + ":"
					+ alarms.get(0).getMinite());
		}
		if (alarms.size() >= 2) {
			medicine.setRmdLunch(alarms.get(1).getHour() + ":"
					+ alarms.get(1).getMinite());
		}
		if (alarms.size() >= 3) {
			medicine.setRmdsupper(alarms.get(2).getHour() + ":"
					+ alarms.get(2).getMinite());
		}
		if(alarms.size() >= 4){
			medicine.setRmdSleep(alarms.get(3).getHour() + ":"
					+ alarms.get(3).getMinite());
		}
		long id = bo.saveMedicine(medicine);
		// save提醒
		AlarmBo aBo = new AlarmBo(activity);
		for (Alarm alarm : alarms) {
			alarm.setoId(id);
			alarm.setEnable(checkBox.isChecked());
			alarm.setRepeat(eatDay);
			alarm.setMessage(ContantsUtil.curInfo.getNick_name() + ":你的服药时间到了");
		}
		if (updateAlarm) {
			aBo.saveMedicineAlarms(medicine.getId(), alarms,
					ContantsUtil.DEFAULT_TEMP_UID,
					ContantsUtil.curUser.getService_uid());
		} else {
			aBo.updateAlarms(alarms);
		}
		activity.getPreference().putBoolean(Preference.HAS_UPDATE, true);
	}

	public void setMedicine(Medicine medicine) {
		this.medicine = medicine;
	}

	public void setCallBack(CallBack callBack) {
		this.callBack = callBack;
	}

}
