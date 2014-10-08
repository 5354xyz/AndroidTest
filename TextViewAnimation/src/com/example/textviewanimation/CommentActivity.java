package com.example.textviewanimation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import personInfo.Comment;
import personInfo.CommentAdapter;
import personInfo.Result_comment_request;
import personInfo.Result_spilt_request;
import personInfo.SpiltModel;

import com.example.utils.HttpProcess;
import com.example.utils.ImageTools;
import com.example.utils.JsonProcess;
import com.example.utils.MyDialog;
import com.example.utils.SildingFinishLayout;
import com.example.utils.SildingFinishLayout.OnSildingFinishListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CommentActivity extends Activity implements OnClickListener
{
	List<Comment> newArraylist = new ArrayList<Comment>();
	//List<Comment> hotArraylist = new ArrayList<Comment>();
	private CommentAdapter newCommentAdapter = null;
	//private CommentAdapter hotCommentAdapter = null;
	Comment thiscomment =new Comment();
	private ListView hot_comment_list = null;
	private int hotcommentlength=0;
	private int newcommentlength=0;
	private ListView new_comment_list = null;
	private EditText et_comment=null;
	private ImageButton sendComment=null;
	private TextView back=null;

	String spiltId = "-1";
	private static String GETNEWCOMMENT = "1";// 最新评论
	private static String GETHOTCOMMENT = "2";// 最热评论
	private static String GETNEWDOWNCOMMENT = "3";// 更多最新评论
	private static String CommentActivityTAG = "CommentActivity";

	private Handler commentHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.obj != ""
					&& msg.arg1 == Integer.valueOf(Contacts.RequestGetComment)
							.intValue() && msg.what == 1) {
				// 若没有评论的时候默认这个
				Comment defualComment = new Comment();
				defualComment.setComment_id("-1");
				defualComment.setComment_comment_con("还没有评论哦(⊙o⊙)....");
				// 处理返回结果
				JsonProcess jsonProcess = new JsonProcess();
				boolean isJson = JsonProcess.isGoodJson(msg.obj.toString());
				if (isJson) {
					Result_comment_request result = jsonProcess
							.getResult_comment_request(msg.obj.toString());
					if (result.getType().equals(GETNEWCOMMENT)) {
						System.out.println("2--------->");
						// 代替小标题（最新评论）
						newArraylist.add(defualComment);
						if (result.getResult() != null) {

							if (newArraylist != null && newArraylist.size() > 0) {
								newcommentlength = result.getResult().size();
								for (int i = 0; i < result.getResult().size(); i++) {
									newArraylist.add(result.getResult().get(i));
								}
							} 

							
						}else {
							// 代替默认表示（没有评论）
							System.out.println("new代替默认表示（没有评论）");
							newArraylist.add(defualComment);
							findViewById(R.id.newemptytext).setVisibility(
									View.VISIBLE);
							new_comment_list
									.setEmptyView(findViewById(R.id.newemptytext));
						}
						newCommentAdapter.setList(newArraylist,
								hotcommentlength, newcommentlength);
						newCommentAdapter.notifyDataSetChanged();
					} else if (result.getType().equals(GETHOTCOMMENT)) {
						System.out.println("1--------->");
						// 代替小标题（人气评论）
						newArraylist.add(defualComment);
						if (result.getResult() != null
								&& result.getResult().size() > 0) {

							hotcommentlength = result.getResult().size();
							for (int i = 0; i < result.getResult().size(); i++) {
								newArraylist.add(i + 1,
										result.getResult().get(i));
							}

							newCommentAdapter.setList(newArraylist,
									hotcommentlength, newcommentlength);
						} else {
							// 代替默认表示（没有评论）
							System.out.println("hot代替默认表示（没有评论）");
							newArraylist.add(defualComment);
							findViewById(R.id.newemptytext).setVisibility(
									View.VISIBLE);
							new_comment_list
									.setEmptyView(findViewById(R.id.newemptytext));
						}

					}

				} else {
					Toast.makeText(CommentActivity.this, "网络连接错误！！",
							Toast.LENGTH_SHORT).show();

				}

			} else if (msg.obj != ""
					&& msg.arg1 == Integer.valueOf(Contacts.RequestPostComment)
							.intValue() && msg.what == 1) {
				// 处理登录Http请求返回的结果
				JsonProcess jsonProcess = new JsonProcess();

				String isSpilt = jsonProcess.checkStatus(msg.obj.toString());
				if (isSpilt.equals("true")) {
					Toast.makeText(CommentActivity.this, "发表成功",
							Toast.LENGTH_SHORT).show();
					newArraylist.add(thiscomment);
					newCommentAdapter.setList(newArraylist, hotcommentlength,
							newcommentlength);
					newCommentAdapter.notifyDataSetChanged();
					et_comment.setText("");
					;
				} else {
					Toast.makeText(CommentActivity.this, "发表失败",
							Toast.LENGTH_SHORT).show();
				}
			}

		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_comment);
		Intent intentfromOther = getIntent();
		spiltId = intentfromOther.getStringExtra("spiltId");
		System.out.println("spiltId:" + spiltId);
		new_comment_list = (ListView) findViewById(R.id.new_comment_list);
		et_comment=(EditText)findViewById(R.id.et_comment);
		sendComment=(ImageButton)findViewById(R.id.sendcomment);
		sendComment.setOnClickListener(this);
		back=(TextView)findViewById(R.id.comment_back);
		back.setOnClickListener(this);

		if (!spiltId.equals("-1")) {
			getComment(Contacts.RequestGetComment, spiltId, GETHOTCOMMENT);
			getComment(Contacts.RequestGetComment, spiltId, GETNEWCOMMENT);
			
		}
		newCommentAdapter = new CommentAdapter(CommentActivity.this, newArraylist);
		//hotCommentAdapter = new CommentAdapter(CommentActivity.this, hotArraylist);
		new_comment_list.setAdapter(newCommentAdapter);
		//hot_comment_list.setAdapter(hotCommentAdapter);
		
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.com_act_sildingFinishLayout);
		mSildingFinishLayout
				.setOnSildingFinishListener(new OnSildingFinishListener() {
					@Override
					public void onSildingFinish(int type) {
						// TODO Auto-generated method stub
						if (type == 1)
							CommentActivity.this.finish();
					}
				});
		mSildingFinishLayout.setSlidingDirection(1);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		System.out.println("spiltId:"+spiltId);
		newCommentAdapter.notifyDataSetChanged();
		//hotCommentAdapter.notifyDataSetChanged();
		super.onResume();
	}
	/**
	 * 参数：
	 * 		请求类型 getSpiltType_fun
	 * 		评论对应的吐槽id
	 * 		评论的类型 type 最新，最热，最新追加
	 * 2014-5-12
	 * 
	 * @author:5354xyz
	 */
	public void getComment(String getSpiltType_fun,String id,String type)
	{
		//请求推送消息的参数对
		NameValuePair pair1 = new BasicNameValuePair("Num",getSpiltType_fun);
		NameValuePair pair2 = new BasicNameValuePair("ram", String.valueOf((int)(Math.random()*1000)));
		NameValuePair pair3 = new BasicNameValuePair("GetSpiltType", getSpiltType_fun);
		NameValuePair pair4 = new BasicNameValuePair("Id", id);
		NameValuePair pair5 = new BasicNameValuePair("Type", type);
        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
        pairList.add(pair1);
        pairList.add(pair2);
        pairList.add(pair3);
        pairList.add(pair4);
        pairList.add(pair5);
        HttpProcess httpProcess1=new HttpProcess(commentHandler,getSpiltType_fun,null);
        if (!Contacts.isNetworkConnected(CommentActivity.this)){
    		Toast.makeText(CommentActivity.this, "无网络连接，请设置网络", Toast.LENGTH_LONG).show();	
    	}
    	else{
    		httpProcess1.execute(pairList);
        }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comment, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
			super.onBackPressed();
			overridePendingTransition(0, R.anim.base_slide_right_out);
		}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		System.out.println("arg0.getId():"+arg0.getId());
		switch(arg0.getId())
		{
			case R.id.comment_back:
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
				break;
			case R.id.sendcomment:
				if(Contacts.PersonalData.getIsLogin().equals("1")){
				String content = et_comment.getText().toString();
				if (!Contacts.isNetworkConnected(CommentActivity.this)) {
					Toast.makeText(CommentActivity.this, "无网络连接，请设置网络",
							Toast.LENGTH_LONG).show();
				} else {
					if (content.equals("")) {
						// 通过产生消息提醒
						Toast.makeText(CommentActivity.this, "内容不能为空",
								Toast.LENGTH_SHORT).show();

					} else {
						//请求推送消息的参数对
						NameValuePair pair1 = new BasicNameValuePair("Num", Contacts.RequestPostComment);
						NameValuePair pair2 = new BasicNameValuePair("User_name", Contacts.PersonalData.getUserName());
						NameValuePair pair3 = new BasicNameValuePair("Comment", content);
						NameValuePair pair4 = new BasicNameValuePair("Id", spiltId);
						
						thiscomment.setComment_comment_con(content);
						thiscomment.setComment_fromid(spiltId);
						thiscomment.setComment_hot("0");
						thiscomment.setComment_fromusername( Contacts.PersonalData.getUserName());
						SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd   hh:mm:ss");     
					    String   date   =   sDateFormat.format(new   java.util.Date()); 
						thiscomment.setComment_comment_time(date);
						thiscomment.setComment_fromuserid(Contacts.PersonalData.getId());
						thiscomment.setHasClick(0);
						thiscomment.setComment_type("0");
						thiscomment.setComment_fromusertouxiang(Contacts.PersonalData.getTouxiangurl());

				        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
				        pairList.add(pair1);
				        pairList.add(pair2);
				        pairList.add(pair3);
				        pairList.add(pair4);
				        
				        HttpProcess httpProcess1=new HttpProcess(commentHandler,Contacts.RequestPostComment,null);
						httpProcess1.execute(pairList);
					}
				}
				}else
				{
					Toast.makeText(CommentActivity.this, "要先登录 ，才能吐槽(⊙o⊙)哦", Toast.LENGTH_LONG).show();
		        	Intent fromComment2LoginIntent = new Intent();
		        	//讲数据加入到Intent当中去 
		        	fromComment2LoginIntent.putExtra("from",CommentActivityTAG);
		        	fromComment2LoginIntent.setClass(CommentActivity.this,
							LoginActivity.class);
					startActivity(fromComment2LoginIntent);
				}
				break;
				
		}
	}

}
