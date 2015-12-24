package com.dian.diabetes.dialog.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.dto.CheckModel;

public class CheckAdapter extends MBaseAdapter{

	public CheckAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_check_layout);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.icon = (ImageView) convertView.findViewById(R.id.check);
		holder.name = (TextView) convertView.findViewById(R.id.name);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		CheckModel model = (CheckModel) itemObject;
		holder.name.setText(model.value);
		if(model.isCheck){
			holder.icon.setImageResource(R.drawable.icon_check);
		}else{
			holder.icon.setImageResource(R.drawable.icon_uncheck);
		}
	}

	class ViewHolder{
		ImageView icon;
		TextView name;
	}
}
