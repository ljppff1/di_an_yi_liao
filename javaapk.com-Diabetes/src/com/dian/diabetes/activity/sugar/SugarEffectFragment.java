package com.dian.diabetes.activity.sugar;

import java.text.DecimalFormat;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.sugar.SugarSetFragment.CallBack;
import com.dian.diabetes.db.BloodBo;
import com.dian.diabetes.db.dao.Diabetes;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.SimpleProgress;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 控糖成效
 * @author hua
 *
 */
public class SugarEffectFragment extends BaseFragment implements
		OnClickListener {

	@ViewInject(id = R.id.diabetes_listener)
	private RelativeLayout sugarTarget;
	@ViewInject(id = R.id.chart)
	private RelativeLayout chartLayout;
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.mid_value)
	private TextView midValueView;
	@ViewInject(id = R.id.high_value)
	private TextView highValueView;
	@ViewInject(id = R.id.low_value)
	private TextView lowValueView;
	@ViewInject(id = R.id.avg_progress)
	private SimpleProgress avgProgress;
	@ViewInject(id = R.id.avg_value)
	private TextView avgValue;
	@ViewInject(id = R.id.sugar_target)
	private TextView sugarTargetValue;
	@ViewInject(id = R.id.sugar_plan)
	private TextView sugarPlan;
	@ViewInject(id = R.id.trend_img)
	private ImageView trendImg;
	@ViewInject(id = R.id.trend_value)
	private TextView trendValue;
	@ViewInject(id = R.id.sugar_effect)
	private ImageView sugarEffect;
	@ViewInject(id = R.id.pre_week)
	private ImageButton preBtn;
	@ViewInject(id = R.id.next_week)
	private ImageButton nextBtn;
	@ViewInject(id = R.id.current_week)
	private TextView curWeek;
	@ViewInject(id = R.id.check_dinner)
	private Button effectDinner;

	// chart
	private XYMultipleSeriesDataset dataSet;
	private XYMultipleSeriesRenderer mRenderer;
	private BloodBo diabetesBo;
	private GraphicalView lineChart;
	private long weekPre = 0;
	private long weekAfter = 0;

	private List<Diabetes> data;
	private int type = ContantsUtil.EAT_PRE;
	private DecimalFormat format;
	private float total = 0, increse = 1 , persent = 1;
	private int high = 0, mid = 0, low = 0;
	private float alignment[];
	private DataTask task;
	private MealsPopDialog mealsDialog;
	private int targetMax = 15;

	public static SugarEffectFragment getInstance() {
		SugarEffectFragment fragment = new SugarEffectFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ContantsUtil.EFFECT_UPDATE = false;
		dataSet = new XYMultipleSeriesDataset();
		diabetesBo = new BloodBo(context);
		format = new DecimalFormat("##0.0");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DATE, -7);
		weekPre = calendar.getTimeInMillis();
		weekAfter = System.currentTimeMillis();
		getRender();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sugar_effect, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		preBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		sugarTarget.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		sugarPlan.setOnClickListener(this);
		effectDinner.setOnClickListener(this);
		loadChart();
		if (type == ContantsUtil.EAT_PRE) {
			effectDinner.setText(R.string.eat_pre);
		} else {
			effectDinner.setText(R.string.eat_after);
		}
		if (!ContantsUtil.EFFECT_UPDATE) {
			task = new DataTask();
			task.execute();
			ContantsUtil.EFFECT_UPDATE = true;
		} else {
			loadView();
		}
	}

	@SuppressWarnings("deprecation")
	private void loadChart() {
		lineChart = ChartFactory.getScatterChartView(context, dataSet,
				mRenderer);
		chartLayout.addView(lineChart, 0, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.diabetes_listener:
			toTargetSet();
			break;
		case R.id.back_btn:
			context.finish();
			break;
		case R.id.sugar_plan:
			context.startActivity(null, PlanActivity.class);
			break;
		case R.id.pre_week:
			weekAfter = weekPre;
			nextBtn.setClickable(true);
			weekPre = weekPre - ContantsUtil.deltaWeek;
			curWeek.setText(DateUtil.parseToMD(weekPre) + "~"
					+ DateUtil.parseToMD(weekAfter));
			task = new DataTask();
			task.execute();
			break;
		case R.id.next_week:
			weekPre = weekAfter;
			weekAfter = weekAfter + ContantsUtil.deltaWeek;
			task = new DataTask();
			task.execute();
			break;
		case R.id.check_dinner:
			switchEatType(view);
			break;
		}
	}

	private void loadData() {
		// 基准线
		alignment = new float[] { Config.getFloatPro("levelLow" + type),
				Config.getFloatPro("levelMid" + type),
				Config.getFloatPro("levelHigh" + type) };
		// reset
		low = 0;
		mid = 0;
		high = 0;
		total = 0;
		data = diabetesBo.loadSubType(ContantsUtil.DEFAULT_TEMP_UID, weekPre,
				weekAfter, type);
		int size = data.size();
		// x轴坐标
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(weekPre);
		int preMonth = -1;
		mRenderer.clearXTextLabels();
		for (int i = 0; i < 7; i++) {
			calendar.add(Calendar.DATE, 1);
			int tempMonth = calendar.get(Calendar.MONTH);
			if (tempMonth == preMonth) {
				mRenderer
						.addXTextLabel(i, DateUtil.parseToString(
								calendar.getTime(), DateUtil.dd));
			} else {
				mRenderer.addXTextLabel(i, DateUtil.parseToString(
						calendar.getTime(), DateUtil.MMdd));
			}
			preMonth = tempMonth;
		}

		mRenderer.setAlignColors(new int[] { Color.RED,
				Color.rgb(136, 204, 153), Color.RED });
		mRenderer.setAlignLineColor(Color.rgb(120, 253, 100));
		mRenderer.setRect(true);
		mRenderer.setRectColor(Color.argb(40, 120, 253, 100));

		dataSet.clear();
		mRenderer.removeAllRenderers();
		XYSeries highSeries = addSeries(Color.RED);
		XYSeries midSeries = addSeries(Color.GREEN);
		XYSeries minSeries = addSeries(Color.YELLOW);
		float value1 = 0, value2 = 0;
		int index = 0;
		float tempTotal = 0;
		Log.e("size", data.size()+"----");
		for (Diabetes diabetes : data) {
			long timeDelta = diabetes.getCreate_time() - weekPre;
			int delta = (int) (timeDelta / ContantsUtil.deltaDay) - 1;
			float value = diabetes.getValue();
			if (value < alignment[0]) {
				if (value > targetMax) {
					minSeries.add(delta, (value - targetMax) * 0.2 + targetMax);
				} else {
					minSeries.add(delta, value);
				}
				low++;
			} else if (value >= alignment[0] && value <= alignment[1]) {
				mid++;
				if (value > targetMax) {
					midSeries.add(delta, (value - targetMax) * 0.2 + targetMax);
				} else {
					midSeries.add(delta, value);
				}
			} else if (value > alignment[1] && value <= alignment[2]) {
				low++;
				if (value > targetMax) {
					minSeries.add(delta, (value - targetMax) * 0.2 + targetMax);
				} else {
					minSeries.add(delta, value);
				}
			} else {
				if (value > targetMax) {
					highSeries.add(delta, (value - targetMax) * 0.2 + targetMax);
				} else {
					highSeries.add(delta, value);
				}
				high++;
			}
			if(index < data.size()/2){
				if (value1 == 0) {
					value1 = value;
				} else {
					value1 = (value1 + value) / 2;
				}
			}else{
				if (value2 == 0) {
					value2 = value;
				} else {
					value2 = (value2 + value) / 2;
				}
			}
			tempTotal += value;
			index ++;
		}
		increse = value1 == 0 ? 0 : (value2 - value1) / value1;
		if(data.size() == 0){
			persent = 1;
		}else{
			persent = mid/data.size();
		}
		if (size == 0) {
			total = 0;
		} else {
			total = tempTotal / (size * 1.0f);
		}
		dataSet.addSeries(highSeries);
		dataSet.addSeries(midSeries);
		dataSet.addSeries(minSeries);
	}

	private void loadView() {
		sugarTargetValue.setText(alignment[0] + "~" + alignment[1]);
		mRenderer.setAlign(new float[] { alignment[0], alignment[1] });
		// 数值写入
		if (data.size() == 0) {
			avgProgress.setValueLevel(0, 0);
			avgValue.setText("0");
		} else {
			avgProgress.setValueLevel(total,
					Config.getBloodState(total, alignment));
			avgValue.setText(format.format(total));
		}
		setWeek(); // 周初始化
		midValueView.setText(mid + "");
		lowValueView.setText(low + "");
		highValueView.setText(high + "");
		lineChart.repaint();
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

	private void toTargetSet() {
		String tag = "sugar_target_set";
		FragmentManager manager = context.getSupportFragmentManager();
		SugarSetFragment tempFragment = (SugarSetFragment) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = SugarSetFragment.getInstance(type);
			tempFragment.setCallBack(new CallBack() {
				@Override
				public void callBack(String persent, String value, int cur) {
					if (cur == type) {
						sugarTargetValue.setText(value);
						ContantsUtil.ENTRY_UPDATE = false;
						ContantsUtil.TOTAL_UPDATE = false;
						alignment = new float[] {
								Config.getFloatPro("levelLow" + type),
								Config.getFloatPro("levelMid" + type),
								Config.getFloatPro("levelHigh" + type)};
						loadView();
					}
				}
			});
		}
		tempFragment.show(manager, tag);
	}

	private XYMultipleSeriesRenderer getRender() {
		mRenderer = new XYMultipleSeriesRenderer();
		mRenderer
				.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
		mRenderer.setAxisTitleTextSize(getResources().getDimension(
				R.dimen.text_size_18));// 设置轴标题文本大小
		mRenderer.setYAxisMin(0);// 设置y轴最小值是0
		mRenderer.setYAxisMax(19);
		mRenderer.setXAxisMin(0);
		mRenderer.setXAxisMax(6);
		mRenderer.setZoomEnabled(false, false);
		mRenderer.setPanEnabled(false, false);
		mRenderer.setShowGrid(true);
		mRenderer.setYLabelsPadding(10);
		mRenderer.setLabelsTextSize(getResources().getDimension(
				R.dimen.text_size_14));
		mRenderer.setLabelsColor(Color.BLACK);
		mRenderer.setShowAxes(false);
		mRenderer.setShowLegend(false);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.WHITE);
		mRenderer.setMarginsColor(Color.WHITE);
		mRenderer.setXLabelsPadding(getResources().getDimension(
				R.dimen.text_size_14));

		return mRenderer;
	}

	private XYSeries addSeries(int color) {
		// addData
		XYSeries series = new XYSeries("");
		// 画一条线
		XYSeriesRenderer r = new XYSeriesRenderer();// (类似于一条线对象)
		r.setColor(color);// 设置颜色
		r.setPointStyle(PointStyle.CIRCLE);// 设置点的样式
		mRenderer.addSeriesRenderer(r);
		return series;
	}

	private void setWeek() {
		long del = System.currentTimeMillis() - weekAfter;
		if (del < ContantsUtil.deltaDay && del >= 0) {
			weekAfter = System.currentTimeMillis();
			curWeek.setText("本周");
			nextBtn.setClickable(false);
		} else {
			curWeek.setText(DateUtil.parseToMD(weekPre) + "~"
					+ DateUtil.parseToMD(weekAfter));
		}
	}

	private void switchEatType(View parent) {
		if (mealsDialog == null) {
			mealsDialog = new MealsPopDialog(context);
			mealsDialog.setCallBack(new MealsPopDialog.CallBack() {
				@Override
				public void callBack(int model) {
					switch (model) {
					case 0:
						Log.i("msg", "pre");
						effectDinner.setText(R.string.eat_pre);
						type = ContantsUtil.EAT_PRE;
						task = new DataTask();
						task.execute();
						break;
					case 1:
						Log.i("msg", "pre");
						effectDinner.setText(R.string.eat_after);
						type = ContantsUtil.EAT_AFTER;
						task = new DataTask();
						task.execute();
						break;
					}
				}
			});
		}
		mealsDialog.showAsDropDown(parent);
	}

	private class DataTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {
			loadData();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			loadView();
		}
	}
}
