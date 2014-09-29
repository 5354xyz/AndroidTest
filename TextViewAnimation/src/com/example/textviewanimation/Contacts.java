package com.example.textviewanimation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import personInfo.PersonInfo;
import personInfo.PushNews;
import personInfo.SpiltModel;

public class Contacts {
	public static final int NUMofMessage=10;//推送的消息条数
	public static final int NUMofTextView=5;//TEXTview条数
	public static final int TRUE=1;//是否为真
	public static final int FALSE=0;//是否为真
	public static final int TIME_OUT=181;//是否为真
	public static final int SHAKE_OK_M=101;//是否为真
	public static final int SHAKE_OK_C=102;//是否为真
	
	public static List <PushNews> MessageShow=new ArrayList<PushNews> ();//存放的推送消息数组
	public static List <SpiltModel> SpiltLists=new ArrayList<SpiltModel> ();//存放的吐槽消息数组
	public static PersonInfo PersonalData=new PersonInfo ();//存放的消息数组
	
	public static final String LocalPersonFolder= "localInformation"+File.separator+"personInfo";//sd卡的个人信息路径
	public static final String LocalPersondata= "personal.dat";//sd卡的个人信息文件名
	
	public static final String LocalBufferFolder= "localInformation"+File.separator+"Buffer";//sd卡的消息缓存文件夹
	public static final String LocalBufferImageFolder= "localInformation"+File.separator+"Buffer";//sd卡的图像缓存文件夹
	public static final String LocalPushNewsBufferdata= "PushNewsBuffer.dat";//sd卡的推送消息文件名
	public static final String LocalSpiltBufferdata= "SpiltBuffer.dat";//sd卡的吐槽消息文件名
	public static final String Encoding= "GBK";
	public static final String BaseURL= "http://www.shopping-100.com/xyz/server_process.php?";//+(int)(Math.random()*1000)_http://172.18.93.118/testaa.php
	public static final String BaseURL_IMAGE= "http://www.shopping-100.com/xyz/";
	
	public static final String LADING_ONLINE_DATA_ACTION="com.example.textviewanimation.LADING_ONLINE_DATA_ACTION";
	
	//请求类型
	public static final String RequestLogin = "1";//登录
	public static final String RequestRegister = "2";//注册
	public static final String RequestGetPushNews = "3";//获得推送消息
	public static final String RequestPushSpilt = "4";//发表吐槽
	public static final String RequestgetSpilt = "5";//获得吐槽
		public static final String GetSpiltTypeDefualt ="50";//取最开始的10条数据
		public static final String GetSpiltTypeUp ="51";//取某个id前面的n条数据
		public static final String GetSpiltTypeDown ="52";//取某个id后面的10条数据
	public static final String RequestPushNewsUp = "6";//推送消息点赞
	public static final String RequestPushNewsDown = "7";//推送消息点板砖
	public static final String RequestSpiltUp = "8";//吐槽消息点赞
	public static final String RequestSpiltDown = "9";//吐槽消息点板砖
	public static final String RequestAddInterest = "10";//添加兴趣
	public static final String RequestRefleshContent = "11";//刷新content内容
	public static final String RequestGettouxiang = "12";//获取头像路径
	public static final String RequestGetComment = "13";//获取最新评论
	public static final String RequestCommentHotUp = "14";//评论点赞
	public static final String RequestPostComment = "15";//发表评论
	public static final String isRead="1";
	public static final String unIsRead="0";
	public static final int RESULT_OK = -1;
	// 判断是否有网络链接
	public static final boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}



}
