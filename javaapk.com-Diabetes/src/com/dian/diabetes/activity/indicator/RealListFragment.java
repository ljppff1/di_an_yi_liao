package com.dian.diabetes.activity.indicator;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.eat.TotalBaseFragment;
import com.dian.diabetes.activity.indicator.adapter.ValueAdapter;
import com.dian.diabetes.dto.IndicateDto;
import android.os.AsyncTask;
import com.dian.diabetes.widget.anotation.ViewInject;

public class RealListFragment extends TotalBaseFragment {
	
	@ViewInject(id = R.id.sugar_list)
	private ListView entryList;
	
	private RealDetailFragment parent;
	private boolean isCreate = false;
	private List<IndicateDto> datas;
	private ValueAdapter adapter;
	private IndicatorActivity activity;

	public static RealListFragment getInstance() {
		RealListFragment fragment = new RealListFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parent = (RealDetailFragment) getParentFragment();
		datas = new ArrayList<IndicateDto>();
		adapter = new ValueAdapter(context, datas);
		activity = (IndicatorActivity) getActivity();
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
		if(!isCreate){
			new DataTask().execute();
		}
		entryList.setAdapter(adapter);
//		entryList.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View parentView, int position,
//					long id) {
//				//parent.addRealFragment(datas.get(position), false);
//			}
//		});
	}
	
	private class DataTask extends AsyncTask<Object, Object, Object> {
		private List<IndicateDto> temp;
		
		public DataTask(){
			super();
			activity.show();
			datas.clear();
			adapter.notifyDataSetChanged();
		}
		
		@Override
		protected Object doInBackground(Object... arg0) {
			temp = parent.getData();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			activity.hide();
			if(temp == null){
				Toast.makeText(context, "数据处理出错，请稍后再试", Toast.LENGTH_SHORT).show();
			}else{
				datas.clear();
				datas.addAll(temp);
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void notifyData() {
		new DataTask().execute();
	}

}
