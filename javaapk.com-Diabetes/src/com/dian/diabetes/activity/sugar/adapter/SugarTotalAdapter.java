package com.dian.diabetes.activity.sugar.adapter;

import java.util.List;

import com.dian.diabetes.activity.sugar.TotalChartFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

public class SugarTotalAdapter extends FragmentPagerAdapter {

	private List<String> data;

	public SugarTotalAdapter(FragmentManager fm, List<String> data) {
		super(fm);
		this.data = data;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		TotalChartFragment f = (TotalChartFragment) super.instantiateItem(
				container, position);
		f.setRepaint();
		return f;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Fragment getItem(int position) {
		return TotalChartFragment.getInstance(data.get(position), position);
	}

	public int getItemPosition(Object object) {
		return PagerAdapter.POSITION_NONE;
	}
}
