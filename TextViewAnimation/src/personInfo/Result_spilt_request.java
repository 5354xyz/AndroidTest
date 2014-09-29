package personInfo;

import java.util.List;

/** 
 * @author  5354xyz
 * @version 2013-11-5 ÉÏÎç12:01:24 
 * @Eacer-mail: xiaoyizong@126.com
 */
public class Result_spilt_request {
		String status;	
		String type;
		List <SpiltModel> result;
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
		public List<SpiltModel> getResult() {
			return result;
		}
		public void setResult(List<SpiltModel> result) {
			this.result = result;
		}
		@Override
		public String toString() {
			return "Result [status=" + status + ", type=" + type + ", result="
					+ result + "]";
		}
		
		
		
}
