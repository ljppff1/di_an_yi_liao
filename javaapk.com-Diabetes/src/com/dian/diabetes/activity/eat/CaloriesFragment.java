package com.dian.diabetes.activity.eat;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.sugar.TimePopUp;
import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.db.BloodBo;
import com.dian.diabetes.db.EatBo;
import com.dian.diabetes.db.SportBo;
import com.dian.diabetes.db.dao.Eat;
import com.dian.diabetes.db.dao.Sport;
import com.dian.diabetes.tool.CommonUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 饮食统计外壳，用于切换统计类型等
 * 
 * @author longbh
 * 
 */
public class CaloriesFragment extends BaseFragment implements OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.model)
	private LinearLayout modelLayout;
	@ViewInject(id = R.id.model_switch)
	private TextView modelTxt;
	@ViewInject(id = R.id.add_entry_time)
	private TextView timeContro;
	@ViewInject(id = R.id.data_list)
	private ImageButton listChart;

	private TotalBaseFragment dataFragment;

	private List<Eat> eatData;
	private List<Sport> sportData;
	private EatBo eatBo;
	private SportBo sportBo;
	private TotalPopDialog popView;
	private TimePopUp timePop;
	private long preTime = 0;
	private long after = 0;
	private int lastDay = -8;
	private int delta = -1;
	private int lastid = 1;
	private boolean curentPage = false;
	private int currentPosition = 0;

	public static CaloriesFragment getInstance() {
		return new CaloriesFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new BloodBo(context);
		ContantsUtil.TOTAL_EAT_UPDATE = false;
		// 一周前
		after = DateUtil.getPreTime(0, 0, 0);
		preTime = DateUtil.getPreTime(0, 0, -7);
		popView = new TotalPopDialog(context);
		eatBo = new EatBo(context);
		sportBo = new SportBo(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_eat_total, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		modelLayout.setOnClickListener(this);
		timeContro.setOnClickListener(this);
		listChart.setOnClickListener(this);
		timeContro.setText(CommonUtil.getValue("last" + lastid));
		popView.setCallBack(new TotalPopDialog.CallBack() {

			@Override
			public void callBack(int position) {
				currentPosition = position;
				setCurFragment(position);
			}
		});
		if (!curentPage) {
			listChart.setImageResource(R.drawable.sugar_entry_list);
		} else {
			listChart.setImageResource(R.drawable.sugar_entry_chart);
		}
		setCurFragment(currentPosition);
	}

	private void loadData() {
		ContantsUtil.TOTAL_EAT_UPDATE = false;
		dataFragment.notifyData();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			context.finish();
			break;
		case R.id.model:
			popView.showAsDropDown(view);
			break;
		case R.id.add_entry_time:
			toRadio(view);
			break;
		case R.id.data_list:
			if (!curentPage) {
				listChart.setImageResource(R.drawable.sugar_entry_list);
			} else {
				listChart.setImageResource(R.drawable.sugar_entry_chart);
			}
			curentPage = !curentPage;
			dataFragment.switchListChart();
			break;
		}
	}

	private void toRadio(View parent) {
		if (timePop == null) {
			timePop = new TimePopUp(context);
			timePop.setCallBack(new TimePopUp.CallBack() {
				@Override
				public void callBack(int model) {
					switch (model) {
					case 1:
						lastid = 2;
						preTime = DateUtil.getPreTime(0, 0, -30);
						delta = -5;
						lastDay = -31;
						loadData();
						break;
					case 2:
						lastid = 3;
						preTime = DateUtil.getPreTime(0, 0, -90);
						delta = -15;
						lastDay = -91;
						loadData();
						break;
					case 0:
						lastid = 1;
						preTime = DateUtil.getPreTime(0, 0, -7);
						delta = -1;
						lastDay = -8;
						loadData();
						break;
					}
					ContantsUtil.TOTAL_EAT_UPDATE = false;
					ContantsUtil.EAT_MULTI_UPDATE = false;
					ContantsUtil.EAT_STRUCT_UPDATE = false;
					ContantsUtil.NUTRITION_UPDATE = false;
					ContantsUtil.CALORE_STRUCT_UPDATE = false;
					timeContro.setText(CommonUtil.getValue("last" + lastid));
				}
			});
		}
		timePop.showAsDropDown(parent);
	}

	private void setCurFragment(int position) {
		switch (position) {
		case 0:
			toMutitotalFragment();
			break;
		case 1:
			toCalorieAllFragment();
			break;
		case 2:
			toCaloreNetFragment();
			break;
		case 3:
			toCaloreStructFragment();
			break;
		case 4:
			toEatStructFragment();
			break;
		case 5:
			toNutritionFragment();
			break;
		}
		MapModel model = popView.getModel(position);
		modelTxt.setText(model.getValue());
	}

	// 查询数据
	public List<Eat> getData() {
		if (!ContantsUtil.TOTAL_EAT_UPDATE) {
			eatData = eatBo.loadTotalOrder(ContantsUtil.DEFAULT_TEMP_UID,
					preTime,after);
			sportData = sportBo.loadTotalOrder(ContantsUtil.DEFAULT_TEMP_UID,
					preTime,after);
			ContantsUtil.TOTAL_EAT_UPDATE = true;
		}
		return eatData;
	}

	public List<Sport> getSportData() {
		if (!ContantsUtil.TOTAL_EAT_UPDATE) {
			eatData = eatBo.loadTotalOrder(ContantsUtil.DEFAULT_TEMP_UID,
					preTime,after);
			sportData = sportBo.loadTotalOrder(ContantsUtil.DEFAULT_TEMP_UID,
					preTime,after);
			ContantsUtil.TOTAL_EAT_UPDATE = true;
		}
		return sportData;
	}

	public int getDay() {
		return lastDay;
	}

	// 去饮食结构界面
	private void toEatStructFragment() {
		boolean isAdd = true;
		dataFragment = (EatStructFragment) getChildFragmentManager()
				.findFragmentByTag("eat_struct");
		if (dataFragment == null) {
			dataFragment = EatStructFragment.getInstance();
			isAdd = false;
		}
		replaceFragment("eat_struct", dataFragment, isAdd);
	}

	// 去热量净值界面
	private void toCaloreNetFragment() {
		boolean isAdd = true;
		dataFragment = (CaloriesNetFragment) getChildFragmentManager()
				.findFragmentByTag("calore_net");
		if (dataFragment == null) {
			dataFragment = CaloriesNetFragment.getInstance();
			isAdd = false;
		}
		replaceFragment("calore_net", dataFragment, isAdd);
	}

	private void toCaloreStructFragment() {
		boolean isAdd = true;
		dataFragment = (CaloriesStructFragment) getChildFragmentManager()
				.findFragmentByTag("calore_struct");
		if (dataFragment == null) {
			dataFragment = CaloriesStructFragment.getInstance();
			isAdd = false;
		}
		replaceFragment("calore_struct", dataFragment, isAdd);
	}

	private void toMutitotalFragment() {
		boolean isAdd = true;
		dataFragment = (MutiTotalFragment) getChildFragmentManager()
				.findFragmentByTag("muti_total");
		if (dataFragment == null) {
			dataFragment = MutiTotalFragment.getInstance();
			isAdd = false;
		}
		replaceFragment("muti_total", dataFragment, isAdd);
	}

	private void toCalorieAllFragment() {
		boolean isAdd = true;
		dataFragment = (CalorieAllFragment) getChildFragmentManager()
				.findFragmentByTag("calorie_all");
		if (dataFragment == null) {
			dataFragment = CalorieAllFragment.getInstance();
			isAdd = false;
		}
		replaceFragment("calorie_all", dataFragment, isAdd);
	}

	private void toNutritionFragment() {
		boolean isAdd = true;
		dataFragment = (NutritionFragment) getChildFragmentManager()
				.findFragmentByTag("nutrition_strut");
		if (dataFragment == null) {
			dataFragment = NutritionFragment.getInstance();
			isAdd = false;
		}else{
			dataFragment.notifyData();
		}
		replaceFragment("nutrition_strut", dataFragment, isAdd);
	}

	public void replaceFragment(String tag, Fragment tempFragment, boolean isAdd) {
		FragmentTransaction tran = getChildFragmentManager().beginTransaction();
		tran.replace(R.id.eat_total_contain, tempFragment, tag);
		if (!isAdd) {
			tran.addToBackStack(tag);
		}
		tran.commitAllowingStateLoss();
	}

	public int getDelta() {
		return delta;
	}

	public boolean isCurentPage() {
		return curentPage;
	}

	public long getPreTime() {
		return preTime;
	}

}
