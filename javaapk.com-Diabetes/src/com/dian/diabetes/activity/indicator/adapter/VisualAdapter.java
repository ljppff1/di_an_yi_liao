package com.dian.diabetes.activity.indicator.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.db.dao.IndicateValue;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.IndicateWidget;
import com.dian.diabetes.widget.SigIndicateWidget;

public class VisualAdapter extends MBaseAdapter {

	private String union = "kg";
	private String key = "";
	private DecimalFormat format;

	public VisualAdapter(Context context, List<?> data,String type) {
		super(context, data, R.layout.item_visual_layout);
		if("heart".equals(type)){
			format = new DecimalFormat("0");
		}else{
			format = new DecimalFormat("0.0");
		}
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.dayView = (TextView) convertView.findViewById(R.id.day);
		holder.timeView = (TextView) convertView.findViewById(R.id.time);
		holder.valueView = (IndicateWidget) convertView
				.findViewById(R.id.value);
		holder.union = (TextView) convertView.findViewById(R.id.union);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		IndicateValue value = (IndicateValue) itemObject;
		holder.dayView.setText(DateUtil.parseToString(value.getCreate_time(),
				DateUtil.yyyyMMdd));
		holder.timeView.setText(DateUtil.parseToString(value.getCreate_time(),
				DateUtil.HHmm));
		holder.union.setText(union);
		if ("openPress".equals(value.getKey())) {
			holder.valueView.setValue((int) value.getValue(),
					(int) value.getValue1(), value.getLevel(),
					value.getUp_down());
		} else {
			holder.valueView.setValue(format.format(value.getValue()),
					value.getLevel(), value.getUp_down());
		}
	}

	class ViewHolder {
		TextView dayView;
		TextView timeView;
		IndicateWidget valueView;
		TextView union;
	}

	public void setUnion(String union, String key) {
		this.union = union;
		this.key = key;
	}

}
