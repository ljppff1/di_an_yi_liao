package com.dian.diabetes.activity.sugar.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;

public class DeviceAdapter extends MBaseAdapter{

	public DeviceAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_device_adapter);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.deviceName = (TextView) convertView.findViewById(R.id.device);
		holder.deviceDetail = (TextView) convertView.findViewById(R.id.device_detail);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		Map<String,String> data = (Map<String, String>) itemObject;
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.deviceName.setText(data.get("name"));
		holder.deviceDetail.setText(data.get("address"));
	}

	class ViewHolder{
		TextView deviceName;
		TextView deviceDetail;
	}
}
