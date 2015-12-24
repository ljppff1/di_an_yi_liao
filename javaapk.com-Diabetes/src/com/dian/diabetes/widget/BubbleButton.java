package com.dian.diabetes.widget;

import com.dian.diabetes.R;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class BubbleButton extends RelativeLayout {

	private Context context;

	private TextView bubleValue;
	private ImageView bg;

	public BubbleButton(Context context) {
		super(context);
		this.context = context;
		initView();
	}

	public BubbleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}

	public BubbleButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = (FragmentActivity) context;
		initView();
	}

	private void initView() {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.widget_bubble_layout, null);
		bg = (ImageView) view.findViewById(R.id.icon);
		bubleValue = (TextView) view.findViewById(R.id.value);
		RelativeLayout.LayoutParams params = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		addView(view, params);
	}

	/**
	 * 设置冒泡值
	 * 
	 * @param value
	 */
	public void setBubleValue(int value) {
		if (value == 0) {
			bubleValue.setVisibility(View.GONE);
		} else {
			bubleValue.setVisibility(View.VISIBLE);
			bubleValue.setText(value + "");
		}
	}

	/**
	 * 设置冒泡背景
	 * 
	 * @param resourceId
	 */
	public void setBg(int resourceId) {
		bg.setImageResource(resourceId);
	}

	public void setBubleBg(int resourceId) {
		bubleValue.setBackgroundResource(resourceId);
	}
}
