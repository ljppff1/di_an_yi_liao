package com.dian.diabetes.activity.user;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.db.UserInfoBo;
import com.dian.diabetes.db.dao.UserInfo;
import com.dian.diabetes.request.HttpContants;
import com.dian.diabetes.tool.CommonUtil;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.tool.ReadAreaFile;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil.CallBack;
import com.dian.diabetes.widget.CircleImageView;
import com.dian.diabetes.widget.anotation.ViewInject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 类/接口注释
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月10日
 * 
 */
public class MemberInfoFragment extends BaseFragment implements OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.memberinfo_editbtn)
	private Button editBtn;
	@ViewInject(id = R.id.maxcard_layout)
	private RelativeLayout maxcardLayout;
	@ViewInject(id = R.id.memberinfo_nickname)
	private EditText userNickName;
	@ViewInject(id = R.id.memberinfo_age)
	private EditText age;
	@ViewInject(id = R.id.memberinfo_gender)
	private EditText gender;
	@ViewInject(id = R.id.memberinfo_nation)
	private EditText nation;
	@ViewInject(id = R.id.memberinfo_city)
	private EditText city;
	@ViewInject(id = R.id.memberinfo_province)
	private EditText province;
	@ViewInject(id = R.id.photo)
	private CircleImageView photo;
	@ViewInject(id = R.id.maxcard)
	private ImageView maxcard;

	private View view;
	private ManageUsersActivity activity;
	private ReadAreaFile areaFile;
	private DisplayImageOptions options;

	private UserInfoBo userInfoBo;
	private Long mid;

	private CallBack getUserInfo;

	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			if (msg.what == 1) {
				UserInfo uInfo = userInfoBo.getUinfoByMid(mid);
				if (uInfo == null) {
					userNickName.setText("");
					age.setText("");
					gender.setText("");
					nation.setText("");
					photo.setImageResource(R.drawable.user_icon_unkown);
				} else {
					if (uInfo.getNick_name() != null) {
						userNickName.setText(uInfo.getNick_name());
					} else {
						userNickName.setText("");
					}
					if (uInfo.getAge() != null && uInfo.getAge() >= 0) {
						age.setText(uInfo.getAge() + "");
					} else {
						age.setText("");
					}
					if (uInfo.getSex() != null && uInfo.getSex() >= 0) {
						gender.setText(uInfo.getSex() == 0 ? getText(R.string.man)
								: getText(R.string.women));
					} else {
						gender.setText("");
					}
					if (uInfo.getNation() != null) {
						nation.setText(uInfo.getNation());
					} else {
						nation.setText("");
					}
					if (uInfo.getProvince() != null) {
						province.setText(areaFile.getNameByProvinceId(uInfo
								.getProvince()));
					} else {
						province.setText("");
					}
					if (uInfo.getCity() != null) {
						city.setText(areaFile.getNameById(uInfo.getCity()));
					} else {
						city.setText("");
					}
					if (uInfo.getAvatar() != null) {
						ImageLoader.getInstance().displayImage(uInfo.getAvatar(), photo, options);
						//mImageFetcher.loadCircleImage(uInfo.getAvatar(), photo);
					} else {
						photo.setImageResource(R.drawable.photo_default);
					}
					initMatrixCode(uInfo.getMid() + uInfo.getNick_name()
							+ CommonUtil.getValue("sex" + uInfo.getSex())
							+ uInfo.getAge());
				}
			}
		}
	};

	public static MemberInfoFragment getInstance() {
		return new MemberInfoFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (ManageUsersActivity) getActivity();
		userInfoBo = new UserInfoBo(activity);
		areaFile = new ReadAreaFile(activity);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.photo_default)
				.showImageForEmptyUri(R.drawable.photo_default)
				.showImageOnLoading(R.drawable.photo_default)
				.showImageOnFail(R.drawable.photo_default).cacheInMemory(true)
				.cacheOnDisc(true).build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater
				.inflate(R.layout.fragment_member_info, container, false);
		fieldView(view);
		init();
		initUserInfo();
		return view;
	}

	private void init() {
		backBtn.setOnClickListener(this);
		editBtn.setOnClickListener(this);
		// 初始化加载图片的尺寸
		initCallBack();
	}

	private void initUserInfo() {
		// 根据mid获取带本地数据库的对象
		mid = Preference.instance(getActivity()).getLong(ContantsUtil.USER_MID);
		handler.sendEmptyMessage(1);
	}

	private void initCallBack() {
		getUserInfo = new CallBack() {

			@Override
			public void callback(String json) {
				activity.hideLoading();
				try {
					JSONObject data = new JSONObject(json);

					int status = data.getInt("status");
					switch (status) {
					case HttpContants.REQUEST_SUCCESS:
						UserInfo userInfo = new UserInfo();
						UserInfo.transformToUserInfo(userInfo,
								data.getString("data"));
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
						handler.sendEmptyMessage(1);
						break;
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			activity.finish();
			break;
		case R.id.memberinfo_editbtn:
			editBtnClick();
			break;
		}
	}

	private void editBtnClick() {
		BaseFragment tempFragment;
		boolean isAdd = true;
		String tag;
		tag = "edit_member_info";
		isAdd = true;
		tempFragment = (BaseFragment) context.getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = MemberInfoEditFragment.getInstance();
			isAdd = false;
		}
		activity.replaceFragment(tag, tempFragment, isAdd);
	}

	private void initMatrixCode(String codeStr) {
		int size = getResources().getDimensionPixelSize(R.dimen.matrix_width);
		BitMatrix matrix;
		try {
			matrix = new MultiFormatWriter().encode(codeStr,
					BarcodeFormat.QR_CODE, size, size);
			int width = matrix.getWidth();
			int height = matrix.getHeight();
			// 二维矩阵转为一维像素数组,也就是一直横着排了
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (matrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			// 通过像素数组生成bitmap,具体参考api
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			maxcard.setImageBitmap(bitmap);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
