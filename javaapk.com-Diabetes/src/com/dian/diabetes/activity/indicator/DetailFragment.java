package com.dian.diabetes.activity.indicator;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.dian.diabetes.activity.sugar.TimePopUp;
import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.db.IndicateBo;
import com.dian.diabetes.db.dao.Indicate;
import com.dian.diabetes.db.dao.IndicateValue;
import com.dian.diabetes.tool.CallBack;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 自测指标明细界面，点击顶部按钮切换数据展现的chartfragment和listfragment
 * 当需要更新数据时调用他们的基类TotalBaseFragment notifydata
 * 
 * @author longbh
 * 
 */
public class DetailFragment extends BaseFragment implements OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.add)
	private ImageButton addBtn;
	@ViewInject(id = R.id.data_list)
	private ImageButton dataList;
	@ViewInject(id = R.id.model)
	private LinearLayout modelBtn;
	@ViewInject(id = R.id.time)
	private LinearLayout timeBtn;
	@ViewInject(id = R.id.model_switch)
	private TextView modelSwitch;
	@ViewInject(id = R.id.add_entry_time)
	private TextView timeSwitch;
	@ViewInject(id = R.id.lipid_con)
	private LinearLayout lipidCon;
	@ViewInject(id = R.id.lipid)
	private TextView lipid;

	private TimePopUp timePop;
	private IndicateDialog dialog;
	private LipidDialog lipidDialog;
	private IndicatorActivity activity;

	private boolean curChart = true;
	private long inId = 0;
	private String union = "";
	private String key = "";
	private String name = "";
	private TotalBaseFragment curentFragment;
	private List<IndicateValue> indicats;
	private Map<String, Indicate> maps;
	private IndicateBo inBo;
	private long preTime;
	private int delta = -1;
	private int lastDay = -7;
	private String timeModel = "近一周";

	public static DetailFragment getInstance(long inId, String key,
			String union, String name) {
		DetailFragment fragment = new DetailFragment();
		Bundle bundle = new Bundle();
		bundle.putLong("inId", inId);
		bundle.putString("union", union);
		bundle.putString("key", key);
		bundle.putString("name", name);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (IndicatorActivity) getActivity();
		inId = getArguments().getLong("inId", 0);
		union = getArguments().getString("union");
		key = getArguments().getString("key");
		name = getArguments().getString("name");
		inBo = new IndicateBo(activity);
		preTime = DateUtil.getPreTime(0, 0, -7);
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
	
	public void onResume(){
		super.onResume();
		if(!ContantsUtil.SELF_DETAIL){
			ContantsUtil.IDICATE_UPDATE = false;
			switchFragment();
			ContantsUtil.SELF_DETAIL = true;
		}
	}

	private void initView(View view) {
		modelBtn.setOnClickListener(this);
		timeBtn.setOnClickListener(this);
		addBtn.setOnClickListener(this);
		dataList.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		lipidCon.setOnClickListener(this);
		switchFragment();
		
		// data
		modelSwitch.setText(name);
		timeSwitch.setText(timeModel);
	}
	
	private void switchFragment(){
		if (curChart) {
			if ("openPress".equals(key)) {
				toPressFragment();
			} else {
				toChartFragment();
			}
		} else {
			toDataFragment();
		}
		if("ch".equals(key) || "tg".equals(key) || "hdl".equals(key) || "ldl".equals(key)){
			lipidCon.setVisibility(View.VISIBLE);
		}else{
			lipidCon.setVisibility(View.GONE);
		}
	}

	/**
	 * to chart fragment
	 */
	private void toChartFragment() {
		String tag = "visual_chart";
		boolean isAdd = true;
		VisualChartFragment dataFragment = (VisualChartFragment) getChildFragmentManager()
				.findFragmentByTag(tag);
		if (dataFragment == null) {
			dataFragment = VisualChartFragment.getInstance();
			isAdd = false;
		} else {
			dataFragment.notifyData();
		}
		dataList.setImageResource(R.drawable.sugar_entry_list);
		replaceFragment(tag, dataFragment, isAdd);
	}

	/**
	 * to press fragment
	 */
	private void toPressFragment() {
		String tag = "press_chart";
		boolean isAdd = true;
		PressChartFragment dataFragment = (PressChartFragment) getChildFragmentManager()
				.findFragmentByTag(tag);
		if (dataFragment == null) {
			dataFragment = PressChartFragment.getInstance();
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
		String tag = "visual_list";
		boolean isAdd = true;
		VisualListFragment dataFragment = (VisualListFragment) getChildFragmentManager()
				.findFragmentByTag(tag);
		if (dataFragment == null) {
			dataFragment = VisualListFragment.getInstance();
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
			activity.startIndicateList();
			break;
		case R.id.data_list:
			curChart = !curChart;
			switchFragment();
			break;
		case R.id.time:
			toRadio(view);
			break;
		case R.id.add:
			addIndicateFragment(null, true);
			break;
		case R.id.model:
			if (dialog == null) {
				dialog = new IndicateDialog(activity);
				dialog.setCallBack(new IndicateDialog.CallBack() {
					@Override
					public void callBack(int model) {
						ContantsUtil.IDICATE_UPDATE = false;
						MapModel value = dialog.getModel(model);
						Indicate indicate = maps.get(value.getKey());
						setModel(indicate.getId(), indicate.getKey(),
								indicate.getUnion(), indicate.getName());
					}
				});
			}
			dialog.showAsDropDown(view);
			break;
		case R.id.lipid_con:
			if(lipidDialog == null){
				lipidDialog = new LipidDialog(activity);
				lipidDialog.setCallBack(new LipidDialog.CallBack() {
					@Override
					public void callBack(int model) {
						ContantsUtil.IDICATE_UPDATE = false;
						MapModel value = lipidDialog.getModel(model);
						Indicate indicate = maps.get(value.getKey());
						if("ch".equals(indicate.getKey())){
							setModel1(indicate.getId(), indicate.getKey(),
									indicate.getUnion(), "胆固醇");
						}else{
							setModel1(indicate.getId(), indicate.getKey(),
									indicate.getUnion(), indicate.getName());
						}
					}
				});
			}
			lipidDialog.showAsDropDown(view);
			break;
		}
	}

	public void addIndicateFragment(IndicateValue value, boolean state) {
		String tag = "";
		FragmentManager manager = context.getSupportFragmentManager();
		if ("weight".equals(key) || "bmi".equals(key)) {
			tag = "weight_impl";
			WeightImpFragment fragment = (WeightImpFragment) manager
					.findFragmentByTag(tag);
			if (fragment == null) {
				fragment = WeightImpFragment.getInstance(state, key, inId);
				fragment.setCallback(new CallBack() {
					@Override
					public void callBack() {
						ContantsUtil.IDICATE_UPDATE = false;
						checkListChart();
					}
				});
			}
			if (!state) {
				fragment.setIndicateValue(value);
			}
			if (!fragment.isAdded()) {
				fragment.show(manager, tag);
			}
		} else if ("waist".equals(key)) {
			tag = "waist_impl";
			WaistImpFragment fragment = (WaistImpFragment) manager
					.findFragmentByTag(tag);
			if (fragment == null) {
				fragment = WaistImpFragment.getInstance(state, key, inId);
				fragment.setCallback(new CallBack() {
					@Override
					public void callBack() {
						ContantsUtil.IDICATE_UPDATE = false;
						checkListChart();
					}
				});
			}
			if (!state) {
				fragment.setIndicateValue(value);
			}
			if (!fragment.isAdded()) {
				fragment.show(manager, tag);
			}
		} else if ("openPress".equals(key)) {
			Bundle bundle = new Bundle();
			bundle.putBoolean("isAdd", state);
			bundle.putString("key", key);
			bundle.putLong("indicateId", inId);
			if(value != null){
				bundle.putString("group", value.getGroup());
			}
			activity.startActivity(bundle, PressImpFragment.class);
		}else if("heart".equals(key)){ 
			tag = "heart_impl";
			HeartImpFragment fragment = (HeartImpFragment) manager
					.findFragmentByTag(tag);
			if (fragment == null) {
				fragment = HeartImpFragment.getInstance(state, key, inId);
				fragment.setCallback(new CallBack() {
					@Override
					public void callBack() {
						ContantsUtil.IDICATE_UPDATE = false;
						checkListChart();
					}
				});
			}
			if (!state) {
				fragment.setIndicateValue(value);
			}
			if (!fragment.isAdded()) {
				fragment.show(manager, tag);
			}
		}else if ("protein".equals(key)) {
			tag = "protein_impl";
			ProteinImpFragment fragment = (ProteinImpFragment) manager
					.findFragmentByTag(tag);
			if (fragment == null) {
				fragment = ProteinImpFragment.getInstance(state, key, inId);
				fragment.setCallback(new CallBack() {
					@Override
					public void callBack() {
						ContantsUtil.IDICATE_UPDATE = false;
						checkListChart();
					}
				});
			}
			if (!state) {
				fragment.setIndicateValue(value);
			}
			if (!fragment.isAdded()) {
				fragment.show(manager, tag);
			}
		}else if("ch".equals(key) || "tg".equals(key) || "hdl".equals(key) || "ldl".equals(key)){
			tag = "lipid_impl";
			LipidImpFragment fragment = (LipidImpFragment) manager
					.findFragmentByTag(tag);
			if (fragment == null) {
				fragment = LipidImpFragment.getInstance(state, key, inId);
				fragment.setCallback(new CallBack() {
					@Override
					public void callBack() {
						ContantsUtil.IDICATE_UPDATE = false;
						checkListChart();
					}
				});
			}
			if (!state) {
				fragment.setIndicateValue(value);
			}
			if (!fragment.isAdded()) {
				fragment.show(manager, tag);
			}
		}
	}

	private void checkListChart() {
		if (curChart) {
			if ("openPress".equals(key)) {
				toPressFragment();
			} else {
				toChartFragment();
			}
		} else {
			toDataFragment();
		}
	}

	private void toRadio(View parent) {
		if (timePop == null) {
			timePop = new TimePopUp(activity);
			timePop.setCallBack(new TimePopUp.CallBack() {
				@Override
				public void callBack(int model) {
					switch (model) {
					case 1:
						preTime = DateUtil.getPreTime(0, 0, -30);
						delta = -5;
						lastDay = -35;
						loadDataTime();
						break;
					case 2:
						preTime = DateUtil.getPreTime(0, 0, -90);
						delta = -15;
						lastDay = -105;
						loadDataTime();
						break;
					case 0:
						preTime = DateUtil.getPreTime(0, 0, -7);
						delta = -1;
						lastDay = -7;
						loadDataTime();
						break;
					}
					timeModel = timePop.getModel(model).getValue();
					timeSwitch.setText(timeModel);
				}
			});
		}
		timePop.showAsDropDown(parent);
	}

	private void loadDataTime() {
		ContantsUtil.IDICATE_UPDATE = false;
		curentFragment.notifyData();
	}

	// 查询数据
	public List<IndicateValue> getData() {
		Log.e("data", DateUtil.parseToDate(preTime));
		if (!ContantsUtil.IDICATE_UPDATE) {
			indicats = inBo.listValue(ContantsUtil.DEFAULT_TEMP_UID, key,
					preTime);
			ContantsUtil.IDICATE_UPDATE = true;
		}
		return indicats;
	}

	public void setModel(long inId, String key, String union, String name) {
		this.inId = inId;
		this.key = key;
		this.union = union;
		this.name = name;
		modelSwitch.setText(name);
		switchFragment();
	}
	
	public void setModel1(long inId, String key, String union, String name) {
		this.inId = inId;
		this.key = key;
		this.union = union;
		this.name = name;
		lipid.setText(name);
		switchFragment();
	}

	public String getUnion() {
		return union;
	}

	public String getKey() {
		return key;
	}

	public int getDay() {
		return lastDay;
	}

	public int getDelta() {
		return delta;
	}

	public void setDataMap(Map<String, Indicate> maps) {
		this.maps = maps;
	}

	public boolean onBackKeyPressed() {
		activity.startIndicateList();
		return true;
	}
	
	public void setCur(){
		curChart = true;
	}
}
