package com.jack.netutil;

import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jack.dataobject.AllNodeObject;
import com.jack.dataobject.DataObject;
import com.jack.dataobject.MemberInfoObject;
import com.jack.dataobject.ReplyObject;

public class ParseJson {
	
	public static List<DataObject> parseNewObjects(String json){
		Gson mGson=new Gson();
		return mGson.fromJson(json,new TypeToken<List<DataObject>>(){}.getType());
	}

	public static List<AllNodeObject> parseAllNodeObjects(String json){
		Gson mGson=new Gson();
		return mGson.fromJson(json,new TypeToken<List<AllNodeObject>>(){}.getType());
	}

	public static List<ReplyObject> parseReplyObject(String json){
		Gson mGson=new Gson();
		return mGson.fromJson(json,new TypeToken<List<ReplyObject>>(){}.getType());
	}

	public static MemberInfoObject parseMemberInfo(String json){
		Gson mGson=new Gson();
		return mGson.fromJson(json,MemberInfoObject.class);
	}
	
}

