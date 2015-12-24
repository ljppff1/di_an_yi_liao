package com.dian.diabetes.activity.assess;

import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.dialog.NumberDialog;
import com.dian.diabetes.dialog.WheelCallBack;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/*
 * 已经废弃
 */
public class FirstFragment extends BaseFragment implements OnClickListener {

	@ViewInject(id = R.id.height)
	private TextView heightView;
	@ViewInject(id = R.id.weight)
	private TextView weightView;
	@ViewInject(id = R.id.yaowei)
	private TextView yaoweiView;
	@ViewInject(id = R.id.strong_content)
	private TextView strongView;

	// conroller
	@ViewInject(id = R.id.height_con)
	private RelativeLayout heightCon;
	@ViewInject(id = R.id.weight_con)
	private RelativeLayout weightCon;
	@ViewInject(id = R.id.yaowei_con)
	private RelativeLayout yaoweiCon;
	@ViewInject(id = R.id.strong_con)
	private RelativeLayout strongCon;

	private NumberDialog weightDialog;
	private NumberDialog heightDialog;
	private NumberDialog waistDialog;
	private StrongDialog strongDialog;

	private AssessActivity activity;

	public static FirstFragment getInstance() {
		FirstFragment fragment = new FirstFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (AssessActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_assess_normal,
				container, false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		heightCon.setOnClickListener(this);
		weightCon.setOnClickListener(this);
		yaoweiCon.setOnClickListener(this);
		strongCon.setOnClickListener(this);
		heightView.setText(activity.getKeyStr("height"));
		weightView.setText(activity.getKeyStr("weight"));
		yaoweiView.setText(activity.getKeyStr("yaowei"));
		strongView.setText(activity.getKeyStr("strong"));
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.height_con:
			if (heightDialog == null) {
				heightDialog = new NumberDialog(activity, new WheelCallBack() {
					@Override
					public void item(int value) {
						heightView.setText(value + "");
					}
				}, 200, 20, "cm", "身高");
			}
			heightDialog.show();
			break;
		case R.id.weight_con:
			if (weightDialog == null) {
				weightDialog = new NumberDialog(activity, new WheelCallBack() {
					@Override
					public void item(int value) {
						weightView.setText(value + "");
					}
				}, 200, 20, "kg", "体重");
			}
			weightDialog.show();
			break;
		case R.id.strong_con:
			if (strongDialog == null) {
				strongDialog = new StrongDialog(activity);
				strongDialog.setCall(new StrongDialog.CallBack() {
					@Override
					public void callBack(int position, String model) {
						strongView.setText(model);
					}
				});
			}
			strongDialog.show();
			break;
		case R.id.yaowei_con:
			if (waistDialog == null) {
				waistDialog = new NumberDialog(activity, new WheelCallBack() {
					@Override
					public void item(int value) {
						yaoweiView.setText(value + "");
					}
				}, 200, 20, "cm", "腰围");
			}
			waistDialog.show();
			break;
		}
	}

	public boolean onBackKeyPressed() {
		activity.finish();
		return true;
	}

	public boolean saveCache(Map<String, Object> data) {
		if (CheckUtil.isNull(heightView.getText())) {
			Toast.makeText(activity, "身高不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (CheckUtil.isNull(weightView.getText())) {
			Toast.makeText(activity, "体重不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (CheckUtil.isNull(yaoweiView.getText())) {
			Toast.makeText(activity, "腰围不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (CheckUtil.isNull(strongView.getText())) {
			Toast.makeText(activity, "强度不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		data.put("height", heightView.getText());
		data.put("weight", weightView.getText());
		data.put("yaowei", yaoweiView.getText());
		data.put("strong", strongView.getText());
		return true;
	}
}
