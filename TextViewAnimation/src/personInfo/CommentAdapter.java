package personInfo;

import imageLoad.AsyncImageLoadUtil;
import imageLoad.CallbackImp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import personInfo.MyAdapter.ViewHolder;

import com.example.textviewanimation.Contacts;
import com.example.textviewanimation.R;
import com.example.utils.HttpProcess;
import com.example.utils.RoundedImageView;
import com.example.utils.SildingFinishLayout;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/** 
 * @author  5354xyz
 * @version 2014-5-8 下午9:20:28 
 * @E5354xyz-mail: xiaoyizong@126.com
 */
public class CommentAdapter extends BaseAdapter
{
	Context context=null;
	private LayoutInflater inflater = null;
    private List<Comment> items = null;
    PackageManager pm=null;
    Resources resources=null;
    private int hotcomment_length=0;
    private int newcomment_length=0;
    private int outofdata =0;
    private boolean tag = false;
    Handler listItemHandler=new Handler()
    {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
    	
    };
	public CommentAdapter(Context context, List<Comment> arraylist) {  
        // TODO Auto-generated constructor stub 
    	this.context=context;
        // LayoutInflater用来加载界面   
        inflater = LayoutInflater.from(context);
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
	
	public void setList(List<Comment> list,int hotcomment_length,int newcomment_length)
	{
		this.items = list;
		System.out.println("list-size:"+this.items.size()+" | hotlegth:"+this.hotcomment_length+" | list:"+list);
		if(hotcomment_length>0)
			this.hotcomment_length=hotcomment_length;
		if(newcomment_length>0)
			this.newcomment_length=newcomment_length;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();  
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return items.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		System.out.println("getView ----" +position + "----" + convertView);
		CommentViewHolder holder = null;
		final CommentViewHolder holder_in;
		final int positions = position;
		//View view = convertView;
		//返回小标题的view
		if (position == 0) {
			System.out.println("position " + position);
			View addView = inflater.inflate(R.layout.comment_list_title, null);
			TextView title = (TextView) addView
					.findViewById(R.id.new_comment_title);
			tag= true;
			return addView;
		}else if(hotcomment_length ==0 && position == 1){
			System.out.println("position " + position);
			System.out.println("hot defualt");
			View nullView = inflater.inflate(R.layout.comment_list_null, null);
			tag= true;
			return nullView;
		}else if((hotcomment_length ==0 && position == hotcomment_length+2) 
				||((hotcomment_length !=0 && position == hotcomment_length+1))){
			System.out.println("position " + position);
			View addView = inflater.inflate(R.layout.comment_list_title, null);
			TextView title = (TextView) addView
					.findViewById(R.id.new_comment_title);
			title.setText(R.string.new_comment_list);
			System.out.println("addView ----" +position + "----" + addView);
			tag= true;
			return addView;
		}else if(newcomment_length ==0 && position == hotcomment_length+3){
			System.out.println("position " + position);
			System.out.println("new defualt");
			View nullView = inflater.inflate(R.layout.comment_list_null, null);
			TextView defualt_text = (TextView) nullView
					.findViewById(R.id.emptytext);
			defualt_text.setText(R.string.new_defualt_tips);
			tag= true;
			return nullView;
		}
		else {
				if( convertView.getTag() == null || convertView == null ){
					System.out.println("in!!!!!!");
					holder = new CommentViewHolder();
					// 调用LayoutInflater的inflate方法加载layout文件夹中的界面
					convertView = inflater
							.inflate(R.layout.comment_list_item, null);
					
					holder.author = (TextView) convertView
							.findViewById(R.id.commentauthor);
					holder.touxiang = (RoundedImageView) convertView
							.findViewById(R.id.comment_author_icon);
					holder.hot = (TextView) convertView
							.findViewById(R.id.commenthot);
					holder.time = (TextView) convertView
							.findViewById(R.id.commenttime);
					holder.content = (TextView) convertView
							.findViewById(R.id.commentcontent);
					holder.Llayout = (LinearLayout) convertView
							.findViewById(R.id.comment_item_linearLayout);
					// 保存包含当前项控件的对象
					System.out.println("aaaa");
					convertView.setTag(holder);
					holder = (CommentViewHolder) convertView.getTag();
				}else{
					System.out.println("bbbb");
					holder = (CommentViewHolder) convertView.getTag();
					}
			if(holder == null)
				System.out.println("holder == null");
			holder_in = holder;
			System.out.println("position:"+position+"  items.get(position):"+items.get(position));
			holder.hot.setText(" " + items.get(position).getComment_hot());
			holder.hot.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Comment focusComment = new Comment();
					focusComment = (Comment) getItem(positions);
					System.out.println(focusComment.getComment_id());
					if (focusComment.getHasClick() == 0) {
						focusComment.setHasClick(1);
						int hot = Integer.valueOf(focusComment.getComment_hot()) + 1;
						focusComment.setComment_hot(String.valueOf(hot));
						holder_in.hot.setText(" " + String.valueOf(hot));
						holder_in.hot
								.setBackgroundResource(R.drawable.bg_alibuybutton_pressed);
						// 请求推送消息的参数对
						NameValuePair pair1 = new BasicNameValuePair("Num",
								Contacts.RequestCommentHotUp);
						NameValuePair pair2 = new BasicNameValuePair("Id",
								focusComment.getComment_id());

						List<NameValuePair> pairList = new ArrayList<NameValuePair>();
						pairList.add(pair1);
						pairList.add(pair2);

						HttpProcess httpProcess1 = new HttpProcess(
								listItemHandler, Contacts.RequestCommentHotUp,
								null);
						httpProcess1.execute(pairList);
					}
				}

			});

			holder.time.setText(items.get(position).getComment_comment_time());
			// System.out.println();
			holder.content
					.setText(items.get(position).getComment_comment_con());
			holder.author
					.setText(items.get(position).getComment_fromusername());

			// 设置头像
			System.out.println("设置头像");
			Drawable touxiang = null;
			if (!items.get(position).getComment_fromusertouxiang().equals(""))
				touxiang = loadImage(
						Contacts.BaseURL_IMAGE
								+ items.get(position)
										.getComment_fromusertouxiang(),
						holder.touxiang);
			if (touxiang == null
					&& items.get(position).getComment_fromusertouxiang()
							.equals("")) {
				touxiang = resources
						.getDrawable(R.drawable.male_touxiang_default);
			}
			holder.touxiang.setImageDrawable(touxiang);
			System.out.println("设置头像   OK");
		}

		return convertView;
		
	}
	
	public Drawable loadImage(final String imageUrl,ImageView imageView)
	{
		Drawable imagefromweb=null;
		AsyncImageLoadUtil loader=new AsyncImageLoadUtil();
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
	// 保存每项中的控件的引用   
    class CommentViewHolder {  
        TextView author;
        RoundedImageView touxiang;
        TextView hot;
        TextView time;  
        TextView content;  
        LinearLayout Llayout;
       
    }

}
