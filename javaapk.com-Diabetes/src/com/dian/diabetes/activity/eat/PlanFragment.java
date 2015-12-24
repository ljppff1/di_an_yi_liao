package com.dian.diabetes.activity.eat;

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
import com.dian.diabetes.activity.eat.adapter.PlanAdapter;
import com.dian.diabetes.dto.PlanDto;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 计划，读取展现服务端数据
 * @author hua
 *
 */
public class PlanFragment extends BaseFragment implements OnClickListener{
	
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.plan_list)
	private ListView planList;
	
	private boolean isCreate = false;

	private List<PlanDto> data;
	private PlanAdapter adapter;
	private EatActivity activity;

	public static PlanFragment getInstance() {
		return new PlanFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (EatActivity) context;
		data = new ArrayList<PlanDto>();
		adapter = new PlanAdapter(activity, data);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_eat_plan, container,
				false);
		fieldView(view);
		initView(inflater,view);
		return view;
	}

	private void initView(LayoutInflater inflater, View view) {
		backBtn.setOnClickListener(this);
//		View headerView = inflater.inflate(R.layout.widget_eat_plan, null);
//		planList.addHeaderView(headerView);
		planList.setAdapter(adapter);
		if(!isCreate){
			loadPlan();
			isCreate = true;
		}
	}
	
	private void loadPlan(){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("mid", ContantsUtil.DEFAULT_TEMP_UID);
		activity.show();
		HttpServiceUtil.request(ContantsUtil.EAT_PLAN, "post", params, new HttpServiceUtil.CallBack() {
			
			@Override
			public void callback(String json) {
				if(getActivity() == null){
					return;
				}
				activity.hide();
				try {
					JSONObject jsonData = new JSONObject(json);
					if (CheckUtil.checkStatusOk(jsonData.getInt("status"))) {
						data.clear();
						if (jsonData.has("data")) {
							JSONArray array = jsonData
									.getJSONArray("data");
							for (int i = 0; i < array.length(); i++) {
								PlanDto dto = new PlanDto();
								dto.of(array.getJSONObject(i));
								data.add(dto);
							}
						}
						adapter.notifyDataSetChanged();
					} else if(jsonData.getInt("status") == 5000110){
						activity.finish();
						Toast.makeText(context, "您还未做一次完整的问卷答题，请先答题", Toast.LENGTH_SHORT).show();
						activity.startActivity(null, AssessActivity.class);
					} else {
						ToastTool.showToast(jsonData.getInt("status"),context);
					}
				} catch (JSONException e) {
					Toast.makeText(context, "数据解析出错",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.back_btn:
			activity.finish();
			break;
		}
	}
	
}
