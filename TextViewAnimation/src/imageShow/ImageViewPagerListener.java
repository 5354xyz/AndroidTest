package imageShow;

import java.util.List;

import personInfo.ImageModel;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;

/** 
 * @author  5354xyz
 * @version 2014-4-20 ÏÂÎç9:34:46 
 * @E5354xyz-mail: xiaoyizong@126.com
 */
public class ImageViewPagerListener implements OnPageChangeListener 
{
	TextView imgEntryView;
	List<ImageModel> model;
	public ImageViewPagerListener(View imgEntryView,List<ImageModel> model)
	{
		this.imgEntryView=(TextView)imgEntryView;
		this.model=model;
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		imgEntryView.setText((arg0+1)+" / "+model.size());
	}

}
