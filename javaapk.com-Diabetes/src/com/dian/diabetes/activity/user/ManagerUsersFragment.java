package com.dian.diabetes.activity.user;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dian.diabetes.MainActivity;
import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.db.UserInfoBo;
import com.dian.diabetes.db.dao.UserInfo;
import com.dian.diabetes.request.HttpContants;
import com.dian.diabetes.request.LoginRegisterAPI;
import com.dian.diabetes.request.UserDataAPI;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.utils.HttpServiceUtil.CallBack;
import com.dian.diabetes.widget.anotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 类/接口注释
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月8日
 * 
 */
public class ManagerUsersFragment extends BaseFragment implements
		OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.member_btn)
	private Button memberBtn;
	@ViewInject(id = R.id.mydata_btn)
	private Button mydataBtn;
	@ViewInject(id = R.id.logout_btn)
	private Button logoutBtn;
	@ViewInject(id = R.id.name)
	private TextView nameText;
	@ViewInject(id = R.id.photo)
	private ImageView photo;

	private ManageUsersActivity activity;
	private BaseFragment tempFragment;

	private CallBack logoutCallback;

	private UserInfoBo userInfoBo;
	private DisplayImageOptions options;

	public static ManagerUsersFragment getInstance() {
		return new ManagerUsersFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (ManageUsersActivity) context;
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.photo_default)
				.showImageForEmptyUri(R.drawable.photo_default)
				.showImageOnLoading(R.drawable.photo_default)
				.showImageOnFail(R.drawable.photo_default).cacheInMemory(true)
				.cacheOnDisc(true).build();
		// 初始化加载图片的尺寸
		initCallBack();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_manage_users, container,
				false);
		fieldView(view);
		init();
		initUserInfo();
		return view;

	}

	private void init() {
		backBtn.setOnClickListener(this);
		memberBtn.setOnClickListener(this);
		mydataBtn.setOnClickListener(this);
		logoutBtn.setOnClickListener(this);

		userInfoBo = new UserInfoBo(activity);
	}

	private void initUserInfo() {

		// 从数据库加载数据
		Long mid = activity.getMid();

		UserInfo uInfo = userInfoBo.getUinfoByMid(mid);

		// 从数据库获取成功
		if (uInfo != null && uInfo.getAvatar() != null) {
			ImageLoader.getInstance().displayImage(uInfo.getAvatar(), photo, options);
		} else {
			photo.setImageResource(R.drawable.user_icon_unkown);
		}
		if (uInfo != null && uInfo.getNick_name() != null) {
			nameText.setText(uInfo.getNick_name());
		} else {
			nameText.setText("");
		}

	}

	private void initCallBack() {

		logoutCallback = new CallBack() {

			@Override
			public void callback(String json) {
				activity.hideLoading();
				// 无论什么状态都可以注销用户
				// mid
				activity.getPrefernce().remove(Preference.USER_ID);
				ContantsUtil.DEFAULT_TEMP_UID = "";
				// uid
				activity.getPrefernce().remove(ContantsUtil.USER_ID);
				// sessionId
				activity.getPrefernce().remove(ContantsUtil.USER_SESSIONID);
				HttpServiceUtil.sessionId = "";

				activity.startActivity(null, MainActivity.class);
				activity.finish();
				ToastTool.showMemberStatus(HttpContants.LOGOUT_SUCCESS,
						activity);
			}
		};
	}

	@Override
	public void onClick(View v) {
		boolean isAdd = true;
		String tag;
		switch (v.getId()) {
		case R.id.back_btn:
			context.onBackPressed();
			break;
		case R.id.member_btn:
			tag = "member_list";
			isAdd = true;
			tempFragment = (BaseFragment) context.getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (tempFragment == null) {
				tempFragment = MemberListFragment.getInstance();
				isAdd = false;
			}
			activity.replaceFragment(tag, tempFragment, isAdd);
			break;
		case R.id.mydata_btn:
			tag = "member_info";
			isAdd = true;
			tempFragment = (BaseFragment) context.getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (tempFragment == null) {
				tempFragment = MemberInfoFragment.getInstance();
				isAdd = false;
			}
			activity.replaceFragment(tag, tempFragment, isAdd);
			break;
		case R.id.logout_btn:
			activity.getLoading().setDialogLabel(
					getText(R.string.loging_out) + "");
			activity.showLoading();
			LoginRegisterAPI.logout(null, activity, logoutCallback);
			break;
		}
	}

}
