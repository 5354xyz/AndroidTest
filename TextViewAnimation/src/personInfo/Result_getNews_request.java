package personInfo;

import java.util.List;

/** 
 * @author  5354xyz
 * @version 2013-11-5 ÏÂÎç7:44:47 
 * @Eacer-mail: xiaoyizong@126.com
 */
public class Result_getNews_request {

	String status;	
	String type;
	List <PushNews> result;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<PushNews> getResult() {
		return result;
	}
	public void setResult(List<PushNews> result) {
		this.result = result;
	}
	
	
	@Override
	public String toString() {
		return "Result_pushnew_request [status=" + status + ", type=" + type
				+ ", result=" + result + "]";
	}

}
