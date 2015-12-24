package com.dian.diabetes.activity.sport;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.eat.TotalBaseFragment;
import com.dian.diabetes.activity.sport.adapter.HeartAdapter;
import com.dian.diabetes.db.dao.Sport;
import com.dian.diabetes.dto.TotalModel;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

public class HeartFragment extends TotalBaseFragment implements OnClickListener {

	@ViewInject(id = R.id.chart)
	private RelativeLayout chart;
	@ViewInject(id = R.id.data_list)
	private ListView dataList;

	// chart
	private GraphicalView lineChart;
	private XYMultipleSeriesDataset dataSet;
	private XYMultipleSeriesRenderer mRenderer;
	private BasicActivity activity;
	private SportTotalFragment parentFragment;

	// data
	private HeartAdapter adapter;
	private List<TotalModel> caloreData;
	private List<TotalModel> listData;

	public static HeartFragment getInstance() {
		return new HeartFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (BasicActivity) context;
		dataSet = new XYMultipleSeriesDataset();
		parentFragment = (SportTotalFragment) getParentFragment();
		XYSeries series = new XYSeries("心率");
		dataSet.addSeries(series);
		caloreData = new ArrayList<TotalModel>();
		listData = new ArrayList<TotalModel>();
		adapter = new HeartAdapter(activity, listData);
		ContantsUtil.HEART_UPDATE = false;
		getRender();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_calories_net, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		loadChart();
		if (!ContantsUtil.HEART_UPDATE) {
			new DataTask().execute();
			ContantsUtil.HEART_UPDATE = true;
		}
		dataList.setAdapter(adapter);
		switchListChart();
	}

	private void loadData() {
		dataSet.clear();
		mRenderer.clearXTextLabels();
		XYSeries series = new XYSeries("心率");
		double value = 0;
		for (int i = 0; i < caloreData.size(); i++) {
			double temp = caloreData.get(i).getValue();
			series.add(i, temp);
			mRenderer.addXTextLabel(i, caloreData.get(i).getDay());
			value = Math.max(value, temp);
		}
		if (value < 30) {
			mRenderer.setYAxisMax(30);
		} else {
			mRenderer.setYAxisMax(value + value / 10);
		}
		dataSet.addSeries(series);
		lineChart.repaint();
		dataList.setAdapter(adapter);
	}

	@SuppressWarnings("deprecation")
	private void loadChart() {
		lineChart = ChartFactory.getLineChartView(context, dataSet, mRenderer);
		chart.addView(lineChart, 0, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	private XYMultipleSeriesRenderer getRender() {
		if (mRenderer != null) {
			return mRenderer;
		}
		float size = getResources().getDimension(R.dimen.text_size_14);
		mRenderer = new XYMultipleSeriesRenderer();
		mRenderer
				.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
		mRenderer.setYAxisMin(0);// 设置y轴最小值是0
		mRenderer.setYAxisMax(30);
		mRenderer.setZoomEnabled(false, false);
		mRenderer.setPanEnabled(false, false);
		mRenderer.setShowGrid(true);
		mRenderer.setYLabelsPadding(10);
		mRenderer.setLabelsTextSize(size);
		mRenderer.setAxisTitleTextSize(size);
		mRenderer.setLegendTextSize(size);

		mRenderer.setXLabelsAlign(Align.CENTER);
		mRenderer.setYLabelsAlign(Align.CENTER);
		mRenderer.setLabelsColor(Color.BLACK);
		mRenderer.setShowAxes(false);
		mRenderer.setShowLegend(false);
		mRenderer.setShowLabels(true);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.WHITE);
		mRenderer.setMarginsColor(Color.WHITE);
		mRenderer.setXLabelsPadding(getResources().getDimension(
				R.dimen.text_size_14));

		// 画一条线
		XYSeriesRenderer r = new XYSeriesRenderer();// (类似于一条线对象)
		r.setColor(Color.rgb(136, 204, 153));// 设置颜色
		r.setPointStyle(PointStyle.POINT);// 设置点的样式
		r.setDisplayChartValues(true);
		r.setDisplayChartValuesDistance(1);
		r.setChartValuesTextSize(size);
		r.setChartValuesFormat(NumberFormat.getInstance());
		r.setFillBelowLine(true);// 是否填充折线图的下方
		r.setFillBelowLineColor(Color.argb(40, 136, 204, 153));// 填充的颜色，如果不设置就默认与线的颜色一致
		r.setLineWidth(2);// 设置线宽
		mRenderer.addSeriesRenderer(r);

		return mRenderer;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			activity.finish();
			break;
		}
	}

	@Override
	public void notifyData() {
		new DataTask().execute();
	}

	public void switchListChart() {
		if (parentFragment.isCurentPage()) {
			chart.setVisibility(View.VISIBLE);
			dataList.setVisibility(View.GONE);

		} else {
			chart.setVisibility(View.GONE);
			dataList.setVisibility(View.VISIBLE);
			adapter.notifyDataSetChanged();
		}
	}

	private class DataTask extends AsyncTask<Object, Object, Object> {

		private List<TotalModel> tempList = new ArrayList<TotalModel>();

		@Override
		protected Object doInBackground(Object... arg0) {
			caloreData.clear();
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, parentFragment.getDay());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			List<Sport> tempsportData = parentFragment.getSportData();
			String day = "";
			int index = 0, sportIndex = 0, delta = -parentFragment.getDelta();
			float sportDetaValue = 0;
			for (int i = 0; i > parentFragment.getDay(); i--) {
				index++;
				calendar.add(Calendar.DATE, 1);
				day = DateUtil.parseToString(calendar.getTime(),
						DateUtil.yyyyMMdd);
				// 运动
				float sportValue = 0;
				for (int j = sportIndex; j < tempsportData.size(); j++) {
					Sport sport = tempsportData.get(j);
					if (day.equals(sport.getDay())) {
						sportIndex++;
						sportValue += sport.getHeart();
					} else {
						break;
					}
				}
				if (sportValue != 0) {
					TotalModel model = new TotalModel();
					model.setDay(day);
					model.setValue(sportValue);
					tempList.add(model);
				}
				sportDetaValue += sportValue;
				if (index == delta || i == (parentFragment.getDay() + 1)) {
					String dayout = DateUtil.parseToString(calendar.getTime(),
							DateUtil.MMdd);
					TotalModel model = new TotalModel();
					model.setDay(dayout);
					model.setValue(sportDetaValue);
					caloreData.add(model);
					sportDetaValue = 0;
					index = 0;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			listData.clear();
			listData.addAll(tempList);
			loadData();
			switchListChart();
		}
	}
}
