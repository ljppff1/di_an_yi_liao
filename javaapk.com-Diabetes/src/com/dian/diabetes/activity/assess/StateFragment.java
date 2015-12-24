package com.dian.diabetes.activity.assess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.assess.adapter.StateAdapter;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 显示答题状态的fragment
 * @author hua
 *
 */
public class StateFragment extends BaseFragment {

	@ViewInject(id = R.id.sugar_list)
	private ListView planList;

	private AssessActivity activity;
	private StateAdapter adapter;

	public static StateFragment getInstance() {
		return new StateFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (AssessActivity) context;
		adapter = new StateAdapter(context, activity.getModelList());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_entry_list, container,
				false);
		fieldView(view);
		initView(inflater, view);
		return view;
	}

	private void initView(LayoutInflater inflater, View view) {
		planList.setAdapter(adapter);
		planList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View parent, int position,
					long id) {
				activity.addRadio(position);
			}
		});
	}

}
