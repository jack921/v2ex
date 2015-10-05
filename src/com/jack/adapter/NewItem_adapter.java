package com.jack.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.jack.dataobject.DataObject;
import com.jack.netutil.BitmapCache;
import com.jack.netutil.VolleyImageGetter;
import com.jack.v2ex.MainActivity;
import com.jack.v2ex.R;
import com.jack.v2ex.UserContent;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NewItem_adapter extends ArrayAdapter<DataObject>{

	private static SimpleDateFormat dataFormat;
	private ImageLoader imageLoader;
	private static Date date;
	private int resource;
	private String time;
	
	public NewItem_adapter(Context context,int resource,List<DataObject> objects) {
		super(context, resource, objects);
		this.resource=resource;
		imageLoader=new ImageLoader(MainActivity.mQueue,new BitmapCache());
		dataFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		date=new Date();
		if(objects.get(0)!=null){
			if(objects.get(0).getTime()!=null){
				time=getTime(objects.get(0));
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final DataObject object=getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView==null){
			view=LayoutInflater.from(getContext()).inflate(resource,null);
			viewHolder=new ViewHolder();
			viewHolder.name=(TextView)view.findViewById(R.id.newitem_name);
			viewHolder.image=(NetworkImageView)view.findViewById(R.id.newitem_image);
			viewHolder.title=(TextView)view.findViewById(R.id.newitem_title);
			viewHolder.node=(TextView)view.findViewById(R.id.newitem_node);
			viewHolder.time=(TextView)view.findViewById(R.id.newitem_time);
			viewHolder.reply=(TextView)view.findViewById(R.id.newitem_repley);
			viewHolder.content=(TextView)view.findViewById(R.id.newitem_content);
			view.setTag(viewHolder);
		}else{
			view=convertView;
			viewHolder=(ViewHolder)view.getTag();
		}
		
		viewHolder.name.setText(object.getMember().getUsername());
		viewHolder.title.setText(object.getTitle());
		
		if(object.getTime()==null){
			viewHolder.time.setText("刚刚");
		}else{
			if(time.equals("0")){
				viewHolder.time.setText("一天之内");
			}else{
				viewHolder.time.setText(time+"天前");
			}
		}
		
		if(object.getContent_rendered().equals("")){
			viewHolder.content.setVisibility(View.GONE);
		}else{
			viewHolder.content.setText(Html.fromHtml(object.getContent_rendered()
					,new VolleyImageGetter(getContext(),viewHolder.content),null));
		}
		
		viewHolder.reply.setText(object.getReplies()+"回复"+"");
		viewHolder.node.setText(object.getNode().getTitle());
		
		viewHolder.image.setDefaultImageResId(R.drawable.default_image);
		viewHolder.image.setErrorImageResId(R.drawable.default_image);
		viewHolder.image.setImageUrl("http:"+object.getMember().getAvatar_normal(), imageLoader);
		
		viewHolder.image.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getContext(),UserContent.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("username",object.getMember().getUsername());
				getContext().startActivity(intent);
			}
		});
		return view;
	}
	
   class ViewHolder{
	   TextView title;
	   NetworkImageView image;
	   TextView reply;
	   TextView time;
	   TextView node;
	   TextView content;
	   TextView name;
   }
	
   public String getTime(DataObject object){
	   String newtime=dataFormat.format(date);
		String oldtime=object.getTime();
		long days = 0;
		try {
			Date d1=dataFormat.parse(newtime);
			Date d2=dataFormat.parse(oldtime);
			long diff = d1.getTime() - d2.getTime();
			days = diff / (1000 * 60 * 60 * 24);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return days+"";
   }
   
}
