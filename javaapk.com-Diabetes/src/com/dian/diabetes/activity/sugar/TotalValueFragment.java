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
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.ValueWidget;
import com.dian.diabetes.widget.anotation.ViewInject;
/**
 * 血糖统计最大值/最小值界面,直接在内存中比较出最小值，最大值
 * @author hua
 *
 */
public class TotalValueFragment extends BaseFragment {

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

	private boolean type;
	private SugarTotalFragment pparentFragment;
	private float breakPre = 0, breakAfter = 0, lunchPre = 0, lunchAfter = 0,
			dinnerpre = 0, dinnerafter = 0, sleep = 0;
	private float preValue = 0, afaterValue = 0, allValue = 0;
	private DecimalFormat format;

	public static TotalValueFragment getInstance(boolean type) {
		TotalValueFragment fragment = new TotalValueFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("type", type);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = getArguments().getBoolean("type");
		if(type){
			ContantsUtil.TOTAL_HIGH_UPDATE = false;
		}else{
			ContantsUtil.TOTAL_LOW_UPDATE = false;
		}
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
		String label;
		if (type) {
			label = "最高值";
			if (!ContantsUtil.TOTAL_HIGH_UPDATE) {
				new DataTask().execute();
				ContantsUtil.TOTAL_HIGH_UPDATE = true;
			}else{
				setView();
			}
		} else {
			label = "最低值";
			if (!ContantsUtil.TOTAL_LOW_UPDATE) {
				new DataTask().execute();
				ContantsUtil.TOTAL_LOW_UPDATE = true;
			}else{
				setView();
			}
		}
		totalLabel.setText("总体" + label);
		preLabel.setText("餐前" + label);
		afterLabel.setText("餐后" + label);
	}

	private void setView() {
		float alignmentPre[] = { Config.getFloatPro("levelLow" + ContantsUtil.EAT_PRE),
				Config.getFloatPro("levelMid" + ContantsUtil.EAT_PRE),
				Config.getFloatPro("levelHigh" + ContantsUtil.EAT_PRE)};
		float alignmentAfter[] = { Config.getFloatPro("levelLow" + ContantsUtil.EAT_AFTER),
				Config.getFloatPro("levelMid" + ContantsUtil.EAT_AFTER),
				Config.getFloatPro("levelHigh" + ContantsUtil.EAT_AFTER)};
		breakfastPre.setValue(format, breakPre,Config.getBloodState(breakPre, alignmentPre));
		breakfastAfter.setValue(format, breakAfter,Config.getBloodState(breakAfter, alignmentAfter));
		launchPre.setValue(format, lunchPre,Config.getBloodState(lunchPre, alignmentPre));
		launchAfter.setValue(format, lunchAfter,Config.getBloodState(lunchAfter, alignmentAfter));
		dinnerPre.setValue(format, dinnerpre,Config.getBloodState(dinnerpre, alignmentPre));
		dinnerAfter.setValue(format, dinnerafter,Config.getBloodState(dinnerafter, alignmentAfter));
		sleepPre.setValue(format, sleep,Config.getBloodState(sleep, alignmentPre));

		pre.setValue(format, preValue,Config.getBloodState(preValue, alignmentPre));
		after.setValue(format, afaterValue,Config.getBloodState(afaterValue, alignmentPre));
		all.setValue(format, allValue,Config.getBloodState(allValue, alignmentPre));
	}

	private void loadData() {
		for (Diabetes diabetes : pparentFragment.getData()) {
			if (diabetes.getType() == ContantsUtil.BRECKFAST) {
				if (diabetes.getSub_type() == ContantsUtil.EAT_PRE) {
					if (type || breakPre == 0) {
						preValue = breakPre = Math.max(breakPre,
								diabetes.getValue());
					} else {
						breakPre = Math.min(breakPre, diabetes.getValue());
					}
				} else {
					if (type || breakAfter == 0) {
						afaterValue = breakAfter = Math.max(breakAfter,
								diabetes.getValue());
					} else {
						breakAfter = Math.min(breakAfter, diabetes.getValue());
					}
				}
			} else if (diabetes.getType() == ContantsUtil.LAUNCH) {
				if (diabetes.getSub_type() == ContantsUtil.EAT_PRE) {
					if (type || lunchPre == 0) {
						preValue = lunchPre = Math.max(lunchPre,
								diabetes.getValue());
					} else {
						lunchPre = Math.min(lunchPre, diabetes.getValue());
					}
				} else {
					if (type || lunchAfter == 0) {
						afaterValue = lunchAfter = Math.max(lunchAfter,
								diabetes.getValue());
					} else {
						lunchAfter = Math.min(lunchAfter, diabetes.getValue());
					}
				}
			} else if (diabetes.getType() == ContantsUtil.DINNER) {
				if (diabetes.getSub_type() == ContantsUtil.EAT_PRE) {
					if (type || dinnerpre == 0) {
						preValue = dinnerpre = Math.max(dinnerpre,
								diabetes.getValue());
					} else {
						dinnerpre = Math.min(dinnerpre, diabetes.getValue());
					}
				} else {
					if (type || dinnerafter == 0) {
						afaterValue = dinnerafter = Math.max(dinnerafter,
								diabetes.getValue());
					} else {
						dinnerafter = Math.min(dinnerafter, diabetes.getValue());
					}
				}
			} else {
				if (type || sleep == 0) {
					preValue = sleep = Math.max(sleep, diabetes.getValue());
				} else {
					sleep = Math.min(sleep, diabetes.getValue());
				}
			}
		}
		if (type) {
			preValue = StringUtil.max(breakPre,lunchPre,dinnerpre,sleep);
			afaterValue = StringUtil.max(breakAfter, lunchAfter,dinnerafter);
			allValue = StringUtil.max(preValue, afaterValue);
		} else {
			preValue = StringUtil.min(breakPre,lunchPre,dinnerpre,sleep);
			afaterValue = StringUtil.min(breakAfter, lunchAfter,dinnerafter);
			allValue = StringUtil.min(preValue, afaterValue);
		}
	}

	/**
	 * 刷新视图
	 */
	public void refreshData() {
		breakPre = 0;
		breakAfter = 0;
		sleep = 0;
		dinnerafter = 0;
		dinnerpre = 0;
		lunchPre = 0;
		lunchAfter = 0;
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
