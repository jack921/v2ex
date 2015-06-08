package com.jack.v2ex;

import java.util.List;
import com.markmao.pullscrollview.ui.widget.PullScrollView.OnTurnListener;
import com.markmao.pullscrollview.ui.widget.PullScrollView;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.ImageLoader;
import com.jack.dataobject.MemberInfoObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jack.dataobject.DataObject;
import com.jack.netutil.BitmapCache;
import com.jack.netutil.ImageHandler;
import com.jack.netutil.ParseJson;
import com.jack.netutil.VolleyImageGetter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class UserContent extends SwipeBackActivity implements OnTurnListener{

	public  static String memberInfo="https://www.v2ex.com/api/members/show.json?username=";
    public  static String userTopic="https://www.v2ex.com/api/topics/show.json?username=";
	private PullScrollView mPullScrollView;
	private NetworkImageView userimage;
    private LinearLayout mLinearLayout;
    private TextView userbio;
    private ImageLoader imageLoader;
    private TextView View_username;
    private TextView huiyuannum;
    private String username;
    private View view;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_message);	
		initView();
		
		new getUserInfo().execute();
		mPullScrollView.setOnTurnListener(this);
	}
	
	public void initView(){
		mPullScrollView=(PullScrollView)findViewById(R.id.scrollview);
		mLinearLayout=(LinearLayout)findViewById(R.id.usermessage_linearlayout);
		View_username=(TextView)findViewById(R.id.user_name);
		userimage=(NetworkImageView)findViewById(R.id.user_image);
		huiyuannum=(TextView)findViewById(R.id.user_info);
		userbio=(TextView)findViewById(R.id.user_bio);
		username=getIntent().getStringExtra("username");
		mPullScrollView.setHeader((ImageView)findViewById(R.id.header_image));
		imageLoader=new ImageLoader(MainActivity.mQueue,new BitmapCache());
	}
	
	class getUserInfo extends AsyncTask<Void,Void,Void>{
		@Override
		protected Void doInBackground(Void... params) {
			
			MainActivity.mQueue.add(new StringRequest(memberInfo+username,
				    new Response.Listener<String>() {
						@Override
						public void onResponse(String json) {
							MemberInfoObject member=ParseJson.parseMemberInfo(json);
							View_username.setText(username);
							huiyuannum.setText("第"+member.getId()+"会员"+"");
							userimage.setDefaultImageResId(R.drawable.default_image);
							userimage.setErrorImageResId(R.drawable.default_image);
							String user_bio=member.getBio();
							if(user_bio.equals("")){
								userbio.setVisibility(View.GONE);
							}else{
								userbio.setText(user_bio);
							}
							userimage.setImageUrl("http:"+member.getAvatar_large(),imageLoader);
						}	
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {
							Log.e("getContentError",arg0.getMessage());
						}
					}));
				 	
			MainActivity.mQueue.add(new StringRequest(userTopic+username,
				    new Response.Listener<String>() {
						@Override
						public void onResponse(String json) {
							final List<DataObject> listObjects=ParseJson.parseNewObjects(json);
							for(int i=0;i<listObjects.size();i++){
								view=LayoutInflater.from(getBaseContext()).inflate(R.layout.usercontent_item,null);
								((TextView)view.findViewById(R.id.usercontent_title)).setText(listObjects.get(i).getTitle());
								String usercontent_content=listObjects.get(i).getContent_rendered();
								if(usercontent_content.equals("")){
									((TextView)view.findViewById(R.id.usercontent_content)).setVisibility(View.GONE);
									((TextView)view.findViewById(R.id.usercontent_driver)).setVisibility(View.GONE);
								}else{
									TextView tempuserContent=(TextView)view.findViewById(R.id.usercontent_content);
									tempuserContent.setText(Html.fromHtml(usercontent_content,
											new VolleyImageGetter(getBaseContext(),tempuserContent),
											new ImageHandler(getBaseContext())));
								}
								((TextView)view.findViewById(R.id.usercontent_reply)).setText(listObjects.get(i).getReplies()+"回复");
								((TextView)view.findViewById(R.id.usercontent_node)).setText(listObjects.get(i).getNode().getTitle());
								final int a=i;
								view.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										comeActivity(listObjects.get(a));
									}
								});
								 mLinearLayout.addView(view); 
							}
						}	
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {
							Log.e("getUserContentError",arg0.getMessage());
						}
					}));
			return null;
		}
	} 

	@Override
	public void onTurn() {
		new getUserInfo().execute();
	}
	
	public void comeActivity(DataObject dataObject){
		Bundle bundle=new Bundle();	
		bundle.putString("title",dataObject.getTitle());
		bundle.putString("content",dataObject.getContent_rendered());
		bundle.putInt("id",dataObject.getId());
		bundle.putInt("replies",dataObject.getReplies());
		bundle.putString("name",dataObject.getMember().getUsername());
		bundle.putString("node",dataObject.getNode().getTitle());
	    NetworkImageView imageView=((NetworkImageView)findViewById(R.id.user_image));
		imageView.setDrawingCacheEnabled(true);
	    bundle.putParcelable("image",imageView.getDrawingCache());
		Intent intent=new Intent(getBaseContext(),ContentActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		switch (level) {
		case TRIM_MEMORY_UI_HIDDEN:
		mLinearLayout.removeAllViews();
		break;
		default:
		break;
		}
	}
	
}
