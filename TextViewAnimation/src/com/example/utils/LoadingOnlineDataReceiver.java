package com.example.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

/** 
 * @author  5354xyz
 * @version 2014-9-27 下午10:20:54 
 * @E5354xyz-mail: xiaoyizong@126.com
 */
public class LoadingOnlineDataReceiver  extends BroadcastReceiver{

	Handler handler=null;
	Message message = new Message();
	
	public LoadingOnlineDataReceiver(Handler handler)
	{
		this.handler=handler;
		message.arg1 = 0;
	}
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		//得到广播中得到的数据，并显示出来
        String resOfLoadOnlineData = arg1.getStringExtra("data");
        if (resOfLoadOnlineData.equals("OK")){
        	message.arg1 = 1;
        }else if(resOfLoadOnlineData.equals("FAILED")){
        	message.arg1 = 2;
        }else if(resOfLoadOnlineData.equals("TIMEOUT")){
        	message.arg1 = 3;
        }
        handler.sendMessage(message);

	}
	
	
}
