package com.dian.diabetes.widget;

import java.text.DecimalFormat;

import com.dian.diabetes.R;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ValueWidget extends RelativeLayout {

	private FragmentActivity context;
	private LayoutInflater inflater;

	private TextView valueView;
	private TextView unitView;
	private ImageView bgIcon;

	private int[] imgSourceId;

	public ValueWidget(Context context) {
		super(context);
		this.context = (FragmentActivity) context;
		initView();
	}

	public ValueWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = (FragmentActivity) context;
		initView();
	}

	public ValueWidget(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = (FragmentActivity) context;
		initView();
	}

	private void initView() {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.widget_effect_value, null);
		valueView = (TextView) view.findViewById(R.id.value);
		unitView = (TextView) view.findViewById(R.id.unit);
		bgIcon = (ImageView) view.findViewById(R.id.icon);
		RelativeLayout.LayoutParams params = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		addView(view, params);
		imgSourceId = new int[] { R.drawable.good_circle,
				R.drawable.normal_circle, R.drawable.normal_circle,
				R.drawable.red_circle };
	}

	/**
	 * 赋值
	 * 
	 * @param value
	 */
	public void setValue(DecimalFormat format, float value, int level) {
		valueView.setText(format.format(value));
		if (level == -1) {
			unitView.setVisibility(View.GONE);
			valueView.setText("未测");
			bgIcon.setImageResource(R.drawable.no_circle);
		} else {
			bgIcon.setImageResource(imgSourceId[level]);
			unitView.setVisibility(View.VISIBLE);
		}
		//unitView.setVisibility(View.VISIBLE);
	}

	public void setValue(int value) {
		valueView.setText(value + "");
		bgIcon.setImageResource(imgSourceId[0]);
		unitView.setVisibility(View.GONE);
	}
	
	public void setCount(int value) {
		valueView.setText(value + "");
		bgIcon.setImageResource(R.drawable.count_circle);
		unitView.setVisibility(View.GONE);
	}
	
	public void setFloatValue(float value) {
		valueView.setText(value + "");
		bgIcon.setImageResource(imgSourceId[0]);
		unitView.setVisibility(View.GONE);
	}

	public void setValue(String value, int level) {
		valueView.setText(value);
		if (level == -1) {
			unitView.setVisibility(View.GONE);
			bgIcon.setImageResource(R.drawable.no_circle);
		} else {
			bgIcon.setImageResource(imgSourceId[level]);
			unitView.setVisibility(View.VISIBLE);
		}
	}

	public void setValueLevel(DecimalFormat format, float value, int level) {
		valueView.setText(format.format(value));
		if (level == -1) {
			unitView.setVisibility(View.GONE);
			valueView.setText("未测");
			bgIcon.setImageResource(R.drawable.no_circle);
		} else {
			bgIcon.setImageResource(imgSourceId[level]);
			unitView.setVisibility(View.VISIBLE);
		}
		unitView.setVisibility(View.VISIBLE);
		invalidate();
	}
}
