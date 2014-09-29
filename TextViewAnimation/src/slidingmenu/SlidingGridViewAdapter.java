package slidingmenu;

import java.util.ArrayList;

import com.example.textviewanimation.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/** 
 * @author  5354xyz
 * @version 2014-4-22 下午1:02:15 
 * @E5354xyz-mail: xiaoyizong@126.com
 */
public class SlidingGridViewAdapter extends BaseAdapter implements OnClickListener
{
	private Context context;
	private ArrayList<MyData> datas;
	private LayoutInflater inflater;
	public SlidingGridViewAdapter(Context context, ArrayList<MyData> datas)
	{
		this.context = context;
		this.datas = datas;
		if (this.datas == null) {
			this.datas = new ArrayList<MyData>();
		}
		this.inflater = LayoutInflater.from(this.context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		MyHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(com.example.textviewanimation.R.layout.slidingmenu_inner_grid_item, null);
			holder = new MyHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.inner_img);
			holder.txt = (TextView) convertView.findViewById(R.id.txt_tile);
			holder.img.setOnClickListener(this);
			// holder.img.setOnTouchListener(this.touchListener);
			convertView.setTag(holder);
		} else {
			holder = (MyHolder) convertView.getTag();
		}
		MyData data = this.datas.get(position);
		holder.data = data;
		holder.img.setBackgroundResource(data.rid);
		holder.txt.setText(data.title);
		holder.img.setTag(holder);
		Drawable d = context.getResources().getDrawable(R.drawable.blue_rectangle);
		int h = d.getIntrinsicHeight();
		int w = d.getIntrinsicWidth();
		//System.out.println(holder.img.getWidth()+"edrfgdxhg"+holder.img.getHeight()+"}}}h:"+h+"{{w:"+w);
		convertView.setLayoutParams(new GridView.LayoutParams(w*2,h*2));
		convertView.setPadding(8, 8, 8, 8); 
		return convertView;
		
	}

	private class MyHolder {
		ImageView img;
		TextView txt;
		MyData data;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		((MyHolder) v.getTag()).data.type.toAct(context);
	}
	
	private static enum MyClassify {
		Myspilt, Mykeepping, Mywish, Mycount;

		public void toAct(Context context) {
			switch (this) {
			case Myspilt:
				Toast.makeText(context, "Myspilt发中.......", Toast.LENGTH_SHORT).show();
				break;
			case Mykeepping:
				Toast.makeText(context, "Mykeepping代开发中.......", Toast.LENGTH_SHORT).show();
				break;
			case Mywish:
				Toast.makeText(context, "Mywish代开发中.......", Toast.LENGTH_SHORT).show();
				break;
			case Mycount:
				Toast.makeText(context, "Mycount代开发中.......", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
	public static final ArrayList<MyData> getDataList() {
		ArrayList<MyData> list = new ArrayList<MyData>();
		list.add(new MyData(R.drawable.blue_rectangle, R.string.myspilt,MyClassify.Myspilt));
		list.add(new MyData(R.drawable.red_rectangle, R.string.mykeepping,MyClassify.Mykeepping));
		list.add(new MyData(R.drawable.green_rectangle, R.string.mywish,MyClassify.Mywish));
		list.add(new MyData(R.drawable.yellow_rectangle, R.string.everydaysub,MyClassify.Mycount));
		
		return list;
	}
	
	private static class MyData {
		private int rid;
		private int title;
		private MyClassify type;

		public MyData(int rid, int title,MyClassify type) {
			this.rid = rid;
			this.title = title;
			this.type=type;
		}
	}
}
