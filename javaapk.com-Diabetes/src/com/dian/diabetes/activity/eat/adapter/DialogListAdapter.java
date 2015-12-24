package com.dian.diabetes.activity.eat.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.activity.sugar.model.MapModel;

public class DialogListAdapter extends MBaseAdapter{

	public DialogListAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_list_dialog);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.modelName = (TextView) convertView.findViewById(R.id.textView);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		MapModel model = (MapModel) itemObject;
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.modelName.setText(model.getValue());
	}

	class ViewHolder {
		TextView modelName;
	}
	
}
