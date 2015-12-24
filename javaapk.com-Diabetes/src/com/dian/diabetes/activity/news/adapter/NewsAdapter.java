package com.dian.diabetes.activity.news.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.SBaseAdapter;
import com.dian.diabetes.db.dao.News;
import com.dian.diabetes.utils.DateUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NewsAdapter extends SBaseAdapter {

	private DisplayImageOptions options;

	public NewsAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_news_layout);
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
		holder.one = convertView.findViewById(R.id.one);
		holder.second = convertView.findViewById(R.id.second);
		holder.oneIcon = (ImageView) convertView.findViewById(R.id.one_icon);
		holder.oneTitle = (TextView) convertView.findViewById(R.id.one_title);
		holder.iconView = (ImageView) convertView.findViewById(R.id.icon);
		holder.titleView = (TextView) convertView.findViewById(R.id.news_title);
		holder.contentView = (TextView) convertView
				.findViewById(R.id.news_content);
		holder.timeView = (TextView) convertView.findViewById(R.id.time);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject, int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		News news = (News) itemObject;
		if (position == 0) {
			holder.one.setVisibility(View.VISIBLE);
			holder.second.setVisibility(View.GONE);
			holder.oneTitle.setText(news.getTitle());
			ImageLoader.getInstance().displayImage(news.getThumbnail(), holder.oneIcon, options);
		} else {
			holder.one.setVisibility(View.GONE);
			holder.second.setVisibility(View.VISIBLE);
			holder.titleView.setText(news.getTitle());
			holder.contentView.setText(news.getSummary());
			holder.timeView.setText(DateUtil.parseToDate(news.getDay()));
			ImageLoader.getInstance().displayImage(news.getThumbnail(), holder.iconView, options);
		}
	}

	class ViewHolder {
		ImageView oneIcon;
		TextView oneTitle;
		ImageView iconView;
		TextView titleView;
		TextView contentView;
		TextView timeView;
		View one;
		View second;
	}

}
