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

public class SigIndicateWidget extends RelativeLayout {

	private FragmentActivity context;
	private LayoutInflater inflater;

	private TextView valueView;
	private TextView bottomView;
	private ImageView bgIcon;
	private View line;

	private int[] imgSourceId;

	public SigIndicateWidget(Context context) {
		super(context);
		this.context = (FragmentActivity) context;
		initView();
	}

	public SigIndicateWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = (FragmentActivity) context;
		initView();
	}

	public SigIndicateWidget(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = (FragmentActivity) context;
		initView();
	}

	private void initView() {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.widget_notrend_layout, null);
		valueView = (TextView) view.findViewById(R.id.value);
		bottomView = (TextView) view.findViewById(R.id.bottom);
		bgIcon = (ImageView) view.findViewById(R.id.icon);
		line = view.findViewById(R.id.line);
		addView(view);
		imgSourceId = new int[] { R.drawable.good_circle,
				R.drawable.normal_circle, R.drawable.red_circle };
	}

	public void setValue(float value, int level) {
		valueView.setText(value + "");
		bgIcon.setImageResource(imgSourceId[level]);
		bottomView.setVisibility(View.GONE);
		line.setVisibility(View.GONE);
	}

	public void setValue(float value, int bottom, int level) {
		valueView.setText(value + "");
		bgIcon.setImageResource(imgSourceId[level]);
		bottomView.setText(bottom + "");
	}
	
}
