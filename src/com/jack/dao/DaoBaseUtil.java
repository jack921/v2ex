package com.jack.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jack.dataobject.AllNodeObject;
import com.jack.dataobject.DataObject;
import com.jack.dataobject.Member;
import com.jack.dataobject.Node;

public class DaoBaseUtil {

	private static AllNodeDatabaseHelper mAllNodeDatabaseHelper;
	
	//保存所有节点
	public static void SaveAllNode(Context context,List<AllNodeObject> listNodeObjects){
		mAllNodeDatabaseHelper=new AllNodeDatabaseHelper(context,"v2ex.db",null,1);
		SQLiteDatabase db=mAllNodeDatabaseHelper.getWritableDatabase();
		ContentValues values=new ContentValues();;
		db.delete("allnode",null,null);
		for(AllNodeObject ano:listNodeObjects){
			values.put("nodeid",ano.getId());
			values.put("nodename",ano.getName());
			values.put("nodetitle",ano.getTitle());
			values.put("nodecontent",ano.getHeader());
			values.put("nodesavestate",ano.getSaveState());
			db.insert("allnode",null,values);
			values.clear();
		}
		
		Cursor cursor=db.query("collectnode",null,null,null,null,null,null);
		if(cursor.moveToFirst()){
			int id=cursor.getColumnIndex("collect_id");
			values.clear();
			do{
				values.put("nodesavestate",1);
				db.update("allnode", values,"nodeid=?",new String[]{cursor.getInt(id)+""});
			}while(cursor.moveToNext());
		}
		
	}
	
	//获取所有节点
	public static List<AllNodeObject> GetAllNode(Context context){
		mAllNodeDatabaseHelper=new AllNodeDatabaseHelper(context,"v2ex.db", null,1);
		SQLiteDatabase db=mAllNodeDatabaseHelper.getWritableDatabase();
		Cursor cursor=db.query("allnode",null,null,null,null,null,null);
		List<AllNodeObject> listAllNodeObjects=new ArrayList<>();
		if(cursor.moveToFirst()){
			AllNodeObject allNodeObject;
			int id=cursor.getColumnIndex("nodeid");
			int title=cursor.getColumnIndex("nodetitle");
			int content=cursor.getColumnIndex("nodecontent");
			int savestate=cursor.getColumnIndex("nodesavestate");
			do{
				allNodeObject=new AllNodeObject(cursor.getInt(id),cursor.getString(title)
						,cursor.getString(content),cursor.getInt(savestate));
				listAllNodeObjects.add(allNodeObject);
			}while(cursor.moveToNext());
		}else{
			Log.e("DaoBaseUtil","DaoBaseUtil_ReadError");
			return null;
		}
		return listAllNodeObjects;
	}
	
	//模糊查找node
	public static List<AllNodeObject> SearchQueryNode(Context context,String title){
		mAllNodeDatabaseHelper=new AllNodeDatabaseHelper(context,"v2ex.db",null,1);
		SQLiteDatabase db=mAllNodeDatabaseHelper.getWritableDatabase();
	    Cursor cursor=db.query("allnode",null,"nodetitle like ?",new String[]{"%"+title+"%"},null,null,null);
		List<AllNodeObject> listAllNodeObjects=new ArrayList<>();
	    if(cursor.moveToFirst()){
	    	AllNodeObject temp=null;
			int id=cursor.getColumnIndex("nodeid");
			int temptitle=cursor.getColumnIndex("nodetitle");
			int content=cursor.getColumnIndex("nodecontent");
	    	int savestate=cursor.getColumnIndex("nodesavestate");
			do{
				temp=new AllNodeObject(cursor.getInt(id),cursor.getString(temptitle)
						,cursor.getString(content),cursor.getInt(savestate));
				listAllNodeObjects.add(temp);
			}while(cursor.moveToNext());
		}else{
			return null;
		}
		return listAllNodeObjects;
	}
	
	//收藏节点
	public static boolean AddNode(Context context,AllNodeObject allNodeObject){
		try{
			mAllNodeDatabaseHelper=new AllNodeDatabaseHelper(context,"v2ex.db",null,1);
			SQLiteDatabase db=mAllNodeDatabaseHelper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("collect_id",allNodeObject.getId());
			values.put("collect_title",allNodeObject.getTitle());
			values.put("collect_content",allNodeObject.getHeader());
			values.put("collect_savestate",1);
			db.insert("collectnode",null,values);
			values.clear();
			values.put("nodesavestate",1);
			db.update("allnode",values,"nodeid=?",new String[]{allNodeObject.getId()+""});
			return true;
		}catch(Exception e){
			Log.e("addNode",e.getMessage());
			return false;
		}
	}
	
