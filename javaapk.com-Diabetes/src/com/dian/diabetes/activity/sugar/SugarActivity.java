package com.dian.diabetes.activity.sugar;

import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.widget.anotation.ViewInject;

public class SugarActivity extends BasicActivity implements
		OnCheckedChangeListener {

	@ViewInject(id = R.id.tab_bottom)
	private RadioGroup tabBottom;

	private BaseFragment curentFragment;
	private Preference preference;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sugar_manager);
		preference = Preference.instance(context);
		initActivity();
		replaceFragment("sugar_entry", SugarEntryFragment.getInstance(), false);
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
		case R.id.entry:
			tag = "sugar_entry";
			tempFragment = (BaseFragment) getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (tempFragment == null) {
				tempFragment = SugarEntryFragment.getInstance();
				isAdd = false;
			}
			replaceFragment(tag, tempFragment, isAdd);
			break;
		case R.id.result:
			tag = "sugar_result";
			tempFragment = (BaseFragment) getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (tempFragment == null) {
				tempFragment = SugarEffectFragment.getInstance();
				isAdd = false;
			}
			replaceFragment(tag, tempFragment, isAdd);
			break;
		case R.id.count:
			tag = "sugar_total";
			tempFragment = (BaseFragment) getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (tempFragment == null) {
				tempFragment = SugarTotalFragment.getInstance();
				isAdd = false;
			}
			replaceFragment(tag, tempFragment, isAdd);
			break;
		}
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

	public void unCheckAll() {
		tabBottom.check(R.id.up_check);
	}

	public void checkRadio(int id) {
		tabBottom.check(id);
	}
	
	public Preference getPreference(){
		return preference;
	}

	public void setSugarTotal(int type) {
	}
}
