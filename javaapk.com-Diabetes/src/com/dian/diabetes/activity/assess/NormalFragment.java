package com.dian.diabetes.activity.assess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.assess.adapter.NormalAdapter;
import com.dian.diabetes.dialog.NumberDialog;
import com.dian.diabetes.dialog.WheelCallBack;
import com.dian.diabetes.dto.DataModel;
import com.dian.diabetes.dto.ItemModel;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 问题列表fragment
 * @author hua
 *
 */
public class NormalFragment extends BaseFragment {
	@ViewInject(id = R.id.sugar_list)
	private ListView entryList;
	
	private StrongDialog strongDialog;
	private NumberDialog heightDialog;

	private AssessActivity activity;
	private ItemModel datas;
	private NormalAdapter adapter;

	public static NormalFragment getInstance(int index) {
		NormalFragment fragment = new NormalFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("index", index);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (AssessActivity) context;
		datas = activity.getByKey(getArguments().getInt("index"));
		adapter = new NormalAdapter(context, datas.question_line);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_entry_list, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		entryList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				DataModel model = datas.question_line.get(position);
				if(model.method_question == 0){
					adapter.check(position);
				}else if(model.method_question == 1){
					openDialog(model);
				}else if(model.method_question == 2){
					openWheelDialog(model);
				}
			}
		});
		entryList.setAdapter(adapter);
	}
	
	private void openWheelDialog(final DataModel model){
		heightDialog = new NumberDialog(activity, new WheelCallBack() {
			@Override
			public void item(int value) {
				model.answer = value + "";
				adapter.notifyDataSetChanged();
			}
		}, 300, 0, model.unit, model.question);
		if(CheckUtil.isNull(model.answer)){
			heightDialog.show(StringUtil.toInt(model.value_mid));
		}else{
			heightDialog.show(StringUtil.toInt(model.answer));
		}
	}
	
	//弹出列表选择框
	private void openDialog(final DataModel dataModel){
		strongDialog = new StrongDialog(activity);
		strongDialog.addData(dataModel.unit);
		strongDialog.setCall(new StrongDialog.CallBack() {
			@Override
			public void callBack(int position, String model) {
				dataModel.answer = model;
				adapter.notifyDataSetChanged();
			}
		});
		strongDialog.show();
	}
}
