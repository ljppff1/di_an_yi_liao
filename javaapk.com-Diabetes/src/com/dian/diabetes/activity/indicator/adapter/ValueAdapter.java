package com.dian.diabetes.activity.indicator.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.dto.IndicateDto;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.IndicateWidget;

public class ValueAdapter extends MBaseAdapter {

	public ValueAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_real_value);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.dayView = (TextView) convertView.findViewById(R.id.day);
		holder.timeView = (TextView) convertView.findViewById(R.id.time);
		holder.valueView = (IndicateWidget) convertView
				.findViewById(R.id.value);
		holder.valueText = (TextView) convertView.findViewById(R.id.value_text);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		IndicateDto value = (IndicateDto) itemObject;
		holder.dayView.setText(DateUtil.parseToString(value.date,
				DateUtil.yyyyMMdd));
		holder.timeView.setText(DateUtil.parseToString(value.date,
				DateUtil.HHmm));
		if (!value.isTxt) {
//			int level = 0;
//			if (value.result < value.min) {
//				level = 1;
//			} else if (value.result > value.max) {
//				level = 2;
//			}
			if (value.lastValue == -1) {
				holder.valueView.setValue(value.stringValue, value.level, -1);
			} else {
				if (value.lastValue < value.result) {
					holder.valueView.setValue(value.stringValue, value.level, 0);
				} else {
					holder.valueView.setValue(value.stringValue, value.level, 1);
				}
			}
			holder.valueView.setVisibility(View.VISIBLE);
			holder.valueText.setVisibility(View.GONE);
		} else {
			holder.valueView.setVisibility(View.GONE);
			holder.valueText.setVisibility(View.VISIBLE);
			if (value.level == 0) {
				holder.valueText.setTextColor(context.getResources().getColor(
						R.color.trend_down_normal));
			} else if (value.level == 1) {
				holder.valueText.setTextColor(context.getResources().getColor(
						R.color.trend_down_bad));
			} else {
				holder.valueText.setTextColor(context.getResources().getColor(
						R.color.trend_up_more));
			}
			holder.valueText.setText(value.stringValue);
		}
	}

	class ViewHolder {
		TextView dayView;
		TextView timeView;
		TextView valueText;
		IndicateWidget valueView;
	}

}
