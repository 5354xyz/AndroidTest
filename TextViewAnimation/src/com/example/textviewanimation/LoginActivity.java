package com.example.textviewanimation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import personInfo.MyApplication;
import personInfo.Result_getNews_request;

import com.ant.liao.GifView;
import com.example.utils.File_SD_utils;
import com.example.utils.HttpProcess;
import com.example.utils.JsonProcess;
import com.example.utils.MyDialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private Button signinButton=null;
	private EditText passwordText=null;
	private EditText usernameText=null;
	private CheckBox rememberBox=null;
	private Handler loginHandler=null;
	private String password=null;
	private String username=null;
	private String fromTag=null;
	public static final String LoginActivityTAG="LoginActivity";
	private Dialog dialog = null;
	//GifView gifLoading;//载入gif
	File_SD_utils fileUtils = new File_SD_utils();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_login);
		signinButton=(Button)findViewById(R.id.signin_button_1);
		signinButton.setOnClickListener(new SigninButtonListener());
		rememberBox=(CheckBox)findViewById(R.id.remember_pass_1);
		MyApplication.getInstance().addActivity(this);
		Intent intent = getIntent(); 
		 // 取出Intent中的数据，通过键值对的方式 
		fromTag = intent.getStringExtra("from"); 
		//是否记住密码
		rememberBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					Contacts.PersonalData.setIsLoginRemember("1");
				}else
				{
					Contacts.PersonalData.setIsLoginRemember("0");
				}
			}
		});
		
		//传给Http的handler
		loginHandler = new Handler() 
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.obj!="" && msg.arg1 == Integer.valueOf(Contacts.RequestLogin).intValue())
				{
					//处理登录Http请求返回的结果
					JsonProcess jsonProcess=new JsonProcess();
					boolean isJson =JsonProcess.isGoodJson(msg.obj.toString());
					//System.out.println("isJson:"+isJson);
					if(isJson){
					String isLogin = jsonProcess.checkLoginStatus(msg.obj.toString());
					
					//Contacts.PersonalData.setIsLogin(isLogin)
					if(isLogin.equals("true"))
					{
						Contacts.PersonalData.setIsLogin("1");
						
						SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy/MM/dd    HH:mm:ss   ");       
						Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
						String    str    =    formatter.format(curDate);
						System.out.println(str);
						Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
						
						dialog.dismiss();
						if(fromTag != null && fromTag.equals(PostActivity.PostActivityTAG))
						{
							Log.i("PostActivityTAG", LoginActivityTAG);
							LoginActivity.this.finish();
						}
						else
						{
						Intent fromLogin2MainIntent = new Intent();
						fromLogin2MainIntent.putExtra("loginFace", true);
						fromLogin2MainIntent.setClass(LoginActivity.this,
								MainActivity.class);
						startActivity(fromLogin2MainIntent);
						LoginActivity.this.finish();
						}
						
					}else
					{
						Contacts.PersonalData.setIsLogin("0");
						dialog.dismiss();
						Toast.makeText(LoginActivity.this, "登陆失败，你的账号或者密码不对", Toast.LENGTH_SHORT).show();
					}
					}else
					{
						Contacts.PersonalData.setIsLogin("0");
						dialog.dismiss();
						Toast.makeText(LoginActivity.this, "sorry，网络数据错误", Toast.LENGTH_LONG).show();
					}
				}else if(msg.arg2==Contacts.TIME_OUT)
				{
					dialog.dismiss();
					Toast.makeText(LoginActivity.this, "网络连接超时喔(⊙o⊙)。。。", Toast.LENGTH_LONG).show();
				}		
			}
			
		};
		
		
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
				dialog =  MyDialog.createAboutDialog(LoginActivity.this, R.style.InterestDialog);
				dialog.show();
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
				dialog =  MyDialog.createAccountDialog(LoginActivity.this, R.style.InterestDialog);
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
						dialog.dismiss();
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
						fromMain2LoginIntent.setClass(LoginActivity.this,
								LoginActivity.class);
						startActivity(fromMain2LoginIntent);
						dialog.dismiss();
					}
				});
				dialog.show();
			}
			return super.onOptionsItemSelected(item);
		}

	//跳转注册页面
	public void register(View v)
	{
			// TODO Auto-generated method stub
			Intent fromLogintoRegister =new Intent();
			fromLogintoRegister.setClass(LoginActivity.this, RegisterActivity.class);
			startActivity(fromLogintoRegister);
			overridePendingTransition(R.anim.push_left_in,R.anim.base_slide_remain);
		
	}
	
	public void back(View v)
	{
		//Intent data=new Intent();
		//data.putExtra("loginFace", true);
		//setResult(21, data);
		Intent fromLogin2MainIntent = new Intent();
		fromLogin2MainIntent.putExtra("loginFace", true);
		fromLogin2MainIntent.setClass(LoginActivity.this,
				MainActivity.class);
		startActivity(fromLogin2MainIntent);
		LoginActivity.this.finish();
		overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_remain);
	}
	
	//提交登录信息
	public class SigninButtonListener implements OnClickListener
		{

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				usernameText=(EditText)findViewById(R.id.username_edit);
				username=usernameText.getText().toString();
				passwordText=(EditText)findViewById(R.id.password_edit);
				password=passwordText.getText().toString();
				
				Contacts.PersonalData.setUserName(username);
				if(checkLegitimacy()){
				//请求推送消息的参数对
				NameValuePair pair1 = new BasicNameValuePair("Num", Contacts.RequestLogin);
				NameValuePair pair2 = new BasicNameValuePair("User_name", username);
				NameValuePair pair3 = new BasicNameValuePair("Password", password);

	            List<NameValuePair> pairList = new ArrayList<NameValuePair>();
	            pairList.add(pair1);
	            pairList.add(pair2);
	            pairList.add(pair3);
	            HttpProcess httpProcess1=new HttpProcess(loginHandler,Contacts.RequestLogin,null);
	            if (!Contacts.isNetworkConnected(LoginActivity.this)) {
	            		Toast.makeText(LoginActivity.this, "无网络连接，请设置网络", Toast.LENGTH_LONG).show();
	            	}
	            	else{
	            		httpProcess1.execute(pairList);
	            		//弹出正在登录的
	            		dialog =  MyDialog.createLoadingDialog(LoginActivity.this,R.style.custom_dialog,"正在登录。。");
	            		dialog.show();
	    		}
			}
			}
			
		}
	
	public boolean checkLegitimacy()
	{
		
		if(username.equals(""))
		{
			//通过产生消息提醒
			Toast.makeText(LoginActivity.this, "用户名不能为空",Toast.LENGTH_SHORT).show();
			return false;
		}else if(password.equals(""))
		{
			//通过产生消息提醒
			Toast.makeText(LoginActivity.this, "密码不能为空",Toast.LENGTH_SHORT).show();
			return false;
		}else if(password.length()<6)
		{
			//通过产生消息提醒
			Toast.makeText(LoginActivity.this, "密码尽量不要小于6位",Toast.LENGTH_SHORT).show();
			//return false;
		}

		return true;
	}
}
