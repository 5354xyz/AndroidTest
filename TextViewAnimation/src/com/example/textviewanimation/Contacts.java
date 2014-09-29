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
	public static final int NUMofMessage=10;//���͵���Ϣ����
	public static final int NUMofTextView=5;//TEXTview����
	public static final int TRUE=1;//�Ƿ�Ϊ��
	public static final int FALSE=0;//�Ƿ�Ϊ��
	public static final int TIME_OUT=181;//�Ƿ�Ϊ��
	public static final int SHAKE_OK_M=101;//�Ƿ�Ϊ��
	public static final int SHAKE_OK_C=102;//�Ƿ�Ϊ��
	
	public static List <PushNews> MessageShow=new ArrayList<PushNews> ();//��ŵ�������Ϣ����
	public static List <SpiltModel> SpiltLists=new ArrayList<SpiltModel> ();//��ŵ��²���Ϣ����
	public static PersonInfo PersonalData=new PersonInfo ();//��ŵ���Ϣ����
	
	public static final String LocalPersonFolder= "localInformation"+File.separator+"personInfo";//sd���ĸ�����Ϣ·��
	public static final String LocalPersondata= "personal.dat";//sd���ĸ�����Ϣ�ļ���
	
	public static final String LocalBufferFolder= "localInformation"+File.separator+"Buffer";//sd������Ϣ�����ļ���
	public static final String LocalBufferImageFolder= "localInformation"+File.separator+"Buffer";//sd����ͼ�񻺴��ļ���
	public static final String LocalPushNewsBufferdata= "PushNewsBuffer.dat";//sd����������Ϣ�ļ���
	public static final String LocalSpiltBufferdata= "SpiltBuffer.dat";//sd�����²���Ϣ�ļ���
	public static final String Encoding= "GBK";
	public static final String BaseURL= "http://www.shopping-100.com/xyz/server_process.php?";//+(int)(Math.random()*1000)_http://172.18.93.118/testaa.php
	public static final String BaseURL_IMAGE= "http://www.shopping-100.com/xyz/";
	
	public static final String LADING_ONLINE_DATA_ACTION="com.example.textviewanimation.LADING_ONLINE_DATA_ACTION";
	
	//��������
	public static final String RequestLogin = "1";//��¼
	public static final String RequestRegister = "2";//ע��
	public static final String RequestGetPushNews = "3";//���������Ϣ
	public static final String RequestPushSpilt = "4";//�����²�
	public static final String RequestgetSpilt = "5";//����²�
		public static final String GetSpiltTypeDefualt ="50";//ȡ�ʼ��10������
		public static final String GetSpiltTypeUp ="51";//ȡĳ��idǰ���n������
		public static final String GetSpiltTypeDown ="52";//ȡĳ��id�����10������
	public static final String RequestPushNewsUp = "6";//������Ϣ����
	public static final String RequestPushNewsDown = "7";//������Ϣ���ש
	public static final String RequestSpiltUp = "8";//�²���Ϣ����
	public static final String RequestSpiltDown = "9";//�²���Ϣ���ש
	public static final String RequestAddInterest = "10";//�����Ȥ
	public static final String RequestRefleshContent = "11";//ˢ��content����
	public static final String RequestGettouxiang = "12";//��ȡͷ��·��
	public static final String RequestGetComment = "13";//��ȡ��������
	public static final String RequestCommentHotUp = "14";//���۵���
	public static final String RequestPostComment = "15";//��������
	public static final String isRead="1";
	public static final String unIsRead="0";
	public static final int RESULT_OK = -1;
	// �ж��Ƿ�����������
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
