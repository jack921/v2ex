package com.jack.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AllNodeDatabaseHelper extends SQLiteOpenHelper{

	private String CREAET_ALLNODE="create table allnode(" 
			+"nodeid integer primary key,"
			+"nodename text,"
			+"nodetitle text,"
			+"nodecontent text,"
			+"nodesavestate integer"
			+")";
	
	private String CREATE_COLLECTNODE="create table collectnode("
			+"collect_id integer primary key,"
			+"collect_title text,"
			+"collect_content text,"
			+"collect_savestate integer"
			+")";
	
	private String CREATE_NEW="create table new("
			+"new_id integer primary key,"
			+"new_title text,"
			+"new_content_rendered text,"
			+"new_replies integer,"
			+"new_member_username text,"
			+"new_member_avatar_normal text,"
			+"new_time text,"
			+"new_node_title text"
			+")";
	
	private String CREATE_TOPIC="create table topic(" 
			+"topic_id integer primary key,"
			+"topic_title text,"
			+"topic_content_rendered text,"
			+"topic_replies integer,"
			+"topic_member_username text,"
			+"topic_member_avatar_normal text,"
			+"topic_time text,"
			+"topic_node_title text"
			+")";
	
	
	public AllNodeDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREAET_ALLNODE);
		db.execSQL(CREATE_COLLECTNODE);
		db.execSQL(CREATE_NEW);
		db.execSQL(CREATE_TOPIC);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
}
