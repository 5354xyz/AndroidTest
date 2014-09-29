package com.example.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

/** 
 * @author  5354xyz
 * @version 2014-2-28 下午4:14:43 
 * @Eacer-mail: xiaoyizong@126.com
 */

//GestureDetector需要在View中重写onTouchEvent事件
public class GestureListener extends SimpleOnGestureListener  
{  
	private int screen_width;//获得屏幕的宽度
	private OnFlingListener listener_onfling;//滑动的监听器
	public GestureListener(Context context)
	{
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		screen_width = dm.widthPixels;
	}
	
	public void setOnFlingListener(OnFlingListener listener) {
		
			this.listener_onfling=listener;
		}
	public static abstract class OnFlingListener {
		public abstract void OnFlingLeft();
		public abstract void OnFlingRight();
	}
	
	

	//双击的第二下Touch down时触发 
    @Override  
    public boolean onDoubleTap(MotionEvent e)  
    {  
        // TODO Auto-generated method stub  
        Log.i("TEST", "onDoubleTap");  
        return super.onDoubleTap(e);  
    }  

    //只要接触屏幕都会触发这个事件
    @Override  
    public boolean onDown(MotionEvent e)  
    {  
        // TODO Auto-generated method stub  
        Log.i("TEST", "onDown");  
        return super.onDown(e);  
    }  

    //Touch了滑动一点距离后，up时触发。 
    @Override  
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
            float velocityY)  
    {  
        // TODO Auto-generated method stub  
       // Log.i("TEST", "onFling:velocityX = " + velocityX + " velocityY" + velocityY);  
    	// X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
    	final int FLING_MIN_DISTANCE = (int) (screen_width / 3.0f), FLING_MIN_VELOCITY = 200;
    	if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
    	listener_onfling.OnFlingLeft();
    	} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
    	listener_onfling.OnFlingRight();
    	}
        return super.onFling(e1, e2, velocityX, velocityY);  
    }  

    //Touch了不移动一直Touch down时触发 
    @Override  
    public void onLongPress(MotionEvent e)  
    {  
        // TODO Auto-generated method stub  
        Log.i("TEST", "onLongPress");  
        System.out.println("长按屏幕");
        super.onLongPress(e);  
    }  

    //Touch了滑动时触发。
    @Override  
    public boolean onScroll(MotionEvent e1, MotionEvent e2,  
            float distanceX, float distanceY)  
    {  
        // TODO Auto-generated method stub  
        //Log.i("TEST", "onScroll:distanceX = " + distanceX + " distanceY = " + distanceY);  
        return super.onScroll(e1, e2, distanceX, distanceY);  
    }  

    //单击了屏幕
    @Override  
    public boolean onSingleTapUp(MotionEvent e)  
    {  
        // TODO Auto-generated method stub  
        Log.i("TEST", "onSingleTapUp");  
        return super.onSingleTapUp(e);  
    }  
      
}  
