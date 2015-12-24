package com.dian.diabetes.activity.sport.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.db.dao.Sport;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.ValueWidget;

public class SportAdapter extends MBaseAdapter {
	
	private DecimalFormat format;

	public SportAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_sport_view);
		format = new DecimalFormat("##0.0");
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.sportName = (TextView) convertView.findViewById(R.id.sport_name);
		holder.timeView = (TextView) convertView.findViewById(R.id.time);
		holder.timeNum = (TextView) convertView.findViewById(R.id.time_length);
		holder.totalCalore = (TextView) convertView.findViewById(R.id.calorie);
		holder.sportStep = (TextView) convertView.findViewById(R.id.step);
		holder.stepLayout = (LinearLayout) convertView.findViewById(R.id.sport_run);
		holder.otherType = (ImageView) convertView.findViewById(R.id.other_type);
		holder.otherValue = (ValueWidget) convertView.findViewById(R.id.sport_up);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		Sport sport = (Sport) itemObject;
		holder.timeView.setText(DateUtil.parseToString(sport.getCreate_time(), DateUtil.HHmm));
		holder.timeNum.setText(sport.getSportTime()+"min");
		holder.totalCalore.setText("-" + format.format(sport.getTotal()));
		holder.sportName.setText(sport.getSportName());
		holder.sportStep.setText(sport.getSportTime() + "");
	}

	class ViewHolder{
		TextView sportName;
		TextView timeView;
		TextView timeNum;
		TextView totalCalore;
		TextView sportStep;
		LinearLayout stepLayout;
		LinearLayout otherLayout;
		ImageView otherType;
		ValueWidget otherValue;
	}
}
