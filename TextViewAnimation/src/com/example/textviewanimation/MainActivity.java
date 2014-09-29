package com.example.textviewanimation;

import imageLoad.AsyncImageLoadUtil;
import imageLoad.CallbackImp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.example.utils.KeywordsFlow;
import com.example.utils.MyDialog;
import personInfo.MyApplication;
import personInfo.PushNews;
import personInfo.Result_getNews_request;
import slidingmenu.SlidingGridViewAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.textviewanimation.Contacts;
import com.example.utils.File_SD_utils;
import com.example.utils.HttpProcess;
import com.example.utils.JsonProcess;
import com.example.utils.LoadingOnlineDataReceiver;
import com.example.utils.NetCheckReceiver;
import com.example.utils.PostInterest;
import com.example.utils.RoundedImageView;
import com.example.utils.ShakeListenerUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
public class MainActivity extends Activity  implements OnClickListener 
{

	private Button toSpiltBut=null;
	private Button toContentBut=null;
	private com.example.utils.KeywordsFlow keywordsFlow=null;
	private int tospilt=0;
	int [] ram=null;
	boolean isLogin=false;
	boolean loginFace=false;
	boolean isNullData=true;
	boolean isOnStop=false;
	boolean isOnDestroy=false;
	private ShakeListenerUtils shakeUtils;  
    private SensorManager mSensorManager; //定义sensor管理器, 注册监听器用 
    Dialog dialog = null;
    File_SD_utils fileUtils = new File_SD_utils();
    private SlidingMenu slidingMenu;
    private View SlidingMenurootLayout=null;
    RoundedImageView touxiang=null;
    TextView name=null;
    GridView gridView=null;
    Button slidingmenu_back_but=null;
    private AsyncImageLoadUtil loader=new AsyncImageLoadUtil();
  	/*****************handler 的使用**********************/
    
