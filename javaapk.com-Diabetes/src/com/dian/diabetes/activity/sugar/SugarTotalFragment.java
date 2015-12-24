package com.dian.diabetes.activity.sugar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.db.BloodBo;
import com.dian.diabetes.db.dao.Diabetes;
import com.dian.diabetes.tool.CommonUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 血糖统计
 * 
 * @author longbh
 * 
 */
public class SugarTotalFragment extends BaseFragment implements OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.total)
	private TextView totalBtn;
	@ViewInject(id = R.id.total_list)
	private TextView totalListBtn;
	@ViewInject(id = R.id.data_list)
	private ImageButton dataList;
	@ViewInject(id = R.id.time_radio)
	private TextView timeRadio;
	@ViewInject(id = R.id.total_list_model)
	private LinearLayout totalListModel;
	@ViewInject(id = R.id.total_model)
	private LinearLayout totalModel;
	@ViewInject(id = R.id.model)
	private LinearLayout model;

	private BloodBo bloodBo;
	private Map<String, Diabetes> data;
	private TotalPopDialog popView;
	private TimePopUp timePop;
	private TotalListPopDialog popdialog;
	private SugarActivity activity;
	private boolean curentPage = false;
	private TotalBaseFragment curentFragment;
	private long preTime = 0;
	private int lastDay = -7;
	private int delta = -1;
	private int lastid = 1;

	public static SugarTotalFragment getInstance() {
		return new SugarTotalFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bloodBo = new BloodBo(context);
		ContantsUtil.TOTAL_UPDATE = false;
		// 一周前
		preTime = DateUtil.getPreTime(0, 0, -7);
		// data = bloodBo.listDayDiabetesSort(ContantsUtil.DEFAULT_TEMP_UID,
		// preTime);
		activity = (SugarActivity) context;
		popView = new TotalPopDialog(context);
		popdialog = new TotalListPopDialog(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_total_layout, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		if (curentPage) {
			toChartFragment();
		} else {
			toDataFragment();
		}
		backBtn.setOnClickListener(this);
		totalBtn.setOnClickListener(this);
		dataList.setOnClickListener(this);
		totalListBtn.setOnClickListener(this);
		timeRadio.setOnClickListener(this);
		timeRadio.setText(CommonUtil.getValue("last" + lastid));
		popView.setCallBack(new TotalPopDialog.CallBack() {

			@Override
			public void callBack(int position) {
				curentFragment.setCurentPage(position);
				totalBtn.setText(popView.getModel(position).getValue());
			}
		});
		popdialog.setCallBack(new TotalListPopDialog.CallBack() {
			@Override
			public void callBack(int model) {
				curentFragment.setCurentPage(model);
				totalListBtn.setText(popdialog.getModel(model).getValue());
			}
		});
	}
	
	private void loadData() {
		ContantsUtil.TOTAL_UPDATE = false;
		curentFragment.notifyData();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			context.finish();
			break;
		case R.id.total:
			openPopMenu();
			break;
		case R.id.total_list:
			popdialog.showAsDropDown(view);
			break;
		case R.id.data_list:
			if (!curentPage) {
				toChartFragment();
			} else {
				toDataFragment();
			}
			curentPage = !curentPage;
			break;
		case R.id.time_radio:
			toRadio(view);
			break;
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
						lastid = 2;
						preTime = DateUtil.getPreTime(0, 0, -30);
						delta = -5;
						lastDay = -35;
						loadData();
						break;
					case 2:
						lastid = 3;
						preTime = DateUtil.getPreTime(0, 0, -90);
						delta = -15;
						lastDay = -105;
						loadData();
						break;
					case 0:
						lastid = 1;
						preTime = DateUtil.getPreTime(0, 0, -7);
						delta = -1;
						lastDay = -7;
						loadData();
						break;
					}
					ContantsUtil.TOTAL_AVG_UPDATE = false;
					ContantsUtil.TOTAL_COUNT_UPDATE = false;
					ContantsUtil.TOTAL_HIGH_UPDATE = false;
					ContantsUtil.TOTAL_LOW_UPDATE = false;
					timeRadio.setText(CommonUtil.getValue("last" + lastid));
				}
			});
		}
		timePop.showAsDropDown(parent);
	}

	/**
	 * to chart fragment
	 */
	private void toChartFragment() {
		String tag = "sugar_total_chart";
		boolean isAdd = true;
		SugarTotalChartFragment dataFragment = (SugarTotalChartFragment) getChildFragmentManager()
				.findFragmentByTag(tag);
		if (dataFragment == null) {
			dataFragment = SugarTotalChartFragment.getInstance();
			isAdd = false;
		} else {
			dataFragment.notifyData();
		}
		totalBtn.setText(popView.getModel(dataFragment.getModel()).getValue());
		totalListModel.setVisibility(View.GONE);
		totalModel.setVisibility(View.VISIBLE);
		dataList.setImageResource(R.drawable.sugar_entry_list);
		replaceFragment(tag, dataFragment, isAdd);
	}

	/**
	 * to list fragment
	 */
	private void toDataFragment() {
		String tag = "sugar_total_list";
		boolean isAdd = true;
		SugarTotalListFragment dataFragment = (SugarTotalListFragment) getChildFragmentManager()
				.findFragmentByTag(tag);
		if (dataFragment == null) {
			dataFragment = SugarTotalListFragment.getInstance();
			isAdd = false;
		} else {
			dataFragment.notifyData();
		}
		totalListBtn.setText(popdialog.getModel(dataFragment.getModel())
				.getValue());
		totalListModel.setVisibility(View.VISIBLE);
		totalModel.setVisibility(View.GONE);
		dataList.setImageResource(R.drawable.sugar_entry_chart);
		replaceFragment(tag, dataFragment, isAdd);
	}

	public void replaceFragment(String tag, Fragment tempFragment, boolean isAdd) {
		curentFragment = (TotalBaseFragment) tempFragment;
		FragmentTransaction tran = getChildFragmentManager().beginTransaction();
		tran.replace(R.id.sugar_total_contain, tempFragment, tag);
		if (!isAdd) {
			tran.addToBackStack(tag);
		}
		tran.commitAllowingStateLoss();
	}

	/**
	 * change chart and list
	 * 
	 * @param position
	 */
	public void switchChartModel(int position) {
		MapModel model = popView.getModel(position);
		totalBtn.setText(model.getValue());
	}

	private void openPopMenu() {
		popView.showAsDropDown(totalBtn);
	}

	/**
	 * 以下为公共数据获取方法
	 * 
	 * @return
	 */
	public List<Diabetes> getData() {
		if (!ContantsUtil.TOTAL_UPDATE) {
			data = bloodBo.loadTotal(ContantsUtil.DEFAULT_TEMP_UID, preTime);
			ContantsUtil.TOTAL_UPDATE = true;
		}
		return new ArrayList<Diabetes>(data.values());
	}

	public Map<String, Diabetes> getMapData() {
		if (!ContantsUtil.TOTAL_UPDATE) {
			data = bloodBo.loadTotal(ContantsUtil.DEFAULT_TEMP_UID, preTime);
			ContantsUtil.TOTAL_UPDATE = true;
		}
		return data;
	}

	public int getDay() {
		return lastDay;
	}

	public long getPreTime() {
		return preTime;
	}

	public int getDelta() {
		return delta;
	}
}
