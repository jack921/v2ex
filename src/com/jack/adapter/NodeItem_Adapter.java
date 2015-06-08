package com.jack.adapter;

import java.util.List;
import com.jack.dao.DaoBaseUtil;
import com.jack.dataobject.AllNodeObject;
import com.jack.v2ex.NodeActivity;
import com.jack.v2ex.R;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NodeItem_Adapter extends ArrayAdapter<AllNodeObject>{

	private int resourceId;
	
	public NodeItem_Adapter(Context context, int resource, List<AllNodeObject> objects) {
		super(context, resource, objects);
		resourceId=resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final AllNodeObject nodeObject=getItem(position);
		View view;
		final ViewHolder viewHolder;
		if(convertView==null){
			view=LayoutInflater.from(getContext()).inflate(resourceId,null);
			viewHolder=new ViewHolder();
			viewHolder.nodeTitle=(TextView)view.findViewById(R.id.node_item_title);
			viewHolder.nodeContent=(TextView)view.findViewById(R.id.node_item_content);
			viewHolder.nodeAdd=(ImageView)view.findViewById(R.id.node_item_add);
			viewHolder.nodeRemove=(ImageView)view.findViewById(R.id.node_item_remove);
			view.setTag(viewHolder);
		}else{
			view=convertView;
			viewHolder=(ViewHolder)view.getTag();
		}
		
		if(nodeObject.getHeader()==null){
			viewHolder.nodeContent.setVisibility(View.GONE);
			viewHolder.nodeTitle.setVisibility(View.VISIBLE);
			viewHolder.nodeTitle.setText(nodeObject.getTitle());
		}else{
			viewHolder.nodeContent.setVisibility(View.VISIBLE);
			viewHolder.nodeTitle.setVisibility(View.VISIBLE);
			viewHolder.nodeTitle.setText(nodeObject.getTitle());
			viewHolder.nodeContent.setText(Html.fromHtml(nodeObject.getHeader()));
		}
		
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			    Intent intent=new Intent(getContext(),NodeActivity.class);
				intent.putExtra("node_id",nodeObject.getId()+"");
			    getContext().startActivity(intent);
			}
		});
		
		viewHolder.nodeAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DaoBaseUtil.AddNode(getContext(),nodeObject);
				nodeObject.setSaveState(1);
				viewHolder.nodeRemove.setVisibility(View.VISIBLE);
				viewHolder.nodeAdd.setVisibility(View.GONE);
			}
		});
		
		viewHolder.nodeRemove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nodeObject.setSaveState(0);
				DaoBaseUtil.RemoveNode(getContext(),nodeObject);
				viewHolder.nodeRemove.setVisibility(View.GONE);
				viewHolder.nodeAdd.setVisibility(View.VISIBLE);
			}
		});
		
		if(nodeObject.getSaveState()==0){ 
			viewHolder.nodeRemove.setVisibility(View.GONE);
			viewHolder.nodeAdd.setVisibility(View.VISIBLE);
		}else{
			viewHolder.nodeRemove.setVisibility(View.VISIBLE);
			viewHolder.nodeAdd.setVisibility(View.GONE);
		}
		
		return view;
	}
	
    class ViewHolder{
    	TextView nodeTitle;
    	TextView nodeContent;
    	TextView nodeTopics;
    	ImageView nodeAdd;
    	ImageView nodeRemove;
    }

}
