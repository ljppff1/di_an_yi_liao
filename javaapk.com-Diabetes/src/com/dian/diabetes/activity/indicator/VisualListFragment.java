package com.dian.diabetes.activity.indicator;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.eat.TotalBaseFragment;
import com.dian.diabetes.activity.indicator.adapter.VisualAdapter;
import com.dian.diabetes.db.dao.IndicateValue;
import android.os.AsyncTask;
import com.dian.diabetes.widget.anotation.ViewInject;

public class VisualListFragment extends TotalBaseFragment {
	
	@ViewInject(id = R.id.sugar_list)
	private ListView entryList;
	
	private DetailFragment parent;
	private boolean isCreate = false;
	private List<IndicateValue> datas;
	private VisualAdapter adapter;

	public static VisualListFragment getInstance() {
		VisualListFragment fragment = new VisualListFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parent = (DetailFragment) getParentFragment();
		datas = new ArrayList<IndicateValue>();
		adapter = new VisualAdapter(context, datas,parent.getKey());
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
		entryList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View parentView, int position,
					long id) {
				parent.addIndicateFragment(datas.get(position), false);
			}
		});
	}
	
	private class DataTask extends AsyncTask<Object, Object, Object> {
		private List<IndicateValue> temp;
		@Override
		protected Object doInBackground(Object... arg0) {
			temp = parent.getData();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			datas.clear();
			Float pre = null;
			for(IndicateValue valueItem : temp){
				if(pre == null){
					valueItem.setUp_down(-1);
				}else{
					if(pre - valueItem.getValue() > 0){
						valueItem.setUp_down(1);
					}else if(pre - valueItem.getValue() == 0){
						valueItem.setUp_down(-1);
					}else{
						valueItem.setUp_down(0);
					}
				}
				datas.add(valueItem);
				pre = valueItem.getValue();
			}
			adapter.notifyDataSetChanged();
			adapter.setUnion(parent.getUnion(),parent.getKey());
		}
	}

	@Override
	public void notifyData() {
		new DataTask().execute();
	}

}