    //删除节点
	public static boolean RemoveNode(Context context,AllNodeObject allNodeObject){
		try{
			mAllNodeDatabaseHelper=new AllNodeDatabaseHelper(context,"v2ex.db",null,1);
			SQLiteDatabase db=mAllNodeDatabaseHelper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("nodesavestate",0);
			db.update("allnode",values,"nodeid=?",new String[]{allNodeObject.getId()+""});
			db.delete("collectnode","collect_id=?",new String[]{allNodeObject.getId()+""});
			return true;
		}catch(Exception e){
			Log.e("removeNode",e.getMessage());
			return false;
		}
	}
	
	//获取所有的收藏节点
	public static List<AllNodeObject> GetCollectNode(Context context){
		try{
			mAllNodeDatabaseHelper=new AllNodeDatabaseHelper(context,"v2ex.db",null,1);
			SQLiteDatabase db=mAllNodeDatabaseHelper.getWritableDatabase();
			Cursor cursor=db.query("collectnode",null,null,null,null,null,null);
			if(cursor.moveToFirst()){
				List<AllNodeObject> AllCollectNode=new ArrayList<>();
				int id=cursor.getColumnIndex("collect_id");
				int title=cursor.getColumnIndex("collect_title");
				int content=cursor.getColumnIndex("collect_content");				
				AllNodeObject nodeObject;
				do{
					nodeObject=new AllNodeObject(cursor.getInt(id),cursor.getString(title)
							,cursor.getString(content),1);
					AllCollectNode.add(nodeObject);
				}while(cursor.moveToNext());
				return AllCollectNode;
			}else{
				return null;
			}
		}catch(Exception e){
			Log.e("GetCollectNode",e.getMessage());
			return null;
		}
	}
	
	//获取所有节点的title
		public static List<String> GetCollectNodeTitle(Context context){
			try{
				mAllNodeDatabaseHelper=new AllNodeDatabaseHelper(context,"v2ex.db",null,1);
				SQLiteDatabase db=mAllNodeDatabaseHelper.getWritableDatabase();
				Cursor cursor=db.query("allnode",null,null,null,null,null,null);
				List<String> listdate=new ArrayList<>();
				if(cursor.moveToFirst()){
					int title=cursor.getColumnIndex("nodename");
					do{
						listdate.add(cursor.getString(title));
					}while(cursor.moveToNext());
					return listdate;
				}else{
					return null;
				}
			}catch(Exception e){
				Log.e("GetCollectNodeIdAndTitle",e.getMessage());
				return null;
			}
		}
	
	//删除收藏的节点
	public static boolean RemoveCollectNode(Context context,int id){
		try{
			mAllNodeDatabaseHelper=new AllNodeDatabaseHelper(context,"v2ex.db",null,1);
			SQLiteDatabase db=mAllNodeDatabaseHelper.getWritableDatabase();
			db.delete("collectnode","collect_id=?",new String[]{id+""});
			return true;
		}catch(Exception e){
			Log.e("RemoveCollectNode",e.getMessage());
			return false;	
		}
	}
	
	//保存最新topic
	public static boolean SaveNewTopic(Context context,List<DataObject> listObject){
		try{
			if(listObject!=null){
				mAllNodeDatabaseHelper=new AllNodeDatabaseHelper(context,"v2ex.db",null,1);
				SQLiteDatabase db=mAllNodeDatabaseHelper.getWritableDatabase();
				Date d=new Date();
				SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time=timeFormat.format(d);
				Log.e("time",time);
				ContentValues values=new ContentValues();
				db.delete("new",null,null);
				for(DataObject object:listObject){
					values.put("new_id",object.getId());
					values.put("new_title",object.getTitle());
					values.put("new_content_rendered",object.getContent_rendered());
					values.put("new_replies",object.getReplies());
					values.put("new_time",time);
					values.put("new_member_username",object.getMember().getUsername());
					values.put("new_member_avatar_normal",object.getMember().getAvatar_normal());
					values.put("new_node_title",object.getNode().getTitle());					
					db.insert("new",null,values);
					values.clear();
				}
			}
			return true;
		}catch(Exception e){
			Log.e("SaveNewTopic",e.getMessage());
			return false;
		}
	}
	
