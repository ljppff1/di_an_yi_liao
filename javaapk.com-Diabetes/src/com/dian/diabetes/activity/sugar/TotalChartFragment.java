package com.dian.diabetes.activity.sugar;

import java.text.NumberFormat;
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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.sugar.model.CountModel;
import com.dian.diabetes.tool.CommonUtil;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 血糖统计图表展现
 * @author hua
 *
 */
public class TotalChartFragment extends BaseFragment {
	@ViewInject(id = R.id.chart)
	private RelativeLayout chart;
	@ViewInject(id = R.id.title_pre)
	private TextView titlePre;
	@ViewInject(id = R.id.date_title)
	private TextView dayView;
	// chart
	private XYMultipleSeriesDataset dataSet;
	private XYMultipleSeriesRenderer mRenderer;

	private String type = "11";
	private int position = 0;
	private SugarTotalChartFragment sugarFragment;
	private SugarTotalFragment totalFragment;

	public static TotalChartFragment getInstance(String type, int position) {
		TotalChartFragment fragment = new TotalChartFragment();
		Bundle bundle = new Bundle();
		bundle.putString("type", type);
		bundle.putInt("position", position);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = getArguments().getString("type");
		position = getArguments().getInt("position");
		sugarFragment = (SugarTotalChartFragment) getParentFragment();
		totalFragment = (SugarTotalFragment) sugarFragment.getParentFragment();
		dataSet = new XYMultipleSeriesDataset();
		Config.pageMap.put(className + position, false);
		getRender();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_total_chart, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		titlePre.setText(CommonUtil.getValue("diabetes" + type));
		String title = DateUtil.parseToDate(totalFragment.getPreTime()) + "~"
				+ DateUtil.parseToDate(System.currentTimeMillis());
		dayView.setText(title);
		if (!Config.getPageState(className + position)) {
			loadChartData();
			Config.pageMap.put(className + position, true);
		}
		loadChart();		
	}

	@SuppressWarnings("deprecation")
	private void loadChart() {
		GraphicalView lineChart = ChartFactory.getLineChartView(context,
				dataSet, mRenderer);
		chart.addView(lineChart, 0, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	private void loadChartData() {
		int typeI = StringUtil.toInt(type) % 10;
		if(typeI != 4){
			float alignment[] = { Config.getFloatPro("levelLow" + typeI),
					Config.getFloatPro("levelHigh" + typeI) };
			mRenderer.setAlign(alignment);
			mRenderer.setAlignColors(new int[] { Color.RED,
					Color.rgb(136, 204, 153), Color.RED });
			mRenderer.setAlignLineColor(Color.rgb(120, 253, 100));
		}		
		mRenderer.clearXTextLabels();
		dataSet.clear();
		XYSeries series = new XYSeries("血糖线");
		List<CountModel> data = sugarFragment.getData(position);
		for (int i = 0; i < data.size(); i++) {
			CountModel item = data.get(i);
			series.add(i, item.getValue());
			mRenderer.addXTextLabel(i, item.getDate());
		}
		mRenderer.setXAxisMin(0);
		mRenderer.setXAxisMax(data.size() - 1);
		dataSet.addSeries(series);		
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
		mRenderer.setShowLegend(false);
		mRenderer.setShowLabels(true);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.WHITE);
		mRenderer.setMarginsColor(Color.WHITE);
		mRenderer.setXLabelsPadding(getResources().getDimension(
				R.dimen.text_size_14));
		mRenderer.setShowAxes(false);

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

	public void setRepaint() {
		Config.pageMap.put(className + position, false);
	}
}
