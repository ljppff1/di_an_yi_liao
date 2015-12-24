package com.dian.diabetes.activity.sport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.assess.AssessActivity;
import com.dian.diabetes.activity.sport.adapter.PlanAdapter;
import com.dian.diabetes.dto.PlanDto;
import com.dian.diabetes.dto.SportDto;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

public class PlanFragment extends BaseFragment implements OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.plan_list)
	private ListView planList;

	private boolean isCreate = false;
	private List<PlanDto> data;
	private PlanAdapter adapter;

	private SportActivity activity;

	public static PlanFragment getInstance() {
		return new PlanFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (SportActivity) context;
		data = new ArrayList<PlanDto>();
		adapter = new PlanAdapter(activity, data);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sport_plan, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		planList.setAdapter(adapter);
		if (!isCreate) {
			loadPlan();
			isCreate = true;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			activity.finish();
			break;
		}

	}

	private void loadPlan() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mid", ContantsUtil.DEFAULT_TEMP_UID);
		activity.show();
		HttpServiceUtil.request(ContantsUtil.SPORT_PLAN, "post", params,
				new HttpServiceUtil.CallBack() {
					@Override
					public void callback(String json) {
						if (getActivity() == null) {
							return;
						}
						activity.hide();
						try {
							JSONObject groupData = new JSONObject(json);
							if (CheckUtil.checkStatusOk(groupData
									.getInt("status"))) {
								if (groupData.has("data")) {
									JSONArray array = groupData
											.getJSONArray("data");
									for (int i = 0; i < array.length(); i++) {
										PlanDto dto = new PlanDto();
										dto.of(array.getJSONObject(i));
										data.add(dto);
									}
								}
								// JSONObject object = new JSONObject("{"
								// + "\"index\": 1,\"name\": \"原则\","
								// + "\"contain\": [\"合理控制总热量\","
								// + "\"充足的碳水化合物摄入量；\","
								// + "\"控制脂肪和胆固醇的摄入量\","
								// + "\"适量的蛋白质摄入量；\","
								// + "\"充足的膳食纤维摄入量；\"," + "\"清淡饮食。\""
								// + "]}");
								// PlanDto dto = new PlanDto();
								// dto.of(object);
								// data.add(dto);
								adapter.notifyDataSetChanged();
							} else if(groupData.getInt("status") == 5000110){
								activity.finish();
								Toast.makeText(context, "您还未做一次完整的问卷答题，请先答题", Toast.LENGTH_SHORT).show();
								activity.startActivity(null, AssessActivity.class);
							} else {
								ToastTool.showToast(groupData.getInt("status"),
										context);
							}
						} catch (JSONException e) {
							Toast.makeText(context, "数据解析出错",
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				});
	}
}
