package com.dian.diabetes.activity.user;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.DialogLoading;
import com.dian.diabetes.activity.indicator.IndicatorActivity;
import com.dian.diabetes.db.AlarmBo;
import com.dian.diabetes.db.UserBo;
import com.dian.diabetes.db.UserInfoBo;
import com.dian.diabetes.db.dao.User;
import com.dian.diabetes.db.dao.UserInfo;
import com.dian.diabetes.request.HttpContants;
import com.dian.diabetes.request.LoginRegisterAPI;
import com.dian.diabetes.service.IndicateService;
import com.dian.diabetes.service.LoadingService;
import com.dian.diabetes.service.UpdateService;
import com.dian.diabetes.service.UserService;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.utils.HttpServiceUtil.CallBack;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 类/接口注释
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月4日
 * 
 */
public class LoginActivity extends BasicActivity implements OnClickListener,
		OnFocusChangeListener {

	@ViewInject(id = R.id.forget_psw)
	private TextView forgetPswText;
	@ViewInject(id = R.id.login_uname)
	private EditText nameEditText;
	@ViewInject(id = R.id.login_psw)
	private EditText pswEditText;
	@ViewInject(id = R.id.login_btn)
	private Button loginBtn;
	@ViewInject(id = R.id.goregister_btn)
	private Button goregisterBtn;
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.name_right_img)
	private ImageView nameRightImg;
	@ViewInject(id = R.id.psw_right_img)
	private ImageView pswRightImg;

	private LoginActivity activity;
	private DialogLoading loading;

	// 存放获取输入的电话
	private String num = "";
	// 存放获取输入的密码
	private String psw = "";
	private Preference preference;
	private CallBack loginCallback;
	private UserBo userBo;
	private UserInfoBo userInfoBo;

	private String scanCode;
	private Bundle bundle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_login_layout);
		activity = (LoginActivity) context;
		loading = new DialogLoading(activity);
		userBo = new UserBo(activity);
		userInfoBo = new UserInfoBo(activity);
		preference = Preference.instance(activity);
		initCallBack();
		init();
	}

	private void init() {
		bundle = getIntent().getExtras();
		if (bundle != null) {
			scanCode = bundle.getString("code");
		}
		forgetPswText.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		goregisterBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		nameEditText.setOnFocusChangeListener(this);
		pswEditText.setOnFocusChangeListener(this);

		// 每次oncreateView的时候设置为空
		nameEditText.setText(preference.getString(Preference.CACHE_USER));
		nameEditText.requestFocus();
	}

	/**
	 * 检查用户输入的手机号
	 * 
	 * @return 是否通过检查
	 */
	private boolean checkNum() {
		// 判断号码是否存在
		num = nameEditText.getText().toString();
		// 电话的长度为11，开头为1，layout里面已经设置只能输入数字
		if (CheckUtil.checkLengthEq(num, 11) && num.charAt(0) == '1') {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查用户输入的密码
	 * 
	 * @return 是否通过检查
	 */
	private boolean checkPsw() {
		psw = pswEditText.getText().toString();
		// 判断长度checkLength
		if (CheckUtil.checkLength(psw, 4)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onStop() {
		nameEditText.setText("");
		pswEditText.setText("");
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			activity.onBackPressed();
			break;
		case R.id.forget_psw:
			startActivity(null, ForgetPswActivity.class);
			break;
		case R.id.login_btn:
			if (checkNum() && checkPsw()) {
				// 提交到服务器
				goLogin();
			} else {
				ToastTool.showUserStatus(HttpContants.WRONG_FORMAT, activity);
			}
			break;
		case R.id.goregister_btn:
			startActivity(bundle, RegisterActivity.class);
			break;
		}
	}

	private void goLogin() {
		// 提交到服务器
		String phone = nameEditText.getText().toString();
		String password = pswEditText.getText().toString();
		preference.putString(Preference.CACHE_USER, phone);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phone", phone);
		map.put("password", password);
		loading.show();
		LoginRegisterAPI.login(map, loginCallback);
	}

	private void initCallBack() {
		loginCallback = new CallBack() {
			@Override
			public void callback(String json) {
				loading.hide();
				// 解析json
				try {
					JSONObject jsonObject = new JSONObject(json);
					int jStatus = jsonObject.getInt("status");
					switch (jStatus) {
					case HttpContants.REQUEST_SUCCESS:
						JSONObject data = jsonObject.getJSONObject("data");
						String jSessionId = data.getString("sessionId");
						JSONObject user = data.getJSONObject("user");
						Long mid = user.getLong("mid");
						// 更新到user表中，存在修改，不存在插入
						UserService uService = new UserService();
						User userModel = uService.convertJsonTo(user);
						ContantsUtil.curUser = userBo.saveUserServer(userModel);
						ContantsUtil.MAIN_UPDATE = false;
						// mid
						preference.putLong(Preference.USER_ID, mid);
						ContantsUtil.DEFAULT_TEMP_UID = mid + "";
						// uid
						// activity.getPrefernce().putLong(ContantsUtil.USER_ID,
						// uid);
						// sessionId
						preference.putString(ContantsUtil.USER_SESSIONID,
								jSessionId);
						HttpServiceUtil.sessionId = jSessionId;

						Config.startUpdate();
						// 显示登录状态
						MyThread myThread = new MyThread();
						new Thread(myThread).start();
						loading.show();
						break;
					default:
						ToastTool.showToast(jStatus, activity);
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.login_uname:
			nameEditText.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					if (s.length() > 0) {
						// check username
						if (checkNum()) {
							nameRightImg
									.setImageResource(R.drawable.input_correct);
						} else {
							nameRightImg
									.setImageResource(R.drawable.input_wrong);
						}
					} else {
						nameRightImg.setImageBitmap(null);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});
			break;
		case R.id.login_psw:
			pswEditText.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					if (s.length() > 0) {
						// check psw
						if (checkPsw()) {
							pswRightImg
									.setImageResource(R.drawable.input_correct);
						} else {
							pswRightImg
									.setImageResource(R.drawable.input_wrong);
						}

					} else {
						pswRightImg.setImageBitmap(null);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});
		}
	}

	private void setDialogLabel(String label) {
		if (loading == null) {
			loading = new DialogLoading(context);
		}
		loading.setDialogLabel(label);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				setDialogLabel("正在合并用户设置数据");
				break;
			case 2:
				setDialogLabel("正在合并血糖数据");
				break;
			case 3:
				setDialogLabel("数据同步完成");
				// 启动当前用户闹钟系统
				new AlarmBo(activity).setNextAlarm(
						ContantsUtil.DEFAULT_TEMP_UID,
						ContantsUtil.curUser.getService_uid());
				loading.dismiss();
				if (CheckUtil.isNull(ContantsUtil.curInfo.getSex())
						|| ContantsUtil.curInfo.getSex() == -1) {
					Bundle bundle = new Bundle();
					bundle.putLong("mid",
							StringUtil.toLong(ContantsUtil.DEFAULT_TEMP_UID));
					bundle.putBoolean("isedit", true);
					startActivity(bundle, ManageUsersActivity.class);
					Toast.makeText(context, "请先完善您的个人信息，再进行下面的操作!",
							Toast.LENGTH_SHORT).show();
				} else {
					ToastTool.showUserStatus(HttpContants.LONGIN_SUCCESS,
							activity);
					// activity.startActivity(null, MainActivity.class);
					if (scanCode != null) {
						startActivity(bundle, IndicatorActivity.class);
					}
				}
				finishSimple();
				break;
			case 4:
				setDialogLabel("正在同步本地用户设置数据");
				break;
			case 5:
				setDialogLabel("正在同步本地血糖数据");
				break;
			case 6:
				setDialogLabel("正在同步本地监测计划数据");
				break;
			case 7:
				setDialogLabel("正在上传系统数据");
				break;
			case 8:
				setDialogLabel("正在上传饮食数据");
				break;
			case 9:
				setDialogLabel("正在上传运动数据");
				break;
			case 10:
				setDialogLabel("正在上传用药数据");
				break;
			case 11:
				setDialogLabel("正在同步本地饮食数据");
				break;
			case 12:
				setDialogLabel("正在同步本地运动数据");
				break;
			case 13:
				setDialogLabel("正在同步本地用药数据");
				break;
			case 14:
				setDialogLabel("正在同步自测指标数据");
				break;
			case 15:
				setDialogLabel("正在上传自测指标数据");
				break;
			case 16:
				setDialogLabel("同步用户信息成功");
				break;
			case 17:
				loading.dismiss();
				Toast.makeText(context, "同步用户信息失败，请稍后再试", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	/**
	 * 同步服务端数据
	 */
	class MyThread implements Runnable {
		public void run() {
			IndicateService.initIndicate(getApplicationContext(),
					ContantsUtil.DEFAULT_TEMP_UID);
			// 同步用户个人信息
			// 从服务器获取数据更新
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mid", ContantsUtil.DEFAULT_TEMP_UID);
			String result = HttpServiceUtil.post(ContantsUtil.HOST
					+ HttpContants.URL_MEMBER_GETONE, map);
			if (!CheckUtil.isNull(result)) {
				boolean state = loadUserInfo(result);
				if (!state) {
					handler.sendEmptyMessage(17);
					return;
				} else {
					handler.sendEmptyMessage(16);
				}
			}
			// 如果登陆之前未联网先同步系统参数
			if (!ContantsUtil.isSycnSystem) {
				handler.sendEmptyMessage(7);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("timestamp",
						preference.getLong(Preference.SYS_UPDATE_TIME));
				result = HttpServiceUtil.post(ContantsUtil.PRED_UPDATE, params);
				LoadingService.sycnData(preference, context, result);
				ContantsUtil.isSycnSystem = true;
			}
			// 判断是否有未同步数据
			UpdateService uService = new UpdateService(context, userBo);
			if (preference.getBoolean(Preference.HAS_UPDATE)) {
				handler.sendEmptyMessage(4);
				uService.updateUserProperty();
				handler.sendEmptyMessage(5);
				uService.updateDiabetes();
				handler.sendEmptyMessage(6);
				uService.updateAlarm();
				handler.sendEmptyMessage(11);
				uService.updateEat();
				handler.sendEmptyMessage(12);
				uService.updateSport();
				handler.sendEmptyMessage(13);
				uService.updateMedicine();
				handler.sendEmptyMessage(15);
				uService.updateIndicates();
				Config.stopUpdate();
				preference.putBoolean(Preference.HAS_UPDATE, false);
			}
			LoadingService service = new LoadingService(context, userBo);
			// load用户自定义设置
			handler.sendEmptyMessage(1);
			service.loadingUserSet();
			handler.sendEmptyMessage(2);
			service.loadingDbtData();
			handler.sendEmptyMessage(3);
			service.loadingEatData();
			handler.sendEmptyMessage(8);
			service.loadingSportData();
			handler.sendEmptyMessage(9);
			service.loadingMedicineData();
			handler.sendEmptyMessage(10);
			service.loadingIndicateData();
			handler.sendEmptyMessage(14);
			// 刷新配置
			Config.loadUserSet(activity);
		}
	};

	private boolean loadUserInfo(String json) {
		JSONObject data;
		try {
			data = new JSONObject(json);
			int status = data.getInt("status");
			if (CheckUtil.checkStatusOk(status)) {
				UserInfo userInfo = new UserInfo();
				UserInfo.transformToUserInfo(userInfo, data.getString("data"));
				// 数据库里面有没有当前用户，没有就保存在数据库，有就更新信息
				UserInfo temp = userInfoBo.getUinfoByMid(userInfo.getMid());
				if (temp == null) {
					userInfoBo.saveUserInfo(userInfo);
				} else {
					userInfo.setId(temp.getId());
					userInfo.setUid(temp.getUid());
					userInfoBo.updateUserInfo(userInfo);
				}
				ContantsUtil.curInfo = userInfo;
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
}
