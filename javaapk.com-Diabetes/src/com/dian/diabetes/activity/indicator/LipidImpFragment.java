package com.dian.diabetes.activity.indicator;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
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
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

public class LipidImpFragment extends BasicFragmentDialog implements
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
	@ViewInject(id = R.id.ch_value)
	private EditText chView;
	@ViewInject(id = R.id.tg_value)
	private EditText tgView;
	@ViewInject(id = R.id.hdl_value)
	private EditText hdlView;
	@ViewInject(id = R.id.ldl_value)
	private EditText ldlView;
	@ViewInject(id = R.id.bmi)
	private TextView bmiView;
	@ViewInject(id = R.id.ch_con)
	private RelativeLayout chCon;
	@ViewInject(id = R.id.tg_con)
	private RelativeLayout tgCon;
	@ViewInject(id = R.id.hdl_con)
	private RelativeLayout hdlCon;
	@ViewInject(id = R.id.ldl_con)
	private RelativeLayout ldlCon;

	private IndicatorActivity activity;
	private DayDialog dayDialog;
	private TimeDialog timeDialog;
	private CallBack callBack;

	private DecimalFormat format;
	private IndicateValue chvalue;
	private IndicateValue tgvalue;
	private IndicateValue hdlvalue;
	private IndicateValue ldlvalue;
	private boolean isAdd = true;
	private Date selectDate;
	private IndicateBo bo;
	private long indicateId;
	private String key;
	private String group;

	public static LipidImpFragment getInstance(boolean isAdd, String key,
			long indicateId) {
		LipidImpFragment LipidImp = new LipidImpFragment();
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
		activity = (IndicatorActivity) context;
		Bundle bundle = getArguments();
		isAdd = bundle.getBoolean("isAdd");
		key = bundle.getString("key");
		indicateId = bundle.getLong("indicateId");
		format = new DecimalFormat("00");
		selectDate = new Date();
		bo = new IndicateBo(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_lipid_impl, container,
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
		chCon.setOnClickListener(this);
		tgCon.setOnClickListener(this);
		hdlCon.setOnClickListener(this);
		ldlCon.setOnClickListener(this);
		time.setOnClickListener(this);
		if (isAdd) {
			deleteBtn.setVisibility(View.GONE);
		} else {
			setIndicateView();
		}
		dayView.setText(DateUtil.parseToString(selectDate, DateUtil.yyyymmdd));
		timeView.setText(DateUtil.parseToString(selectDate, DateUtil.HHmm));
	}

	private void setIndicateView() {
		if (chvalue != null) {
			chView.setText(chvalue.getValue() + "");
		}
		if (tgvalue != null) {
			tgView.setText(tgvalue.getValue() + "");
		}
		if (hdlvalue != null) {
			hdlView.setText(hdlvalue.getValue() + "");
		}
		if (ldlvalue != null) {
			ldlView.setText(ldlvalue.getValue() + "");
		}
		selectDate.setTime(chvalue.getCreate_time());
		dayView.setText(DateUtil.parseToString(selectDate, DateUtil.yyyymmdd));
		timeView.setText(DateUtil.parseToString(selectDate, DateUtil.HHmm));
	}

	public void setIndicateValue(IndicateValue value) {
		if (bo == null) {
			bo = new IndicateBo(activity);
		}
		group = value.getGroup();
		Map<String, IndicateValue> datas = bo.keyMapIndicate(value.getGroup());
		this.chvalue = datas.get("ch");
		this.tgvalue = datas.get("tg");
		this.hdlvalue = datas.get("hdl");
		this.ldlvalue = datas.get("ldl");
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
		case R.id.ch_con:
			openInput(chView);
			break;
		case R.id.tg_con:
			openInput(tgView);
			break;
		case R.id.hdl_con:
			openInput(hdlView);
			break;
		case R.id.ldl_con:
			openInput(ldlView);
			break;
		}
	}

	private void openInput(EditText view) {
		view.requestFocus();
		view.setSelection(view.getText().length());
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}

	private void saveValue() {
		if (isAdd) {
			String group = UUID.randomUUID().toString();
			if (!CheckUtil.isNull(chView.getText())) {
				float ch = StringUtil.toFloat(chView.getText());
				bo.saveValue("ch", "lipid", group, ch, indicateId,
						ContantsUtil.DEFAULT_TEMP_UID,
						Config.getIndicateLevel(ch, "lowCh", "highCh"),
						selectDate.getTime());
			}
			if (!CheckUtil.isNull(tgView.getText())) {
				float tg = StringUtil.toFloat(tgView.getText());
				bo.saveValue("tg", "lipid", group, tg, indicateId,
						ContantsUtil.DEFAULT_TEMP_UID,
						Config.getIndicateLevel(tg, "lowTg", "highTg"),
						selectDate.getTime());
			}
			if (!CheckUtil.isNull(hdlView.getText())) {
				float hdl = StringUtil.toFloat(hdlView.getText());
				bo.saveValue("hdl", "lipid", group, hdl, indicateId,
						ContantsUtil.DEFAULT_TEMP_UID,
						Config.getIndicateLevel(hdl, "lowHdl", "highHdl"),
						selectDate.getTime());
			}
			if (!CheckUtil.isNull(ldlView.getText())) {
				float ldl = StringUtil.toFloat(ldlView.getText());
				bo.saveValue("ldl", "lipid", group, ldl, indicateId,
						ContantsUtil.DEFAULT_TEMP_UID,
						Config.getIndicateLevel(ldl, "lowLdl", "highLdl"),
						selectDate.getTime());
			}
		} else {
			if (chvalue != null && !CheckUtil.isNull(chView.getText())) {
				float ch = StringUtil.toFloat(chView.getText());
				bo.updateValue(ch, chvalue, indicateId,
						Config.getIndicateLevel(ch, "lowCh", "highCh"),
						selectDate.getTime());
			} else if (chvalue == null && !CheckUtil.isNull(chView.getText())) {
				float ch = StringUtil.toFloat(chView.getText());
				bo.saveValue("ch", "lipid", group, ch, indicateId,
						ContantsUtil.DEFAULT_TEMP_UID,
						Config.getIndicateLevel(ch, "lowCh", "highCh"),
						selectDate.getTime());
			}
			if (tgvalue != null && !CheckUtil.isNull(tgView.getText())) {
				float tg = StringUtil.toFloat(tgView.getText());
				bo.updateValue(tg, tgvalue, indicateId,
						Config.getIndicateLevel(tg, "lowTg", "highTg"),
						selectDate.getTime());
			} else if (tgvalue == null && !CheckUtil.isNull(tgView.getText())) {
				float tg = StringUtil.toFloat(tgView.getText());
				bo.saveValue("tg", "lipid", group, tg, indicateId,
						ContantsUtil.DEFAULT_TEMP_UID,
						Config.getIndicateLevel(tg, "lowTg", "highTg"),
						selectDate.getTime());
			}
			if (hdlvalue != null && !CheckUtil.isNull(hdlView.getText())) {
				float hdl = StringUtil.toFloat(hdlView.getText());
				bo.updateValue(hdl, hdlvalue, indicateId,
						Config.getIndicateLevel(hdl, "lowHdl", "highHdl"),
						selectDate.getTime());
			} else if (hdlvalue == null && !CheckUtil.isNull(hdlView.getText())) {
				float hdl = StringUtil.toFloat(hdlView.getText());
				bo.saveValue("hdl", "lipid", group, hdl, indicateId,
						ContantsUtil.DEFAULT_TEMP_UID,
						Config.getIndicateLevel(hdl, "lowHdl", "highHdl"),
						selectDate.getTime());
			}
			if (ldlvalue != null && !CheckUtil.isNull(hdlView.getText())) {
				float ldl = StringUtil.toFloat(ldlView.getText());
				bo.updateValue(ldl, ldlvalue, indicateId,
						Config.getIndicateLevel(ldl, "lowLdl", "highLdl"),
						selectDate.getTime());
			} else if (ldlvalue == null && !CheckUtil.isNull(ldlView.getText())) {
				float ldl = StringUtil.toFloat(ldlView.getText());
				bo.saveValue("ldl", "lipid", group, ldl, indicateId,
						ContantsUtil.DEFAULT_TEMP_UID,
						Config.getIndicateLevel(ldl, "lowLdl", "highLdl"),
						selectDate.getTime());
			}
		}
		activity.getPreference().putBoolean(Preference.HAS_UPDATE, true);
		if (callBack != null) {
			callBack.callBack();
		}
		dismiss();
	}

	private void delete() {
		bo.deleteValue(chvalue, indicateId);
		bo.deleteValue(tgvalue, indicateId);
		bo.deleteValue(hdlvalue, indicateId);
		bo.deleteValue(ldlvalue, indicateId);
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
