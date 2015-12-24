package com.dian.diabetes.activity.eat;

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
import com.dian.diabetes.activity.eat.adapter.NetAdapter;
import com.dian.diabetes.db.dao.Eat;
import com.dian.diabetes.db.dao.Sport;
import com.dian.diabetes.dto.NetModel;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 热量净值，主要在datatask里边进行数据处理
 * @author hua
 *
 */
public class CaloriesNetFragment extends TotalBaseFragment implements
		OnClickListener {

	@ViewInject(id = R.id.chart)
	private RelativeLayout chart;
	@ViewInject(id = R.id.data_list)
	private ListView dataList;

	// chart
	private GraphicalView lineChart;
	private XYMultipleSeriesDataset dataSet;
	private XYMultipleSeriesRenderer mRenderer;
	private BasicActivity activity;
	private CaloriesFragment parentFragment;

	// data
	private NetAdapter adapter;
	private List<NetModel> caloreData;
	private List<NetModel> listData;

	public static CaloriesNetFragment getInstance() {
		return new CaloriesNetFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (BasicActivity) context;
		dataSet = new XYMultipleSeriesDataset();
		parentFragment = (CaloriesFragment) getParentFragment();
		XYSeries series = new XYSeries("饮食");
		dataSet.addSeries(series);
		caloreData = new ArrayList<NetModel>();
		listData = new ArrayList<NetModel>();
		adapter = new NetAdapter(activity, listData);
		ContantsUtil.NET_EAT_UPDATE = false;
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
		if (!ContantsUtil.NET_EAT_UPDATE) {
			new DataTask().execute();
			ContantsUtil.NET_EAT_UPDATE = true;
		}
		dataList.setAdapter(adapter);
		switchListChart();
	}

	private void loadData() {
		dataSet.clear();
		mRenderer.clearXTextLabels();
		XYSeries series = new XYSeries("饮食");
		float value = 0,min = 0;
		for (int i = 0; i < caloreData.size(); i++) {
			float temp = caloreData.get(i).sub(ContantsUtil.curUser.getSupport());
			series.add(i, temp);
			mRenderer.addXTextLabel(i, caloreData.get(i).getDay());
			value = Math.max(value, temp);
			min = Math.min(min, temp);
		}
		if(value < 30){
			mRenderer.setYAxisMax(30);
		}else{
			mRenderer.setYAxisMax(value + value/10);
		}
		if(min > 0){
			min = 0;
		}else{
			min = min + min/10;
		}
		mRenderer.setYAxisMin(min);
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
		mRenderer.setYLabelsAlign(Align.CENTER);
		mRenderer.setLabelsColor(Color.BLACK);
		mRenderer.setShowAxes(false);
		mRenderer.setShowLegend(false);
		mRenderer.setShowLabels(true);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.WHITE);
		mRenderer.setMarginsColor(Color.WHITE);
		mRenderer.setXLabelsPadding(getResources().getDimension(
				R.dimen.text_size_12));

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
	
	public void switchListChart(){
		if(parentFragment.isCurentPage()){
			chart.setVisibility(View.VISIBLE);
			dataList.setVisibility(View.GONE);
			
		}else{
			chart.setVisibility(View.GONE);
			dataList.setVisibility(View.VISIBLE);	
			adapter.notifyDataSetChanged();
		}
	}

	private class DataTask extends AsyncTask<Object, Object, Object> {
		
		private List<NetModel> tempList = new ArrayList<NetModel>();

		@Override
		protected Object doInBackground(Object... arg0) {
			caloreData.clear();
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, parentFragment.getDay());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			List<Eat> tempeatData = parentFragment.getData();
			List<Sport> tempsportData = parentFragment.getSportData();
			String day = "";
			int index = 0, sportIndex = 0, eatIndex = 0, delta = -parentFragment
					.getDelta();
			float eatDetaValue = 0, sportDetaValue = 0;
			int preMonth = -1;
			for (int i = 0; i > parentFragment.getDay()+1; i--) {
				index++;
				calendar.add(Calendar.DATE, 1);
				int tempMonth = calendar.get(Calendar.MONTH);
				day = DateUtil.parseToString(calendar.getTime(),
						DateUtil.yyyyMMdd);
				// 饮食
				float itemValue = 0;
				for (int j = eatIndex; j < tempeatData.size(); j++) {
					Eat eat = tempeatData.get(j);
					if (day.equals(eat.getDay())) {
						eatIndex++;
						itemValue += eat.getTotal();
					} else {
						break;
					}
				}

				// 运动
				float sportValue = 0;
				for (int j = sportIndex; j < tempsportData.size(); j++) {
					Sport sport = tempsportData.get(j);
					if (day.equals(sport.getDay())) {
						sportIndex++;
						sportValue += sport.getTotal();
					} else {
						break;
					}
				}
				eatDetaValue += itemValue;
				sportDetaValue += sportValue;
				if(itemValue != 0 || sportValue != 0){
					NetModel model = new NetModel(day, itemValue,
							sportValue);
					tempList.add(model);
				}
				if (index == delta || i == (parentFragment.getDay() + 1)) {
					String dayout;
					if(tempMonth == preMonth){
						dayout = DateUtil.parseToString(calendar.getTime(),DateUtil.dd);
					}else{
						dayout = DateUtil.parseToString(calendar.getTime(),DateUtil.MMdd);
					}
					preMonth = tempMonth;
					NetModel model = new NetModel(dayout, eatDetaValue,
							sportDetaValue);
					caloreData.add(model);
					eatDetaValue = 0;
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
