package pulltorefresh_listview;

public interface OnRefreshListener {

	/**
	 * 下拉刷新的接口
	 */
	public void onRefresh();
	
	/**
	 * 载入更多的接口
	 */
	public void onLoadMoring();
	
	/**
	 * 当listview有新的item出现在可视范围内的时候
	 * 2014-4-23
	 * 
	 * @author:5354xyz
	 */
	public void onOutitem(int itemNum,int from);
}
