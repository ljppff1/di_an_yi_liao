package com.dian.diabetes.widget;

import com.dian.diabetes.R;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AlarmBar extends RelativeLayout implements OnTouchListener {

	private FragmentActivity context;
	private LayoutInflater inflater;
	
	private ImageView clockIcon;
	private TextView clockValue;
	private int lastX = 0;
	private int lastY = 0;
	private int screenWidth;

	public AlarmBar(Context context) {
		super(context);
		this.context = (FragmentActivity) context;
		initView();
	}

	public AlarmBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = (FragmentActivity) context;
		initView();
	}

	public AlarmBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = (FragmentActivity) context;
		initView();
	}

	private void initView() {
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		screenWidth = metric.widthPixels;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.widget_alarm_bar, null);
		
		//初始化view
		clockIcon = (ImageView) view.findViewById(R.id.clock_icon);
		clockValue = (TextView) view.findViewById(R.id.clock_value);
		view.setOnTouchListener(this);
		addView(view);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			int dx = (int) event.getRawX() - lastX;
			int dy = (int) event.getRawY() - lastY;

			int left = v.getLeft() + dx;
			int top = v.getTop() + dy;
			int right = v.getRight() + dx;
			int bottom = v.getBottom() + dy;
			int vwidth = v.getWidth();
			int vheight = v.getHeight();
			if (left < 0) {
				left = 0;
				right = left + vwidth;
			}
			if (top < 0) {
				top = 0;
				bottom = top + v.getHeight();
			}
			if (left > getRight() - vwidth) {
				left = getRight() - vwidth;
				right = getRight();
			}
			int leftTop = getBottom() - vheight;
			if (top > leftTop) {
				top = leftTop;
				bottom = leftTop + vheight;
			}			
			if((left + v.getWidth()/2) > screenWidth/2){
				
			}else{
				
			}
			v.layout(left, top, right, bottom);
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_UP:
			int leftUp = v.getLeft();
			int topUp = v.getTop();
			int bottomUp = v.getBottom();
			if((leftUp + v.getWidth()/2) > screenWidth/2){
				v.layout(screenWidth - v.getWidth(), topUp, screenWidth, bottomUp);
			}else{
				v.layout(0, topUp, v.getWidth(), bottomUp);
			}
			break;
		}
		return false;
	}

}
