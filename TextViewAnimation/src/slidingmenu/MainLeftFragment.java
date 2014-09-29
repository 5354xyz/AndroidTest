package slidingmenu;


import com.example.textviewanimation.R;
import com.example.utils.RoundedImageView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainLeftFragment extends BaseFragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.slidingmenu, null);
	}

	private RoundedImageView touxiang;
	private TextView name;
	private GridView gridView;

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		this.initViews();
	}

	private void initViews() {
		View parent = this.getView();

		this.touxiang = (RoundedImageView) parent.findViewById(R.id.slidingmenu_myicon);
		this.name = (TextView) parent.findViewById(R.id.slidingmenu_myname_text);
		this.gridView = (GridView) parent.findViewById(R.id.slidingmenu_gridview);
		
		this.touxiang.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.slidingmenu_myicon: {
			Toast.makeText(this.context, "µÇÂ¼", Toast.LENGTH_SHORT).show();
			break;
		}
		
		}
	}
}
