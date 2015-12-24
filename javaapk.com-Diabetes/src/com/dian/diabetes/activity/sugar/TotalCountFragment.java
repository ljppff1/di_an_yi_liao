package com.dian.diabetes.activity.sugar;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.db.dao.Diabetes;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.ValueWidget;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 血糖测量次数的改变
 * @author hua
 *
 */
public class TotalCountFragment extends BaseFragment {

	@ViewInject(id = R.id.test_num)
	private TextView testNum;
	@ViewInject(id = R.id.target_num)
	private TextView targetNum;
	@ViewInject(id = R.id.target_parent)
	private TextView targetPesent;
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
	private DecimalFormat format;
	private int total = 0, targetnum = 0;
	private Map<String, Integer> data;

	public static TotalCountFragment getInstance() {
		TotalCountFragment fragment = new TotalCountFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ContantsUtil.TOTAL_COUNT_UPDATE = false;
		pparentFragment = (SugarTotalFragment) getParentFragment()
				.getParentFragment();
		format = new DecimalFormat("##0.00");
		data = new HashMap<String, Integer>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_total_count, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		if (!ContantsUtil.TOTAL_COUNT_UPDATE) {
			new DataTask().execute();
			ContantsUtil.TOTAL_COUNT_UPDATE = true;
		}else{
			refreshView();
		}
	}

	private void refreshView() {
		testNum.setText(total + "");
		targetNum.setText(targetnum + "");
		if (total == 0) {
			targetPesent.setText(0 + "%");
		} else {
			targetPesent.setText(format.format(targetnum * 1.0f / total * 100) + "%");
		}
		breakfastPre.setCount(StringUtil.toCount(data.get("diabetes"
				+ ContantsUtil.BRECKFAST + ContantsUtil.EAT_PRE)));
		breakfastAfter.setCount(StringUtil.toCount(data.get("diabetes"
				+ ContantsUtil.BRECKFAST + ContantsUtil.EAT_AFTER)));
		launchPre.setCount(StringUtil.toCount(data.get("diabetes"
				+ ContantsUtil.LAUNCH + ContantsUtil.EAT_PRE)));
		launchAfter.setCount(StringUtil.toCount(data.get("diabetes"
				+ ContantsUtil.LAUNCH + ContantsUtil.EAT_AFTER)));
		dinnerPre.setCount(StringUtil.toCount(data.get("diabetes"
				+ ContantsUtil.DINNER + ContantsUtil.EAT_PRE)));
		dinnerAfter.setCount(StringUtil.toCount(data.get("diabetes"
				+ ContantsUtil.DINNER + ContantsUtil.EAT_AFTER)));
		sleepPre.setCount(StringUtil.toCount(data.get("diabetes"
				+ ContantsUtil.SLEEP_PRE + ContantsUtil.EAT_PRE)));
	}

	private void countData() {
		Log.e("size", pparentFragment.getData().size() + "");
		for (Diabetes diabetes : pparentFragment.getData()) {
			String key = "diabetes" + diabetes.getType()
					+ diabetes.getSub_type();
			Integer item = data.get(key);
			if (item == null) {
				item = 0;
			}
			item++;
			data.put(key, item);
			total++;
			if (diabetes.getLevel() == ContantsUtil.MID_DIABETES) {
				targetnum++;
			}
		}
	}

	/**
	 * 刷新view
	 */
	public void refreshData() {
		total = 0;
		targetnum = 0;
		data.clear();
		new DataTask().execute();
	}
	
	private class DataTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {
			countData();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			refreshView();
		}
	}
}
