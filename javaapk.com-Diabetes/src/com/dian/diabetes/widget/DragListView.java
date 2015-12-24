package com.dian.diabetes.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class DragListView extends ListView {

	private ImageView dragImageView;// ����ק�����ʵ����һ��ImageView
	private int dropFrom;// ��ָ�϶���ԭʼ���б��е�λ��
	private int dropTo;// ��ָ�϶���ʱ�򣬵�ǰ�϶������б��е�λ��

	private int dragPointX;// �ڵ�ǰ������е�X���ϵ�λ��
	private int dragPointY;// �ڵ�ǰ������е�Y���ϵ�λ��
	private int dragOffsetX;// ��ǰ��ͼ����Ļ�ľ���(x������)
	private int dragOffsetY;// ��ǰ��ͼ����Ļ�ľ���(y������)

	private WindowManager windowManager;// windows���ڿ�����
	private WindowManager.LayoutParams windowParams;// ���ڿ�����ק�����ʾ�Ĳ���

	private int scaledTouchSlop;// �жϻ����ľ���,���ǵ��׻����೤���룬������ �ƶ� ����
	private int upScrollBounce;// �϶���ʱ�򣬿�ʼ���Ϲ���������
	private int downScrollBounce;// �϶���ʱ�򣬿�ʼ���¹���������

	// ������ݵļ���
	private ExchangeDataListener exchangeDataListener = null;

	boolean longclick = false;
	public boolean isEdit = false;
	public DragListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// ��ô����ƶ��¼�����С����
		scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mLongPressRunnable = new Runnable() {

			@Override
			public void run() {
				mCounter--;
				// ����������0��˵����ǰִ�е�Runnable�������һ��down����ġ�
				if (mCounter > 0 || isReleased || isMoved)
					return;

				Log.e("", "performLongClick");
				// performLongClick();

				longclick = true;
			}
		};
	}

	// ����touch�¼�����ʵ���Ǽ�һ�����
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * �����¼�
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (longclick && exchangeDataListener != null&&isEdit) {
			Log.e("", "DragListView editing");
			longclick=false;
			// ȷ��������� listview������ؼ��� ����
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			Log.i("DragListView", "onInterceptTouchEvent  :  ev.getX() = " + x);
			Log.i("DragListView", "onInterceptTouchEvent  :  ev.gety() = " + y);

			// x,y�������listview�ľ��룬����ͨ��������ֵȷ����listview��λ��
			dropFrom = dropTo = pointToPosition(x, y);
			// �������Ч��listview�ľ���
			if (dropTo == AdapterView.INVALID_POSITION) {
				return super.onInterceptTouchEvent(ev);
			}

			// ��õ����ʱ��ѡ�е�itemView,��ListView����һ��ѡ������Ǽ���view��ͼ�ģ�������ViewGroup
			ViewGroup itemView = (ViewGroup) getChildAt(dropTo
					- getFirstVisiblePosition());

			// ȷ��������� listview�����ѡ��һ����itemview ��λ�ã�
			// ��������Ǹ���һ��ģ�
			// getLeft��getTop���������listview�ľ���
			dragPointX = x - itemView.getLeft();
			dragPointY = y - itemView.getTop();
			Log.i("DragListView", "onInterceptTouchEvent  :  dragPointX = "
					+ dragPointX);
			Log.i("DragListView", "onInterceptTouchEvent  :  dragPointY = "
					+ dragPointY);

			// ��ǰ��ͼ����Ļ�ľ���,getRawX()��getRawY()��ָ����Ļ�ľ���
			dragOffsetX = (int) (ev.getRawX() - x);
			dragOffsetY = (int) (ev.getRawY() - y);
			Log.i("DragListView", "onInterceptTouchEvent  :  ev.getRawX()= "
					+ ev.getRawX());
			Log.i("DragListView", "onInterceptTouchEvent  :  ev.getRawY() = "
					+ ev.getRawY());
			Log.i("DragListView",
					"onInterceptTouchEvent  :  ev.dragOffsetX() = "
							+ dragOffsetX);
			Log.i("DragListView",
					"onInterceptTouchEvent  :  ev.dragOffsetY() = "
							+ dragOffsetX);

			// �ж��϶���λ��: -20 ֻ��Ϊ������ �����϶��Ŀռ� ���������µ�����ֵ��������
				// ���϶���ʱ�򣬼���listview������Ļ�Ļ���listview������Ҫ�ж������Ϲ��������¹�����
				upScrollBounce = Math.min(y - scaledTouchSlop, getHeight() / 3);
				downScrollBounce = Math.max(y + scaledTouchSlop,
						getHeight() * 2 / 3);

				// ͼƬ����
				itemView.setDrawingCacheEnabled(true);
				// �ѻ����е�ͼƬ����ȥ
				Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
				// ��ʼ��ͼ
				startDrag(bm, x, y);
			// ����false��ʾ���Լ������´���

		}

		if (dragImageView != null && dropTo != INVALID_POSITION
				&& exchangeDataListener != null) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_UP:
				int upY = (int) ev.getY();
				// ֹͣ�滭���൱���ͷ��ڴ�
				stopDrag();
				// ��ͼ
				onDrop(upY);
				break;
			case MotionEvent.ACTION_MOVE:
				int moveX = (int) ev.getX();
				int moveY = (int) ev.getY();
				// �滭�ƶ���״̬
				onDrag(moveX, moveY);
				break;
			default:
				break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * ׼���϶�����ʼ���϶����ͼ��
	 * 
	 * @param bm
	 * @param x
	 * @param y
	 */
	public void startDrag(Bitmap bm, int x, int y) {
		// ֹͣ�滭���ͷ��ڴ�
		stopDrag();

		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP;
		windowParams.x = x - dragPointX + dragOffsetX;
		windowParams.y = y - dragPointY + dragOffsetY;
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParams.format = PixelFormat.TRANSLUCENT;
		windowParams.windowAnimations = 0;

		ImageView imageView = new ImageView(getContext());
		imageView.setImageBitmap(bm);
		windowManager = (WindowManager) getContext().getSystemService("window");
		windowManager.addView(imageView, windowParams);
		dragImageView = imageView;
	}

	/**
	 * ֹͣ�϶���ȥ���϶����ͷ��
	 */
	public void stopDrag() {
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
			dragImageView = null;
		}
	}

	/**
	 * �϶�ִ�У���Move������ִ��
	 * 
	 * @param x
	 * @param y
	 */
	public void onDrag(int x, int y) {
		if (dragImageView != null) {
			// �϶��Ĺ�̵� ͸����
			windowParams.alpha = 0.8f;
			windowParams.x = x - dragPointX + dragOffsetX;
			windowParams.y = y - dragPointY + dragOffsetY;
			// ���϶� �߸������ , ע��֮����ܿ���Ч���ˡ���
			windowManager.updateViewLayout(dragImageView, windowParams);
		}
		// Ϊ�˱��⻬�����ָ��ߵ�ʱ�򣬷���-1������
		int tempPosition = pointToPosition(0, y);
		// �ж��Ƿ���Ч����
		if (tempPosition != INVALID_POSITION) {
			dropTo = tempPosition;
		}

		// ����
		int scrollHeight = 0;
		if (y < upScrollBounce) {
			scrollHeight = 8;// �������Ϲ���8�����أ����������Ϲ����Ļ�
		} else if (y > downScrollBounce) {
			scrollHeight = -8;// �������¹���8�����أ������������Ϲ����Ļ�
		}

		if (scrollHeight != 0) {
			// ��������ķ���setSelectionFromTop()
			setSelectionFromTop(dropTo,
					getChildAt(dropTo - getFirstVisiblePosition()).getTop()
							+ scrollHeight);
		}
	}

	/**
	 * �϶����µ�ʱ��
	 * 
	 * @param y
	 */
	public void onDrop(int y) {

		// Ϊ�˱��⻬�����ָ��ߵ�ʱ�򣬷���-1������
		int tempPosition = pointToPosition(0, y);
		if (tempPosition != INVALID_POSITION) {
			dropTo = tempPosition;
		}

		// �����߽紦��
		if (y < getChildAt(0).getTop()) {
			// �����ϱ߽�
			dropTo = 0;
		} else if (y > getChildAt(getChildCount() - 1).getBottom()) {
			// �����±߽�
			dropTo = getAdapter().getCount() - 1;
		}

		// ��ݽ���
		if (dropTo >= 0 && dropTo < getAdapter().getCount()) {
			if (exchangeDataListener != null)
				// �ü̳�����ʵ�ֽ���
				exchangeDataListener.setExchangeData(dropFrom, dropTo);
		}
	}

	private int mLastMotionX, mLastMotionY;
	// �Ƿ��ƶ���
	private boolean isMoved;
	// �Ƿ��ͷ���
	private boolean isReleased;
	// ����������ֹ��ε���������һ���γ�longpress��ʱ����
	private int mCounter;
	// ������runnable
	private Runnable mLongPressRunnable;
	// �ƶ�����ֵ
	private static final int TOUCH_SLOP = 20;

	public boolean dispatchTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			mCounter++;
			isReleased = false;
			isMoved = false;
			postDelayed(mLongPressRunnable,
					500);
			break;
		case MotionEvent.ACTION_MOVE:
			if (isMoved)
				break;
			if (Math.abs(mLastMotionX - x) > TOUCH_SLOP
					|| Math.abs(mLastMotionY - y) > TOUCH_SLOP) {
				// �ƶ�������ֵ�����ʾ�ƶ���
				isMoved = true;
			}
			break;
		case MotionEvent.ACTION_UP:
			// �ͷ���
			isReleased = true;
			break;
		}
		super.dispatchTouchEvent(event);
		return true;
	}

	// ������ݵļ���ӿ�
	public interface ExchangeDataListener {
		void setExchangeData(int from, int to);
	}

	// ��ʼ��������ݵļ���
	public void setExchangeDataListener(
			ExchangeDataListener exchangeDataListener) {
		this.exchangeDataListener = exchangeDataListener;
	}

}
