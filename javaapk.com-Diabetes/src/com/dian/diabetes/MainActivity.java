package com.dian.diabetes;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.assess.AssessActivity;
import com.dian.diabetes.activity.eat.EatActivity;
import com.dian.diabetes.activity.indicator.IndicatorActivity;
import com.dian.diabetes.activity.medicine.MedicineActivity;
import com.dian.diabetes.activity.news.NewListActivity;
import com.dian.diabetes.activity.report.UserReportActivity;
import com.dian.diabetes.activity.set.SettingActivity;
import com.dian.diabetes.activity.sport.SportActivity;
import com.dian.diabetes.activity.sugar.PlanActivity;
import com.dian.diabetes.activity.sugar.SugarActivity;
import com.dian.diabetes.activity.user.LoginActivity;
import com.dian.diabetes.activity.user.ManageUsersActivity;
import com.dian.diabetes.activity.user.MemberActivity;
import com.dian.diabetes.db.CommonBo;
import com.dian.diabetes.db.dao.UserInfo;
import com.dian.diabetes.dialog.AlertDialog;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.CircleImageView;
import com.dian.diabetes.widget.anotation.ViewInject;
import com.dian.diabetes.zxing.CaptureActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zdp.aseo.content.AseoZdpAseo;

import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BasicActivity implements OnClickListener {

	@ViewInject(id = R.id.diabetes_listener)
	private LinearLayout diabetesListener;
	@ViewInject(id = R.id.news_share)
	private LinearLayout newsListener;
	@ViewInject(id = R.id.eat_manager)
	private LinearLayout eatManager;
	@ViewInject(id = R.id.sport_manager)
	private LinearLayout sportManager;
	@ViewInject(id = R.id.evaluate_self)
	private LinearLayout evaluateSelf;
	@ViewInject(id = R.id.content_report)
	private LinearLayout contentReport;
	@ViewInject(id = R.id.drug_manager)
	private LinearLayout drugManager;
	@ViewInject(id = R.id.target_manager)
	private LinearLayout targetManager;
	@ViewInject(id = R.id.set_manager)
	private LinearLayout setManager;
	@ViewInject(id = R.id.scance_zxing)
	private ImageButton scancelZxing;
	@ViewInject(id = R.id.photo)
	private CircleImageView photo;
	@ViewInject(id = R.id.name)
	private TextView nameText;
	@ViewInject(id = R.id.member)
	private ImageButton memberBtn;
	@ViewInject(id = R.id.toast_container)
	private LinearLayout toastCon;
	@ViewInject(id = R.id.toast_text)
	private TextView toastTxt;
	@ViewInject(id = R.id.clock_icon)
	private ImageView clockIcon;
	@ViewInject(id = R.id.pre)
	private ImageButton pre;
	@ViewInject(id = R.id.next)
	private ImageButton next;

	private AlertDialog alert;

	private long lastBackPress = 0;
	private UserInfo uInfo;
	private final int CAPTURE = 2001;
	private Preference preference;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 初始话图片加载
		ContantsUtil.MAIN_UPDATE = false;
		preference = Preference.instance(context);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.photo_default)
				.showImageForEmptyUri(R.drawable.photo_default)
				.showImageOnLoading(R.drawable.photo_default)
				.showImageOnFail(R.drawable.photo_default).cacheInMemory(true)
				.cacheOnDisc(true).build();
		AseoZdpAseo.initFinalTimer(this, AseoZdpAseo.BOTH_TYPE);
		alert = new AlertDialog(context, "您还未做自我评估，现在要去做一次自我检测吗？");
		alert.setCallBack(new AlertDialog.CallBack() {
			@Override
			public void cancel() {
			}

			@Override
			public void callBack() {
				if ((ContantsUtil.curInfo == null && CheckUtil
						.isNull(ContantsUtil.curInfo.getSex()))
						|| ContantsUtil.curInfo.getSex() == -1) {
					Toast.makeText(context, "您的个人信息未完善，请先完善用户信息",
							Toast.LENGTH_SHORT).show();
					Bundle bundle = new Bundle();
					bundle.putLong("mid",
							StringUtil.toLong(ContantsUtil.DEFAULT_TEMP_UID));
					bundle.putBoolean("isedit", true);
					startActivity(bundle, ManageUsersActivity.class);
					return;
				}
				startActivity(null, AssessActivity.class);
			}
		});
		initActivity();
		checkUpdate();
	}

	private void initActivity() {
		diabetesListener.setOnClickListener(this);
		scancelZxing.setOnClickListener(this);
		newsListener.setOnClickListener(this);
		eatManager.setOnClickListener(this);
		sportManager.setOnClickListener(this);
		evaluateSelf.setOnClickListener(this);
		contentReport.setOnClickListener(this);
		drugManager.setOnClickListener(this);
		targetManager.setOnClickListener(this);
		setManager.setOnClickListener(this);
		photo.setOnClickListener(this);
		memberBtn.setOnClickListener(this);
		clockIcon.setOnClickListener(this);
		next.setOnClickListener(this);
		pre.setOnClickListener(this);
	}

	public void onResume() {
		super.onResume();
		if (!CheckUtil.isNull(HttpServiceUtil.sessionId)
				&& !CheckUtil.isNull(ContantsUtil.DEFAULT_TEMP_UID)) {
			uInfo = ContantsUtil.curInfo;
			if (uInfo != null && uInfo.getNick_name() != null) {
				nameText.setText(uInfo.getNick_name());
			}
			if (uInfo != null && uInfo.getAvatar() != null) {
				String url = uInfo.getAvatar();
				ImageLoader.getInstance().displayImage(url, photo, options);
			}else{
				photo.setImageResource(R.drawable.user_icon_unkown);
			}
		} else {
			nameText.setText("游客");
			photo.setImageResource(R.drawable.user_icon_unkown);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case CAPTURE:
			Bundle bundle = data.getExtras();
			String result = bundle.getString("result");
			if (CheckUtil.isNull(result)) {
				Toast.makeText(context, "未扫描到正确的二维码", Toast.LENGTH_SHORT)
						.show();
			} else {
				switchData(result, bundle.getString("name"));
			}
			break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.diabetes_listener:
			if (checkLogin()) {
				startActivity(null, SugarActivity.class);
			}
			break;
		case R.id.scance_zxing:
			Intent intent = new Intent();
			intent.setClass(context, CaptureActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, CAPTURE);
			break;
		case R.id.photo:
			if (checkLogin()) {
				// JCTODO 跳到个人信息中心
				Bundle bundle = new Bundle();
				bundle.putLong("mid",
						StringUtil.toLong(ContantsUtil.DEFAULT_TEMP_UID));
				startActivity(bundle, ManageUsersActivity.class);
			}
			break;
		case R.id.news_share:
			startActivity(null, NewListActivity.class);
			break;
		case R.id.sport_manager:
			if (checkLogin()) {
				if ((ContantsUtil.curInfo == null && CheckUtil
						.isNull(ContantsUtil.curInfo.getSex()))
						|| ContantsUtil.curInfo.getSex() == -1) {
					Toast.makeText(context, "您的个人信息未完善，请先完善用户信息",
							Toast.LENGTH_SHORT).show();
					Bundle bundle = new Bundle();
					bundle.putLong("mid",
							StringUtil.toLong(ContantsUtil.DEFAULT_TEMP_UID));
					bundle.putBoolean("isedit", true);
					startActivity(bundle, ManageUsersActivity.class);
					return;
				}
				if (ContantsUtil.curInfo.getExamStatus() == 0) {
					alert.show();
					return;
				}
				startActivity(null, SportActivity.class);
			}
			break;
		case R.id.eat_manager:
			if (checkLogin()) {
				if ((ContantsUtil.curInfo == null && CheckUtil
						.isNull(ContantsUtil.curInfo.getSex()))
						|| ContantsUtil.curInfo.getSex() == -1) {
					Toast.makeText(context, "您的个人信息未完善，请先完善用户信息",
							Toast.LENGTH_SHORT).show();
					Bundle bundle = new Bundle();
					bundle.putLong("mid",
							StringUtil.toLong(ContantsUtil.DEFAULT_TEMP_UID));
					bundle.putBoolean("isedit", true);
					startActivity(bundle, ManageUsersActivity.class);
					return;
				}
				if (ContantsUtil.curInfo.getExamStatus() == 0) {
					alert.show();
					return;
				}
				startActivity(null, EatActivity.class);
			}
			break;
		case R.id.evaluate_self:
			if (!checkLogin()) {
				return;
			}
			if ((ContantsUtil.curInfo == null && CheckUtil
					.isNull(ContantsUtil.curInfo.getSex()))
					|| ContantsUtil.curInfo.getSex() == -1) {
				Toast.makeText(context, "您的个人信息未完善，请先完善用户信息",
						Toast.LENGTH_SHORT).show();
				Bundle bundle = new Bundle();
				bundle.putLong("mid",
						StringUtil.toLong(ContantsUtil.DEFAULT_TEMP_UID));
				bundle.putBoolean("isedit", true);
				startActivity(bundle, ManageUsersActivity.class);
				return;
			}
			startActivity(null, AssessActivity.class);
			break;
		case R.id.content_report:
			if (checkLogin()) {
				if ((ContantsUtil.curInfo == null && CheckUtil
						.isNull(ContantsUtil.curInfo.getSex()))
						|| ContantsUtil.curInfo.getSex() == -1) {
					Toast.makeText(context, "您的个人信息未完善，请先完善用户信息",
							Toast.LENGTH_SHORT).show();
					Bundle bundle = new Bundle();
					bundle.putLong("mid",
							StringUtil.toLong(ContantsUtil.DEFAULT_TEMP_UID));
					bundle.putBoolean("isedit", true);
					startActivity(bundle, ManageUsersActivity.class);
					return;
				}
				if (ContantsUtil.curInfo.getExamStatus() == 0) {
					alert.show();
					return;
				}
				startActivity(null, UserReportActivity.class);
			}
			break;
		case R.id.drug_manager:
			if (checkLogin()) {
				startActivity(null, MedicineActivity.class);
			}
			break;
		case R.id.target_manager:
			if (checkLogin()) {
				startActivity(null, IndicatorActivity.class);
			}
			break;
		case R.id.set_manager:
			if (checkLogin()) {
				startActivity(null, SettingActivity.class);
			}
			break;
		case R.id.member:
			if (checkLogin()) {
				startActivity(null, MemberActivity.class);
			}
			break;
		case R.id.clock_icon:
			// if (toastCon.getVisibility() == View.VISIBLE) {
			// toastCon.setVisibility(View.GONE);
			// } else {
			// toastCon.setVisibility(View.VISIBLE);
			// }
			if (checkLogin()) {
				context.startActivity(null, PlanActivity.class);
			}
			break;
		}
	}

	private void switchData(String code, String codeType) {
		Bundle bundle = new Bundle();
		bundle.putString("code", code);
		bundle.putString("type", codeType);
		if (checkLogin()) {
			startActivity(bundle, IndicatorActivity.class);
		} else {
			startActivity(bundle, LoginActivity.class);
		}
	}

	/**
	 * 检查是否登录
	 * 
	 * @return
	 */
	private boolean checkLogin() {
		if (!CheckUtil.isNull(HttpServiceUtil.sessionId)
				&& !CheckUtil.isNull(ContantsUtil.DEFAULT_TEMP_UID)) {
			return true;
		} else {
			startActivity(new Bundle(), LoginActivity.class);
		}
		return false;
	}

	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}

	public void onDestroy() {
		super.onDestroy();
	}

	private void checkUpdate() {
		long lastTime = preference.getLong(Preference.UPDATE_TIME);
		if (System.currentTimeMillis() - lastTime < 24 * 60 * 60 * 1000) {
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("device", 0);
		params.put("current", ContantsUtil.currentVesion);
		HttpServiceUtil.request(ContantsUtil.UPDATE_CHECK_URL, "get", params,
				new HttpServiceUtil.CallBack() {
					@Override
					public void callback(String json) {
						try {
							JSONObject jsonObj = new JSONObject(json);
							if (CheckUtil.checkStatusOk(jsonObj
									.getInt("status"))) {
								if (jsonObj.has("data")) {
									JSONObject dataJson = jsonObj
											.getJSONObject("data");
									String url = dataJson.getString("url");
									preference.putString(Preference.UPDATE_URL,
											url);
									showUpdateDialog(url);
								}
								preference.putLong(Preference.UPDATE_TIME,
										System.currentTimeMillis());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
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
}
