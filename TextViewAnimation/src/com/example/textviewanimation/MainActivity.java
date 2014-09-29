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
    private SensorManager mSensorManager; //����sensor������, ע��������� 
    Dialog dialog = null;
    File_SD_utils fileUtils = new File_SD_utils();
    private SlidingMenu slidingMenu;
    private View SlidingMenurootLayout=null;
    RoundedImageView touxiang=null;
    TextView name=null;
    GridView gridView=null;
    Button slidingmenu_back_but=null;
    private AsyncImageLoadUtil loader=new AsyncImageLoadUtil();
  	/*****************handler ��ʹ��**********************/
    
    /**
     * ���setShowViewRunnableÿ60s����һ����������textview��������Ϣ
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
				setMessageShow();//��������textview��������Ϣ
				// ���
				feedKeywordsFlow(keywordsFlow, Contacts.MessageShow);
			}
		}
		
	};

	/**
	 * ��������������仯ʱ
	 */
	Handler checkNetworkStateHandler=new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			System.out.println("����״̬�����仯��������");
			if(msg.arg1== 0 )
			{
				Toast.makeText(MainActivity.this, "����������...", Toast.LENGTH_SHORT).show();
			}else if (msg.arg1== 1 ||  msg.arg1== 2){
				System.out.println("�߳��и������ݣ�������");
				if (isNullData){
					Toast.makeText(MainActivity.this, "׼����������", Toast.LENGTH_SHORT).show();	
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
				// ���������Ϣ
				JsonProcess jsonProcess1 = new JsonProcess();
				boolean isJson =JsonProcess.isGoodJson(msg.obj.toString());
				if(isJson){
					Result_getNews_request result = jsonProcess1
							.getResult_getNews_request(msg.obj.toString());
					Contacts.MessageShow = result.getResult();
					// ��SD����д�뻺��
					fileUtils.writePushNewsBuffertoSD(result.getResult(),
							Contacts.LocalBufferFolder,
							Contacts.LocalPushNewsBufferdata);
					feedKeywordsFlow(keywordsFlow, Contacts.MessageShow);
				}else
				{
					Toast.makeText(MainActivity.this, "�������Ӵ��󣡣�", Toast.LENGTH_SHORT).show();
					
				}
			}else if(msg.arg2==Contacts.TIME_OUT)
			{
				Toast.makeText(MainActivity.this, "�������ӳ�ʱ�(��o��)������", Toast.LENGTH_LONG).show();
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
				//�����¼Http���󷵻صĽ��
				JsonProcess jsonProcess=new JsonProcess();
				boolean isJson =JsonProcess.isGoodJson(msg.obj.toString());
				
				if(isJson){
				String touxiangurl = jsonProcess.gettouxiangfromjson(msg.obj.toString());
				if(!touxiangurl.equals(""))
					loadImage(Contacts.BaseURL_IMAGE+touxiangurl,touxiang);
				}else
				{
					Toast.makeText(MainActivity.this, "sorry���������ݴ���", Toast.LENGTH_LONG).show();
				}
			}else if(msg.arg2==Contacts.TIME_OUT)
			{
				
				Toast.makeText(MainActivity.this, "�������ӳ�ʱ�(��o��)������", Toast.LENGTH_LONG).show();
			}		
		}
		
	};
	
	/***************************************************/
	//����һ��BroadcastReceiver����
  	NetCheckReceiver netCheckReceiver=new NetCheckReceiver(checkNetworkStateHandler);
  	LoadingOnlineDataReceiver loadingOnlineDataReceiver = new LoadingOnlineDataReceiver(loadingOnlineDataHandler);
  	/******************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		setContentView(R.layout.activity_main);
		MyApplication.getInstance().addActivity(this);
				Intent getIntent=this.getIntent();
				Bundle bundle = getIntent.getExtras();
				System.out.println("Main_onCreate()");
				
				isLogin=getIntent.getBooleanExtra("isLogin", false);
				loginFace=getIntent.getBooleanExtra("loginFace", false);
				
				//����������������Ĺ㲥��������
	            IntentFilter filter = new IntentFilter();
	            filter.addAction(Contacts.LADING_ONLINE_DATA_ACTION);    //ֻ�г�����ͬ��action�Ľ����߲��ܽ��մ˹㲥
	            registerReceiver(loadingOnlineDataReceiver, filter);
	            
				if (bundle != null) {
					System.out.println("test123456"+bundle.toString());
					initializeTextView();
					initializeSlidingMenu();
					//����һ��intentfilter���󣬹���
					IntentFilter intentfilter=new IntentFilter();
					//���action
					intentfilter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
					//ע�������
					MainActivity.this.registerReceiver(netCheckReceiver, intentfilter);
					//��ȡ�������������   
			        mSensorManager = (SensorManager) this  
			                .getSystemService(Service.SENSOR_SERVICE);  
			        //���ٶȴ�����    
			        mSensorManager.registerListener(shakeUtils,  
			                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  
			                //����SENSOR_DELAY_UI��SENSOR_DELAY_FASTEST��SENSOR_DELAY_GAME�ȣ�    
			                //���ݲ�ͬӦ�ã���Ҫ�ķ�Ӧ���ʲ�ͬ���������ʵ������趨    
			                SensorManager.SENSOR_DELAY_NORMAL);
					initializeTextView();
					setShowViewHandler.post(setShowViewRunnable);//�������������Ϣ
					getMessageShow();
					// ���
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
		// ���
		keywordsFlow.setStart(true);
		if(!isLogin && !loginFace)
		{
			Intent fromMain2LoginIntent = new Intent();
			fromMain2LoginIntent.setClass(MainActivity.this,
					LoginActivity.class);
			startActivityForResult(fromMain2LoginIntent, 100);
		}
		//��ȡ�������������   
        mSensorManager = (SensorManager) this  
                .getSystemService(Service.SENSOR_SERVICE);  
        //���ٶȴ�����    
        mSensorManager.registerListener(shakeUtils,  
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  
                //����SENSOR_DELAY_UI��SENSOR_DELAY_FASTEST��SENSOR_DELAY_GAME�ȣ�    
                //���ݲ�ͬӦ�ã���Ҫ�ķ�Ӧ���ʲ�ͬ���������ʵ������趨    
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
	//���ͻ�����˵��е�ĳһ��ѡ��ʱ���ø÷���
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			if(item.getItemId()==R.id.menu_about)
			{
				// ����һ��progressdialog�ĵ���
				dialog =  MyDialog.createAboutDialog(MainActivity.this, R.style.InterestDialog);
				dialog.show();
			}
			else if(item.getItemId()==R.id.menu_exit)
			{
				
				SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy/MM/dd    HH:mm:ss   ");       
				Date    curDate    =   new    Date(System.currentTimeMillis());//��ȡ��ǰʱ��       
				String    str    =    formatter.format(curDate);
				if(Contacts.PersonalData.getIsLogin().equals("1")){
					Contacts.PersonalData.setLoginTime(str);
					Contacts.PersonalData.setIsStorageBuffer("1");
				}
				fileUtils.writePersonInfo2SD(Contacts.PersonalData, Contacts.LocalPersonFolder, Contacts.LocalPersondata);
				
				MyApplication.getInstance().exitApp();;//��������
				//finish();
				System.exit(0);
				
			}else if(item.getItemId()==R.id.menu_account)
			{
				// ����һ��progressdialog�ĵ���
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
	 * ��ʼ��TextView
	 * 2014-4-9
	 * 
	 * @author:5354xyz
	 */
	public void initializeTextView()
	{
		//��ʼ���ؼ���
		keywordsFlow = (KeywordsFlow) findViewById(R.id.textviewShowId);
		keywordsFlow.setDuration(800l);
		keywordsFlow.setOnItemClickListener(this);
		// ���
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
	 * ��ʼ���Ҳ�˵�
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
		//���ò˵�ռ��Ļ�ı���
		slidingMenu.setBehindOffset(dm.widthPixels*20/100);
		//ʹSlidingMenu������Activity��
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMode(SlidingMenu.LEFT);//����ģʽ����Ϊ���Ҷ��в˵���RIGHT��ʾ�Ҳ˵���LEFT��ʾ��˵���LEFT_RIGH��ʾ���Ҳ˵�
		slidingMenu.setMenu(R.layout.slidingmenu);
		// ��ȡmenu��layout
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
					Toast.makeText(MainActivity.this, "ͷ��", Toast.LENGTH_SHORT).show();
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
					//��ȡͷ��
					NameValuePair pair1 = new BasicNameValuePair("Num", Contacts.RequestGettouxiang);
					NameValuePair pair2 = new BasicNameValuePair("User_name", Contacts.PersonalData.getUserName());
					List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		            pairList.add(pair1);
		            pairList.add(pair2);
		            HttpProcess httpProcess1=new HttpProcess(gettouxiangHandler,Contacts.RequestGettouxiang,null);
		            if (!Contacts.isNetworkConnected(MainActivity.this)) {
		            		Toast.makeText(MainActivity.this, "���������ӣ�����������", Toast.LENGTH_LONG).show();
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
	 * ������͵���Ϣ
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
			nullPushNews.setPushNews_content("�������ݣ����ȼ���������");
			nullPushNews.setPushNews_down("0");
			nullPushNews.setPushNews_id("1");
			nullPushNews.setPushNews_title("��������");
			nullPushNews.setPushNews_up("0");
			nullPushNews.setHasClick(0);
			listPushNews.add(nullPushNews);
		}
		if(Contacts.MessageShow.isEmpty())
			Contacts.MessageShow=listPushNews;
		System.out.println(Contacts.MessageShow.size());
	}
	
	/**
	 * ����·������������
	 */
	public void setMessageShow(){
		System.out.println("start get data");

        // ����������Ϣ�Ĳ�����
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
	 * ͨ��title���ҵ�pushNew
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
		if(Contacts.MessageShow.size()==0)//�������Ϊ��
		{
			getMessageShow();//����Ĭ�ϵĿ�����
		}
		List<PushNews> pushNews= Contacts.MessageShow;
		System.out.println("MessageShow size"+Contacts.MessageShow.size());
		int ramNewsLocation = (int)(Math.random()*Contacts.MessageShow.size());
		PushNews foculsNews=pushNews.get(ramNewsLocation);
		shakeUtils = new ShakeListenerUtils(this,foculsNews,0,shakeHandler);
		if(resultCode==20)
		{
			
			setShowViewHandler.postDelayed(setShowViewRunnable,3500);//�������������Ϣ
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
		//����һ������ͼƬ�Ļص��ӿ�ʵ����
		CallbackImp callbackImp=new CallbackImp(imageView);
		//����Ǵӻ�����ȡ����
		imagefromweb=loader.loadImage(imageUrl, callbackImp,true,MainActivity.this);
		//������治Ϊ�գ�������callbackֱ�����ú��ˣ��������˲���
		if(imagefromweb !=null)
		{
			callbackImp.imageLoadedSet(imagefromweb);
		}
		
		return imagefromweb;
	}

}
