package com.dian.diabetes.widget;

import com.dian.diabetes.R;
import com.dian.diabetes.tool.Config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SimpleProgress extends View {

	private RectF bgRect;
	private RectF topl;
	private Paint paint;
	private Matrix matrix;

	private int width;
	private int height;
	// 背景
	private Bitmap backGround;
	private Bitmap topBitmap;

	private int colos = Color.rgb(232, 145, 0);
	private float value = 0;
	private float max = 30;
	private float progressStrokeWidth = 1;
	private int[] imgSourceId;
	private float scale = 1;

	public SimpleProgress(Context context) {
		super(context);
		initBitmap();
	}

	public SimpleProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		bgRect = new RectF();
		topl = new RectF();
		paint = new Paint();
		matrix = new Matrix();
		matrix.setScale(1.0f, 1.0f);
		imgSourceId = new int[] { R.color.trend_down_normal,
				R.color.trend_down_bad, R.color.trend_down_bad,
				R.color.trend_up_bad };
		colos = context.getResources().getColor(R.color.eat_color);
		initBitmap();
	}

	private void initBitmap() {
		backGround = BitmapFactory.decodeResource(getResources(),
				R.drawable.sim_bg);
		topBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.progress_top);
		progressStrokeWidth = getResources().getDimensionPixelSize(
				R.dimen.simple_stock_width);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = View.MeasureSpec.getSize(widthMeasureSpec);
		height = View.MeasureSpec.getSize(heightMeasureSpec);
		bgRect.left = 0;
		bgRect.top = 0;
		bgRect.right = width;
		bgRect.bottom = height;
		topl.left = (float) (width / 2 - width * 0.397);
		topl.top = (float) (width / 2 - height * 0.394);
		topl.right = (float) Math.floor(width / 2 + width * 0.397);
		topl.bottom = (float) Math.floor(width / 2 + height * 0.395);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint.setAntiAlias(true); // 设置画笔为抗锯齿
		paint.setFilterBitmap(true);
		canvas.drawBitmap(backGround, null, bgRect, paint);

//		// 画圆
//		paint.setColor(defaultColor);
//		paint.setStrokeWidth(progressStrokeWidth); // 线宽
//		paint.setStyle(Style.STROKE);
//		canvas.drawArc(topl, -90, 360, false, paint);

		// 画圆%
		paint.setColor(colos);
		paint.setStrokeWidth(progressStrokeWidth); // 线宽
		paint.setStyle(Style.STROKE);
		canvas.drawArc(topl, -90, (value / max) * 360, false, paint);

		// 上层覆盖图
		scale = (float) (backGround.getWidth()/width * 0.95);
		matrix.setScale(1f, 1f);
		matrix.postScale(scale, scale);
		matrix.postRotate((value / max) * 360 - 5);
		Bitmap temp = Bitmap.createBitmap(topBitmap, 0, 0,
				topBitmap.getWidth(), topBitmap.getHeight(), matrix, true);
		canvas.drawBitmap(temp, width / 2 - temp.getWidth() / 2, width / 2
				- temp.getHeight() / 2, paint);
	}

	public void setMax(float max) {
		this.max = max;
	}

	public void setValue(float value, int type) {
		int level = Config.getBloodState(value, type);
		colos = getResources().getColor(imgSourceId[level]);
		this.value = value;
		invalidate();
	}

	public void setValueLevel(float value, int level) {
		colos = getResources().getColor(imgSourceId[level]);
		this.value = value;
		invalidate();
	}

	public void setValueLevel(float value, float max, int level) {
		colos = getResources().getColor(imgSourceId[level]);
		this.max = max;
		this.value = value;
		invalidate();
	}
}
