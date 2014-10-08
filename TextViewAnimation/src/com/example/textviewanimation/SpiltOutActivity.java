package com.example.textviewanimation;

import imageShow.Rotate3dAnimation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import personInfo.MyAdapter;
import personInfo.MyApplication;
import personInfo.Result_spilt_request;
import personInfo.SpiltModel;
import pulltorefresh_listview.OnRefreshListener;
import pulltorefresh_listview.RefreshListView;
import android.app.Dialog;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.utils.File_SD_utils;
import com.example.utils.HttpProcess;
import com.example.utils.JsonProcess;
import com.example.utils.MyDialog;
import com.example.utils.SildingFinishLayout;
import com.example.utils.SildingFinishLayout.OnSildingFinishListener;

public class SpiltOutActivity extends Activity {

	static final int NOT_FIRST_TIME_FROM_MAIN = -1;
	static final int POST_SPILT = 168;
	/**
	 * 控制是否动画显示listview
	 */
	int chenkTimes = 0;
	/**
	 * 控制是否显示正在加载的弹出框
	 */
	int checkNum=0;
	private RefreshListView mPullRefreshListView;
	private MyAdapter mAdapter;
	private String getSpiltType =Contacts.GetSpiltTypeDefualt;
	private Button toSpiltOutButton=null;
	File_SD_utils fileUtils = new File_SD_utils();
	private Dialog dialog = null;
	private Handler spiltOutHandler=null;
	ImageView mTopImageView =null;//回到顶部按钮
	SildingFinishLayout imgEntryView=null;
	boolean onStop = false;
	View lastview =null;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//告诉系统要自己设置bar的样式
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.pull_to_refresh_list);

		if(!Contacts.isNetworkConnected(SpiltOutActivity.this) && 
				Contacts.SpiltLists.size()<=0 ){
			Toast.makeText(SpiltOutActivity.this, "请先链接网络", Toast.LENGTH_SHORT).show();
			finish();
		}

		imgEntryView= (SildingFinishLayout) findViewById(R.id.pulltorefresh_sildingFinishLayout);
		imgEntryView
		.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish(int type) {
				if (type == 1)
					SpiltOutActivity.this.finish();
				if (type ==2){
					onStop = true;
					Intent fromSpiltOuttoPostIntent = new Intent();
					fromSpiltOuttoPostIntent.setClass(SpiltOutActivity.this,
							PostActivity.class);
					startActivity(fromSpiltOuttoPostIntent);
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					
				}
					
			}
		});
		imgEntryView.setSlidingDirection(3);
		toSpiltOutButton=(Button)findViewById(R.id.SpiltOut);
		toSpiltOutButton.setOnClickListener(new ToSpiltOutButtonOnclickListener());
		
		spiltOutHandler=new Handler() 
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.obj!="" && msg.arg1 == Integer.valueOf(Contacts.RequestgetSpilt).intValue() && msg.what==1)
				{
					//处理登录Http请求返回的结果
					JsonProcess jsonProcess=new JsonProcess();
					boolean isJson =JsonProcess.isGoodJson(msg.obj.toString());
					//System.out.println("isJson:"+isJson);
					if(isJson){
					Result_spilt_request result = jsonProcess.getResult_getSplits_request(msg.obj.toString());
					if(getSpiltType.equals(Contacts.GetSpiltTypeDefualt)){
							Contacts.SpiltLists=result.getResult();
							// 向SD卡中写入缓存
							fileUtils.writeSpiltBuffertoSD(result.getResult(),
									Contacts.LocalBufferFolder,
									Contacts.LocalSpiltBufferdata);
						}else if(getSpiltType.equals(Contacts.GetSpiltTypeUp) && !result.getStatus().equals("null"))
						{
							List<SpiltModel> resultList =new ArrayList<SpiltModel> ();
							resultList=result.getResult();
							for(int i=0;i<resultList.size();i++)
							{
								Contacts.SpiltLists.add(0, resultList.get(resultList.size()-1-i));
							}
						}else if(getSpiltType.equals(Contacts.GetSpiltTypeDown)  && !result.getStatus().equals("null"))
						{
							List<SpiltModel> resultList =new ArrayList<SpiltModel> ();
							resultList=result.getResult();
							for(int i=0;i<resultList.size();i++)
							{
								Contacts.SpiltLists.add(resultList.get(i));
							}
						}
					}else
					{
						dialog.dismiss();
						Toast.makeText(SpiltOutActivity.this, "网络连接错误！！", Toast.LENGTH_SHORT).show();
						if(chenkTimes == 0)
							finish();
					}
					if(chenkTimes==0){
						if(checkNum==0)
							dialog.dismiss();
						Animation animation =(Animation)AnimationUtils.loadAnimation(SpiltOutActivity.this, R.anim.list_anim);
						LayoutAnimationController lac = new LayoutAnimationController(animation);
						lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
						mPullRefreshListView.setLayoutAnimation(lac);
						mAdapter.setList(Contacts.SpiltLists);
						mAdapter.notifyDataSetChanged();
						mPullRefreshListView.onRefreshFinish();
						chenkTimes++;
					}else
					{
						//System.out.println("chenkTimes:"+chenkTimes);
						mAdapter.setList( Contacts.SpiltLists);
						mAdapter.notifyDataSetChanged();
						mPullRefreshListView.onRefreshFinish();
					}
				}else if(msg.arg2==Contacts.TIME_OUT)
				{
					if(dialog != null && dialog.isShowing())
						dialog.dismiss();
					Toast.makeText(SpiltOutActivity.this, "网络连接超时喔(⊙o⊙)...请检查你的网络", Toast.LENGTH_LONG).show();
					finish();
				}
				
				if(msg.what==NOT_FIRST_TIME_FROM_MAIN)
				{
					mAdapter.setList(Contacts.SpiltLists);
					mAdapter.notifyDataSetChanged();
					mPullRefreshListView.onRefreshFinish();
				}
			}
			
		};
		
		
		MyApplication.getInstance().addActivity(this);
		Intent intentfromMain = getIntent();
		checkNum=intentfromMain.getIntExtra("num", 0);
		if(checkNum==0)
		{
			//弹出正在加载框
		     dialog =  MyDialog.createLoadingDialog(SpiltOutActivity.this,R.style.custom_dialog,"加载中...");
		     dialog.show();
		     //网络操作
			getSpilts(Contacts.GetSpiltTypeDefualt);
		}else if(checkNum==1)
		{
			Message message = new Message();
			message.what=NOT_FIRST_TIME_FROM_MAIN;
			spiltOutHandler.sendMessage(message);
		}
		
		mPullRefreshListView = (RefreshListView) findViewById(R.id.pull_refresh_list);
		mTopImageView = (ImageView) this.findViewById(R.id.lv_backtotop);
		mPullRefreshListView.setBackToTopView(mTopImageView);
		mAdapter =  new MyAdapter(SpiltOutActivity.this, Contacts.SpiltLists,imgEntryView);  
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener(){
			int larg2=-1;
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				System.out.println("arg2:"+arg2+" arg3:"+arg3);
				Animation animationgone = AnimationUtils.loadAnimation(SpiltOutActivity.this, R.anim.base_slide_right_out);
				if(arg1.findViewById(R.id.spiltcomment).getVisibility() == View.GONE){
					
					arg1.findViewById(R.id.spiltcomment).setVisibility(View.VISIBLE);
					Animation animation = AnimationUtils.loadAnimation(SpiltOutActivity.this, R.anim.push_left_in);
					arg1.findViewById(R.id.spiltcomment).startAnimation(animation);
					if(lastview != null)
						System.out.println(lastview.findViewById(R.id.spiltcomment).getVisibility()+"|"+View.VISIBLE);
					if(lastview != null && lastview.findViewById(R.id.spiltcomment).getVisibility() == View.VISIBLE 
							&& larg2 != arg2){
						
						lastview.findViewById(R.id.spiltcomment).startAnimation(animationgone);
						lastview.findViewById(R.id.spiltcomment).setVisibility(View.GONE);
						}
					larg2=arg2;
					lastview=arg1;
					}else
					{
						arg1.findViewById(R.id.spiltcomment).startAnimation(animationgone);
						arg1.findViewById(R.id.spiltcomment).setVisibility(View.GONE);
					}
				
			}
			
		});
		
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {

			public void onRefresh() {
				// Do work to refresh the list here.
				getSpilts(Contacts.GetSpiltTypeUp);
			}

			@Override
			public void onLoadMoring() {
				// TODO Auto-generated method stub
				getSpilts(Contacts.GetSpiltTypeDown);
			}

			@Override
			public void onOutitem(int itemNum, int from) {
				// TODO Auto-generated method stub
				System.out.println(itemNum);
				int wantedPosition = itemNum; // Whatever position you're looking for
				int firstPosition = mPullRefreshListView.getFirstVisiblePosition() ; // - mPullRefreshListView.getHeaderViewsCount() This is the same as child #0
				int wantedChild = wantedPosition - firstPosition;
				if (wantedChild < 0 || wantedChild >= mPullRefreshListView.getChildCount()) {
				  Log.w("tag", "Unable to get view for desired position, because it's not being displayed on screen.");
				  return;
				}
				// Could also check if wantedPosition is between listView.getFirstVisiblePosition() and listView.getLastVisiblePosition() instead.
				View view = mPullRefreshListView.getChildAt(wantedChild);
				
				Animation procAnim = null;
				if(from == 1)
				{
					procAnim=new TranslateAnimation(0, 0,20,0);
				}else
				{
					procAnim=new TranslateAnimation(0, 0,-20,0);
				}
				procAnim.setDuration(700);
				procAnim.setFillAfter(true);
				view.startAnimation(procAnim);
				
			}
		});
		
		
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("onResume:"+onStop);
		if(onStop){
			imgEntryView.scrollOrigin(true);
			onStop = false;
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}


	class ToSpiltOutButtonOnclickListener implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent fromSpiltOuttoPostIntent = new Intent();
			fromSpiltOuttoPostIntent.setClass(SpiltOutActivity.this,
					PostActivity.class);
			startActivityForResult(fromSpiltOuttoPostIntent,POST_SPILT);
			overridePendingTransition(R.anim.push_left_in,R.anim.base_slide_remain);
		}
		
	}


	public void back(View v)
	{
		Intent data = new Intent();
		Bundle mBundle = new Bundle();
		mBundle.putParcelableArrayList("ss",
				(ArrayList<? extends Parcelable>) Contacts.MessageShow);
		data.putExtras(mBundle);
        //把返回数据存入Intent
        data.putExtra("num", "1");
        //设置返回g给mainactivity的数据
        SpiltOutActivity.this.setResult(Contacts.RESULT_OK, data);
        //关闭Activity
		SpiltOutActivity.this.finish();
		SpiltOutActivity.this.overridePendingTransition(0, R.anim.base_slide_right_out);
	}
	
	public void getSpilts(String getSpiltType_fun)
	{
		getSpiltType = getSpiltType_fun;
		//请求推送消息的参数对
		NameValuePair pair1 = new BasicNameValuePair("Num", Contacts.RequestgetSpilt);
		NameValuePair pair2 = new BasicNameValuePair("ram", String.valueOf((int)(Math.random()*1000)));
		NameValuePair pair3 = new BasicNameValuePair("GetSpiltType", getSpiltType);
		NameValuePair pair4 =null;
		if(getSpiltType.equals(Contacts.GetSpiltTypeDown)){
			 pair4 = new BasicNameValuePair("Id", Contacts.SpiltLists.get(Contacts.SpiltLists.size()-1).getSplilt_id());
		}else if(getSpiltType.equals(Contacts.GetSpiltTypeUp))
		{
			pair4 = new BasicNameValuePair("Id", Contacts.SpiltLists.get(0).getSplilt_id());
		}

        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
        pairList.add(pair1);
        pairList.add(pair2);
        pairList.add(pair3);
        if(pair4 != null && !getSpiltType.equals(Contacts.GetSpiltTypeDefualt))
        {
        	 pairList.add(pair4);
        }
        HttpProcess httpProcess1=new HttpProcess(spiltOutHandler,Contacts.RequestgetSpilt,null);
        if (!Contacts.isNetworkConnected(SpiltOutActivity.this)){
    		Toast.makeText(SpiltOutActivity.this, "无网络连接，请设置网络", Toast.LENGTH_LONG).show();
    		// 从缓存中读取数据
    		try {
    				Contacts.SpiltLists = fileUtils.loadingSpilts(Contacts.LocalBufferFolder,
    						Contacts.LocalSpiltBufferdata);
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		
    	}
    	else{
    		httpProcess1.execute(pairList);
        }
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Contacts.RESULT_OK)
		{
			//发表成功
			if(data.getExtras().getString("POST").equals("1"))
			{
				getSpilts(Contacts.GetSpiltTypeUp);
			}else if(data.getExtras().getString("POST").equals("0"))//返回
			{
				
			}
		}
		
	}
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
	    if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
	    	if(imgEntryView.findViewById(R.id.content_list_RelativeLayout).getVisibility() == View.VISIBLE){
	    			finish();
	    			overridePendingTransition(0, R.anim.base_slide_right_out);
				}
			else
			{
				float centerX = imgEntryView.getWidth() / 2f;
				float centerY = imgEntryView.getHeight() / 2f;
				//
				final Rotate3dAnimation rotation = new Rotate3dAnimation(360, 270, centerX,
						centerY, 310.0f, true);
				//
				rotation.setDuration(500);
				//
				rotation.setFillAfter(true);
				rotation.setInterpolator(new AccelerateInterpolator());
				//
				rotation.setAnimationListener(new TurnToListView(imgEntryView));
				imgEntryView.startAnimation(rotation);
			}
	        return true; 
	    }    
	return super.onKeyDown(keyCode, event); 
	}
	class TurnToListView implements AnimationListener {
		SildingFinishLayout layout=null;
		public TurnToListView(SildingFinishLayout imgEntryView)
		{
			this.layout=imgEntryView;
		}
		@Override
		public void onAnimationStart(Animation animation) {
		}

		/**
		 * 
		 */
		@Override
		public void onAnimationEnd(Animation animation) {
			
			float centerX = layout.getWidth() / 2f;
			float centerY = layout.getHeight() / 2f;
			RelativeLayout content_list_RelativeLayout=(RelativeLayout)layout.findViewById(R.id.content_list_RelativeLayout);
			RelativeLayout image_show_RelativeLayout=(RelativeLayout)layout.findViewById(R.id.image_show_RelativeLayout);
			
			image_show_RelativeLayout.setVisibility(View.GONE);
			
			content_list_RelativeLayout.setVisibility(View.VISIBLE);
			content_list_RelativeLayout.requestFocus();
			
			final Rotate3dAnimation rotation = new Rotate3dAnimation(90, 0, centerX, centerY,
					310.0f, false);
			
			rotation.setDuration(500);
			
			rotation.setFillAfter(true);
			rotation.setInterpolator(new AccelerateInterpolator());
			layout.startAnimation(rotation);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

	}
}
