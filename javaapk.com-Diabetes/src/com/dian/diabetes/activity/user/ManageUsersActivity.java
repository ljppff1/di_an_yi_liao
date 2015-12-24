package com.dian.diabetes.activity.user;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.DialogLoading;
import com.dian.diabetes.tool.Preference;

/**
 * 类/接口注释
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月7日
 * 
 */
public class ManageUsersActivity extends BasicActivity implements
		OnClickListener {

	private BaseFragment curentFragment;
	private DialogLoading loading;
	private Preference prefernce;

	private long mid;
	private boolean isEdit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_users);
		loading = new DialogLoading(context);
		prefernce = Preference.instance(context);
		init();
		if(isEdit){
			replaceFragment("edit_users", MemberInfoEditFragment.getInstance(),
					false);
		}else{
			replaceFragment("manager_users", MemberInfoFragment.getInstance(),
					false);
		}
	}

	private void init() {
		Bundle bundle = getIntent().getExtras();
		mid = bundle.getLong("mid",-1);
		isEdit = bundle.getBoolean("isedit", false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			this.finish();
			break;
		}
	}

	public void replaceFragment(String tag, BaseFragment tempFragment,
			boolean isAdd) {
		curentFragment = tempFragment;
		FragmentTransaction tran = getSupportFragmentManager()
				.beginTransaction();
		tran.replace(R.id.manager_users_fragment, tempFragment, tag);
		if (!isAdd) {
			tran.addToBackStack(tag);
		}
		tran.commitAllowingStateLoss();
	}

	/**
	 * @see LoginAcitivity#onBackPressed()
	 */
	public void onBackPressed() {
		if (!curentFragment.onBackKeyPressed()) {
			finish();
		} else {
			if(getSupportFragmentManager().getBackStackEntryCount() == 1){
				finish();
			}else{
				curentFragment = MemberInfoFragment.getInstance();
				super.onBackPressed();
			}
		}
	}

	public void showLoading() {
		loading.show();
	}

	public void hideLoading() {
		if (loading.isShowing()) {
			loading.dismiss();
		}
	}

	public DialogLoading getLoading() {
		return loading;
	}

	public Preference getPrefernce() {
		return prefernce;
	}

	public long getMid() {
		return mid;
	}

	public void setMid(long mid) {
		this.mid = mid;
	}
}
