package imageLoad;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

/** 
 * @author  5354xyz
 * @version 2013-11-28 ÏÂÎç10:04:36 
 * @Eacer-mail: xiaoyizong@126.com
 */
public class CallbackImp implements AsyncImageLoadUtil.ImageCallBack
{

	private ImageView imageView=null;
	public CallbackImp(ImageView imageView)
	{
		this.imageView=imageView;
	} 
	@Override
	public void imageLoadedSet(Drawable drawable) {
		// TODO Auto-generated method stub
		imageView.setImageDrawable(drawable);
		//drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		//textView.setCompoundDrawables(drawable, null, null, null);
	}

}
