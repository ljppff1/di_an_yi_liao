package com.dian.diabetes.activity.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.user.AreaDialogFragment.OnListDialogItemSelect;
import com.dian.diabetes.db.UserInfoBo;
import com.dian.diabetes.db.dao.UserInfo;
import com.dian.diabetes.dialog.ListDialog;
import com.dian.diabetes.request.HttpContants;
import com.dian.diabetes.request.UserDataAPI;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.tool.ReadAreaFile;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ClippingPicture;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.SdCardUtil;
import com.dian.diabetes.utils.HttpServiceUtil.CallBack;
import com.dian.diabetes.utils.SelectPicUtil;
import com.dian.diabetes.widget.anotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 类/接口注释
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月11日
 * 
 */
public class MemberInfoEditFragment extends BaseFragment implements
		OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.memberinfo_donebtn)
	private Button doneBtn;
	@ViewInject(id = R.id.memberinfo_nickname)
	private EditText nickNameTextView;
	@ViewInject(id = R.id.memberinfo_age)
	private EditText ageTextView;
	@ViewInject(id = R.id.memberinfo_gender)
	private EditText genderTextView;
	@ViewInject(id = R.id.memberinfo_nation)
	private EditText nationTextView;
	@ViewInject(id = R.id.memberinfo_area)
	private EditText areaTextView;
	@ViewInject(id = R.id.memberinfo_city)
	private EditText cityTextView;
	@ViewInject(id = R.id.photo)
	private ImageView photo;
	@ViewInject(id = R.id.maxcard)
	private ImageView maxcard;
	@ViewInject(id = R.id.memberinfo_nickname_rlayout)
	private RelativeLayout nicknameRlayout;
	@ViewInject(id = R.id.memberinfo_age_rlayout)
	private RelativeLayout ageRlayout;
	@ViewInject(id = R.id.memberinfo_gender_rlayout)
	private RelativeLayout genderRlayout;
	@ViewInject(id = R.id.memberinfo_nation_rlayout)
	private RelativeLayout nationRlayout;
	@ViewInject(id = R.id.memberinfo_area_rlayout)
	private RelativeLayout areaRlayout;
	@ViewInject(id = R.id.memberinfo_city_rlayout)
	private RelativeLayout cityRlayout;

	// private ArrayAdapter areaAdapter, cityAdapter;
	private ReadAreaFile readAreaFile;
	private List provinceList, cityList;

	private View view;
	private ManageUsersActivity activity;
	private Bitmap avatarBitmap;
	private CallBack doneBtnCallback, avatorCallBack, matrixCallBack;
	private Long mid;
	private UserInfoBo userInfoBo;
	private UserInfo doneUserInfo;
	private String avatorURL = "";
	private String maxcardURL = "";
	private boolean change_avatar;
	private boolean change_maxcard;
	private int provinceId, cityId;
	// 用来计算点击完成后有多少次请求的类
	private CountCallBack countCallBack;
	// 性别选项的对话框
	private Dialog dialog;
	// 地区的dialogFragment
	private AreaDialogFragment areaFragment;

	private InputMethodManager imm;
	private DisplayImageOptions options;

	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			UserInfo userInfo = userInfoBo.getUinfoByMid(mid);
			if (msg.what == 1) {

				// JCTODO 设置头像下面的用户名 以后还有其他用户信息
				if (userInfo.getNick_name() != null) {
					nickNameTextView.setText(userInfo.getNick_name());
				} else {
					nickNameTextView.setText("");
				}
				if (userInfo.getAge() != null && userInfo.getAge() >= 0) {
					ageTextView.setText(userInfo.getAge() + "");
				} else {
					ageTextView.setText("");
				}
				if (userInfo.getSex() != null && userInfo.getSex() >= 0) {
					genderTextView
							.setText(userInfo.getSex() == 0 ? getText(R.string.man)
									: getText(R.string.women));
				} else {
					genderTextView.setText("");
				}
				if (userInfo.getNation() != null) {
					nationTextView.setText(userInfo.getNation());
				} else {
					nationTextView.setText("");
				}
				if (userInfo.getAvatar() != null) {
					ImageLoader.getInstance().displayImage(
							userInfo.getAvatar(), photo, options);
				} else {
					photo.setImageResource(R.drawable.photo_default);
				}
				if (userInfo.getMatrix() != null) {
					ImageLoader.getInstance().displayImage(
							userInfo.getMatrix(), maxcard, options);
				} else {
					maxcard.setImageResource(R.drawable.matrix);
				}
				if (userInfo.getProvince() != null) {
					areaTextView.setText(readAreaFile
							.getNameByProvinceId(userInfo.getProvince()));
					provinceId = userInfo.getProvince();
				} else {
					areaTextView.setText("");
				}
				if (userInfo.getCity() != null) {
					cityTextView.setText(readAreaFile.getNameById(userInfo
							.getCity()));
				} else {
					cityTextView.setText("");
				}
			}

		}
	};

	public static MemberInfoEditFragment getInstance() {
		return new MemberInfoEditFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (ManageUsersActivity) getActivity();
		// 初始化加载图片的尺寸
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.photo_default)
				.showImageForEmptyUri(R.drawable.photo_default)
				.showImageOnLoading(R.drawable.photo_default)
				.showImageOnFail(R.drawable.photo_default).cacheInMemory(true)
				.cacheOnDisc(true).build();
		initCallBack();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_memberinfo_edit, container,
				false);
		fieldView(view);
		init();
		initUserInfo();
		return view;
	}

	private void init() {
		imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		readAreaFile = new ReadAreaFile(getActivity());
		userInfoBo = new UserInfoBo(activity);
		countCallBack = new CountCallBack();

		backBtn.setOnClickListener(this);
		doneBtn.setOnClickListener(this);
		photo.setOnClickListener(this);
		maxcard.setOnClickListener(this);
		nickNameTextView.setOnClickListener(this);
		ageTextView.setOnClickListener(this);
		nationTextView.setOnClickListener(this);
		areaTextView.setOnClickListener(this);
		cityTextView.setOnClickListener(this);
		genderTextView.setOnClickListener(this);

		nicknameRlayout.setOnClickListener(this);
		ageRlayout.setOnClickListener(this);
		genderRlayout.setOnClickListener(this);
		nationRlayout.setOnClickListener(this);
		areaRlayout.setOnClickListener(this);
		cityRlayout.setOnClickListener(this);

		change_avatar = false;
		change_maxcard = false;

	}

	private void initUserInfo() {
		if (activity.getMid() != -1) {
			// 获取MemberList传过来的mid
			mid = activity.getMid();
		} else {
			// 根据mid获取带本地数据库的对象
			mid = Preference.instance(getActivity()).getLong(
					ContantsUtil.USER_MID);
		}

		Message message = new Message();
		message.what = 1;
		handler.sendMessage(message);
	}

	/**
	 * 获取省份数据
	 * 
	 * @return
	 */
	private String[] getProvinceData() {
		provinceList = readAreaFile.readProvince();
		return readAreaFile.getAddressArray(provinceList);
	}

	/**
	 * 获取市区的数据
	 * 
	 * @return
	 */
	private String[] getCityData(int provinceId) {
		cityList = readAreaFile.readCity(provinceId);
		return readAreaFile.getAddressArray(cityList);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			activity.onBackPressed();
			break;
		case R.id.memberinfo_donebtn:
			doneBtnClick();
			break;
		case R.id.photo:
			// 检查有没有sd卡
			if (SdCardUtil.checkSdState() == 1) {
				ToastTool.showMemberStatus(ToastTool.SDCardNotFound, activity);
				return;
			}
			Intent photoIntent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			/* 开启Pictures画面Type设定为image */
			photoIntent.setType("image/*");
			photoIntent.putExtra("crop", "true");
			photoIntent.putExtra("aspectX", 1);
			photoIntent.putExtra("aspectY", 1);
			photoIntent.putExtra("outputX", 300);
			photoIntent.putExtra("outputY", 300);
			photoIntent.putExtra("scale", true);
			photoIntent.putExtra("return-data", false);
			photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getAvatarTempUri());
			photoIntent.putExtra("outputFormat",
					Bitmap.CompressFormat.JPEG.toString());
			photoIntent.putExtra("noFaceDetection", true); // no face detection
			/* 取得相片后返回本画面 */
			startActivityForResult(photoIntent, 1);
			break;
		case R.id.maxcard:
			// 检查有没有sd卡
			if (SdCardUtil.checkSdState() == 1) {
				ToastTool.showMemberStatus(ToastTool.SDCardNotFound, activity);
				return;
			}
			Intent maxcardIntent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			/* 开启Pictures画面Type设定为image */
			maxcardIntent.setType("image/*");
			maxcardIntent.putExtra("crop", "true");
			maxcardIntent.putExtra("aspectX", 1);
			maxcardIntent.putExtra("aspectY", 1);
			maxcardIntent.putExtra("outputX", 300);
			maxcardIntent.putExtra("outputY", 300);
			maxcardIntent.putExtra("scale", true);
			maxcardIntent.putExtra("return-data", false);
			maxcardIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, getMaxcardTempUri());
			maxcardIntent.putExtra("outputFormat",
					Bitmap.CompressFormat.JPEG.toString());
			maxcardIntent.putExtra("noFaceDetection", true); // no face
																// detection
			/* 取得相片后返回本画面 */
			startActivityForResult(maxcardIntent, 2);
			break;
		case R.id.memberinfo_gender:
			if (imm.isActive() && activity.getCurrentFocus() != null) {
				imm.hideSoftInputFromWindow(activity.getCurrentFocus()
						.getWindowToken(), 0);
			}
			showMenuDialog();
			break;
		case R.id.memberinfo_area:
			areaFragment = new AreaDialogFragment(areSelectLisener(),
					getProvinceData(), getText(R.string.select_province) + "");
			areaFragment.show(activity.getSupportFragmentManager(),
					"province_tag");
			break;
		case R.id.memberinfo_city:
			areaFragment = new AreaDialogFragment(citySelectLisener(),
					getCityData(provinceId), getText(R.string.select_city) + "");
			areaFragment.show(activity.getSupportFragmentManager(), "city_tag");
			break;

		case R.id.memberinfo_nation:
			editClicked(nationTextView);
			break;
		case R.id.memberinfo_nickname:
			editClicked(nickNameTextView);
			break;
		case R.id.memberinfo_age:
			editClicked(ageTextView);
			break;

		case R.id.memberinfo_nickname_rlayout:
			nickNameTextView.performClick();
			break;
		case R.id.memberinfo_age_rlayout:
			ageTextView.performClick();
			break;
		case R.id.memberinfo_nation_rlayout:
			nationTextView.performClick();
			break;
		case R.id.memberinfo_gender_rlayout:
			// 获取焦点
			genderTextView.requestFocus();
			// 关闭之前打开的软键盘
			if (imm.isActive() && activity.getCurrentFocus() != null) {
				imm.hideSoftInputFromWindow(activity.getCurrentFocus()
						.getWindowToken(), 0);
			}
			// 点击genderTextView，弹出选择性别的对话框
			genderTextView.performClick();
			break;
		case R.id.memberinfo_area_rlayout:
			// 获取焦点
			areaTextView.requestFocus();
			// 关闭之前打开的软键盘
			if (imm.isActive() && activity.getCurrentFocus() != null) {
				imm.hideSoftInputFromWindow(activity.getCurrentFocus()
						.getWindowToken(), 0);
			}
			// 点击areaTextView，弹出选择省份的对话框
			areaTextView.performClick();
			break;
		case R.id.memberinfo_city_rlayout:
			// 获取焦点
			cityTextView.requestFocus();
			// 关闭之前打开的软键盘
			if (imm.isActive() && activity.getCurrentFocus() != null) {
				imm.hideSoftInputFromWindow(activity.getCurrentFocus()
						.getWindowToken(), 0);
			}
			// 点击cityTextView，弹出选择城市的对话框
			cityTextView.performClick();
			break;
		}
	}

	/**
	 * 可手动编辑的editText点击之后的处理
	 * 
	 * @param editText
	 */
	private void editClicked(EditText editText) {
		// 获取焦点
		editText.requestFocus();
		// 由于内容向右靠
		editText.setSelection(editText.getText().length());
		// 关闭之前打开的软键盘
		if (imm.isActive() && activity.getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(activity.getCurrentFocus()
					.getWindowToken(), 0);
		}
		// 弹出键盘
		imm.toggleSoftInputFromWindow(editText.getWindowToken(), 0,
				InputMethodManager.SHOW_FORCED);
	}

	private OnListDialogItemSelect areSelectLisener() {
		OnListDialogItemSelect arelisener = new OnListDialogItemSelect() {

			@Override
			public void onListItemSelected(String selection) {
				provinceId = readAreaFile.findProvinceIdByName(selection);
				areaTextView.setText(selection);
				cityTextView.setText(R.string.unlimited);
			}
		};
		return arelisener;
	}

	private OnListDialogItemSelect citySelectLisener() {
		OnListDialogItemSelect citylisener = new OnListDialogItemSelect() {

			@Override
			public void onListItemSelected(String selection) {
				cityId = readAreaFile.findAreaIdByName(selection);
				if (cityId == -1) {
					areaTextView.setText(selection);
				}
				cityTextView.setText(selection);
			}
		};
		return citylisener;
	}

	private void showMenuDialog() {
		dialog = new ListDialog(activity);
		dialog.show();
		// 先show再设置视图
		Window window = dialog.getWindow();
		window.setContentView(R.layout.dialog_two_menu);
		TextView manText = (TextView) window
				.findViewById(R.id.dialog_first_menu);
		manText.setText(R.string.man);
		TextView womanText = (TextView) window
				.findViewById(R.id.dialog_second_menu);
		womanText.setText(R.string.women);
		manText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				genderTextView.setText(R.string.man);
				dialog.dismiss();
			}
		});
		womanText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				genderTextView.setText(R.string.women);
				dialog.dismiss();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			// Uri uri = data.getData();
			ContentResolver cr = activity.getContentResolver();
			try {
				switch (requestCode) {
				case 1:
					avatarBitmap = ClippingPicture.decodeBitmapStream(
							cr.openInputStream(getAvatarTempUri()), 67, 67);
					avatarBitmap = ClippingPicture.toCircleCorner(avatarBitmap);
					/* 将Bitmap设定到ImageView */
					photo.setImageBitmap(avatarBitmap);
					avatorURL = SelectPicUtil.getPicPath(getAvatarTempUri(),
							activity);
					change_avatar = true;
					countCallBack.increment();
					break;
				case 2:
					Bitmap maxCardBitmap = ClippingPicture.decodeBitmapStream(
							cr.openInputStream(getMaxcardTempUri()), 67, 67);
					maxCardBitmap = ClippingPicture
							.toCircleCorner(maxCardBitmap);
					maxcard.setImageBitmap(maxCardBitmap);
					maxcardURL = SelectPicUtil.getPicPath(getMaxcardTempUri(),
							activity);
					change_maxcard = true;
					countCallBack.increment();
					break;
				}
			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void doneBtnClick() {
		if (CheckUtil.isNull(ageTextView.getText())) {
			Toast.makeText(activity, "请输入您的年龄", Toast.LENGTH_SHORT).show();
			return;
		}
		if (CheckUtil.isNull(genderTextView.getText())) {
			Toast.makeText(activity, "请选择您的性别", Toast.LENGTH_SHORT).show();
			return;
		}

		activity.getLoading().setDialogLabel(
				getText(R.string.editing_member) + "");
		activity.showLoading();

		UserInfo userInfo = userInfoBo.getUinfoByMid(mid);
		// 更新数据库
		userInfo.setNick_name(nickNameTextView.getText().toString());
		if (ageTextView.getText().toString().length() > 0) {
			userInfo.setAge(Integer.parseInt(ageTextView.getText().toString()));
		} else {
			userInfo.setAge(-1);
		}
		String genderStr = genderTextView.getText().toString();
		if (CheckUtil.checkLength(genderStr, 1)) {
			userInfo.setSex(genderStr.equals(getText(R.string.man)) ? 0 : 1);
		} else {
			userInfo.setSex(-1);
		}
		if (CheckUtil.checkLength(nationTextView.getText().toString(), 0)) {
			userInfo.setNation(nationTextView.getText().toString());
		} else {
			userInfo.setNation("");
		}
		if (CheckUtil.checkLength(areaTextView.getText().toString(), 0)) {
			userInfo.setProvince(readAreaFile.findProvinceIdByName(areaTextView
					.getText().toString()));
		} else {
			userInfo.setProvince(-1);
		}
		if (CheckUtil.checkLength(cityTextView.getText().toString(), 0)) {
			userInfo.setCity(readAreaFile.findAreaIdByName(cityTextView
					.getText().toString()));
		} else {
			userInfo.setCity(-1);
		}
		doneUserInfo = userInfo;

		// 放入map
		Map<String, Object> dataMap = UserInfo.putUserInfoToMap(userInfo);
		// 提交更新用户数据请求
		countCallBack.increment();
		UserDataAPI.updateMember(dataMap, activity, doneBtnCallback);

		// 如果头像换掉了
		if (change_avatar) {
			// 提交更新头像请求
			Map<String, Object> avatarMap = new HashMap<String, Object>();
			avatarMap.put("mid", String.valueOf(userInfo.getMid()));
			activity.getLoading().setDialogLabel(
					getText(R.string.uploading_avatar) + "");
			activity.showLoading();
			UserDataAPI.uploadAvatar(avatarMap, avatorURL, activity,
					avatorCallBack);
		}

		// // 如果二维码换掉了
		// if (change_maxcard) {
		// // 提交更新头像请求
		// Map<String, Object> matrixMap = new HashMap<String, Object>();
		// matrixMap.put("mid", String.valueOf(userInfo.getMid()));
		// activity.getLoading().setDialogLabel(getText(R.string.uploading_maxcard)
		// + "");
		// activity.showLoading();
		// UserDataAPI.uploadMatrix(matrixMap, maxcardURL, activity,
		// matrixCallBack);
		// }
		// JCTODO 提交到本地数据库
	}

	private void initCallBack() {
		doneBtnCallback = new CallBack() {

			@Override
			public void callback(String json) {
				activity.hideLoading();
				try {
					JSONObject data = new JSONObject(json);
					int status = data.getInt("status");
					switch (status) {
					case HttpContants.REQUEST_SUCCESS:
						userInfoBo.updateUserInfo(doneUserInfo);
						if (CheckUtil.checkEquels(activity.getMid(),
								ContantsUtil.DEFAULT_TEMP_UID)) {
							ContantsUtil.curInfo = doneUserInfo;
						}
						countCallBack.decrement();
						break;
					default:
						ToastTool.showToast(status, activity);
						break;
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
		};

		avatorCallBack = new CallBack() {

			@Override
			public void callback(String json) {
				activity.hideLoading();
				try {
					JSONObject object = new JSONObject(json);
					int status = object.getInt("status");
					if (status == HttpContants.REQUEST_SUCCESS) {
						String avatar = object.getString("data");
						//mImageFetcher.addCacheHandle(avatar, avatarBitmap);
						//ImageLoader.getInstance().displayImage(avatar, photo, options);
						ImageLoader.getInstance().loadImageSync(avatar, options);
						//mImageFetcher.loadCircleImage(avatar, photo);
						ContantsUtil.curInfo.setAvatar(avatar);
						userInfoBo.updateUserInfo(ContantsUtil.curInfo);
						countCallBack.decrement();
					} else {
						ToastTool.showMemberStatus(
								HttpContants.UPLOAD_AVATAR_FAILED, activity);
						ToastTool.showToast(status, activity);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};

		matrixCallBack = new CallBack() {

			@Override
			public void callback(String json) {
				activity.hideLoading();
				try {
					JSONObject object = new JSONObject(json);
					int status = object.getInt("status");
					if (status == HttpContants.REQUEST_SUCCESS) {
						String matrix = object.getString("data");
						//ImageLoader.getInstance().displayImage(matrix, photo, options);
						UserInfo uInfo = userInfoBo.getUinfoByMid(mid);
						uInfo.setMatrix(matrix);
						userInfoBo.updateUserInfo(uInfo);
						countCallBack.decrement();
					} else {
						ToastTool.showMemberStatus(
								HttpContants.UPLOAD_MAXCARD_FAILED, activity);
						ToastTool.showToast(status, activity);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
	}

	@Override
	public void onPause() {
		// 软键盘弹出的话就关闭它
		if (imm.isActive() && activity.getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(activity.getCurrentFocus()
					.getWindowToken(), 0);
		}
		super.onPause();

	}

	public boolean onBackKeyPressed() {
		return true;
	}

	private class CountCallBack {
		public AtomicInteger countBack = new AtomicInteger();

		public void increment() {
			countBack.getAndIncrement();
		}

		public void decrement() {
			countBack.decrementAndGet();
			if (countBack.get() == 0) {
				ToastTool.showMemberStatus(HttpContants.MODIFY_SUCCESS,
						activity);
				activity.onBackPressed();
			}
		}
	}

	private Uri getAvatarTempUri() {
		Uri tempPhotoUri = Uri.fromFile(getAvatarTempFile());
		return tempPhotoUri;
	}

	private Uri getMaxcardTempUri() {
		Uri tempPhotoUri = Uri.fromFile(getMaxcardTempFile());
		return tempPhotoUri;
	}

	private File getMaxcardTempFile() {
		if (SelectPicUtil.isSDCARDMounted()) {

			File f = new File(Environment.getExternalStorageDirectory()
					+ "/Android/data/" + context.getPackageName() + "/cache/",
					"maxcardTemp.jpg");
			Log.d("uri", "fiel path =" + f.getAbsolutePath());
			try {
				f.createNewFile();
			} catch (IOException e) {

			}
			return f;
		}
		return null;
	}

	private File getAvatarTempFile() {
		if (SelectPicUtil.isSDCARDMounted()) {

			File f = new File(Environment.getExternalStorageDirectory()
					+ "/Android/data/" + context.getPackageName() + "/cache/",
					"avatarTemp.jpg");
			Log.d("uri", "fiel path =" + f.getAbsolutePath());
			try {
				f.createNewFile();
			} catch (IOException e) {

			}
			return f;
		}
		return null;
	}

}
