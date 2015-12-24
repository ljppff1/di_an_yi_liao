package com.dian.diabetes.activity.user;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dian.diabetes.MainActivity;
import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.TranLoading;
import com.dian.diabetes.db.UserBo;
import com.dian.diabetes.db.UserInfoBo;
import com.dian.diabetes.db.dao.User;
import com.dian.diabetes.db.dao.UserInfo;
import com.dian.diabetes.request.HttpContants;
import com.dian.diabetes.request.LoginRegisterAPI;
import com.dian.diabetes.request.UserDataAPI;
import com.dian.diabetes.service.UserService;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.utils.HttpServiceUtil.CallBack;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 类/接口注释 注册fragment 性别选中为女
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月4日
 * 
 */
public class RegisterActivity extends BasicActivity implements OnClickListener,
		OnFocusChangeListener {

	@ViewInject(id = R.id.register_btn)
	private Button registerBtn;
	@ViewInject(id = R.id.login_uname)
	private EditText nameEditText;
	@ViewInject(id = R.id.login_code)
	private EditText codeEditText;
	@ViewInject(id = R.id.getcode_btn)
	private Button getcode_btn;
	@ViewInject(id = R.id.name_right_img)
	private ImageView nameRightImg;
	@ViewInject(id = R.id.psw_right_img)
	private ImageView pswRightImg;
	@ViewInject(id = R.id.code_right_img)
	private ImageView codeRightImg;
	@ViewInject(id = R.id.login_psw)
	private EditText pswEditText;
	@ViewInject(id = R.id.confirm_psw)
	private EditText reEditText;
	@ViewInject(id = R.id.confirm_right_img)
	private ImageView conRightImg;
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.sex)
	private ToggleButton sexBtn;

	private RegisterActivity activity;
	private TranLoading loading;

	// 存放获取输入的电话
	private String num = "";
	// 存放输入的验证码
	private String code = "";
	// 存放获取输入的密码
	private String psw = "";
	private Preference preference;
	private CallBack registerCallback, codeCallback, getUserInfo;
	private UserInfoBo userInfoBo;
	private UserBo userBo;
	private String matrixCode = "";

	private String username = "";
	private int age = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_register_layout);
		activity = (RegisterActivity) context;
		loading = new TranLoading(activity);
		userInfoBo = new UserInfoBo(activity);
		preference = Preference.instance(activity);
		userBo = new UserBo(activity);
		initCallBack();
		init();
	}

	private void init() {
		registerBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		getcode_btn.setOnClickListener(this);
		nameEditText.setOnFocusChangeListener(this);
		pswEditText.setOnFocusChangeListener(this);
		codeEditText.setOnFocusChangeListener(this);

		// 每次oncreateView的时候设置为空
		nameEditText.setText("");
		nameEditText.requestFocus();
		pswEditText.setText("");
		codeEditText.setText("");
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			matrixCode = bundle.getString("code");
		}
		if (!CheckUtil.isNull(matrixCode)) {
			String[] codeValue = matrixCode.split("\\|")[1].split("\\^");
			username = codeValue[1];
			if ("男".equals(codeValue[2])) {
				sexBtn.setChecked(false);
			} else {
				sexBtn.setChecked(true);
			}
			age = StringUtil.toInt(codeValue[3].substring(0,
					codeValue[3].length() - 1));
		}

		reEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence edit, int arg1, int arg2,
					int arg3) {
				if (CheckUtil.checkEquels(pswEditText.getText(), edit)) {
					conRightImg.setImageResource(R.drawable.input_correct);
				} else {
					conRightImg.setImageResource(R.drawable.input_wrong);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
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
		// 判断长度
		if (CheckUtil.checkLength(psw, 4)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查验证码
	 * 
	 * @return 是否通过检查
	 */
	private boolean identifyCode() {
		// 长度 是否为空 空格
		code = codeEditText.getText().toString();
		if (CheckUtil.checkLength(code, 0)) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean confirmPasswd(){
		if(CheckUtil.checkEquels(pswEditText.getText(), reEditText.getText())){
			return true;
		}
		return false;
	}

	@Override
	public void onStop() {
		nameEditText.setText("");
		pswEditText.setText("");
		codeEditText.setText("");
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			activity.onBackPressed();
			break;
		case R.id.register_btn:
			if (checkNum() && identifyCode() && checkPsw()&&confirmPasswd()) {
				goRegister();
			} else {
				ToastTool.showUserStatus(HttpContants.WRONG_FORMAT, activity);
			}
			break;
		case R.id.getcode_btn:
			if (checkNum()) {
				// 显示 状态
				ToastTool.showUserStatus(HttpContants.GETTING_CODE, activity);
				getCode();
			} else {
				ToastTool.showUserStatus(HttpContants.PHONE_FORMAT_ERROR,
						activity);
			}
			break;
		}
	}

	/**
	 * 请求注册
	 */
	private void goRegister() {
		String phone = nameEditText.getText().toString();
		String password = pswEditText.getText().toString();
		String code = codeEditText.getText().toString();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phone", phone);
		map.put("password", password);
		map.put("code", code);
		if (sexBtn.isChecked()) {
			map.put("sex", 1);
		} else {
			map.put("sex", 0);
		}
		if (CheckUtil.isNull(matrixCode)) {
			map.put("username", username);
			map.put("age", age);
		}
		loading.show();
		LoginRegisterAPI.register(map, registerCallback);
	}

	/**
	 * 请求获取验证码
	 */
	private void getCode() {
		String phone = nameEditText.getText().toString();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phone", phone);
		LoginRegisterAPI.genCode(map, codeCallback);
	}

	private void initCallBack() {
		getUserInfo = new CallBack() {

			@Override
			public void callback(String json) {
				loading.dismiss();
				try {
					JSONObject data = new JSONObject(json);

					int status = data.getInt("status");
					switch (status) {
					case HttpContants.REQUEST_SUCCESS:
						UserInfo userInfo = new UserInfo();
						UserInfo.transformToUserInfo(userInfo,
								data.getString("data"));
						// 数据库里面有没有当前用户，没有就保存在数据库，有就更新信息
						UserInfo temp = userInfoBo.getUinfoByMid(userInfo
								.getMid());
						if (temp == null) {
							userInfoBo.saveUserInfo(userInfo);
						} else {
							userInfo.setId(temp.getId());
							userInfo.setUid(temp.getUid());
							userInfoBo.updateUserInfo(userInfo);
						}
						break;
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};

		registerCallback = new CallBack() {

			@Override
			public void callback(String json) {
				// 解析json
				try {
					JSONObject jsonObject = new JSONObject(json);
					int status = jsonObject.getInt("status");
					loading.dismiss();
					switch (status) {
					// 注册成功后跳转到个人页面，无需再去登录
					case HttpContants.REQUEST_SUCCESS:
						JSONObject data = jsonObject.getJSONObject("data");
						String sessionId = data.getString("sessionId");
						JSONObject user = data.getJSONObject("user");

						UserInfo uInfo = userInfoBo.getUinfoByMid(user
								.getLong("mid"));
						if (uInfo == null) {
							uInfo = new UserInfo();
							UserInfo.transformToUserInfo(uInfo, user.toString());
							long id = userInfoBo.saveUserInfo(uInfo);
							uInfo.setId(id);
						} else {
							UserInfo.transformToUserInfo(uInfo, user.toString());
							userInfoBo.updateUserInfo(uInfo);
						}
						ContantsUtil.curInfo = uInfo;
						// 更新到user表中，存在修改，不存在插入
						UserService uService = new UserService();
						User userModel = uService.convertJsonTo(user);
						ContantsUtil.curUser = userBo.saveUserServer(userModel);

						// mid
						preference.putLong(ContantsUtil.USER_MID,
								user.getLong("mid"));
						ContantsUtil.DEFAULT_TEMP_UID = user.getLong("mid")
								+ "";
						// sessionId
						preference.putString(ContantsUtil.USER_SESSIONID,
								sessionId);
						HttpServiceUtil.sessionId = sessionId;
						// uid
						preference.putLong(ContantsUtil.USER_ID,
								user.getLong("uid"));

						// 从服务器获取数据更新
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("mid", String.valueOf(user.getLong("mid")));
						UserDataAPI.getOne(map, activity, getUserInfo);

						// 显示登录状态
						ToastTool.showUserStatus(HttpContants.REGISTER_SUCCESS,
								activity);
						activity.startActivity(null, MainActivity.class);
						activity.finishSimple();
						break;
					default:
						ToastTool.showToast(status, activity);
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				// 跳转到登录页面
				finish();
			}
		};

		codeCallback = new CallBack() {

			@Override
			public void callback(String json) {
				// 解析json
				try {
					JSONObject jsonObject = new JSONObject(json);
					int status = jsonObject.getInt("status");
					String code = jsonObject.has("data") ? "请记住验证码:"
							+ jsonObject.getString("data") : "获取失败";
					switch (status) {
					case HttpContants.REQUEST_SUCCESS:
						Toast.makeText(activity, code, Toast.LENGTH_LONG)
								.show();
						// 以后有短信使用这个showLoginState(CODE_SUCCESS);
						break;
					default:
						ToastTool.showToast(status, activity);
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
		case R.id.login_code:
			codeEditText.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					if (s.length() > 0) {
						// check code
						if (identifyCode()) {
							codeRightImg
									.setImageResource(R.drawable.input_correct);
						} else {
							codeRightImg
									.setImageResource(R.drawable.input_wrong);
						}
					} else {
						codeRightImg.setImageBitmap(null);
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

	@Override
	public void onBackPressed() {
		finish();
	}

}
