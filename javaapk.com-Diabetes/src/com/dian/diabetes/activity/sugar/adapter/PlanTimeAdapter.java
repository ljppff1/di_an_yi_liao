package com.dian.diabetes.activity.sugar.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.activity.sugar.model.TimeModel;
import com.dian.diabetes.tool.Config;

public class PlanTimeAdapter extends MBaseAdapter {

	public PlanTimeAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_plan_time);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.itemDelta = (TextView) convertView.findViewById(R.id.time_delta);
		holder.time = (TextView) convertView.findViewById(R.id.time);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		TimeModel set = (TimeModel) itemObject;
		holder.itemDelta.setText(set.getName());
		final String key = "clock" + set.getType() + set.getSubType();
		holder.time.setText(Config.getProperty(key) + "");
	}

	class ViewHolder {
		TextView itemDelta;
		TextView time;
	}
}
