package com.dian.diabetes.activity.sugar;

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
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

import com.dian.diabetes.R;
import com.dian.diabetes.db.dao.Diabetes;
import com.dian.diabetes.tool.CommonUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 录入图表fragment
 * @author hua
 *
 */
public class EntryChartFragment extends EntryBaseFragment {
	@ViewInject(id = R.id.chart)
	private RelativeLayout chart;
	private GraphicalView lineChart;

	// chart
	private XYMultipleSeriesDataset dataSet;
	private XYMultipleSeriesRenderer mRenderer;
	private SugarEntryFragment fragment;

	// private float alignment[];

	public static EntryChartFragment getInstance() {
		EntryChartFragment fragment = new EntryChartFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ContantsUtil.ENTRY_UPDATE_VIEW = false;
		dataSet = new XYMultipleSeriesDataset();
		XYSeries series = new XYSeries("血糖线");
		dataSet.addSeries(series);
		getRender();
		fragment = (SugarEntryFragment) getParentFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_entry_chart, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		loadChart();
		// if(!ContantsUtil.ENTRY_UPDATE_VIEW){
		// alignment = new float[]{
		// Config.getFloatPro("levelLow" + ContantsUtil.EAT_PRE),
		// Config.getFloatPro("levelHigh" + ContantsUtil.EAT_PRE) };
		// ContantsUtil.ENTRY_UPDATE_VIEW = true;
		// }
		new DataTask().execute();
	}

	@Override
	public void loadEntryData(List<Diabetes> data) {
		new DataTask().execute();
	}

	private void loadData(List<Diabetes> data) {
		mRenderer.clearXTextLabels();
		dataSet.clear();
		XYSeries series = new XYSeries("血糖线");
		int index = 0;
		for (int i = 0; i < data.size(); i++) {
			Diabetes item = data.get(i);
			series.add(i, (double) (Math.round(item.getValue() * 100)) / 100);
			mRenderer.addXTextLabel(
					i,
					CommonUtil.getValue("diabetes" + item.getType()
							+ item.getSub_type()));
			index = i;
		}
		// if ("今天".equals(fragment.getDay())) {
		// if (subType == 0) {
		// subType++;
		// } else {
		// subType = 0;
		// type++;
		// }
		// for (int i = type; i < 4; i++) {
		// for (int j = subType; j < 2; j++) {
		// index++;
		// mRenderer.addXTextLabel(index,
		// CommonUtil.getValue("diabetes" + i + j));
		// }
		// subType = 0;
		// }
		// mRenderer.removeXTextLabel(index);
		// mRenderer.setXAxisMax(index - 1);
		// } else {
		if (index == 0) {
			index = 1;
		}
		mRenderer.setXAxisMax(index);
		// }
		// 准线
		// mRenderer.setAlign(alignment);
		// mRenderer.setAlignColors(new int[] { Color.RED,
		// Color.rgb(136, 204, 153), Color.RED });
		// mRenderer.setAlignLineColor(Color.rgb(120, 253, 100));

		dataSet.addSeries(series);
		lineChart.repaint();
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
		mRenderer.setYAxisMax(29.9);
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
		r.setPointStyle(PointStyle.CIRCLE);// 设置点的样式
		r.setDisplayChartValues(true);
		r.setDisplayChartValuesDistance(1);
		r.setChartValuesTextSize(size);
		r.setFillBelowLine(true);// 是否填充折线图的下方
		r.setFillBelowLineColor(Color.argb(40, 136, 204, 153));// 填充的颜色，如果不设置就默认与线的颜色一致
		r.setLineWidth(2);// 设置线宽
		mRenderer.addSeriesRenderer(r);

		return mRenderer;
	}

	private class DataTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {
			fragment.getData();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			loadData(fragment.getData());
		}
	}
}
