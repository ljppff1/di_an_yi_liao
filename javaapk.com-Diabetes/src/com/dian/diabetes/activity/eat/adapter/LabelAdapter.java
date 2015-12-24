package com.dian.diabetes.activity.eat.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.dto.TotalModel;

public class LabelAdapter extends MBaseAdapter{

	public LabelAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_label_layout);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.label = (TextView) convertView.findViewById(R.id.label);
		holder.value = (TextView) convertView.findViewById(R.id.value);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		TotalModel model = (TotalModel) itemObject;
		holder.label.setText(model.getDay());
		holder.value.setText(model.getValue() + "");
	}

	class ViewHolder{
		TextView label;
		TextView value;
	}
}
