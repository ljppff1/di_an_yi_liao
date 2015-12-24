package com.dian.diabetes.activity.assess.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.dto.ItemModel;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;

public class StateAdapter extends MBaseAdapter{
	
	private long curTime;

	public StateAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_asses_state);
		this.curTime = System.currentTimeMillis();
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView) convertView.findViewById(R.id.name);
		holder.resultState = (TextView) convertView.findViewById(R.id.result_state);
		holder.result = (TextView) convertView.findViewById(R.id.result);
		holder.date = (TextView) convertView.findViewById(R.id.date);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		ItemModel model = (ItemModel) itemObject;
		holder.name.setText(model.page_name);
		if(model.is_write){
			long updateTime = model.update_time + model.days_refresh*ContantsUtil.deltaDay;
			Log.d("update", updateTime+"");
			if(updateTime > curTime){
				holder.result.setText("已完成");
				holder.result.setTextColor(context.getResources().getColor(R.color.trend_down_normal));
			}else{
				holder.result.setText("需更新");
				holder.result.setTextColor(context.getResources().getColor(R.color.page_title_color));
			}
			holder.date.setText(Html.fromHtml("最后完成日期：<font color='#0072BB'>" + DateUtil.parseToDate(updateTime) + "</font>"));
			holder.resultState.setVisibility(View.GONE);
		}else{
			holder.result.setText("未完成");
			holder.result.setTextColor(context.getResources().getColor(R.color.trend_up_bad));
			holder.resultState.setVisibility(View.VISIBLE);
			holder.date.setText("");
		}
	}

	class ViewHolder{
		TextView name;
		TextView resultState;
		TextView result;
		TextView date;
	}
}
