package com.dian.diabetes.activity.news.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.SBaseAdapter;
import com.dian.diabetes.activity.news.FavorateFragment;
import com.dian.diabetes.db.dao.News;
import com.dian.diabetes.dialog.AlertDialog;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FavorateAdapter extends SBaseAdapter {

	private DisplayImageOptions options;
	private FavorateFragment fragment;
	private boolean edit = false;
	private AlertDialog alert;

	public FavorateAdapter(Context context, List<?> data,
			FavorateFragment fragment) {
		super(context, data, R.layout.item_news_layout);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.content_bg)
				.showImageForEmptyUri(R.drawable.content_bg)
				.showImageOnLoading(R.drawable.content_bg)
				.showImageOnFail(R.drawable.content_bg).cacheInMemory(true)
				.cacheOnDisc(true).build();
		alert = new AlertDialog(context, "您确定要取消收藏吗");
		this.fragment = fragment;
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
		holder.delete = convertView.findViewById(R.id.delete);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject,
			final int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		final News news = (News) itemObject;
		holder.one.setVisibility(View.GONE);
		holder.second.setVisibility(View.VISIBLE);
		holder.titleView.setText(news.getTitle());
		holder.contentView.setText(news.getSummary());
		holder.timeView.setText(DateUtil.parseToDate(news.getDay()));
		ImageLoader.getInstance().displayImage(news.getThumbnail(),
				holder.iconView, options);
		if (edit) {
			holder.delete.setVisibility(View.VISIBLE);
		} else {
			holder.delete.setVisibility(View.GONE);
		}
		holder.delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				alert.setCallBack(new AlertDialog.CallBack() {

					@Override
					public void cancel() {
					}

					@Override
					public void callBack() {
						fragment.showLoading();
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("postId", news.getSid());
						HttpServiceUtil.request(ContantsUtil.REMOVE_FAVORATE,
								"post", params, new HttpServiceUtil.CallBack() {
									@Override
									public void callback(String json) {
										fragment.hideLoading();
										try {
											JSONObject jsonData = new JSONObject(
													json);
											if (CheckUtil
													.checkStatusOk(jsonData
															.getInt("status"))) {
												data.remove(position);
												notifyDataSetChanged();
											} else {
												ToastTool.showToast(jsonData
														.getInt("status"),
														context);
											}
										} catch (JSONException e) {
											Toast.makeText(context, "数据解析出错",
													Toast.LENGTH_SHORT).show();
											e.printStackTrace();
										}
									}
								});
					}
				});
				alert.show();
			}
		});
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
		View delete;
	}

	public void setEdit(boolean state) {
		this.edit = state;
	}

	public boolean getEdit() {
		return edit;
	}
}
