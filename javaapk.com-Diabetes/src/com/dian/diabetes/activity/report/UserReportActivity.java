package com.dian.diabetes.activity.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.TranLoading;
import com.dian.diabetes.dto.ReportDto;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 综合报告首页，分为两个tab页，头部切换时间，中间切换fragment
 * 
 * @author longbh
 * 
 */
public class UserReportActivity extends BasicActivity implements
		OnClickListener,AnimationListener {

	@ViewInject(id = R.id.current_day)
	private TextView curMonth;
	@ViewInject(id = R.id.pre)
	private ImageButton preBtn;
	@ViewInject(id = R.id.next)
	private ImageButton nextBtn;
	@ViewInject(id = R.id.tab_bottom)
	private RadioGroup radioGroup;
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.content_fragment)
	private FrameLayout fragmentLayout;

	private ReportBaseFragment curentFragment;
	private TranLoading loading;
	private Date curentDate;

	// 详细数据
	private List<ReportDto> reportList;
	private List<ReportDto> resultList;

	private boolean isLoading = false;
	private Handler handle = new Handler();
	private int seconds = 0;
	
	private Animation fromLeft;
	private Animation fromRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_layout);
		setGuesture(true);
		curentDate = new Date();
		loading = new TranLoading(context);
		reportList = new ArrayList<ReportDto>();
		resultList = new ArrayList<ReportDto>();
		fromLeft = AnimationUtils.loadAnimation(context, R.anim.fragment_right_out);
		fromLeft.setAnimationListener(this);
		fromRight = AnimationUtils.loadAnimation(context, R.anim.fragment_left_out); 
		fromRight.setAnimationListener(this);
		initActivity();
		replaceFragment("report_view", FragmentReport.getInstance(), false);
		loadData(0);
	}

	private void initActivity() {
		backBtn.setOnClickListener(this);
		preBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int id) {
				if (isLoading) {
					return;
				}
				String tag;
				boolean isAdd = true;
				ReportBaseFragment tempFragment;
				switch (id) {
				case R.id.report:
					tag = "report_view";
					tempFragment = (ReportBaseFragment) getSupportFragmentManager()
							.findFragmentByTag(tag);
					if (tempFragment == null) {
						tempFragment = FragmentReport.getInstance();
						isAdd = false;
					}
					replaceFragment(tag, tempFragment, isAdd);
					break;
				case R.id.exam_result:
					tag = "exam_result";
					tempFragment = (ReportBaseFragment) getSupportFragmentManager()
							.findFragmentByTag(tag);
					if (tempFragment == null) {
						tempFragment = FragmentResult.getInstance();
						isAdd = false;
					}
					replaceFragment(tag, tempFragment, isAdd);
					break;
				}
			}
		});

	}

	private void loadData(int sub) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curentDate);
		calendar.add(Calendar.MONTH, sub);
		curentDate = calendar.getTime();
		String today = DateUtil.parseToDate(System.currentTimeMillis());
		String temp = DateUtil.parseToDate(curentDate.getTime());
		if (today.equals(temp)) {
			nextBtn.setClickable(false);
		} else {
			nextBtn.setClickable(true);
		}
		curMonth.setText(DateUtil.parseToString(curentDate, DateUtil.yyyyMM));
		notifyData();
	}

	public void replaceFragment(String tag, ReportBaseFragment tempFragment,
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

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.next:
			loadData(1);
			break;
		case R.id.pre:
			loadData(-1);
			break;
		}
	}

	private void notifyData() {
		if (UserReportActivity.this == null) {
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sex", ContantsUtil.curInfo.getSex());
		params.put("mid", ContantsUtil.DEFAULT_TEMP_UID);
		params.put("date", DateUtil.parseToInt(curentDate.getTime()));
		loading.show();
		loading.hideLabel();
		HttpServiceUtil.request(ContantsUtil.GET_RESULT, "post", params,
				new HttpServiceUtil.CallBack() {
					@Override
					public void callback(String json) {
						try {
							JSONObject groupData = new JSONObject(json);
							if (CheckUtil.checkStatusOk(groupData
									.getInt("status"))) {
								JSONObject dataJson = groupData
										.getJSONObject("data");
								seconds = dataJson.getInt("waitTime");
								if (seconds == 0) {
									loading.dismiss();
									isLoading = false;
									reportList.clear();
									resultList.clear();
									JSONArray array = dataJson
											.getJSONArray("reportInfos");
									for (int i = 0; i < array.length(); i++) {
										JSONObject item = array
												.getJSONObject(i);
										int type = item.getInt("type");
										ReportDto dto = new ReportDto();
										dto.name = item.getString("name");
										dto.value = item.getString("value");
										if (type == 0) {
											reportList.add(dto);
										} else {
											resultList.add(dto);
										}
									}
									curentFragment.notifyData();
									fragmentLayout.setVisibility(View.VISIBLE);
								} else {
									loading.show();
									loading.setLabel("正在生产综合报告,请稍后...\n预计等待时间："
											+ seconds + "秒");
									handle.postDelayed(runnable, 1000);
									isLoading = true;
								}
							} else {
								ToastTool.showToast(groupData.getInt("status"),
										context);
								loading.dismiss();
							}
						} catch (JSONException e) {
							loading.dismiss();
							Toast.makeText(context, "获取数据失败",
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				});
	}

	protected void scrollXBack() {
		if(isLoading){
			return;
		}
		fragmentLayout.startAnimation(fromLeft);
		preBtn.performClick();
	}

	protected void srollYRight() {
		if(isLoading){
			return;
		}
		String today = DateUtil.parseToDate(System.currentTimeMillis());
		String temp = DateUtil.parseToDate(curentDate.getTime());
		if (!today.equals(temp)) {
			fragmentLayout.startAnimation(fromRight);
			nextBtn.performClick();
		}
	}

	public void onBackPressed() {
		if (!curentFragment.onBackKeyPressed()) {
			finish();
		}
	}

	public void onDestroy() {
		super.onDestroy();
		handle = null;
	}
	
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (handle != null) {
				if(seconds > 0){
					seconds --;
					loading.setLabel("正在生产综合报告,请稍后...\n预计等待时间："
							+ seconds + "秒");
					handle.postDelayed(this, 1000);
				}else{
					notifyData();
				}
				
			}
		}
	};

	public List<ReportDto> getReportList() {
		return reportList;
	}

	public List<ReportDto> getResultList() {
		return resultList;
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		fragmentLayout.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		
	}

}
