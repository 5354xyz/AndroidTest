package com.example.utils;

/*
 * android 报错java.io.IOException: Permission denied
 首先检查你的路径是不是对的。应该在Environment.getExternalStorageDirectory().getAbsolutePath()这个目录下写东西。
 如果路径是对的，那就检查是不是给你的application添加了权限。

 如果没有，在manifest中添加<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 如果上面的都有正确，但是仍然会上面的错误。
 请检查你的avd在创建的时候有没有设置size，如果没有设置的话就重新创建一个有size的avd
 * */
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;

import com.example.textviewanimation.Contacts;

import personInfo.PersonInfo;
import personInfo.PushNews;
import personInfo.SpiltModel;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

public class File_SD_utils {
	private String SDRoot;

	/**
	 * 判断sd卡是否存在
	 * 2014-4-21
	 * 
	 * @author:5354xyz
	 */
	public boolean ExistSDCard() {  
		  if (android.os.Environment.getExternalStorageState().equals(  
		    android.os.Environment.MEDIA_MOUNTED)) {  
		   return true;  
		  } else  
		   return false;  
		 }
	/**
	 * SD卡剩余空间,单位MB
	 * 2014-4-21
	 * 
	 * @author:5354xyz
	 */
	public long getSDFreeSize(){  
	     //取得SD卡文件路径  
	     File path = Environment.getExternalStorageDirectory();   
	     StatFs sf = new StatFs(path.getPath());   
	     //获取单个数据块的大小(Byte)  
	     long blockSize = sf.getBlockSize();   
	     //空闲的数据块的数量  
	     long freeBlocks = sf.getAvailableBlocks();  
	     //返回SD卡空闲大小  
	     //return freeBlocks * blockSize;  //单位Byte  
	     //return (freeBlocks * blockSize)/1024;   //单位KB  
	     return (freeBlocks * blockSize)/1024 /1024; //单位MB  
	   } 
	
	/**
	 * SD卡总容量,单位MB  
	 */
	public long getSDAllSize(){  
	     //取得SD卡文件路径  
	     File path = Environment.getExternalStorageDirectory();   
	     StatFs sf = new StatFs(path.getPath());   
	     //获取单个数据块的大小(Byte)  
	     long blockSize = sf.getBlockSize();   
	     //获取所有数据块数  
	     long allBlocks = sf.getBlockCount();  
	     //返回SD卡大小  
	     //return allBlocks * blockSize; //单位Byte  
	     //return (allBlocks * blockSize)/1024; //单位KB  
	     return (allBlocks * blockSize)/1024/1024; //单位MB  
	   }
	public File_SD_utils() {
		// 获得当前外部储存设备的目录
		if(ExistSDCard()){
		SDRoot = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator;
		}else
		{
			SDRoot=null;
		}
	}

	// 获得SD卡的路径
	public String getPath() {
		return SDRoot;
	}

	/*
	 * 在SD卡上创建文件
	 */
	public File creatSDFile(String fileName, String dir) throws IOException {
		File file = new File(SDRoot + dir + File.separator + fileName);
		System.out.println(SDRoot + fileName);
		file.createNewFile();
		return file;
	}

	/*
	 * 在SD卡上面创键多级目录
	 */

	public File creatSDDir(String dirName) throws IOException {
		System.out.println("asdf：" + dirName);
		boolean xx = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
		File dir = new File(SDRoot + dirName);
		System.out.println(dir.mkdirs() + "xxxx:" + xx);
		// dir.mkdir();
		dir.mkdirs();
		return dir;
	}

