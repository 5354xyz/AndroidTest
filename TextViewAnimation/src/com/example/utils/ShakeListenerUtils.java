package com.example.utils;



import java.util.List;

import personInfo.PushNews;

import com.example.textviewanimation.Contacts;
import com.example.textviewanimation.ContentActivity;
import com.example.textviewanimation.R;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.format.Time;

public class ShakeListenerUtils implements SensorEventListener  
{  
    private Activity context; 
    Vibrator vibrator = null; 
    PushNews pushNews=null;
    int type=0;
    Handler thishandler=null;
    
  
    long time=System.currentTimeMillis();
    public ShakeListenerUtils(Activity context,PushNews pushNews,int type,Handler handler)  
    {  
        super();  
        this.context = context; 
        this.pushNews=pushNews;
        this.type=type;
        this.thishandler=handler;
       
        vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE); 
    }  
  
    @Override  
    public void onSensorChanged(SensorEvent event)  
    {  
        int sensorType = event.sensor.getType();  
        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴    
        float[] values = event.values;  
  
        if (sensorType == Sensor.TYPE_ACCELEROMETER)  
        {  
  
            /*正常情况下，任意轴数值最大就在9.8~10之间，只有在突然摇动手机  
              的时候，瞬时加速度才会突然增大或减少。   监听任一轴的加速度大于17即可 
            */
        	
            if ((Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math  
                    .abs(values[2]) > 17))  
            {  
            	long time2=System.currentTimeMillis();
            	System.out.println(time+"|"+time2);
            	if(time2-time>250 && type==0){
            		time=time2;
            		Intent toContentIntent = new Intent(context, ContentActivity.class);  
            		Bundle mBundle = new Bundle(); 
            		mBundle.putParcelable("foculsNews", pushNews);
            		toContentIntent.putExtras(mBundle);
                
            		context.startActivity(toContentIntent);
            		context.overridePendingTransition(R.anim.zoomin,  
            				R.anim.zoomout); 
            		//摇动手机后，再伴随震动提示~~  
            		vibrator.vibrate(100);
            		List<PushNews> pushNewss= Contacts.MessageShow;
            		int ramNewsLocation = (int)(Math.random()*Contacts.MessageShow.size());
            		pushNews=pushNewss.get(ramNewsLocation);
            		Message msg=new Message();
            		msg.arg1 = Contacts.SHAKE_OK_M;
            		thishandler.sendMessage(msg);
            		//context.finish();  
                }else if(time2-time>250 && type==1)
                {
                	time=time2;
                	Message msg=new Message();
            		msg.arg1 = Contacts.SHAKE_OK_C;
            		msg.obj=pushNews;
            		thishandler.sendMessage(msg);
            		//摇动手机后，再伴随震动提示~~  
            		vibrator.vibrate(100);
            		List<PushNews> pushNewss= Contacts.MessageShow;
            		int ramNewsLocation = (int)(Math.random()*Contacts.MessageShow.size());
            		pushNews=pushNewss.get(ramNewsLocation);
                }
            }  
        }  
    }  
  
    @Override  
    public void onAccuracyChanged(Sensor sensor, int accuracy)  
    {  
        //当传感器精度改变时回调该方法，Do nothing.   
    }  
  
}  