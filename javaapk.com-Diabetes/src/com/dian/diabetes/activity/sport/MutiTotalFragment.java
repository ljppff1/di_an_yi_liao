package com.dian.diabetes.activity.sport;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.eat.TotalBaseFragment;
import com.dian.diabetes.db.dao.Eat;
import com.dian.diabetes.db.dao.Sport;
import android.os.AsyncTask;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.MutiProgress;
import com.dian.diabetes.widget.anotation.ViewInject;

public class MutiTotalFragment extends TotalBaseFragment {
	
	@ViewInject(id = R.id.total_suggest)
	private TextView totalSuport;
	@ViewInject(id = R.id.total_net)
	private TextView totalNet;
	@ViewInject(id = R.id.total_eat)
	private TextView totalEat;
	@ViewInject(id = R.id.total_sport)
	private TextView totalSport;
	@ViewInject(id = R.id.total_net_day)
	private TextView totalNetAvg;
	@ViewInject(id = R.id.total_eat_day)
	private TextView totalEatAvg;
	@ViewInject(id = R.id.total_sport_day)
	private TextView totalSportAvg;
	@ViewInject(id = R.id.result_persent)
	private TextView resultPersent;
	@ViewInject(id = R.id.calore_result)
	private TextView calorerView;
	@ViewInject(id = R.id.eat_progress)
	private MutiProgress progress;
	
	private float netValue = 0;
	private float eatValue = 0;
	private float sportValue = 0;
	private float netAvgValue = 0;
	private float sportAvgValue = 0;
	private float eatAvgValue = 0;
	
	private float highCalore = 0;
	private float lowCalore = 0;
	private float defSurport = 0;
	private int num = 0;
	private int total = 1;
	private DecimalFormat format;
	
	private BasicActivity activity;
	private SportTotalFragment parentFragment;

	public static MutiTotalFragment getInstance() {
		return new MutiTotalFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentFragment = (SportTotalFragment) getParentFragment();
		format = new DecimalFormat("0.00");
		defSurport = ContantsUtil.curUser.getSupport();
		highCalore = Config.getFloatPro("highCalore");
		lowCalore = Config.getFloatPro("lowCalore");
		ContantsUtil.MULTI_UPDATE = false;
		activity = (BasicActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_eat_mutitotal, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		if(!ContantsUtil.MULTI_UPDATE){
			new DataTask().execute();
			ContantsUtil.MULTI_UPDATE = true;
		}
		setView();
	}
	
	private class DataTask extends AsyncTask<Object, Object, Object> {
		
		@Override
		protected Object doInBackground(Object... arg0) {
			sportValue = 0;
			eatAvgValue = 0;
			sportAvgValue = 0;
			netAvgValue = 0;
			eatValue = 0;
			num = 0;
			total = 0;
			List<Eat> eatData = parentFragment.getData();
			List<Sport> sportData = parentFragment.getSportData();
			String tempDay = "", temp;
			int day = 0, day1 = 0, eatIndex = 0, sportIndex = 0,index = 0;
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, parentFragment.getDay());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			String curDay;
			boolean hasData = false;
			for (int i = 0; i > parentFragment.getDay()+1; i--) {
				calendar.add(Calendar.DATE, 1);
				curDay = DateUtil.parseToString(calendar.getTime(),
						DateUtil.yyyyMMdd);
				Log.e("day", curDay);
				float teatValue = 0, tsportValue = 0,suport = 0;
				for (int j = eatIndex; j < eatData.size(); j++) {
					if (curDay.equals(eatData.get(j).getDay())) {
						teatValue += eatData.get(j).getTotal();
						suport = eatData.get(j).getSurport();
						defSurport = (defSurport + suport)/2;
						hasData = true;
					} else {
						break;
					}
					eatIndex++;
				}
				for (int k = sportIndex; k < sportData.size(); k++) {
					if (curDay.equals(sportData.get(k).getDay())) {
						tsportValue += sportData.get(k).getTotal();
						suport = sportData.get(k).getSuport();
						defSurport = (defSurport + suport)/2;
						hasData = true;
					} else {
						break;
					}
					sportIndex++;
				}
				if(suport == 0){
					suport = defSurport;
				}
				if(hasData){
					float tempNet = teatValue - tsportValue - suport;
					if (tempNet <= suport * 0.1 && tempNet >= -suport * 0.1) {
						num++;
					}
					sportValue += tsportValue;
					eatValue += teatValue;
					index ++;
				}
				hasData = false;
			}
			total = index;
			if(total != 0){
				eatAvgValue = eatValue / total;
				sportAvgValue = sportValue / total;
				netAvgValue = eatAvgValue - sportAvgValue - defSurport;
			}else{
				netAvgValue = - defSurport;
			}
			// for (Eat eat : eatData) {
			// eatValue += eat.getTotal();
			// temp = eat.getDay();
			// if (!tempDay.equals(temp)) {
			// day++;
			// }
			// tempDay = temp;
			// }
			// List<Sport> sportData = parentFragment.getSportData();
			// tempDay = "";
			// for (Sport sport : sportData) {
			// sportValue += sport.getTotal();
			// temp = sport.getDay();
			// if (!tempDay.equals(temp)) {
			// day1++;
			// }
			// tempDay = temp;
			// }
			// total = eatData.size();
			// netValue = eatValue - sportValue;
			// // float persent = netValue/defSurport;
			// if (netValue <= defSurport * 0.1 && netValue >= -defSurport *
			// 0.1) {
			// num++;
			// }
			// int max = Math.max(day, day1);
			// if (netValue != 0 && max != 0) {
			// netAvgValue = netValue / max;
			// }
			// if (eatValue != 0 && max != 0) {
			// eatAvgValue = eatValue / max;
			// }
			// if (sportValue != 0 && max != 0) { 
			// sportAvgValue = sportValue / max;
			// }
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			setView();
		}
	}
	
	private void setView(){
		totalNet.setText(netValue+"");
		totalEat.setText(eatValue+"");
		totalSport.setText(sportValue+"");
		totalNetAvg.setText(format.format(netAvgValue));
		totalEatAvg.setText(format.format(eatAvgValue));
		totalSportAvg.setText(format.format(sportAvgValue));
		totalSuport.setText(defSurport + "");
		progress.setValue(sportAvgValue, eatAvgValue);
		if(total == 0){
			resultPersent.setText("0%");
		}else{
			resultPersent.setText((int)(num*1.0f/total*100) + "%");
		}
		float persent = netAvgValue/defSurport;
		if(persent > Config.getFloatPro("highCalore")){
			calorerView.setText("热量过高");
			calorerView.setTextColor(activity.getResources().getColor(R.color.ear_color3));
		}else if(persent < Config.getFloatPro("lowCalore")){
			calorerView.setText("热量不足");
			calorerView.setTextColor(activity.getResources().getColor(R.color.ear_color1));
		}else{
			calorerView.setText("热量均衡");
			calorerView.setTextColor(activity.getResources().getColor(R.color.ear_color2));
		}
	}

	@Override
	public void notifyData() {
		new DataTask().execute();
	}
}
