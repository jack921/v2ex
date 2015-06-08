package com.jack.v2ex;

import com.jack.mainfragment.SettingFragment;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class SettingActivity extends SwipeBackActivity{

	SettingFragment settingFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		settingFragment=new SettingFragment();
		
		FragmentManager fragmentManager=getFragmentManager();
		FragmentTransaction transaction=fragmentManager.beginTransaction();
		transaction.replace(R.id.activitymain_setting,settingFragment);
		transaction.commit();
	}
	
}
