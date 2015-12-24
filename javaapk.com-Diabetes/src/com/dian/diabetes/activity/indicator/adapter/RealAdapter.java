package com.dian.diabetes.activity.indicator.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.dto.IndicateDto;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.widget.IndicateWidget;

public class RealAdapter extends MBaseAdapter {

	public RealAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_real_layout);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.titleView = (TextView) convertView.findViewById(R.id.name);
		holder.unionView = (TextView) convertView.findViewById(R.id.union);
		holder.valueView = (IndicateWidget) convertView
				.findViewById(R.id.value);
		holder.valueText = (TextView) convertView.findViewById(R.id.value_text);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		IndicateDto value = (IndicateDto) itemObject;
		holder.titleView.setText(value.item_name);
		if (!value.isTxt) {
			// int level = 0;
			// if(value.result < value.min){
			// level = 1;
			// }else if(value.result > value.max){
			// level = 2;
			// }
			if (value.lastValue == null) {
				holder.valueView.setValue(value.stringValue, value.level, -1);
			} else {
				holder.valueView.setValue(value.stringValue, value.level, 0);
			}
			holder.valueView.setVisibility(View.VISIBLE);
			holder.valueText.setVisibility(View.GONE);
			holder.unionView.setVisibility(View.VISIBLE);
			holder.unionView.setText(value.units);
		} else {
			holder.valueView.setVisibility(View.GONE);
			holder.valueText.setVisibility(View.VISIBLE);
			holder.unionView.setVisibility(View.INVISIBLE);
			// if(CheckUtil.checkEquels(value.stringValue, value.surport)){
			// holder.valueText.setTextColor(context.getResources().getColor(R.color.trend_down_normal));
			// }else{
			// holder.valueText.setTextColor(context.getResources().getColor(R.color.trend_down_bad));
			// }
			if (value.level == 0) {
				holder.valueText.setTextColor(context.getResources().getColor(
						R.color.trend_down_normal));
			} else if (value.level == 0) {
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
		TextView titleView;
		TextView unionView;
		TextView valueText;
		IndicateWidget valueView;
	}
}