	/*
	 * 判断SD卡上的文件是否存在
	 */
	public boolean isExitFile(String dirName, String fileName) {
		File file = new File(SDRoot + dirName + File.separator + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream中的数据写入至SD卡中 appendable为true的时候是以追加的方式写入文件
	 */
	public File writeStreamToSDCard(String dirpath, String filename,
			InputStream input, boolean appendable) {
		File file = new File(SDRoot + dirpath + File.separator + filename);
		FileOutputStream output = null;
		try {
			// 判断或者创建目录；这里要比较注意，如果目录错了，下面创建文件的时候就进行不了
			if (!isExitFile(dirpath, filename) && !appendable) {
				creatSDDir(dirpath);
				System.out.println("创建sd卡文件，输入流");

				// 在创建 的目录上创建文件；
				file = creatSDFile(filename, dirpath);
				

				output = new FileOutputStream(file);
				//OutputStreamWriter osw=new OutputStreamWriter(output,Contacts.Encoding);
				// 1024*4
				// 表示的是这个字节数组的长度。就是1024乘以4的意思。这个长度是自己定义的,一般长度为1024的整数倍比较好。
				byte[] bt = new byte[20 * 1024];
				int length = 0;
				while ((length = input.read(bt)) != -1) {
					// input.read(bt)不一定正好读入4*1024个字节，测试后发现很少能一次读满buffer，大部分时候是1440字节，
					output.write(bt, 0, length);
					//System.out.println(length);
				}
				// 刷新缓存，
				output.flush();
				
			} else if (appendable) {
				if (!isExitFile(dirpath, filename)) {
					creatSDDir(dirpath);
					System.out.println("创建sd卡文件，输入流,追加");
					// 在创建 的目录上创建文件；
					file = creatSDFile(filename, dirpath);
				}
				
					System.out.println(input != null);
				output = new FileOutputStream(file, appendable);
				byte[] bt = new byte[2 * 1024];
				int length = 0;
				while ((length = input.read(bt)) != -1) {
					// input.read(bt)不一定正好读入4*1024个字节，测试后发现很少能一次读满buffer，大部分时候是1440字节，
					output.write(bt, 0, length);
					//System.out.println(length);
				}
				// 刷新缓存，
				output.flush();
				

			}

			else {
				System.out.println("文件已经存在");
				
				output = new FileOutputStream(file, appendable);
				byte[] bt = new byte[2 * 1024];
				int length = 0;
				while ((length = input.read(bt)) != -1) {
					// input.read(bt)不一定正好读入4*1024个字节，测试后发现很少能一次读满buffer，大部分时候是1440字节，
					output.write(bt, 0, length);
					System.out.println(length);
				}
				// 刷新缓存，
				output.flush();
				
				return file;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return file;

	}
	
	public boolean writePersonInfo2SD(PersonInfo personInfo,String path, String fileName)
	{
		String temp = null;
		
		// 获得路径
		File file = new File(SDRoot + File.separator + path);
		ByteArrayInputStream stream = null;
		temp = personInfo.toString();
				
		// 判断文件夹是否存在
		if (file.isDirectory() && file.exists()) {
			// 判断文件是否存在
			if (isExitFile(path, fileName)) {
				stream = new ByteArrayInputStream(temp.getBytes());
				file = writeStreamToSDCard(path,
							fileName, stream, false);//不追加的形式

				} else {
					System.out.println("(文件)不存在,现在创建文件。。。");
					// 创建文件
					boolean appenable = false;// 不追加
					stream = new ByteArrayInputStream(temp.getBytes());
					// 写入一个空的
					writeStreamToSDCard(path,
							fileName, stream, appenable);
					}

			} else {
				System.out.println("(文件夹)不存在,现在创建文件夹。。。");
				// 创建文件夹
				try {
					creatSDDir(path);
				} catch (IOException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					if (file != null)
						return true;
					return false;		
	}

	// 处理本人信息
	public PersonInfo loadingPersonInfo(String path) throws IOException {
		PersonInfo personInfo = new PersonInfo();
		personInfo.setIsLogin("0");
		personInfo.setIsLoginRemember("0");
		personInfo.setIsStorageBuffer("0");
		personInfo.setFirstLogin("0");
		personInfo.setLoginTime("");
		personInfo.setUserName("");
		personInfo.setTouxiangurl("");
		personInfo.setId("");
		// 获得路径
		File file = new File(SDRoot + File.separator + path);
		// 判断文件夹是否存在
		if (file.isDirectory() && file.exists()) {
			// 判断文件是否存在
			if (isExitFile(path, Contacts.LocalPersondata)) {
				try {

					Pattern p_userName = Pattern.compile("^userName:.*");
					Pattern p_islogin = Pattern.compile("^isLogin:.*");
					Pattern p_loginTime = Pattern.compile("^loginTime:.*");
					Pattern p_isLoginRemember = Pattern
							.compile("^isLoginRemember:.*");
					Pattern p_isStorageBuffer = Pattern
							.compile("^isStorageBuffer:.*");
					Pattern p_firstLogin = Pattern
							.compile("^firstLogin:.*");
					Pattern p_touxiangurl = Pattern
							.compile("^touxiangurl:.*");
					Pattern p_userId = Pattern.compile("^id:.*");
					InputStream inputStream = new FileInputStream(SDRoot
							+ File.separator + Contacts.LocalPersonFolder
							+ File.separator + Contacts.LocalPersondata);
					// 防止乱码，，，，，，
					InputStreamReader inputReader = new InputStreamReader(
							inputStream, Contacts.Encoding);
					BufferedReader bufReader = new BufferedReader(inputReader);
					String temp = null;
					while ((temp = bufReader.readLine()) != null) {
						//System.out.println("personal+++");
						Matcher m_userName = p_userName.matcher(temp);
						Matcher m_isLogin = p_islogin.matcher(temp);
						Matcher m_loginTime = p_loginTime.matcher(temp);
						Matcher m_isLoginRemember = p_isLoginRemember
								.matcher(temp);
						Matcher m_isStorageBuffer = p_isStorageBuffer
								.matcher(temp);
						Matcher m_touxiangurl = p_touxiangurl
								.matcher(temp);
						Matcher m_firstLogin = p_firstLogin
										.matcher(temp);
						Matcher m_id = p_userId
										.matcher(temp);
						if (m_userName.find())
							personInfo.setUserName(temp.substring(9));
						if (m_isLogin.find())
							personInfo.setIsLogin(temp.substring(8));
						if (m_loginTime.find())
							personInfo.setLoginTime(temp.substring(10));
						if (m_isLoginRemember.find())
							personInfo.setIsLoginRemember(temp.substring(16));
						if (m_isStorageBuffer.find())
							personInfo.setIsStorageBuffer(temp.substring(16));
						if (m_firstLogin.find())
							personInfo.setFirstLogin(temp.substring(11));
						if(m_touxiangurl.find())
							personInfo.setTouxiangurl(temp.substring(12));
						if(m_id.find())
							personInfo.setId(temp.substring(3));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("(文件)不存在,现在创建文件。。。");
				// 创建文件

				String null_info = "userName:" + "\nisLogin:0" + "\n"
						+ "loginTime:" + "\n" + "isLoginRemember:0" + "\n"
						+ "isStorageBuffer:0" + "\nfirstLogin:0"+"\ntouxiangurl:"+"\nid:"+"\n";
				InputStream ins = new ByteArrayInputStream(null_info.getBytes());
				boolean appenable = false;// 不追加
				// 写入一个空的
				writeStreamToSDCard(Contacts.LocalPersonFolder,
						Contacts.LocalPersondata, ins, appenable);
			}

		} else {
			System.out.println("(文件夹)不存在,现在创建文件夹。。。");
			// 创建文件夹
			creatSDDir(path);

		}
		//System.out.println(personInfo);
		return personInfo;

	}

	// 向sd卡中写入推送消息缓存
	public boolean writePushNewsBuffertoSD(List<PushNews> pushNews,
			String path, String fileName) {
		
		
		String temp = null;
		int i=0;//标记一下，否则会有一个null这样的字符串
		// 获得路径
		File file = new File(SDRoot + File.separator + path);
		ByteArrayInputStream stream = null;
		for (Iterator iterator = pushNews.iterator(); iterator
				.hasNext();) {
			PushNews pushNews2 = (PushNews) iterator.next();
			if(i!= 0){
				temp += "isRead:" + Contacts.unIsRead + "\n";// 将消息设置为未读
				temp += pushNews2.toString();
				temp += "\n";
				}else
				{
					temp = "isRead:" + Contacts.unIsRead + "\n";// 将消息设置为未读
					temp += pushNews2.toString();
					temp += "\n";
					i++;
				}

		}
		//System.out.println("temp:" + temp);
		
		// 判断文件夹是否存在
		if (file.isDirectory() && file.exists()) {
			// 判断文件是否存在
			if (isExitFile(path, fileName)) {
				stream = new ByteArrayInputStream(temp.getBytes());
				file = writeStreamToSDCard(path,
						fileName, stream, true);

			} else {
				System.out.println("(文件)不存在,现在创建文件。。。");
				// 创建文件
				boolean appenable = false;// 不追加
				stream = new ByteArrayInputStream(temp.getBytes());
				// 写入一个空的
				writeStreamToSDCard(path,
						fileName, stream, appenable);
			}

		} else {
			System.out.println("(文件夹)不存在,现在创建文件夹。。。");
			// 创建文件夹
			try {
				creatSDDir(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (file != null)
			return true;
		return false;
	}

	/**
	 * 从sd缓存文件中获得缓存消息
	 * 2013-11-17
	 * 
	 * @author:5354xyz
	 */
	public List<PushNews> loadingPushNews(String path, String fileName,
			boolean isLoadingHadRead) throws IOException {
		List<PushNews> pushNewsList = new ArrayList<PushNews>();
		String isRead = "false";
		// 获得路径
		File file = new File(SDRoot + File.separator + path);
		// 判断文件夹是否存在
		if (file.isDirectory() && file.exists()) {
			// 判断文件是否存在
			if (isExitFile(path, fileName)) {
				try {

					

					Pattern p_isRead = Pattern.compile("^isRead:.*");
					Pattern p_pushNews_id = Pattern.compile("^pushNews_id:.*");
					Pattern p_pushNews_content = Pattern
							.compile("^pushNews_content:.*");
					Pattern p_pushNews_up = Pattern.compile("^pushNews_up:.*");
					Pattern p_pushNews_down = Pattern
							.compile("^pushNews_down:.*");
					Pattern p_pushNews_title = Pattern
							.compile("^pushNews_title:.*");

					InputStream inputStream = new FileInputStream(SDRoot
							+ File.separator + path + File.separator + fileName);
					// 防止乱码，，，，，，
					InputStreamReader inputReader = new InputStreamReader(
							inputStream);
					BufferedReader bufReaderd = new BufferedReader(inputReader);
					String temp = null;
					String content = null;
					PushNews pushNews = new PushNews();
					pushNews.setPushNews_content("");
					pushNews.setPushNews_down("");
					pushNews.setPushNews_id("");
					pushNews.setPushNews_title("");
					pushNews.setPushNews_up("");
					while ((temp = bufReaderd.readLine()) != null) {
						
						//System.out.println("。。。"+temp);
						boolean check=true;
						//Matcher只能用来判断一次，之后失效
						Matcher m_isRead = p_isRead.matcher(temp);
						Matcher m_pushNews_id = p_pushNews_id.matcher(temp);
						Matcher m_pushNews_content = p_pushNews_content
								.matcher(temp);
						Matcher m_pushNews_up = p_pushNews_up.matcher(temp);
						Matcher m_pushNews_down = p_pushNews_down.matcher(temp);
						Matcher m_pushNews_title = p_pushNews_title
								.matcher(temp);
						
						if (m_isRead.find()){
							isRead = temp.substring(7);
							check=false;
							//System.out.println("。foundm_isRead");
							}
						
						if (m_pushNews_id.find()) {
							check=false;
							pushNews.setPushNews_id(temp.substring(12));
							
						}
						if (m_pushNews_up.find()){
							pushNews.setPushNews_up(temp.substring(12));
							check=false;
							//System.out.println("。foundm_pushNews_up");
							}
						if (m_pushNews_down.find()){
							check=false;
							pushNews.setPushNews_down(temp.substring(14));
							//System.out.println("。foundm_pushNews_down");
						}
						if (m_pushNews_title.find()){
							check=false;
							pushNews.setPushNews_title(temp.substring(15));
							pushNews.setPushNews_content(content);
							//System.out.println("。foundm_pushNews_title");
							}

						if (check) {
							
							if (m_pushNews_content.find())
							{
								content = temp.substring(17);
							}
							else{
									content += temp;
									//System.out.println("content:"+content);
							}
							
						}

						
						
						// 是否读入已读的消息，默认读入未读的消息
						if (isRead.equals("0") && !pushNews.getPushNews_title().equals("")) {
								if(!isInCheck(pushNewsList,pushNews))
									//System.out.println("add:"+pushNews);
									pushNewsList.add(pushNews);
								
									pushNews = new PushNews();
									pushNews.setPushNews_content("");
									pushNews.setPushNews_down("");
									pushNews.setPushNews_id("");
									pushNews.setPushNews_title("");
									pushNews.setPushNews_up("");
							
								
						} else if (isRead.equals("1") && isLoadingHadRead) {
							if(!isInCheck(pushNewsList,pushNews))
								pushNewsList.add(pushNews);
						}
						
						if(pushNewsList.size()>Contacts.NUMofMessage)
							break;

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("(文件)不存在,现在创建文件。。。");
				// 创建文件

				String null_info = "";
				InputStream ins = new ByteArrayInputStream(null_info.getBytes());
				boolean appenable = false;// 不追加
				// 写入一个空的
				writeStreamToSDCard(Contacts.LocalBufferFolder,
						Contacts.LocalPushNewsBufferdata, ins, appenable);
			}

		} else {
			System.out.println("(文件夹)不存在,现在创建文件夹。。。");
			// 创建文件夹
			creatSDDir(path);

		}
		
		
		//System.out.println(pushNewsList +"124eel");
		return pushNewsList;

	}
	
	//检查是否已经载入相同的
	public boolean isInCheck(List<PushNews> pushNewsList1,PushNews pushNew)
	{
		PushNews pushNew1=null;
		//System.out.println("check"+pushNew);
		for (Iterator iterator = pushNewsList1.iterator(); iterator.hasNext();) {
			pushNew1 = (PushNews) iterator.next();
			//System.out.println(pushNew1.getPushNews_id()+"___"+pushNew.getPushNews_id());
			if(pushNew1.getPushNews_id().equals(pushNew.getPushNews_id()))
			{
				//System.out.println("true");
				return true;
			}
			
		}
		System.out.println("false");
		return false;
	}
	
	/**
	 *  向sd卡中写入吐槽消息缓存
	 * 2013-11-18
	 * 
	 * @author:5354xyz
	 */
		public boolean writeSpiltBuffertoSD(List<SpiltModel> spilts,
				String path, String fileName) {
			
			
			String temp = null;
			int i=0;//标记一下，否则会有一个null这样的字符串
			// 获得路径
			File file = new File(SDRoot + File.separator + path);
			ByteArrayInputStream stream = null;
			for (Iterator iterator = spilts.iterator(); iterator
					.hasNext();) {
				SpiltModel spilt = (SpiltModel) iterator.next();
				if(i!= 0){
					temp += spilt.toString();
					temp += "\n";
					}else
					{
						
						temp = spilt.toString();
						temp += "\n";
						i++;
					}

			}
			//System.out.println("temp:" + temp);
			
			// 判断文件夹是否存在
			if (file.isDirectory() && file.exists()) {
				// 判断文件是否存在
				if (isExitFile(path, fileName)) {
					stream = new ByteArrayInputStream(temp.getBytes());
					file = writeStreamToSDCard(path,
							fileName, stream, false);//不追加的形式

				} else {
					System.out.println("(文件)不存在,现在创建文件。。。");
					// 创建文件
					boolean appenable = false;// 不追加
					stream = new ByteArrayInputStream(temp.getBytes());
					// 写入一个空的
					writeStreamToSDCard(path,
							fileName, stream, appenable);
				}

			} else {
				System.out.println("(文件夹)不存在,现在创建文件夹。。。");
				// 创建文件夹
				try {
					creatSDDir(path);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			if (file != null)
				return true;
			return false;
		}
		
		
		/**
		 * 从sd缓存文件中获得吐槽缓存消息
		 * 2013-11-17
		 * 
		 * @author:5354xyz
		 */
		public List<SpiltModel> loadingSpilts(String path, String fileName) throws IOException {
			List<SpiltModel> SpiltsList = new ArrayList<SpiltModel>();
			// 获得路径
			File file = new File(SDRoot + File.separator + path);
			// 判断文件夹是否存在
			if (file.isDirectory() && file.exists()) {
				// 判断文件是否存在
				if (isExitFile(path, fileName)) {
					try {

						Pattern p_splilt_id = Pattern.compile("^splilt_id:.*");
						Pattern p_splilt_content = Pattern
								.compile("^splilt_content:.*");
						Pattern p_splilt_author = Pattern.compile("^splilt_author:.*");
						Pattern p_splilt_up = Pattern
								.compile("^splilt_up:.*");
						Pattern p_splilt_down = Pattern
								.compile("^splilt_down:.*");
						Pattern p_splilt_location = Pattern
								.compile("^splilt_location:.*");
						Pattern p_splilt_sex = Pattern.compile("^splilt_sex:.*");
						Pattern p_splilt_touxiang = Pattern.compile("^splilt_touxiang:.*");
						Pattern p_splilt_time = Pattern.compile("^splilt_time:.*");

						InputStream inputStream = new FileInputStream(SDRoot
								+ File.separator + path + File.separator + fileName);
						// 防止乱码，，，，，，
						InputStreamReader inputReader = new InputStreamReader(
								inputStream);
						BufferedReader bufReaderd = new BufferedReader(inputReader);
						String temp = null;
						String content = null;
						SpiltModel spilt = new SpiltModel();
						spilt.setSplilt_author("");
						spilt.setSplilt_content("");
						spilt.setSplilt_down("");
						spilt.setSplilt_id("");
						spilt.setSplilt_up("");
						spilt.setSplilt_location("");
						spilt.setSplilt_sex("");
						spilt.setSplilt_touxiang("");
						spilt.setSplilt_time("");
						while ((temp = bufReaderd.readLine()) != null) {
							
							//System.out.println("。。。"+temp);
							boolean check=true;
							//Matcher只能用来判断一次，之后失效
							Matcher m_splilt_id = p_splilt_id.matcher(temp);
							Matcher m_splilt_content = p_splilt_content.matcher(temp);
							Matcher m_splilt_author = p_splilt_author
									.matcher(temp);
							Matcher m_splilt_up = p_splilt_up.matcher(temp);
							Matcher m_splilt_down = p_splilt_down.matcher(temp);
							Matcher m_splilt_location = p_splilt_location.matcher(temp);
							Matcher m_splilt_sex = p_splilt_sex.matcher(temp);
							Matcher m_splilt_touxiang = p_splilt_touxiang.matcher(temp);
							Matcher m_splilt_time = p_splilt_time.matcher(temp);
							if (m_splilt_id.find()) {
								check=false;
								spilt.setSplilt_id(temp.substring(10));
								
							}
							if (m_splilt_up.find()){
								spilt.setSplilt_up(temp.substring(10));
								check=false;
								//System.out.println("。foundm_pushNews_up");
								}
							if (m_splilt_down.find()){
								check=false;
								spilt.setSplilt_down(temp.substring(12));
								spilt.setSplilt_content(content);
								//System.out.println("。foundm_pushNews_down");
							}
							if (m_splilt_author.find()){
								check=false;
								spilt.setSplilt_author(temp.substring(14));
								
								//System.out.println("。foundm_pushNews_title");
								}
							if (m_splilt_location.find()){
								check=false;
								spilt.setSplilt_location(temp.substring(16));
								
								//System.out.println("。foundm_pushNews_title");
								}
							if (m_splilt_sex.find()){
								check=false;
								spilt.setSplilt_sex(temp.substring(11));
								
								//System.out.println("。foundm_pushNews_title");
								}
							if (m_splilt_touxiang.find()){
								check=false;
								spilt.setSplilt_touxiang(temp.substring(16));
								}
							if (m_splilt_time.find()){
								check=false;
								spilt.setSplilt_time(temp.substring(12));
								}

							if (check) {
								
								if (m_splilt_content.find())
								{
									content = temp.substring(15);
								}
								else{
										content += temp;
										//System.out.println("content:"+content);
								}
								
							}
							// 是否读入已读的消息，默认读入未读的消息
							if (!spilt.getSplilt_down().equals("")) {
									
										System.out.println("add:"+spilt);
										SpiltsList.add(spilt);
									
										spilt = new SpiltModel();
										spilt.setSplilt_author("");
										spilt.setSplilt_content("");
										spilt.setSplilt_down("");
										spilt.setSplilt_id("");
										spilt.setSplilt_up("");
										spilt.setSplilt_location("");
										spilt.setSplilt_sex("");
										spilt.setSplilt_touxiang("");
										spilt.setSplilt_time("");

									}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("(文件)不存在,现在创建文件。。。");
					// 创建文件

					String null_info = "";
					InputStream ins = new ByteArrayInputStream(null_info.getBytes());
					boolean appenable = false;// 不追加
					// 写入一个空的
					writeStreamToSDCard(Contacts.LocalBufferFolder,
							Contacts.LocalPushNewsBufferdata, ins, appenable);
				}

			} else {
				System.out.println("(文件夹)不存在,现在创建文件夹。。。");
				// 创建文件夹
				creatSDDir(path);

			}
			
			
			//System.out.println(SpiltsList +"fiel");
			return SpiltsList;

		}
	public static List<String> getEmojiFile(Context context) {
			try {
				List<String> list = new ArrayList<String>();
				InputStream in = context.getResources().getAssets().open("emoji");//
				BufferedReader br = new BufferedReader(new InputStreamReader(in,
						"UTF-8"));
				String str = null;
				while ((str = br.readLine()) != null) {
					list.add(str);
				}

				return list;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
}