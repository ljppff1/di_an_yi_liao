package com.dian.diabetes.activity.eat;

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

/**
 * 饮食入口activity
 * @author hua
 *
 */
public class EatActivity extends BasicActivity implements
		OnCheckedChangeListener {

	@ViewInject(id = R.id.tab_bottom)
	private RadioGroup tabBottom;

	private BaseFragment curentFragment;
	private Preference preference;
	private TranLoading loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eat_layout);
		preference = Preference.instance(context);
		initActivity();
		replaceFragment("data_view", DataViewFragment.getInstance(), false);
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
				tempFragment = DataViewFragment.getInstance();
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
				tempFragment = CaloriesFragment.getInstance();
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
	
	public void show(){
		loading.show();
	}
	
	public void hide(){
		loading.dismiss();
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
