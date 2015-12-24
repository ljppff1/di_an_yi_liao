package com.dian.diabetes.activity.sugar;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicFragmentDialog;
import com.dian.diabetes.activity.sugar.adapter.SugarPlugAdapter;
import com.dian.diabetes.db.BloodBo;
import com.dian.diabetes.db.CommonBo;
import com.dian.diabetes.db.dao.Common;
import com.dian.diabetes.db.dao.Diabetes;
import com.dian.diabetes.dialog.AlertDialog;
import com.dian.diabetes.dialog.DayDialog;
import com.dian.diabetes.dialog.SugarInputDialog;
import com.dian.diabetes.dialog.TimeDialog;
import com.dian.diabetes.tool.CommonUtil;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.BubbleButton;
import com.dian.diabetes.widget.HorizontalListView;
import com.dian.diabetes.widget.ProgressWidget;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 添加血糖界面,伴随状态以逗号分隔保存，中部图标为一个自定义控件
 * @author hua
 *
 */
public class SugarAddFragment extends BasicFragmentDialog implements
		OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	// @ViewInject(id = R.id.sycn_equip)
	// private ImageButton sycnEquip;
	@ViewInject(id = R.id.plut_list)
	private HorizontalListView listView;
	@ViewInject(id = R.id.add_entry_time)
	private TextView timeView;
	@ViewInject(id = R.id.add_entry_day)
	private TextView dayView;
	@ViewInject(id = R.id.save_btn)
	private Button saveButton;
	@ViewInject(id = R.id.eat)
	private Button eatView;
	@ViewInject(id = R.id.blood_input)
	private EditText bloodInput;
	@ViewInject(id = R.id.mark)
	private EditText mark;
	@ViewInject(id = R.id.delete)
	private ImageButton deleteBtn;
	@ViewInject(id = R.id.blood_progress)
	private ProgressWidget progWidget;
	@ViewInject(id = R.id.container)
	private ScrollView container;
	@ViewInject(id = R.id.sycn_cache)
	private BubbleButton sycnCache;
	@ViewInject(id = R.id.sycn_equip_button)
	private LinearLayout equipBtn;

	private List<Common> data;
	private SugarPlugAdapter adapter;
	private SugarActivity activity;
	private boolean type = false;
	private short aType = ContantsUtil.BRECKFAST;
	private int testType = ContantsUtil.EAT_PRE;
	public final int EQUIP_CODE = 10003;
	
	private DayDialog dayDialog;
	private TimeDialog timeDialog;
	private EntryPopDialog mealsDialog;
	private Date selectDate;
	private Diabetes diabetes;
	private DecimalFormat format;
	private BloodBo bo;
	private CallBack callBack;
	private AlertDialog alert;
	private SugarInputDialog inputDialog;

	public static SugarAddFragment getInstance(boolean edit, long diabetesId,
			int dinnerType) {
		Bundle bundle = new Bundle();
		bundle.putBoolean("type", edit);
		bundle.putLong("id", diabetesId);
		bundle.putInt("dinnerType", dinnerType);
		SugarAddFragment sugarAdd = new SugarAddFragment();
		sugarAdd.setArguments(bundle);
		return sugarAdd;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (SugarActivity) context;
		new CommonBo(activity);
		bo = new BloodBo(activity);
		adapter = new SugarPlugAdapter(activity, data);

		Bundle bundle = getArguments();
		type = bundle.getBoolean("type", false);
		int dinnerType = bundle.getInt("dinnerType");
		aType = (short) (dinnerType/10);
		testType = dinnerType%10;
		if (type) {
			long id = bundle.getLong("id");
			diabetes = new BloodBo(activity).getById(id);
			alert = new AlertDialog(activity, "您确定要删除这条记录吗");
			alert.setCallBack(new AlertDialog.CallBack() {

				@Override
				public void cancel() {
				}

				@Override
				public void callBack() {
					bo.deleteLocal(diabetes);
					activity.getPreference().putBoolean(Preference.HAS_UPDATE,
							true);
					if (callBack != null) {
						callBack.callBack(selectDate);
					}
					dismiss();
				}
			});
		}

		format = new DecimalFormat("00");
		inputDialog = new SugarInputDialog(activity, "血糖输入");
		inputDialog.setCallBack(new SugarInputDialog.CallBack() {
			@Override
			public boolean callBack(float value) {
				bloodInput.setText(value + "");
				return true;
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case EQUIP_CODE:
			Bundle bundle = data.getExtras();
			String value = bundle.getString("value");
			bloodInput.setText(value);
			bloodInput.setEnabled(false);
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sugar_add, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		// sycnEquip.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		sycnCache.setOnClickListener(this);
		equipBtn.setOnClickListener(this);
		// bloodInput.setOnClickListener(this);
		// inputLayout.setOnClickListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				data.get(position).select();
				adapter.notifyDataSetChanged();
				saveButton.setVisibility(View.VISIBLE);
			}
		});
		// 测量分类
		eatView.setOnClickListener(this);
		// load默认时间
		timeView.setText(DateUtil.parseToString(selectDate, DateUtil.HHmm));
		timeView.setOnClickListener(this);
		dayView.setText(DateUtil.parseToString(selectDate, DateUtil.yyyymmdd));
		dayView.setOnClickListener(this);
		initData();
		listView.setAdapter(adapter);
		// 让 ScrollView移动到顶端
		container.smoothScrollTo(0, 20);
		bloodInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence set, int arg1, int arg2,
					int arg3) {
				if (!CheckUtil.isNull(set)) {
					float value = StringUtil.toFloat(set);
					progWidget.setValue(value, testType);
					// equipBtn.setVisibility(View.GONE);
					equipBtn.setEnabled(false);
					// if (!type) {
					// sycnEquip.setVisibility(View.VISIBLE);
					// }
					saveButton.setVisibility(View.VISIBLE);
				} else {
					progWidget.setValue(0, testType);
					// equipBtn.setVisibility(View.VISIBLE);
					equipBtn.setEnabled(true);
					// sycnEquip.setVisibility(View.GONE);
					saveButton.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
	}

	private void initData() {
		// 缓存血糖数据
		int total = activity.getPreference().getInt(Preference.CACHE_NUM, 0);
		sycnCache.setBubleValue(total);
		// if(total != 0){
		// sycnCache.setVisibility(View.VISIBLE);
		// sycnCache.setBubleValue(total);
		// }else{
		// sycnCache.setVisibility(View.GONE);
		// }
		if (type) {
			bloodInput.setText(diabetes.getValue() + "");
			bloodInput.setSelection(bloodInput.getText().length());
			progWidget.setValueLevel(diabetes.getValue(), diabetes.getLevel());
			mark.setText(diabetes.getMark());
			selectDate = new Date(diabetes.getCreate_time());
			dayView.setText(DateUtil.parseToString(diabetes.getCreate_time(),
					DateUtil.yyyyMMdd));
			timeView.setText(DateUtil.parseToString(diabetes.getCreate_time(),
					DateUtil.HHmm));
			String typeStr = CommonUtil.getValue("diabetes" + diabetes.getType() + diabetes.getSub_type());
			eatView.setText(typeStr);
			if (!CheckUtil.isNull(diabetes.getFeel())) {
				String feels[] = diabetes.getFeel().split(",");
				for (int j = 0; j < data.size(); j++) {
					for (int i = 0; i < feels.length; i++) {
						if (data.get(j).getServerid().equals(feels[i].trim())) {
							data.get(j).setSelect(true);
							break;
						} else {
							data.get(j).setSelect(false);
						}
					}
				}
				adapter.notifyDataSetChanged();
			} else {
				for (int j = 0; j < data.size(); j++) {
					data.get(j).setSelect(false);
				}
			}

			deleteBtn.setVisibility(View.VISIBLE);
			// sycnEquip.setVisibility(View.GONE);
		} else {
			for (Common itemData : data) {
				itemData.setSelect(false);
			}
			adapter.notifyDataSetChanged();
			deleteBtn.setVisibility(View.GONE);
			String typeStr = CommonUtil.getValue("diabetes" + aType + testType);
			eatView.setText(typeStr);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			dismiss();
			break;
		case R.id.sycn_equip_button:
			// case R.id.sycn_equip:
			toEquipActivity();
			break;
		case R.id.add_entry_day:
			if (dayDialog == null) {
				dayDialog = new DayDialog(context, "日期选择");
				dayDialog.setCallBack(new DayDialog.CallBack() {
					@Override
					public boolean callBack(int year, int month, int day) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(selectDate);
						calendar.set(Calendar.YEAR, year);
						calendar.set(Calendar.MONTH, month - 1);
						calendar.set(Calendar.DATE, day);
						selectDate = calendar.getTime();
						Log.i("date", DateUtil.parseToString(new Date(),
								DateUtil.yyyyMMddHHmmss));
						Log.i("test", DateUtil.parseToString(selectDate,
								DateUtil.yyyyMMddHHmmss));
						if (selectDate.compareTo(new Date()) > 0) {
							Toast.makeText(context, R.string.toast_time_after,
									Toast.LENGTH_SHORT).show();
							return false;
						}
						dayView.setText(year + "-" + format.format(month) + "-"
								+ format.format(day));
						saveButton.setVisibility(View.VISIBLE);
						return true;
					}
				});
			}
			dayDialog.show(selectDate.getTime());
			break;
		case R.id.add_entry_time:
			if (timeDialog == null) {
				timeDialog = new TimeDialog(activity, "时间选择");
				timeDialog.setCallBack(new TimeDialog.CallBack() {
					@Override
					public boolean callBack(int hour, int mini) {
						if (selectDate.compareTo(new Date()) > 0) {
							Toast.makeText(context, R.string.toast_time_after,
									Toast.LENGTH_SHORT).show();
							return false;
						}
						selectDate.setHours(hour);
						selectDate.setMinutes(mini);
						saveButton.setVisibility(View.VISIBLE);
						timeView.setText(format.format(hour) + ":"
								+ format.format(mini));
						return true;
					}
				});
			}
			timeDialog.show(selectDate.getTime());
			break;
		case R.id.save_btn:
			if (saveButton() != null) {
				if (callBack != null) {
					callBack.callBack(selectDate);
				}
				dismiss();
			}
			break;
		case R.id.delete:
			alert.show();
			break;
		// case R.id.blood_input:
		// inputDialog.show(StringUtil.toFloat(bloodInput.getText()+""));
		// break;
		case R.id.sycn_cache:
			toCacheFragment();
			break;
		case R.id.eat:
			switchEatType(view);
			break;
		// case R.id.input_layout:
		// inputDialog.show(StringUtil.toFloat(bloodInput.getText()+""));
		// break;
		}
	}

	private void switchEatType(View parent) {
		if (mealsDialog == null) {
			mealsDialog = new EntryPopDialog(activity);
			mealsDialog.setCallBack(new MealsPopDialog.CallBack() {
				@Override
				public void callBack(int model) {
					switch (model) {
					case 0:
						aType = ContantsUtil.BRECKFAST;
						testType = ContantsUtil.EAT_PRE;
						break;
					case 1:
						aType = ContantsUtil.BRECKFAST;
						testType = ContantsUtil.EAT_AFTER;
						break;
					case 2:
						aType = ContantsUtil.LAUNCH;
						testType = ContantsUtil.EAT_PRE;
						break;
					case 3:
						aType = ContantsUtil.LAUNCH;
						testType = ContantsUtil.EAT_AFTER;
						break;
					case 4:
						aType = ContantsUtil.DINNER;
						testType = ContantsUtil.EAT_PRE;
						break;
					case 5:
						aType = ContantsUtil.DINNER;
						testType = ContantsUtil.EAT_AFTER;
						break;
					case 6:
						aType = ContantsUtil.SLEEP_PRE;
						testType = ContantsUtil.EAT_PRE;
						break;
					}
					eatView.setText(mealsDialog.getModel(model).getValue());
					if (!CheckUtil.isNull(bloodInput.getText())) {
						float value = StringUtil.toFloat(bloodInput.getText());
						progWidget.setValue(value, testType);
					} else {
						progWidget.setValue(0, testType);
					}
					saveButton.setVisibility(View.VISIBLE);
				}
			});
		}
		mealsDialog.showAsDropDown(parent);
	}

	/**
	 * 保存到本地
	 */
	private Diabetes saveButton() {
		if (diabetes == null) {
			diabetes = new Diabetes();
		}
		String value = bloodInput.getText() + "";
		if (CheckUtil.isNull(value)) {
			Toast.makeText(activity, "请输入血糖值", Toast.LENGTH_SHORT).show();
			return null;
		}
		float valueF = StringUtil.toFloat(value);
		if (valueF < 0 || valueF > 33) {
			Toast.makeText(activity, "请输入正确范围内的血糖值", Toast.LENGTH_SHORT).show();
			return null;
		}
		diabetes.setMark(mark.getText() + "");
		long time = selectDate.getTime();
		diabetes.setCreate_time(time);
		diabetes.setDay(DateUtil.parseToString(selectDate, DateUtil.yyyyMMdd));
		String ids = "";
		int index = 0;
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSelect()) {
				if (index != 0) {
					ids += ",";
				}
				ids = ids + data.get(i).getServerid();
				index++;
			}
		}
		diabetes.setFeel(ids);
		diabetes.setStatus((short) 0);
		diabetes.setValue(valueF);
		diabetes.setUpdate_time(System.currentTimeMillis());
		diabetes.setLevel(Config.getBloodState(valueF, testType));
		// short type = (short) Config.getEatState(selectDate);
		diabetes.setType(aType);
		diabetes.setSub_type(testType);
		if (aType == ContantsUtil.SLEEP_PRE) {
			diabetes.setSub_type(ContantsUtil.EAT_PRE);
		}
		diabetes.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
		long id = bo.saveUpdateDiabetes(diabetes);
		Log.e("data_size", id + "----save 成功");
		diabetes.setId(id);
		activity.getPreference().putBoolean(Preference.HAS_UPDATE, true);
		return diabetes;
	}

