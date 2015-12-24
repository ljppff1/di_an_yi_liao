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

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.request.HttpContants;
import com.dian.diabetes.request.LoginRegisterAPI;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.HttpServiceUtil.CallBack;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 类/接口注释
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月7日
 * 
 */
public class ForgetPswActivity extends BasicActivity implements OnClickListener,
        OnFocusChangeListener {

    @ViewInject(id = R.id.login_uname)
    private EditText nameEditText;
    @ViewInject(id = R.id.login_psw)
    private EditText pswEditText;
    @ViewInject(id = R.id.login_code)
    private EditText codeEditText;
    @ViewInject(id = R.id.findpsw_btn)
    private Button findpswBtn;
    @ViewInject(id = R.id.back_btn)
    private Button backBtn;
    @ViewInject(id = R.id.getcode_btn)
    private Button getcode_btn;
    @ViewInject(id = R.id.name_right_img)
    private ImageView nameRightImg;
    @ViewInject(id = R.id.psw_right_img)
    private ImageView pswRightImg;
    @ViewInject(id = R.id.code_right_img)
    private ImageView codeRightImg;

    private ForgetPswActivity activity;

    private CallBack codeCallback, findpswCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_forgetpsw_layout);
        activity = (ForgetPswActivity) context;
        initCallBack();
        init();
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_forgetpsw_layout, container, false);
//        fieldView(view);
//        init();
//        return view;
//    }

    @Override
    public void onStop() {
        nameEditText.setText("");
        pswEditText.setText("");
        codeEditText.setText("");
        super.onStop();
    }

    /**
     * 检查用户输入的手机号
     * 
     * @return 是否通过检查
     */
    private boolean checkNum() {
        // 判断号码是否存在
        String num = nameEditText.getText().toString();
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
        String psw = pswEditText.getText().toString();
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
        String code = codeEditText.getText().toString();
        if (CheckUtil.checkLength(code, 0)) {
            return true;
        } else {
            return false;
        }
    }

    private void init() {
        findpswBtn.setOnClickListener(this);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.findpsw_btn:
            if (checkNum() && identifyCode() && checkPsw()) {
                gofindpsw();
            } else {
                ToastTool.showUserStatus(HttpContants.WRONG_FORMAT, activity);
            }
            break;
        case R.id.back_btn:
            activity.onBackPressed();
            break;
        case R.id.getcode_btn:
            if (checkNum()) {
                // 显示 状态
                ToastTool.showUserStatus(HttpContants.GETTING_CODE, activity);
                getCode();
            } else {
                ToastTool.showUserStatus(HttpContants.PHONE_FORMAT_ERROR, activity);
            }
            break;
        }
    }

    private void getCode() {
        String phone = nameEditText.getText().toString();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("phone", phone);
        LoginRegisterAPI.pswGenCode(map, codeCallback);
    }

    private void gofindpsw() {
        String phone = nameEditText.getText().toString();
        String password = pswEditText.getText().toString();
        String code = codeEditText.getText().toString();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("phone", phone);
        map.put("password", password);
        map.put("code", code);
        LoginRegisterAPI.change(map, findpswCallback);
    }

    private void initCallBack() {
        codeCallback = new CallBack() {

            @Override
            public void callback(String json) {
                // 解析json
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int status = jsonObject.getInt("status");
                    String code = jsonObject.getString("data");
                    switch (status) {
                    case HttpContants.REQUEST_SUCCESS:
                        Toast.makeText(activity, "请记住验证码:" + code, Toast.LENGTH_LONG).show();
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

        findpswCallback = new CallBack() {

            @Override
            public void callback(String json) {
                // 解析json
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int status = jsonObject.getInt("status");
                    switch (status) {
                    case HttpContants.REQUEST_SUCCESS:
                        ToastTool.showUserStatus(HttpContants.MODIFY_PSW_SUCCESS, activity);
                        // 返回登录页面
                        activity.onBackPressed();
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
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        // check username
                        if (checkNum()) {
                            nameRightImg.setImageResource(R.drawable.input_correct);
                        } else {
                            nameRightImg.setImageResource(R.drawable.input_wrong);
                        }
                    } else {
                        nameRightImg.setImageBitmap(null);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            break;
        case R.id.login_code:
            codeEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        // check code
                        if (identifyCode()) {
                            codeRightImg.setImageResource(R.drawable.input_correct);
                        } else {
                            codeRightImg.setImageResource(R.drawable.input_wrong);
                        }
                    } else {
                        codeRightImg.setImageBitmap(null);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            break;
        case R.id.login_psw:
            pswEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        // check code
                        if (checkPsw()) {
                            pswRightImg.setImageResource(R.drawable.input_correct);
                        } else {
                            pswRightImg.setImageResource(R.drawable.input_wrong);
                        }
                    } else {
                        pswRightImg.setImageBitmap(null);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

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
