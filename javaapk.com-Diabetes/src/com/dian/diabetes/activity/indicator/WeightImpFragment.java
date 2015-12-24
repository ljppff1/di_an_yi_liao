package com.dian.diabetes.activity.indicator;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicFragmentDialog;
import com.dian.diabetes.db.IndicateBo;
import com.dian.diabetes.db.PropertyBo;
import com.dian.diabetes.db.dao.IndicateValue;
import com.dian.diabetes.dialog.DayDialog;
import com.dian.diabetes.dialog.TimeDialog;
import com.dian.diabetes.tool.CallBack;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

public class WeightImpFragment extends BasicFragmentDialog implements
		OnClickListener {

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
	@ViewInject(id = R.id.weight_value)
	private EditText weightView;
	@ViewInject(id = R.id.height_value)
	private EditText heightView;
	@ViewInject(id = R.id.bmi)
	private TextView bmiView;
	@ViewInject(id = R.id.weight_con)
	private RelativeLayout weightCon;
	@ViewInject(id = R.id.height_con)
	private RelativeLayout heightCon;
	@ViewInject(id = R.id.add_entry_time)
	private TextView timeView;

	private IndicatorActivity activity;
	private DayDialog dayDialog;
	private TimeDialog timeDialog;
	private CallBack callBack;

	private DecimalFormat format;
	private IndicateValue weightValue;
	private IndicateValue heightValue;
	private IndicateValue bmiValue;
	private boolean isAdd = true;
	private Date selectDate;
	private IndicateBo bo;
	private long indicateId;
	private String key;
	private InputMethodManager imm;

	public static WeightImpFragment getInstance(boolean isAdd, String key,
			long indicateId) {
		WeightImpFragment LipidImp = new WeightImpFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isAdd", isAdd);
		bundle.putString("key", key);
		bundle.putLong("indicateId", indicateId);
		LipidImp.setArguments(bundle);
		return LipidImp;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		activity = (IndicatorActivity) context;
		Bundle bundle = getArguments();
		isAdd = bundle.getBoolean("isAdd");
		key = bundle.getString("key");
		indicateId = bundle.getLong("indicateId");
		format = new DecimalFormat("00");
		selectDate = new Date();
		if (bo == null) {
			bo = new IndicateBo(activity);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_weight_impl, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		day.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		weightCon.setOnClickListener(this);
		timeView.setOnClickListener(this);
		heightCon.setOnClickListener(this);
		if (isAdd) {
			deleteBtn.setVisibility(View.GONE);
			if (CheckUtil.isNull(Config.getProperty("height"))) {
				heightView.setText("");
			} else {
				heightView.setText(Config.getProperty("height") + "");
			}
		} else {
			setIndicateView();
		}
		dayView.setText(DateUtil.parseToString(selectDate, DateUtil.yyyymmdd));
		timeView.setText(DateUtil.parseToString(selectDate, DateUtil.HHmm));
		weightView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable edit) {
				if (!CheckUtil.isNull(weightView.getText())
						&& !CheckUtil.isNull(heightView.getText())) {
					float weightFlo = StringUtil.toFloat(edit);
					float heightFlo = StringUtil.toFloat(heightView.getText()) / 100;
					float bmi = (float) (weightFlo / heightFlo / heightFlo);
					bmiView.setText(format.format(bmi));
				}
			}
		});
		heightView.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable edit) {
				if (!CheckUtil.isNull(weightView.getText())
						&& !CheckUtil.isNull(heightView.getText())) {
					float weightFlo = StringUtil.toFloat(weightView.getText());
					float heightFlo = StringUtil.toFloat(heightView.getText()) / 100;
					float bmi = (float) (weightFlo / heightFlo / heightFlo);
					bmiView.setText(format.format(bmi));
				}
			}
		});

	}

	private void setIndicateView() {
		weightView.setText(weightValue.getValue() + "");
		bmiView.setText(format.format(bmiValue.getValue()));
		heightView.setText(heightValue.getValue() + "");
		selectDate.setTime(bmiValue.getCreate_time());
		dayView.setText(DateUtil.parseToString(selectDate, DateUtil.yyyymmdd));
		timeView.setText(DateUtil.parseToString(selectDate, DateUtil.HHmm));
	}

	public void setIndicateValue(IndicateValue value) {
		if (bo == null) {
			bo = new IndicateBo(activity);
		}
		Map<String, IndicateValue> datas = bo.keyMapIndicate(value.getGroup());
		this.weightValue = datas.get("weight");
		this.bmiValue = datas.get("bmi");
		this.heightValue = datas.get("height");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.day:
			openDayDialog();
			break;
		case R.id.back_btn:
			dismiss();
			break;
		case R.id.delete:
			delete();
			break;
		case R.id.save_btn:
			if (CheckUtil.isNull(weightView.getText())) {
				Toast.makeText(activity, "体重不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (CheckUtil.isNull(heightView.getText())) {
				Toast.makeText(activity, "身高不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			saveValue();
			break;
		case R.id.weight_con:
			openInput(weightView);
			break;
		case R.id.add_entry_time:
			openTimeDialog();
			break;
		case R.id.height_con:
			openInput(heightView);
			break;
		}
	}

	private void openInput(EditText view) {
		view.requestFocus();
		view.setSelection(view.getText().length());
		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}

	private void saveValue() {
		Config.setPro("height", heightView.getText() + "");
		new PropertyBo(context).updateByKey(key, ContantsUtil.DEFAULT_TEMP_UID,
				heightView.getText() + "");
		float temp = StringUtil.toFloat(weightView.getText());
		float bmi = StringUtil.toFloat(bmiView.getText());
		float height = StringUtil.toFloat(heightView.getText());
		int level = Config.getIndicateLevel(bmi, "bmiLow", "bmiHigh");
		if (isAdd) {
			String group = UUID.randomUUID().toString();
			bo.saveValue("weight", "weight", group, temp, indicateId,
					ContantsUtil.DEFAULT_TEMP_UID,
					Config.getIndicateLevel(temp, "weightLow", "weightHigh"),
					selectDate.getTime());
			bo.saveValue("bmi", "weight", group, bmi, indicateId,
					ContantsUtil.DEFAULT_TEMP_UID, level, selectDate.getTime());
			bo.saveValue("height", "weight", group, height, indicateId,
					ContantsUtil.DEFAULT_TEMP_UID, level, selectDate.getTime());
		} else {
			bo.updateValue(temp, weightValue, indicateId,
					Config.getIndicateLevel(temp, "weightLow", "weightHigh"),
					selectDate.getTime());
			bo.updateValue(bmi, bmiValue, indicateId, level,
					selectDate.getTime());
			bo.updateValue(height, heightValue, indicateId, 1,
					selectDate.getTime());
		}
		activity.getPreference().putBoolean(Preference.HAS_UPDATE, true);
		if (callBack != null) {
			callBack.callBack();
		}
		dismiss();
	}

	private void delete() {
		bo.deleteByGroup(weightValue.getMarkNo(), indicateId,
				ContantsUtil.DEFAULT_TEMP_UID);
		if (callBack != null) {
			callBack.callBack();
		}
		dismiss();
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
}
