package com.dian.diabetes.activity.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.DialogLoading;
import com.dian.diabetes.activity.TranLoading;
import com.dian.diabetes.activity.user.adapter.MemberAdapter;
import com.dian.diabetes.db.AlarmBo;
import com.dian.diabetes.db.UserBo;
import com.dian.diabetes.db.UserInfoBo;
import com.dian.diabetes.db.dao.User;
import com.dian.diabetes.db.dao.UserInfo;
import com.dian.diabetes.dialog.AlertDialog;
import com.dian.diabetes.request.HttpContants;
import com.dian.diabetes.request.UserDataAPI;
import com.dian.diabetes.service.IndicateService;
import com.dian.diabetes.service.LoadingService;
import com.dian.diabetes.service.UpdateService;
import com.dian.diabetes.service.UserService;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.tool.ReadAreaFile;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 类/接口注释
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月8日
 * 
 */
public class MemberActivity extends BasicActivity implements OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.member_listview)
	private ListView memberListview;
	@ViewInject(id = R.id.addmember_btn)
	private Button addMemberBtn;
	@ViewInject(id = R.id.save_btn)
	private ImageButton saveBtn;

	private Button addMember;

	private List<UserInfo> data;
	private MemberAdapter adapter;
	private UserInfoBo userInfoBo;
	private UserBo userBo;
	private Preference preference;

	private TranLoading loading;
	private DialogLoading loadingDialog;
	private AlertDialog alert;
	private Map<String, String> maps;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_memberlist);
		userInfoBo = new UserInfoBo(context);
		userBo = new UserBo(context);
		preference = Preference.instance(context);
		// 初始化加载图片的尺寸
		ContantsUtil.ADD_MEMBER = true;
		loading = new TranLoading(context);
		alert = new AlertDialog(context, "检测到您的个人信息不完整，是否去完善您的个人信息？");
		alert.setCallBack(new AlertDialog.CallBack() {
			@Override
			public void cancel() {
			}

			@Override
			public void callBack() {
				Bundle bundle = new Bundle();
				bundle.putLong("mid",
						StringUtil.toLong(ContantsUtil.DEFAULT_TEMP_UID));
				bundle.putBoolean("isedit", true);
				startActivity(bundle, ManageUsersActivity.class);
			}
		});
		loadingDialog = new DialogLoading(context);
		maps = new ReadAreaFile(context).readArea();
		initActivity();
	}

	public void onResume() {
		super.onResume();
		if (!ContantsUtil.ADD_MEMBER) {
			loadUser();
			ContantsUtil.ADD_MEMBER = true;
		}
	}

	private void initActivity() {
		addMemberBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);

		data = userInfoBo.list(ContantsUtil.curUser.getService_uid());
		adapter = new MemberAdapter(context, data);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.item_member_bottom, null);
		addMember = (Button) view.findViewById(R.id.new_member);
		addMember.setOnClickListener(this);
		memberListview.addFooterView(view);
		memberListview.setAdapter(adapter);
		memberListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				UserInfo info = data.get(position);
				if (adapter.isEditAble()) {
					// 跳到编辑界面
					Bundle data = new Bundle();
					data.putLong("mid", info.getMid());
					data.putString("nickname", info.getNick_name());
					data.putBoolean("isedit", true);
					startActivity(data, ManageUsersActivity.class);
				} else {
					// 选择主成员
					if (!CheckUtil.checkEquels(info.getId(),
							ContantsUtil.DEFAULT_TEMP_UID)) {
						setMainUser(info);
					}
				}
			}
		});
		loadUser();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			if (adapter.isEditAble()) {
				adapter.setEditAble(false);
				adapter.notifyDataSetChanged();
			} else {
				finish();
			}
			break;
		case R.id.addmember_btn:
			adapter.setEditAble(!adapter.isEditAble());
			adapter.notifyDataSetChanged();
			saveBtn.setVisibility(View.VISIBLE);
			addMemberBtn.setVisibility(View.GONE);
			break;
		case R.id.save_btn:
			adapter.setEditAble(!adapter.isEditAble());
			adapter.notifyDataSetChanged();
			saveBtn.setVisibility(View.GONE);
			addMemberBtn.setVisibility(View.VISIBLE);
			break;
		case R.id.new_member:
			startActivity(null, MemberAddFragment.class);
			break;
		}
	}

	// 加载用户成员
	private void loadUser() {
		loading.show();
		Map<String, Object> params = new HashMap<String, Object>();
		HttpServiceUtil.request(ContantsUtil.URL_MEMBER_LIST, "post", params,
				new HttpServiceUtil.CallBack() {
					@Override
					public void callback(String json) {
						loading.dismiss();
						try {
							JSONObject object = new JSONObject(json);
							int status = object.getInt("status");
							if (status == HttpContants.REQUEST_SUCCESS) {
								data.clear();
								JSONArray array = object.getJSONArray("data");
								for (int i = 0; i < array.length(); i++) {
									String item = array.getString(i);
									UserInfo userInfo = new UserInfo();
									UserInfo.transformToUserInfo(userInfo, item);
									// 数据库里面有没有当前用户，没有就保存在数据库，有就更新信息
									UserInfo temp = userInfoBo
											.getUinfoByMid(userInfo.getMid());
									if (temp == null) {
										userInfoBo.saveUserInfo(userInfo);
									} else {
										userInfo.setId(temp.getId());
										userInfo.setUid(temp.getUid());
										userInfoBo.updateUserInfo(userInfo);
									}
									data.add(userInfo);
								}
								adapter.notifyDataSetChanged();
							} else {
								ToastTool.showToast(status, context);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(context, "数据解析出错",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	private void setMainUser(final UserInfo info) {
		loadingDialog.show();
		loadingDialog.setDialogLabel("正在切换用户主成员");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mid", info.getMid());
		HttpServiceUtil.request(
				ContantsUtil.HOST + HttpContants.URL_SET_MASTER,
				HttpContants.REQUEST_MOTHOD, map,
				new HttpServiceUtil.CallBack() {
					@Override
					public void callback(String json) {
						loadingDialog.dismiss();
						try {
							JSONObject object = new JSONObject(json);
							int status = object.getInt("status");
							if (status == HttpContants.REQUEST_SUCCESS) {
								// mid
								preference.putLong(Preference.USER_ID,
										info.getMid());
								ContantsUtil.DEFAULT_TEMP_UID = info.getMid()
										+ "";
								ContantsUtil.curInfo = info;
								ContantsUtil.MAIN_UPDATE = false;

								// 更新到user表中，存在修改，不存在插入
								User user = new User();
								User temp = ContantsUtil.curUser;
								// ContantsUtil.curUser中的phone、uid跟切换的成员是一样的，所以直接在里面取
								user.setPhone(temp.getPhone());
								user.setService_uid(temp.getService_uid());

								user.setNick_name(info.getNick_name());
								user.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);

								UserService uService = new UserService();
								ContantsUtil.curUser = new UserBo(context)
										.saveUserServer(uService
												.convertUserTo(user));
								// 用户uid和sessionId不变

								Config.startUpdate();
								// 同步数据，包括更新家庭成员列表
								loadingDialog.show();
								MyThread myThread = new MyThread();
								new Thread(myThread).start();
							} else {
								ToastTool.showToast(status, context);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	public void deleteMember(final int position, final UserInfo info) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mid", info.getMid());
		setDialogLabel(getText(R.string.deleting_user).toString());
		loading.show();
		UserDataAPI.delMember(map, context, new HttpServiceUtil.CallBack() {
			@Override
			public void callback(String json) {
				loading.hide();
				try {
					JSONObject obejct = new JSONObject(json);
					int status = obejct.getInt("status");
					if (status == HttpContants.REQUEST_SUCCESS) {
						// 从数据库删除
						data.remove(position);
						userInfoBo.deleteUserInfo(info);
						adapter.notifyDataSetChanged();
						ToastTool.showMemberStatus(
								HttpContants.DEL_USER_SUCCESS, context);
					} else {
						ToastTool.showToast(status, context);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				setDialogLabel("正在合并用户设置数据");
				break;
			case 2:
				setDialogLabel("正在合并血糖数据");
				break;
			case 3:
				setDialogLabel("数据同步完成");
				// 启动当前用户闹钟系统
				new AlarmBo(context).setNextAlarm(
						ContantsUtil.DEFAULT_TEMP_UID,
						ContantsUtil.curUser.getService_uid());
				loadingDialog.dismiss();
				ContantsUtil.MAIN_UPDATE = false;
				// 更新成员列表视图
				adapter.notifyDataSetChanged();
				if (CheckUtil.isNull(ContantsUtil.curInfo.getSex())
						|| ContantsUtil.curInfo.getSex() == -1) {
					alert.show();
				}
				break;
			case 4:
				setDialogLabel("正在同步本地用户设置数据");
				break;
			case 5:
				setDialogLabel("正在同步本地血糖数据");
				break;
			case 6:
				setDialogLabel("正在同步本地监测计划数据");
				break;
			case 7:
				setDialogLabel("正在系统数据");
				break;
			case 8:
				setDialogLabel("正在饮食数据");
				break;
			case 9:
				setDialogLabel("正在运动数据");
				break;
			case 10:
				setDialogLabel("正在用药数据");
				break;
			case 11:
				setDialogLabel("正在同步本地饮食数据");
				break;
			case 12:
				setDialogLabel("正在同步本地运动数据");
				break;
			case 13:
				setDialogLabel("正在同步本地用药数据");
				break;
			case 16:
				setDialogLabel("同步用户信息成功");
				break;
			case 17:
				loading.dismiss();
				Toast.makeText(context, "同步用户信息失败，请稍后再试", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	private void setDialogLabel(String label) {
		if (loadingDialog == null) {
			loadingDialog = new DialogLoading(context);
		}
		loadingDialog.setDialogLabel(label);
	}

	/**
	 * 同步服务端数据
	 */
	public class MyThread implements Runnable {

		public void run() {
			IndicateService.initIndicate(getApplicationContext(),
					ContantsUtil.DEFAULT_TEMP_UID);
			// 从服务器获取数据更新
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mid", ContantsUtil.DEFAULT_TEMP_UID);
			String result = HttpServiceUtil.post(ContantsUtil.HOST
					+ HttpContants.URL_MEMBER_GETONE, map);
			if (!CheckUtil.isNull(result)) {
				boolean state = loadUserInfo(result);
				if (!state) {
					handler.sendEmptyMessage(17);
					return;
				} else {
					handler.sendEmptyMessage(16);
				}
			}
			// 如果登陆之前未联网先同步系统参数
			if (!ContantsUtil.isSycnSystem) {
				handler.sendEmptyMessage(7);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("timestamp",
						preference.getLong(Preference.SYS_UPDATE_TIME));
				result = HttpServiceUtil.post(ContantsUtil.PRED_UPDATE, params);
				LoadingService.sycnData(preference, context, result);
				ContantsUtil.isSycnSystem = true;
			}
			// 判断是否有未同步数据
			UpdateService uService = new UpdateService(context, userBo);
			if (preference.getBoolean(Preference.HAS_UPDATE)) {
				handler.sendEmptyMessage(4);
				uService.updateUserProperty();
				handler.sendEmptyMessage(5);
				uService.updateDiabetes();
				handler.sendEmptyMessage(6);
				uService.updateAlarm();
				handler.sendEmptyMessage(11);
				uService.updateEat();
				handler.sendEmptyMessage(12);
				uService.updateSport();
				handler.sendEmptyMessage(13);
				uService.updateMedicine();
				handler.sendEmptyMessage(15);
				uService.updateIndicates();
				preference.putBoolean(Preference.HAS_UPDATE, false);
				Config.stopUpdate();
			}
			// load用户自定义设置
			LoadingService service = new LoadingService(context, userBo);
			handler.sendEmptyMessage(1);
			service.loadingUserSet();
			handler.sendEmptyMessage(2);
			service.loadingDbtData();
			handler.sendEmptyMessage(3);
			service.loadingEatData();
			handler.sendEmptyMessage(8);
			service.loadingSportData();
			handler.sendEmptyMessage(9);
			service.loadingMedicineData();
			handler.sendEmptyMessage(10);
			service.loadingIndicateData();
			handler.sendEmptyMessage(14);
			// 刷新配置
			Config.loadUserSet(context);
		}
	}

	private boolean loadUserInfo(String json) {
		JSONObject data;
		try {
			data = new JSONObject(json);
			int status = data.getInt("status");
			if (CheckUtil.checkStatusOk(status)) {
				UserInfo userInfo = new UserInfo();
				UserInfo.transformToUserInfo(userInfo, data.getString("data"));
				// 数据库里面有没有当前用户，没有就保存在数据库，有就更新信息
				UserInfo temp = userInfoBo.getUinfoByMid(userInfo.getMid());
				if (temp == null) {
					userInfoBo.saveUserInfo(userInfo);
				} else {
					userInfo.setId(temp.getId());
					userInfo.setUid(temp.getUid());
					userInfoBo.updateUserInfo(userInfo);
				}
				ContantsUtil.curInfo = userInfo;
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Map<String, String> getMaps() {
		return maps;
	}

}
