package com.dian.diabetes.activity.assess.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.dto.DataModel;

public class NormalAdapter extends MBaseAdapter{

	public NormalAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_assess_normal);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView) convertView.findViewById(R.id.name);
		holder.check = (ImageView) convertView.findViewById(R.id.check);
		holder.numberCon = (LinearLayout) convertView.findViewById(R.id.number_value);
		holder.value = (TextView) convertView.findViewById(R.id.value);
		holder.union = (TextView) convertView.findViewById(R.id.union);
		holder.keyIndex = (TextView) convertView.findViewById(R.id.key_index);
		holder.keyToast = (TextView) convertView.findViewById(R.id.key_toast);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		DataModel model = (DataModel) itemObject;
		if(model.method_question == 0){
			holder.check.setVisibility(View.VISIBLE);
			holder.numberCon.setVisibility(View.GONE);
			holder.keyIndex.setVisibility(View.GONE);
			holder.keyToast.setVisibility(View.INVISIBLE);
			if(model.isCheck()){
				model.answer = "true";
				holder.check.setImageResource(R.drawable.icon_check);
			}else{
				model.answer = "false";
				holder.check.setImageResource(R.drawable.icon_uncheck);
			}
		}else if(model.method_question == 2){
			holder.check.setVisibility(View.GONE);
			holder.numberCon.setVisibility(View.VISIBLE);
			holder.keyIndex.setVisibility(View.GONE);
			holder.keyToast.setVisibility(View.INVISIBLE);
			holder.value.setText(model.answer);
			holder.union.setText(model.unit);
		}else{
			holder.check.setVisibility(View.GONE);
			holder.numberCon.setVisibility(View.GONE);
			holder.keyIndex.setVisibility(View.VISIBLE);
			holder.keyToast.setVisibility(View.VISIBLE);
			holder.keyIndex.setText(model.answer);
		}
		holder.name.setText(model.question);
	}
	
	public void check(int position){
		DataModel model = (DataModel) data.get(position);
		if(model.isCheck()){
			model.answer = "false";
		}else{
			model.answer = "true";
		}
		notifyDataSetChanged();
	}
	
	class ViewHolder{
		TextView name;
		ImageView check;
		LinearLayout numberCon;
		TextView value;
		TextView union;
		TextView keyIndex;
		TextView keyToast;
	}

}
