package com.dian.diabetes.activity.indicator;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.TranLoading;
import com.dian.diabetes.db.dao.Indicate;
import com.dian.diabetes.dto.IndicateDto;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 指标管理
 * 
 * @author longbh
 * 
 */
public class IndicatorActivity extends BasicActivity implements
		OnCheckedChangeListener {

	@ViewInject(id = R.id.tab_bottom)
	private RadioGroup tabBottom;

	private BaseFragment curentFragment;
	private TranLoading loading;

	private Preference preference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index_manager);
		initActivity();
		preference = Preference.instance(context);
		loading = new TranLoading(context);
	}

	private void initActivity() {
		tabBottom.setOnCheckedChangeListener(this);
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			replaceFragment("real_indicator", RealFragment.getInstance(bundle.getString("code"),bundle.getString("type")), false);
		}else{
			toRealFragment();
			//replaceFragment("visual_indicator", VisualFragment.getInstance(), false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		curentFragment.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int id) {
		switch (id) {
		case R.id.visual_indicator:
			startIndicateList();
			break;
		case R.id.real_indicator:
			toRealFragment();
			break;
		}
	}

	private void replaceFragment(String tag, BaseFragment tempFragment,
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

	public void toRealFragment() {
		boolean isAdd = true;
		String tag = "real_indicator";
		BaseFragment tempFragment = (BaseFragment) getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = RealFragment.getInstance("","");
			isAdd = false;
		}
		replaceFragment(tag, tempFragment, isAdd);
	}

	/**
	 * 查看指标列表
	 */
	public void startIndicateList() {
		String tag = "visual_indicator";
		BaseFragment tempFragment = (BaseFragment) getSupportFragmentManager()
				.findFragmentByTag(tag);
		boolean isAdd = true;
		if (tempFragment == null) {
			tempFragment = VisualFragment.getInstance();
			isAdd = false;
		}
		replaceFragment(tag, tempFragment, isAdd);
	}

	/**
	 * 查看指标详情
	 */
	public void startIndicateDetail(long id, String key, String union,
			String name, Map<String, Indicate> maps) {
		String tag = "visual_list";
		boolean isAdd = true;
		DetailFragment tempFragment = (DetailFragment) getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = DetailFragment.getInstance(id, key, union, name);
			isAdd = false;
		} else {
			tempFragment.setModel(id, key, union, name);
			tempFragment.setCur();
		}
		tempFragment.setDataMap(maps);
		replaceFragment(tag, tempFragment, isAdd);
	}

	/**
	 * 查看实验室指标历史记录
	 */
	public void startRealDetail(String union, String name, float max,
			float min, boolean isText,float value,List<IndicateDto> maps) {
		String tag = "real_history_list";
		boolean isAdd = true;
		RealDetailFragment tempFragment = (RealDetailFragment) getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = RealDetailFragment
					.getInstance(union, name, max, min,isText,value);
			isAdd = false;
		} else {
			tempFragment.setModel(union, name, max, min,isText,value);
			tempFragment.setCur();
		}
		tempFragment.setDataMap(maps);
		replaceFragment(tag, tempFragment, isAdd);
	}

	public Preference getPreference() {
		return preference;
	}

	public void show() {
		loading.show();
	}

	public void hide() {
		loading.dismiss();
	}

	public void onBackPressed() {
		if (!curentFragment.onBackKeyPressed()) {
			finish();
		}
	}
}