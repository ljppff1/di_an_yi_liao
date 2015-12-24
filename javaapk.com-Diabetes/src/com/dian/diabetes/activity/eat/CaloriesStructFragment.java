package com.dian.diabetes.activity.eat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.eat.adapter.LabelAdapter;
import com.dian.diabetes.db.dao.Eat;
import com.dian.diabetes.dto.TotalModel;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 热量结构,初始化了一些图标，设置了几种默认颜色值
 * @author hua
 *
 */
public class CaloriesStructFragment extends TotalBaseFragment {

	@ViewInject(id = R.id.pie_chart)
	private RelativeLayout pieChart;
	@ViewInject(id = R.id.chart)
	private RelativeLayout chartLayout;
	@ViewInject(id = R.id.data_list)
	private ListView dataList;
	@ViewInject(id = R.id.chart_container)
	private LinearLayout chartContainer;

	private GraphicalView pie;
	private GraphicalView line;
	private XYMultipleSeriesRenderer pieRenderer;
	private XYMultipleSeriesRenderer lineRenderer;
	private XYMultipleSeriesDataset lineDataset;
	private CategorySeries mSeries;
	private static int[] COLORS = new int[] { Color.rgb(072, 116, 169),
		Color.rgb(171, 70, 066), Color.rgb(137, 165, 78),
		Color.rgb(112, 88, 143), Color.rgb(064, 153, 175),
		Color.rgb(213, 133, 060) };
	private CaloriesFragment parentFragment;
	private EatActivity activity;

	private List<TotalModel> values;
	private LabelAdapter adapter;

	public static CaloriesStructFragment getInstance() {
		return new CaloriesStructFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (EatActivity) context;
		lineDataset = new XYMultipleSeriesDataset();
		mSeries = new CategorySeries("");
		XYSeries series = new XYSeries("热量柱形图");
		lineDataset.addSeries(series);
		ContantsUtil.CALORE_STRUCT_UPDATE = false;
		getPieRender();
		getLineRender();
		values = new ArrayList<TotalModel>();
		parentFragment = (CaloriesFragment) getParentFragment();
		adapter = new LabelAdapter(activity, values);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_calore_struct,
				container, false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		loadPieChart();
		loadLineChart();
		if (!ContantsUtil.CALORE_STRUCT_UPDATE) {
			new DataTask().execute();
			ContantsUtil.CALORE_STRUCT_UPDATE = true;
		}
		dataList.setAdapter(adapter);
		switchListChart();
	}

