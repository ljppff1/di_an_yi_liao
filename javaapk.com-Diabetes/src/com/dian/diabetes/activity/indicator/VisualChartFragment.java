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

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.eat.TotalBaseFragment;
import com.dian.diabetes.activity.sugar.SugarSetFragment;
import com.dian.diabetes.activity.sugar.SugarSetFragment.CallBack;
import com.dian.diabetes.db.PropertyBo;
import com.dian.diabetes.db.dao.IndicateValue;
import android.os.AsyncTask;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.ProWidget;
import com.dian.diabetes.widget.anotation.ViewInject;

public class VisualChartFragment extends TotalBaseFragment implements
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
	@ViewInject(id = R.id.edit_target)
	private TextView editTarget;
	@ViewInject(id = R.id.input_target)
	private EditText inputTarget;
	@ViewInject(id = R.id.container)
	private LinearLayout container;
	@ViewInject(id = R.id.target_unit)
	private TextView unitView;

	// chart
	private XYMultipleSeriesDataset dataSet;
	private XYMultipleSeriesRenderer mRenderer;
	private IndicatorActivity activity;
	private GraphicalView lineChart;

	private InputMethodManager imm;
	private DecimalFormat format;
	private boolean isCreate = false;
	private DetailFragment parentFragment;
	private float high = 0;
	private float low = 0;
	private int size = 0, level = 0;
	private float total = 0, increse = 1,persent = 1;
	private float normalMax = -1;

	public static VisualChartFragment getInstance() {
		VisualChartFragment fragment = new VisualChartFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (IndicatorActivity) getActivity();
		parentFragment = (DetailFragment) getParentFragment();
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
		editTarget.setOnClickListener(this);
		container.setOnClickListener(this);
		unitView.setVisibility(View.VISIBLE);
		if(!"heart".equals(parentFragment.getKey())){
			format = new DecimalFormat("#.00");
		}else{
			format = new DecimalFormat("0");
		}
		intiNormalView();
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
		float[] alignment = new float[] { low, high };
		int[] levelColor = { getResources().getColor(R.color.trend_down_bad),
				getResources().getColor(R.color.trend_down_normal),
				getResources().getColor(R.color.trend_up_bad) };
		mRenderer.setAlignColors(levelColor);
		mRenderer.setAlign(alignment);
		mRenderer.setAlignLineColor(Color.rgb(120, 253, 100));
		// 数值写入
		if (size == 0) {
			avgProgress.setValueLevel(0, alignment[1], level);
			avgValueView.setText("0");
		} else {
			avgProgress.setValueLevel(total, alignment[1], level);
			avgValueView.setText(format.format(total));
		}
		lineChart.repaint();
		if (level == 1) {
			unionTarget.setText("指标过低");
			unionTarget.setTextColor(getResources().getColor(
					R.color.trend_down_bad));
		} else if (level == 2) {
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
		inputTarget.setVisibility(View.GONE);
		editTarget.setVisibility(View.GONE);
		Log.e("key", parentFragment.getKey());
		if ("weight".equals(parentFragment.getKey())) {
			targetLabel.setText("目标值");
			high = Config.getFloatPro("weightHigh");
			low = Config.getFloatPro("weightLow");
			targetValue.setVisibility(View.GONE);
			inputTarget.setVisibility(View.GONE);
			editTarget.setVisibility(View.VISIBLE);
			editTarget.setText(low + "~" + high);
			unitView.setText(parentFragment.getUnion());
		} else if ("bmi".equals(parentFragment.getKey())) {
			targetLabel.setText("目标值");
			high = Config.getFloatPro("bmiHigh");
			low = Config.getFloatPro("bmiLow");
			targetValue.setVisibility(View.GONE);
			inputTarget.setVisibility(View.GONE);
			editTarget.setVisibility(View.VISIBLE);
			editTarget.setText(low + "~" + high);
			unitView.setText(parentFragment.getUnion());
		} else if ("waist".equals(parentFragment.getKey())) {
			targetLabel.setText("目标值");
			high = Config.getFloatPro("waistHigh");
			low = Config.getFloatPro("waistLow");
			targetValue.setVisibility(View.GONE);
			inputTarget.setVisibility(View.GONE);
			editTarget.setVisibility(View.VISIBLE);
			editTarget.setText(low + "~" + high);
			unitView.setText(parentFragment.getUnion());
		} else if ("press".equals(parentFragment.getKey())) {

		} else if ("ch".equals(parentFragment.getKey())) {
			targetLabel.setText("参考值");
			high = Config.getFloatPro("highCh");
			low = Config.getFloatPro("lowCh");
			targetValue.setText(low + "~" + high);
			unitView.setText(parentFragment.getUnion());
		} else if ("tg".equals(parentFragment.getKey())) {
			targetLabel.setText("参考值");
			high = Config.getFloatPro("highTg");
			low = Config.getFloatPro("lowTg");
			targetValue.setText(low + "~" + high);
			unitView.setText(parentFragment.getUnion());
		} else if ("hdl".equals(parentFragment.getKey())) {
			targetLabel.setText("参考值");
			high = Config.getFloatPro("highHdl");
			low = Config.getFloatPro("lowHdl");
			targetValue.setText(low + "~" + high);
			unitView.setText(parentFragment.getUnion());
		} else if ("ldl".equals(parentFragment.getKey())) {
			targetLabel.setText("参考值");
			high = Config.getFloatPro("highLdl");
			low = Config.getFloatPro("lowLdl");
			targetValue.setText(low + "~" + high);
			unitView.setText(parentFragment.getUnion());
		} else if ("heart".equals(parentFragment.getKey())) {
			targetLabel.setText("参考值");
			high = Config.getFloatPro("highHeart");
			low = Config.getFloatPro("lowHeart");
			targetValue.setText(low + "~" + high);
			unitView.setText(parentFragment.getUnion());
		} else if ("protein".equals(parentFragment.getKey())) {
			targetLabel.setText("目标值");
			high = Config.getFloatPro("diastaticValue");
			low = Integer.MIN_VALUE;
			targetValue.setVisibility(View.GONE);
			editTarget.setVisibility(View.VISIBLE);
			inputTarget.setText(high + "");
			editTarget.setText(high + "%");
			normalMax = 10;
		}
		if("heart".equals(parentFragment.getKey())){
			targetValue.setText(format.format(low) + "~" + format.format(high));
		}
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
			intiNormalView();
			mRenderer.clearXTextLabels();
			dataSet.clear();
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, parentFragment.getDay());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			float value1 = 0, value2 = 0, max = 0, itemValue = 0;
			XYSeries series = new XYSeries("指标线");
			String day = "";
			int index = 0, delta = -parentFragment.getDelta(), indicateIndex = 0, temp = 0,normal = 0;
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
							value.getCreate_time(), DateUtil.yyyyMMdd);
					if (day.equals(itemDay)) {
						if (itemValue != 0) {
							itemValue = (itemValue + value.getValue()) / 2;
						} else {
							itemValue = value.getValue();
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
				max = Math.max(max, itemValue);
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
					mRenderer.addXTextLabel(temp, dayout);
					index = 0;
					itemValue = 0;
					temp++;
				}
			}
			for (int j = 0; j < listData.size(); j++) {
				if(listData.get(j).getLevel() == 0){
					normal ++;
				}
			}
			max = Math.max(max, high);
			mRenderer.setXAxisMin(0);
			mRenderer.setXAxisMax(temp - 1);
			if (!"protein".equals(parentFragment.getKey())) {
				mRenderer.setYAxisMax(high + high / 2);
			} else {
				mRenderer.setYAxisMax(normalMax);
			}
			dataSet.addSeries(series);
			if(listData.size() == 0){
				persent = 1;
			}else{
				persent = normal/listData.size();
			}
			increse = value1 == 0 ? 0 : (value2 - value1) / value1;
			if (size == 0) {
				total = 0;
				level = 0;
			} else {
				total = listData.get(listData.size() - 1).getValue();
				level = listData.get(listData.size() - 1).getLevel();
			}
			loadChartData();
		}
	}

	@Override
	public void notifyData() {
		if(!"heart".equals(parentFragment.getKey())){
			format = new DecimalFormat("#.00");
		}else{
			format = new DecimalFormat("0");
		}
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

		return mRenderer;
	}

	public void setNormalMax(float max) {
		this.normalMax = max;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.edit_target:
			if("protein".equals(parentFragment.getKey())){
				inputTarget.setVisibility(View.VISIBLE);
				editTarget.setVisibility(View.GONE);
				container.setEnabled(true);
				inputTarget.requestFocus();
				imm = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(inputTarget, InputMethodManager.SHOW_FORCED);
				inputTarget.setSelection(inputTarget.getText().length());
			}else{
				toTargetSet();
			}
			break;
		case R.id.container:
			if(inputTarget.getVisibility() !=View.VISIBLE){
				return;
			}
			if (CheckUtil.isNull(inputTarget.getText())) {
				Toast.makeText(context, "控制目标值不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			Config.setProperty("diastaticValue", inputTarget.getText() + "");
			new PropertyBo(activity).updateByKey("diastaticValue", ContantsUtil.DEFAULT_TEMP_UID,
					inputTarget.getText() + "");
			imm.hideSoftInputFromWindow(inputTarget.getWindowToken(), 0);
			inputTarget.setVisibility(View.GONE);
			editTarget.setVisibility(View.VISIBLE);
			editTarget.setText(inputTarget.getText()+ "%");
			container.setEnabled(false);
			break;
		}
	}
	
	private void toSugarSet() {
		String tag = "sugar_target_set";
		FragmentManager manager = context.getSupportFragmentManager();
		SugarSetFragment tempFragment = (SugarSetFragment) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = SugarSetFragment.getInstance(ContantsUtil.EAT_PRE);
			tempFragment.setCallBack(new CallBack() {
				@Override
				public void callBack(String persent,String value, int cur) {
					editTarget.setText(persent);
					high = StringUtil.toFloat(persent);
				}
			});
		}
		tempFragment.show(manager, tag);
	}

	private void toTargetSet() {
		String tag = "indicate_target_set";
		FragmentManager manager = context.getSupportFragmentManager();
		TargetSetFragment tempFragment = (TargetSetFragment) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = TargetSetFragment.getInstance(
					parentFragment.getKey() + "High", parentFragment.getKey()
							+ "Low", parentFragment.getUnion());
			tempFragment.setCallBack(new TargetSetFragment.CallBack() {
				@Override
				public void callBack(float lowValue, float highValue) {
					editTarget.setText(lowValue + "~" + highValue);
					low = lowValue;
					high = highValue;
					loadChartData();
				}
			});
		}
		tempFragment.show(manager, tag);
	}
}
