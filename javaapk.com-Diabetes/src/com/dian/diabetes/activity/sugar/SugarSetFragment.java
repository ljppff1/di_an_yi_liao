package com.dian.diabetes.activity.sugar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.BasicFragmentDialog;
import com.dian.diabetes.db.PropertyBo;
import com.dian.diabetes.db.UserBo;
import com.dian.diabetes.db.dao.User;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 控糖目标设置
 * @author hua
 *
 */
public class SugarSetFragment extends BasicFragmentDialog implements
		OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.high_sugar)
	private EditText highSugar;
	@ViewInject(id = R.id.mid_sugar)
	private EditText midSugar;
	@ViewInject(id = R.id.low_sugar)
	private EditText lowSugar;
	@ViewInject(id = R.id.sugar_persent)
	private EditText sugarPersent;
	@ViewInject(id = R.id.save_btn)
	private ImageButton saceBtn;
	@ViewInject(id = R.id.set_default)
	private Button setDefault;
	@ViewInject(id = R.id.check_dinner)
	private Button eatPre;

	private BasicActivity activity;
	private CallBack callBack;
	private PropertyBo bo;
	private int curentType = ContantsUtil.EAT_PRE;
	private User user;
	private UserBo userBo;
	private MealsPopDialog mealsDialog;

	public static SugarSetFragment getInstance(int type) {
		SugarSetFragment fragment = new SugarSetFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("type", type);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bo = new PropertyBo(context);
		activity = (BasicActivity) getActivity();
		curentType = getArguments().getInt("type", ContantsUtil.EAT_PRE);
		userBo = new UserBo(activity);
		user = userBo.getUserByServerId(ContantsUtil.DEFAULT_TEMP_UID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_target_set, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		saceBtn.setOnClickListener(this);
		setDefault.setOnClickListener(this);
		eatPre.setOnClickListener(this);
		if (curentType == ContantsUtil.EAT_PRE) {
			eatPre.setText(R.string.eat_pre);
		} else {
			eatPre.setText(R.string.eat_after);
		}
		setValue(curentType + "");
	}

	private void setValue(String typeValue) {
		// 设置默认值
		sugarPersent.setText(Config.getProperty("diastaticValue") + "");
		highSugar.setText(Config.getProperty("levelHigh" + typeValue) + "");
		midSugar.setText(Config.getProperty("levelMid" + typeValue) + "");
		lowSugar.setText(Config.getProperty("levelLow" + typeValue) + "");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			dismiss();
			break;
		case R.id.save_btn:
			saveTarget();
			break;
		case R.id.set_default:
			setValue("Def" + curentType);
			break;
		case R.id.check_dinner:
			switchEatType(view);
			break;
		}
	}

	private void saveTarget() {
		if (CheckUtil.isNull(sugarPersent.getText())) {
			Toast.makeText(context, "糖化值不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		float persent = StringUtil.toFloat(sugarPersent.getText());
		if(persent <0 || persent >100){
			Toast.makeText(context, "请输入正确的糖化值", Toast.LENGTH_SHORT).show();
			return;
		}
		if (CheckUtil.isNull(highSugar.getText())) {
			Toast.makeText(context, "控糖目标值不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (CheckUtil.isNull(midSugar.getText())) {
			Toast.makeText(context, "控糖目标值不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (CheckUtil.isNull(lowSugar.getText())) {
			Toast.makeText(context, "控糖目标值不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		float highValue = StringUtil.toFloat(highSugar.getText());
		float midValue = StringUtil.toFloat(midSugar.getText());
		float lowValue = StringUtil.toFloat(lowSugar.getText());
		if (midValue <= lowValue) {
			Toast.makeText(context, "高血糖值需大于低血糖值", Toast.LENGTH_SHORT).show();
			return;
		}
		if (highValue <= midValue) {
			Toast.makeText(context, "高血糖值需大于低血糖值", Toast.LENGTH_SHORT).show();
			return;
		}
		// 保存数据
		Config.setProperty("diastaticValue", sugarPersent.getText() + "");
		bo.updateByKey("diastaticValue", ContantsUtil.DEFAULT_TEMP_UID,
				sugarPersent.getText() + "");

		Config.setProperty("levelHigh" + curentType, highValue + "");
		bo.updateByKey("levelHigh" + curentType, ContantsUtil.DEFAULT_TEMP_UID,
				highSugar.getText() + "");
		Config.setProperty("levelMid" + curentType, midValue + "");
		bo.updateByKey("levelMid" + curentType, ContantsUtil.DEFAULT_TEMP_UID,
				midSugar.getText() + "");
		Config.setProperty("levelLow" + curentType, lowValue + "");
		bo.updateByKey("levelLow" + curentType, ContantsUtil.DEFAULT_TEMP_UID,
				lowSugar.getText() + "");
		Preference.instance(activity).putBoolean(Preference.HAS_UPDATE, true);
		user.setUpdate_time(System.currentTimeMillis());
		userBo.updateUser(user);
		if (callBack != null) {
			callBack.callBack(sugarPersent.getText() + "",lowValue + "~" + midValue, curentType);
		}
		dismiss();
	}

	private void switchEatType(View parent) {
		if (mealsDialog == null) {
			mealsDialog = new MealsPopDialog(context);
			mealsDialog.setCallBack(new MealsPopDialog.CallBack() {
				@Override
				public void callBack(int model) {
					switch (model) {
					case 0:
						curentType = ContantsUtil.EAT_PRE;
						setValue(curentType + "");
						eatPre.setText(R.string.eat_pre);
						break;
					case 1:
						curentType = ContantsUtil.EAT_AFTER;
						setValue(curentType + "");
						eatPre.setText(R.string.eat_after);
						break;
					}
				}
			});
		}
		mealsDialog.showAsDropDown(parent);
	}

	public void setCallBack(CallBack callBack) {
		this.callBack = callBack;
	}

	public interface CallBack {
		void callBack(String persent,String target, int type);
	}
}
