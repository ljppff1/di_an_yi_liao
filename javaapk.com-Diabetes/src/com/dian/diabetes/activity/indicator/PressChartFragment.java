package com.dian.diabetes.activity.indicator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.eat.TotalBaseFragment;
import com.dian.diabetes.db.dao.IndicateValue;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.MutiProgress;
import com.dian.diabetes.widget.anotation.ViewInject;

public class PressChartFragment extends TotalBaseFragment implements
		OnClickListener {

	@ViewInject(id = R.id.weight_target_label)
	private TextView targetLabel;
	@ViewInject(id = R.id.trend_label)
	private TextView trendLabel;
	@ViewInject(id = R.id.trend_suggest)
	private TextView trendSuggest;
	@ViewInject(id = R.id.trend_suggest)
	private TextView unionView;
	@ViewInject(id = R.id.chart)
	private RelativeLayout chart;
	@ViewInject(id = R.id.target)
	private TextView targetValue;
	@ViewInject(id = R.id.avg_progress)
	private MutiProgress avgProgress;
	@ViewInject(id = R.id.close_press)
	private TextView closePressView;
	@ViewInject(id = R.id.open_press)
	private TextView openPressView;
	@ViewInject(id = R.id.trend_img)
	private ImageView trendImg;
	@ViewInject(id = R.id.trend_value)
	private TextView trendValue;
	@ViewInject(id = R.id.sugar_effect)
	private ImageView sugarEffect;

	// chart
	private XYMultipleSeriesDataset dataSet;
	private XYMultipleSeriesRenderer mRenderer;
	private IndicatorActivity activity;
	private GraphicalView lineChart;

	private DecimalFormat format;
	private boolean isCreate = false;
	private DetailFragment parentFragment;
	private float[] alignmentOpen, alignmentClose;
	private int size = 0, level = 0, level1 = 0;
	private float total = 0, total1 = 0, increse = 1;

	public static PressChartFragment getInstance() {
		PressChartFragment fragment = new PressChartFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (IndicatorActivity) getActivity();
		parentFragment = (DetailFragment) getParentFragment();
		dataSet = new XYMultipleSeriesDataset();
		format = new DecimalFormat("0");
		getRender();
		XYSeries series = new XYSeries("舒张压线");
		dataSet.addSeries(series);
		XYSeries series1 = new XYSeries("收缩压线");
		dataSet.addSeries(series1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_press_chart, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		targetValue.setOnClickListener(this);
		alignmentOpen = new float[] { 90, Config.getFloatPro("highOpenPress") };
		alignmentClose = new float[] { 60, Config.getFloatPro("highClosePress") };
		if (!isCreate) {
			new DataTask().execute();
		}
		loadChart();
	}

	@SuppressWarnings("deprecation")
	private void loadChart() {
		lineChart = ChartFactory.getLineChartView(context, dataSet, mRenderer);
		chart.addView(lineChart, 0, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	private void loadChartData() {
		int[] levelColor = { getResources().getColor(R.color.trend_down_bad),
				getResources().getColor(R.color.trend_down_normal),
				getResources().getColor(R.color.trend_up_bad) };
		float[] temp = new float[]{alignmentOpen[1],alignmentClose[1]};
		mRenderer.setAlign(temp);
		mRenderer.setAlignColors(levelColor);
		targetValue.setText(format.format(alignmentClose[1]) + "/" + format.format(alignmentOpen[1]));
		mRenderer.setAlignLineColor(Color.rgb(120, 253, 100));
		// 数值写入
		int[] levelColor1 = {
				getResources().getColor(R.color.trend_down_normal),
				getResources().getColor(R.color.trend_down_bad),
				getResources().getColor(R.color.trend_up_bad) };
		if (size == 0) {
			avgProgress.setValue(0, 0, alignmentOpen[1], levelColor1[level1],
					levelColor1[level]);
			closePressView.setText("0");
			openPressView.setText("0");
		} else {
			avgProgress.setValue(total1, total, alignmentOpen[1],
					levelColor1[level], levelColor1[level1]);
			openPressView.setText(format.format(total1));
			openPressView.setTextColor(levelColor1[level1]);
			closePressView.setText(format.format(total));
			closePressView.setTextColor(levelColor1[level]);
		}
		lineChart.repaint();
		if (increse < -0.25) {
			trendValue.setTextColor(getResources().getColor(
					R.color.trend_down_bad));
			trendImg.setImageResource(R.drawable.trend_down_normal);
			sugarEffect.setImageResource(R.drawable.icon_sugar_bad);
		} else if (increse < 0) {
			trendValue.setTextColor(getResources().getColor(
					R.color.trend_down_normal));
			trendImg.setImageResource(R.drawable.trend_down_good);
			sugarEffect.setImageResource(R.drawable.icon_sugar_normal);
		} else if (increse < 0.25) {
			trendValue.setTextColor(getResources().getColor(
					R.color.trend_up_normal));
			trendImg.setImageResource(R.drawable.trend_up_normal);
			sugarEffect.setImageResource(R.drawable.icon_sugar_good);
		} else if (increse < 0.5) {
			trendValue.setTextColor(getResources().getColor(
					R.color.trend_up_more));
			trendImg.setImageResource(R.drawable.trend_up_more);
			sugarEffect.setImageResource(R.drawable.icon_sugar_normal);
		} else {
			trendValue.setTextColor(getResources().getColor(
					R.color.trend_up_bad));
			trendImg.setImageResource(R.drawable.trend_up_bad);
			sugarEffect.setImageResource(R.drawable.icon_sugar_bad);
		}
		trendValue.setText(((int) (increse * 100)) * 1.0f + "%");
	}

	public boolean onBackKeyPressed() {
		activity.startIndicateList();
		return true;
	}

	private class DataTask extends AsyncTask<Object, Object, Object> {

		private List<IndicateValue> listData;

		@Override
		protected Object doInBackground(Object... arg0) {
			listData = parentFragment.getData();
			size = listData.size();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			mRenderer.clearXTextLabels();
			dataSet.clear();
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, parentFragment.getDay());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			float value1 = 0, value2 = 0, max = 0, itemValue = 0, itemValue1 = 0;
			XYSeries series = new XYSeries("舒张压线");
			XYSeries series1 = new XYSeries("收缩压线");
			String day = "";
			int index = 0, delta = -parentFragment.getDelta(), indicateIndex = 0, temp = 0;
			int preMonth = -1;
			for (int i = 0; i > parentFragment.getDay(); i--) {
				index++;
				calendar.add(Calendar.DATE, 1);
				int tempMonth = calendar.get(Calendar.MONTH);
				day = DateUtil.parseToString(calendar.getTime(),
						DateUtil.yyyyMMdd);
				for (int j = indicateIndex; j < listData.size(); j++) {
					IndicateValue value = listData.get(j);
					String itemDay = DateUtil.parseToString(
							value.getUpdate_time(), DateUtil.yyyyMMdd);
					if (day.equals(itemDay)) {
						if (itemValue != 0) {
							itemValue = (itemValue + value.getValue()) / 2;
							itemValue1 = (itemValue1 + value.getValue1()) / 2;
						} else {
							itemValue = value.getValue();
							itemValue1 = value.getValue1();
						}
						indicateIndex++;
					} else {
						break;
					}
					if (j < listData.size() / 2) {
						value1 = (value1 + value.getValue()) / 2;
					} else {
						value2 = (value2 + value.getValue()) / 2;
					}
				}
				if (index == delta || i == (parentFragment.getDay() + 1)) {
					String dayout;
					if(tempMonth == preMonth){
						dayout = DateUtil.parseToString(calendar.getTime(),
								DateUtil.dd);
					}else{
						dayout = DateUtil.parseToString(calendar.getTime(),
								DateUtil.MMdd);
					}
					preMonth = tempMonth;
					series.add(temp, itemValue);
					series1.add(temp, itemValue1);
					max = Math.max(itemValue, max);
					max = Math.max(itemValue1, max);
					mRenderer.addXTextLabel(temp, dayout);
					index = 0;
					itemValue = 0;
					itemValue1 = 0;
					temp++;
				}
			}
			max = Math.max(max, alignmentOpen[1]);
			mRenderer.setXAxisMin(0);
			mRenderer.setXAxisMax(temp - 1);
			mRenderer.setYAxisMax(max + max / 2);
			dataSet.addSeries(series);
			dataSet.addSeries(series1);
			increse = value1 == 0 ? 0 : (value2 - value1) / value1;
			if (size == 0) {
				total = 0;
			} else {
				IndicateValue value = listData.get(listData.size() - 1);
				total = value.getValue();
				level = value.getLevel();
				total1 = value.getValue1();
				level1 = value.getLevel1();
			}
			loadChartData();
		}
	}

	@Override
	public void notifyData() {
		new DataTask().execute();
	}

	private XYMultipleSeriesRenderer getRender() {
		if (mRenderer != null) {
			return mRenderer;
		}
		float size = getResources().getDimension(R.dimen.text_size_12);
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
		mRenderer.setYLabelsAlign(Align.LEFT);
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
		mRenderer.addSeriesRenderer(r);

		return mRenderer;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.target:
			toTargetSet();
			break;
		}
	}

	private void toTargetSet() {
		String tag = "press_target_set";
		FragmentManager manager = context.getSupportFragmentManager();
		PressSetFragment tempFragment = (PressSetFragment) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = PressSetFragment.getInstance();
			tempFragment.setCallBack(new PressSetFragment.CallBack() {
				@Override
				public void callBack(float lowValue, float highValue) {
					targetValue.setText(lowValue + "/" + highValue);
					alignmentClose[1] = lowValue;
					alignmentOpen[1] = highValue;
					loadChartData();
				}
			});
		}
		tempFragment.show(manager, tag);
	}

}
