package com.example.textviewanimation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.example.utils.File_SD_utils;
import com.example.utils.HttpProcess;
import com.example.utils.ImageTools;
import com.example.utils.JsonProcess;
import com.example.utils.MyDialog;
import com.example.utils.SildingFinishLayout;
import com.example.utils.SildingFinishLayout.OnSildingFinishListener;

import personInfo.MyApplication;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private static final int TAKE_PICTURE = 0;
	private static final int CHOOSE_PICTURE = 1;
	private static final int CROP = 2;
	private static final int CROP_PICTURE = 3;
	public final static String TAG = "RegisterActivity";  
	
	private EditText passwordText=null;
	private EditText comfirmPasswordText=null;
	private EditText usernameText=null;
	private EditText emailText=null;
	private RadioGroup groupRadio=null;
	private RadioButton female=null;
	private RadioButton male=null;
	private String password=null;
	private String username=null;
	private String password_comfirm=null;
	private String email=null;
	private String gender="0";
	private String nameOftouxiang=null;
	private Handler registerHandler=null;
	private ImageView iv_image = null;
	File_SD_utils fileUtils = new File_SD_utils();
	private Dialog dialog = null;
	public static String uploadUrl = "http://www.shopping-100.com/xyz/testupload.php?Path=touxiang";//Contacts.BaseURL_IMAGE+
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_register);
		
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.reg_act_sildingFinishLayout);
		mSildingFinishLayout
				.setOnSildingFinishListener(new OnSildingFinishListener() {
					@Override
					public void onSildingFinish(int type) {
						// TODO Auto-generated method stub
						if (type == 1)
							RegisterActivity.this.finish();
					}
				});
		mSildingFinishLayout.setSlidingDirection(1);
		usernameText=(EditText)findViewById(R.id.register_username_edit);
		
		passwordText=(EditText)findViewById(R.id.register_pass_edit);
		
		comfirmPasswordText=(EditText)findViewById(R.id.register_pass_confirm_edit);
		
		emailText=(EditText)findViewById(R.id.register_email_edit);
		iv_image = (ImageView) this.findViewById(R.id.register_headSculpture_imageView);
		this.findViewById(R.id.register_headSculpture_but).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//截图后显示
				showPicturePicker(RegisterActivity.this,true);
			}
		});
		groupRadio=(RadioGroup)findViewById(R.id.register_groupRadio);
		female=(RadioButton)findViewById(R.id.register_female);
		female.setChecked(true);//默认选中
		male=(RadioButton)findViewById(R.id.register_male);
		
		//为groupRadio设置监听器，变化就会调用，有两个参数（RadioGroup，变化的button的id）
		groupRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if(female.getId() == checkedId)
						{
							gender="0";
						}else if(male.getId() == checkedId)
						{
							gender="1";
						}
					}
				});
		
		MyApplication.getInstance().addActivity(this);
		//submitButton=(Button)findViewById(R.id.submit);
		
		//传给Http的handler
		registerHandler = new Handler() 
						{

							@Override
							public void handleMessage(Message msg) {
								// TODO Auto-generated method stub
								super.handleMessage(msg);
								if(msg.obj!="" && msg.arg1 == Integer.valueOf(Contacts.RequestRegister).intValue())
								{
									//处理登录Http请求返回的结果
									JsonProcess jsonProcess=new JsonProcess();
									boolean isJson =JsonProcess.isGoodJson(msg.obj.toString());
									//System.out.println("isJson:"+isJson);
									if(isJson){
										String status = jsonProcess.checkStatus(msg.obj.toString());

										if(status.equals("true"))
										{
											SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy/MM/dd    HH:mm:ss   ");       
											Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
											String    str    =    formatter.format(curDate);
											System.out.println(str);
											Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
											dialog.dismiss();
											RegisterActivity.this.finish();
										}else if(status.equals("Eexist"))
										{
											dialog.dismiss();
											Toast.makeText(RegisterActivity.this, "注册失败,邮箱已存在", Toast.LENGTH_SHORT).show();
										}else if(status.equals("Uexist"))
										{
											dialog.dismiss();
											Toast.makeText(RegisterActivity.this, "注册失败,用户名已存在", Toast.LENGTH_SHORT).show();
										}else
										{
											dialog.dismiss();
											Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
										}
									}else
									{
										dialog.dismiss();
										Toast.makeText(RegisterActivity.this, "sorry,数据错误，注册失败", Toast.LENGTH_SHORT).show();
									}
								}else if(msg.arg2==Contacts.TIME_OUT)
								{
									dialog.dismiss();
									Toast.makeText(RegisterActivity.this, "网络连接超时喔(⊙o⊙)。。。", Toast.LENGTH_LONG).show();
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
				dialog =  MyDialog.createAboutDialog(RegisterActivity.this, R.style.InterestDialog);
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
				dialog =  MyDialog.createAccountDialog(RegisterActivity.this, R.style.InterestDialog);
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
						fromMain2LoginIntent.setClass(RegisterActivity.this,
								LoginActivity.class);
						startActivity(fromMain2LoginIntent);
						dialog.dismiss();
					}
				});
				dialog.show();
			}
			return super.onOptionsItemSelected(item);
		}
	
	
	public void cancel(View v)
	{
		RegisterActivity.this.finish();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}
	
	public void register(View v)
	{
		if(checkLegitimacy()){//检查数据的合法性
		
		//请求推送消息的参数对
		NameValuePair pair1 = new BasicNameValuePair("Num", Contacts.RequestRegister);
		NameValuePair pair2 = new BasicNameValuePair("User_name", username);
		NameValuePair pair3 = new BasicNameValuePair("Password", password);
		NameValuePair pair4 = new BasicNameValuePair("E_mail", email);
		NameValuePair pair5 = new BasicNameValuePair("Sex", gender);

        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
        pairList.add(pair1);
        pairList.add(pair2);
        pairList.add(pair3);
        pairList.add(pair4);
        pairList.add(pair5);
        Vector <String> attachment=new Vector <String>();
        if(nameOftouxiang != null)
        {
        	attachment.add(Environment.getExternalStorageDirectory()+File.separator+Contacts.LocalBufferImageFolder+File.separator+nameOftouxiang);
        	NameValuePair pair6 = new BasicNameValuePair("Portrait", "touxiang/"+nameOftouxiang);
        	pairList.add(pair6);
        }
        System.out.println(attachment.get(0));
        HttpProcess httpProcess1=new HttpProcess(registerHandler,Contacts.RequestRegister,attachment);
        if (!Contacts.isNetworkConnected(RegisterActivity.this)){
    		Toast.makeText(RegisterActivity.this, "无网络连接，请设置网络", Toast.LENGTH_LONG).show();
    	}
    	else{
    		
    		httpProcess1.execute(pairList);
    		dialog =  MyDialog.createLoadingDialog(RegisterActivity.this,R.style.custom_dialog,"注册中。。");		 
    		dialog.show();
			}
		}
		
	}
	
	/*
	 * 检查数据合法性
	 */
	public boolean checkLegitimacy()
	{
		password=passwordText.getText().toString();
		password_comfirm=comfirmPasswordText.getText().toString();
		username=usernameText.getText().toString();
		email=emailText.getText().toString();
		//System.out.println("_____--------"+password+"|"+password_comfirm);
		if(username.equals(""))
		{
			//通过产生消息提醒
			Toast.makeText(RegisterActivity.this, "用户名不能为空",Toast.LENGTH_SHORT).show();
			return false;
		}else if(password.equals(""))
		{
			//通过产生消息提醒
			Toast.makeText(RegisterActivity.this, "密码不能为空",Toast.LENGTH_SHORT).show();
			return false;
		}else if(password.length()<6)
		{
			//通过产生消息提醒
			Toast.makeText(RegisterActivity.this, "密码不能小于6位",Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!password.equals(password_comfirm))
		{
			//通过产生消息提醒
			Toast.makeText(RegisterActivity.this, "密码不一致",Toast.LENGTH_SHORT).show();
			//获取焦点
			passwordText.setFocusable(true);
			passwordText.setFocusableInTouchMode(true);
			passwordText.requestFocus();
			return false;
		}
		String check = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		Pattern p_email = Pattern.compile(check);
		Matcher m_email = p_email.matcher(email);
		if (!m_email.find())
		{
			//通过产生消息提醒
			Toast.makeText(RegisterActivity.this, "邮箱格式不对",Toast.LENGTH_SHORT).show();
			//获取焦点
			emailText.setFocusable(true);
			emailText.setFocusableInTouchMode(true);
			emailText.requestFocus();
			return false;
		}
			
		
		return true;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case TAKE_PICTURE:
				//将保存在本地的图片取出并缩小后显示在界面上
				Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+File.separator+Contacts.LocalBufferImageFolder+"/image.jpg");
				Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, 90, 90);
				Log.i("bitmap.getWidth()", String.valueOf(bitmap.getWidth()));
				//由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
				bitmap.recycle();
				
				//将处理过的图片显示在界面上，并保存到本地
				iv_image.setImageBitmap(newBitmap);
				StringBuilder sb = new StringBuilder();
				nameOftouxiang=usernameText.getText().toString();
				sb.append(nameOftouxiang);
				//sb.append(String.valueOf(System.currentTimeMillis()));
				sb.append(".png");
				nameOftouxiang=sb.toString();
				Log.i("nameOftouxiang---sb", nameOftouxiang);
				ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+Contacts.LocalBufferImageFolder, nameOftouxiang);
				
				break;

			case CHOOSE_PICTURE:
				ContentResolver resolver = getContentResolver();
				//照片的原始资源地址
				Uri originalUri = data.getData(); 
	            try {
	            	//使用ContentProvider通过URI获取原始图片
					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
					if (photo != null) {
						//为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
						Bitmap smallBitmap = ImageTools.zoomBitmap(photo, 90, 90);
						Log.i("bitmap.getWidth()", String.valueOf(smallBitmap.getWidth()));
						//释放原始图片占用的内存，防止out of memory异常发生
						photo.recycle();
						
						iv_image.setImageBitmap(smallBitmap);
						StringBuilder sb_1 = new StringBuilder();
						nameOftouxiang=usernameText.getText().toString();
						sb_1.append(nameOftouxiang);
						//sb_1.append(String.valueOf(System.currentTimeMillis()));
						sb_1.append(".png");
						nameOftouxiang=sb_1.toString();
						Log.i("nameOftouxiang---sb_1", nameOftouxiang);
						ImageTools.savePhotoToSDCard(smallBitmap, Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+Contacts.LocalBufferImageFolder, nameOftouxiang);
					}
				} catch (FileNotFoundException e) {
				    e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
				
			case CROP:
				Uri uri = null;
				if (data != null) {
					uri = data.getData();
					System.out.println("Data");
				}else {
					System.out.println("File");
					String fileName = getSharedPreferences("temp",Context.MODE_WORLD_WRITEABLE).getString("tempName", "");
					uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()+File.separator+Contacts.LocalBufferImageFolder,fileName));
				}
				cropImage(uri, 500, 500, CROP_PICTURE);
				break;
			
			case CROP_PICTURE:
				Bitmap photo = null;
				Uri photoUri = data.getData();
				if (photoUri != null) {
					photo = BitmapFactory.decodeFile(photoUri.getPath());
				}
				if (photo == null) {
					Bundle extra = data.getExtras();
					if (extra != null) {
		                photo = (Bitmap)extra.get("data");  
		                ByteArrayOutputStream stream = new ByteArrayOutputStream();  
		                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		            }  
				}
				Bitmap newBitmap1 = ImageTools.zoomBitmap(photo, 90, 90);
				iv_image.setImageBitmap(newBitmap1);
				Log.i("bitmap.getWidth()", String.valueOf(newBitmap1.getWidth()));
				StringBuilder sb_2 = new StringBuilder();
				nameOftouxiang=usernameText.getText().toString();
				sb_2.append(nameOftouxiang);
				//sb_2.append(String.valueOf(System.currentTimeMillis()));
				sb_2.append(".png");
				nameOftouxiang=sb_2.toString();
				
				Log.i("nameOftouxiang---sb_2", nameOftouxiang);
				ImageTools.savePhotoToSDCard(newBitmap1, Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+Contacts.LocalBufferImageFolder, nameOftouxiang);
				break;
			default:
				break;
			}
		}
	}
	public void showPicturePicker(Context context,boolean isCrop){
		final boolean crop = isCrop;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("图片来源");
		builder.setNegativeButton("取消", null);
		builder.setItems(new String[]{"拍照","相册"}, new DialogInterface.OnClickListener() {
			//类型码
			int REQUEST_CODE;
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case TAKE_PICTURE:
					Uri imageUri = null;
					String fileName = null;
					Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					if (crop) {
						REQUEST_CODE = CROP;
						//删除上一次截图的临时文件
						SharedPreferences sharedPreferences = getSharedPreferences("temp",Context.MODE_WORLD_WRITEABLE);
						ImageTools.deletePhotoAtPathAndName(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+Contacts.LocalBufferImageFolder, sharedPreferences.getString("tempName", ""));
						
						//保存本次截图临时文件名字
						fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
						Editor editor = sharedPreferences.edit();
						editor.putString("tempName", fileName);
						editor.commit();
					}else {
						REQUEST_CODE = TAKE_PICTURE;
						fileName = "image.jpg";
					}
					imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()+File.separator+Contacts.LocalBufferImageFolder,fileName));
					//指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
					openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(openCameraIntent, REQUEST_CODE);
					break;
					
				case CHOOSE_PICTURE:
					Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
					if (crop) {
						REQUEST_CODE = CROP;
					}else {
						REQUEST_CODE = CHOOSE_PICTURE;
					}
					openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
					startActivityForResult(openAlbumIntent, REQUEST_CODE);
					break;

				default:
					break;
				}
			}
		});
		builder.create().show();
	}

	//截取图片
	public void cropImage(Uri uri, int outputX, int outputY, int requestCode){
			Intent intent = new Intent("com.android.camera.action.CROP");  
	        intent.setDataAndType(uri, "image/*");  
	        intent.putExtra("crop", "true");  
	        intent.putExtra("aspectX", 1);  
	        intent.putExtra("aspectY", 1);  
	        intent.putExtra("outputX", outputX);   
	        intent.putExtra("outputY", outputY); 
	        intent.putExtra("outputFormat", "JPEG");
	        intent.putExtra("noFaceDetection", true);
	        intent.putExtra("return-data", true);//返回值，装在intent data里面  
		    startActivityForResult(intent, requestCode);
		}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

}
