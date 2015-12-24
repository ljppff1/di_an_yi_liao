package com.dian.diabetes.activity.indicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.eat.TotalBaseFragment;
import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dto.IndicateDto;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 实验室指标，单条指标的历史趋势界面
 * 
 * @author Administrator
 * 
 */
public class RealDetailFragment extends BaseFragment implements OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.add)
	private ImageButton addBtn;
	@ViewInject(id = R.id.data_list)
	private ImageButton dataList;
	@ViewInject(id = R.id.time)
	private LinearLayout timeBtn;
	@ViewInject(id = R.id.model_switch)
	private TextView modelSwitch;
	@ViewInject(id = R.id.model_switch1)
	private TextView modelSwitch1;
	@ViewInject(id = R.id.add_entry_time)
	private TextView timeSwitch;
	@ViewInject(id = R.id.lipid_con)
	private LinearLayout lipidCon;

	private TimePopUp timePop;
	private IndicatorActivity activity;

	private boolean curChart = true;
	private String union = "";
	private String name = "";
	private float max = 0;
	private float min = 0;
	private float value = 0;
	private boolean isText = false;
	
	private TotalBaseFragment curentFragment;
	private List<IndicateDto> reals;
	private List<IndicateDto> datas;
	private RealPopup realPop;
	private long preTime;
	private int delta = -182;
	private int lastDay = -1092;
	private String timeModel = "最近三年";

	public static RealDetailFragment getInstance(String union, String name,float max,float min,boolean isText,float value) {
		RealDetailFragment fragment = new RealDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putString("union", union);
		bundle.putString("name", name);
		bundle.putFloat("max", max);
		bundle.putFloat("min", min);
		bundle.putFloat("value", value);
		bundle.putBoolean("isText", isText);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (IndicatorActivity) getActivity();
		union = getArguments().getString("union");
		name = getArguments().getString("name");
		max = getArguments().getFloat("max");
		min = getArguments().getFloat("min");
		value = getArguments().getFloat("value");
		isText = getArguments().getBoolean("isText");
		preTime = DateUtil.getPreTime(0, 0, -1092);
		ContantsUtil.IDICATE_UPDATE = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragement_indicator_detail,
				container, false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		timeBtn.setOnClickListener(this);
		addBtn.setOnClickListener(this);
		addBtn.setVisibility(View.GONE);
		dataList.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		lipidCon.setVisibility(View.GONE);
		modelSwitch.setOnClickListener(this);
		if(isText){
			curChart = true;
			if (curChart) {
				toChartFragment();
			} else {
				toDataFragment();
			}
			dataList.setVisibility(View.VISIBLE);
		}else{
			toDataFragment();
			dataList.setVisibility(View.GONE);
		}

		// data
		modelSwitch.setText(name);
		timeSwitch.setText(timeModel);
//		modelSwitch1.setVisibility(View.VISIBLE);
//		modelSwitch.setVisibility(View.GONE);
	}

	/**
	 * to chart fragment
	 */
	private void toChartFragment() {
		String tag = "real_chart";
		boolean isAdd = true;
		RealChartFragment dataFragment = (RealChartFragment) getChildFragmentManager()
				.findFragmentByTag(tag);
		if (dataFragment == null) {
			dataFragment = RealChartFragment.getInstance();
			isAdd = false;
		} else {
			dataFragment.notifyData();
		}
		dataList.setImageResource(R.drawable.sugar_entry_list);
		replaceFragment(tag, dataFragment, isAdd);
	}

	/**
	 * to list fragment
	 */
	private void toDataFragment() {
		String tag = "real_list";
		boolean isAdd = true;
		RealListFragment dataFragment = (RealListFragment) getChildFragmentManager()
				.findFragmentByTag(tag);
		if (dataFragment == null) {
			dataFragment = RealListFragment.getInstance();
			isAdd = false;
		} else {
			dataFragment.notifyData();
		}
		dataList.setImageResource(R.drawable.sugar_entry_chart);
		replaceFragment(tag, dataFragment, isAdd);
	}

	public void replaceFragment(String tag, Fragment tempFragment, boolean isAdd) {
		curentFragment = (TotalBaseFragment) tempFragment;
		FragmentTransaction tran = getChildFragmentManager().beginTransaction();
		tran.replace(R.id.content, tempFragment, tag);
		if (!isAdd) {
			tran.addToBackStack(tag);
		}
		tran.commitAllowingStateLoss();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			activity.toRealFragment();
			break;
		case R.id.data_list:
			curChart = !curChart;
			if (curChart) {
				toChartFragment();
			} else {
				toDataFragment();
			}
			break;
		case R.id.time:
			toRadio(view);
			break;
		case R.id.add:
			//addRealFragment(null, true);
			break;
		case R.id.model_switch:
			if (realPop == null) {
				realPop = new RealPopup(activity);
				realPop.setCallBack(new RealPopup.CallBack() {
					@Override
					public void callBack(int model) {
						IndicateDto dto = datas.get(model);
						max = dto.max;
						min = dto.min;
						MapModel relvalue = realPop.getModel(model);
						name = relvalue.getValue();
						value = dto.result;
						modelSwitch.setText(name);
						if(!dto.hasChart){
							toDataFragment();
							dataList.setVisibility(View.GONE);
						}else{
							if (curChart) {
								toChartFragment();
							} else {
								toDataFragment();
							}
							dataList.setVisibility(View.VISIBLE);
						}
					}
				});
			}
			realPop.addData(datas);
			realPop.showAsDropDown(view);
			break;
		}
	}

