package com.jack.v2ex;


import com.jack.mainfragment.ContentFragment;

import android.app.FragmentManager;
import android.os.Bundle;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class ContentActivity extends SwipeBackActivity {

	private ContentFragment contentFragment;
	private FragmentManager fragmentManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_activity);
		if(savedInstanceState==null){
			contentFragment=new ContentFragment();
		    fragmentManager=getFragmentManager();
		    fragmentManager.beginTransaction().replace(R.id.contentFragment,contentFragment).commit();
		}
	}
}
