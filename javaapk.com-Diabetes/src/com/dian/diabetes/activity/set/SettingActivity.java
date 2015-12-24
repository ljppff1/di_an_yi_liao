package com.dian.diabetes.activity.set;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.TranLoading;
import com.dian.diabetes.activity.user.ManageUsersActivity;
import com.dian.diabetes.db.dao.UserInfo;
import com.dian.diabetes.dialog.AlertDialog;
import com.dian.diabetes.request.LoginRegisterAPI;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.tool.ReadAreaFile;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.CircleImageView;
import com.dian.diabetes.widget.anotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends BasicActivity implements OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.about_us)
	private RelativeLayout aboutUs;
	@ViewInject(id = R.id.help)
	private RelativeLayout help;
	@ViewInject(id = R.id.check_update)
	private RelativeLayout checkUpdate;
	@ViewInject(id = R.id.exit_user_login)
	private Button logout;
	@ViewInject(id = R.id.photo)
	private CircleImageView photo;
	@ViewInject(id = R.id.user_info)
	private RelativeLayout userInfo;
	@ViewInject(id = R.id.user_name)
	private TextView name;
	@ViewInject(id = R.id.area)
	private TextView area;

	private AlertDialog alert;
	private TranLoading loading;
	private Preference preference;
	private DisplayImageOptions options;
	private Map<String, String> maps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_layout);
		loading = new TranLoading(context);
		preference = Preference.instance(context);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.photo_default)
				.showImageForEmptyUri(R.drawable.photo_default)
				.showImageOnLoading(R.drawable.photo_default)
				.showImageOnFail(R.drawable.photo_default).cacheInMemory(true)
				.cacheOnDisc(true).build();
		initActivity();
	}

	private void initActivity() {
		backBtn.setOnClickListener(this);
		aboutUs.setOnClickListener(this);
		checkUpdate.setOnClickListener(this);
		logout.setOnClickListener(this);
		help.setOnClickListener(this);
		userInfo.setOnClickListener(this);
		maps = new ReadAreaFile(context).readArea();
//		UserInfo uInfo = ContantsUtil.curInfo;
//		name.setText(uInfo.getNick_name());
//		Map<String, String> maps = new ReadAreaFile(context).readArea();
//		String areaTxt = "";
//		if (uInfo.getProvince() != null && uInfo.getProvince() != -1) {
//			areaTxt += maps.get(uInfo.getProvince() + "");
//		}
//		if (uInfo.getProvince() != null && uInfo.getCity() != -1) {
//			areaTxt += maps.get(uInfo.getCity() + "");
//		}
//		area.setText(areaTxt);
		//ImageLoader.getInstance().displayImage(uInfo.getAvatar(), photo, options);
	}
	
	public void onStart() {
		super.onStart();
		if (!CheckUtil.isNull(HttpServiceUtil.sessionId)
				&& !CheckUtil.isNull(ContantsUtil.DEFAULT_TEMP_UID)) {
			UserInfo uInfo = ContantsUtil.curInfo;
			name.setText(uInfo.getNick_name());
			String areaTxt = "";
			if (uInfo.getProvince() != null && uInfo.getProvince() != -1) {
				areaTxt += maps.get(uInfo.getProvince() + "");
			}
			if (uInfo.getProvince() != null && uInfo.getCity() != -1) {
				areaTxt += maps.get(uInfo.getCity() + "");
			}
			area.setText(areaTxt);
			ImageLoader.getInstance().displayImage(ContantsUtil.curInfo.getAvatar(), photo, options);
		} else {
			photo.setImageResource(R.drawable.user_icon_unkown);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.about_us:
			showAboutDialog();
			break;
		case R.id.check_update:
			checkUpdate();
			break;
		case R.id.exit_user_login:
			logout();
			break;
		case R.id.help:
			showHelpDialog();
			break;
		case R.id.user_info:
			Bundle bundle = new Bundle();
			bundle.putLong("mid",
					StringUtil.toLong(ContantsUtil.DEFAULT_TEMP_UID));
			startActivity(bundle, ManageUsersActivity.class);
			break;
		}
	}

	/**
	 * 打开关于
	 */
	private void showAboutDialog() {
		String tag = "about_dialog";
		FragmentManager manager = context.getSupportFragmentManager();
		AboutFragment fragment = (AboutFragment) manager.findFragmentByTag(tag);
		if (fragment == null) {
			fragment = AboutFragment.getInstance();
		}
		fragment.show(manager, tag);
	}

	/**
	 * 打开帮助
	 */
	private void showHelpDialog() {
		startActivity(null, HelpActivity.class);
	}

	private void checkUpdate() {
		loading.show();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("device", 0);
		params.put("current", ContantsUtil.currentVesion);
		HttpServiceUtil.request(ContantsUtil.UPDATE_CHECK_URL, "get", params,
				new HttpServiceUtil.CallBack() {
					@Override
					public void callback(String json) {
						loading.dismiss();
						try {
							JSONObject jsonObj = new JSONObject(json);
							if (CheckUtil.checkStatusOk(jsonObj
									.getInt("status"))) {
								if (!jsonObj.has("data")) {
									Toast.makeText(context, "已经是最新版本",
											Toast.LENGTH_SHORT).show();
								} else {
									JSONObject dataJson = jsonObj
											.getJSONObject("data");
									String url = dataJson.getString("url");
									preference.putString(Preference.UPDATE_URL,
											url);
									showUpdateDialog(url);
								}
							} else {
								Toast.makeText(context, "检查版本失败",
										Toast.LENGTH_SHORT).show();
							}
							preference.putLong(Preference.UPDATE_TIME,
									System.currentTimeMillis());
						} catch (JSONException e) {
							e.printStackTrace();
						}

						//
						// if ("".equals(json) || json == null) {
						// Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT)
						// .show();
						// } else {
						// int serverVesion = 0;
						// try {
						// serverVesion = StringUtil.toInt(json);
						// } catch (Exception e) {
						// serverVesion = 0;
						// }
						// if (ContantsUtil.currentVesion < serverVesion) {
						// preference.putBoolean(Preference.UPDATE_STATE,true);
						// showUpdateDialog(ContantsUtil.DOWNLOAD_URL);
						// } else {
						// Toast.makeText(context, "已经是最新版本",
						// Toast.LENGTH_SHORT).show();
						// }
						// preference.putLong(Preference.UPDATE_TIME,
						// System.currentTimeMillis());
						// }
					}
				});
	}

	private void showUpdateDialog(final String url) {
		alert = new AlertDialog(context, "有新版本，您确定要更新吗");
		alert.setCallBack(new AlertDialog.CallBack() {
			@Override
			public void cancel() {
			}

			@Override
			public void callBack() {
				Uri uri = Uri.parse(url);
				Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(downloadIntent);
			}
		});
		alert.show();
	}

	private void logout() {
		loading.show();
		LoginRegisterAPI.logout(null, this, new HttpServiceUtil.CallBack() {

			@Override
			public void callback(String json) {
				loading.dismiss();
				HttpServiceUtil.sessionId = null;
				ContantsUtil.curUser = null;
				ContantsUtil.curInfo = null;
				ContantsUtil.DEFAULT_TEMP_UID = null;
				preference.remove(ContantsUtil.USER_SESSIONID);
				preference.remove(ContantsUtil.USER_ID);
				ContantsUtil.MAIN_UPDATE = false;
				logout.setVisibility(View.GONE);
				finish();
			}
		});
	}

}
