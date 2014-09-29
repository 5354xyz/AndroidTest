package com.example.textviewanimation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import personInfo.MyApplication;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.utils.File_SD_utils;
import com.example.utils.HttpProcess;
import com.example.utils.ImageTools;
import com.example.utils.JsonProcess;
import com.example.utils.MyDialog;
import com.example.utils.MyLocationListener;
import com.example.utils.SildingFinishLayout;
import com.example.utils.SildingFinishLayout.OnSildingFinishListener;

public class PostActivity extends Activity {

	private EditText mEditText = null;
	private TextView mTextView = null;
	private TextView locationTextView=null;
	private ImageButton photoButton=null;
	private ImageButton cameraButton=null;
	private GridView picView =null;
	private ImageAdapter gridviewAdapter=null; 
	private List <Bitmap> mThumbIds =new ArrayList <Bitmap> ();//用来存放图片
	Vector <String> attachment=new Vector <String>();//用来存放图片的路径
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = null;
	private Handler postHandler=null;
	private Dialog dialog = null;
	File_SD_utils fileUtils = new File_SD_utils();

	private static final int MAX_COUNT = 140;
	private static final int CHOOSE_PHOTO = 106;
	private static final int TAKE_PICTURE = 107;
	public static final String PostActivityTAG="PostActivity";
	public static String uploadUrl = "http://www.shopping-100.com/xyz/testupload.php?Path=picSpilt";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_post);
		
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.post_act_sildingFinishLayout);
		mSildingFinishLayout
				.setOnSildingFinishListener(new OnSildingFinishListener() {

					@Override
					public void onSildingFinish() {
						PostActivity.this.finish();
					}
				});
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
	    
	    mLocationClient.setAK("50b6248aed30effde0d0cba9536b8524");
	    LocationClientOption option = new LocationClientOption();
	    option.setOpenGps(true);
	    option.setAddrType("all");//返回的定位结果包含地址信息
	    option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
	    option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
	    option.disableCache(true);//禁止启用缓存定位
	    option.setPoiNumber(5);    //最多返回POI个数   
	    option.setPoiDistance(1000); //poi查询距离        
	    option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息        
	    mLocationClient.setLocOption(option);
	    MyApplication.getInstance().addActivity(this);
	    
		mEditText = (EditText) findViewById(R.id.et_content);
		mEditText.addTextChangedListener(mTextWatcher);
		mEditText.setSelection(mEditText.length()); // 将光标移动最后一个字符后面
		
		cameraButton =(ImageButton)findViewById(R.id.post_camera);
		photoButton =(ImageButton)findViewById(R.id.post_photo);
		cameraButton.setOnClickListener(new PicOnclickListener());
		photoButton.setOnClickListener(new PicOnclickListener());
		picView =(GridView) findViewById(R.id.post_pic_gridview);
		gridviewAdapter=new ImageAdapter(this);
		picView.setAdapter(gridviewAdapter);  
        //单击GridView元素的响应  
		picView.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                //弹出单击的GridView元素的位置  
                Toast.makeText(PostActivity.this,"点击删除", Toast.LENGTH_SHORT).show(); 
                
                String[] strarray=ImageTools.getpathname(attachment.get(position));
                System.out.println(strarray[0]+"$$$$"+strarray[1]+"$$$$$$"+attachment.get(position));
                ImageTools.deletePhotoAtPathAndName(strarray[0], strarray[1]);
                mThumbIds.remove(position);
                attachment.remove(position);
                gridviewAdapter.notifyDataSetChanged();
            }  
        }); 
		mTextView = (TextView) findViewById(R.id.count);
		setLeftCount();
		
		
		locationTextView= (TextView) findViewById(R.id.post_location);
		myListener=new MyLocationListener(locationTextView);
		mLocationClient.registerLocationListener( myListener );    //注册监听函数
		mLocationClient.start();
		
		//传给Http的handler
		postHandler = new Handler() 
				{

					@Override
					public void handleMessage(Message msg) {
						// TODO Auto-generated method stub
						super.handleMessage(msg);
						if(msg.obj!="" && msg.arg1 == Integer.valueOf(Contacts.RequestPushSpilt).intValue())
						{
							//处理登录Http请求返回的结果
							JsonProcess jsonProcess=new JsonProcess();
							
							String isSpilt = jsonProcess.checkStatus(msg.obj.toString());
							
							//Contacts.PersonalData.setIsLogin(isLogin)
							if(isSpilt.equals("true"))
							{
								SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy/MM/dd    HH:mm:ss   ");       
								Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
								String    str    =    formatter.format(curDate);
								System.out.println(str);
								Intent data = new Intent();
								data.putExtra("POST", "1");
								Toast.makeText(PostActivity.this, "发表成功", Toast.LENGTH_LONG).show();
								dialog.dismiss();
								setResult(Contacts.RESULT_OK, data);
								PostActivity.this.finish();
							}else
							{
								dialog.dismiss();
								Toast.makeText(PostActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
							}
						}else if(msg.arg2==Contacts.TIME_OUT)
						{
							dialog.dismiss();
							Toast.makeText(PostActivity.this, "网络连接超时喔(⊙o⊙)...请检查你的网络", Toast.LENGTH_LONG).show();
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
				dialog =  MyDialog.createAboutDialog(PostActivity.this, R.style.InterestDialog);
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
				dialog =  MyDialog.createAccountDialog(PostActivity.this, R.style.InterestDialog);
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
						Intent fromPost2LoginIntent = new Intent();
						fromPost2LoginIntent.putExtra("from",PostActivityTAG);
						fromPost2LoginIntent.setClass(PostActivity.this,
								LoginActivity.class);
						startActivity(fromPost2LoginIntent);
						dialog.dismiss();
					}
				});
				dialog.show();
			}
			return super.onOptionsItemSelected(item);
		}
	
	private TextWatcher mTextWatcher = new TextWatcher() {

		private int editStart;

		private int editEnd;

		public void afterTextChanged(Editable s) {
			editStart = mEditText.getSelectionStart();
			editEnd = mEditText.getSelectionEnd();

			// 先去掉监听器，否则会出现栈溢出
			mEditText.removeTextChangedListener(mTextWatcher);

			// 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
			// 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
			while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
				s.delete(editStart - 1, editEnd);
				editStart--;
				editEnd--;
			}
			mEditText.setText(s);
			mEditText.setSelection(editStart);

			// 恢复监听器
			mEditText.addTextChangedListener(mTextWatcher);

			setLeftCount();
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	};

	/**
	 * 计算发送内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
	 * 
	 * @param c
	 * @return
	 */
	private long calculateLength(CharSequence c) {
		double len = 0;
		for (int i = 0; i < c.length(); i++) {
			int tmp = (int) c.charAt(i);
			if (tmp > 0 && tmp < 127) {
				len += 0.5;
			} else {
				len++;
			}
		}
		return Math.round(len);
	}

	/**
	 * 刷新剩余输入字数,最大值新浪微博是140个字，人人网是200个字
	 */
	private void setLeftCount() {
		mTextView.setText(String.valueOf((MAX_COUNT - getInputCount())));
	}

	/**
	 * 获取用户输入的分享内容字数
	 * 
	 * @return
	 */
	private long getInputCount() {
		return calculateLength(mEditText.getText().toString());
	}
	
	/**
	 * 返回销毁当前activity
	 * 
	 */
	public void back(View v)
	{
		Intent data = new Intent();
		data.putExtra("POST", "0");
		setResult(Contacts.RESULT_OK, data);
		PostActivity.this.finish();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}
	
	/**
	 * 发送的处理事件
	 * 2013-11-17
	 * 
	 * @author:5354xyz
	 */
	public void send(View v)
	{
		if(Contacts.PersonalData.getIsLogin().equals("1")){
		//请求推送消息的参数对
		NameValuePair pair1 = new BasicNameValuePair("Num", Contacts.RequestPushSpilt);
		NameValuePair pair2 = new BasicNameValuePair("User_name", Contacts.PersonalData.getUserName());
		NameValuePair pair3 = new BasicNameValuePair("Shit", mEditText.getText().toString());
		String location=locationTextView.getText().toString();
		System.out.println("location:"+location);
		NameValuePair pair4 = new BasicNameValuePair("Location", location);
		//NameValuePair pair4 = new BasicNameValuePair("Location", location);

        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
        pairList.add(pair1);
        pairList.add(pair2);
        pairList.add(pair3);
        pairList.add(pair4);
        
        for(int i=0;i<attachment.size();i++)
        {
        	String []name =ImageTools.getpathname( attachment.get(i));
        	pairList.add(new BasicNameValuePair("picSpilt[]","picSpilt/"+name[1]));
        }
        HttpProcess httpProcess1=new HttpProcess(postHandler,Contacts.RequestPushSpilt,attachment);
        if (!Contacts.isNetworkConnected(PostActivity.this)){
        		Toast.makeText(PostActivity.this, "无网络连接，请设置网络", Toast.LENGTH_LONG).show();
        	}
        	else{
        		if(mEditText.getText().toString().equals(""))
        		{
        			//通过产生消息提醒
        			Toast.makeText(PostActivity.this, "内容不能为空",Toast.LENGTH_SHORT).show();
        			
        		}else{
        			httpProcess1.execute(pairList);
        
        			//弹出正在登录的
        			dialog =  MyDialog.createLoadingDialog(PostActivity.this,R.style.custom_dialog,"吐槽中。。");
        			dialog.show();
        		}
        	}
        }else
        {
        	Toast.makeText(PostActivity.this, "要先登录 ，才能吐槽(⊙o⊙)哦", Toast.LENGTH_LONG).show();
        	Intent fromPost2LoginIntent = new Intent();
        	//讲数据加入到Intent当中去 
        	fromPost2LoginIntent.putExtra("from",PostActivityTAG);
			fromPost2LoginIntent.setClass(PostActivity.this,
					LoginActivity.class);
			startActivity(fromPost2LoginIntent);
        }
	}
	
	public void location(View v)
	{
		locationTextView.setText("");
		if (mLocationClient != null && mLocationClient.isStarted())
			  mLocationClient.requestLocation();
			else 
			     System.out.println( "locClient is null or not started");
	}

	 @Override
	  public void onWindowFocusChanged(boolean hasFocus) {
	 if (hasFocus ) {
	        ((RelativeLayout)findViewById(R.id.ll_facechoose)).setVisibility(View.GONE);
 
	        System.out.println( "hasFocus"+hasFocus);
	    } else {

	        ((RelativeLayout)findViewById(R.id.ll_facechoose)).setVisibility(View.VISIBLE);
	        System.out.println( "hasFocus"+hasFocus);
	    }
	  super.onWindowFocusChanged(hasFocus);
	}
	 
	 class PicOnclickListener implements OnClickListener
	 {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.post_photo :
					if(mThumbIds.size() < 4){
						Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
						openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
						startActivityForResult(openAlbumIntent, CHOOSE_PHOTO);
					}else
					{
						Toast.makeText(PostActivity.this, "图片不要超过4张哦~~~", Toast.LENGTH_SHORT).show();
					}
					break;
				case R.id.post_camera:
					if(mThumbIds.size() < 4){
						Uri imageUri = null;
						String fileName = "temp.jpg";
						Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()+File.separator+Contacts.LocalBufferImageFolder,fileName));
						//指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
						openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
						startActivityForResult(openCameraIntent, TAKE_PICTURE);
					}else
					{
						Toast.makeText(PostActivity.this, "图片不要超过4张哦~~~", Toast.LENGTH_SHORT).show();
					}
					break;
			}
		}
		 
	 }
	 
	 @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK) {
				switch (requestCode) {
					case TAKE_PICTURE:
						//将保存在本地的图片取出并缩小后显示在界面上
						Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+File.separator+Contacts.LocalBufferImageFolder+"/temp.jpg");
						Bitmap newBitmap = ImageTools.reZoomBmp(bitmap, 400);
						Log.i("bitmap.getWidth()", String.valueOf(bitmap.getWidth()));
						//由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
						bitmap.recycle();
						
						//将处理过的图片显示在界面上，并保存到本地
						mThumbIds.add(newBitmap);
						gridviewAdapter.notifyDataSetChanged();
						//保存图片到本地
						StringBuilder sb = new StringBuilder();
						String userName="dfvgd";
						userName=Contacts.PersonalData.getUserName();
						System.out.println("userName:"+userName);
						sb.append(userName);
						sb.append("_");
						sb.append(String.valueOf(System.currentTimeMillis()));
						sb.append(mThumbIds.size()+1);
						//sb.append(String.valueOf(System.currentTimeMillis()));
						sb.append("_");
						sb.append(mThumbIds.size()+1);
						sb.append(".png");
						String picName =sb.toString();
						Log.i("picName---sb", picName);
						
						ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+Contacts.LocalBufferImageFolder, picName);
						attachment.add(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+Contacts.LocalBufferImageFolder+File.separator+picName);
						break;
					case CHOOSE_PHOTO:
						ContentResolver resolver = getContentResolver();
						//照片的原始资源地址
						Uri originalUri = data.getData(); 
			            try {
			            	//使用ContentProvider通过URI获取原始图片
							Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
							if (photo != null) {
								//为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
								Bitmap smallBitmap = ImageTools.reZoomBmp(photo, 400);
								Log.i("bitmap.getWidth()", String.valueOf(smallBitmap.getWidth()));
								//释放原始图片占用的内存，防止out of memory异常发生
								photo.recycle();
								//将处理过的图片显示在界面上，并保存到本地
								mThumbIds.add(smallBitmap);
								gridviewAdapter.notifyDataSetChanged();
								//保存图片到本地
								StringBuilder sb01 = new StringBuilder();
								String userName01="";
								userName01=Contacts.PersonalData.getUserName();
								sb01.append(userName01);
								sb01.append("_");
								sb01.append(String.valueOf(System.currentTimeMillis()));
								sb01.append("_");
								sb01.append(mThumbIds.size()+1);
								//sb.append(String.valueOf(System.currentTimeMillis()));
								sb01.append(".png");
								String picName01 =sb01.toString();
								Log.i("picName---sb", picName01);
								
								ImageTools.savePhotoToSDCard(smallBitmap, Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+Contacts.LocalBufferImageFolder, picName01);
								attachment.add(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+Contacts.LocalBufferImageFolder+File.separator+picName01);
							}
						} catch (FileNotFoundException e) {
						    e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
	 
	 @Override
	public void onBackPressed() {
			super.onBackPressed();
			overridePendingTransition(0, R.anim.base_slide_right_out);
		}
	 
	 
	 private class ImageAdapter extends BaseAdapter{  
	        private Context mContext;  
	  
	        public ImageAdapter(Context context) {  
	            this.mContext=context;  
	        }  
	  
	        @Override  
	        public int getCount() {  
	            return mThumbIds.size();  
	        }  
	  
	        @Override  
	        public Object getItem(int position) {  
	            return mThumbIds.get(position);  
	        }  
	  
	        @Override  
	        public long getItemId(int position) {  
	            // TODO Auto-generated method stub  
	            return 0;  
	        }  
	  
	        @Override  
	        public View getView(int position, View convertView, ViewGroup parent) {  
	            //定义一个ImageView,显示在GridView里  
	            ImageView imageView;  
	            if(convertView==null){  
	                imageView=new ImageView(mContext);
	                imageView.setLayoutParams(new GridView.LayoutParams(mThumbIds.get(position).getWidth()/2, mThumbIds.get(position).getHeight()/2));
	                imageView.setScaleType(ImageView.ScaleType.CENTER);  
	                imageView.setPadding(8, 8, 8, 8); 
	            }else{  
	                imageView = (ImageView) convertView;  
	            }  
	            imageView.setImageBitmap(mThumbIds.get(position));
	            return imageView;  
	        }  
	          
	  
	          
	    }
	 
}
