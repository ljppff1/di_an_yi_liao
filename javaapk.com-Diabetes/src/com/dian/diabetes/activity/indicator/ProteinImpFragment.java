package com.dian.diabetes.activity.indicator;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import android.content.Context;
import android.os.Bundle;
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
import com.dian.diabetes.db.dao.IndicateValue;
import com.dian.diabetes.dialog.DayDialog;
import com.dian.diabetes.dialog.TimeDialog;
import com.dian.diabetes.tool.CallBack;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

public class ProteinImpFragment extends BasicFragmentDialog implements
		OnClickListener {

	@ViewInject(id = R.id.delete)
	private ImageButton deleteBtn;
	@ViewInject(id = R.id.save_btn)
	private ImageButton saveBtn;
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.day)
	private LinearLayout day;
	@ViewInject(id = R.id.time)
	private LinearLayout time;
	@ViewInject(id = R.id.add_entry_day)
	private TextView dayView;
	@ViewInject(id = R.id.add_entry_time)
	private TextView timeView;
	@ViewInject(id = R.id.protern_value)
	private EditText proternView;
	@ViewInject(id = R.id.protern_con)
	private RelativeLayout proternCon;

	private IndicatorActivity activity;
	private TimeDialog timeDialog;
	private DayDialog dayDialog;
	private CallBack callBack;

	private DecimalFormat format;
	private IndicateValue value;
	private boolean isAdd = true;
	private Date selectDate;
	private IndicateBo bo;
	private long indicateId;

	public static ProteinImpFragment getInstance(boolean isAdd, String key,long indicateId) {
		ProteinImpFragment LipidImp = new ProteinImpFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isAdd", isAdd);
		bundle.putLong("indicateId", indicateId);
		LipidImp.setArguments(bundle);
		return LipidImp;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (IndicatorActivity) context;
		Bundle bundle = getArguments();
		isAdd = bundle.getBoolean("isAdd");
		indicateId = bundle.getLong("indicateId");
		format = new DecimalFormat("00");
		selectDate = new Date();
		bo = new IndicateBo(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_protern_layout,
				container, false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		day.setOnClickListener(this);
		time.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		proternCon.setOnClickListener(this);
		if (isAdd) {
			deleteBtn.setVisibility(View.GONE);
			value = new IndicateValue();
		} else {
			setIndicateView();
		}
		dayView.setText(DateUtil.parseToString(selectDate, DateUtil.yyyymmdd));
		timeView.setText(DateUtil.parseToString(selectDate, DateUtil.HHmm));
	}

	private void setIndicateView() {
		proternView.setText(value.getValue() + "");
		selectDate.setTime(value.getCreate_time());
		dayView.setText(DateUtil.parseToString(selectDate, DateUtil.yyyymmdd));
		timeView.setText(DateUtil.parseToString(selectDate, DateUtil.HHmm));
	}

	public void setIndicateValue(IndicateValue value) {
		this.value = value;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.day:
			openDayDialog();
			break;
		case R.id.time:
			openTimeDialog();
			break;
		case R.id.back_btn:
			dismiss();
			break;
		case R.id.delete:
			delete();
			break;
		case R.id.save_btn:
			saveValue();
			break;
		case R.id.protern_con:
			proternView.requestFocus();
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			proternView.setSelection(proternView.getText().length());
			imm.showSoftInput(proternView,InputMethodManager.SHOW_FORCED);  
			break;
		}
	}

	private void saveValue() {
		float temp = StringUtil.toFloat(proternView.getText());
		if (isAdd) {
			String group = UUID.randomUUID().toString(); 
			bo.saveValue("protein","protein",group,temp, indicateId, ContantsUtil.DEFAULT_TEMP_UID,
					Config.getIndicateLevel(temp, "lowProtein", "highProtein"),
					selectDate.getTime());
		} else {
			bo.updateValue(temp, value, indicateId,
					Config.getIndicateLevel(temp, "lowProtein", "highProtein"),
					selectDate.getTime());
		}
		activity.getPreference().putBoolean(Preference.HAS_UPDATE, true);
		if (callBack != null) {
			callBack.callBack();
		}
		dismiss();
	}

	private void delete() {
		bo.deleteValue(value, indicateId);
		if (callBack != null) {
			callBack.callBack();
		}
		dismiss();
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

	public void setCallback(CallBack callBack) {
		this.callBack = callBack;
	}
}
