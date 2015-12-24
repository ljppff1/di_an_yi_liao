package com.dian.diabetes.activity.eat.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.dto.PlanDto;

public class PlanAdapter extends MBaseAdapter{

	public PlanAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_eat_plan);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.title = (TextView) convertView.findViewById(R.id.title);
		holder.content = (TextView) convertView.findViewById(R.id.content);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder =  (ViewHolder) convertView.getTag();
		PlanDto dto = (PlanDto) itemObject;
		holder.title.setText(dto.name);
		holder.content.setText(dto.contain);
	}

	class ViewHolder{
		TextView title;
		TextView content;
	}
}
