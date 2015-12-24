package com.dian.diabetes.activity.indicator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dian.diabetes.R;
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

public class PressTarget extends BasicFragmentDialog implements
		OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.high_indicate)
	private EditText highIndicate;
	@ViewInject(id = R.id.low_indicate)
	private EditText lowIndicate;
	@ViewInject(id = R.id.save_btn)
	private Button saceBtn;

	private IndicatorActivity activity;
	private CallBack callBack;
	private PropertyBo bo;
	private User user;
	private UserBo userBo;
	private String highKey;
	private String lowKey;
	private String unin;

	public static PressTarget getInstance(String highValue,String lowValue,String unin) {
		PressTarget fragment = new PressTarget();
		Bundle bundle = new Bundle();
		bundle.putString("highValue", highValue);
		bundle.putString("lowValue", lowValue);
		bundle.putString("unin", unin);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bo = new PropertyBo(context);
		highKey = getArguments().getString("highValue");
		lowKey = getArguments().getString("lowValue");
		unin = getArguments().getString("unin");
		activity = (IndicatorActivity) getActivity();
		userBo = new UserBo(activity);
		user = userBo.getUserByServerId(ContantsUtil.DEFAULT_TEMP_UID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_indicate_set, container,
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
		highIndicate.setText(Config.getProperty(highKey) + "");
		lowIndicate.setText(Config.getProperty(lowKey) + "");
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
		if (CheckUtil.isNull(highIndicate.getText())) {
			Toast.makeText(context, "控制目标值不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (CheckUtil.isNull(lowIndicate.getText())) {
			Toast.makeText(context, "控制目标值不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		float highValue = StringUtil.toFloat(highIndicate.getText());
		float lowValue = StringUtil.toFloat(lowIndicate.getText());
		if (highValue <= lowValue) {
			Toast.makeText(context, "高指标值需大于高指标值", Toast.LENGTH_SHORT).show();
			return;
		}
		// 保存数据
		Config.setProperty(highKey, highValue + "");
		bo.updateByKey(highKey, ContantsUtil.DEFAULT_TEMP_UID,highValue + "");
		Config.setProperty(lowKey, lowValue + "");
		bo.updateByKey(lowKey, ContantsUtil.DEFAULT_TEMP_UID,lowValue + "");
		activity.getPreference().putBoolean(Preference.HAS_UPDATE, true);
		user.setUpdate_time(System.currentTimeMillis());
		userBo.updateUser(user);
		if (callBack != null) {
			callBack.callBack(lowValue,highValue);
		}
		dismiss();
	}

	public void setCallBack(CallBack callBack) {
		this.callBack = callBack;
	}

	public interface CallBack {
		void callBack(float low,float high);
	}
}
