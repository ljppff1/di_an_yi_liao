package com.dian.diabetes.activity.report.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.dto.ReportDto;

/**
 * 综合报告adapter
 * @author longbh
 *
 */
public class ReportAdapter extends MBaseAdapter {

	public ReportAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_report_layout);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.title = (TextView) convertView.findViewById(R.id.title);
		holder.value = (TextView) convertView.findViewById(R.id.content);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		ReportDto dto = (ReportDto) itemObject;
		holder.title.setText(dto.name);
		holder.value.setText(dto.value);
	}

	class ViewHolder {
		TextView title;
		TextView value;
	}

}