	//得到最新topic
	public static List<DataObject> GetNewTopic(Context context){
		try{
			mAllNodeDatabaseHelper=new AllNodeDatabaseHelper(context,"v2ex.db",null,1);
			SQLiteDatabase db=mAllNodeDatabaseHelper.getWritableDatabase();
			Cursor cursor=db.query("new",null,null,null,null,null,null);
			if(cursor.moveToFirst()){
				List<DataObject> listObjects=new ArrayList<>();
				DataObject object=null;
				int id=cursor.getColumnIndex("new_id");
				int title=cursor.getColumnIndex("new_title");
				int content=cursor.getColumnIndex("new_content_rendered");
				int replies=cursor.getColumnIndex("new_replies");
				int time=cursor.getColumnIndex("new_time");
				int username=cursor.getColumnIndex("new_member_username");
				int normal=cursor.getColumnIndex("new_member_avatar_normal");
				int nodetitle=cursor.getColumnIndex("new_node_title");
				do{
					object=new DataObject(cursor.getInt(id), cursor.getString(title),
							cursor.getString(content),
							cursor.getInt(replies),cursor.getString(time),
							new Member(cursor.getString(username),cursor.getString(normal))
					,new Node(cursor.getString(nodetitle)));
					listObjects.add(object);
				}while(cursor.moveToNext());
				return listObjects;
			}
			return null;
		}catch(Exception e){
			Log.e("GetNewTopic",e.getMessage());
			return null;
		}		
	}
	
	
	//保存最热topic
		public static boolean SaveHotTopic(Context context,List<DataObject> listObject){
			try{
				if(listObject!=null){
					mAllNodeDatabaseHelper=new AllNodeDatabaseHelper(context,"v2ex.db",null,1);
					SQLiteDatabase db=mAllNodeDatabaseHelper.getWritableDatabase();
					Date d=new Date();
					SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time=timeFormat.format(d);
					ContentValues values=new ContentValues();
					db.delete("topic",null,null);
					for(DataObject object:listObject){
						values.put("topic_id",object.getId());
						values.put("topic_title",object.getTitle());
						values.put("topic_content_rendered",object.getContent_rendered());
						values.put("topic_replies",object.getReplies());
						values.put("topic_time",time);
						values.put("topic_member_username",object.getMember().getUsername());
						values.put("topic_member_avatar_normal",object.getMember().getAvatar_normal());
						values.put("topic_node_title",object.getNode().getTitle());					
						db.insert("topic",null,values);
						values.clear();
					}
				}
				return true;
			}catch(Exception e){
				Log.e("SaveNewTopic",e.getMessage());
				return false;
			}
		}
		
		//得到最热topic
		public static List<DataObject> GetHotTopic(Context context){
			try{
				mAllNodeDatabaseHelper=new AllNodeDatabaseHelper(context,"v2ex.db",null,1);
				SQLiteDatabase db=mAllNodeDatabaseHelper.getWritableDatabase();
				Cursor cursor=db.query("topic",null,null,null,null,null,null);
				if(cursor.moveToFirst()){
					List<DataObject> listObjects=new ArrayList<>();
					DataObject object=null;
					int id=cursor.getColumnIndex("topic_id");
					int title=cursor.getColumnIndex("topic_title");
					int content=cursor.getColumnIndex("topic_content_rendered");
					int replies=cursor.getColumnIndex("topic_replies");
					int time=cursor.getColumnIndex("topic_time");
					int username=cursor.getColumnIndex("topic_member_username");
					int normal=cursor.getColumnIndex("topic_member_avatar_normal");
					int nodetitle=cursor.getColumnIndex("topic_node_title");
					do{
						object=new DataObject(cursor.getInt(id), cursor.getString(title),
								cursor.getString(content),
								cursor.getInt(replies),cursor.getString(time),
								new Member(cursor.getString(username),cursor.getString(normal))
						,new Node(cursor.getString(nodetitle)));
						listObjects.add(object);
					}while(cursor.moveToNext());
					return listObjects;
				}
				return null;
			}catch(Exception e){
				Log.e("GetHotTopic",e.getMessage());
				return null;
			}		
		}
	
}