//	private void toEquipFragment() {
//		String tag = "sugar_sycn_equip";
//		FragmentManager manager = context.getSupportFragmentManager();
//		SugarEquipDialog tempFragment = (SugarEquipDialog) context
//				.getSupportFragmentManager().findFragmentByTag(tag);
//		if (tempFragment == null) {
//			tempFragment = SugarEquipDialog.getInstance();
//			tempFragment.setCallBack(new SugarEquipDialog.CallBack() {
//				@Override
//				public void callBack(int position) {
//					bloodInput.setText(position + "");
//				}
//			});
//		}
//		tempFragment.setDateAndType(dayView.getText() + "", timeView.getText()
//				+ "", eatView.getText() + "");
//		if (!tempFragment.isAdded()) {
//			tempFragment.show(manager, tag);
//		}
//	}
	
	private void toEquipActivity(){
		Bundle bundle = new Bundle();
		bundle.putString("day", dayView.getText() + "");
		bundle.putString("time", timeView.getText() + "");
		bundle.putString("eat", eatView.getText() + "");
		Intent intent = new Intent(activity,SugarEquipActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, EQUIP_CODE);
	}

	public boolean onBackKeyPressed() {
		dismiss();
		return true;
	}

	public void setCallBack(CallBack callBack) {
		this.callBack = callBack;
	}

	public void setCommonData(List<Common> data) {
		this.data = data;
	}
	
	public void setDate(Date select){
		this.selectDate = select;
	}

	private void toCacheFragment() {
		String tag = "sugar_sycn_cache";
		FragmentManager manager = context.getSupportFragmentManager();
		SugarCacheFragment tempFragment = (SugarCacheFragment) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = SugarCacheFragment.Instance();
			tempFragment.setCallBack(new com.dian.diabetes.tool.CallBack() {
				@Override
				public void callBack() {
					int total = activity.getPreference().getInt(Preference.CACHE_NUM, 0);
					sycnCache.setBubleValue(total);
				}
			});
		}
		if (!tempFragment.isAdded()) {
			tempFragment.show(manager, tag);
		}
	}
	
	public interface CallBack{
		void callBack(Date date);
	}
}
