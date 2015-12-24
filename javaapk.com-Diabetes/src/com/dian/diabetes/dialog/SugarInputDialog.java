package com.dian.diabetes.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.widget.wheel.NumericWheelAdapter;
import com.dian.diabetes.widget.wheel.WheelView;

public class SugarInputDialog extends Dialog implements
		android.view.View.OnClickListener {

	private String title;

	private CallBack callBack;
	private TextView titleView;
	private Button okBtn;

	private WheelView tenView;
	private WheelView numberView;
	private WheelView pointView;

	public SugarInputDialog(Context context, String title) {
		super(context, R.style.Dialog);
		this.title = title;
		setCanceledOnTouchOutside(true);
		setContentView(R.layout.dialog_sugar_input);
		initView();
	}

	private void initView() {
		titleView = (TextView) findViewById(R.id.dialog_operate_name);
		titleView.setText(title);
		okBtn = (Button) findViewById(R.id.ok_btn);
		okBtn.setOnClickListener(this);
		Button btnCancel = (Button) findViewById(R.id.cancel_btn);
		btnCancel.setOnClickListener(this);
		tenView = (WheelView) findViewById(R.id.ten);
		numberView = (WheelView) findViewById(R.id.number);
		pointView = (WheelView) findViewById(R.id.point);

		// ten
		tenView.setAdapter(new NumericWheelAdapter(0, 2));
		tenView.setCyclic(true);
		tenView.setVisibleItems(3);

		// number
		numberView.setAdapter(new NumericWheelAdapter(0, 9));
		numberView.setCyclic(true);
		numberView.setVisibleItems(3);
		numberView.setLabel(".");

		// point
		pointView.setAdapter(new NumericWheelAdapter(0, 9));
		pointView.setCyclic(true);
		pointView.setVisibleItems(3);
	}

	public void show(float value) {
		super.show();
		int number = (int) value;
		float fValue = (value - number) * 10;
		tenView.setCurrentItem((int) number / 10);
		numberView.setCurrentItem((int) number % 10);
		pointView.setCurrentItem((int) fValue);
	}

	public void setCallBack(CallBack cal) {
		this.callBack = cal;
	}

	public interface CallBack {
		boolean callBack(float value);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ok_btn:
			float value = (float) (tenView.getCurrentItem() * 10
					+ numberView.getCurrentItem() + (pointView.getCurrentItem() / 10.0));
			boolean state = callBack.callBack(value);
			if (state) {
				dismiss();
			}
			break;
		case R.id.cancel_btn:
			dismiss();
			break;
		}
	}
}