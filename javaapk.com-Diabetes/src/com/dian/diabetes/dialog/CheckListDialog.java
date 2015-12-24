package com.dian.diabetes.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.dialog.adapter.CheckAdapter;
import com.dian.diabetes.dto.CheckModel;

public class CheckListDialog extends Dialog implements
		OnItemClickListener {

	private TextView titleView;
	private ListView dataList;
	private Button okBtn;

	private Context context;
	private CallBack callBack;
	protected List<CheckModel> data;
	private CheckAdapter adapter;

	public CheckListDialog(Context context) {
		super(context, R.style.Dialog);
		this.context = context;
		setContentView(R.layout.dialog_list_check);
		this.setCanceledOnTouchOutside(true);
		data = new ArrayList<CheckModel>();
		initData(data);
		initView();
	}

	private void initView() {
		adapter = new CheckAdapter(context, data);
		titleView = (TextView) findViewById(R.id.title);
		dataList = (ListView) findViewById(R.id.content_list);
		okBtn = (Button) findViewById(R.id.ok_btn);
		dataList.setOnItemClickListener(this);
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (callBack != null) {
					callBack.callBack(data);
				}
				dismiss();
			}
		});
		dataList.setAdapter(adapter);
	}

	private void initData(List<CheckModel> data) {
		CheckModel model = new CheckModel("0", "早餐", false);
		data.add(model);
		model = new CheckModel("1", "中餐", false);
		data.add(model);
		model = new CheckModel("2", "晚餐", false);
		data.add(model);
		model = new CheckModel("3", "睡前", false);
		data.add(model);
	}

	public interface CallBack {
		void callBack(List<CheckModel> data);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		data.get(position).isCheck = !data.get(position).isCheck;
		adapter.notifyDataSetChanged();
	}

	public void setTitle(String title) {
		this.titleView.setText(title);
	}

	public void setCall(CallBack callBack) {
		this.callBack = callBack;
	}
}
