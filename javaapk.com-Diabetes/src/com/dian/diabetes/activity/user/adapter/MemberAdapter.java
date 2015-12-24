package com.dian.diabetes.activity.user.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.activity.SBaseAdapter;
import com.dian.diabetes.activity.user.MemberActivity;
import com.dian.diabetes.db.dao.UserInfo;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.widget.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MemberAdapter extends SBaseAdapter {

	private DisplayImageOptions options;
	private boolean editAble = false;
	private MemberActivity activity;

	public MemberAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_member_layout);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.photo_default)
				.showImageForEmptyUri(R.drawable.photo_default)
				.showImageOnLoading(R.drawable.photo_default)
				.showImageOnFail(R.drawable.photo_default).cacheInMemory(true)
				.cacheOnDisc(true).build();
		activity = (MemberActivity) context;
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.icon = (CircleImageView) convertView.findViewById(R.id.icon);
		holder.title = (TextView) convertView.findViewById(R.id.title);
		holder.content = (TextView) convertView.findViewById(R.id.content);
		holder.check = (ImageView) convertView.findViewById(R.id.is_main);
		holder.delete = (ImageView) convertView.findViewById(R.id.delete);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject,
			final int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		final UserInfo info = (UserInfo) itemObject;
		if (info != null) {
			holder.title.setText(info.getNick_name() + "");
			if (info.getProvince() == null || info.getProvince() == -1) {
				holder.content.setText(info.getAge() + "岁　不限");
			} else {
				holder.content.setText(info.getAge() + "岁　"
						+ activity.getMaps().get(info.getProvince() + "")
						+ activity.getMaps().get(info.getCity() + ""));
			}
			ImageLoader.getInstance().displayImage(info.getAvatar(),
					holder.icon, options);
			if (editAble) {
				holder.delete.setVisibility(View.VISIBLE);
				holder.delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						activity.deleteMember(position, info);
					}
				});
				holder.check.setVisibility(View.GONE);
			} else {
				if (CheckUtil.checkEquels(info.getMid(),
						ContantsUtil.DEFAULT_TEMP_UID)) {
					holder.check.setVisibility(View.VISIBLE);
				} else {
					holder.check.setVisibility(View.GONE);
				}
				holder.delete.setVisibility(View.GONE);
			}
		}
	}

	class ViewHolder {
		CircleImageView icon;
		TextView title;
		TextView content;
		ImageView check;
		ImageView delete;
	}

	public boolean isEditAble() {
		return editAble;
	}

	public void setEditAble(boolean editAble) {
		this.editAble = editAble;
	}

}
