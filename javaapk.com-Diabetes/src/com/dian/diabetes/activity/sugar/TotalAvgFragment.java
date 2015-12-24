package com.dian.diabetes.activity.sugar;

import java.text.DecimalFormat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.db.dao.Diabetes;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.widget.ValueWidget;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 平均值界面
 * @author hua
 *
 */
public class TotalAvgFragment extends BaseFragment {

	@ViewInject(id = R.id.total_label)
	private TextView totalLabel;
	@ViewInject(id = R.id.pre_label)
	private TextView preLabel;
	@ViewInject(id = R.id.after_label)
	private TextView afterLabel;

	@ViewInject(id = R.id.all)
	private ValueWidget all;
	@ViewInject(id = R.id.pre)
	private ValueWidget pre;
	@ViewInject(id = R.id.after)
	private ValueWidget after;
	@ViewInject(id = R.id.breadfast_pre)
	private ValueWidget breakfastPre;
	@ViewInject(id = R.id.breadfast_after)
	private ValueWidget breakfastAfter;
	@ViewInject(id = R.id.lunch_pre)
	private ValueWidget launchPre;
	@ViewInject(id = R.id.lunch_after)
	private ValueWidget launchAfter;
	@ViewInject(id = R.id.dinner_pre)
	private ValueWidget dinnerPre;
	@ViewInject(id = R.id.dinner_after)
	private ValueWidget dinnerAfter;
	@ViewInject(id = R.id.sleep_pre_pre)
	private ValueWidget sleepPre;

	private SugarTotalFragment pparentFragment;
	private float breakPre = 0, breakAfter = 0, lunchPre = 0, lunchAfter = 0,
			dinnerpre = 0, dinnerafter = 0, sleep = 0;
	private int breakP = 0, breakA = 0, lunchP = 0, lunchA = 0, dinnerp = 0,
			dinnera = 0, sleepP = 0;
	private DecimalFormat format;

	public static TotalAvgFragment getInstance() {
		TotalAvgFragment fragment = new TotalAvgFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ContantsUtil.TOTAL_AVG_UPDATE = false;
		pparentFragment = (SugarTotalFragment) getParentFragment()
				.getParentFragment();
		format = new DecimalFormat("##0.0");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_total_value, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		totalLabel.setText("总体平均");
		preLabel.setText("餐前平均");
		afterLabel.setText("餐后平均");
		if (!ContantsUtil.TOTAL_AVG_UPDATE) {
			new DataTask().execute();
			ContantsUtil.TOTAL_AVG_UPDATE = true;
		}else{
			setView();
		}
	}

	private void setView() {
		float alignmentPre[] = { Config.getFloatPro("levelLow" + ContantsUtil.EAT_PRE),
				Config.getFloatPro("levelMid" + ContantsUtil.EAT_PRE),
				Config.getFloatPro("levelHigh" + ContantsUtil.EAT_PRE)};
		float alignmentAfter[] = { Config.getFloatPro("levelLow" + ContantsUtil.EAT_AFTER),
				Config.getFloatPro("levelMid" + ContantsUtil.EAT_AFTER),
				Config.getFloatPro("levelHigh" + ContantsUtil.EAT_AFTER)};
		float temp = breakP == 0 ? 0 : breakPre / breakP;
		breakfastPre.setValue(format,temp,Config.getBloodState(temp, alignmentPre));
		temp = breakA == 0 ? 0 : breakAfter / breakA;
		breakfastAfter.setValue(format,temp,Config.getBloodState(temp, alignmentAfter));
		temp = lunchP == 0 ? 0 : lunchPre / lunchP;
		launchPre.setValue(format,temp,Config.getBloodState(temp, alignmentPre));
		temp = lunchA == 0 ? 0 : lunchAfter / lunchA;
		launchAfter.setValue(format,temp,Config.getBloodState(temp, alignmentAfter));
		temp = dinnerpre == 0 ? 0 : dinnerpre / dinnerp;
		dinnerPre.setValue(format,temp,Config.getBloodState(temp, alignmentPre));
		temp = dinnera == 0 ? 0 : dinnerafter / dinnera;
		dinnerAfter.setValue(format,temp,Config.getBloodState(temp, alignmentAfter));
		temp = sleepP == 0 ? 0 : sleep / sleepP;
		sleepPre.setValue(format,temp,Config.getBloodState(temp, alignmentPre));
		float preValue = breakPre + lunchPre + dinnerpre + sleep;
		int preDay = breakP + lunchP + dinnerp + sleepP;
		float afterValue = breakAfter + lunchAfter + dinnerafter;
		int afterDay = breakA + lunchA + dinnera;
		temp = preDay == 0 ? 0 : preValue / preDay;
		pre.setValue(format,temp,Config.getBloodState(temp, alignmentPre));
		temp = afterDay == 0 ? 0 : afterValue / afterDay;
		after.setValue(format,temp,Config.getBloodState(temp, alignmentAfter));
		temp = afterDay + preDay == 0 ? 0 : (preValue + afterValue)
				/ (afterDay + preDay);
		all.setValue(format,temp,Config.getBloodState(temp, alignmentPre));
	}

	private void loadData() {
		for (Diabetes diabetes : pparentFragment.getData()) {
			if (diabetes.getType() == ContantsUtil.BRECKFAST) {
				if (diabetes.getSub_type() == ContantsUtil.EAT_PRE) {
					breakPre += diabetes.getValue();
					breakP++;
				} else {
					breakAfter += diabetes.getValue();
					breakA++;
				}
			} else if (diabetes.getType() == ContantsUtil.LAUNCH) {
				if (diabetes.getSub_type() == ContantsUtil.EAT_PRE) {
					lunchPre += diabetes.getValue();
					lunchP++;
				} else {
					lunchAfter += diabetes.getValue();
					lunchA++;
				}
			} else if (diabetes.getType() == ContantsUtil.DINNER) {
				if (diabetes.getSub_type() == ContantsUtil.EAT_PRE) {
					dinnerpre += diabetes.getValue();
					dinnerp++;
				} else {
					dinnerafter += diabetes.getValue();
					dinnera++;
				}
			} else {
				sleep += diabetes.getValue();
				sleepP++;
			}
		}
	}

	/**
	 * 刷新视图
	 */
	public void refreshData() {
		breakPre = 0;
		breakP = 0;
		breakAfter = 0;
		breakA = 0;
		sleep = 0;
		sleepP = 0;
		dinnera = 0;
		dinnerafter = 0;
		dinnerpre = 0;
		dinnerp = 0;
		lunchPre = 0;
		lunchP = 0;
		lunchAfter = 0;
		lunchA = 0;
		new DataTask().execute();
	}
	
	private class DataTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {
			loadData();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			setView();
		}
	}
}
