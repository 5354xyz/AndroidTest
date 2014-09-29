package personInfo;

import java.util.List;

/** 
 * @author  5354xyz
 * @version 2014-5-8 ÏÂÎç8:26:12 
 * @E5354xyz-mail: xiaoyizong@126.com
 */
public class Result_comment_request {
	String status;
	String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	List <Comment> result;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<Comment> getResult() {
		return result;
	}
	public void setResult(List<Comment> result) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "Result_comment_request [status=" + status + ", type=" + type
				+ ", result=" + result + "]";
	}
	
	
}
