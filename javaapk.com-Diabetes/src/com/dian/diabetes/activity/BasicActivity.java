package com.dian.diabetes.activity;

import java.lang.reflect.Field;

import com.dian.diabetes.R;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.SdCardUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.ViewGroup.LayoutParams;

@SuppressWarnings("deprecation")
public class BasicActivity extends FragmentActivity implements
		OnGestureListener {
	
	protected static final int REQUEST_CODE_CAMERA = 1;
	protected static final int REQUEST_CODE_PHOTO = 2;
	protected static final int REQUEST_CODE_PHOTO_DEAL = 3;

	protected BasicActivity context;
	public int screenWidth = 0;
	public int screenHeight = 0;
	private GestureDetector detector;
	private boolean isGesture = false;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		// 手势
		detector = new GestureDetector(this);
	}
	
	protected void onStart(){
		super.onStart();
		if(Config.canUpdate()){
			Preference preference = Preference.instance(context);
			if (preference.getBoolean(Preference.HAS_UPDATE)) {
				Intent playAlarm = new Intent(ContantsUtil.SYCN_DATA);
				playAlarm.putExtra("state", true);
				context.startService(playAlarm);
				Config.isUpdate = true;
			}else{
				Config.stopUpdate();
			}
		}
	}
	
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		initView();
	}

	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		initView();
	}

	public void setContentView(View view) {
		super.setContentView(view);
		initView();
	}
	
	/**
	 * 初始化findviewbyid注解
	 */
	public void initView() {
		Field[] fields = getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				try {
					field.setAccessible(true);
					if (field.get(this) != null)
						continue;
					ViewInject viewInject = field
							.getAnnotation(ViewInject.class);
					if (viewInject != null) {
						int viewId = viewInject.id();
						field.set(this, findViewById(viewId));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean dispatchTouchEvent(MotionEvent event) {
		if (detector.onTouchEvent(event)) {
			event.setAction(MotionEvent.ACTION_CANCEL);
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (isGesture) {
			float subX = e2.getX() - e1.getX();
			float subY = e2.getY() - e1.getY();		
			if (subX > 150 && Math.abs(subY) < 170) {
				scrollXBack();
			}else if(subX < -150 && Math.abs(subY) < 170){
				srollYRight();
			}
		}
		return false;
	}

	protected void scrollXBack() {
		finish();
	}
	
	protected void srollYRight(){
		
	}

	/**
	 * 设置为可以侧滑关�?
	 * 
	 * @param state
	 */
	public void setGuesture(boolean state) {
		isGesture = state;
	}

	/**
	 * 覆写finish方法，覆盖默认方法，加入切换动画
	 */
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_right_in,
				R.anim.slide_right_out);
	}
	
	public void finishResult(){
		setResult(RESULT_OK);
		this.finish();
	}
	
	public void finishSimple(){
		super.finish();
	}

	/**
	 * 覆写startactivity方法，加入切换动�?
	 */
	public void startActivity(Bundle bundle,Class<?> target) {
		Intent intent = new Intent(this, target);
		if(bundle != null){
			intent.putExtras(bundle);
		}
		startActivity(intent);
		overridePendingTransition(R.anim.slide_left_in,
				R.anim.slide_left_out);
	}
	
	/**
	 * 带回调的跳转
	 * @param bundle
	 * @param requestCode
	 * @param target
	 */
	public void startForResult(Bundle bundle,int requestCode,Class<?> target){
		Intent intent = new Intent(this, target);
		if(bundle != null){
			intent.putExtras(bundle);
		}
		startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.slide_left_in,
				R.anim.slide_left_out);
	}
	
	/**
	 * 打开照相�?
	 */
	protected void openCamera(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra("return-data", false);
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.parse(SdCardUtil.TEMP));
		startActivityForResult(intent, REQUEST_CODE_CAMERA);
	}
	
	/**
	 * 打开照片选择
	 */
	protected void pickUpPhoto(){
		Intent intent = new Intent();  
        intent.setType("image/*");   
        intent.setAction(Intent.ACTION_GET_CONTENT);   
        startActivityForResult(intent, REQUEST_CODE_PHOTO);
	}
	
	/*
	 * 对图片进行剪裁，通过Intent来调用系统自带的图片剪裁API
	 */
	protected void cropPhoto(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		/* aspectX aspectY 是裁剪后图片的宽高比�?*/
		intent.putExtra("aspectX", 5);
		intent.putExtra("aspectY", 5);
		/* outputX outputY 是裁剪图片宽�?*/
		intent.putExtra("outputX", 256);
		intent.putExtra("outputY", 256);
		intent.putExtra("noFaceDetection", true);// 关闭人脸�?��
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(SdCardUtil.TEMP));
		startActivityForResult(intent, REQUEST_CODE_PHOTO_DEAL);
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
