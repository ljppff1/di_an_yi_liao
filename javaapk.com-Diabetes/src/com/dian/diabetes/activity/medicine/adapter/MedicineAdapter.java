package com.dian.diabetes.activity.medicine.adapter;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.db.dao.Medicine;
import com.dian.diabetes.tool.CommonUtil;

public class MedicineAdapter extends MBaseAdapter {
	
	private Calendar calendar;

	public MedicineAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_medicine_layout);
		calendar = Calendar.getInstance();
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.eatDinner = (TextView) convertView.findViewById(R.id.eat_dinner_type);
		holder.medicineEatType = (TextView) convertView.findViewById(R.id.eat_medicine_type);
		holder.medicineName = (TextView) convertView.findViewById(R.id.medicine_name);
		holder.medicineType = (TextView) convertView.findViewById(R.id.medicine_type);
		holder.medicineSub = (TextView) convertView.findViewById(R.id.sub);
		holder.weightLabel = (TextView) convertView.findViewById(R.id.weight_label);
		holder.weightUnion = (TextView) convertView.findViewById(R.id.weight_unit);
		holder.outDate = (TextView) convertView.findViewById(R.id.out_date);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		Medicine medicine = (Medicine) itemObject;
		holder.eatDinner.setText(CommonUtil.getValue("stage" + medicine.getStage()));
		holder.medicineName.setText(medicine.getName());
		holder.medicineSub.setText(medicine.getWeight()+"");
		holder.medicineEatType.setText(CommonUtil.getValue("medicine" + medicine.getNumType()));
		holder.medicineType.setText(medicine.getType());
		holder.weightUnion.setText(medicine.getUnit());
		calendar.setTimeInMillis(medicine.getCreateTime());
		calendar.add(Calendar.DATE, medicine.getEatDay());
		if(calendar.getTimeInMillis() < System.currentTimeMillis()){
			holder.eatDinner.setTextColor(context.getResources().getColor(R.color.un_able));
			holder.medicineName.setTextColor(context.getResources().getColor(R.color.un_able));
			holder.medicineSub.setTextColor(context.getResources().getColor(R.color.un_able));
			holder.medicineEatType.setTextColor(context.getResources().getColor(R.color.un_able));
			holder.medicineType.setTextColor(context.getResources().getColor(R.color.un_able));
			holder.weightLabel.setTextColor(context.getResources().getColor(R.color.un_able));
			holder.weightUnion.setTextColor(context.getResources().getColor(R.color.un_able));
			holder.outDate.setVisibility(View.VISIBLE);
		}else{
			holder.eatDinner.setTextColor(context.getResources().getColor(R.color.radio_text_selector));
			holder.medicineName.setTextColor(context.getResources().getColor(R.color.item_black_selector));
			holder.medicineSub.setTextColor(context.getResources().getColor(R.color.radio_text_selector));
			holder.medicineEatType.setTextColor(context.getResources().getColor(R.color.radio_text_selector));
			holder.medicineType.setTextColor(context.getResources().getColor(R.color.radio_text_selector));
			holder.weightLabel.setTextColor(context.getResources().getColor(R.color.item_black_selector));
			holder.weightUnion.setTextColor(context.getResources().getColor(R.color.item_black_selector));
			holder.outDate.setVisibility(View.GONE);
		}
	}

	class ViewHolder{
		TextView eatDinner;
		TextView medicineEatType;
		TextView medicineName;
		TextView medicineType;
		TextView medicineSub;
		TextView weightLabel;
		TextView weightUnion;
		TextView outDate;
	}
}
