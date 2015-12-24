package com.dian.diabetes.activity.date;

import hirondelle.date4j.DateTime;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.SBaseAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 日期天的item adapter
 * @author hua
 *
 */
public class CaldroidGridAdapter extends SBaseAdapter {

	private int curentMonth;
	private Resources resource;
	private Map<String, Integer> valueMap;
	private DecimalFormat format;

	public CaldroidGridAdapter(Context context, List<?> data,
			Map<String, Integer> map) {
		super(context, data, R.layout.item_day_view);
		resource = context.getResources();
		valueMap = map;
		format = new DecimalFormat("00");
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.day = (TextView) convertView.findViewById(R.id.date);
		holder.selector = (ImageView) convertView.findViewById(R.id.date_bg);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject,
			final int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		DateTime dateTime = (DateTime) itemObject;
		holder.day.setText(dateTime.getDay() + "");
		if (curentMonth == dateTime.getMonth()) {
			holder.selector.setVisibility(View.INVISIBLE);
			holder.day.setVisibility(View.VISIBLE);
			Integer level = valueMap.get(dateTime.getYear()
					+"-"+ format.format(dateTime.getMonth())
					+"-"+ format.format(dateTime.getDay()));
			if (level == null) {
				holder.day.setTextColor(resource
						.getColor(R.color.week_color));
			} else if (level < 3) {
				holder.day.setTextColor(resource
						.getColor(R.color.level2));
			} else{
				holder.day.setTextColor(resource
						.getColor(R.color.level3));
			}
		} else {
			holder.selector.setVisibility(View.INVISIBLE);
			holder.day.setVisibility(View.INVISIBLE);
		}

	}

	public void setValueMap(Map<String, Integer> valueMap) {
		this.valueMap = valueMap;
	}

	public void setCurentMonth(int curentMonth) {
		this.curentMonth = curentMonth;
	}

	public DateTime getPosition(int position) {
		return (DateTime) data.get(position);
	}

	class ViewHolder {
		TextView day;
		ImageView selector;
	}

}
