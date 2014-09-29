package personInfo;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class SpiltModel implements Parcelable
{
	private String splilt_id;//id
	private String splilt_content;//内容
	private String splilt_author;//作者
	private String splilt_up;//赞
	private String splilt_down;//板砖
	private String splilt_location;//地点
	private String splilt_sex;//性别
	private String splilt_touxiang;//头像
	private String splilt_time;//发表时间
	private String splilt_comment_num;//发表总共的评论条数
	private List <ImageModel> splilt_picSpilt;//附带的图片
	
	
	public String getSplilt_comment_num() {
		return splilt_comment_num;
	}
	public void setSplilt_comment_num(String splilt_comment_num) {
		this.splilt_comment_num = splilt_comment_num;
	}
	public List<ImageModel> getImageModel() {
		return splilt_picSpilt;
	}
	public void setImageModel(List<ImageModel> splilt_picSpilt) {
		this.splilt_picSpilt = splilt_picSpilt;
	}
	public String getSplilt_time() {
		return splilt_time;
	}
	public void setSplilt_time(String splilt_time) {
		this.splilt_time = splilt_time;
	}
	public String getSplilt_sex() {
		return splilt_sex;
	}
	public void setSplilt_sex(String splilt_sex) {
		this.splilt_sex = splilt_sex;
	}
	public String getSplilt_touxiang() {
		return splilt_touxiang;
	}
	public void setSplilt_touxiang(String splilt_touxiang) {
		this.splilt_touxiang = splilt_touxiang;
	}
	private int hasClick =0;
	
	public String getSplilt_location() {
		return splilt_location;
	}
	public void setSplilt_location(String splilt_location) {
		this.splilt_location = splilt_location;
	}
	public int getHasClick() {
		return hasClick;
	}
	public void setHasClick(int hasClick) {
		this.hasClick = hasClick;
	}
	public static final Parcelable.Creator<SpiltModel> CREATOR = new Creator<SpiltModel>(){
		  
        @Override
        public SpiltModel createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
        	SpiltModel p = new SpiltModel();
        	p.setSplilt_id(source.readString());
            
            p.setSplilt_content(source.readString());
            p.setSplilt_author(source.readString());

            p.setSplilt_up(source.readString());
            p.setSplilt_down(source.readString());
            p.setSplilt_location(source.readString());
            p.setSplilt_sex(source.readString());
            p.setSplilt_touxiang(source.readString());
            p.setSplilt_time(source.readString());
            p.setSplilt_comment_num(source.readString());
            // 必须实例化   
            p.splilt_picSpilt = new ArrayList<ImageModel>();   
            source.readList(p.splilt_picSpilt, getClass().getClassLoader()); 
           
            return p;
        }

        @Override
        public SpiltModel[] newArray(int size) {
            // TODO Auto-generated method stub
            return new SpiltModel[size];
        }
    };
	public String getSplilt_author() {
		return splilt_author;
	}
	public void setSplilt_author(String splilt_author) {
		this.splilt_author = splilt_author;
	}
	
	
	public String getSplilt_id() {
		return splilt_id;
	}
	public void setSplilt_id(String splilt_id) {
		this.splilt_id = splilt_id;
	}
	public String getSplilt_content() {
		return splilt_content;
	}
	public void setSplilt_content(String splilt_content) {
		this.splilt_content = splilt_content;
	}
	public String getSplilt_up() {
		return splilt_up;
	}
	public void setSplilt_up(String splilt_up) {
		this.splilt_up = splilt_up;
	}
	public String getSplilt_down() {
		return splilt_down;
	}
	public void setSplilt_down(String splilt_down) {
		this.splilt_down = splilt_down;
	}

	@Override
	public String toString() {
		return "splilt_id:" + splilt_id + "\nsplilt_content:"
				+ splilt_content + "\nsplilt_author:" + splilt_author
				+"\nsplilt_up:" + splilt_up + "\nsplilt_down:" + splilt_down
				+"\n"+"splilt_location:"+splilt_location+"\nsplilt_sex:"+splilt_sex
				+"\nsplilt_touxiang:"+splilt_touxiang+"\nsplilt_time:"+splilt_time
				+"\nsplilt_comment_num:"+splilt_comment_num
				+"\nsplilt_picSpilt:"+splilt_picSpilt+"\n";
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		// 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错
		// 2.序列化对象
		dest.writeString(splilt_id);  
		dest.writeString(splilt_content);  
		dest.writeString(splilt_author); 
		dest.writeString(splilt_up);  
		dest.writeString(splilt_down);
		dest.writeString(splilt_location);
		dest.writeString(splilt_sex);
		dest.writeString(splilt_touxiang);
		dest.writeString(splilt_time);
		dest.writeString(splilt_comment_num);
		//注意不同类型
		dest.writeList(splilt_picSpilt);
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