	@SuppressWarnings("deprecation")
	private void loadPieChart() {
		pie = ChartFactory.getPieChartView(context, mSeries, pieRenderer);
		pieChart.addView(pie, 0, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	@SuppressWarnings("deprecation")
	private void loadLineChart() {
		line = ChartFactory.getBarChartView(activity, lineDataset,
				lineRenderer, Type.DEFAULT);
		chartLayout.addView(line, 0, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	private void loadPieData() {
		mSeries.clear();
		pieRenderer.removeAllRenderers();
		for (int i = 0; i < values.size(); i++) {
			mSeries.add(values.get(i).getDay(), values.get(i).getValue());
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			renderer.setColor(COLORS[i % COLORS.length]);
			pieRenderer.addSeriesRenderer(renderer);
		}
	}

	private void loadLineData() {
		lineDataset.clear();
		XYSeries series = new XYSeries("各类热量摄取");
		double value = 0;
		for (int i = 0; i < values.size(); i++) {
			String label = values.get(i).getDay();
			series.add(i+1, values.get(i).getValue());
			lineRenderer.addXTextLabel(i+1, label);
			value = Math.max(value, values.get(i).getValue());
		}
		lineDataset.addSeries(series);
		if(values.size() < 4){
			lineRenderer.setXAxisMax(5);
		}else{
			lineRenderer.setXAxisMax(values.size());
		}
		lineRenderer.setYAxisMax(value + value/5);
	}

	private XYMultipleSeriesRenderer getPieRender() {
		if (pieRenderer != null) {
			return pieRenderer;
		}
		float size = getResources().getDimension(R.dimen.text_size_14);
		pieRenderer = new XYMultipleSeriesRenderer();
		pieRenderer
				.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
		pieRenderer.setYAxisMin(0);// 设置y轴最小值是0
		pieRenderer.setYAxisMax(30);
		pieRenderer.setZoomEnabled(false, false);
		pieRenderer.setPanEnabled(false, false);
		pieRenderer.setShowGrid(true);
		pieRenderer.setYLabelsPadding(10);
		pieRenderer.setLabelsTextSize(size);
		pieRenderer.setAxisTitleTextSize(size);
		pieRenderer.setLegendTextSize(size);

		pieRenderer.setXLabelsAlign(Align.CENTER);
		pieRenderer.setYLabelsAlign(Align.CENTER);
		pieRenderer
				.setLabelsColor(getResources().getColor(R.color.label_color));
		pieRenderer.setShowAxes(true);
		pieRenderer.setShowLegend(false);
		pieRenderer.setShowLabels(true);
		pieRenderer.setApplyBackgroundColor(true);
		pieRenderer.setBackgroundColor(Color.WHITE);
		pieRenderer.setMarginsColor(Color.WHITE);
		pieRenderer.setXLabelsPadding(getResources().getDimension(
				R.dimen.text_size_14));
		return pieRenderer;
	}

	private XYMultipleSeriesRenderer getLineRender() {
		if (lineRenderer != null) {
			return lineRenderer;
		}
		float size = getResources().getDimension(R.dimen.text_size_14);
		lineRenderer = new XYMultipleSeriesRenderer();
		lineRenderer
				.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
		lineRenderer.setZoomEnabled(false, false);
		lineRenderer.setPanEnabled(false, false);
		lineRenderer.setShowGridX(true);
		lineRenderer.setShowGridY(false);
		lineRenderer.setLabelsTextSize(size);
		lineRenderer.setAxisTitleTextSize(size);
		lineRenderer.setLegendTextSize(size);
		lineRenderer.setBarSpacing(0.5f);
		lineRenderer.setYAxisMin(0);
		lineRenderer.setXAxisMin(0);
		lineRenderer.setXLabelsAlign(Align.CENTER);
		lineRenderer.setYLabelsAlign(Align.LEFT);
		lineRenderer.setLabelsColor(Color.BLACK);
		lineRenderer.setShowLegend(false);
		lineRenderer.setApplyBackgroundColor(true);
		lineRenderer.setBackgroundColor(Color.WHITE);
		lineRenderer.setMarginsColor(Color.WHITE);
		lineRenderer.setShowAxes(false);
		lineRenderer.setLineColor(COLORS);

		// 柱子
		XYSeriesRenderer r = new XYSeriesRenderer();// (类似于一条线对象)
		r.setColor(COLORS[2]);// 设置颜色
		r.setDisplayChartValues(true);
		r.setChartValuesTextSize(size);
		r.setChartValuesTextAlign(Align.CENTER);
		lineRenderer.addSeriesRenderer(r);

		return lineRenderer;
	}

	private void loadData() {
		values.clear();
		List<Eat> listData = parentFragment.getData();
		Map<String,TotalModel> tempList = new HashMap<String,TotalModel>();
		for (int i = 0; i < listData.size(); i++) {
			Eat eat = listData.get(i);
			TotalModel model = tempList.get(eat.getCaloreType());
			if(model != null){
				model.setValue(model.getValue() + eat.getFoodWeight());
			}else{
				model = new TotalModel();
				model.setDay(eat.getCaloreType());
				model.setValue(eat.getFoodWeight());
				tempList.put(eat.getCaloreType(),model);
			}
		}
		values.addAll(tempList.values());
	}

	@Override
	public void notifyData() {
		new DataTask().execute();
	}

	public void switchListChart() {
		if (parentFragment.isCurentPage()) {
			chartContainer.setVisibility(View.VISIBLE);
			dataList.setVisibility(View.GONE);
			pie.repaint();
			line.repaint();
		} else {
			chartContainer.setVisibility(View.GONE);
			dataList.setVisibility(View.VISIBLE);
			adapter.notifyDataSetChanged();
		}
	}

	private class DataTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {
			loadData();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			loadPieData();
			loadLineData();
			switchListChart();
		}
	}
}
