package com.dian.diabetes.activity.medicine.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.db.dao.Alarm;
import com.dian.diabetes.tool.CommonUtil;

public class AlarmAdapter extends MBaseAdapter{
	
	private DecimalFormat format;

	public AlarmAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_medicine_alarm);
		format = new DecimalFormat("00");
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.alarmName = (TextView) convertView.findViewById(R.id.alarm_name);
		holder.alarmTime = (TextView) convertView.findViewById(R.id.alarm_time);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		Alarm alarm = (Alarm) itemObject;
		holder.alarmName.setText(CommonUtil.getValue("diabetes" + format.format(alarm.getSub_type())));
		holder.alarmTime.setText(format.format(alarm.getHour()) + ":" + format.format(alarm.getMinite()));
	}
	
	class ViewHolder{
		TextView alarmName;
		TextView alarmTime;
	}

}
