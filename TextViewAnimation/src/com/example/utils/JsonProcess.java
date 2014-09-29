package com.example.utils;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.lang3.StringUtils;

import android.util.Log;

import com.example.textviewanimation.Contacts;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import personInfo.Result_comment_request;
import personInfo.Result_getNews_request;
import personInfo.Result_spilt_request;

public class JsonProcess {
		public Result_getNews_request getResult_getNews_request(String jsonData)
		{
			
			Gson gson=new Gson();
			System.out.println(jsonData+"@@@@@@@@");
			//gson.
			Result_getNews_request result = gson.fromJson(jsonData, Result_getNews_request.class);
			//System.out.println(result);

			return result;
		}
		
		public Result_spilt_request getResult_getSplits_request(String jsonData)
		{
			Gson gson=new Gson();
			Result_spilt_request result = gson.fromJson(jsonData, Result_spilt_request.class);

			return result;
		}
		
		public String checkStatus(String jsonData)
		{
			//如果要解析json数据，生成一个json数据
			JsonReader reader =new JsonReader(new StringReader(jsonData));
			String check=null;
			try {	
			reader.beginObject();
			while(reader.hasNext())
			{
				String tagName=reader.nextName();
				if(tagName.equals("status"))
				{
					//System.out.println("status:"+reader.nextString());
					check=reader.nextString();
				}
			}
			reader.endObject();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("check return:"+check);
			
			return check;
		}
		public String checkLoginStatus(String jsonData)
		{
			//如果要解析json数据，生成一个json数据
			JsonReader reader =new JsonReader(new StringReader(jsonData));
			String check=null;
			String id=null;
			String touxiangurl="";
			try {	
			reader.beginObject();
			while(reader.hasNext())
			{
				String tagName=reader.nextName();
				if(tagName.equals("status"))
				{
					//System.out.println("status:"+reader.nextString());
					check=reader.nextString();
				}else if(tagName.equals("id"))
				{
					id =reader.nextString();
				}else if(tagName.equals("touxiangurl"))
				{
					touxiangurl=reader.nextString();
				}
				
			}
			reader.endObject();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("check return:"+check);
			if(id !=null && check.equals("true"))
			{
				Contacts.PersonalData.setId(id);
				Contacts.PersonalData.setTouxiangurl(touxiangurl);
			}
			
			return check;
		}
		
		public String gettouxiangfromjson(String jsonData)
		{
			//如果要解析json数据，生成一个json数据
			JsonReader reader =new JsonReader(new StringReader(jsonData));
			String check=null;
			String touxiang=null;
			try {	
			reader.beginObject();
			while(reader.hasNext())
			{
				String tagName = reader.nextName();
				
				if(tagName.equals("touxiang"))
				{
					//System.out.println("status:"+reader.nextString());
					touxiang=reader.nextString();
				}else if(tagName.equals("status"))
				{
					check=reader.nextString();
				}
				
			}
			reader.endObject();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("touxiang return:"+touxiang);
			
			return touxiang;
		}
		
		public Result_comment_request getResult_comment_request(String jsonData)
		{
			Gson gson=new Gson();
			Result_comment_request result = gson.fromJson(jsonData, Result_comment_request.class);

			return result;
		}
		
		public static boolean isGoodJson(String json) {    
		       
		    try {    
		        new JsonParser().parse(json);  
		        return true;    
		    } catch (JsonParseException e) {    
		        System.out.println("bad json: " + json);    
		        return false;    
		    }    
		}
}
