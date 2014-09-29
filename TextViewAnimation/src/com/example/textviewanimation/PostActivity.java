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
	private List <Bitmap> mThumbIds =new ArrayList <Bitmap> ();//�������ͼƬ
	Vector <String> attachment=new Vector <String>();//�������ͼƬ��·��
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
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		setContentView(R.layout.activity_post);
		
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.post_act_sildingFinishLayout);
		mSildingFinishLayout
				.setOnSildingFinishListener(new OnSildingFinishListener() {

					@Override
					public void onSildingFinish() {
						PostActivity.this.finish();
					}
				});
		mLocationClient = new LocationClient(getApplicationContext());     //����LocationClient��
	    
	    mLocationClient.setAK("50b6248aed30effde0d0cba9536b8524");
	    LocationClientOption option = new LocationClientOption();
	    option.setOpenGps(true);
	    option.setAddrType("all");//���صĶ�λ���������ַ��Ϣ
	    option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
	    option.setScanSpan(5000);//���÷���λ����ļ��ʱ��Ϊ5000ms
	    option.disableCache(true);//��ֹ���û��涨λ
	    option.setPoiNumber(5);    //��෵��POI����   
	    option.setPoiDistance(1000); //poi��ѯ����        
	    option.setPoiExtraInfo(true); //�Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ        
	    mLocationClient.setLocOption(option);
	    MyApplication.getInstance().addActivity(this);
	    
		mEditText = (EditText) findViewById(R.id.et_content);
		mEditText.addTextChangedListener(mTextWatcher);
		mEditText.setSelection(mEditText.length()); // ������ƶ����һ���ַ�����
		
		cameraButton =(ImageButton)findViewById(R.id.post_camera);
		photoButton =(ImageButton)findViewById(R.id.post_photo);
		cameraButton.setOnClickListener(new PicOnclickListener());
		photoButton.setOnClickListener(new PicOnclickListener());
		picView =(GridView) findViewById(R.id.post_pic_gridview);
		gridviewAdapter=new ImageAdapter(this);
		picView.setAdapter(gridviewAdapter);  
        //����GridViewԪ�ص���Ӧ  
		picView.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                //����������GridViewԪ�ص�λ��  
                Toast.makeText(PostActivity.this,"���ɾ��", Toast.LENGTH_SHORT).show(); 
                
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
		mLocationClient.registerLocationListener( myListener );    //ע���������
		mLocationClient.start();
		
		//����Http��handler
		postHandler = new Handler() 
				{

					@Override
					public void handleMessage(Message msg) {
						// TODO Auto-generated method stub
						super.handleMessage(msg);
						if(msg.obj!="" && msg.arg1 == Integer.valueOf(Contacts.RequestPushSpilt).intValue())
						{
							//�����¼Http���󷵻صĽ��
							JsonProcess jsonProcess=new JsonProcess();
							
							String isSpilt = jsonProcess.checkStatus(msg.obj.toString());
							
							//Contacts.PersonalData.setIsLogin(isLogin)
							if(isSpilt.equals("true"))
							{
								SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy/MM/dd    HH:mm:ss   ");       
								Date    curDate    =   new    Date(System.currentTimeMillis());//��ȡ��ǰʱ��       
								String    str    =    formatter.format(curDate);
								System.out.println(str);
								Intent data = new Intent();
								data.putExtra("POST", "1");
								Toast.makeText(PostActivity.this, "����ɹ�", Toast.LENGTH_LONG).show();
								dialog.dismiss();
								setResult(Contacts.RESULT_OK, data);
								PostActivity.this.finish();
							}else
							{
								dialog.dismiss();
								Toast.makeText(PostActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
							}
						}else if(msg.arg2==Contacts.TIME_OUT)
						{
							dialog.dismiss();
							Toast.makeText(PostActivity.this, "�������ӳ�ʱ�(��o��)...�����������", Toast.LENGTH_LONG).show();
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
	//���ͻ�����˵��е�ĳһ��ѡ��ʱ���ø÷���
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			if(item.getItemId()==R.id.menu_about)
			{
				// ����һ��progressdialog�ĵ���
				dialog =  MyDialog.createAboutDialog(PostActivity.this, R.style.InterestDialog);
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

			// ��ȥ������������������ջ���
			mEditText.removeTextChangedListener(mTextWatcher);

			// ע������ֻ��ÿ�ζ�������EditText�������󳤶ȣ����ܶ�ɾ���ĵ����ַ��󳤶�
			// ��Ϊ����Ӣ�Ļ�ϣ������ַ����ԣ�calculateLength�������᷵��1
			while (calculateLength(s.toString()) > MAX_COUNT) { // �������ַ������������ƵĴ�Сʱ�����нضϲ���
				s.delete(editStart - 1, editEnd);
				editStart--;
				editEnd--;
			}
			mEditText.setText(s);
			mEditText.setSelection(editStart);

			// �ָ�������
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
	 * ���㷢�����ݵ�������һ������=����Ӣ����ĸ��һ�����ı��=����Ӣ�ı�� ע�⣺�ú����Ĳ������ڶԵ����ַ����м��㣬��Ϊ�����ַ������������1
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
	 * ˢ��ʣ����������,���ֵ����΢����140���֣���������200����
	 */
	private void setLeftCount() {
		mTextView.setText(String.valueOf((MAX_COUNT - getInputCount())));
	}

	/**
	 * ��ȡ�û�����ķ�����������
	 * 
	 * @return
	 */
	private long getInputCount() {
		return calculateLength(mEditText.getText().toString());
	}
	
	/**
	 * �������ٵ�ǰactivity
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
	 * ���͵Ĵ����¼�
	 * 2013-11-17
	 * 
	 * @author:5354xyz
	 */
	public void send(View v)
	{
		if(Contacts.PersonalData.getIsLogin().equals("1")){
		//����������Ϣ�Ĳ�����
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
        		Toast.makeText(PostActivity.this, "���������ӣ�����������", Toast.LENGTH_LONG).show();
        	}
        	else{
        		if(mEditText.getText().toString().equals(""))
        		{
        			//ͨ��������Ϣ����
        			Toast.makeText(PostActivity.this, "���ݲ���Ϊ��",Toast.LENGTH_SHORT).show();
        			
        		}else{
        			httpProcess1.execute(pairList);
        
        			//�������ڵ�¼��
        			dialog =  MyDialog.createLoadingDialog(PostActivity.this,R.style.custom_dialog,"�²��С���");
        			dialog.show();
        		}
        	}
        }else
        {
        	Toast.makeText(PostActivity.this, "Ҫ�ȵ�¼ �������²�(��o��)Ŷ", Toast.LENGTH_LONG).show();
        	Intent fromPost2LoginIntent = new Intent();
        	//�����ݼ��뵽Intent����ȥ 
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
						Toast.makeText(PostActivity.this, "ͼƬ��Ҫ����4��Ŷ~~~", Toast.LENGTH_SHORT).show();
					}
					break;
				case R.id.post_camera:
					if(mThumbIds.size() < 4){
						Uri imageUri = null;
						String fileName = "temp.jpg";
						Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()+File.separator+Contacts.LocalBufferImageFolder,fileName));
						//ָ����Ƭ����·����SD������image.jpgΪһ����ʱ�ļ���ÿ�����պ����ͼƬ���ᱻ�滻
						openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
						startActivityForResult(openCameraIntent, TAKE_PICTURE);
					}else
					{
						Toast.makeText(PostActivity.this, "ͼƬ��Ҫ����4��Ŷ~~~", Toast.LENGTH_SHORT).show();
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
						//�������ڱ��ص�ͼƬȡ������С����ʾ�ڽ�����
						Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+File.separator+Contacts.LocalBufferImageFolder+"/temp.jpg");
						Bitmap newBitmap = ImageTools.reZoomBmp(bitmap, 400);
						Log.i("bitmap.getWidth()", String.valueOf(bitmap.getWidth()));
						//����Bitmap�ڴ�ռ�ýϴ�������Ҫ�����ڴ棬����ᱨout of memory�쳣
						bitmap.recycle();
						
						//���������ͼƬ��ʾ�ڽ����ϣ������浽����
						mThumbIds.add(newBitmap);
						gridviewAdapter.notifyDataSetChanged();
						//����ͼƬ������
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
						//��Ƭ��ԭʼ��Դ��ַ
						Uri originalUri = data.getData(); 
			            try {
			            	//ʹ��ContentProviderͨ��URI��ȡԭʼͼƬ
							Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
							if (photo != null) {
								//Ϊ��ֹԭʼͼƬ�������ڴ��������������Сԭͼ��ʾ��Ȼ���ͷ�ԭʼBitmapռ�õ��ڴ�
								Bitmap smallBitmap = ImageTools.reZoomBmp(photo, 400);
								Log.i("bitmap.getWidth()", String.valueOf(smallBitmap.getWidth()));
								//�ͷ�ԭʼͼƬռ�õ��ڴ棬��ֹout of memory�쳣����
								photo.recycle();
								//���������ͼƬ��ʾ�ڽ����ϣ������浽����
								mThumbIds.add(smallBitmap);
								gridviewAdapter.notifyDataSetChanged();
								//����ͼƬ������
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
	            //����һ��ImageView,��ʾ��GridView��  
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
