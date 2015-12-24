package com.dian.diabetes.activity.eat.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.dto.NetModel;

public class AllAdapter extends MBaseAdapter{

	public AllAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_calore_all);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.label = (TextView) convertView.findViewById(R.id.label);
		holder.value = (TextView) convertView.findViewById(R.id.value);
		holder.value1 = (TextView) convertView.findViewById(R.id.value1);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		NetModel model = (NetModel) itemObject;
		holder.label.setText(model.getDay());
		holder.value.setText("饮食：" + (int)model.getEat());
		holder.value1.setText("运动：" + (int)model.getSport());
	}
	
	class ViewHolder{
		TextView label;
		TextView value;
		TextView value1;
	}

}
