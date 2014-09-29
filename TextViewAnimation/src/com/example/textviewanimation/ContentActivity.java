package com.example.textviewanimation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.example.utils.GestureListener;
import com.example.utils.GestureListener.OnFlingListener;
import com.example.utils.File_SD_utils;
import com.example.utils.HttpProcess;
import com.example.utils.JsonProcess;
import com.example.utils.MyDialog;
import com.example.utils.ShakeListenerUtils;

import personInfo.ContentViewAdapter;
import personInfo.MyApplication;
import personInfo.PushNews;
import personInfo.Result_getNews_request;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.aphidmobile.flip.FlipViewController;
public class ContentActivity extends Activity  
{
	private FlipViewController flipView;
	Button backButton = null;
	//Button refleshButton = null;
	/*
	
	private ShakeListenerUtils shakeUtils;  
    private SensorManager mSensorManager; //����sensor������, ע��������� 
    File_SD_utils fileUtils = new File_SD_utils();
    List<PushNews> pushNews= Contacts.MessageShow;
   //���Ƶ�ʶ��
    GestureDetector mGesture = null;  
    
    Handler changePushNews=new Handler()
    {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.obj!="" && msg.arg1 == Integer.valueOf(Contacts.RequestGetPushNews).intValue() && msg.what==1)
			{
				// ���������Ϣ
				JsonProcess jsonProcess1 = new JsonProcess();
				Result_getNews_request result = jsonProcess1
						.getResult_getNews_request(msg.obj.toString());
				
				Contacts.MessageShow = result.getResult();
				pushNews = Contacts.MessageShow;
				// ��SD����д�뻺��
				fileUtils.writePushNewsBuffertoSD(result.getResult(),
						Contacts.LocalBufferFolder,
						Contacts.LocalPushNewsBufferdata);
				//setMessageShow();
			}else if(msg.arg2==Contacts.TIME_OUT)
			{
				Toast.makeText(ContentActivity.this, "���粻�ȶ��(��o��)������", Toast.LENGTH_LONG).show();
			}
		}
    	
    };
    
    
	
	Handler shake_con_Handler = new Handler() 
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.arg1 == Contacts.SHAKE_OK_C)
			{
				pushNewsFoucus = (PushNews)msg.obj; 
				System.out.println("drg" + pushNewsFoucus + "contentNews");
				textViewTitle.setText(pushNewsFoucus.getPushNews_title());
				textViewContent.setText(pushNewsFoucus.getPushNews_content());
				upButton.setText("��" + pushNewsFoucus.getPushNews_up());
				downButton.setText("��ש" + pushNewsFoucus.getPushNews_down());
			}
		}
		
	};
*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����ϵͳҪ�Լ�����bar����ʽ
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		setContentView(R.layout.activity_content);

		// ����titleBar����ʽ
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		//		R.layout.titlebar_content);
		MyApplication.getInstance().addActivity(this);
		
		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		int pushNewsFoucus_id = getIntent()
		.getIntExtra("foculsNews", 0);
		
		flipView = new FlipViewController(this, FlipViewController.HORIZONTAL);
		
		System.out.println("foucusNews_position_position_ContentActivity:"+pushNewsFoucus_id);
	    flipView.setAdapter(new ContentViewAdapter(this),pushNewsFoucus_id);

	    setContentView(flipView);
	    //backButton = (Button) findViewById(R.id.contentToolbar_back_but);
		//backButton.setOnClickListener(new BackButtonOnclickListener());
		/*
		//��ʼ������
		init();
		
		//���ƵĲ���
		GestureListener gl=new GestureListener(this);
		gl.setOnFlingListener(new OnFlingListener(){
			int j=0;
			
			@Override
			public void OnFlingLeft() {
				// TODO Auto-generated method stub
						System.out.println("j:"+j+"size:"+pushNews.size());
						j=pushNews.indexOf(pushNewsFoucus);
						if((j+1)<pushNews.size() && j!=-1){
							j+=1;
							pushNewsFoucus=pushNews.get(j);
							textViewTitle.setText(pushNewsFoucus.getPushNews_title());
							textViewContent.setText(pushNewsFoucus.getPushNews_content());
							upButton.setText("��" + pushNewsFoucus.getPushNews_up());
							downButton.setText("��ש" + pushNewsFoucus.getPushNews_down());
						}
						else if(j==-1)
						{
							j=0;
							pushNewsFoucus=pushNews.get(0);
							textViewTitle.setText(pushNewsFoucus.getPushNews_title());
							textViewContent.setText(pushNewsFoucus.getPushNews_content());
							upButton.setText("��" + pushNewsFoucus.getPushNews_up());
							downButton.setText("��ש" + pushNewsFoucus.getPushNews_down());
						}
						else
							
						{
							dialog =  MyDialog.createLoadingDialog(ContentActivity.this,R.style.custom_dialog,"���Եȡ���"); 
							dialog.show();
							// ����������Ϣ�Ĳ�����
							NameValuePair pair1 = new BasicNameValuePair("Num", Contacts.RequestGetPushNews);
							NameValuePair pair2 = new BasicNameValuePair("User_name",Contacts.PersonalData.getUserName());

							List<NameValuePair> pairList = new ArrayList<NameValuePair>();
							pairList.add(pair1);
							pairList.add(pair2);
							HttpProcess httpProcess1 = new HttpProcess(changePushNews,
									Contacts.RequestGetPushNews);
							httpProcess1.execute(pairList);
							dialog.dismiss();
							j=-1;
						}
				
			}

			@Override
			public void OnFlingRight() {
				// TODO Auto-generated method stub
				System.out.println("���һ���=j"+j);
				if(j-1>=0)
				{
					j-=1;
					pushNewsFoucus=pushNews.get(j);
					textViewTitle.setText(pushNewsFoucus.getPushNews_title());
					textViewContent.setText(pushNewsFoucus.getPushNews_content());
					upButton.setText("��" + pushNewsFoucus.getPushNews_up());
					downButton.setText("��ש" + pushNewsFoucus.getPushNews_down());
				}else
				{
					j=pushNews.size()-1;
				}
			}
		});
		
		mGesture = new GestureDetector(this, gl);
		*/
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		
		/*
		int ramNewsLocation = (int)(Math.random()*pushNews.size());
		PushNews foculsNews=pushNews.get(ramNewsLocation);
		shakeUtils = new ShakeListenerUtils(this,foculsNews,1,shake_con_Handler);
		//��ȡ�������������   
        mSensorManager = (SensorManager) this  
                .getSystemService(Service.SENSOR_SERVICE);  
        //���ٶȴ�����    
        mSensorManager.registerListener(shakeUtils,  
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  
                //����SENSOR_DELAY_UI��SENSOR_DELAY_FASTEST��SENSOR_DELAY_GAME�ȣ�    
                //���ݲ�ͬӦ�ã���Ҫ�ķ�Ӧ���ʲ�ͬ���������ʵ������趨    
                SensorManager.SENSOR_DELAY_GAME);*/
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.content, menu);
		return true;
	}
	
	//���ͻ�����˵��е�ĳһ��ѡ��ʱ���ø÷���
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==R.id.action_settings)
		{
			Intent fromCont2MainIntent = new Intent();
			Bundle bundle = new Bundle(); 
			bundle.putString("c2m", "true");
			fromCont2MainIntent.putExtras(bundle);
			fromCont2MainIntent.setClass(ContentActivity.this,
					MainActivity.class);
			startActivity(fromCont2MainIntent);
		}
				
		return super.onOptionsItemSelected(item);
	}
/*
	//ʹ��GestureDetector��Ҫ��View����дonTouchEvent�¼�
	public boolean onTouchEvent(MotionEvent event) {

		//��ô���������
		float x = event.getX();
		float y = event.getY();
		//System.out.println("x:"+x+"|y:"+y);
		switch (event.getAction()) 
			{
				//������Ļʱ��
				case MotionEvent.ACTION_DOWN:

				break;
				//�������ƶ�ʱ��
				case MotionEvent.ACTION_MOVE:

				break;
				//��ֹ����ʱ��
				case MotionEvent.ACTION_UP:
				break;
				}
		return mGesture.onTouchEvent(event);
			}
	
	  

	

	class BackButtonOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
			//MyApplication.getInstance().exitApp();
			
		}

	}*/
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		//mSensorManager.unregisterListener(shakeUtils, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

}
