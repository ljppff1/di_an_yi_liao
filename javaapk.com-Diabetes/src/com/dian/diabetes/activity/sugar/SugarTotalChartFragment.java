package com.dian.diabetes.activity.sugar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.sugar.adapter.SugarTotalAdapter;
import com.dian.diabetes.activity.sugar.model.CountModel;
import com.dian.diabetes.db.dao.Diabetes;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.VerticalViewPager;
import com.dian.diabetes.widget.anotation.ViewInject;

public class SugarTotalChartFragment extends TotalBaseFragment {

	@ViewInject(id = R.id.sugar_total)
	private VerticalViewPager totalView;

	private SugarTotalAdapter adaper;
	private SugarTotalFragment parent;
	private int model = 0;
	private List<List<CountModel>> sugarData;
	private List<String> data;
	private boolean iscreate = false;
	
	public static SugarTotalChartFragment getInstance() {
		return new SugarTotalChartFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parent = (SugarTotalFragment) getParentFragment();
		sugarData = new ArrayList<List<CountModel>>();		 
		data = initAdapterData();
		adaper = new SugarTotalAdapter(getChildFragmentManager(), data);
	}

	/**
	 * 初始化数据，写死算了
	 * 
	 * @return
	 */
	private List<String> initAdapterData() {
		List<String> data = new ArrayList<String>();
		data.add("44");
		data.add(ContantsUtil.BRECKFAST + "" + ContantsUtil.EAT_PRE);
		data.add(ContantsUtil.BRECKFAST + "" + ContantsUtil.EAT_AFTER);
		data.add(ContantsUtil.LAUNCH + "" + ContantsUtil.EAT_PRE);
		data.add(ContantsUtil.LAUNCH + "" + ContantsUtil.EAT_AFTER);
		data.add(ContantsUtil.DINNER + "" + ContantsUtil.EAT_PRE);
		data.add(ContantsUtil.DINNER + "" + ContantsUtil.EAT_AFTER);
		data.add(ContantsUtil.SLEEP_PRE + "" + ContantsUtil.EAT_PRE);
		return data;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sugar_total, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {		
		if (!Config.getPageState(className)) {
			new DataTask().execute();
			Config.pageMap.put(className, true);			
		}		
		totalView.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				parent.switchChartModel(position);
				model = position;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int position) {
			}
		});		
	}

	private void initData() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, parent.getDay());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Map<String, Diabetes> list = parent.getMapData();		
		List<CountModel> all = new ArrayList<CountModel>();
		List<CountModel> brekPre = new ArrayList<CountModel>();
		List<CountModel> brekAfter = new ArrayList<CountModel>();
		List<CountModel> lunchPre = new ArrayList<CountModel>();
		List<CountModel> lunchAfter = new ArrayList<CountModel>();
		List<CountModel> dinnerPre = new ArrayList<CountModel>();
		List<CountModel> dinnerAfter = new ArrayList<CountModel>();
		List<CountModel> sleep = new ArrayList<CountModel>();
		int index = 0,delta = - parent.getDelta();
		int brekPrei = 0, brekAfteri = 0, lunchPrei = 0, lunchAfteri = 0, dinnerPrei = 0, dinnerAfteri = 0, sleepi = 0; 
		float brekPref = 0, brekAfterf = 0, lunchPref = 0, lunchAfterf = 0, dinnerPref = 0, dinnerAfterf = 0, sleepf = 0;
		int preMonth = -1;
		for (int i = 0; i > parent.getDay(); i=i-delta) {
			calendar.add(Calendar.DATE, delta);
			index++;
			String day = DateUtil.parseToString(calendar.getTime(),
					DateUtil.yyyyMMdd);
			int tempMonth = calendar.get(Calendar.MONTH);
			String dayout;
			if(tempMonth == preMonth){
				dayout = DateUtil.parseToString(calendar.getTime(),
						DateUtil.dd);
			}else{
				dayout = DateUtil.parseToString(calendar.getTime(),
						DateUtil.MMdd);
			}
			preMonth = tempMonth;
			// 早餐前
			Diabetes bpre = list.get(day + ContantsUtil.BRECKFAST + ContantsUtil.EAT_PRE);
			Diabetes bafter = list.get(day + ContantsUtil.BRECKFAST + ContantsUtil.EAT_AFTER);
			Diabetes lpre = list.get(day + ContantsUtil.LAUNCH + ContantsUtil.EAT_PRE);
			Diabetes lafter = list.get(day + ContantsUtil.LAUNCH + ContantsUtil.EAT_AFTER);
			Diabetes dpre = list.get(day + ContantsUtil.DINNER + ContantsUtil.EAT_PRE);
			Diabetes dafter = list.get(day + ContantsUtil.DINNER + ContantsUtil.EAT_AFTER);
			Diabetes sleepDiabetes = list.get(day + ContantsUtil.SLEEP_PRE + ContantsUtil.EAT_PRE);
			if (bpre != null) {
				brekPref += bpre.getValue();
				brekPrei ++;
			}
			if (bafter != null) {
				brekAfterf += bafter.getValue();
				brekAfteri ++;
			}
			if (lpre != null) {
				lunchPref += lpre.getValue();
				lunchPrei ++;
			}
			if (lafter != null) {
				lunchAfterf += lafter.getValue();
				lunchAfteri ++;
			}
			if (dpre != null) {
				dinnerPref += dpre.getValue();
				dinnerPrei ++;
			}
			if (dafter != null) {
				dinnerAfterf += dafter.getValue();
				dinnerAfteri ++;
			}
			if (sleepDiabetes != null) {
				sleepf += sleepDiabetes.getValue();
				sleepi ++;
			}
			//if (index == delta || i == (parent.getDay() + 1)) {				
				brekPre.add(getCountModel(dayout,brekPref/brekPrei));
				brekAfter.add(getCountModel(dayout,brekAfterf/brekAfteri));
				lunchPre.add(getCountModel(dayout,lunchPref/lunchPrei));
				lunchAfter.add(getCountModel(dayout,lunchAfterf/lunchAfteri));
				dinnerPre.add(getCountModel(dayout,dinnerPref/dinnerPrei));
				dinnerAfter.add(getCountModel(dayout,dinnerAfterf/dinnerAfteri));
				sleep.add(getCountModel(dayout,sleepf/sleepi));
				float allValue = (brekPref + brekAfterf +lunchPref+lunchAfterf+dinnerPref+dinnerAfterf+sleepf)
						/(brekPrei + brekAfteri +lunchPrei+lunchAfteri+dinnerPrei+dinnerAfteri+sleepi);
				all.add(getCountModel(dayout,allValue));
				index = 0;
				brekPref = brekAfterf = lunchPref = lunchAfterf = 0;
				dinnerPref = dinnerAfterf = sleepf = 0;
				brekPrei = brekAfteri = lunchPrei = lunchAfteri = 0;
				dinnerPrei = dinnerAfteri = sleepi = 0;
			//}				
		}
		sugarData.clear();
		sugarData.add(all);
		sugarData.add(brekPre);
		sugarData.add(brekAfter);
		sugarData.add(lunchPre);
		sugarData.add(lunchAfter);
		sugarData.add(dinnerPre);
		sugarData.add(dinnerAfter);
		sugarData.add(sleep);
	}
	
	private CountModel getCountModel(String day,float value){
		CountModel model = new CountModel();
		if(Float.isNaN(value)){
			model.setValue(0);
		}else{
			model.setValue((float)(Math.round(value*100))/100);
		}		
		model.setDate(day);		
		return model;
	}
	
	public void onDestroy(){
		super.onDestroy();
	}

	public void notifyData() {
		new DataTask().execute();
	}

	public void setCurentPage(int position) {
		totalView.setCurrentItem(position);
	}
	
	public List<CountModel> getData(int position){
		return sugarData.get(position);
	}

	public int getModel() {
		return model;
	}
	
	private class DataTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {
			initData();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if(iscreate){
				adaper.notifyDataSetChanged();
			}else{
				totalView.setAdapter(adaper);
				totalView.setCurrentItem(model);
			}
		}
	}
}
