package com.dian.diabetes.activity.tool.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.dto.GroupDto;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ToolGridAdapter extends MBaseAdapter {

	private DisplayImageOptions options;

	public ToolGridAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_tool_gridview);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.gray_breakfast)
				.showImageForEmptyUri(R.drawable.gray_breakfast)
				.showImageOnLoading(R.drawable.gray_breakfast)
				.showImageOnFail(R.drawable.gray_breakfast).cacheInMemory(true)
				.cacheOnDisc(true).build();
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.icon = (ImageView) convertView.findViewById(R.id.tool_icon);
		holder.nameView = (TextView) convertView.findViewById(R.id.tool_name);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		GroupDto normal = (GroupDto) itemObject;
		holder.nameView.setText(normal.name);
		holder.icon.setImageResource(R.drawable.gray_breakfast);
		ImageLoader.getInstance().displayImage(normal.icon, holder.icon,
				options);
	}

	class ViewHolder {
		ImageView icon;
		TextView nameView;
	}

}
