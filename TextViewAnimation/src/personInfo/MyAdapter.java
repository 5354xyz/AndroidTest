package personInfo;


import imageLoad.AsyncImageLoadUtil;
import imageLoad.CallbackImp;
import imageShow.ImageViewPagerListener;
import imageShow.MyImageViewPagerAdapter;
import imageShow.Rotate3dAnimation;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import biaoqing.FaceConversionUtil;

import com.example.textviewanimation.CommentActivity;
import com.example.textviewanimation.Contacts;
import com.example.textviewanimation.R;
import com.example.utils.HttpProcess;
import com.example.utils.RoundedImageView;
import com.example.utils.SildingFinishLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/** 
 * @author  5354xyz
 * @version 2013-11-18 下午12:53:44 
 * @Eacer-mail: xiaoyizong@126.com
 */
//这是listview Adapter
public class MyAdapter extends BaseAdapter
{
	private LayoutInflater inflater = null;
    private List<SpiltModel> items = null;
    private AsyncImageLoadUtil loader=new AsyncImageLoadUtil();
    PackageManager pm=null;
    Resources resources=null;
    private int selectedPosition = -1;    
    private Context context;
    private SildingFinishLayout imgEntryView=null;
    
    Handler listItemHandler=new Handler()
    {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
    	
    };
    
    public void setSelectedPosition(int position) {  
        selectedPosition = position;  
    } 
    
    
    
    public void setList(List<SpiltModel> arraylist)
    {
    	this.items = arraylist; 
    }
    
    public MyAdapter(Context context, List<SpiltModel> arraylist,SildingFinishLayout imgEntryView) {  
        // TODO Auto-generated constructor stub 
    	this.context=context;
        // LayoutInflater用来加载界面   
        inflater = LayoutInflater.from(context);
        //imgEntryView=(SildingFinishLayout) inflater.inflate(R.layout.pull_to_refresh_list, null);
        this.imgEntryView=imgEntryView;
        // 保存适配器中的每项的文字信息   
        this.items = arraylist;  
        
        pm = context.getPackageManager();
        try {
        	resources = pm.getResourcesForApplication("com.example.textviewanimation");
            //获取resources之后，就可以代替函数getResources( )函数了
            } catch (NameNotFoundException e) {
            e.printStackTrace();
            }
    }    

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();  
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return items.get(arg0);//position
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		final int positions=position;
		 if(convertView == null)  
	       {  
	            // 调用LayoutInflater的inflate方法加载layout文件夹中的界面   
			 convertView = inflater.inflate(R.layout.spilt_list_item, null);
	            holder = new ViewHolder();  
	            holder.author = (TextView)convertView.findViewById(R.id.spiltauthor);
	            holder.touxiang = (RoundedImageView)convertView.findViewById(R.id.spilt_author_icon);
	            holder.comment = (TextView)convertView.findViewById(R.id.spiltcomment);
	            holder.time = (TextView)convertView.findViewById(R.id.spilttime);
	            holder.content = (TextView)convertView.findViewById(R.id.spiltcontent);
	            holder.up=(Button)convertView.findViewById(R.id.spilt_but_up);
	            holder.down=(Button)convertView.findViewById(R.id.spilt_but_down);
	            holder.location=(TextView)convertView.findViewById(R.id.spiltLocation);
	            holder.pic_show=(ImageView)convertView.findViewById(R.id.spiltImageModel);
	            holder.Llayout=(LinearLayout)convertView.findViewById(R.id.spilt_item_linearLayout);
	            holder.Rlayout=(RelativeLayout)convertView.findViewById(R.id.spilt_item_relativeLayout);
	            // 保存包含当前项控件的对象   
	            convertView.setTag(holder);
	        } else {  
	            // 获取包含当前项控件的对象   
	            holder = (ViewHolder)convertView.getTag();  
	        }
		 
		// 设置当前项的内容  
		 
