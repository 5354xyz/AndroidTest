package personInfo;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/** 
 * @author  5354xyz
 * @version 2014-4-16 下午6:47:09 
 * @E5354xyz-mail: xiaoyizong@126.com
 */
public class ImageModel implements Parcelable
{
	private String img_id;
	private String img_fromid;
	private String img_path;
	private String img_type;
	private String img_description;
	@Override
	public String toString() {
		return "img_id:" + img_id + "\nimg_fromid:"
				+ img_fromid + "\nimg_path:" + img_path
				+ "\nimg_type:" + img_type + "\nimg_description:" + img_description
				+  "\n";
		
	}
	public String getImg_id() {
		return img_id;
	}
	public void setImg_id(String img_id) {
		this.img_id = img_id;
	}
	public String getImg_fromid() {
		return img_fromid;
	}
	public void setImg_fromid(String img_fromid) {
		this.img_fromid = img_fromid;
	}
	public String getImg_path() {
		return img_path;
	}
	public void setImg_path(String img_path) {
		this.img_path = img_path;
	}
	public String getImg_type() {
		return img_type;
	}
	public void setImg_type(String img_type) {
		this.img_type = img_type;
	}
	public String getImg_description() {
		return img_description;
	}
	public void setImg_description(String img_description) {
		this.img_description = img_description;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		// 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错
				// 2.序列化对象
				arg0.writeString(img_id);  
				arg0.writeString(img_fromid);  
				arg0.writeString(img_path); 
				arg0.writeString(img_type);  
				arg0.writeString(img_description);
				
	}
	
	public static final Parcelable.Creator<ImageModel> CREATOR = new Creator<ImageModel>(){
		  
        @Override
        public ImageModel createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
        	ImageModel p = new ImageModel();
        	p.setImg_id(source.readString());
            
            p.setImg_fromid(source.readString());
            p.setImg_path(source.readString());

            p.setImg_type(source.readString());
            p.setImg_description(source.readString());
            return p;
        }

        @Override
        public ImageModel[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ImageModel[size];
        }
    };
}
