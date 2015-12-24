package com.dian.diabetes.activity.indicator.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.db.dao.Indicate;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.IndicateWidget;

/**
 * 指标首页
 * 
 * @author longbh
 * 
 */
public class IndicatorAdapter extends MBaseAdapter {

	private long systime;
	private DecimalFormat format;

	public IndicatorAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_indicate_layout);
		systime = System.currentTimeMillis();
		format = new DecimalFormat("##0");
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.iconView = (ImageView) convertView.findViewById(R.id.icon);
		holder.titleView = (TextView) convertView.findViewById(R.id.name);
		holder.contentView = (TextView) convertView
				.findViewById(R.id.content_value);
		holder.valueView = (IndicateWidget) convertView
				.findViewById(R.id.value);
		holder.unionView = (TextView) convertView.findViewById(R.id.union);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		Indicate indicate = (Indicate) itemObject;
		holder.titleView.setText(indicate.getName());
		if (indicate.getUpdate_time() == 0) {
			holder.contentView
					.setText(Html.fromHtml("最近测量:<font color=\"#770000\">"
							+ DateUtil
									.parseToString(systime, DateUtil.yyyyMMdd)));
		} else {
			holder.contentView.setText(Html
					.fromHtml("最近测量:<font color=\"#770000\">"
							+ DateUtil.parseToString(indicate.getUpdate_time(),
									DateUtil.yyyyMMdd)));
		}
		holder.unionView.setText(indicate.getUnion());
		if ("openPress".equals(indicate.getKey())) {
			if (indicate.getValue1() == null) {
				holder.valueView.setValue((int) indicate.getLast_value(),
						format.format(0), indicate.getLevel(),
						indicate.getUp_down());
			} else {
				holder.valueView.setValue((int) indicate.getLast_value(),
						format.format(indicate.getValue1()),
						indicate.getLevel(), indicate.getUp_down());
			}
		} else {
			holder.valueView.setValue((int) indicate.getLast_value(),
					indicate.getLevel(), indicate.getUp_down());
		}
		holder.iconView.setImageResource(indicate.getImg());
	}

	class ViewHolder {
		ImageView iconView;
		TextView titleView;
		TextView contentView;
		IndicateWidget valueView;
		TextView unionView;
	}

}
