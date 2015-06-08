package com.jack.mainfragment;

import java.util.List;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jack.adapter.NodeItem_Adapter;
import com.jack.dao.DaoBaseUtil;
import com.jack.dataobject.AllNodeObject;
import com.jack.netutil.ParseJson;
import com.jack.v2ex.R;
import com.origamilabs.library.views.StaggeredGridView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

public class AllNodeFragment extends Fragment{

	public static String AllNode="https://www.v2ex.com/api/nodes/all.json";
	public static List<AllNodeObject> listAllNodeObjects;
	public static NodeItem_Adapter nodeItem=null;
	private StaggeredGridView mStaggeredGrid;
	private ProgressBar mProgressBar;
	private RequestQueue queue;
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
		
		view=inflater.inflate(R.layout.activity_main_allnode,container,false);
		mStaggeredGrid=(StaggeredGridView)view.findViewById(R.id.mStaggeredGrid);
		mProgressBar=(ProgressBar)view.findViewById(R.id.allnode_progressbar);
		queue=Volley.newRequestQueue(getActivity());		
		setHasOptionsMenu(true);
		mStaggeredGrid.setVisibility(View.GONE);
		
		new GetLocalAllNode().execute();
	
		return view;
	}
	
	class GetLocalAllNode extends AsyncTask<Void,Void,List<AllNodeObject>>{
		@Override
		protected List<AllNodeObject> doInBackground(Void... params) {
			listAllNodeObjects=DaoBaseUtil.GetAllNode(getActivity());
			return listAllNodeObjects;
		}

		@Override
		protected void onPostExecute(List<AllNodeObject> result) {
			if(result==null){
				new Thread(new getAllNode()).start();
			}else{
				nodeItem=new NodeItem_Adapter(getActivity(),R.layout.node_item,result);
				mStaggeredGrid.setAdapter(nodeItem);
				mProgressBar.setVisibility(View.GONE);
				mStaggeredGrid.setVisibility(View.VISIBLE);
			}
		}	
	}
	
	class getAllNode extends Thread{
		@Override
		public void run() {
			queue.add(new StringRequest(AllNode
					 ,new Response.Listener<String>() {
						@Override
					   public void onResponse(String json) {
							//保存数据
							DaoBaseUtil.SaveAllNode(getActivity(),ParseJson.parseAllNodeObjects(json));
							listAllNodeObjects=DaoBaseUtil.GetAllNode(getActivity());
							//发送消息
							Message message=new Message();
							message.what=NewFragment.update_text;
							handler.sendMessage(message);
						}
			           },new Response.ErrorListener() {
						@Override
					  public void onErrorResponse(VolleyError arg0) {
							Log.e("getAllNodes",arg0.getMessage());
					  }
			    }));
		}
	}
	
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case NewFragment.update_text:
				//加载视图
				nodeItem=new NodeItem_Adapter(getActivity(),
						R.layout.node_item,listAllNodeObjects);
				mStaggeredGrid.setAdapter(nodeItem);
				mProgressBar.setVisibility(View.GONE);
				mStaggeredGrid.setVisibility(View.VISIBLE);
			break;
			default:
			break;
			}
		};
		
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.reload_node:
			mStaggeredGrid.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
			new Thread(new getAllNode()).start();
		break;	
		default:
			Toast.makeText(getActivity(),"更新失败哦>-<",Toast.LENGTH_SHORT).show();
		}
		return true;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.search, menu);
		MenuItem searchMenuItem = menu.findItem(R.id.menu_all_node_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint("Search for nodes");
        
        //收缩框展开和关闭
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                getActivity().getActionBar().setDisplayShowHomeEnabled(true);
                return true;
            }
        });
        
       //设置查询时间监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				if(newText.equals("")){
					if(listAllNodeObjects==null){
						
					}else{
						nodeItem=new NodeItem_Adapter(getActivity(),
								R.layout.node_item,listAllNodeObjects);
						mStaggeredGrid.setAdapter(nodeItem);
					}
				}else{
					new searchNode().execute(newText);
				}
				return true;
			}
		});
	}
	
	class searchNode extends AsyncTask<String,Void,List<AllNodeObject>>{
		@Override
		protected List<AllNodeObject> doInBackground(String... params) {
			return DaoBaseUtil.SearchQueryNode(getActivity(),params[0]);
		}
		
		@Override
		protected void onPostExecute(List<AllNodeObject> result) {
			if(result==null){
				mStaggeredGrid.setAdapter(new NodeItem_Adapter(getActivity(),
						R.layout.node_item,listAllNodeObjects));
			}else{
				mStaggeredGrid.setAdapter(new NodeItem_Adapter(getActivity(),
						R.layout.node_item,result));
			}
		}
	}
	
}
