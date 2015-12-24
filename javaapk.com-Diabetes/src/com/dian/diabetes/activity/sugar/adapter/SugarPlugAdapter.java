package com.dian.diabetes.activity.sugar.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.db.dao.Common;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SugarPlugAdapter extends MBaseAdapter {

	private DisplayImageOptions options;

	public SugarPlugAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_sugar_plug);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.content_bg)
				.showImageForEmptyUri(R.drawable.content_bg)
				.showImageOnLoading(R.drawable.content_bg)
				.showImageOnFail(R.drawable.content_bg).cacheInMemory(true)
				.cacheOnDisc(true).build();
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.icon = (ImageView) convertView.findViewById(R.id.icon);
		holder.name = (TextView) convertView.findViewById(R.id.name);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		Common common = (Common) itemObject;
		holder.name.setText(common.getName());
		if (common.isSelect()) {
			ImageLoader.getInstance().displayImage(common.getImage(), holder.icon, options);
//			fetcher.loadImage(common.getImage(), holder.icon);
			holder.name.setTextColor(context.getResources().getColor(
					R.color.contents_text));
		} else {
			ImageLoader.getInstance().displayImage(common.getImage_b(), holder.icon, options);
			//fetcher.loadImage(common.getImage_b(), holder.icon);
			holder.name.setTextColor(context.getResources().getColor(
					R.color.line_target));
		}
	}

	class ViewHolder {
		ImageView icon;
		TextView name;
	}
}
