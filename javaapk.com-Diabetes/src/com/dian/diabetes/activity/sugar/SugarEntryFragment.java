package com.dian.diabetes.activity.sugar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.date.DateView;
import com.dian.diabetes.db.BloodBo;
import com.dian.diabetes.db.CommonBo;
import com.dian.diabetes.db.dao.Common;
import com.dian.diabetes.db.dao.Diabetes;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 血糖录入，首页，分为图标展现和列表展现,图表示entrychartFragment   列表 ｅｎｔｒｙＬｉｓｔ
 * @author longbh
 *
 */
public class SugarEntryFragment extends BaseFragment implements OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.new_sugar)
	private ImageButton newBtn;
	@ViewInject(id = R.id.chart_value)
	private ImageButton chartList;
	@ViewInject(id = R.id.date_controller)
	private RelativeLayout dateController;
	@ViewInject(id = R.id.date_view)
	private DateView dateView;
	@ViewInject(id = R.id.pre)
	private ImageButton preBtn;
	@ViewInject(id = R.id.next)
	private ImageButton nextBtn;
	@ViewInject(id = R.id.current_day)
	private TextView curentDay;
	@ViewInject(id = R.id.date)
	private LinearLayout dateCon;

	// chart
	private EntryBaseFragment entryFragment;
	private SugarActivity activity;
	private boolean ischart = false;
	private Date curentDate;
	private List<Diabetes> data = new ArrayList<Diabetes>();
	private BloodBo bo;
	private Map<String,Common> plugMap;

	public static SugarEntryFragment getInstance() {
		return new SugarEntryFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (SugarActivity) context;
		ContantsUtil.ENTRY_UPDATE = false;
		AnimationUtils.loadAnimation(activity, R.anim.from_top_int);
		plugMap = new CommonBo(activity).getCommMap(ContantsUtil.DIABETES_PLUG_STATE);
		curentDate = new Date();
		bo = new BloodBo(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sugar_entry, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		newBtn.setOnClickListener(this);
		dateController.setOnClickListener(this);
		chartList.setOnClickListener(this);
		preBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		setDay(curentDate.getTime());
		dateView.setCallBack(new DateView.CallBack() {
			@Override
			public void callBack(Date date,boolean state) {
				if(state){
					curentDate = date;
					setDay(curentDate.getTime());
				}
				dateCon.setVisibility(View.VISIBLE);
				dateController.setVisibility(View.VISIBLE);
			}
		});
		switchChartFragment();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			context.finish();
			break;
		case R.id.new_sugar:
			toAddFragment();
			break;
		case R.id.chart_value:
			ischart = !ischart;
			switchChartFragment();
			break;
		case R.id.date_controller:
			//dateView.startAnimation(fromTop);
			dateView.showDate();
			dateCon.setVisibility(View.GONE);
			dateController.setVisibility(View.GONE);
			break;
		case R.id.pre:
			changeDate(-1);
			break;
		case R.id.next:
			changeDate(1);
			break;
		}
	}

	private void changeDate(int sub) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curentDate);
		calendar.add(Calendar.DATE, sub);
		curentDate = calendar.getTime();
		setDay(calendar.getTimeInMillis());
	}

	private void setDay(long mini) {
		setDateTxt(mini);
		loadDayData();
	}
	
	private void setDateTxt(long mini){
		String today = DateUtil.parseToDate(System.currentTimeMillis());
		String temp = DateUtil.parseToDate(mini);
		if (today.equals(temp)) {
			curentDay.setText("今天");
			nextBtn.setEnabled(false);
		} else {
			curentDay.setText(DateUtil.parseToString(curentDate,
					DateUtil.yyyyMMdd));
			nextBtn.setEnabled(true);
		}
	}

	private void loadDayData() {
		data.clear();
		data.addAll(bo.listDayDiabetesSort(ContantsUtil.DEFAULT_TEMP_UID,
				DateUtil.parseToString(curentDate, DateUtil.yyyyMMdd)));
		Log.e("data_size", data.size() + "----");
		if (entryFragment != null) {
			entryFragment.loadEntryData(data);
		}
	}

	/**
	 * 血糖面板切换
	 */
	private void switchChartFragment() {
		String tag;
		boolean isAdd = true;
		if (ischart) {
			tag = "entry_chart";
			entryFragment = (EntryBaseFragment) getChildFragmentManager()
					.findFragmentByTag(tag);
			if (entryFragment == null) {
				entryFragment = EntryChartFragment.getInstance();
				isAdd = false;
			}
			chartList.setImageResource(R.drawable.sugar_entry_list);
		} else {
			tag = "entry_list";
			entryFragment = (EntryBaseFragment) getChildFragmentManager()
					.findFragmentByTag(tag);
			if (entryFragment == null) {
				entryFragment = EntryListFragment.getInstance();
				isAdd = false;
			}
			chartList.setImageResource(R.drawable.sugar_entry_chart);
		}
		replaceFragment(tag, entryFragment, isAdd);
	}

	private void toAddFragment() {
		String tag = "sugar_add_fragment";
		FragmentManager manager = context.getSupportFragmentManager();
		SugarAddFragment tempFragment = (SugarAddFragment) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = SugarAddFragment.getInstance(false, 0, 0);
			tempFragment.setCommonData(new ArrayList<Common>(plugMap.values()));
			tempFragment.setCallBack(new SugarAddFragment.CallBack() {
				@Override
				public void callBack(Date date) {
					curentDate = date;
					setDateTxt(date.getTime());
					updateData();
				}
			});
		}
		if(!tempFragment.isAdded()){
			tempFragment.setDate(curentDate);
			tempFragment.show(manager, tag);
		}
	}

	public void replaceFragment(String tag, Fragment tempFragment, boolean isAdd) {
		FragmentTransaction tran = getChildFragmentManager().beginTransaction();
		tran.replace(R.id.list_chart, tempFragment, tag);
		if (!isAdd) {
			tran.addToBackStack(tag);
		}
		tran.commitAllowingStateLoss();
	}

	public void updateData() {
		ContantsUtil.ENTRY_UPDATE = false;
		ContantsUtil.EFFECT_UPDATE = false;
		ContantsUtil.TOTAL_UPDATE = false;
		ContantsUtil.TOTAL_AVG_UPDATE = false;
		ContantsUtil.TOTAL_HIGH_UPDATE = false;
		ContantsUtil.TOTAL_LOW_UPDATE = false;
		ContantsUtil.TOTAL_COUNT_UPDATE = false;
		Config.resetPageState();
		entryFragment.loadEntryData(data);
	}

	public List<Diabetes> getData() {
		if(!ContantsUtil.ENTRY_UPDATE){
			loadDayData();
			ContantsUtil.ENTRY_UPDATE = true;
		}
		return data;
	}

	public String getDay() {
		return curentDay.getText() + "";
	}
	
	public Map<String,Common> getPlug(){
		return plugMap;
	}
	
	public Date getCurrentDate(){
		return curentDate;
	}
}
