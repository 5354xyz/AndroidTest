package personInfo;

import android.os.Parcel;
import android.os.Parcelable;

public class PushNews implements Parcelable
{
	//推送消息的成员变量
	private String pushNews_id;//id
	private String pushNews_content;//内容
	private String pushNews_up;//赞
	private String pushNews_down;//板砖
	private String pushNews_title;//标题
	
	private int hasClick =0;
	
	   public int getHasClick() {
		return hasClick;
	}
	public void setHasClick(int hasClick) {
		this.hasClick = hasClick;
	}
	// 1.必须实现Parcelable.Creator接口,否则在获取Person数据的时候，会报错，如下：
	   // android.os.BadParcelableException:
	   // Parcelable protocol requires a Parcelable.Creator object called  CREATOR on class  personInfo.PushNews
	   // 2.这个接口实现了从Percel容器读取Person数据，并返回Person对象给逻辑层使用
	   // 3.实现Parcelable.Creator接口对象名必须为CREATOR，不如同样会报错上面所提到的错；
	   // 4.在读取Parcel容器里的数据事，必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
       // 5.反序列化对象
	 public static final Parcelable.Creator<PushNews> CREATOR = new Creator<PushNews>(){
		  
		          @Override
		          public PushNews createFromParcel(Parcel source) {
		              // TODO Auto-generated method stub
		              // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
		        	  PushNews p = new PushNews();
		              p.setPushNews_id(source.readString());
		              p.setPushNews_content(source.readString());
		              p.setPushNews_up(source.readString());
		              p.setPushNews_down(source.readString());
		              p.setPushNews_title(source.readString());
		              
		              return p;
		          }
		  
		          @Override
		          public PushNews[] newArray(int size) {
		              // TODO Auto-generated method stub
		              return new PushNews[size];
		          }
		      };
	public String getPushNews_title() {
		return pushNews_title;
	}
	public void setPushNews_title(String pushNews_title) {
		this.pushNews_title = pushNews_title;
	}
	public String getPushNews_id() {
		return pushNews_id;
	}
	public void setPushNews_id(String pushNews_id) {
		this.pushNews_id = pushNews_id;
	}
	public String getPushNews_content() {
		return pushNews_content;
	}
	public void setPushNews_content(String pushNews_content) {
		this.pushNews_content = pushNews_content;
	}
	public String getPushNews_up() {
		return pushNews_up;
	}
	public void setPushNews_up(String pushNews_up) {
		this.pushNews_up = pushNews_up;
	}
	public String getPushNews_down() {
		return pushNews_down;
	}
	public void setPushNews_down(String pushNews_down) {
		this.pushNews_down = pushNews_down;
	}
	@Override
	public String toString() {
		return "pushNews_id:" + pushNews_id + "\npushNews_content:"
				+ pushNews_content + "\npushNews_up:" + pushNews_up
				+ "\npushNews_down:" + pushNews_down + "\npushNews_title:"
				+ pushNews_title + "\n";
	}
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		// 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错
		// 2.序列化对象
		dest.writeString(pushNews_id);  
		dest.writeString(pushNews_content);  
		dest.writeString(pushNews_up); 
		dest.writeString(pushNews_down);  
		dest.writeString(pushNews_title);  
	}


	
}
