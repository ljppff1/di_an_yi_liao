package com.dian.diabetes.activity.sugar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.sugar.adapter.SugarEntryAdapter;
import com.dian.diabetes.db.AlarmBo;
import com.dian.diabetes.db.dao.Alarm;
import com.dian.diabetes.db.dao.Common;
import com.dian.diabetes.db.dao.Diabetes;
import android.os.AsyncTask;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 录入列表fragment
 * @author hua
 *
 */
public class EntryListFragment extends EntryBaseFragment {

	@ViewInject(id = R.id.sugar_list)
	private ListView sugarList;
	@ViewInject(id = R.id.toast_container)
	private RelativeLayout toastCon;

	private SugarActivity activity;
	private SugarEntryAdapter adapter;
	private SugarEntryFragment entryFragment;
	private List<Diabetes> listData;
	private int todayWeek = 1;

	// private float levelPre[];
	// private float levelAfter[];

	public static EntryListFragment getInstance() {
		EntryListFragment fragment = new EntryListFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (SugarActivity) getActivity();
		entryFragment = (SugarEntryFragment) getParentFragment();
		todayWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
		// if (todayWeek == 0) {
		// todayWeek = 7;
		// }
		listData = new ArrayList<Diabetes>();
		adapter = new SugarEntryAdapter(context, listData);
		adapter.setPlugMap(entryFragment.getPlug());
		new DataTask().execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_entry_list, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		// levelPre = new float[] {
		// Config.getFloatPro("levelLow" + ContantsUtil.EAT_PRE),
		// Config.getFloatPro("levelHigh" + ContantsUtil.EAT_PRE) };
		// levelAfter = new float[] {
		// Config.getFloatPro("levelLow" + ContantsUtil.EAT_AFTER),
		// Config.getFloatPro("levelHigh" + ContantsUtil.EAT_AFTER) };
		sugarList.setAdapter(adapter);
		sugarList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Diabetes diabetes = (Diabetes) parent.getAdapter().getItem(
						position);
				if (diabetes.getId() != null) {
					toAddFragment(true, diabetes.getId(), diabetes.getType()
							* 10 + diabetes.getSub_type());
				} else {
					toAddFragment(false, -1,
							diabetes.getType() * 10 + diabetes.getSub_type());
				}
			}
		});
	}

	private void toAddFragment(boolean state, long id, int type) {
		String tag = "sugar_edit_fragment";
		FragmentManager manager = context.getSupportFragmentManager();
		SugarAddFragment tempFragment = (SugarAddFragment) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = SugarAddFragment.getInstance(state, id, type);
			tempFragment.setCommonData(new ArrayList<Common>(entryFragment
					.getPlug().values()));
			tempFragment.setCallBack(new SugarAddFragment.CallBack() {
				@Override
				public void callBack(Date date) {
					entryFragment.updateData();
				}
			});
		}
		if (!tempFragment.isAdded()) {
			tempFragment.setDate(entryFragment.getCurrentDate());
			tempFragment.show(manager, tag);
		}
	}

	@Override
	public void loadEntryData(List<Diabetes> data) {
		new DataTask().execute();
	}

	private class DataTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {
			List<Diabetes> data = entryFragment.getData();
			return data;
		}

		@Override
		protected void onPostExecute(Object result) {
			List<Diabetes> data = (List<Diabetes>) result;
			if ("今天".equals(entryFragment.getDay())) {
				convertData(data);
				if(listData.size() == 0){
					toastCon.setVisibility(View.VISIBLE);
					sugarList.setVisibility(View.GONE);
				}else{
					toastCon.setVisibility(View.GONE);
					sugarList.setVisibility(View.VISIBLE);
				}
			} else {
				toastCon.setVisibility(View.GONE);
				sugarList.setVisibility(View.VISIBLE);
				listData.clear();
				listData.addAll(data);
			}
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 计算当天数据，并统计当日未测试的
	 * 
	 * @param data
	 */
	private void convertData(List<Diabetes> data) {
		listData.clear();
		Map<String,Alarm> alarmList = new AlarmBo(activity)
				.getPlanMapAlarm(ContantsUtil.DEFAULT_TEMP_UID,ContantsUtil.SUGAR_ALARM);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				Diabetes diabetes = getDiabetes(data,i,j);
				if(diabetes != null){
					listData.add(diabetes);
					continue;
				}else{
					int subType = i*10+j;
					Alarm alarm = alarmList.get("plan" + subType);
					if(alarm != null && alarm.isSetAll(todayWeek)){
						diabetes = new Diabetes();
						diabetes.setType((short) (i));
						diabetes.setSub_type((short) (j));
						diabetes.setCreate_time(alarm.getAlarm_time());
						diabetes.setLevel(-1);
						listData.add(diabetes);
					}
				}
			}
		}
//		int index = 0;
//		for (Alarm alarm : alarmList) {
//			if (alarm.isSet(todayWeek)) {
//				int type = (alarm.getSub_type() / 10);
//				int subType = alarm.getSub_type() % 10;
//				boolean state = false;
//				if (index < data.size()) {
//					for (int i = index; i < data.size(); i++) {
//						Diabetes item = data.get(i);
//						if (item.getType() == type
//								&& item.getSub_type() == subType) {
//							listData.add(item);
//							state = true;
//							index++;
//							break;
//						}
//					}
//				}
//				if (!state) {
//					Diabetes diabetes = new Diabetes();
//					diabetes.setType((short) (type));
//					diabetes.setSub_type((short) (subType));
//					diabetes.setCreate_time(alarm.getAlarm_time());
//					if (index < data.size()) {
//						diabetes.setLevel(-1);
//					} else {
//						diabetes.setLevel(-2);
//					}
//					listData.add(diabetes);
//				}
//			}
//		}
//		if (index < data.size() - 1) {
//			for (int i = index; i < data.size(); i++) {
//				listData.add(data.get(i));
//			}
//		}
	}
	
	private Diabetes getDiabetes(List<Diabetes> data,int type,int subType){
		for (int i = 0; i < data.size(); i++) {
			Diabetes item = data.get(i);
			if (item.getType() == type
					&& item.getSub_type() == subType) {
				return item;
			}
		}
		return null;
	}
}
