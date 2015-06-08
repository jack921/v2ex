package com.jack.adapter;

import java.util.List;
import com.jack.dataobject.AllNodeObject;
import com.jack.v2ex.R;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CollectNode_Adapter extends ArrayAdapter<AllNodeObject>{

	private int resource;

	public CollectNode_Adapter(Context context, int resource,List<AllNodeObject> objects) {
		super(context, resource, objects);
		this.resource=resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AllNodeObject object=getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView==null){
			viewHolder=new ViewHolder();
			view=LayoutInflater.from(getContext()).inflate(resource,null);
			viewHolder.title=(TextView)view.findViewById(R.id.collect_title);
			viewHolder.content=(TextView)view.findViewById(R.id.collect_content);
			view.setTag(viewHolder);
		}else{
			view=convertView;
			viewHolder=(ViewHolder)view.getTag();
		}
		
		viewHolder.title.setText(object.getTitle());
		if(object.getHeader()!=null){
		viewHolder.content.setText(Html.fromHtml(object.getHeader()));
		}else{
		viewHolder.content.setVisibility(View.GONE);	
		}
		return view;
	}
	
	class ViewHolder{
		TextView title;
		TextView content;	
	}
	
}
