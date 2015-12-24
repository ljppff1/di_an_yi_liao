package com.dian.diabetes.activity.date;

import hirondelle.date4j.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.db.BloodBo;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.widget.NGridView;

/**
 * month date view
 * 
 * @author longbh
 * 
 */
public class DateView extends LinearLayout implements OnClickListener {

	private NGridView monthPager;
	private TextView norMonthView;
	private ImageButton nextBtn;
	private ImageButton preBtn;
	private Button closeBtn;

	private CaldroidGridAdapter adapter;
	private Calendar calendar;
	private int todayMonth;
	private int todayYear;
	private DateTime date;
	private List<DateTime> datetimeList = new ArrayList<DateTime>();
	private Map<String, Integer> params = new HashMap<String, Integer>();

	private LayoutInflater inflater;
	private View view;
	private Context context;
	private Animation closeToTop;
	private CallBack callback;
	private Date curent = new Date();
	private boolean only = true;
	private BloodBo bo;

	public DateView(Context context) {
		super(context);
		this.context = context;
		initView();
	}

	public DateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}

	private void initView() {
		bo = new BloodBo(context);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.fragment_month_layout, null);
		addView(view, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		monthPager = (NGridView) view.findViewById(R.id.calendar_gridview);
		monthPager.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DateTime dtime = adapter.getPosition(position);
				if (dtime.compareTo(date) != 1) {
					if (todayMonth == dtime.getMonth()) {
						//startAnimation(closeToTop);
						curent = CalendarHelper.convertDateTimeToDate(dtime);
						only = false;
						setVisibility(View.GONE);
						if (callback != null) {
							callback.callBack(curent,true);
						}
					}
				} else {
					if (todayMonth == dtime.getMonth()) {
						Toast.makeText(context, R.string.toast_time_after,
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		norMonthView = (TextView) view.findViewById(R.id.current_day);
		nextBtn = (ImageButton) view.findViewById(R.id.next);
		preBtn = (ImageButton) view.findViewById(R.id.pre);
		closeBtn = (Button) view.findViewById(R.id.close);

		adapter = new CaldroidGridAdapter(context, datetimeList, params);
		nextBtn.setOnClickListener(this);
		preBtn.setOnClickListener(this);
		closeBtn.setOnClickListener(this);
		calendar = Calendar.getInstance();
		todayYear = calendar.get(Calendar.YEAR);
		todayMonth = calendar.get(Calendar.MONTH) + 1;
		date = CalendarHelper.convertDateToDateTime(new Date());

		calendar.get(Calendar.DAY_OF_MONTH);
		monthPager.setAdapter(adapter);
		loadDateTime(todayMonth, todayYear);
		closeToTop = AnimationUtils.loadAnimation(context, R.anim.to_top_out);
		closeToTop.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				setVisibility(View.GONE);
				if (callback != null) {
					callback.callBack(curent,false);
				}
			}
		});
	}

	private void loadData() {
		bo.queryLevelData(params, todayYear, todayMonth,
				ContantsUtil.DEFAULT_TEMP_UID);

	}

	private void loadDateTime(int month, int year) {
		todayMonth = month;
		todayYear = year;
		datetimeList.clear();
		CalendarHelper.getFullWeeks(datetimeList, month, year, 1, true);
		adapter.setCurentMonth(month);
		adapter.notifyDataSetChanged();
		String yMonth = year + "年" + month + "月";
		norMonthView.setText(yMonth);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pre:
			calendar.add(Calendar.MONTH, -1);
			loadDateTime(calendar.get(Calendar.MONTH) + 1,
					calendar.get(Calendar.YEAR));
			new DataTask().execute();
			break;
		case R.id.next:
			calendar.add(Calendar.MONTH, 1);
			loadDateTime(calendar.get(Calendar.MONTH) + 1,
					calendar.get(Calendar.YEAR));
			new DataTask().execute();
			break;
		case R.id.close:
			setVisibility(View.GONE);
			if (callback != null) {
				callback.callBack(curent,false);
			}
			//startAnimation(closeToTop);
			only = true;
			break;
		}
	}

	public void showDate() {
		this.setVisibility(View.VISIBLE);
		loadDateTime(todayMonth, todayYear);
		new DataTask().execute();
	}

	public void setCallBack(CallBack tempback) {
		callback = tempback;
	}

	public interface CallBack {
		void callBack(Date date,boolean isChoose);
	}

	private class DataTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {
			loadData();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			adapter.notifyDataSetChanged();
		}
	}
}
