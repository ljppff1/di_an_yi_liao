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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.eat.adapter.LabelAdapter;
import com.dian.diabetes.dto.TotalModel;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 饮食结构，数据从服务端读取，客户端负责展现
 * @author hua
 *
 */
public class NutritionFragment extends TotalBaseFragment {

	@ViewInject(id = R.id.pie_chart)
	private RelativeLayout pieChart;
	@ViewInject(id = R.id.chart)
	private RelativeLayout chartLayout;
	@ViewInject(id = R.id.data_list)
	private ListView dataList;
	@ViewInject(id = R.id.chart_container)
	private LinearLayout chartContainer;
	@ViewInject(id = R.id.null_container)
	private LinearLayout nullContainer;

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

	public static NutritionFragment getInstance() {
		return new NutritionFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (EatActivity) context;
		lineDataset = new XYMultipleSeriesDataset();
		mSeries = new CategorySeries("");
		XYSeries series = new XYSeries("热量柱形图");
		lineDataset.addSeries(series);
		ContantsUtil.NUTRITION_UPDATE = false;
		getPieRender();
		getLineRender();
		values = new ArrayList<TotalModel>();
		parentFragment = (CaloriesFragment) getParentFragment();
		adapter = new LabelAdapter(activity, values);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_nutrition_layout,
				container, false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		loadPieChart();
		loadLineChart();
		if (!ContantsUtil.NUTRITION_UPDATE) {
			new DataTask().execute();
			ContantsUtil.NUTRITION_UPDATE = true;
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
		XYSeries series = new XYSeries("各类热量摄取值");
		double max = 0;
		for (int i = 0; i < values.size(); i++) {
			String label = values.get(i).getDay();
			double value = values.get(i).getValue();
			max = Math.max(max, value);
			series.add(i + 1, value);
			lineRenderer.addXTextLabel(i + 1, label);
		}
		lineRenderer.setXAxisMax(values.size() + 1);
		lineRenderer.setYAxisMax(max + max / 5);
		lineDataset.addSeries(series);
	}

	private XYMultipleSeriesRenderer getPieRender() {
		if (pieRenderer != null) {
			return pieRenderer;
		}
		float size = getResources().getDimension(R.dimen.text_size_14);
		pieRenderer = new XYMultipleSeriesRenderer();
		pieRenderer
				.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
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
		lineRenderer.setLineColor(COLORS);
		lineRenderer.setShowAxes(false);

		// 柱子
		XYSeriesRenderer r = new XYSeriesRenderer();// (类似于一条线对象)
		r.setColor(COLORS[0]);// 设置颜色
		r.setDisplayChartValues(true);
		r.setChartValuesTextSize(size);
		r.setChartValuesTextAlign(Align.CENTER);
		lineRenderer.addSeriesRenderer(r);

		return lineRenderer;
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
			if(values.size() == 0){
				nullContainer.setVisibility(View.VISIBLE);
				chartContainer.setVisibility(View.GONE);
			}else{
				nullContainer.setVisibility(View.GONE);
				chartContainer.setVisibility(View.VISIBLE);
			}
		}else{
			nullContainer.setVisibility(View.GONE);
			chartContainer.setVisibility(View.GONE);
			dataList.setVisibility(View.VISIBLE);
			adapter.notifyDataSetChanged();
		}
	}

	private class DataTask extends AsyncTask<Object, Object, Object> {

		private int status = 1;
		private int state;

		public DataTask() {
			super();
			activity.show();
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("startTime", parentFragment.getPreTime());
			params.put("endTime", System.currentTimeMillis());
			params.put("mid", ContantsUtil.DEFAULT_TEMP_UID);
			values.clear();
			String result = HttpServiceUtil
					.post(ContantsUtil.NUTRITION, params);
			if (CheckUtil.isNull(result)) {
				status = 1;
			} else {
				try {
					JSONObject groupData = new JSONObject(result);
					state = groupData.getInt("status");
					if (CheckUtil.checkStatusOk(state)) {
						JSONArray array = groupData.getJSONArray("data");
						for (int i = 0; i < array.length(); i++) {
							TotalModel model = new TotalModel();
							model.setDay(array.getJSONObject(i).getString(
									"name"));
							model.setValue(array.getJSONObject(i).getDouble(
									"value"));
							values.add(model);
						}
					} else {
						status = -1;
					}
				} catch (JSONException e) {
					status = -2;
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			activity.hide();
			if (status == 1) {
				loadPieData();
				loadLineData();
				pie.repaint();
				line.repaint();
				switchListChart();
			} else if (status == -2) {
				Toast.makeText(context, "读取数据失败", Toast.LENGTH_SHORT).show();
			} else {
				ToastTool.showToast(state, context);
			}
		}
	}
}
