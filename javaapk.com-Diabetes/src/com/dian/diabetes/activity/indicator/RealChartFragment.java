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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.eat.TotalBaseFragment;
import com.dian.diabetes.dto.IndicateDto;
import android.os.AsyncTask;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.ProWidget;
import com.dian.diabetes.widget.anotation.ViewInject;

public class RealChartFragment extends TotalBaseFragment {

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
	private ProWidget avgProgress;
	@ViewInject(id = R.id.avg_value)
	private TextView avgValueView;
	@ViewInject(id = R.id.trend_img)
	private ImageView trendImg;
	@ViewInject(id = R.id.trend_value)
	private TextView trendValue;
	@ViewInject(id = R.id.sugar_effect)
	private ImageView sugarEffect;
	@ViewInject(id = R.id.union)
	private TextView unionTarget;
	@ViewInject(id = R.id.target_unit)
	private TextView targetUnion;
	@ViewInject(id = R.id.toast)
	private LinearLayout toast;
	@ViewInject(id = R.id.sence_toast)
	private TextView senceToast;
	@ViewInject(id = R.id.high_toast)
	private TextView highToast;
	@ViewInject(id = R.id.low_toast)
	private TextView lowToast;
	@ViewInject(id = R.id.last_toast)
	private TextView lastToast;

	// chart
	private XYMultipleSeriesDataset dataSet;
	private XYMultipleSeriesRenderer mRenderer;
	private IndicatorActivity activity;
	private GraphicalView lineChart;

	private DecimalFormat format;
	private boolean isCreate = false;
	private RealDetailFragment parentFragment;
	private int size = 0, level = 0;
	private float total = 0, increse = 1, max = 1,persent = 1;

	public static RealChartFragment getInstance() {
		RealChartFragment fragment = new RealChartFragment();
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (IndicatorActivity) getActivity();
		parentFragment = (RealDetailFragment) getParentFragment();
		dataSet = new XYMultipleSeriesDataset();
		getRender();
		XYSeries series = new XYSeries("指标线");
		dataSet.addSeries(series);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_indicate_chart,
				container, false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		if (!isCreate) {
			new DataTask().execute();
		}
		lastToast.setVisibility(View.GONE);
		intiNormalView();
		loadChart();
	}

