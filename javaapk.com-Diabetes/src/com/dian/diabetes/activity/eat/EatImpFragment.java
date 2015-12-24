package com.dian.diabetes.activity.eat;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
import com.dian.diabetes.activity.tool.EatFragment;
import com.dian.diabetes.db.EatBo;
import com.dian.diabetes.db.dao.Eat;
import com.dian.diabetes.db.dao.Normal;
import com.dian.diabetes.dialog.AlertDialog;
import com.dian.diabetes.dialog.DayDialog;
import com.dian.diabetes.dialog.TimeDialog;
import com.dian.diabetes.dto.EatNormalDto;
import com.dian.diabetes.tool.CallBack;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.ProWidget;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 饮食录入fragment弹出框
 * @author hua
 *
 */
public class EatImpFragment extends BasicFragmentDialog implements
		OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.save_btn)
	private Button saveBtn;
	@ViewInject(id = R.id.day)
	private LinearLayout dayView;
	@ViewInject(id = R.id.time)
	private LinearLayout timeView;
	@ViewInject(id = R.id.din_stage)
	private RelativeLayout dinnerStage;
	@ViewInject(id = R.id.food_name)
	private RelativeLayout foodName;
	@ViewInject(id = R.id.eat_weight)
	private RelativeLayout eatWeight;
	@ViewInject(id = R.id.remark)
	private EditText remark;
	@ViewInject(id = R.id.weight_value)
	private EditText weightInput;
	@ViewInject(id = R.id.add_entry_time)
	private TextView entryTime;
	@ViewInject(id = R.id.add_entry_day)
	private TextView entryDay;
	@ViewInject(id = R.id.cooking_value)
	private TextView cookTxt;
	@ViewInject(id = R.id.din_stage_value)
	private TextView dinStateValue;
	@ViewInject(id = R.id.food_value)
	private TextView foodNameValue;
	@ViewInject(id = R.id.scroll)
	private ScrollView container;
	@ViewInject(id = R.id.blood_progress)
	private ProWidget progress;
	@ViewInject(id = R.id.blood_input)
	private TextView inputValue;
	@ViewInject(id = R.id.level)
	private TextView levelView;
	@ViewInject(id = R.id.delete)
	private ImageButton deleteBtn;

	private EatActivity activity;
	private DinStageDialog stageDialog;
	// private CookingDialog cookingDialog;
	private DayDialog dayDialog;
	private TimeDialog timeDialog;
	private AlertDialog alert;

	private Date selectDate;
	private DecimalFormat format;
	private DecimalFormat totalFormat;
	private EatBo bo;
	private Eat eat;
	private int dinnerPosition = ContantsUtil.EAT_PRE;
	private CallBack back;
	private float unitValue = 0;
	private float total = 0;
	private boolean isAdd = true;
	private boolean chooseTool = false;
	private String caloreType = "";
	private String foodType = "";
	private String nutriType = "";
	private float suport = 3000;

	public static EatImpFragment getInstance(boolean isAdd,float suport) {
		EatImpFragment fragment = new EatImpFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isAdd", isAdd);
		bundle.putFloat("suport", suport);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (EatActivity) context;
		format = new DecimalFormat("00");
		totalFormat = new DecimalFormat("##0.0");
		bo = new EatBo(activity);
		isAdd = getArguments().getBoolean("isAdd");
		suport = getArguments().getFloat("suport", 3000);
		stageDialog = new DinStageDialog(activity);
		stageDialog.setCall(new DinStageDialog.CallBack() {
			@Override
			public void callBack(int position, String model) {
				dinStateValue.setText(model);
				dinnerPosition = stageDialog.getModel(position).getType();
			}
		});
		// cook
		// cookingDialog = new CookingDialog(activity);
		// cookingDialog.setCall(new CookingDialog.CallBack() {
		// @Override
		// public void callBack(int position, String model) {
		// MapModel mapModel = cookingDialog.getModel(position);
		// cookTxt.setText(model);
		// // caloreType = value.getGroup1();
		// // nutriType = value.getGroup3();
		// // foodType = value.getGroup2();
		// unitValue = mapModel.getPrice();
		// String weight = weightInput.getText() + "";
		// total = unitValue * StringUtil.toFloat(weight);
		// progress.setValue(total, R.color.eat_color);
		// inputValue.setText(total + "");
		// levelView.setText(Html.fromHtml("卡路里含量"
		// + Config.getEatLevelColor(mapModel.getHeatLevel(),
		// getResources())));
		// }
		// });
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_eat_impl, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		dayView.setOnClickListener(this);
		timeView.setOnClickListener(this);
		dinnerStage.setOnClickListener(this);
		foodName.setOnClickListener(this);
		eatWeight.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		entryTime.setText(DateUtil.parseToString(selectDate, DateUtil.HHmm));
		entryDay.setText(DateUtil.parseToString(selectDate, DateUtil.yyyymmdd));
		// 让 ScrollView移动到顶端
		container.smoothScrollTo(0, 20);
		// 初始化数据
		if (isAdd) {
			deleteBtn.setVisibility(View.GONE);
			eat = new Eat();
		} else {
			setEatView();
		}
		weightInput.addTextChangedListener(new TextWatcher() {

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
				double temp = unitValue * StringUtil.toFloat(edit.toString());
				total = (float) (((int)(temp * 10))/10.0f);
				progress.setValue(total, R.color.eat_color);
				inputValue.setText(totalFormat.format(temp));
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			dismiss();
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
						bo.deleteLocal(eat);
						activity.getPreference().putBoolean(
								Preference.HAS_UPDATE, true);
						ContantsUtil.EAT_MULTI_UPDATE = false;
						ContantsUtil.TOTAL_EAT_UPDATE = false;
						if (back != null) {
							back.callBack();
						}
						dismiss();
					}
				});
			}
			alert.show();
			break;
		case R.id.din_stage:
			stageDialog.show();
			break;
		// case R.id.cooking:
		// if (!chooseTool) {
		// Toast.makeText(activity, "请选选择饮食类型", Toast.LENGTH_SHORT).show();
		// return;
		// }
		// openCookingDialog();
		// break;
		case R.id.food_name:
			showToolFragment();
			break;
		case R.id.day:
			openDayDialog();
			break;
		case R.id.time:
			openTimeDialog();
			break;
		case R.id.eat_weight:
			weightInput.setEnabled(true);
			weightInput.requestFocus();
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(weightInput, InputMethodManager.SHOW_FORCED);
			break;
		case R.id.save_btn:
			if (CheckUtil.isNull(foodNameValue.getText())) {
				Toast.makeText(activity, "请选择食物", Toast.LENGTH_SHORT).show();
				return;
			}
			if (CheckUtil.isNull(weightInput.getText())) {
				Toast.makeText(activity, "食物重量不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (CheckUtil.isNull(cookTxt.getText())) {
				Toast.makeText(activity, "烹饪方法不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			saveEatData();
			back.callBack();
			dismiss();
			break;
		}
	}

	private void setEatView() {
		dinnerPosition = eat.getDinnerType();
		foodType = eat.getFoodType();
		caloreType = eat.getCaloreType();
		unitValue = eat.getTotal() / eat.getFoodWeight();
		progress.setValue(eat.getTotal(), R.color.eat_color);
		remark.setText(eat.getMark());
		dinStateValue.setText(stageDialog.getModel(eat.getDinnerType())
				.getValue());
		foodNameValue.setText(eat.getFoodName());
		weightInput.setText(eat.getFoodWeight() + "");
		cookTxt.setText(eat.getCookType());
		inputValue.setText(totalFormat.format(eat.getTotal()));
	}

	private void showToolFragment() {
		String tag = "show_tool_fragment";
		FragmentManager manager = context.getSupportFragmentManager();
		EatFragment tempFragment = (EatFragment) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = EatFragment.getInstance();
			tempFragment.setCallBack(new EatFragment.CallBack() {
				@Override
				public void callBack(List<EatNormalDto> lists, Normal normal) {
					foodNameValue.setText(normal.getName());
					// cookingDialog.setData(lists);
					// chooseTool = true;
					EatNormalDto dto = lists.get(0);
					cookTxt.setText(dto.cookingName);
					unitValue = dto.heat/100;
					caloreType = dto.heatLevel;
					foodType = dto.category;
					String weight = weightInput.getText() + "";
					double temp = unitValue * StringUtil.toFloat(weight);
					total = (float) (((int)(temp * 10))/10.0f);
					progress.setValue(total, R.color.eat_color);
					inputValue.setText(totalFormat.format(total));
					levelView.setText(Html.fromHtml("卡路里含量"
							+ Config.getEatLevelColor(dto.heatLevel,
									getResources())));
				}
			});
		}
		if (!tempFragment.isAdded()) {
			tempFragment.show(manager, tag);
		}
	}

	// private void openCookingDialog() {
	// cookingDialog.show();
	// }

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

	private void saveEatData() {
		eat.setFoodName(foodNameValue.getText() + "");
		eat.setMark(remark.getText() + "");
		eat.setCookType(cookTxt.getText() + "");
		eat.setFoodType(foodType);
		eat.setCaloreType(caloreType);
		eat.setNutriType(nutriType);
		eat.setDay(DateUtil.parseToString(selectDate, DateUtil.yyyyMMdd));
		eat.setDinnerType(dinnerPosition);
		eat.setCreate_time(selectDate.getTime());
		eat.setUpdate_time(System.currentTimeMillis());
		eat.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
		eat.setStatus(ContantsUtil.NO_SERVER);
		String weight = weightInput.getText() + "";
		if (CheckUtil.isNull(weight)) {
			Toast.makeText(activity, "请填写摄入食物重量", Toast.LENGTH_SHORT).show();
			return;
		}
		eat.setSurport(suport);
		eat.setFoodWeight(StringUtil.toFloat(weight));
		float totalValue = unitValue * eat.getFoodWeight();
		eat.setTotal(totalValue);
		bo.saveUpdateEat(eat);
		activity.getPreference().putBoolean(Preference.HAS_UPDATE, true);
		ContantsUtil.EAT_MULTI_UPDATE = false;
		ContantsUtil.TOTAL_EAT_UPDATE = false;
	}

	public void setEat(Eat eat) {
		this.eat = eat;
	}

	public void setCallback(CallBack back) {
		this.back = back;
	}
	
	public void setDate(Date select){
		this.selectDate = select;
	}
}
