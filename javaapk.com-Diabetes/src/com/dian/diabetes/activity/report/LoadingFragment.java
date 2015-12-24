package com.dian.diabetes.activity.report;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.widget.anotation.ViewInject;

public class LoadingFragment extends ReportBaseFragment {

	@ViewInject(id = R.id.time_text)
	private TextView timeText;

	private int times = 0;
	private UserReportActivity activity;

	public static LoadingFragment getInstance(int seconds) {
		Bundle bundle = new Bundle();
		bundle.putInt("seconds", seconds);
		LoadingFragment fragment = new LoadingFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (UserReportActivity) context;
		times = getArguments().getInt("seconds");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_report_loading,
				container, false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		setTime(times);
	}

	public void setTime(int time) {
		timeText.setText(Html.fromHtml("预计等待时间：<font color='red'>" + time
				+ "</font>秒"));
	}

	@Override
	public void notifyData() {

	}

}
