package com.dian.diabetes.activity.eat.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.db.dao.Eat;
import com.dian.diabetes.tool.CommonUtil;
import com.dian.diabetes.utils.DateUtil;

/**
 * 饮食首页  运动 饮食  分栏里边的adapter
 * @author longbh
 *
 */
public class EatAdapter extends MBaseAdapter {
	
	private DecimalFormat format;

	public EatAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_eat_view);
		format = new DecimalFormat("##0.0");
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.eatTime = (TextView) convertView.findViewById(R.id.time);
		holder.eatType = (TextView) convertView.findViewById(R.id.eat_type);
		holder.foodName = (TextView) convertView.findViewById(R.id.food_name);
		holder.totalNum = (TextView) convertView.findViewById(R.id.total_num);
		holder.totalCalore = (TextView) convertView.findViewById(R.id.total);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		Eat eat = (Eat) itemObject;
		holder.eatTime.setText(DateUtil.parseToString(eat.getCreate_time(), DateUtil.HHmm));
		holder.totalCalore.setText("+" + format.format(eat.getTotal()));
		holder.foodName.setText(eat.getFoodName());
		holder.totalNum.setText(eat.getFoodWeight()+"");
		holder.eatType.setText(CommonUtil.getValue("eat" + eat.getDinnerType()));
	}
	
	class ViewHolder{
		TextView eatTime;
		TextView eatType;
		TextView totalCalore;
		TextView foodName;
		TextView totalNum;
	}

}