	@SuppressWarnings("deprecation")
	private void loadChart() {
		lineChart = ChartFactory.getLineChartView(context, dataSet, mRenderer);
		chart.addView(lineChart, 0, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	private void loadChartData() {
		float[] alignment = new float[] { parentFragment.getMin(),
				parentFragment.getMax() };
		int[] levelColor = { getResources().getColor(R.color.trend_down_bad),
				getResources().getColor(R.color.trend_down_normal),
				getResources().getColor(R.color.trend_up_bad) };
		mRenderer.setAlignColors(levelColor);
		mRenderer.setAlign(alignment);
		mRenderer.setAlignLineColor(Color.rgb(120, 253, 100));
		// 数值写入
//		if (size == 0) {
//			avgProgress.setValueLevel(0, max, level);
//			avgValueView.setText("0");
//		} else {
//			int level = Config.getIndicatLevel(total, alignment);
//			avgProgress.setValueLevel(total, max, level);
//			avgValueView.setText(format.format(total));
//		}
		total = parentFragment.getValue();
		format = new DecimalFormat("#");
		String dd = format.format(parentFragment.getValue());
		avgValueView.setText(dd);
		lineChart.repaint();
		if (total < alignment[0]) {
			unionTarget.setText("指标过低");
			unionTarget.setTextColor(getResources().getColor(
					R.color.trend_down_bad));
		} else if (total > alignment[1]) {
			unionTarget.setText("指标偏高");
			unionTarget.setTextColor(getResources().getColor(
					R.color.trend_up_bad));
		} else {
			unionTarget.setText("指标正常");
			unionTarget.setTextColor(getResources().getColor(
					R.color.trend_down_normal));
		}
		if (increse < -0.25) {
			trendValue.setTextColor(getResources().getColor(
					R.color.trend_down_bad));
			trendImg.setImageResource(R.drawable.trend_down_normal);
		} else if (increse < 0) {
			trendValue.setTextColor(getResources().getColor(
					R.color.trend_down_normal));
			trendImg.setImageResource(R.drawable.trend_down_good);
		} else if (increse < 0.25) {
			trendValue.setTextColor(getResources().getColor(
					R.color.trend_up_normal));
			trendImg.setImageResource(R.drawable.trend_up_normal);
		} else if (increse < 0.5) {
			trendValue.setTextColor(getResources().getColor(
					R.color.trend_up_more));
			trendImg.setImageResource(R.drawable.trend_up_more);
		} else {
			trendValue.setTextColor(getResources().getColor(
					R.color.trend_up_bad));
			trendImg.setImageResource(R.drawable.trend_up_bad);
		}
		if(persent < 0.6){
			sugarEffect.setImageResource(R.drawable.icon_sugar_bad);
		}else if(persent < 0.8 && persent > 0.6){
			sugarEffect.setImageResource(R.drawable.icon_sugar_normal);
		}else{
			sugarEffect.setImageResource(R.drawable.icon_sugar_good);
		}
		trendValue.setText(((int) (increse * 100)) * 1.0f + "%");
	}

	private void intiNormalView() {
		targetLabel.setText("参考值");
		if (parentFragment.getMax() == Integer.MAX_VALUE) {
			targetValue.setText(">" + parentFragment.getMin());
		} else {
			if (parentFragment.getMin() == Integer.MIN_VALUE) {
				targetValue.setText("<" + parentFragment.getMin());
			} else {
				targetValue.setText(parentFragment.getMin() + "~"
						+ parentFragment.getMax());
			}
		}
		targetUnion.setVisibility(View.VISIBLE);
		targetUnion.setText(parentFragment.getUnion());
	}

	public boolean onBackKeyPressed() {
		activity.startIndicateList();
		return true;
	}

	private class DataTask extends AsyncTask<Object, Object, Object> {

		private List<IndicateDto> listData;

		public DataTask() {
			super();
			activity.show();
		}

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
			float value1 = 0, value2 = 0, itemValue = 0;
			XYSeries series = new XYSeries("指标线");
			int delta = -parentFragment.getDelta(), temp = 0,normal = 0;
			boolean isIn = false;
			long preTime = calendar.getTimeInMillis();
			for (int i = 0; i > parentFragment.getDay(); i = i - delta) {
				calendar.add(Calendar.DATE, delta);
				long tempTime = calendar.getTimeInMillis();
				for (int j = 0; j < listData.size(); j++) {
					IndicateDto value = listData.get(j);
					if (value.date > preTime && value.date < tempTime) {
						if (itemValue != 0) {
							itemValue = (itemValue + value.result) / 2;
						} else {
							itemValue = value.result;
						}
						// indicateIndex++;
					}
					if (!isIn) {
						if (j < listData.size() / 2) {
							value1 = (value1 + value.result) / 2;
						} else {
							value2 = (value2 + value.result) / 2;
						}
						isIn = true;
					}
				}
				String dayout;
				if (delta < 10) {
					dayout = DateUtil.parseToString(calendar.getTime(),
							DateUtil.MMdd);
				} else {
					dayout = DateUtil.parseToString(calendar.getTime(),
							DateUtil.yyyyMM);
				}
				if (itemValue > 100) {
					series.add(temp, 100 + (itemValue - 100) * 0.1);
				} else {
					series.add(temp, itemValue);
				}
				mRenderer.addXTextLabel(temp, dayout);
				itemValue = 0;
				temp++;
				preTime = tempTime;
			}
			for (int j = 0; j < listData.size(); j++) {
				if(listData.get(j).level == 0){
					normal ++;
				}
			}
			max = parentFragment.getMax();
			mRenderer.setXAxisMin(0);
			mRenderer.setXAxisMax(temp - 1);
			mRenderer.setYAxisMax(parentFragment.getMax() * 5 / 3);
			dataSet.addSeries(series);
			increse = value1 == 0 ? 0 : (value2 - value1) / value1;
			if(listData.size() == 0){
				persent = 1;
			}else{
				persent = normal/listData.size();
			}
			if (size == 0) {
				total = 0;
			} else {
				total = listData.get(listData.size() - 1).result;
			}
			if(listData.size() > 0){
				IndicateDto dto = listData.get(0);
				if(CheckUtil.isNull(dto.sense)){
					senceToast.setVisibility(View.GONE);
				}else{
					senceToast.setText(dto.sense);	
					senceToast.setVisibility(View.VISIBLE);
				}
				if(CheckUtil.isNull(dto.valueLow)){
					lowToast.setVisibility(View.GONE);
				}else{
					lowToast.setText(dto.valueLow);	
					lowToast.setVisibility(View.VISIBLE);
				}
				if(CheckUtil.isNull(dto.valueHigh)){
					highToast.setVisibility(View.GONE);
				}else{
					highToast.setText(dto.valueHigh);	
					lowToast.setVisibility(View.VISIBLE);
				}
			}
			loadChartData();
			intiNormalView();
			activity.hide();
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
		float size = getResources().getDimension(R.dimen.text_size_14);
		mRenderer = new XYMultipleSeriesRenderer();
		mRenderer
				.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
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

		return mRenderer;
	}

}
