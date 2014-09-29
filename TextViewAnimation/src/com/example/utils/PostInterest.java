package com.example.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.example.textviewanimation.Contacts;
import com.example.textviewanimation.R;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;



/** 
 * @author  5354xyz
 * @version 2013-11-23 下午10:27:50 
 * @Eacer-mail: xiaoyizong@126.com
 */
public class PostInterest {
	Dialog dialog = null;
	Handler postInterestHandler=new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
		
	};
	public PostInterest(Dialog interestDialog)
	{
		this.dialog=interestDialog;
	}
	public Intrest setIntrest(){
		
	final Intrest thisIntrest =new Intrest();
    CheckBox joke=(CheckBox)dialog.findViewById(R.id.joke);
    CheckBox read=(CheckBox)dialog.findViewById(R.id.read);
    CheckBox news=(CheckBox)dialog.findViewById(R.id.news);
    CheckBox sport=(CheckBox)dialog.findViewById(R.id.sport);
    CheckBox game=(CheckBox)dialog.findViewById(R.id.game);
    CheckBox entertainment=(CheckBox)dialog.findViewById(R.id.entertainment);
    Button comfirmButton=(Button)dialog.findViewById(R.id.comfirm_int);
    Button cancelButton=(Button)dialog.findViewById(R.id.cancel_int);
    cancelButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			System.out.println("xdrftgh");
			dialog.dismiss();
		}
	});
    
    comfirmButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			//请求推送消息的参数对
			NameValuePair pair1 = new BasicNameValuePair("Num", Contacts.RequestAddInterest);
			NameValuePair pair2 = new BasicNameValuePair("User_name", Contacts.PersonalData.getUserName());
			NameValuePair pair3 = new BasicNameValuePair("News", thisIntrest.newsstr);
			NameValuePair pair4 = new BasicNameValuePair("Sport", thisIntrest.sportstr);
			NameValuePair pair5 = new BasicNameValuePair("Entertainment", thisIntrest.entertainmentstr);
			NameValuePair pair6 = new BasicNameValuePair("Joke", thisIntrest.jokestr);
			NameValuePair pair7 = new BasicNameValuePair("Game", thisIntrest.gamestr);
			NameValuePair pair8 = new BasicNameValuePair("Read", thisIntrest.readstr);

            List<NameValuePair> pairList = new ArrayList<NameValuePair>();
            pairList.add(pair1);
            pairList.add(pair2);
            pairList.add(pair3);
            pairList.add(pair4);
            pairList.add(pair5);
            pairList.add(pair6);
            pairList.add(pair7);
            pairList.add(pair8);
            HttpProcess httpProcess1=new HttpProcess(postInterestHandler,Contacts.RequestAddInterest,null);
            httpProcess1.execute(pairList);
		}
	});
  //为多选按钮添加监听器
    joke.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if(isChecked)
			{
				thisIntrest.jokestr = "1";
			}else
			{
				thisIntrest.jokestr = "0";
			}
		}
	});
  //为多选按钮添加监听器
    read.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if(isChecked)
			{
				thisIntrest.readstr = "1";
			}else
			{
				thisIntrest.readstr = "0";
			}
		}
	});
  //为多选按钮添加监听器
    news.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if(isChecked)
			{
				thisIntrest.newsstr = "1";
			}else
			{
				thisIntrest.newsstr = "0";
			}
		}
	});
  //为多选按钮添加监听器
    sport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if(isChecked)
			{
				thisIntrest.sportstr = "1";
			}else
			{
				thisIntrest.sportstr = "0";
			}
		}
	});
  //为多选按钮添加监听器
    game.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if(isChecked)
			{
				thisIntrest.gamestr = "1";
			}else
			{
				thisIntrest.gamestr = "0";
			}
		}
	});
  //为多选按钮添加监听器
    entertainment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
				if(isChecked)
				{
					thisIntrest.entertainmentstr = "1";
				}else
				{
					thisIntrest.entertainmentstr = "0";
				}
			}
		});
	return thisIntrest;
	}
	
}
class Intrest
{
	 String jokestr="0";
	 String readstr="0";
	 String newsstr="0";
	 String entertainmentstr="0";
	 String sportstr="0";
	 String gamestr="0";
}