package com.jack.v2ex;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jack.adapter.NewItem_adapter;
import com.jack.dataobject.DataObject;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jack.mainfragment.NewFragment;
import com.jack.netutil.ParseJson;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class NodeActivity extends SwipeBackActivity implements OnRefreshListener, OnItemClickListener{
		
	private String NODOTOPIC="https://www.v2ex.com/api/topics/show.json?node_id=";
	private List<DataObject> listNode=new ArrayList<>();
	private PullToRefreshLayout mPullToRefreshLayout;
	private ListView mNodeTopicList;
	private RequestQueue mqueue; 
	private String node_id;
	private UpdateNode updateNode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.node_fragment);
		updateNode=new UpdateNode();
		
		initView();
		
		ActionBarPullToRefresh.from(this)
        .allChildrenArePullable()
        .listener(this)
        .setup(mPullToRefreshLayout);
		
		mNodeTopicList.setOnItemClickListener(this);
		mPullToRefreshLayout.setRefreshing(true);
		
		updateNode.start();
	}

	public void initView(){
		mPullToRefreshLayout=(PullToRefreshLayout)findViewById(R.id.node_fragment_layout);
		mqueue=Volley.newRequestQueue(this);
		node_id=getIntent().getStringExtra("node_id");
		mNodeTopicList=(ListView)findViewById(R.id.node_fragment_list);
	}
	
    class UpdateNode extends Thread{
		@Override
		public void run() {
			MainActivity.mQueue.add(new StringRequest(NODOTOPIC+node_id,
				    new Response.Listener<String>() {
						@Override
						public void onResponse(String arg0) {
							mqueue.add(new StringRequest(NODOTOPIC+node_id
				    				,new Response.Listener<String>() {
										@Override
										public void onResponse(String arg0){		
											listNode=ParseJson.parseNewObjects(arg0);
											Message message=new Message();
											message.what=NewFragment.update_text;
											handler.sendMessage(message);
											updateNode.interrupt();
										}
									}
				    				,new Response.ErrorListener() {
										@Override
										public void onErrorResponse(VolleyError arg0) {
											Log.e("NodeActivity_getNodeTopic",arg0.getMessage());
										}
									}) );
						}	
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {
							Toast.makeText(getBaseContext(),"无法连接服务器",Toast.LENGTH_SHORT).show();
						}
					}));
		}
	}

	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case NewFragment.update_text:
				mNodeTopicList.setAdapter(new NewItem_adapter(getBaseContext(),
						R.layout.newfragment_liem,listNode));
				mPullToRefreshLayout.setRefreshing(false);
			break;
			default:
			break;	
			}
		};
	};
    
    @Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
    	DataObject dataObject=(DataObject)parent.getItemAtPosition(position);
		Bundle bundle=new Bundle();	
		bundle.putString("title",dataObject.getTitle());
		bundle.putString("content",dataObject.getContent_rendered());
		bundle.putInt("id",dataObject.getId());
		bundle.putInt("replies",dataObject.getReplies());
		bundle.putString("name",dataObject.getMember().getUsername());
		bundle.putString("node",dataObject.getNode().getTitle());
	    NetworkImageView imageView=((NetworkImageView)view.findViewById(R.id.newitem_image));
		imageView.setDrawingCacheEnabled(true);
	    bundle.putParcelable("image",imageView.getDrawingCache());
		Intent intent=new Intent(this,ContentActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}
    
	@Override
	public void onRefreshStarted(View view) {
		new AsyncTask<Void, Void, Void>(){		 
			@Override
			protected Void doInBackground(Void... params) {
				try {
					updateNode.start();
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
	
}
