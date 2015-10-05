package com.jack.mainfragment;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.jack.adapter.CollectNode_Adapter;
import com.jack.dataobject.AllNodeObject;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.AdapterView;
import com.jack.v2ex.NodeActivity;
import com.jack.dao.DaoBaseUtil;
import android.widget.ListView;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.ViewGroup;
import android.os.AsyncTask;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import com.jack.v2ex.R;

public class CollectNodeFragment extends Fragment implements OnItemClickListener
,OnItemLongClickListener,OnRefreshListener{

	private PullToRefreshLayout mPullToRefreshLayout;
	private CollectNode_Adapter collectNode_Adapter;
	private ProgressBar progressBar;
	private ListView collectList;
	private TextView collectTip;
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
	
		view=inflater.inflate(R.layout.activity_main_collectnode,container,false);
		collectList=(ListView)view.findViewById(R.id.collectlist);
		progressBar=(ProgressBar)view.findViewById(R.id.collect_progressbar);
		mPullToRefreshLayout=(PullToRefreshLayout)view.findViewById(R.id.collect_layout);
		collectTip=(TextView)view.findViewById(R.id.collect_tip);
		collectList.setOnItemClickListener(this);
		collectList.setOnItemLongClickListener(this);
		collectList.setVisibility(View.GONE);
		
		ActionBarPullToRefresh.from(getActivity())
		    .allChildrenArePullable()
		    .listener(this)
		    .setup(mPullToRefreshLayout);
		
		collectTip.setVisibility(View.GONE);
		new getCollectNode().execute();
		mPullToRefreshLayout.setRefreshing(true);
		return view;
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
		AllNodeObject nodeObject=(AllNodeObject)parent.getItemAtPosition(position);
		createDialog(nodeObject);
		if(AllNodeFragment.nodeItem!=null&&AllNodeFragment.listAllNodeObjects!=null){
			nodeObject.setSaveState(0);
			AllNodeFragment.listAllNodeObjects.set(nodeObject.getId()-1,nodeObject);
			AllNodeFragment.nodeItem.notifyDataSetChanged();
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		 Intent intent=new Intent(getActivity(),NodeActivity.class);
		 intent.putExtra("node_id",((AllNodeObject)parent.getItemAtPosition(position)).getId()+"");
		 getActivity().startActivity(intent);
	}

	class getCollectNode extends AsyncTask<Void,Void,List<AllNodeObject>>{
		
		@Override
		protected List<AllNodeObject> doInBackground(Void... params) {
			return DaoBaseUtil.GetCollectNode(getActivity());
		}
		
		@Override
		protected void onPostExecute(List<AllNodeObject> result) {
			if(result==null){
				collectTip.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				collectList.setVisibility(View.VISIBLE);
				mPullToRefreshLayout.setRefreshing(false);
			}else{
				collectNode_Adapter=new CollectNode_Adapter(getActivity()
						,R.layout.collect_item,result);
				collectList.setAdapter(collectNode_Adapter);
				progressBar.setVisibility(View.GONE);
				collectTip.setVisibility(View.GONE);
				collectList.setVisibility(View.VISIBLE);
				mPullToRefreshLayout.setRefreshing(false);
			}
		}
	}

	 @Override
	   public void onRefreshStarted(View view) {
		new AsyncTask<Void, Void, Void>() { 
			@Override
			protected Void doInBackground(Void... params) {
				try {
					new getCollectNode().execute();
				} catch (Exception e) {
					Log.e("Exception",e.getMessage());
				}
				return null;
		   }
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);	 
			}}.execute();
	 }
	
	 public void createDialog(final AllNodeObject nodeObject){
		 AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		 builder.setTitle(R.string.tips)
		 .setMessage(R.string.removenode)
	     .setPositiveButton(R.string.posititve,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				boolean result=DaoBaseUtil.RemoveNode(getActivity(),nodeObject);
				if(!result){
					Toast.makeText(getActivity(),getString(R.string.removeerror),Toast.LENGTH_SHORT).show();
				}else{
					collectNode_Adapter.remove(nodeObject);
					collectNode_Adapter.notifyDataSetChanged();
				}
			}
		}).setNegativeButton(R.string.negative,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		}).show();
	 }	 
	 
	 
}