		 //设置昵称
		 holder.author.setText(items.get(position).getSplilt_author());
		 //设置头像
		 Drawable touxiang=null;
		 if(!items.get(position).getSplilt_touxiang().equals(""))
			 touxiang=loadImage(Contacts.BaseURL_IMAGE+items.get(position).getSplilt_touxiang(), holder.touxiang);
		 if(touxiang == null && items.get(position).getSplilt_touxiang().equals(""))
		 {
			 if(items.get(position).getSplilt_sex().equals("男")){
				 	
			 		touxiang= resources.getDrawable(R.drawable.male_touxiang_default);
			 		//touxiang.setBounds(0, 0, touxiang.getMinimumWidth(), touxiang.getMinimumHeight());
			 		}
			 else{
				
				 touxiang= resources.getDrawable(R.drawable.femal_touxiang_default);
				 //touxiang.setBounds(0, 0, touxiang.getMinimumWidth(), touxiang.getMinimumHeight());
				 }

		 }
		 holder.touxiang.setImageDrawable(touxiang);
		 
		 //设置图片,必须先设置头像因为touxiang是全局变量，否则头像会变成图片，设置完了清空
		 if(!items.get(position).getImageModel().toString().equals("[null]"))
		 {
			 holder.pic_show.setImageResource(R.drawable.black_bg);
			 holder.pic_show.setVisibility(View.VISIBLE);
			 System.out.println("@@@@"+items.get(position).getImageModel());
			 loadImage(Contacts.BaseURL_IMAGE+items.get(position).getImageModel().get(0).getImg_path(), holder.pic_show);
			 holder.pic_show.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//LayoutInflater inflater = LayoutInflater.from(context);
					//动画设置
					System.out.println("click pic");
					float centerX = imgEntryView.getWidth() / 2f;
					float centerY = imgEntryView.getHeight() / 2f;
					System.out.println("imgEntryView.getHeight():" + imgEntryView.getHeight());
					//final AlertDialog dialog = new AlertDialog.Builder(context,R.style.transparent_dialog).create();
					ViewPager imageViewPager = (ViewPager) imgEntryView.findViewById(R.id.imageViewpager);
					TextView image_num_show_text = (TextView) imgEntryView.findViewById(R.id.image_num_show_text);
					image_num_show_text.setText("1 / "+items.get(position).getImageModel().size());
					MyImageViewPagerAdapter pagerAdapter=new MyImageViewPagerAdapter(items.get(position).getImageModel(),context);
					imageViewPager.setAdapter(pagerAdapter);
					imageViewPager.setOnPageChangeListener(new ImageViewPagerListener(image_num_show_text,items.get(position).getImageModel()));
					ImageButton closebut=(ImageButton)imgEntryView.findViewById(R.id.imageClose_but);
					//dialog.setView(imgEntryView); // 自定义dialog
					
					final Rotate3dAnimation rotation = new Rotate3dAnimation(0, 90, centerX, centerY,
							310.0f, true);
					rotation.setDuration(250);
					rotation.setFillAfter(true);
					rotation.setInterpolator(new AccelerateInterpolator());
					rotation.setAnimationListener(new TurnToImageView(imgEntryView));
					
					System.out.println("click pic");
					//imgEntryView.setBackgroundColor(Color.BLUE);
					imgEntryView.startAnimation(rotation);
					
					//dialog.show();
					// 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
					closebut.setOnClickListener(new OnClickListener() {
					public void onClick(View paramView) {
						//dialog.cancel();
						float centerX = imgEntryView.getWidth() / 2f;
						float centerY = imgEntryView.getHeight() / 2f;
						//
						final Rotate3dAnimation rotation = new Rotate3dAnimation(360, 270, centerX,
								centerY, 310.0f, true);
						//
						rotation.setDuration(250);
						//
						rotation.setFillAfter(true);
						rotation.setInterpolator(new AccelerateInterpolator());
						//
						rotation.setAnimationListener(new TurnToListView(imgEntryView));
						imgEntryView.startAnimation(rotation);
					}
					});
				}});
		 }else
		 {
			 holder.pic_show.setVisibility(View.GONE);
			 //System.out.println(items.get(position).getImageModel()+"@@@@");
		 }
			 
		 //设置时间
		 String time =null;
		 time = changeTime(items.get(position).getSplilt_time());
		 holder.time.setText(time);
		 if(time.equals("just now"))
			 holder.time.setTextColor(Color.parseColor("#4EEE94"));
		 else if(time.equals("in tens"))
			 holder.time.setTextColor(Color.parseColor("#FFD700"));
		 else
			 holder.time.setTextColor(Color.parseColor("#B5B5B5"));
		 
		 //设置内容
		 SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(context, items.get(position).getSplilt_content());
		 holder.content.setText(spannableString);
		 
		 //设置评论条数
		 holder.comment.setText(" "+items.get(position).getSplilt_comment_num()+" ");
		 holder.comment.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("comment click");
				Intent fromSpiltOuttoCommentIntent = new Intent();
				fromSpiltOuttoCommentIntent.setClass(context,
						CommentActivity.class);
				fromSpiltOuttoCommentIntent.putExtra("spiltId", items.get(position).getSplilt_id());
				Activity a= (Activity)context;
				a.startActivity(fromSpiltOuttoCommentIntent);
				a.overridePendingTransition(R.anim.push_left_in,R.anim.base_slide_remain);
			}
			 
		 });
		 //设置赞和板砖
		 holder.up.setText("up "+items.get(position).getSplilt_up());
		 
		 holder.down.setText("down "+items.get(position).getSplilt_down());
		 holder.location.setText(items.get(position).getSplilt_location());
		 // 设置选中效果   
         if(selectedPosition == position)  
         {  
         holder.Llayout.setBackgroundColor(Color.parseColor("#00FF7F"));   
         } else {  
         holder.Llayout.setBackgroundColor(Color.parseColor("#F0FFF0"));  

         }
         
         holder.up.setOnClickListener(new OnClickListener()
         {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				SpiltModel focusSpiltModel=new SpiltModel();
				focusSpiltModel=(SpiltModel)getItem(positions);
				System.out.println(focusSpiltModel.getSplilt_id());
				if(focusSpiltModel.getHasClick()==0){
					focusSpiltModel.setHasClick(1);
				int up=Integer.valueOf(focusSpiltModel.getSplilt_up())+1;
				focusSpiltModel.setSplilt_up(String.valueOf(up));
				holder.up.setText("up "+String.valueOf(up));
				holder.up.setBackgroundResource(R.drawable.bg_alibuybutton_pressed);
				//请求推送消息的参数对
				NameValuePair pair1 = new BasicNameValuePair("Num", Contacts.RequestSpiltUp);
				NameValuePair pair2 = new BasicNameValuePair("Id", focusSpiltModel.getSplilt_id());


	            List<NameValuePair> pairList = new ArrayList<NameValuePair>();
	            pairList.add(pair1);
	            pairList.add(pair2);

	            HttpProcess httpProcess1=new HttpProcess(listItemHandler,Contacts.RequestSpiltUp,null);
	            httpProcess1.execute(pairList);
	            }
			}
        	 
         });
         holder.down.setOnClickListener(new OnClickListener()
         {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
					SpiltModel focusSpiltModel=new SpiltModel();
					focusSpiltModel=(SpiltModel)getItem(positions);
					System.out.println(focusSpiltModel.getSplilt_id());
					if(focusSpiltModel.getHasClick()==0){
						focusSpiltModel.setHasClick(1);
					int down=Integer.valueOf(focusSpiltModel.getSplilt_down())+1;
					focusSpiltModel.setSplilt_down(String.valueOf(down));
					holder.down.setText("down "+String.valueOf(down));
					holder.down.setBackgroundResource(R.drawable.bg_alibuybutton_pressed);
					
					//请求推送消息的参数对
					NameValuePair pair1 = new BasicNameValuePair("Num", Contacts.RequestSpiltDown);
					NameValuePair pair2 = new BasicNameValuePair("Id", focusSpiltModel.getSplilt_id());


					List<NameValuePair> pairList = new ArrayList<NameValuePair>();
					pairList.add(pair1);
					pairList.add(pair2);

					HttpProcess httpProcess1=new HttpProcess(listItemHandler,Contacts.RequestSpiltDown,null);
					httpProcess1.execute(pairList);
					}
			}
        	 
         });
		 
		return convertView;
	}
	
	public Drawable loadImage(final String imageUrl,ImageView imageView)
	{
		Drawable imagefromweb=null;
		//生成一个加载图片的回调接口实现类
		CallbackImp callbackImp=new CallbackImp(imageView);
		//这个是从缓存中取到的
		imagefromweb=loader.loadImage(imageUrl, callbackImp,true,context);
		//如果缓存不为空，否则是callback直接设置好了，不经过此步骤
		if(imagefromweb !=null)
		{
			callbackImp.imageLoadedSet(imagefromweb);
		}
		
		return imagefromweb;
	}
	
	public String changeTime(String defaulttime)
	{
		System.out.println("yuanlai"+defaulttime+"|");
		defaulttime=getTime(defaulttime);
		long time = System.currentTimeMillis();
		System.out.println("对比"+defaulttime+"|"+time+"|"+(time - Long.parseLong(defaulttime)));
		if(time - Long.parseLong(defaulttime) < 60*1000)
			defaulttime="just now";
		else if(time - Long.parseLong(defaulttime) < 60*10*1000)
			defaulttime="in tens";
		else
			defaulttime=getStrTime(defaulttime);	
	    
		return defaulttime;
	}
	
	class TurnToImageView implements AnimationListener {
		SildingFinishLayout layout=null;
		public TurnToImageView(SildingFinishLayout imgEntryView)
		{
			this.layout=imgEntryView;
			
		}
		@Override
		public void onAnimationStart(Animation animation) {
			
		}

		/**
		 * 
		 */
		@Override
		public void onAnimationEnd(Animation animation) {
			// 
			float centerX = layout.getWidth() / 2f;
			float centerY = layout.getHeight() / 2f;
			RelativeLayout content_list_RelativeLayout=(RelativeLayout)layout.findViewById(R.id.content_list_RelativeLayout);
			RelativeLayout image_show_RelativeLayout=(RelativeLayout)layout.findViewById(R.id.image_show_RelativeLayout);
			//
			content_list_RelativeLayout.setVisibility(View.GONE);
			// 
			image_show_RelativeLayout.setVisibility(View.VISIBLE);
			image_show_RelativeLayout.requestFocus();
			//
			final Rotate3dAnimation rotation = new Rotate3dAnimation(270, 360, centerX, centerY,
					310.0f, false);
			// 
			rotation.setDuration(250);
			//
			rotation.setFillAfter(true);
			rotation.setInterpolator(new AccelerateInterpolator());
			layout.startAnimation(rotation);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

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
			
			rotation.setDuration(250);
			
			rotation.setFillAfter(true);
			rotation.setInterpolator(new AccelerateInterpolator());
			layout.startAnimation(rotation);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

	}

	/**
	 * 将字符串转化为时间戳
	 * 2014-4-12
	 * 
	 * @author:5354xyz
	 */
	public static String getTime(String user_time) {
		String re_time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d;
		try {
		d = sdf.parse(user_time);
		long l = d.getTime();
		String str = String.valueOf(l);
		re_time = str;

		} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		return re_time;
		}
	
	/**
	 * 将时间戳转为字符串  
	 * 2014-4-12
	 * 
	 * @author:5354xyz
	 */
    public static String getStrTime(String cc_time) {  
        String re_StrTime = null;  
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);  
        // 例如：cc_time=1291778220  
        long lcc_time = Long.valueOf(cc_time);  
        re_StrTime = sdf.format(new Date(lcc_time *1l));  
  
        return re_StrTime;  
    }
	// 保存每项中的控件的引用   
    class ViewHolder {  
        TextView author;
        RoundedImageView touxiang;
        TextView comment;
        TextView time;  
        TextView content;  
        LinearLayout Llayout;
        RelativeLayout Rlayout;
        Button  up;
        Button  down;
        TextView  location;
        ImageView pic_show;
    }

}
