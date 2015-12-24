package com.dian.diabetes.activity.sport;

import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.TranLoading;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.widget.anotation.ViewInject;

public class SportActivity extends BasicActivity implements
		OnCheckedChangeListener {

	@ViewInject(id = R.id.tab_bottom)
	private RadioGroup tabBottom;

	private BaseFragment curentFragment;
	private Preference preference;
	private TranLoading loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_layout);
		preference = Preference.instance(context);
		initActivity();
		replaceFragment("data_view", DateViewFragment.getInstance(), false);
		loading = new TranLoading(context);
	}

	private void initActivity() {
		tabBottom.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int id) {
		String tag;
		boolean isAdd = true;
		BaseFragment tempFragment;
		switch (id) {
		case R.id.data_view:
			tag = "data_view";
			tempFragment = (BaseFragment) getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (tempFragment == null) {
				tempFragment = DateViewFragment.getInstance();
				isAdd = false;
			}
			replaceFragment(tag, tempFragment, isAdd);
			break;
		case R.id.sugest_plan:
			tag = "sugest_plan";
			tempFragment = (BaseFragment) getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (tempFragment == null) {
				tempFragment = PlanFragment.getInstance();
				isAdd = false;
			}
			replaceFragment(tag, tempFragment, isAdd);
			break;
		case R.id.data_total:
			tag = "data_total";
			tempFragment = (BaseFragment) getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (tempFragment == null) {
				tempFragment = SportTotalFragment.getInstance();
				isAdd = false;
			}
			replaceFragment(tag, tempFragment, isAdd);
			break;
		}
	}
	
	public void show(){
		loading.show();
	}
	
	public void hide(){
		loading.dismiss();
	}

	public void replaceFragment(String tag, BaseFragment tempFragment,
			boolean isAdd) {
		curentFragment = tempFragment;
		FragmentTransaction tran = getSupportFragmentManager()
				.beginTransaction();
		tran.replace(R.id.content_fragment, tempFragment, tag);
		if (!isAdd) {
			tran.addToBackStack(tag);
		}
		tran.commitAllowingStateLoss();
	}

	public void onBackPressed() {
		if (!curentFragment.onBackKeyPressed()) {
			finish();
		}
	}
	
	public Preference getPreference(){
		return preference;
	}
}
