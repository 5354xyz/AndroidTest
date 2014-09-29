package imageLoad;

import java.io.File;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.example.textviewanimation.Contacts;
import com.example.utils.File_SD_utils;
import com.example.utils.ImageTools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

/** 
 * @author  5354xyz
 * @version 2013-11-28 下午9:29:24 
 * @Eacer-mail: xiaoyizong@126.com
 */

//SoftReference软引用，当内存不足时可以被回收
public class AsyncImageLoadUtil {
	//图片缓存，键是图片的url，值是是SoftReference对象，该对象指向一个Drawable对象
	private Map<String ,SoftReference<Drawable>> imageCache=new
			HashMap<String ,SoftReference<Drawable>>();
	public Drawable loadImage(final String imageUrl,final ImageCallBack imageCallBack,final boolean savetosd,Context context)
	{
		System.out.println(imageCache.isEmpty()+"3");
		final File_SD_utils futil=new File_SD_utils();
		//查询内存，看当前需要的图片是否存在缓存当中
		if(imageCache.containsKey(imageUrl))
		{
			SoftReference<Drawable> softReference=imageCache.get(imageUrl);
			if(softReference.get() != null)
			{
				System.out.println("i have loaded :"+imageUrl);
				return softReference.get();
			}
		}else if(savetosd && futil.ExistSDCard() && futil.getSDFreeSize() > 10)
		{
			String [] name =ImageTools.getpathname(imageUrl);
			System.out.println(name[0]+"!!!!"+name[1]);
			if(futil.isExitFile(Contacts.LocalBufferImageFolder, name[1]))
			{
				Bitmap bitmap = ImageTools.getPhotoFromSDCard(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+Contacts.LocalBufferImageFolder, name[1]);
				Drawable drawable = ImageTools.bitmapToDrawable(context,bitmap);
				System.out.println("i have loaded :"+imageUrl +" from sdcard");
				if(drawable != null)
					return drawable;
				else
					System.out.println(" null !!!");
			}
		}
		final Handler handler=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				System.out.println("i have not loaded :"+imageUrl+"  so i need to set");
				imageCallBack.imageLoadedSet((Drawable)msg.obj);
			}
			
		};
		
		new Thread()
		{
			public void run()
			{
				//加载图片
				Drawable imageDrawable = loadImageFromUrl(imageUrl);
				
				//生成一个SoftReference<Drawable>指向新下载的imageDrawable
				System.out.println(imageCache.isEmpty()+"1");
				imageCache.put(imageUrl, new SoftReference<Drawable>(imageDrawable));
				System.out.println(imageCache.isEmpty()+"2");
				
				//保存到SD卡
				if(savetosd && futil.ExistSDCard() && futil.getSDFreeSize() > 10){
						String [] name =ImageTools.getpathname(imageUrl);
						Bitmap imageBitmap = ImageTools.drawableToBitmap(imageDrawable);
						ImageTools.savePhotoToSDCard(imageBitmap,  Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+Contacts.LocalBufferImageFolder, name[1]);
					}
				Message message=handler.obtainMessage(0, imageDrawable);
				handler.sendMessage(message);
			}
		}.start();
		
		return null;
	}
	
	

	//根据URL从网路上下载图片
	protected Drawable loadImageFromUrl(String imageUrl)
	{
		try
		{
			return Drawable.createFromStream(new URL(imageUrl).openStream(), "src");
			
		}catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	//定义一个内部接口
	public interface ImageCallBack
	{
		public void imageLoadedSet(Drawable drawable);
	}
	
	
}
