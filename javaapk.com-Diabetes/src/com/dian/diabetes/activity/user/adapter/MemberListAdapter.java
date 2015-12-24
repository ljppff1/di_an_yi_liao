package com.dian.diabetes.activity.user.adapter;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.user.ManageUsersActivity;
import com.dian.diabetes.activity.user.MemberInfoEditFragment;
import com.dian.diabetes.activity.user.MemberInfoFragment;
import com.dian.diabetes.db.dao.UserInfoDao;
import com.dian.diabetes.dialog.AlertDialog;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.widget.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * 用户列表的cursorAdapter，主要用来显示用户的图片
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月24日
 * 
 */
public class MemberListAdapter extends SimpleCursorAdapter {

	private Context context;
	private Cursor cursor;
	private DisplayImageOptions options;
	private Preference preference;

	@SuppressLint("NewApi")
	public MemberListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		this.context = context;
		this.cursor = cursor;
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.photo_default)
				.showImageForEmptyUri(R.drawable.photo_default)
				.showImageOnLoading(R.drawable.photo_default)
				.showImageOnFail(R.drawable.photo_default).cacheInMemory(true)
				.cacheOnDisc(true).build();
		this.preference = new Preference(context);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void bindView(View view, final Context context, Cursor cursor) {
		super.bindView(view, context, cursor);
		CircleImageView photo = (CircleImageView) view.findViewById(R.id.memberlist_avator);
		String avatar = cursor.getString(UserInfoDao.Properties.Avatar.ordinal);
		if (avatar != null) {
			ImageLoader.getInstance().displayImage(avatar, photo, options);
		} else {
			photo.setImageResource(R.drawable.photo_default);
		}

		TextView edit = (TextView) view.findViewById(R.id.member_item_edit);
		final TextView mid = (TextView) view.findViewById(R.id.memberlist_mid);
		final TextView nickname = (TextView) view
				.findViewById(R.id.members_item_name);
		if (Long.parseLong(mid.getText().toString()) == preference
				.getLong(Preference.USER_ID)) {
			nickname.setTextColor(Color.RED);
		} else {
			nickname.setTextColor(R.color.main_user_name);
		}
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String tag = "member_info";
				boolean isAdd = true;
				BaseFragment tempFragment = (BaseFragment) ((FragmentActivity) context)
						.getSupportFragmentManager().findFragmentByTag(tag);
				if (tempFragment == null) {
					tempFragment = MemberInfoEditFragment.getInstance();
					isAdd = false;
				}
				Bundle data = new Bundle();
				data.putString("mid", mid.getText().toString());
				data.putString("nickname", nickname.getText().toString());
				tempFragment.setArguments(data);
				((ManageUsersActivity) context).replaceFragment(tag,
						tempFragment, isAdd);
			}
		});
	}

}
