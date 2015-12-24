package com.dian.diabetes.activity.assess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.TranLoading;
import com.dian.diabetes.activity.report.UserReportActivity;
import com.dian.diabetes.db.ReportBo;
import com.dian.diabetes.db.UserBo;
import com.dian.diabetes.db.UserInfoBo;
import com.dian.diabetes.db.dao.Report;
import com.dian.diabetes.dialog.AlertDialog;
import com.dian.diabetes.dto.DataModel;
import com.dian.diabetes.dto.ItemModel;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.widget.anotation.ViewInject;
import com.google.gson.Gson;

/**
 * 用户评估用的activity入口，逻辑放在两个子fragment中
 * @author hua
 *
 */
public class AssessActivity extends BasicActivity implements OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.next_btn)
	private Button nextBtn;
	@ViewInject(id = R.id.progress)
	private LinearLayout progress;
	@ViewInject(id = R.id.title)
	private TextView titleView;
	@ViewInject(id = R.id.progress_con)
	private LinearLayout progresCon;
	@ViewInject(id = R.id.progressbar)
	private ProgressBar progresBar;
	@ViewInject(id = R.id.progres_text)
	private TextView progresTxt;

	private Map<Integer, Object> maps;
	private List<ItemModel> lists;
	private int itemWidth = 0;
	private int itemHeight = 0;
	private int leftMargin = 0;
	private int curentIndex = 0;
	private long updateTime = 0;
	private Gson gson;
	private ReportBo reportBo;
	private Report report;

	private BaseFragment curentfragment;
	private TranLoading loading;
	private boolean isState = false;
	private boolean hasState = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assess_layout);
		lists = new ArrayList<ItemModel>();
		itemWidth = getResources().getDimensionPixelSize(
				R.dimen.assess_item_width);
		itemHeight = getResources().getDimensionPixelSize(
				R.dimen.assess_item_height);
		leftMargin = getResources().getDimensionPixelSize(
				R.dimen.assess_padding);
		loading = new TranLoading(context);
		gson = new Gson();
		reportBo = new ReportBo(context);
		initActivity();
	}

	private void initActivity() {
		nextBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		report = reportBo.getReport(ContantsUtil.DEFAULT_TEMP_UID);
		if (report == null) {
			loadData(0);
		} else {
			updateTime = report.getUpdate_time();
			loadData(updateTime);
			// try {
			// exploreJson(report.getContent());
			// } catch (JSONException e) {
			// e.printStackTrace();
			// }
			// if (lists.get(1).is_write) {
			// toStateFragment();
			// } else {
			// addRadio(0);
			// }
		}
	}

	// 读取题目
	private void loadData(long time) {
		loading.show();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sex", ContantsUtil.curInfo.getSex());
		params.put("time", time);
		params.put("mid", ContantsUtil.DEFAULT_TEMP_UID);
		HttpServiceUtil.request(ContantsUtil.GET_ANSWER, "post", params,
				new HttpServiceUtil.CallBack() {
					@Override
					public void callback(String json) {
						loading.dismiss();
						if (AssessActivity.this == null) {
							return;
						}
						try {
							JSONObject groupData = new JSONObject(json);
							if (CheckUtil.checkStatusOk(groupData
									.getInt("status"))) {
								JSONObject data = groupData
										.getJSONObject("data");
								updateTime = data.getLong("updateTime");
								if (data.has("riskQuestionPages")) {
									updateLocalString(data
											.getString("riskQuestionPages"));
									exploreJson(data
											.getString("riskQuestionPages"));
									addRadio(0);
								} else {
									exploreJson(report.getContent());
									toStateFragment();
								}
//								if (lists.size() > 0) {
//									if (lists.get(1).is_write) {
//										toStateFragment();
//									} else {
//										addRadio(0);
//									}
//								}
							} else {
								ToastTool.showToast(groupData.getInt("status"),
										context);
								loading.dismiss();
							}
						} catch (JSONException e) {
							Toast.makeText(context, "读取题目数据失败",
									Toast.LENGTH_SHORT).show();
							loading.dismiss();
							e.printStackTrace();
						}
					}
				});
	}

	private void toStateFragment() {
		isState = true;
		hasState = true;
		titleView.setText("信息与评估");
		progresCon.setVisibility(View.VISIBLE);
		progress.setVisibility(View.GONE);
		nextBtn.setVisibility(View.GONE);
		int max = 0, progres = 0;
		for (ItemModel normal : lists) {
			max++;
			if (normal.is_write) {
				progres++;
			}
		}
		if (max == 0) {
			max = 1;
		}
		progresBar.setMax(max);
		progresBar.setProgress(progres);
		progresTxt.setText("总进度" + progres * 100 / max + "%");
		StateFragment tempFragment = (StateFragment) getSupportFragmentManager()
				.findFragmentByTag("asses_state");
		boolean isAdd = true;
		if (tempFragment == null) {
			tempFragment = StateFragment.getInstance();
			isAdd = false;
		}
		replaceFragment("asses_state", tempFragment, isAdd);
	}

	public void addRadio(Integer check) {
		isState = false;
		curentIndex = check;
		progresCon.setVisibility(View.GONE);
		progress.setVisibility(View.VISIBLE);
		nextBtn.setVisibility(View.VISIBLE);
		progress.removeAllViews();
		LayoutParams params = new LayoutParams(itemWidth, itemHeight);
		params.leftMargin = leftMargin;
		for (int i = 0; i < lists.size(); i++) {
			ImageView tempButton = new ImageView(this);
			if (check >= i) {
				tempButton.setBackgroundColor(getResources().getColor(
						R.color.page_title_color));
			} else {
				tempButton.setBackgroundColor(getResources().getColor(
						R.color.white));
			}
			progress.addView(tempButton, params);
		}
		if (curentIndex == lists.size() - 1) {
			backBtn.setText("上一项");
			nextBtn.setText("提交");
		} else if (curentIndex == 0) {
			backBtn.setText("返回");
			nextBtn.setText("下一项");
		} else {
			backBtn.setText("上一项");
			nextBtn.setText("下一项");
		}
		titleView.setText(lists.get(check).page_name);
		NormalFragment tempFragment = (NormalFragment) getSupportFragmentManager()
				.findFragmentByTag("page" + (check));
		boolean isAdd = true;
		if (tempFragment == null) {
			tempFragment = NormalFragment.getInstance((check));
			isAdd = false;
		}
		replaceFragment("page" + (check), tempFragment, isAdd);
	}

	public void onBackPressed() {
		int count = getSupportFragmentManager().getBackStackEntryCount();
		if (count == 1) {
			finish();
		} else {
			super.onBackPressed();
			Log.d("state", "" + isState + count);
			if (hasState && count == 2) {
				progresCon.setVisibility(View.VISIBLE);
				progress.setVisibility(View.GONE);
				backBtn.setText("返回");
				titleView.setText("信息与评估");
				nextBtn.setVisibility(View.GONE);
			} else {
				progress.removeAllViews();
				progresCon.setVisibility(View.GONE);
				progress.setVisibility(View.VISIBLE);
				LayoutParams params = new LayoutParams(itemWidth, itemHeight);
				params.leftMargin = leftMargin;
				for (int i = 0; i < lists.size(); i++) {
					ImageView tempButton = new ImageView(this);
					if ((count - 1) > i) {
						tempButton.setBackgroundColor(getResources().getColor(
								R.color.page_title_color));
					} else {
						tempButton.setBackgroundColor(getResources().getColor(
								R.color.white));
					}
					progress.addView(tempButton, params);
				}
			}
		}
		// if (isState) {
		// finish();
		// } else {
		// if(curentIndex == 0){
		// toStateFragment();
		// }else{
		// curentIndex--;
		// addRadio(curentIndex);
		// }
		// }
	}

	private void replaceFragment(String tag, BaseFragment tempFragment,
			boolean isAdd) {
		curentfragment = tempFragment;
		FragmentTransaction tran = getSupportFragmentManager()
				.beginTransaction();
		tran.replace(R.id.content, tempFragment, tag);
		if (!isAdd) {
			tran.addToBackStack(tag);
		}
		tran.commitAllowingStateLoss();
	}

	public ItemModel getByKey(Integer position) {
		return lists.get(position);
	}

	public List<ItemModel> getModelList() {
		return lists;
	}

	public String getKeyStr(String key) {
		return (String) maps.get(key);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			if (isState) {
				finish();
			} else {
				if (curentIndex == 0) {
					finish();
				} else {
					curentIndex--;
					addRadio(curentIndex);
				}
			}
			break;
		case R.id.next_btn:
			if (check()) {
				return;
			}
			if (curentIndex < lists.size() - 1) {
				updateLocal();
				curentIndex++;
				addRadio(curentIndex);
			} else {
				updateLocal();
				submitAnswer();
			}
			break;
		}
	}

	private boolean check() {
		if (curentIndex == 0) {
			ItemModel normal = lists.get(Integer.valueOf(curentIndex));
			for (DataModel data : normal.question_line) {
				if (CheckUtil.isNull(data.answer)) {
					Toast.makeText(context, data.question + "不能为空",
							Toast.LENGTH_SHORT).show();
					return true;
				}
			}
		}
		return false;
	}

	// 提交答卷,并finish
	private void submitAnswer() {
		loading.show();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mid", ContantsUtil.DEFAULT_TEMP_UID);
		params.put("sex", ContantsUtil.curInfo.getSex());
		params.put("dataString", gson.toJson(lists));
		HttpServiceUtil.request(ContantsUtil.SUBMIT_ANSWER, "post", params,
				new HttpServiceUtil.CallBack() {
					@Override
					public void callback(String json) {
						loading.dismiss();
						try {
							JSONObject groupData = new JSONObject(json);
							if (CheckUtil.checkStatusOk(groupData
									.getInt("status"))) {
								ContantsUtil.curInfo.setExamStatus(1);
								ContantsUtil.curUser.setSurport_time(0);
								new UserInfoBo(context)
										.updateUserInfo(ContantsUtil.curInfo);
								new UserBo(context)
										.updateUser(ContantsUtil.curUser);
								AlertDialog alert = new AlertDialog(context,
										"答卷提交成功，是否去查看综合报告？");
								alert.setCallBack(new AlertDialog.CallBack() {
									@Override
									public void cancel() {
										finish();
									}

									@Override
									public void callBack() {
										startActivity(null,
												UserReportActivity.class);
										finish();
									}
								});
								alert.show();
							} else {
								Toast.makeText(context,
										groupData.getString("message"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							Toast.makeText(context, "提交答案数据失败",
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				});
	}

	private void exploreJson(String json) throws JSONException {
		Log.d("data", json);
		JSONArray array = new JSONArray(json);
		for (int i = 0; i < array.length(); i++) {
			JSONObject itemObj = array.getJSONObject(i);
			ItemModel normal = new ItemModel();
			normal.page_index = itemObj.getInt("page_index");
			normal.page_name = itemObj.getString("page_name");
			if (itemObj.has("is_write")) {
				normal.is_write = itemObj.getBoolean("is_write");
			}
			if (itemObj.has("update_time")) {
				normal.update_time = itemObj.getLong("update_time");
			}
			JSONArray questions = itemObj.getJSONArray("question_line");
			for (int j = 0; j < questions.length(); j++) {
				JSONObject temp = questions.getJSONObject(j);
				normal.addDataModel(temp);
			}
			lists.add(normal);
		}
	}

	private void updateLocal() {
		lists.get(curentIndex).is_write = true;
		lists.get(curentIndex).update_time = System.currentTimeMillis();
		String temp = gson.toJson(lists);
		if (report == null) {
			report = new Report();
			report.setService_uid(ContantsUtil.DEFAULT_TEMP_UID);
		}
		report.setUpdate_time(updateTime);
		report.setContent(temp);
		reportBo.save(report);
	}

	private void updateLocalString(String content) {
		if (report == null) {
			report = new Report();
			report.setService_uid(ContantsUtil.DEFAULT_TEMP_UID);
		}
		report.setUpdate_time(updateTime);
		report.setContent(content);
		reportBo.save(report);
	}

	public long getUpdateTime() {
		return updateTime;
	}
}