    /**
     * 配合setShowViewRunnable每60s更新一次重新设置textview的推送消息
     */
	Handler setShowViewHandler=new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.arg1 == Contacts.TRUE && !isOnDestroy)
				setShowViewHandler.postDelayed(setShowViewRunnable,60000);
		}
		
	};
	
	Runnable setShowViewRunnable=new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//System.out.println("cccc");
			Message msg = setShowViewHandler.obtainMessage();
			msg.arg1=Contacts.TRUE;
			setShowViewHandler.sendMessage(msg);
			if(!isOnStop){
				setMessageShow();//重新设置textview的推送消息
				// 添加
				feedKeywordsFlow(keywordsFlow, Contacts.MessageShow);
			}
		}
		
	};

	/**
	 * 当网络情况发生变化时
	 */
	Handler checkNetworkStateHandler=new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			System.out.println("网络状态发生变化！！！！");
			if(msg.arg1== 0 )
			{
				Toast.makeText(MainActivity.this, "无网络连接...", Toast.LENGTH_SHORT).show();
			}else if (msg.arg1== 1 ||  msg.arg1== 2){
				System.out.println("线程中更新数据！！！！");
				if (isNullData){
					Toast.makeText(MainActivity.this, "准备更新数据", Toast.LENGTH_SHORT).show();	
			        setMessageShow();
					}
			}
		}
		
	};
	Handler loadingOnlineDataHandler=new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.arg1==1)
			{
				feedKeywordsFlow(keywordsFlow, Contacts.MessageShow);
			}
		}
		
	};
	private Handler loadingDataHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.obj != ""
					&& msg.arg1 == Integer.valueOf(Contacts.RequestGetPushNews)
							.intValue()) {
				// 获得推送消息
				JsonProcess jsonProcess1 = new JsonProcess();
				boolean isJson =JsonProcess.isGoodJson(msg.obj.toString());
				if(isJson){
					Result_getNews_request result = jsonProcess1
							.getResult_getNews_request(msg.obj.toString());
					Contacts.MessageShow = result.getResult();
					// 向SD卡中写入缓存
					fileUtils.writePushNewsBuffertoSD(result.getResult(),
							Contacts.LocalBufferFolder,
							Contacts.LocalPushNewsBufferdata);
					feedKeywordsFlow(keywordsFlow, Contacts.MessageShow);
				}else
				{
					Toast.makeText(MainActivity.this, "网络连接错误！！", Toast.LENGTH_SHORT).show();
					
				}
			}else if(msg.arg2==Contacts.TIME_OUT)
			{
				Toast.makeText(MainActivity.this, "网络连接超时喔(⊙o⊙)。。。", Toast.LENGTH_LONG).show();
			}

		}

	};
	Handler shakeHandler=new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			if(msg.arg1==Contacts.SHAKE_OK_M)
			{
				mSensorManager.unregisterListener(shakeUtils, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
			}
		}
		
	};
	Handler gettouxiangHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.obj!="" && msg.arg1 == Integer.valueOf(Contacts.RequestGettouxiang).intValue())
			{
				//处理登录Http请求返回的结果
				JsonProcess jsonProcess=new JsonProcess();
				boolean isJson =JsonProcess.isGoodJson(msg.obj.toString());
				
				if(isJson){
				String touxiangurl = jsonProcess.gettouxiangfromjson(msg.obj.toString());
				if(!touxiangurl.equals(""))
					loadImage(Contacts.BaseURL_IMAGE+touxiangurl,touxiang);
				}else
				{
					Toast.makeText(MainActivity.this, "sorry，网络数据错误", Toast.LENGTH_LONG).show();
				}
			}else if(msg.arg2==Contacts.TIME_OUT)
			{
				
				Toast.makeText(MainActivity.this, "网络连接超时喔(⊙o⊙)。。。", Toast.LENGTH_LONG).show();
			}		
		}
		
	};
	
	/***************************************************/
	//生成一个BroadcastReceiver对象
  	NetCheckReceiver netCheckReceiver=new NetCheckReceiver(checkNetworkStateHandler);
  	LoadingOnlineDataReceiver loadingOnlineDataReceiver = new LoadingOnlineDataReceiver(loadingOnlineDataHandler);
  	/******************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_main);
		MyApplication.getInstance().addActivity(this);
				Intent getIntent=this.getIntent();
				Bundle bundle = getIntent.getExtras();
				System.out.println("Main_onCreate()");
				
				isLogin=getIntent.getBooleanExtra("isLogin", false);
				loginFace=getIntent.getBooleanExtra("loginFace", false);
				
				//设置网络数据载入的广播接收属性
	            IntentFilter filter = new IntentFilter();
	            filter.addAction(Contacts.LADING_ONLINE_DATA_ACTION);    //只有持有相同的action的接受者才能接收此广播
	            registerReceiver(loadingOnlineDataReceiver, filter);
	            
				if (bundle != null) {
					System.out.println("test123456"+bundle.toString());
					initializeTextView();
					initializeSlidingMenu();
					//生成一个intentfilter对象，过滤
					IntentFilter intentfilter=new IntentFilter();
					//添加action
					intentfilter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
					//注册接收器
					MainActivity.this.registerReceiver(netCheckReceiver, intentfilter);
					//获取传感器管理服务   
			        mSensorManager = (SensorManager) this  
			                .getSystemService(Service.SENSOR_SERVICE);  
			        //加速度传感器    
			        mSensorManager.registerListener(shakeUtils,  
			                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  
			                //还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，    
			                //根据不同应用，需要的反应速率不同，具体根据实际情况设定    
			                SensorManager.SENSOR_DELAY_NORMAL);
					initializeTextView();
					setShowViewHandler.post(setShowViewRunnable);//随机更换推送消息
					getMessageShow();
					// 添加
					feedKeywordsFlow(keywordsFlow, Contacts.MessageShow);
					
				}else
				{
					System.out.println("test123");
				}
	
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("Main_onResume()");
		isOnStop=false;
		// 添加
		keywordsFlow.setStart(true);
		if(!isLogin && !loginFace)
		{
			Intent fromMain2LoginIntent = new Intent();
			fromMain2LoginIntent.setClass(MainActivity.this,
					LoginActivity.class);
			startActivityForResult(fromMain2LoginIntent, 100);
		}
		//获取传感器管理服务   
        mSensorManager = (SensorManager) this  
                .getSystemService(Service.SENSOR_SERVICE);  
        //加速度传感器    
        mSensorManager.registerListener(shakeUtils,  
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  
                //还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，    
                //根据不同应用，需要的反应速率不同，具体根据实际情况设定    
                SensorManager.SENSOR_DELAY_NORMAL);
        
        if(!fileUtils.isExitFile("localInformation", "notFirst")){
        	try {
        		fileUtils.creatSDFile("notFirst", "localInformation");
        	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
        	}
        	Intent fromMain2GuideViewIntent = new Intent();
        	fromMain2GuideViewIntent.setClass(MainActivity.this,
        			GuideViewActivity.class);
        	startActivity(fromMain2GuideViewIntent);
        }
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
				dialog =  MyDialog.createAboutDialog(MainActivity.this, R.style.InterestDialog);
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
				dialog =  MyDialog.createAccountDialog(MainActivity.this, R.style.InterestDialog);
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
						fromMain2LoginIntent.setClass(MainActivity.this,
								LoginActivity.class);
						startActivity(fromMain2LoginIntent);
						dialog.dismiss();
					}
				});
				dialog.show();
			}
			return super.onOptionsItemSelected(item);
		}

	/**********************************************************/
	/**
	 * 初始化TextView
	 * 2014-4-9
	 * 
	 * @author:5354xyz
	 */
	public void initializeTextView()
	{
		//初始化关键词
		keywordsFlow = (KeywordsFlow) findViewById(R.id.textviewShowId);
		keywordsFlow.setDuration(800l);
		keywordsFlow.setOnItemClickListener(this);
		// 添加
		feedKeywordsFlow(keywordsFlow, Contacts.MessageShow);
		keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
		keywordsFlow.settype();
		
		
		toSpiltBut=(Button)findViewById(R.id.toSpiltOut);
		toContentBut=(Button)findViewById(R.id.main_but);
		toSpiltBut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent fromMaintoSpiltIntent=new Intent();
				fromMaintoSpiltIntent.putExtra("num", tospilt);
				fromMaintoSpiltIntent.setClass(MainActivity.this, SpiltOutActivity.class);
				startActivityForResult(fromMaintoSpiltIntent,2);
				overridePendingTransition(R.anim.in_translate_top,
						R.anim.out_translate_top);
			}
		});
		toContentBut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List<PushNews> pushNews= Contacts.MessageShow;	
				int ramNewsLocation = (int)(Math.random()*Contacts.MessageShow.size());
				PushNews foculsNews1=pushNews.get(ramNewsLocation);
				Intent toContentIntent = new Intent(MainActivity.this, ContentActivity.class);  
        		Bundle mBundle = new Bundle(); 
        		mBundle.putParcelable("foculsNews", foculsNews1);
        		toContentIntent.putExtras(mBundle);
            
        		MainActivity.this.startActivity(toContentIntent);
        		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
	}
	/**
	 * 初始化右侧菜单
	 * 2014-4-9
	 * @author:5354xyz
	 */
	public void initializeSlidingMenu(){
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		slidingMenu.setFadeDegree(0.35f);
		//设置菜单占屏幕的比例
		slidingMenu.setBehindOffset(dm.widthPixels*20/100);
		//使SlidingMenu附加在Activity上
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMode(SlidingMenu.LEFT);//这里模式设置为左右都有菜单，RIGHT显示右菜单，LEFT显示左菜单，LEFT_RIGH显示左右菜单
		slidingMenu.setMenu(R.layout.slidingmenu);
		// 获取menu的layout
		SlidingMenurootLayout = slidingMenu.getMenu();
		touxiang =(RoundedImageView) SlidingMenurootLayout.findViewById(R.id.slidingmenu_myicon);
		name =(TextView)SlidingMenurootLayout.findViewById(R.id.slidingmenu_myname_text);
		gridView=(GridView)SlidingMenurootLayout.findViewById(R.id.slidingmenu_gridview);
		slidingmenu_back_but = (Button)SlidingMenurootLayout.findViewById(R.id.slidingmenu_back_but);
		gridView.setAdapter(new SlidingGridViewAdapter(MainActivity.this,SlidingGridViewAdapter.getDataList()));
		touxiang.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.slidingmenu_myicon: {
					Toast.makeText(MainActivity.this, "头像", Toast.LENGTH_SHORT).show();
					break;
				}
			
				}
			}
		
		});
		
		slidingmenu_back_but.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				slidingMenu.toggle(); 
			}
			
		});
		slidingMenu.setOnOpenListener(new OnOpenListener(){
			@Override
			public void onOpen() {
				if(!Contacts.PersonalData.getIsLogin().equals("0")){
				// TODO Auto-generated method stub
					name.setText(Contacts.PersonalData.getUserName());
					//获取头像
					NameValuePair pair1 = new BasicNameValuePair("Num", Contacts.RequestGettouxiang);
					NameValuePair pair2 = new BasicNameValuePair("User_name", Contacts.PersonalData.getUserName());
					List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		            pairList.add(pair1);
		            pairList.add(pair2);
		            HttpProcess httpProcess1=new HttpProcess(gettouxiangHandler,Contacts.RequestGettouxiang,null);
		            if (!Contacts.isNetworkConnected(MainActivity.this)) {
		            		Toast.makeText(MainActivity.this, "无网络连接，请设置网络", Toast.LENGTH_LONG).show();
		            	}
		            	else{
		            		httpProcess1.execute(pairList);
		    		}
					
				}
				
				
			}});
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		isOnStop = true;
		keywordsFlow.setStart(false);
		mSensorManager.unregisterListener(shakeUtils, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
	}

	
	/**
	 * 获得推送的消息
	 * 2014-4-9
	 * 
	 * @author:5354xyz
	 */
	public void getMessageShow()
	{
		System.out.println("!!!!getMessageShow");
		//MessageShowContacts.MessageShow[i]
		if(!Contacts.MessageShow.isEmpty()){
			isNullData = true;
			return;
			}
		List <PushNews> listPushNews=new ArrayList<PushNews> ();
		for(int i=0;i<Contacts.NUMofMessage;i++)
		{
			PushNews nullPushNews=new PushNews();
			nullPushNews.setPushNews_content("暂无数据，请先检查你的网络");
			nullPushNews.setPushNews_down("0");
			nullPushNews.setPushNews_id("1");
			nullPushNews.setPushNews_title("暂无数据");
			nullPushNews.setPushNews_up("0");
			nullPushNews.setHasClick(0);
			listPushNews.add(nullPushNews);
		}
		if(Contacts.MessageShow.isEmpty())
			Contacts.MessageShow=listPushNews;
		System.out.println(Contacts.MessageShow.size());
	}
	
	/**
	 * 从网路更新推送数据
	 */
	public void setMessageShow(){
		System.out.println("start get data");

        // 请求推送消息的参数对
        NameValuePair pair1 = new BasicNameValuePair("Num", Contacts.RequestGetPushNews);
        NameValuePair pair2 = new BasicNameValuePair("User_name", Contacts.PersonalData.getUserName());

        System.out.println(" Contacts.PersonalData.getUserName()"+ Contacts.PersonalData.getUserName());
        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
        pairList.add(pair1);
        pairList.add(pair2);
        HttpProcess httpProcess1 = new HttpProcess(loadingDataHandler,
            	Contacts.RequestGetPushNews,null);
		
		
       httpProcess1.execute(pairList);
		
	  System.out.println("end get data");
	}
	/**
	 * 通过title来找到pushNew
	 * 2014-4-9
	 * 
	 * @author:5354xyz
	 */
	public int findNewsByTitle(String title)
	{
		int i=0;
		PushNews pushNew=new PushNews();
		List<PushNews> pushNews= Contacts.MessageShow;
		for (Iterator iterator = pushNews.iterator(); iterator.hasNext();) {
					i++;
				pushNew = (PushNews) iterator.next();
			if(pushNew.getPushNews_title().equals(title))
				//return pushNew;
				return i;

		}
		
		return 0;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("resultCode"+resultCode);
		if(Contacts.MessageShow.size()==0)//如果数据为空
		{
			getMessageShow();//设置默认的空数据
		}
		List<PushNews> pushNews= Contacts.MessageShow;
		System.out.println("MessageShow size"+Contacts.MessageShow.size());
		int ramNewsLocation = (int)(Math.random()*Contacts.MessageShow.size());
		PushNews foculsNews=pushNews.get(ramNewsLocation);
		shakeUtils = new ShakeListenerUtils(this,foculsNews,0,shakeHandler);
		if(resultCode==20)
		{
			
			setShowViewHandler.postDelayed(setShowViewRunnable,3500);//随机更换推送消息
			feedKeywordsFlow(keywordsFlow, Contacts.MessageShow);
			keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
			if(Contacts.PersonalData.getFirstLogin().equals("0") && Contacts.PersonalData.getIsLogin().equals("1"))
			{
				Contacts.PersonalData.setFirstLogin("1");
				dialog =  MyDialog.createInterestDialog(MainActivity.this, R.style.InterestDialog);
        		PostInterest post=new PostInterest(dialog);
        		post.setIntrest();
        		dialog.show();
			}
			
		}else if(resultCode == Contacts.RESULT_OK)
		{
			tospilt = Integer.parseInt(data.getExtras().getString("num"));

		}else if(resultCode == 21)
		{
			//loginFace=data.getBooleanExtra("loginFace", false);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isOnDestroy = true;
		MainActivity.this.unregisterReceiver(netCheckReceiver);
		MainActivity.this.unregisterReceiver(loadingOnlineDataReceiver);
		mSensorManager.unregisterListener(shakeUtils, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v instanceof TextView){
		String title = ((TextView) v).getText().toString();
		System.out.println("title:"+title);
		
		int  foucusNews_position=findNewsByTitle(title);
		Intent toContentIntent = new Intent();
		Bundle mBundle = new Bundle(); 
		mBundle.putInt("foculsNews", foucusNews_position);
		System.out.println("foucusNews_position:"+foucusNews_position);
		toContentIntent.putExtras(mBundle);
		toContentIntent.setClass(MainActivity.this, ContentActivity.class);
		MainActivity.this.startActivity(toContentIntent);
		overridePendingTransition(R.anim.block_move_right,
				R.anim.small_2_big);
		}
	}
	
	private static void feedKeywordsFlow(KeywordsFlow keywordsFlow, List<PushNews> PushNewss ) {
		Random random = new Random();
		List<PushNews> PushNews2KeywordsFlow=new ArrayList<PushNews> ();;
		for (int i = 0; i < KeywordsFlow.MAX; i++) {
			if(PushNewss.size() > 0){
				int ran = random.nextInt(PushNewss.size());
				PushNews tmp = PushNewss.get(ran);
				PushNews2KeywordsFlow.add(tmp);
			}
		}
		keywordsFlow.feedKeyword(PushNews2KeywordsFlow);
	}
	
	public Drawable loadImage(final String imageUrl,ImageView imageView)
	{
		Drawable imagefromweb=null;
		//生成一个加载图片的回调接口实现类
		CallbackImp callbackImp=new CallbackImp(imageView);
		//这个是从缓存中取到的
		imagefromweb=loader.loadImage(imageUrl, callbackImp,true,MainActivity.this);
		//如果缓存不为空，否则是callback直接设置好了，不经过此步骤
		if(imagefromweb !=null)
		{
			callbackImp.imageLoadedSet(imagefromweb);
		}
		
		return imagefromweb;
	}

}
