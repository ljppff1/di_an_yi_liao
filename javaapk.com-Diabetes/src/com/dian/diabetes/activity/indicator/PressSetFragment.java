package com.dian.diabetes.activity.indicator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicFragmentDialog;
import com.dian.diabetes.db.PropertyBo;
import com.dian.diabetes.db.UserBo;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

public class PressSetFragment extends BasicFragmentDialog implements
		OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.in_target)
	private EditText closeIndicate;
	@ViewInject(id = R.id.out_target)
	private EditText openIndicate;
	@ViewInject(id = R.id.save_btn)
	private Button saceBtn;

	private IndicatorActivity activity;
	private CallBack callBack;
	private PropertyBo bo;
	private UserBo userBo;

	public static PressSetFragment getInstance() {
		PressSetFragment fragment = new PressSetFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bo = new PropertyBo(context);
		activity = (IndicatorActivity) getActivity();
		userBo = new UserBo(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_press_set, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		saceBtn.setOnClickListener(this);
		setValue();
	}

	private void setValue() {
		// 设置默认值
		closeIndicate.setText(Config.getProperty("highClosePress") + "");
		openIndicate.setText(Config.getProperty("highOpenPress") + "");
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
		}
	}

	private void saveTarget() {
		if (CheckUtil.isNull(openIndicate.getText())) {
			Toast.makeText(context, "控制目标值不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (CheckUtil.isNull(closeIndicate.getText())) {
			Toast.makeText(context, "控制目标值不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		float openValue = StringUtil.toFloat(openIndicate.getText());
		float closeValue = StringUtil.toFloat(closeIndicate.getText());
		// 保存数据
		Config.setProperty("highOpenPress", openValue + "");
		bo.updateByKey("highOpenPress", ContantsUtil.DEFAULT_TEMP_UID,
				openValue + "");
		Config.setProperty("highClosePress", closeValue + "");
		bo.updateByKey("highClosePress", ContantsUtil.DEFAULT_TEMP_UID,
				closeValue + "");
		activity.getPreference().putBoolean(Preference.HAS_UPDATE, true);
		ContantsUtil.curUser.setUpdate_time(System.currentTimeMillis());
		userBo.updateUser(ContantsUtil.curUser);
		if (callBack != null) {
			callBack.callBack(closeValue, openValue);
		}
		dismiss();
	}

	public void setCallBack(CallBack callBack) {
		this.callBack = callBack;
	}

	public interface CallBack {
		void callBack(float low, float high);
	}
}
