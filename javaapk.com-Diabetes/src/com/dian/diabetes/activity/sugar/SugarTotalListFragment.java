package com.dian.diabetes.activity.sugar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;

/**
 * 血糖统计，统计平均值最大值包住的fragment，这里主要控制他们的切换
 * @author hua
 *
 */
public class SugarTotalListFragment extends TotalBaseFragment {

	private int model = 0;

	private BaseFragment curentFragment;

	public static SugarTotalListFragment getInstance() {
		return new SugarTotalListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sugar_total_list,
				container, false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		setCurentPage(model);
	}

	public void setCurentPage(int model) {
		String tag;
		boolean isAdd = false;
		switch (model) {
		case 0:
			tag = "effect_count";
			curentFragment = (BaseFragment) getChildFragmentManager()
					.findFragmentByTag(tag);
			if (curentFragment == null) {
				curentFragment = TotalCountFragment.getInstance();
				isAdd = false;
			}
			replaceFragment(tag, curentFragment, isAdd);
			break;
		case 1:
			tag = "effect_avg_value";
			curentFragment = (BaseFragment) getChildFragmentManager()
					.findFragmentByTag(tag);
			if (curentFragment == null) {
				curentFragment = TotalAvgFragment.getInstance();
				isAdd = false;
			}
			replaceFragment(tag, curentFragment, isAdd);
			break;
		case 2:
			tag = "effect_max_value";
			curentFragment = (BaseFragment) getChildFragmentManager()
					.findFragmentByTag(tag);
			if (curentFragment == null) {
				curentFragment = TotalValueFragment.getInstance(true);
				isAdd = false;
			}
			replaceFragment(tag, curentFragment, isAdd);
			break;
		case 3:
			tag = "effect_min_value";
			curentFragment = (BaseFragment) getChildFragmentManager()
					.findFragmentByTag(tag);
			if (curentFragment == null) {
				curentFragment = TotalValueFragment.getInstance(false);
				isAdd = false;
			}
			replaceFragment(tag, curentFragment, isAdd);
			break;
		}
		this.model = model;
	}

	public void replaceFragment(String tag, Fragment tempFragment, boolean isAdd) {
		FragmentTransaction tran = getChildFragmentManager().beginTransaction();
		tran.replace(R.id.effect_layout, tempFragment, tag);
		if (!isAdd) {
			tran.addToBackStack(tag);
		}
		tran.commitAllowingStateLoss();
	}

	@Override
	public void notifyData() {
		if (curentFragment instanceof TotalCountFragment) {
			((TotalCountFragment) curentFragment).refreshData();
			Log.i("refresh", "TotalCountFragment");
		} else if (curentFragment instanceof TotalAvgFragment) {
			((TotalAvgFragment) curentFragment).refreshData();
			Log.i("refresh", "TotalValueFragment");
		}
	}

	public int getModel() {
		return model;
	}

}
