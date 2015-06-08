package com.jack.mainfragment;

import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.jack.dataobject.ReplyObject;
import com.jack.netutil.BitmapCache;
import com.jack.netutil.ImageHandler;
import com.jack.netutil.NetUtilImpl;
import com.jack.netutil.ParseJson;
import com.jack.netutil.VolleyImageGetter;
import com.jack.v2ex.MainActivity;
import com.jack.v2ex.R;
import com.jack.v2ex.UserContent;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ContentFragment extends Fragment implements OnClickListener,OnRefreshListener{

	public static String Reply="https://www.v2ex.com/api/replies/show.json?topic_id=";
	private PullToRefreshLayout mPullToRefreshLayout;
	private ProgressBar content_progressBar;
	private ScrollView content_scroll;
	private TextView contentContent;
	private ImageView contentImage;
	private LinearLayout replyList;
	private LinearLayout commentLayout;
	private TextView contentReply;
	private TextView contentTitle;
	private TextView contentName;
	private TextView contentTime;
	private TextView contentNode;
	private TextView contentComment;
	private View content_reply;
	private EditText commentContent;
	private ImageButton sendComment;
	private ImageLoader imageLoader;
	private Bundle bundle;
	private ProgressDialog progressDialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		mPullToRefreshLayout=(PullToRefreshLayout)inflater.inflate(R.layout.content_show,container,false);
		initView();
		initdata();
		
		setHasOptionsMenu(true);
    	ActionBarPullToRefresh.from(getActivity())
        .allChildrenArePullable()
        .listener(this)
        .setup(mPullToRefreshLayout);
		
		content_progressBar.setVisibility(View.VISIBLE);
		content_scroll.setVisibility(View.GONE);
		contentImage.setOnClickListener(this);
		new getReply().execute();
		return mPullToRefreshLayout;
	}
	
	public void initdata(){
		contentTitle.setText(bundle.getString("title"));
		contentImage.setImageBitmap((Bitmap) bundle.getParcelable("image"));
		contentNode.setText(bundle.getString("node"));
		contentName.setText(bundle.getString("name"));
		contentReply.setText(bundle.getInt("replies")+"");
		if(bundle.getString("content").equals("")){
			contentContent.setVisibility(View.GONE);
		}else{
			contentContent.setText(Html.fromHtml(bundle.getString("content")
					,new VolleyImageGetter(getActivity(),contentContent)
					,new ImageHandler(getActivity())));
			contentContent.setMovementMethod(LinkMovementMethod.getInstance());
		}
		contentTime.setText("一天前");
		//设置评论区
		if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("username",null)==null){
			commentLayout.setVisibility(View.GONE);
		}else{
			commentContent=(EditText)mPullToRefreshLayout.findViewById(R.id.commentcontent);
			sendComment=(ImageButton)mPullToRefreshLayout.findViewById(R.id.sendComment);
			sendComment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!commentContent.getText().toString().equals("")){
						progressDialog=ProgressDialog.show(getActivity(),null,getActivity().getString(R.string.repleying));
						postComment(bundle.getInt("id"));
					}else{
						Toast.makeText(getActivity(),getActivity().getString(R.string.repleynull),Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
		
	}
	
	public void initView(){
		commentLayout=(LinearLayout)mPullToRefreshLayout.findViewById(R.id.linearrepley);
		contentTitle=(TextView)mPullToRefreshLayout.findViewById(R.id.content_title);
		contentImage=(ImageView)mPullToRefreshLayout.findViewById(R.id.content_image);
		contentName=(TextView)mPullToRefreshLayout.findViewById(R.id.content_name);
		contentTime=(TextView)mPullToRefreshLayout.findViewById(R.id.content_time);
		contentReply=(TextView)mPullToRefreshLayout.findViewById(R.id.content_repley);
		contentNode=(TextView)mPullToRefreshLayout.findViewById(R.id.content_node);
		contentContent=(TextView)mPullToRefreshLayout.findViewById(R.id.content_content);
		replyList=(LinearLayout)mPullToRefreshLayout.findViewById(R.id.content_list);
		content_scroll=(ScrollView)mPullToRefreshLayout.findViewById(R.id.Content_Scroll);
		contentComment=(TextView)mPullToRefreshLayout.findViewById(R.id.content_comment);
		content_progressBar=(ProgressBar)mPullToRefreshLayout.findViewById(R.id.Content_Progress);
		imageLoader=new ImageLoader(MainActivity.mQueue,new BitmapCache());
		bundle=getActivity().getIntent().getExtras();
	}

	@Override
	public void onClick(View v) {
		Intent intent=new Intent(getActivity(),UserContent.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("username",contentName.getText().toString());
		getActivity().startActivity(intent);
	}
    
	class getReply extends AsyncTask<Void,Void,Void>{
		@Override
		protected Void doInBackground(Void... params) {
			MainActivity.mQueue.add(new StringRequest(Reply+bundle.getInt("id")+"",
				    new Response.Listener<String>() {
						@Override
						public void onResponse(String json) {
							//replyList.removeAllViews();
							final List<ReplyObject> listObject=ParseJson.parseReplyObject(json);
							if(listObject.size()==0){
								contentComment.setVisibility(View.GONE);
							}
							for(int i=0;i<listObject.size();i++){
								content_reply=LayoutInflater.from(getActivity()).inflate(R.layout.content_item,null);
								//image
								NetworkImageView image=(NetworkImageView)content_reply.findViewById(R.id.content_reply_image);
								image.setDefaultImageResId(R.drawable.default_image);
								image.setErrorImageResId(R.drawable.default_image);
								image.setImageUrl("http:"+listObject.get(i).getMember().getAvatar_normal(),imageLoader);
								final int a=i;
								image.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										Intent intent=new Intent(getActivity(),UserContent.class);
										intent.putExtra("username",listObject.get(a).getMember().getUsername().toString());
										getActivity().startActivity(intent);
									}
								});
								//name
								((TextView)content_reply.findViewById(R.id.content_reply_name)).setText(listObject.get(i).getMember().getUsername());
								((TextView)content_reply.findViewById(R.id.content_reply_time)).setText("一天前");
								
								TextView context_temp_reply=(TextView)content_reply.findViewById(R.id.content_reply_content);
								
								context_temp_reply.setText(Html.fromHtml(listObject.get(i).getContent_rendered()
										,new VolleyImageGetter(getActivity(),context_temp_reply)
										,new ImageHandler(getActivity())));
								
								context_temp_reply.setMovementMethod(LinkMovementMethod.getInstance());
								replyList.addView(content_reply);
							}
							content_progressBar.setVisibility(View.GONE);
							content_scroll.setVisibility(View.VISIBLE);
						}	
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {
							Log.e("getTopError",arg0.getMessage());
						}
					}));
					return null;
		}
	}
	
	@Override
	public void onRefreshStarted(View view) {

		new AsyncTask<Void, Void, Void>() {
	 
		@Override
		protected Void doInBackground(Void... params) {
			try {
				new getReply().execute();
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mPullToRefreshLayout.setRefreshComplete();
		}
		}.execute();
	}
	
	public void postComment(final int topicId){
		NetUtilImpl.getCode(getActivity(),12412,new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,JSONObject response) {
				try{
					if(response.getString("result").equals("success")){
						
						NetUtilImpl.postComment(getActivity(),response.getString("code"),topicId
								,commentContent.getText().toString(),new JsonHttpResponseHandler(){
							@Override
							public void onSuccess(int statusCode,Header[] headers,JSONObject response) {
								try{
									if(response.getString("result").equals("success")){
										Toast.makeText(getActivity(),getActivity().
												getString(R.string.repleysuccess),Toast.LENGTH_SHORT).show();
									}else if(response.getString("result").equals("fail")){
										Toast.makeText(getActivity(),getActivity().
												getString(R.string.repleyfail),Toast.LENGTH_SHORT).show();
									}
									progressDialog.setMessage(getActivity().getString(R.string.repleysuccess));
									commentContent.setText("");
									progressDialog.dismiss();
								}catch(Exception e){
									e.printStackTrace();
									progressDialog.setMessage(getActivity().getString(R.string.repleyfail));
									progressDialog.dismiss();
								}
							}
						});
					}
				}catch(Exception e){
					e.printStackTrace();
					progressDialog.setMessage(getActivity().getString(R.string.repleyfail));
					progressDialog.dismiss();
				}
			}
		});
	}
	
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	
	
	
}
