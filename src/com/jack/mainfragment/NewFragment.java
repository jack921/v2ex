package com.jack.mainfragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class NewFragment extends Fragment implements OnItemClickListener,OnRefreshListener{

	public static String Lates="https://www.v2ex.com/api/topics/latest.json";
	private List<DataObject> listObject=null;
	private PullToRefreshLayout mPullToRefreshLayout;
	private UpdateNew updateNew=new UpdateNew();
	public static final int update_text=1;
	private static ListView newListView;
	private ProgressBar mProgressBar;
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
			
		view=inflater.inflate(R.layout.activity_main_new,container,false);
		newListView=(ListView)view.findViewById(R.id.newfragment_listview);
		mPullToRefreshLayout=(PullToRefreshLayout)view.findViewById(R.id.ptr_layout);
		mProgressBar=(ProgressBar)view.findViewById(R.id.new_progressbar);
		newListView.setOnItemClickListener(this);
		
	    ActionBarPullToRefresh.from(getActivity())
	    .allChildrenArePullable()
	    .listener(this)
	    .setup(mPullToRefreshLayout);
		
	    mPullToRefreshLayout.setRefreshing(true);
	    updateNew.start(); 
		return view;
	}
	
	class UpdateNew extends Thread{
		@Override
		public void run() {
			MainActivity.mQueue.add(new StringRequest(Lates,
				    new Response.Listener<String>() {
						@Override
						public void onResponse(String arg0) {
							try{
								listObject=ParseJson.parseNewObjects(arg0);
								DaoBaseUtil.SaveNewTopic(getActivity(), listObject);
								Message message=new Message();
								message.what=update_text;
								handler.sendMessage(message);
								updateNew.interrupt();	
							}catch(Exception e){
								Toast.makeText(getActivity(),"无法连接服务器",Toast.LENGTH_SHORT).show();
								mPullToRefreshLayout.setRefreshing(false);
								updateNew.interrupt();
							}
						}	
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {
							Toast.makeText(getActivity(),"对不起,网络有问题",Toast.LENGTH_SHORT).show();
							listObject=DaoBaseUtil.GetNewTopic(getActivity());
							mProgressBar.setVisibility(View.GONE);
							mPullToRefreshLayout.setRefreshing(false);
							Message message=new Message();
							message.what=update_text;
							handler.sendMessage(message);
							updateNew.interrupt();
						}
					}));
		}
		
	}

	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case update_text:
				newListView.setAdapter(new NewItem_adapter(getActivity()
						,R.layout.newfragment_liem,listObject));
				mPullToRefreshLayout.setRefreshing(false);
				mProgressBar.setVisibility(View.GONE);
			break;
			default:
			break;	
			}
		};
	};
	
   @Override
   public void onRefreshStarted(View view) {
	new AsyncTask<Void, Void, Void>() { 
		@Override
		protected Void doInBackground(Void... params) {
			try {
				new Thread(new UpdateNew()).start();
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Log.e("Exception",e.getMessage());
			}
			return null;
	   }
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);	 
			mPullToRefreshLayout.setRefreshComplete();
		}}.execute();
	}
	
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
	
}