//	public void addRealFragment(IndicateValue value, boolean state) {
//		String tag = "";
//		FragmentManager manager = context.getSupportFragmentManager();
//		tag = "weight_impl";
//		ProteinImpFragment fragment = (ProteinImpFragment) manager
//				.findFragmentByTag(tag);
//		if (fragment == null) {
//			fragment = ProteinImpFragment.getInstance(state, 1);
//			fragment.setCallback(new CallBack() {
//				@Override
//				public void callBack() {
//					ContantsUtil.REAL_UPDATE = false;
//					loadData();
//				}
//			});
//		}
//		if (!state) {
//			fragment.setIndicateValue(value);
//		}
//		if (!fragment.isAdded()) {
//			fragment.show(manager, tag);
//		}
//	}

	private void toRadio(View parent) {
		if (timePop == null) {
			timePop = new TimePopUp(activity);
			timePop.setCallBack(new TimePopUp.CallBack() {
				@Override
				public void callBack(int model) {
					switch (model) {
					case 0:
						preTime = DateUtil.getPreTime(0, 0, -30);
						delta = -5;
						lastDay = -35;
						loadData();
						break;
					case 1:
						preTime = DateUtil.getPreTime(0, 0, -186);
						delta = -31;
						lastDay = -186;
						loadData();
						break;
					case 2:
						preTime = DateUtil.getPreTime(0, 0, -365);
						delta = -61;
						lastDay = -366;
						loadData();
						break;
					case 3:
						preTime = DateUtil.getPreTime(0, 0, -730);
						delta = -122;
						lastDay = -732;
						loadData();
						break;
					case 4:
						preTime = DateUtil.getPreTime(0, 0, -1095);
						delta = -182;
						lastDay = -1092;
						loadData();
						break;
					}
					timeModel = timePop.getModel(model).getValue();
					timeSwitch.setText(timeModel);
				}
			});
		}
		timePop.showAsDropDown(parent);
	}

	private void loadData() {
		ContantsUtil.REAL_UPDATE = false;
		curentFragment.notifyData();
	}

	// 查询数据
	public List<IndicateDto> getData() {
		if (!ContantsUtil.REAL_UPDATE) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("item_name", name);
			params.put("start_date", preTime);
			params.put("end_date", System.currentTimeMillis());
			params.put("mid", ContantsUtil.DEFAULT_TEMP_UID);
			String result = HttpServiceUtil.get(ContantsUtil.PERIOD_PRO, params);
			Log.d("result", result);
			if (CheckUtil.isNull(result)) {
				return null;
			}
			try {
				JSONObject groupData = new JSONObject(result);
				if (CheckUtil.checkStatusOk(groupData.getInt("status"))) {
					reals = new ArrayList<IndicateDto>();
					if(groupData.has("data")){
						JSONArray data = groupData.getJSONArray("data");
						float pre = -1;
						for (int i = 0; i < data.length(); i++) {
							IndicateDto dto = new IndicateDto();
							dto.of(data.getJSONObject(i));
							dto.lastValue = pre;
							pre = dto.result;
							reals.add(dto);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			ContantsUtil.REAL_UPDATE = true;
		}
		return reals;
	}

	public void setModel(String union, String name,float max,float min,boolean isText,float value) {
		this.union = union;
		this.name = name;
		this.max = max;
		this.min = min;
		this.isText = isText;
		this.value = value;
		loadData();
		modelSwitch.setText(name);
		if(!isText){
			dataList.setVisibility(View.VISIBLE);
		}else{
			toDataFragment();
			dataList.setVisibility(View.GONE);
		}
	}

	public String getUnion() {
		return union;
	}

	public int getDay() {
		return lastDay;
	}

	public int getDelta() {
		return delta;
	}
	
	public float getMax() {
		return max;
	}

	public float getMin() {
		return min;
	}
	
	public float getValue(){
		return value;
	}

	public void setDataMap(List<IndicateDto> maps) {
		datas =maps;
	}

	public boolean onBackKeyPressed() {
		activity.toRealFragment();
		return true;
	}
	
	public void setCur(){
		curChart = true;
	}
}
