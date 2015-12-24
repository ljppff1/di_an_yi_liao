package com.dian.diabetes.widget;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dian.diabetes.R;

public class IndicateWidget extends RelativeLayout {

	private FragmentActivity context;
	private LayoutInflater inflater;

	private TextView valueView;
	private ImageView bgIcon;
	private ImageView trendView;

	private int[] imgSourceId;
	private int[] trendUpId;
	private int[] trendDownId;

	public IndicateWidget(Context context) {
		super(context);
		this.context = (FragmentActivity) context;
		initView();
	}

	public IndicateWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = (FragmentActivity) context;
		initView();
	}

	public IndicateWidget(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = (FragmentActivity) context;
		initView();
	}

	private void initView() {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.widget_indicate_layout, null);
		valueView = (TextView) view.findViewById(R.id.value);
		bgIcon = (ImageView) view.findViewById(R.id.icon);
		trendView = (ImageView) view.findViewById(R.id.trend_img);
		addView(view);
		imgSourceId = new int[] { R.drawable.good_circle,
				R.drawable.normal_circle, R.drawable.red_circle };
		trendUpId = new int[] { R.drawable.trend_up_normal,
				R.drawable.trend_up_more, R.drawable.trend_up_bad };
		trendDownId = new int[] { R.drawable.trend_down_good,
				R.drawable.trend_down_normal, R.drawable.trend_down_bad };
	}

	public void setValue(Object value, int level, int trend) {
		valueView.setText(value + "");
		bgIcon.setImageResource(imgSourceId[level]);
		if (trend == 0) {
			trendView.setImageResource(trendUpId[level]);
		} else if (trend == 1) {
			trendView.setImageResource(trendDownId[level]);
		} else {
			trendView.setVisibility(View.GONE);
		}
	}
	
	public void setValue(Object value, Object bottom, int level, int trend) {
		bgIcon.setImageResource(imgSourceId[level]);
		valueView.setText(value + "/" + bottom + "");
		if (trend == 0) {
			trendView.setImageResource(trendUpId[level]);
		} else if (trend == 1) {
			trendView.setImageResource(trendDownId[level]);
		} else {
			trendView.setVisibility(View.GONE);
		}
	}
}
