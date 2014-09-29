package personInfo;
/** 
 * @author  5354xyz
 * @version 2014-5-8 下午8:15:16 
 * @E5354xyz-mail: xiaoyizong@126.com
 */
public class Comment {
	//评论实体的成员变量
	private String comment_id;//id
	private String comment_fromid;//对应消息或吐槽的id
	private String comment_type;//是消息或吐槽的评论
	private String comment_comment_con;//评论内容
	private String comment_comment_time;//评论时间
	private String comment_fromuserid;//评论用户的id
	private String comment_fromusername;//评论用户的用户名
	private String comment_fromusertouxiang;//评论用户的头像路径
	private String comment_hot;//评论热度
	
	
	
	private int hasClick =0;
	public int getHasClick() {
		return hasClick;
	}
	public void setHasClick(int hasClick) {
		this.hasClick = hasClick;
	}
	public String getComment_id() {
		return comment_id;
	}
	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}
	public String getComment_fromid() {
		return comment_fromid;
	}
	public void setComment_fromid(String comment_fromid) {
		this.comment_fromid = comment_fromid;
	}
	public String getComment_type() {
		return comment_type;
	}
	public void setComment_type(String comment_type) {
		this.comment_type = comment_type;
	}
	public String getComment_comment_con() {
		return comment_comment_con;
	}
	public void setComment_comment_con(String comment_comment_con) {
		this.comment_comment_con = comment_comment_con;
	}
	public String getComment_comment_time() {
		return comment_comment_time;
	}
	public void setComment_comment_time(String comment_comment_time) {
		this.comment_comment_time = comment_comment_time;
	}
	public String getComment_fromuserid() {
		return comment_fromuserid;
	}
	public void setComment_fromuserid(String comment_fromuserid) {
		this.comment_fromuserid = comment_fromuserid;
	}
	public String getComment_hot() {
		return comment_hot;
	}
	public void setComment_hot(String comment_hot) {
		this.comment_hot = comment_hot;
	}
	public String getComment_fromusername() {
		return comment_fromusername;
	}
	public void setComment_fromusername(String comment_fromusername) {
		this.comment_fromusername = comment_fromusername;
	}
	public String getComment_fromusertouxiang() {
		return comment_fromusertouxiang;
	}
	public void setComment_fromusertouxiang(String comment_fromusertouxiang) {
		this.comment_fromusertouxiang = comment_fromusertouxiang;
	}
	@Override
	public String toString() {
		return "Comment [comment_id=" + comment_id + ", comment_fromid="
				+ comment_fromid + ", comment_type=" + comment_type
				+ ", comment_comment_con=" + comment_comment_con
				+ ", comment_comment_time=" + comment_comment_time
				+ ", comment_fromuserid=" + comment_fromuserid
				+ ", comment_fromusername=" + comment_fromusername
				+ ", comment_fromusertouxiang=" + comment_fromusertouxiang
				+ ", comment_hot=" + comment_hot + "]";
	}

	

}
