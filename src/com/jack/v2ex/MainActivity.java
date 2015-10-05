package com.jack.v2ex;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.jack.mainfragment.AllNodeFragment;
import com.jack.mainfragment.CollectNodeFragment;
import com.jack.mainfragment.NewFragment;
import com.jack.mainfragment.TopicFragment;
import com.jack.netutil.BitmapCache;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements OnItemClickListener, OnClickListener{

	private ActionBarDrawerToggle mActionBarDrawerToggle;
	private DrawerArrowDrawable mDArrowDrawable;
	private DrawerLayout mDrawerLayout;
	private LinearLayout menuLinear;
	private String username;
	private NewFragment newFragment;
	private TopicFragment topicFragment;
	private AllNodeFragment allNodeFragment;
	private CollectNodeFragment collectNodeFragment;
	private RelativeLayout RelativeUserinfo;
	private ListView mDrawerList;
	private NetworkImageView DrawerImage;
	private TextView DrawerName;
	private String[] DrawerString;
	private int[] DrawerDrawable;
	private TextView main_setting;
	private List<Map<String,Object>> listParam;
	private FragmentTransaction transaction;
	private FragmentManager fragmentManager=getSupportFragmentManager();
	public static RequestQueue mQueue;
	private int actionTitleStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setOverflowShowingAlways();
	    init();
	    
	    mDrawerList.setAdapter(new SimpleAdapter(this,listParam,R.layout.drawer_item,
	    		new String[]{"priture","listname"},
	    		new int[]{R.id.drawer_item_image,R.id.drawer_item_nole}));
	    
	    mActionBarDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,mDArrowDrawable,
	    		R.string.DrawerOpen,R.string.DrawerClose){
	    	@Override
	    	public void onDrawerClosed(View drawerView) {
	    		super.onDrawerClosed(drawerView);
	    		invalidateOptionsMenu();
	    		getActionBar().setTitle(DrawerString[actionTitleStatus]);
	    	}
	    	
	        @Override
	        public void onDrawerOpened(View drawerView) {
	        	getActionBar().setTitle(getString(R.string.loginlogo));
	        	username=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("username",null);
	        	if(username==null){
	        		RelativeUserinfo.setVisibility(View.GONE);
	        	}else{
	        		showUserInfo();
	        		if(DrawerImage.getDrawingCache()==null){
	        			new getUserImage().execute(username);
	        		}
	        	}
	        	super.onDrawerOpened(drawerView);
	        	invalidateOptionsMenu();
	        }
	    };
	    
	    mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
	    mActionBarDrawerToggle.syncState();
	    mDrawerList.setOnItemClickListener(this);
	    
	    main_setting.setOnClickListener(this);
	    
	    getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        
        selectItem(0);
	}
	 
    public void init(){
    	mQueue=Volley.newRequestQueue(this);
    	//用户视图区域
    	RelativeUserinfo=(RelativeLayout)findViewById(R.id.activity_main_user);
    	//用户名字
    	DrawerName=(TextView)findViewById(R.id.mainactivity_username);
    	//用户图片
    	DrawerImage=(NetworkImageView)findViewById(R.id.mainactivity_userimage);
    	DrawerImage.setDefaultImageResId(R.drawable.ic_insert_image);
    	DrawerImage.setErrorImageResId(R.drawable.ic_insert_image);
    	//设置按钮
    	main_setting=(TextView)findViewById(R.id.main_setting);
    	//获取抽屉视图
    	mDrawerLayout=(DrawerLayout)findViewById(R.id.mainactivity_drawerLayout);
    	//抽屉linearlayout
    	menuLinear=(LinearLayout)findViewById(R.id.menu_linearlayout);
    	//抽屉列表
    	mDrawerList=(ListView)menuLinear.findViewById(R.id.mainactivity_list);
		//获取控件值
		DrawerString=getResources().getStringArray(R.array.mainactivity_list);
		//初始化DrawArrowDrawable
		mDArrowDrawable=new DrawerArrowDrawable(this) {			
			@Override
			public boolean isLayoutRtl() {
				return false;
			}
		};
		
		//配备视图
		DrawerDrawable=new int[]{R.drawable.ic_drawer_home_normal,R.drawable.ic_drawer_follow_normal
				,R.drawable.ic_drawer_explore_normal,R.drawable.ic_drawer_collect_normal};
		Map<String,Object> mapParam;
		listParam=new ArrayList<Map<String,Object>>();
		int temp=DrawerDrawable.length;
		for(int i=0;i<temp;i++){
			mapParam=new HashMap<>();
			mapParam.put("priture",DrawerDrawable[i]);
			mapParam.put("listname",DrawerString[i]);
			listParam.add(mapParam);
		}
		
		username=PreferenceManager.getDefaultSharedPreferences(this).getString("username",null); 
		if(username==null){
			RelativeUserinfo.setVisibility(View.GONE);
		}else{
			showUserInfo();
			new getUserImage().execute(username);
		}
	}
    
    public void showUserInfo(){
    	DrawerName.setText(username);
		RelativeUserinfo.setVisibility(View.VISIBLE);
		RelativeUserinfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getBaseContext(),UserContent.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("username",username);
				mDrawerLayout.closeDrawer(menuLinear);
				startActivity(intent);
			}
		});
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        selectItem(position); 		
	}
	
	public void selectItem(int position){
		switch(position){
		case 0:
			ChangeFragment(0);
			changeTitleNew(0);
			mDArrowDrawable.setProgress(1f);
			break;
		case 1:
			ChangeFragment(1);
			changeTitleNew(1);
			mDArrowDrawable.setProgress(1f);
			break;
		case 2:
			ChangeFragment(2);
			changeTitleNew(2);
			mDArrowDrawable.setProgress(1f);
			break;
		case 3:
			ChangeFragment(3);
			changeTitleNew(3);
			mDArrowDrawable.setProgress(1f);
			break;
		case 4:
			ChangeFragment(4);
			changeTitleNew(4);
			mDArrowDrawable.setProgress(1f);
			break;
	    default:
			mDArrowDrawable.setProgress(1f);
			changeTitleNew(R.string.app_name);
			mDrawerLayout.closeDrawer(menuLinear);
		}
	}
	
	public void ChangeFragment(int i){
		
		if(fragmentManager==null){
			fragmentManager=getSupportFragmentManager();
		}
		
		transaction=fragmentManager.beginTransaction();
		hideFragment(transaction);
		
		switch(i){
		case 0:
			if(newFragment==null){
				newFragment=new NewFragment();
				transaction.add(R.id.mainActivity_fragment,newFragment);
			}else{
				transaction.show(newFragment);
			}
		break;
		case 1:
			if(topicFragment==null){
				topicFragment=new TopicFragment();
				transaction.add(R.id.mainActivity_fragment,topicFragment);
			}else{
				transaction.show(topicFragment);
			}
		break;
		case 2:
			if(allNodeFragment==null){
				allNodeFragment=new AllNodeFragment();
				transaction.add(R.id.mainActivity_fragment,allNodeFragment);
			}else{
				transaction.show(allNodeFragment);
			}
		break;	
		case 3:
			if(collectNodeFragment==null){
				collectNodeFragment=new CollectNodeFragment();
				transaction.add(R.id.mainActivity_fragment,collectNodeFragment);
			}else{
				transaction.show(collectNodeFragment);
			}
		break;
		}
		transaction.commit();
		mDrawerLayout.closeDrawer(menuLinear);
	}
	
	public void hideFragment(FragmentTransaction transaction){
		
		if(newFragment!=null){
			transaction.hide(newFragment);
		}
		
		if(topicFragment!=null){
			transaction.hide(topicFragment);
		}
		
		if(allNodeFragment!=null){
			transaction.hide(allNodeFragment);
		}
		
		if(collectNodeFragment!=null){
			transaction.hide(collectNodeFragment);
		}
		
	}
	
	public void changeTitleNew(int num){
		actionTitleStatus=num;
		getActionBar().setTitle(DrawerString[num]);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.addtopic, menu);
		MenuItem menuItem=menu.findItem(R.id.addtopic);
		if(PreferenceManager.getDefaultSharedPreferences(this).getString("username",null)==null){
			menuItem.setVisible(false);
		}
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.addtopic){
			Intent intent=new Intent(this,AddTopic.class);
			startActivity(intent);
		}
		
		if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
	        return true;
	    }else{
	    	return super.onOptionsItemSelected(item);
	    }
	}
	
	public boolean onMenuOpened(int featureId, Menu menu) { 
        if (featureId == Window. FEATURE_ACTION_BAR && menu != null) { 
            if (menu.getClass().getSimpleName().equals("MenuBuilder" )) { 
                try { 
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE); 
                    m.setAccessible( true); 
                    m.invoke(menu, true); 
                } catch (Exception e) { 
                } 
            } 
        } 
        return super.onMenuOpened(featureId, menu); 
    } 
     
     private void setOverflowShowingAlways() { 
        try { 
            ViewConfiguration config = ViewConfiguration.get(this); 
            Field menuKeyField = ViewConfiguration. class 
                    .getDeclaredField("sHasPermanentMenuKey" ); 
            menuKeyField.setAccessible( true); 
            menuKeyField.setBoolean(config, false); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mActionBarDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mActionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onClick(View v) {
		Intent intent=new Intent(this,SettingActivity.class);
		mDrawerLayout.closeDrawer(menuLinear);
		startActivity(intent);
	}
	
	class getUserImage extends AsyncTask<String,Void,Bitmap>{	
		@Override
		protected Bitmap doInBackground(String... params) {
			MainActivity.mQueue.add(new JsonObjectRequest(UserContent.memberInfo+params[0],null,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							try {
								DrawerImage.setImageUrl("http:"+response.getString("avatar_normal"),
										new ImageLoader(MainActivity.mQueue,new BitmapCache()));
							} catch (JSONException e) {
								Log.e("dasfasd",e.getMessage());
							}
						}
					},new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError responseerror) {

						}
					}));
			return null;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
		}
		
	}
	
}
