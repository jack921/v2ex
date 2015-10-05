package com.jack.mainfragment;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.jack.adapter.NewItem_adapter;
import com.jack.dao.DaoBaseUtil;
import com.jack.dataobject.DataObject;
import com.jack.netutil.ParseJson;
import com.jack.v2ex.ContentActivity;
import com.jack.v2ex.MainActivity;
import com.jack.v2ex.R;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TopicFragment extends Fragment implements OnItemClickListener,OnRefreshListener{

	public static String Hot="https://www.v2ex.com/api/topics/hot.json";
	private List<DataObject> listTopic=new ArrayList<>();
	private PullToRefreshLayout mPullToRefreshLayout;
	private ProgressBar mProgressBar;
	private UpdateTopic updateTopic;
	private ListView topicList;
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
 
		view=inflater.inflate(R.layout.activity_main_topic,null);
		topicList=(ListView)view.findViewById(R.id.topicfragment_topic);
		mPullToRefreshLayout=(PullToRefreshLayout)view.findViewById(R.id.ptr_layouttopic);
		mProgressBar=(ProgressBar)view.findViewById(R.id.topic_progressbar);
		updateTopic=new UpdateTopic();
		
		ActionBarPullToRefresh.from(getActivity())
	    .allChildrenArePullable()
	    .listener(this)
	    .setup(mPullToRefreshLayout);
		
	    updateTopic.start();
		
		topicList.setOnItemClickListener(this);
		mPullToRefreshLayout.setRefreshing(true);
		return view;
	}

	class UpdateTopic extends Thread{
		@Override
		public void run() {
		MainActivity.mQueue.add(new StringRequest(Hot,
			new Response.Listener<String>() {
			    @Override
				public void onResponse(String arg0) {
					try{
						listTopic=ParseJson.parseNewObjects(arg0);
						DaoBaseUtil.SaveHotTopic(getActivity(), listTopic);
						Message message=new Message();
						message.what=NewFragment.update_text;
						handler.sendMessage(message);
						updateTopic.interrupt();	
					}catch(Exception e){
						Toast.makeText(getActivity(),"无法连接服务器",Toast.LENGTH_SHORT).show();
						mPullToRefreshLayout.setRefreshing(false);
						updateTopic.interrupt();
					}
				}	
			},
			new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError arg0) {
					    Toast.makeText(getActivity(),"对不起,网络有问题",Toast.LENGTH_SHORT).show();
						listTopic=DaoBaseUtil.GetHotTopic(getActivity());
						mPullToRefreshLayout.setRefreshing(false);
						mProgressBar.setVisibility(View.GONE);
						Message message=new Message();
						message.what=NewFragment.update_text;
						handler.sendMessage(message);
				}
			}));
		}	
	}

	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case NewFragment.update_text:
				topicList.setAdapter(new NewItem_adapter(getActivity()
						,R.layout.newfragment_liem,listTopic));
				mPullToRefreshLayout.setRefreshing(false);
				mProgressBar.setVisibility(View.GONE);
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
		Intent intent=new Intent(getActivity(),ContentActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	@Override
	public void onRefreshStarted(View view) {
		
	new AsyncTask<Void, Void, Void>() { 
		@Override
		protected Void doInBackground(Void... params) {
			try {
				new Thread(new UpdateTopic()).start();
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
