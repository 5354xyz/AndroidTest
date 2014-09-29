package personInfo;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aphidmobile.utils.AphidLog;
import com.aphidmobile.utils.UI;
import com.example.textviewanimation.Contacts;
import com.example.textviewanimation.R;
import com.example.utils.HttpProcess;
import com.example.utils.JsonProcess;
import com.example.utils.MyDialog;

public class ContentViewAdapter extends BaseAdapter {

	private LayoutInflater inflater;//外观
	private int repeatCount = 1;
	//private Dialog dialog = null;
	PushNews pushNewsFoucus = null;
	Context context=null;
	Activity mActivity=null;

	private List<PushNews> pushNews= null;

	Handler contentHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.obj != ""
					&& msg.arg1 == Integer.valueOf(Contacts.RequestRefleshContent)
							.intValue())
			{
				JsonProcess jsonProcess1 = new JsonProcess();
				Result_getNews_request result = jsonProcess1
						.getResult_getNews_request(msg.obj.toString());
				PushNews refleshPushNews=result.getResult().get(0);
				UI
		        .<Button>findViewById(inflater.inflate(R.layout.activity_content, null), R.id.content_up).setText("up" + refleshPushNews.getPushNews_up());
				UI
		        .<Button>findViewById(inflater.inflate(R.layout.activity_content, null), R.id.content_down).setText("down" + refleshPushNews.getPushNews_down());
				//dialog.dismiss();
			}else if(msg.arg2==Contacts.TIME_OUT)
			{
				Toast.makeText(context, "网络连接超时喔(⊙o⊙)。。。", Toast.LENGTH_LONG).show();
			}
		}

	};
	public ContentViewAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		pushNews = Contacts.MessageShow;
		this.context=context;
	}

	@Override
	public int getCount() {
		return pushNews.size() * repeatCount;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View layout =inflater.inflate(R.layout.activity_content, null);
		AphidLog.d("created new view from adapter: %d", position);
		System.out.println("foucusNews_position_position:"+position);
		
		//View titlelayout = inflater.inflate(R.layout.titlebar_content, null);
		if(position>1)
			position-=1;
		
		pushNewsFoucus = pushNews.get(position);
		UI
        .<TextView>findViewById(layout, R.id.contentTextId)
        .setText(AphidLog.format("%s", pushNewsFoucus.getPushNews_content()));
		UI
        .<TextView>findViewById(layout, R.id.contentTextId).setMovementMethod(ScrollingMovementMethod.getInstance());
		UI
        .<TextView>findViewById(layout, R.id.contentTitleId)
        .setText(AphidLog.format(" %s",pushNewsFoucus.getPushNews_title()));
		UI
        .<Button>findViewById(layout, R.id.content_up).setText("up" + pushNewsFoucus.getPushNews_up());
		UI
        .<Button>findViewById(layout, R.id.content_up)
        .setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
        	  System.out.println("!!!!!!!!!!!click|||||_________|||||||||||||||"+pushNewsFoucus.getPushNews_id());
  			if (pushNewsFoucus.getHasClick() == 0) {
  				pushNewsFoucus.setHasClick(1);
  				UI
  		        .<ImageView>findViewById(layout, R.id.content_up_image).setVisibility(View.VISIBLE);
  				Animation hyperspaceJumpAnimation = AnimationUtils
  						.loadAnimation(inflater.getContext(), R.anim.content_up);
  				UI
  		        .<ImageView>findViewById(layout, R.id.content_up_image).startAnimation(hyperspaceJumpAnimation);
  				int up = Integer.valueOf(pushNewsFoucus.getPushNews_up()) + 1;
  				UI
  		        .<Button>findViewById(layout, R.id.content_up).setText("up" + String.valueOf(up));
  				UI
  		        .<Button>findViewById(layout, R.id.content_up).setBackgroundResource(R.drawable.bg_alibuybutton_pressed);
  				// 请求推送消息的参数对
  				NameValuePair pair1 = new BasicNameValuePair("Num",
  						Contacts.RequestPushNewsUp);
  				NameValuePair pair2 = new BasicNameValuePair("Id",
  						pushNewsFoucus.getPushNews_id());

  				List<NameValuePair> pairList = new ArrayList<NameValuePair>();
  				pairList.add(pair1);
  				pairList.add(pair2);
  				//contentHandler.obtainMessage();
  				HttpProcess httpProcess1 = new HttpProcess(contentHandler,
  						Contacts.RequestPushNewsUp,null);
  				if (!Contacts.isNetworkConnected(context)) {
              		Toast.makeText(context, "无网络连接，请设置网络", Toast.LENGTH_LONG).show();
              	}
              	else{
              		httpProcess1.execute(pairList);
  				}
  			}

  		
            //inflater.getContext().startActivity(intent);
          }
        });
		
		UI
        .<Button>findViewById(layout, R.id.content_down).setText("down" + pushNewsFoucus.getPushNews_down());
		UI
        .<Button>findViewById(layout, R.id.content_down)
        .setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
          if (pushNewsFoucus.getHasClick() == 0) {
				pushNewsFoucus.setHasClick(1);

				UI
  		        .<ImageView>findViewById(layout, R.id.content_down_image).setVisibility(View.VISIBLE);
				Animation downAnimation = AnimationUtils.loadAnimation(
						context, R.anim.content_down);
				downAnimation.setFillEnabled(true);
				downAnimation.setFillAfter(true);

				UI
  		        .<ImageView>findViewById(layout, R.id.content_down_image).startAnimation(downAnimation);

				int down = Integer.valueOf(pushNewsFoucus.getPushNews_down()) + 1;
				UI
		        .<Button>findViewById(layout, R.id.content_down).setText("down" + String.valueOf(down));
				UI
		        .<Button>findViewById(layout, R.id.content_down)
						.setBackgroundResource(R.drawable.bg_alibuybutton_pressed);
				// 请求推送消息的参数对
				NameValuePair pair1 = new BasicNameValuePair("Num",
						Contacts.RequestPushNewsDown);
				NameValuePair pair2 = new BasicNameValuePair("Id",
						pushNewsFoucus.getPushNews_id());

				List<NameValuePair> pairList = new ArrayList<NameValuePair>();
				pairList.add(pair1);
				pairList.add(pair2);

				HttpProcess httpProcess1 = new HttpProcess(contentHandler,
						Contacts.RequestPushNewsDown,null);
				if (!Contacts.isNetworkConnected(context)) {
          		Toast.makeText(context, "无网络连接，请设置网络", Toast.LENGTH_LONG).show();
          	}
          	else{
          		httpProcess1.execute(pairList);
				}
			}
          }
        });
		
		
		UI
        .<Button>findViewById(layout, R.id.contentToolbar_back_but)
        .setOnClickListener(new BackButtonOnclickListener());
		return layout;
	}
	
	class BackButtonOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mActivity=(Activity)context;
			mActivity.finish();
			mActivity.overridePendingTransition(0, R.anim.base_slide_right_out);
		}

	}


}
