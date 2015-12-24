package com.dian.diabetes.activity.sport;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicFragmentDialog;
import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.activity.tool.SportFragment;
import com.dian.diabetes.db.SportBo;
import com.dian.diabetes.db.dao.Normal;
import com.dian.diabetes.db.dao.Sport;
import com.dian.diabetes.dialog.AlertDialog;
import com.dian.diabetes.dialog.DayDialog;
import com.dian.diabetes.dialog.TimeDialog;
import com.dian.diabetes.dto.NormalDto;
import com.dian.diabetes.tool.CallBack;
import com.dian.diabetes.tool.CommonUtil;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.ProWidget;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 添加运动数据
 * @author longbh
 *
 */
public class SportAddFragment extends BasicFragmentDialog implements
		OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.save_btn)
	private ImageButton saveBtn;
	@ViewInject(id = R.id.delete)
	private ImageButton deleteBtn;
	@ViewInject(id = R.id.day)
	private LinearLayout dayView;
	@ViewInject(id = R.id.time)
	private LinearLayout timeView;
	@ViewInject(id = R.id.add_entry_time)
	private TextView entryTime;
	@ViewInject(id = R.id.add_entry_day)
	private TextView entryDay;
	@ViewInject(id = R.id.scroll)
	private ScrollView container;
	// 运动类型
	@ViewInject(id = R.id.sport_type)
	private RelativeLayout sportType;
	@ViewInject(id = R.id.sport_type_value)
	private TextView sportTypeValue;
	// 感受
	@ViewInject(id = R.id.sport_feel)
	private RelativeLayout sportFeel;
	@ViewInject(id = R.id.sport_feel_value)
	private TextView feelValue;
	// 心率
	@ViewInject(id = R.id.rate)
	private RelativeLayout rate;
	@ViewInject(id = R.id.rate_value)
	private EditText rateValue;
	// 时间
	@ViewInject(id = R.id.sport_time)
	private RelativeLayout sportTime;
	@ViewInject(id = R.id.sport_time_value)
	private EditText sportTimeValue;
	@ViewInject(id = R.id.remark)
	private EditText remark;
	@ViewInject(id = R.id.blood_input)
	private EditText totalView;
	@ViewInject(id = R.id.blood_progress)
	private ProWidget progress;
	@ViewInject(id = R.id.level)
	private TextView levelView;

	private SportActivity activity;
	private DayDialog dayDialog;
	private TimeDialog timeDialog;
	private Date selectDate;
	private DecimalFormat format;
	private SportFeelDialog feelDialog;
	private AlertDialog alert;
	
	private CallBack callBack;
	private SportBo sportBo;
	private InputMethodManager imm;
	private Sport sport;
	private float total = 0;
	private float unitValue = 0;
	private int heartRate = 0;
	private String strength = "";
	private String sportUnit = "";
	private boolean isAdd = true;
	private boolean chooseTool = false;
	private float suport = 3000;

	public static SportAddFragment getInstance(boolean isAdd,float suport) {
		SportAddFragment addFragment = new SportAddFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isAdd", isAdd);
		bundle.putFloat("suport", suport);
		addFragment.setArguments(bundle);
		return addFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (SportActivity) context;
		format = new DecimalFormat("00");
		sportBo = new SportBo(activity);
		isAdd = getArguments().getBoolean("isAdd");
		suport = getArguments().getFloat("suport");
		
		imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		feelDialog = new SportFeelDialog(activity);
		feelDialog.setCall(new SportFeelDialog.CallBack() {
			@Override
			public void callBack(int position, String model) {
				feelValue.setText(model);
				MapModel mapModel = feelDialog.getModel(position);
				if(heartRate == 0){
					strength = CommonUtil.getValue("strength" + mapModel.getStrength());
					levelView.setText(Html.fromHtml("卡路里含量" + Config.getSportLevelColor(mapModel.getStrength(), getResources())));
					unitValue = feelDialog.getValue(mapModel.getStrength());
					float time = 0;
					if(!CheckUtil.isNull(sportTimeValue.getText())){
						time = StringUtil.toFloat(sportTimeValue.getText());
					}
					double temp = unitValue * StringUtil.toFloat(time);
					total = (float) (((int)(temp * 10))/10.0f);
					progress.setValue(total, R.color.sport_color);
					totalView.setText(time * unitValue + "");
				}
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sport_impl, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		dayView.setOnClickListener(this);
		timeView.setOnClickListener(this);
		sportType.setOnClickListener(this);
		sportFeel.setOnClickListener(this);
		rate.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		sportTime.setOnClickListener(this);
		entryTime.setText(DateUtil.parseToString(selectDate, DateUtil.HHmm));
		entryDay.setText(DateUtil.parseToString(selectDate, DateUtil.yyyymmdd));
		// 让 ScrollView移动到顶端
		container.smoothScrollTo(0, 20);
		if (isAdd) {
			deleteBtn.setVisibility(View.GONE);
			sport = new Sport();
		}else{
			setSportView();
		}
		
		//监听运动时间修改
		sportTimeValue.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable edit) {
				double temp = unitValue * StringUtil.toFloat(edit.toString());
				total = (float) (((int)(temp * 10))/10.0f);
				progress.setValue(total, R.color.sport_color);
				totalView.setText(total + "");
			}
		});
		
		//监听心率修改
		rateValue.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable edit) {
				heartRate = StringUtil.toInt(edit + "");
				if(heartRate > 160){
					strength = "重度";
					levelView.setText(Html.fromHtml("卡路里含量" + Config.getSportLevelColor(3, getResources())));
					unitValue = feelDialog.getValue(3);
				}else if(heartRate > 140){
					strength = "中度";
					levelView.setText(Html.fromHtml("卡路里含量" + Config.getSportLevelColor(2, getResources())));
					unitValue = feelDialog.getValue(2);
				}else{
					levelView.setText(Html.fromHtml("卡路里含量" + Config.getSportLevelColor(1, getResources())));
					strength = "轻度";
					unitValue = feelDialog.getValue(1);
				}
				float time = 0;
				if(!CheckUtil.isNull(sportTimeValue.getText())){
					time = StringUtil.toFloat(sportTimeValue.getText());
				}
				double temp = unitValue * StringUtil.toFloat(time);
				total = (float) (((int)(temp * 10))/10.0f);
				progress.setValue(total, R.color.sport_color);
				totalView.setText(total + "");
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			dismiss();
			break;
		case R.id.save_btn:
			if (CheckUtil.isNull(sportTypeValue.getText())) {
				Toast.makeText(activity, "请选择运动类型", Toast.LENGTH_SHORT).show();
				return;
			}
			if (CheckUtil.isNull(sportTimeValue.getText())) {
				Toast.makeText(activity, "请输入运动时间", Toast.LENGTH_SHORT).show();
				return;
			}
			if (CheckUtil.isNull(feelValue.getText())) {
				Toast.makeText(activity, "请选择运动后感觉", Toast.LENGTH_SHORT).show();
				return;
			}
			saveSportData();
			dismiss();
			callBack.callBack();
			break;
		case R.id.day:
			openDayDialog();
			break;
		case R.id.time:
			openTimeDialog();
			break;
		case R.id.sport_type:
			showToolFragment();
			break;
		case R.id.sport_feel:
			if(!chooseTool){
				if(CheckUtil.isNull(sportTypeValue.getText())){
					Toast.makeText(activity, "请选择运动类型", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(activity, "请重新选择运动类型", Toast.LENGTH_SHORT).show();
				}
				return;
			}
			feelDialog.show();
			break;
		case R.id.rate:
			if(!chooseTool){
				if(CheckUtil.isNull(sportTypeValue.getText())){
					Toast.makeText(activity, "请选择运动类型", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(activity, "请重新选择运动类型", Toast.LENGTH_SHORT).show();
				}
				return;
			}
			rateValue.setEnabled(true);
			rateValue.requestFocus();
			rateValue.setSelection(rateValue.getText().length());
			imm.showSoftInput(rateValue,InputMethodManager.SHOW_FORCED);  
			break;
		case R.id.sport_time:
			sportTimeValue.setEnabled(true);
			sportTimeValue.requestFocus();
			imm.showSoftInput(sportTimeValue,InputMethodManager.SHOW_FORCED);  
			break;
		case R.id.delete:
			if(alert == null){
				alert = new AlertDialog(activity, "您确定要删除这条记录吗");
				alert.setCallBack(new AlertDialog.CallBack() {

					@Override
					public void cancel() {
					}

					@Override
					public void callBack() {
						sportBo.deleteLocal(sport);
						activity.getPreference().putBoolean(Preference.HAS_UPDATE,
								true);
						ContantsUtil.MULTI_UPDATE = false;
						ContantsUtil.TOTAL_EAT_UPDATE = false;
						if (callBack != null) {
							callBack.callBack();
						}
						dismiss();
					}
				});
			}
			alert.show();
			break;
		}
	}

	private void showToolFragment() {
		String tag = "show_tool_fragment";
		FragmentManager manager = context.getSupportFragmentManager();
		SportFragment tempFragment = (SportFragment) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = SportFragment.getInstance();
			tempFragment.setCallBack(new SportFragment.CallBack() {
				@Override
				public void callBack(List<NormalDto> lists,Normal normal,String sport) {
					sportUnit = sport;
					unitValue = 0;
					total = 0;
					progress.setValue(0, R.color.sport_color);
					totalView.setText("0");
					rateValue.setText("");
					feelValue.setText("");
					sportTypeValue.setText(normal.getName());
					feelDialog.setData(lists);
					chooseTool = true;
				}
			});
		}
		if (!tempFragment.isAdded()) {
			tempFragment.show(manager, tag);
		}
	}

	private void openTimeDialog() {
		if (timeDialog == null) {
			timeDialog = new TimeDialog(activity, "时间选择");
			timeDialog.setCallBack(new TimeDialog.CallBack() {
				@Override
				public boolean callBack(int hour, int mini) {
					selectDate.setHours(hour);
					selectDate.setMinutes(mini);
					if (selectDate.compareTo(new Date()) > 0) {
						Toast.makeText(context, R.string.toast_time_after,
								Toast.LENGTH_SHORT).show();
						return false;
					}
					entryTime.setText(format.format(hour) + ":"
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
					selectDate = calendar.getTime();
					if (selectDate.compareTo(new Date()) > 0) {
						Toast.makeText(context, R.string.toast_time_after,
								Toast.LENGTH_SHORT).show();
						return false;
					}
					entryDay.setText(year + "-" + format.format(month) + "-"
							+ format.format(day));
					return true;
				}
			});
		}
		dayDialog.show();
	}

	private void saveSportData() {
		sport.setSportName(sportTypeValue.getText()+"");
		if(heartRate != -1){
			sport.setHeart(heartRate);
		}
		sport.setMark(remark.getText()+"");
		sport.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
		sport.setSportTime(StringUtil.toInt(sportTimeValue.getText()));
		sport.setCreate_time(selectDate.getTime());
		sport.setUpdate_time(System.currentTimeMillis());
		sport.setSportUnit(sportUnit);
		sport.setDay(DateUtil.parseToString(selectDate, DateUtil.yyyyMMdd));
		sport.setStatus((short)0);
		sport.setSportFeel(feelValue.getText()+"");
		sport.setTotal(total);
		sport.setStrength(strength);
		sport.setSuport(suport);
		sportBo.saveUpdateSport(sport);
		activity.getPreference().putBoolean(Preference.HAS_UPDATE, true);
		ContantsUtil.MULTI_UPDATE = false;
		ContantsUtil.TOTAL_EAT_UPDATE = false;
	}
	
	private void setSportView(){
		sportUnit = sport.getSportUnit();
		if(!CheckUtil.isNull(sportUnit)){
			initUnitArray(sportUnit);
		}
		heartRate = sport.getHeart();
		total = sport.getTotal();
		unitValue = sport.getTotal()/(sport.getSportTime()*1.0f);
		Log.d("unitValue", total +""+ unitValue+"gg");
		progress.setValue(sport.getTotal(), R.color.sport_color);
		remark.setText(sport.getMark());
		sportTimeValue.setText(sport.getSportTime() + "");
		rateValue.setText(heartRate + "");
		feelValue.setText(sport.getSportFeel());
		totalView.setText(total + "");
		selectDate.setTime(sport.getCreate_time());
		sportTypeValue.setText(sport.getSportName());
		strength = sport.getStrength();
	}
	
	private void initUnitArray(String data){
		feelDialog.clear();
		JSONArray array;
		try {
			array = new JSONArray(data);
			for(int i=0;i<array.length();i++){
				NormalDto dto = new NormalDto();
				dto.of(array.getJSONObject(i));
				feelDialog.addModel(dto);
			}
			chooseTool = true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void setCallback(CallBack callBack){
		this.callBack = callBack;
	}
	
	public void setSport(Sport sport){
		this.sport = sport;
	}
	
	public void setDate(Date select){
		this.selectDate = select;
	}
}
