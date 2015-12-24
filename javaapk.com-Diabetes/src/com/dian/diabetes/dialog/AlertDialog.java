package com.dian.diabetes.dialog;


import com.dian.diabetes.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlertDialog extends Dialog {

	private Button okBtn;
	private Button cancelBtn;
	private TextView contentView;
	private CallBack callBack;
	private String content;

	public AlertDialog(Context context,String content) {
		super(context, R.style.Dialog);
		setContentView(R.layout.dialog_alert);
		this.setCanceledOnTouchOutside(true);
		this.content = content;
		initDialog();
	}

	private void initDialog() {
		okBtn = (Button) findViewById(R.id.ok_btn);
		okBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				callBack.callBack();
				dismiss();
			}
		});
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callBack.cancel();
				dismiss();
			}
		});
		contentView = (TextView) findViewById(R.id.dialog_operate_content);
		contentView.setText(content);
	}

	public void setCallBack(CallBack callBack) {
		this.callBack = callBack;
	}
	
	public void setOkText(String text){
		okBtn.setText(text);
	}

	public interface CallBack {
		void callBack();
		void cancel();
	}

}
