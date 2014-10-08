package com.example.utils;

import com.example.textviewanimation.R;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * 自定义可以滑动的RelativeLayout, 类似于IOS的滑动删除页面效果，当我们要使用
 * 此功能的时候，需要将该Activity的顶层布局设置为SildingFinishLayout
 * 要注意：android 触摸事件分发拦截（如果子View或者子ViewGroup里面还有事件监听方法要先排除）
 * 
 * @email  xiaoyizong@126.com
 * 
 */
public class SildingFinishLayout extends RelativeLayout{
	/**
	 * SildingFinishLayout布局的父布局
	 */
	private ViewGroup mParentView;
	/**
	 * 滑动的最小距离
	 */
	private int mTouchSlop;
	/**
	 * 按下点的X坐标
	 */
	private int downX;
	/**
	 * 按下点的Y坐标
	 */
	private int downY;
	/**
	 * 临时存储X坐标
	 */
	private int tempX;
	/**
	 * 滑动类
	 */
	private Scroller mScroller;
	/**
	 * SildingFinishLayout的宽度
	 */
	private int viewWidth;
	
	private boolean isSilding;
	
	private OnSildingFinishListener onSildingFinishListener;
	private boolean isFinish;
	private int direction = -1;
	/**
	 * 是否可以向某个方向滚动
	 */
	private boolean isSetLeft = false;
	private boolean isSetRight = false;
	private boolean isSetLeft_Right = false;
	/**
	 * 是否是人工滚动回原来的地方
	 */
	private boolean byPeople = false;
	

	public SildingFinishLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SildingFinishLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mScroller = new Scroller(context);
	}
	/**
	 * 1 代表向左滑动
	 * 2代表向右
	 * 3代表左右
	 * @param direction
	 */
	public void setSlidingDirection(int direction)
	{
		switch(direction){
			case 1:isSetLeft=true;break;
			case 2:isSetRight=true;break;
			case 3:isSetLeft_Right=true;break;
			default:return;
		}
			
	}
	/**
	 * 事件拦截操作
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = tempX = (int) ev.getRawX();
			downY = (int) ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getRawX();
			//满足此条件屏蔽SildingFinishLayout里面子类的touch事件
			if (Math.abs(moveX - downX) > mTouchSlop
					&& Math.abs((int) ev.getRawY() - downY) < mTouchSlop 
					&& !checkIndisbootchoose(ev) && (isSetLeft||isSetRight||isSetLeft_Right)) {
				System.out.println("屏蔽子类的touch事件");
				return true;
			}
			break;
		}
		
		return super.onInterceptTouchEvent(ev);
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!checkIndisbootchoose(event)) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				int moveX = (int) event.getRawX();
				int deltaX = tempX - moveX;
				tempX = moveX;
				if (Math.abs(moveX - downX) > mTouchSlop
						&& Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
					isSilding = true;
				}

				if (Math.abs(moveX - downX) >= 0 && isSilding) {
					if (((moveX - downX) > 0 && isSetLeft)
							|| ((moveX - downX) < 0 && isSetRight)
							|| isSetLeft_Right)
						mParentView.scrollBy(deltaX, 0);
				}
				break;
			case MotionEvent.ACTION_UP:
				isSilding = false;
				if (mParentView.getScrollX() <= -viewWidth / 2) {
					isFinish = true;
					direction = 1;
					scrollRight();
				} else if (mParentView.getScrollX() >= viewWidth / 2) {
					isFinish = true;
					direction = 2;
					scrollLeft();
				} else {
					isFinish = false;
					direction = 0;
					scrollOrigin(false);

				}
				break;
			}
		}
		return true;
	}
	

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			// 获取SildingFinishLayout所在布局的父布局
			mParentView = (ViewGroup) this.getParent();
			viewWidth = this.getWidth();
		}
	}

	/**
	 * 设置OnSildingFinishListener, 在onSildingFinish()方法中finish Activity
	 * 
	 * @param onSildingFinishListener
	 */
	public void setOnSildingFinishListener(
			OnSildingFinishListener onSildingFinishListener) {
		this.onSildingFinishListener = onSildingFinishListener;
	}


	/**
	 * 从左向右滚动出界面
	 */
	private void scrollRight() {
		final int delta = (viewWidth + mParentView.getScrollX());
		// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
		mScroller.startScroll(mParentView.getScrollX(), 0, -delta + 1, 0,
				Math.abs(delta));
		postInvalidate();
	}
	
	/**
	 * 从左向右滚动出界面
	 */
	private void scrollLeft() {
		final int delta = (viewWidth - mParentView.getScrollX());
		// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
		mScroller.startScroll(mParentView.getScrollX(), 0, delta - 1, 0,
				Math.abs(delta));
		postInvalidate();
	}

	/**
	 * 滚动到起始位置
	 */
	public void scrollOrigin(boolean byPeople) {
		//getScrollX() ---> Return the scrolled left position of this view(返回偏离了屏幕左边界多远  “正数(左边界|)负数”)
		this.byPeople = byPeople;
		int delta = mParentView.getScrollX();
		mScroller.startScroll(mParentView.getScrollX(), 0, -delta, 0,
				Math.abs(delta));
		postInvalidate();
	}


	@Override
	public void computeScroll() {
		// 调用startScroll的时候scroller.computeScrollOffset()返回值为boolean，true说明滚动尚未完成，false说明滚动已经完成
		if (mScroller.computeScrollOffset()) {
			mParentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();

			if (mScroller.isFinished() && isFinish) {

				if (onSildingFinishListener != null) {
					if(!this.byPeople){
						onSildingFinishListener.onSildingFinish(direction);
						}
					this.byPeople = false;
						
				}else{
					//没有设置OnSildingFinishListener，让其滚动到其实位置
					scrollOrigin(false);
					isFinish = false;
				}
			}
		}
	}
	

	public interface OnSildingFinishListener {
		/**
		 * 从左向右滚动出界面 type=1
		 * 从右向左滚动出界面 type=2
		 * @param type
		 */
		public void onSildingFinish(int type);
	}

	/**
	 * 判断手势是否落在一些不需要执行向右滑动销毁的区域内内，这个地方写死了，可以修改
	 * 2014-4-13
	 * 
	 * @author:5354xyz
	 */
	public boolean checkIndisbootchoose(MotionEvent event)
	{
		boolean isHit=false;
		Rect rect=null;
		if(getId() == R.id.post_act_sildingFinishLayout){
			isHit=false;
			rect = new Rect();
			findViewById(R.id.ll_facechoose).getHitRect(rect);//获得该控件所占的矩形区域
			isHit = rect.contains((int) event.getX(), (int) event.getY());//判断手势是否在矩形区域内
			return isHit;
		}else if(getId() == R.id.pulltorefresh_sildingFinishLayout)
		{
			
			if(findViewById(R.id.image_show_RelativeLayout).getVisibility() == View.VISIBLE)//获得该控件所占的矩形区域
				return true;
			else
				return false;
		}
			return false;
	}
}
