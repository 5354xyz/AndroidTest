package personInfo;

public class PersonInfo {

	private String userName;//用户名
	private String isLogin;//登录的状态
	private String loginTime;//上次登录的时间
	private String isLoginRemember;//是否记住密码
	private String isStorageBuffer;//是否存储缓存
	private String firstLogin;//第一次登陆
	private String touxiangurl;//头像路径
	private String id;//用户的id
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTouxiangurl() {
		return touxiangurl;
	}
	public void setTouxiangurl(String touxiangurl) {
		this.touxiangurl = touxiangurl;
	}
	public String getFirstLogin() {
		return firstLogin;
	}
	public void setFirstLogin(String firstLogin) {
		this.firstLogin = firstLogin;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getIsLogin() {
		return isLogin;
	}
	public void setIsLogin(String isLogin) {
		this.isLogin = isLogin;
	}
	public String getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}
	public String getIsLoginRemember() {
		return isLoginRemember;
	}
	public void setIsLoginRemember(String isLoginRemember) {
		this.isLoginRemember = isLoginRemember;
	}
	public String getIsStorageBuffer() {
		return isStorageBuffer;
	}
	public void setIsStorageBuffer(String isStorageBuffer) {
		this.isStorageBuffer = isStorageBuffer;
	}
	@Override
	public String toString() {
		return "userName:" + userName + "\nisLogin:" + isLogin
				+ "\nloginTime:" + loginTime + "\nisLoginRemember:"
				+ isLoginRemember + "\nisStorageBuffer:" + isStorageBuffer
				+ "\nfirstLogin:" + firstLogin + "\ntouxiangurl:"+touxiangurl+"\nid:"+id+"\n";
	}
	
	

	
	
}
