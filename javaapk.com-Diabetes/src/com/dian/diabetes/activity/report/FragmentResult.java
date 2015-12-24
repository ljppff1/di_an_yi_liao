package com.dian.diabetes.activity.report;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.dian.diabetes.R;
import com.dian.diabetes.activity.report.adapter.ReportAdapter;
import com.dian.diabetes.dto.ReportDto;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 检查报告,是指标检测的一部分，只负责数据展现，数据展现在上一层处理,notify方法里放更新界面的代码
 * 
 * @author longbh
 * 
 */
public class FragmentResult extends ReportBaseFragment {

	@ViewInject(id = R.id.report_list)
	private ListView reportList;
	
	private UserReportActivity activity;
	private boolean isCreate = false;

	private ReportAdapter adapter;
	private List<ReportDto> data;

	public static FragmentResult getInstance() {
		return new FragmentResult();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (UserReportActivity) context;
		data = new ArrayList<ReportDto>();
		adapter = new ReportAdapter(activity, data);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_report_layout,
				container, false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		if(!isCreate){
			data.addAll(activity.getResultList());
			isCreate = true;
		}
		reportList.setAdapter(adapter);
	}

	@Override
	public void notifyData() {
		if(getActivity() == null){
			return;
		}
		data.clear();
		data.addAll(activity.getResultList());
		adapter.notifyDataSetChanged();
	}
}
