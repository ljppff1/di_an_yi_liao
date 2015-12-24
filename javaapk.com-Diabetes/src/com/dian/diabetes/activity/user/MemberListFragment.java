package com.dian.diabetes.activity.user;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.user.adapter.MemberListAdapter;
import com.dian.diabetes.db.AlarmBo;
import com.dian.diabetes.db.DbApplication;
import com.dian.diabetes.db.UserBo;
import com.dian.diabetes.db.UserInfoBo;
import com.dian.diabetes.db.dao.User;
import com.dian.diabetes.db.dao.UserInfo;
import com.dian.diabetes.db.dao.UserInfoDao;
import com.dian.diabetes.dialog.ListDialog;
import com.dian.diabetes.request.HttpContants;
import com.dian.diabetes.request.UserDataAPI;
import com.dian.diabetes.service.LoadingService;
import com.dian.diabetes.service.UpdateService;
import com.dian.diabetes.service.UserService;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.utils.HttpServiceUtil.CallBack;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 类/接口注释
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月8日
 * 
 */
public class MemberListFragment extends BaseFragment implements
		OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.member_listview)
	private ListView memberListview;
	@ViewInject(id = R.id.addmember_btn)
	private Button addMemberBtn;

	private ManageUsersActivity activity;

	private CallBack getListCallBack, setMasterCallBack, delCallBack;

	private MemberListAdapter cursorAdapter;
	private Cursor cursor;
	private UserBo userBo;
	private UserInfoBo userInfoBo;
	private Dialog dialog;
	protected Long listId;// item长按返回的id
	protected long userMid;
	protected String nickName;

	public static MemberListFragment getInstance() {
		return new MemberListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userBo = new UserBo(activity);
		activity = (ManageUsersActivity) getActivity();
		// 初始化加载图片的尺寸
		initCallBack();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_memberlist, container,
				false);
		fieldView(view);
		init();
		return view;
	}

	@SuppressLint("NewApi")
	private void init() {
		userInfoBo = new UserInfoBo(activity);
		getUserlist();
		cursorAdapter = new MemberListAdapter(getActivity(),
				R.layout.item_member_list, getData(), new String[] {
						"NICK_NAME", "MID" }, new int[] {
						R.id.members_item_name, R.id.memberlist_mid }, 0);
		memberListview.setAdapter(cursorAdapter);

		backBtn.setOnClickListener(this);
		addMemberBtn.setOnClickListener(this);

		memberListview
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						TextView midTextview = (TextView) view
								.findViewById(R.id.memberlist_mid);
						TextView nameTextview = (TextView) view
								.findViewById(R.id.members_item_name);
						final String mid = midTextview.getText().toString();
						final String name = nameTextview.getText().toString();
						listId = id;
						nickName = name;
						showMenuDialog(mid);
						return true;

					}

				});

	}

	private void showMenuDialog(final String mid) {
		dialog = new ListDialog(activity);
		dialog.show();

		Window window = dialog.getWindow();
		window.setContentView(R.layout.dialog_three_menu);
		TextView setMasterText = (TextView) window
				.findViewById(R.id.dialog_first_menu);
		setMasterText.setText(R.string.set_master);
		TextView delText = (TextView) window
				.findViewById(R.id.dialog_second_menu);
		delText.setText(R.string.delete_members);
		TextView cancelText = (TextView) window
				.findViewById(R.id.dialog_third_menu);
		cancelText.setText(R.string.cancel_str);
		// 如果item是主成员的话，不允许再设置为主成员和删除该成员
		if (!ContantsUtil.DEFAULT_TEMP_UID.equals(mid)) {
			setMasterText.setTextColor(getResources().getColor(
					R.color.page_title_color));
			setMasterText.setClickable(true);
			delText.setTextColor(getResources().getColor(
					R.color.page_title_color));
			delText.setClickable(true);
			setMasterText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setMaster(Long.parseLong(mid));
					dialog.dismiss();
				}
			});
			delText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					delMember(mid);
					dialog.dismiss();
				}
			});
		} else {
			setMasterText.setTextColor(getResources().getColor(
					R.color.main_user_name));
			setMasterText.setClickable(false);
			delText.setTextColor(getResources()
					.getColor(R.color.main_user_name));
			delText.setClickable(false);
		}
		cancelText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			BaseFragment tempFragment;
			boolean isAdd = true;
			String tag;
			tag = "manager_users";
			isAdd = true;
			tempFragment = (BaseFragment) context.getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (tempFragment == null) {
				tempFragment = ManagerUsersFragment.getInstance();
				isAdd = false;
			}
			activity.replaceFragment(tag, tempFragment, isAdd);
			break;
		case R.id.addmember_btn:
			addMemer();
			break;
		}
	}

	/**
	 * 添加成员
	 */
	private void addMemer() {
		BaseFragment tempFragment;
		boolean isAdd = true;
		String tag;
		tag = "add_member";
		isAdd = true;
		tempFragment = (BaseFragment) context.getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (tempFragment == null) {
			// tempFragment = MemberAddFragment.getInstance();
			isAdd = false;
		}
		activity.replaceFragment(tag, tempFragment, isAdd);

	}

	/**
	 * 删除成员
	 * 
	 * @param 用户id
	 */
	private void delMember(String mid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mid", String.valueOf(mid));
		activity.getLoading().setDialogLabel(
				getText(R.string.deleting_user).toString());
		activity.showLoading();
		UserDataAPI.delMember(map, activity, delCallBack);
	}

	private void setMaster(long mid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mid", String.valueOf(mid));
		userMid = mid;
		activity.getLoading().setDialogLabel(
				getText(R.string.setting_master).toString());
		activity.showLoading();
		UserDataAPI.setMaster(map, activity, setMasterCallBack);
	}

	private void getUserlist() {
		UserDataAPI.getlist(null, activity, getListCallBack);
	}

	private void initCallBack() {
		getListCallBack = new CallBack() {

			@Override
			public void callback(String json) {
				try {
					JSONObject object = new JSONObject(json);
					int status = object.getInt("status");

					if (status == HttpContants.REQUEST_SUCCESS) {
						JSONArray array = object.getJSONArray("data");
						for (int i = 0; i < array.length(); i++) {
							String item = array.getString(i);
							UserInfo userInfo = new UserInfo();
							UserInfo.transformToUserInfo(userInfo, item);
							// 数据库里面有没有当前用户，没有就保存在数据库，有就更新信息
							UserInfo temp = userInfoBo.getUinfoByMid(userInfo
									.getMid());
							if (temp == null) {
								userInfoBo.saveUserInfo(userInfo);
							} else {
								userInfo.setId(temp.getId());
								userInfo.setUid(temp.getUid());
								userInfoBo.updateUserInfo(userInfo);
							}
						}
						// 更新视图
						new ChangeCursor().execute();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};

		setMasterCallBack = new CallBack() {

			@Override
			public void callback(String json) {
				try {
					JSONObject object = new JSONObject(json);
					int status = object.getInt("status");
					if (status == HttpContants.REQUEST_SUCCESS) {
						// mid
						activity.getPrefernce().putLong(Preference.USER_ID,
								userMid);
						ContantsUtil.DEFAULT_TEMP_UID = userMid + "";

						// 更新到user表中，存在修改，不存在插入
						User user = new User();
						User temp = ContantsUtil.curUser;
						// ContantsUtil.curUser中的phone、uid跟切换的成员是一样的，所以直接在里面取
						user.setPhone(temp.getPhone());
						user.setService_uid(temp.getService_uid());

						user.setNick_name(nickName);
						user.setService_mid(String.valueOf(userMid));

						UserService uService = new UserService();
						ContantsUtil.curUser = userBo.saveUserServer(uService
								.convertUserTo(user));
						// 用户uid和sessionId不变

						Config.startUpdate();
						// 同步数据，包括更新家庭成员列表
						MyThread myThread = new MyThread();
						new Thread(myThread).start();

					} else {
						ToastTool.showToast(status, activity);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		delCallBack = new CallBack() {

			@Override
			public void callback(String json) {
				activity.hideLoading();
				try {
					JSONObject obejct = new JSONObject(json);
					int status = obejct.getInt("status");
					if (status == HttpContants.REQUEST_SUCCESS) {
						// 从数据库删除
						userInfoBo.getUserInfoDao().deleteByKey(listId);
						// 更新视图
						new ChangeCursor().execute();
						ToastTool.showMemberStatus(
								HttpContants.DEL_USER_SUCCESS, activity);
					} else {
						ToastTool.showToast(status, activity);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				activity.getLoading().setDialogLabel("正在合并用户设置数据");
				break;
			case 2:
				activity.getLoading().setDialogLabel("正在合并血糖数据");
				break;
			case 3:
				activity.getLoading().setDialogLabel("数据同步完成");
				// 启动当前用户闹钟系统
				new AlarmBo(activity).setNextAlarm(
						ContantsUtil.DEFAULT_TEMP_UID,
						ContantsUtil.curUser.getService_uid());
				activity.hideLoading();
				// 更新成员列表视图
				new ChangeCursor().execute();
				ToastTool.showMemberStatus(HttpContants.SET_MASTER_SUCCESS,
						activity);
				break;
			case 4:
				activity.getLoading().setDialogLabel("正在同步本地用户设置数据");
				break;
			case 5:
				activity.getLoading().setDialogLabel("正在同步本地血糖数据");
				break;
			case 6:
				activity.getLoading().setDialogLabel("正在同步本地监测计划数据");
				break;
			case 7:
				activity.getLoading().setDialogLabel("正在系统数据");
				break;
			}
		}
	};

	/**
	 * 同步服务端数据
	 */
	public class MyThread implements Runnable {

		public void run() {
			// 如果登陆之前未联网先同步系统参数
			if (!ContantsUtil.isSycnSystem) {
				handler.sendEmptyMessage(7);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(
						"timestamp",
						activity.getPrefernce().getLong(
								Preference.SYS_UPDATE_TIME));
				String result = HttpServiceUtil.post(ContantsUtil.PRED_UPDATE,
						params);
				LoadingService.sycnData(activity.getPrefernce(), context,
						result);
				ContantsUtil.isSycnSystem = true;
			}
			// 判断是否有未同步数据
			Config.loadUserSet(activity);
			UpdateService uService = new UpdateService(context, userBo);
			if (activity.getPrefernce().getBoolean(Preference.HAS_UPDATE)) {
				handler.sendEmptyMessage(4);
				uService.updateUserProperty();
				handler.sendEmptyMessage(5);
				uService.updateDiabetes();
				handler.sendEmptyMessage(6);
				uService.updateAlarm();
				Config.stopUpdate();
				activity.getPrefernce()
						.putBoolean(Preference.HAS_UPDATE, false);
			}
			// load用户自定义设置
			LoadingService service = new LoadingService(context, userBo);
			handler.sendEmptyMessage(1);
			service.loadingUserSet();
			handler.sendEmptyMessage(2);
			service.loadingDbtData();
			handler.sendEmptyMessage(3);
		}
	}

	@Override
	public void onStop() {
		if (cursor != null) {
			cursor.close();
		}
		super.onStop();

	}

	/**
	 * 获取cursor
	 * 
	 * @return 返回cursor对象
	 */
	private Cursor getData() {
		userInfoBo = new UserInfoBo(getActivity());
		cursor = DbApplication.getSQLiteDatabase(getActivity()).query(
				UserInfoDao.TABLENAME,
				userInfoBo.getUserInfoDao().getAllColumns(), "UID=?",
				new String[] { String.valueOf(getUid()) }, null, null, null);
		return cursor;
	}

	public boolean onBackKeyPressed() {
		return true;
	}

	/**
	 * 一般使用cursor.requery()来刷新listview。但是，
	 * cursor.requery()已经过时，需要重新获取一个新的cursor来替换原来的cursor。
	 * 
	 * @author Chanjc@ifenguo.com
	 * @createDate 2014年7月9日
	 * 
	 */
	private class ChangeCursor extends AsyncTask<Void, Void, Cursor> {

		SQLiteDatabase db;

		public ChangeCursor() {
			super();
			db = DbApplication.getSQLiteDatabase(getActivity());
		}

		// 步骤1.1：在后台线程中从数据库读取，返回新的游标newCursor
		protected Cursor doInBackground(Void... params) {
			Cursor newCursor = db
					.query(UserInfoDao.TABLENAME, userInfoBo.getUserInfoDao()
							.getAllColumns(), "UID=?", new String[] { String
							.valueOf(getUid()) }, null, null, null);
			return newCursor;
		}

		// 步骤1.2：线程最后执行步骤，更换adapter的游标，并奖原游标关闭，释放资源
		protected void onPostExecute(Cursor newCursor) {
			// 网上看到很多问如何更新ListView的信息，采用CusorApater其实很简单，换cursor就可以
			cursorAdapter.changeCursor(newCursor);
			cursor = newCursor;
		}
	}

	private Long getUid() {
		Long uid = activity.getPrefernce().getLong(ContantsUtil.USER_ID);
		return uid;
	}
}
