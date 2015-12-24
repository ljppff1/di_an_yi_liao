package com.dian.diabetes.widget;

import com.dian.diabetes.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MutiProgress extends View {

	private RectF bgRect;
	private RectF oval;
	private RectF topl;
	private Paint paint;

	private Bitmap backGround;
	private int width;
	private int height;

	private int eatColor = Color.rgb(232, 145, 0);
	private int sportColor = Color.rgb(232, 145, 0);
	private float persent1 = 0;
	private float persent2 = 0;
	private float progressStrokeWidth = 1;

	public MutiProgress(Context context) {
		super(context);
		initBitmap();
	}

	public MutiProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		bgRect = new RectF();
		oval = new RectF();
		topl = new RectF();
		paint = new Paint();
		eatColor = context.getResources().getColor(R.color.eat_color);
		sportColor = context.getResources().getColor(R.color.sport_color);
		initBitmap();
	}

	private void initBitmap() {
		progressStrokeWidth = getResources().getDimensionPixelSize(
				R.dimen.simple_stock_width);
		backGround = BitmapFactory.decodeResource(getResources(),
				R.drawable.muti_bg);
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
		oval.left = (float) (width / 2 - width * 0.397);
		oval.top = (float) (width / 2 - height * 0.394);
		oval.right = (float) Math.floor(width / 2 + width * 0.397);
		oval.bottom = (float) Math.floor(width / 2 + height * 0.395);
		topl.left = (float) (width / 2 - width * 0.267);
		topl.top = (float) (width / 2 - height * 0.263);
		topl.right = (float) Math.floor(width / 2 + width * 0.272);
		topl.bottom = (float) Math.floor(width / 2 + height * 0.268);
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
		paint.setColor(sportColor);
		paint.setStrokeWidth(progressStrokeWidth); // 线宽
		paint.setStyle(Style.STROKE);
		canvas.drawArc(topl, -90, persent1 * 360, false, paint);

//		// 内环
//		// 画圆
//		paint.setColor(defaultColor);
//		paint.setStrokeWidth(progressStrokeWidth); // 线宽
//		paint.setStyle(Style.STROKE);
//		canvas.drawArc(oval, -90, 360, false, paint);
		// 画圆%
		paint.setColor(eatColor);
		paint.setStrokeWidth(progressStrokeWidth); // 线宽
		paint.setStyle(Style.STROKE);
		canvas.drawArc(oval, -90, persent2 * 360, false, paint);
	}

	public void setValue(float value, float value2) {
		persent1 = 1 - 1 / (value / 2000 + 1);
		persent2 = 1 - 1 / (value2 / 2000 + 1);
		invalidate();
	}

	public void setValue(float value, float value2, float max, int color1,
			int color2) {
		persent1 = 1 - 1 / (value2 / max + 1);
		persent2 = 1 - 1 / (value / max + 1);
		eatColor = color2;
		sportColor = color1;
		invalidate();
	}

}
