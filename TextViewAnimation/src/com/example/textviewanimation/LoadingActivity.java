package com.example.textviewanimation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import personInfo.MyApplication;
import personInfo.PersonInfo;
import personInfo.Result_getNews_request;
import personInfo.Result_spilt_request;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.utils.File_SD_utils;
import com.example.utils.HttpProcess;
import com.example.utils.JsonProcess;
import com.example.utils.MyDialog;
import com.example.utils.NetCheckReceiver;

public class LoadingActivity extends Activity {

	ImageView imageView = null;
	ImageView appImageView = null;
	Dialog dialog = null;
	boolean isLogin=false;
	PersonInfo personInfo = null;
	public static final String LoadingActivityTAG="LoadingActivity";

	File_SD_utils fileUtils = new File_SD_utils();

	Handler checkNetworkStateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.arg1 == 0) {
				Toast.makeText(LoadingActivity.this, "无网络连接...",
						Toast.LENGTH_SHORT).show();

			} else {
				//Toast.makeText(LoadingActivity.this, "有网络", Toast.LENGTH_SHORT).show();
			}
		}

	};
	Runnable loadingRunnable = new Runnable() {
		public void run() {

			//dialog.show();// 显示正在载入的界面
			System.out.println("personInfo:"+Contacts.PersonalData);
			if (personInfo.getIsLogin() != null)
				System.out.println(personInfo.getIsLogin());

			//直接跳转登录页面或者主页面
			skip();
			
			if (!Contacts.isNetworkConnected(LoadingActivity.this)) {
				// 若无网络，finish 载入界面
				LoadingActivity.this.finish();
			} else {
				// 请求推送消息的参数对
				NameValuePair pair1 = new BasicNameValuePair("Num", Contacts.RequestGetPushNews);
				NameValuePair pair2 = new BasicNameValuePair("User_name", personInfo.getUserName());

				System.out.println("!!!!!!!!!!!!!!personInfo.getUserName()"+personInfo.getUserName());
				List<NameValuePair> pairList = new ArrayList<NameValuePair>();
				pairList.add(pair1);
				pairList.add(pair2);
				HttpProcess httpProcess1 = new HttpProcess(mHandler,
						Contacts.RequestGetPushNews,null);
				
				
				httpProcess1.execute(pairList);

			}
		}
	};
	
	// 生成一个BroadcastReceiver对象
	NetCheckReceiver netCheckReceiver = new NetCheckReceiver(
			checkNetworkStateHandler);

	// 处理跳转到主Activity
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Log.d(">>>>>Mhandler", "开始handleMessage");
			if (msg.obj != ""
					&& msg.arg1 == Integer.valueOf(Contacts.RequestGetPushNews)
							.intValue()) {
				// 获得推送消息
				JsonProcess jsonProcess1 = new JsonProcess();
				boolean isJson =JsonProcess.isGoodJson(msg.obj.toString());
				//System.out.println("isJson:"+isJson);
				if(isJson){
				Result_getNews_request result = jsonProcess1
						.getResult_getNews_request(msg.obj.toString());
				Contacts.MessageShow = result.getResult();
				// 向SD卡中写入缓存
				fileUtils.writePushNewsBuffertoSD(result.getResult(),
						Contacts.LocalBufferFolder,
						Contacts.LocalPushNewsBufferdata);
				  //发送广播通知主界面更新数据
				 Intent intent = new Intent();
		         intent.putExtra("data", "OK");   
		         intent.setAction(Contacts.LADING_ONLINE_DATA_ACTION);
		         sendBroadcast(intent); 
		         LoadingActivity.this.finish();
				}else
				{
					Toast.makeText(LoadingActivity.this, "网络连接错误！！", Toast.LENGTH_SHORT).show();
					//发送广播通知主界面更新数据
					Intent intent = new Intent();
			        intent.putExtra("data", "FAILED");   
			        intent.setAction(Contacts.LADING_ONLINE_DATA_ACTION);
			        sendBroadcast(intent); 
			        LoadingActivity.this.finish();
				}
			}else if(msg.arg2==Contacts.TIME_OUT)
			{
				Toast.makeText(LoadingActivity.this, "网络连接超时喔(⊙o⊙)。。。", Toast.LENGTH_LONG).show();
				//发送广播通知主界面更新数据
				Intent intent = new Intent();
		        intent.putExtra("data", "TIMEOUT");   
		        intent.setAction(Contacts.LADING_ONLINE_DATA_ACTION);
		        sendBroadcast(intent); 
		        LoadingActivity.this.finish();
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置全屏显示
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
		setContentView(R.layout.activity_loading);
		imageView = (ImageView) findViewById(R.id.loading_image);
		appImageView = (ImageView) findViewById(R.id.loading_app_image);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				LoadingActivity.this, R.anim.loading_anim);
		Animation spaceJumpAnimation = AnimationUtils.loadAnimation(
				LoadingActivity.this, R.anim.loading_app_image);
		imageView.startAnimation(hyperspaceJumpAnimation);
		appImageView.startAnimation(spaceJumpAnimation);


		// 载入个人信息
		personInfo = loadingDataFromSD();
		Contacts.PersonalData = personInfo;
		
		MyApplication.getInstance().addActivity(this);
		//刚开始就要初始化读取[表情]的配置文件
		new Thread(new Runnable() {
            @Override
            public void run() {
                biaoqing.FaceConversionUtil.getInstace().getFileText(getApplication());
            }
        }).start();
		
		/***************************************************/
		IntentFilter intentfilter = new IntentFilter();
		// 添加action
		intentfilter
				.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
		// 注册接收器
		LoadingActivity.this.registerReceiver(netCheckReceiver, intentfilter);
		mHandler.postDelayed(loadingRunnable, 2000);
		loadBufferMessage();
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LoadingActivity.this.unregisterReceiver(netCheckReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mymenu, menu);
		return true;
	}
	//当客户点击菜单中的某一个选项时调用该方法
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			if(item.getItemId()==R.id.menu_about)
			{
				// 设置一个progressdialog的弹窗
				//dialog =  MyDialog.createAboutDialog(LoadingActivity.this, R.style.InterestDialog);
				//dialog.show();
			}
			else if(item.getItemId()==R.id.menu_exit)
			{
				
				SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy/MM/dd    HH:mm:ss   ");       
				Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
				String    str    =    formatter.format(curDate);
				if(Contacts.PersonalData.getIsLogin().equals("1")){
					Contacts.PersonalData.setLoginTime(str);
					Contacts.PersonalData.setIsStorageBuffer("1");
				}
				fileUtils.writePersonInfo2SD(Contacts.PersonalData, Contacts.LocalPersonFolder, Contacts.LocalPersondata);
				
				MyApplication.getInstance().exitApp();;//结束程序
				//finish();
				System.exit(0);
				
			}else if(item.getItemId()==R.id.menu_account)
			{
				// 设置一个progressdialog的弹窗
				dialog =  MyDialog.createAccountDialog(LoadingActivity.this, R.style.InterestDialog);
				Button clearAccount=(Button)dialog.findViewById(R.id.clearfocusaccount);
				Button chageAccount=(Button)dialog.findViewById(R.id.changefocusaccount);
				clearAccount.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Contacts.PersonalData.setFirstLogin("0");
						Contacts.PersonalData.setIsLogin("0");
						Contacts.PersonalData.setIsLoginRemember("0");
						Contacts.PersonalData.setIsStorageBuffer("0");
						Contacts.PersonalData.setLoginTime("");
						Contacts.PersonalData.setUserName("");
						fileUtils.writePersonInfo2SD(Contacts.PersonalData, Contacts.LocalPersonFolder, Contacts.LocalPersondata);
						//dialog.dismiss();
					}
				});
				chageAccount.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Contacts.PersonalData.setFirstLogin("0");
						Contacts.PersonalData.setIsLogin("0");
						Contacts.PersonalData.setIsLoginRemember("0");
						Contacts.PersonalData.setIsStorageBuffer("0");
						Contacts.PersonalData.setLoginTime("");
						Contacts.PersonalData.setUserName("");
						fileUtils.writePersonInfo2SD(Contacts.PersonalData, Contacts.LocalPersonFolder, Contacts.LocalPersondata);
						Intent fromMain2LoginIntent = new Intent();
						fromMain2LoginIntent.setClass(LoadingActivity.this,
								LoginActivity.class);
						startActivity(fromMain2LoginIntent);
						dialog.dismiss();
					}
				});
				dialog.show();
			}
			return super.onOptionsItemSelected(item);
		}

	/**
	 * 从SD卡缓存中读入个人信息数据
	 * 2014-9-27
	 * 
	 * @author:5354xyz
	 */
	public PersonInfo loadingDataFromSD() {

		try {
			PersonInfo personInfoGet;
			personInfoGet = fileUtils
					.loadingPersonInfo(Contacts.LocalPersonFolder);
			return personInfoGet;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 跳转页面
	 * 2014-9-27
	 * 
	 * @author:5354xyz
	 */
	public void skip()
	{

		Intent fromLoadingtoIntent = new Intent();
		// 判断是否已经登录并且记住密码
		if (personInfo.getIsLogin().equals("0")) {
			System.out.println("跳转1");
			// 若未登录跳转到登录界面
			fromLoadingtoIntent.putExtra("from",LoadingActivity.LoadingActivityTAG);
			fromLoadingtoIntent.setClass(LoadingActivity.this,
					LoginActivity.class);
			startActivity(fromLoadingtoIntent);

			//overridePendingTransition(R.anim.base_slide_remain,R.anim.push_up_out);
		} else {
			//dialog.dismiss();
			isLogin=true;
			System.out.println("跳转2");
			// 若登录跳转到主界面
			fromLoadingtoIntent.putExtra("isLogin", isLogin);
			fromLoadingtoIntent.setClass(LoadingActivity.this,
					MainActivity.class);
			startActivity(fromLoadingtoIntent);
		}
	}
	/**
	 * 异步载入缓存推送数据
	 * 2014-9-27
	 * 
	 * @author:5354xyz
	 */
	public void loadBufferMessage(){
		new Thread(new Runnable() {
            @Override
            public void run() {
            	// 从缓存中读取数据
				try {
					System.out.println("loadBufferMessage()从缓存中读取数据");
					Contacts.MessageShow = fileUtils.loadingPushNews(
							Contacts.LocalBufferFolder,
							Contacts.LocalPushNewsBufferdata, true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }).start();
	}
}