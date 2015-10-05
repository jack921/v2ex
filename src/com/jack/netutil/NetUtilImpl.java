package com.jack.netutil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

public class NetUtilImpl {

	private static AsyncHttpClient mClient=null;
	
	public static AsyncHttpClient getClient(Context context){
		if(mClient==null){
			mClient=new AsyncHttpClient();
			mClient.setEnableRedirects(false);
			mClient.setCookieStore(new PersistentCookieStore(context));
            mClient.setUserAgent("Mozilla/5.0 (Linux; U; Android 4.2.1; en-us; M040 Build/JOP40D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
            mClient.addHeader("Cache-Control", "max-age=0");
            mClient.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            mClient.addHeader("Accept-Charset", "utf-8, iso-8859-1, utf-16, *;q=0.7");
            mClient.addHeader("Accept-Language", "zh-CN, en-US");
            mClient.addHeader("X-Requested-With", "com.android.browser");
            SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());
		}
		return mClient;
	}

	public static void getCode(Context context,String nodeName,final JsonHttpResponseHandler handler){
		String url="https://www.v2ex.com/new/"+nodeName;
		AsyncHttpClient client=getClient(context);
		client.addHeader("Referer","https://www.v2ex.com");
		client.get(url,new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				String content=new String(response);
				JSONObject jsonObject=new JSONObject();
				 Pattern pattern = Pattern.compile("<input type=\"hidden\" value=\"([0-9]+)\" name=\"once\" />");
	                final Matcher matcher = pattern.matcher(content);
				try{
					if(matcher.find()){
						jsonObject.put("result","success");
						jsonObject.put("code",matcher.group(1));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				handler.onSuccess(statusCode, headers,jsonObject);
			}
			@Override
			public void onFailure(int statusCode, Header[] header, byte[] response, Throwable throwable) {
				handler.onFailure(statusCode,header,response,throwable);
			}
		});
	}
	
	public static void getCode(final Context context,int topic,final JsonHttpResponseHandler handler){
		AsyncHttpClient client=getClient(context);
		client.addHeader("Referer","https://www.v2ex.com");
		client.get("https://www.v2ex.com/t/"+topic,new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				String htmlcontent=new String(response);
				JSONObject jsonObject=new JSONObject();
				 Pattern pattern = Pattern.compile("<input type=\"hidden\" value=\"([0-9]+)\" name=\"once\" />");
	                final Matcher matcher = pattern.matcher(htmlcontent);
				try{
					if(matcher.find()){
						jsonObject.put("result","success");
						jsonObject.put("code",matcher.group(1));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				handler.onSuccess(statusCode, headers,jsonObject);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable throwable) {
				handler.onFailure(statusCode, headers, responseBody, throwable);
			}
		});
	}
	
	public static void GetCode(final Context context,final JsonHttpResponseHandler handler){
		AsyncHttpClient client=getClient(context);
		client.get("https://www.v2ex.com/signin",new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Pattern pattern = Pattern.compile("<input type=\"hidden\" value=\"([0-9]+)\" name=\"once\" />");
                final Matcher matcher = pattern.matcher(responseBody);
                JSONObject json=new JSONObject();
                try {
                    if(matcher.find()){
                    	json.put("result","success");
                        json.put("code",matcher.group(1));
                    }else{
                    	json.put("result","fail");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
               handler.onSuccess(statusCode, headers,json); 
			}		
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				handler.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}	

	public static void addTopic(Context context,String node,String code,String title,String content,final JsonHttpResponseHandler handler){
		String url="https://www.v2ex.com/new/"+node;
		AsyncHttpClient client=getClient(context);
		client.addHeader("Origin","https://www.v2ex.com");
        client.addHeader("Referer",url);
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        RequestParams params = new RequestParams();
        params.put("once",code);
        params.put("content", content);
        params.put("title", title);
		client.post(url,params,new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				JSONObject jsonObject=new JSONObject();
				try{
					jsonObject.put("result","fail");
				}catch(Exception e){
					e.printStackTrace();
				}
				handler.onSuccess(statusCode,headers,jsonObject);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] response, Throwable throwable) {
				JSONObject jsonObject=new JSONObject();
				try{ 
					if(statusCode==302){
						jsonObject.put("result","success");
					}else{
						jsonObject.put("result","fail");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				handler.onSuccess(statusCode, headers,jsonObject);
			}
		});
	}
	
	
	public static void login(String code,String username,String password,final Context context,final JsonHttpResponseHandler handler){
	
		AsyncHttpClient client=getClient(context);
		client.addHeader("Origin","https://www.v2ex.com");
		client.addHeader("Referer","https://www.v2ex.com/signin");
		client.addHeader("Content-Type","application/x-www-form-urlencoded");
		RequestParams params=new RequestParams();
		params.put("next","/");
		params.put("u",username);
		params.put("once",code);
		params.put("p",password);
	
		client.post("https://www.v2ex.com/signin", params,new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				Toast.makeText(context,"出错了",Toast.LENGTH_SHORT).show();
			}
		
			@Override
			public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
			try{
				JSONObject jsonObject=new JSONObject();
				if(statusCode==302){
					jsonObject.put("result","success");
					handler.onSuccess(statusCode, headers,jsonObject);
				}else{
					jsonObject.put("result","fail");
					Toast.makeText(context,"出错了",Toast.LENGTH_SHORT).show();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	public static void getinfo(Context context,final JsonHttpResponseHandler handler){
	getClient(context).get("https://www.v2ex.com/my/nodes",new TextHttpResponseHandler(){
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseBody){
			JSONObject result = new JSONObject();
            try {
                Pattern userPattern = Pattern.compile("<a href=\"/member/([^\"]+)\" class=\"top\">");
                Matcher userMatcher = userPattern.matcher(responseBody);
                if (userMatcher.find()) {
                    JSONObject content = new JSONObject();
                    content.put("username", userMatcher.group(1));
                    Pattern collectionPattern = Pattern.compile("<a class=\"grid_item\" href=\"/go/([^\"]+)\"");
                    Matcher collectionMatcher = collectionPattern.matcher(responseBody);
                    JSONArray collections = new JSONArray();
                    if (collectionMatcher.find()) {
                        collections.put(collectionMatcher.group(1));
                        while (collectionMatcher.find()) {
                            collections.put(collectionMatcher.group(1));
                        }
                    }
                    content.put("collections", collections);
                    result.put("content", content);
                    result.put("result", "success");
                    Log.e("userinfo",result.toString());
                } else {
                    result.put("result", "fail");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            handler.onSuccess(statusCode, headers,result);
		}
		
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable throwable) {
			handler.onFailure(statusCode,headers,responseBody,throwable);
		}
	});
}
	
	public static void postComment(Context context,String code,int topicId,String commentContent,final JsonHttpResponseHandler handler){
		AsyncHttpClient client=getClient(context);
		client.addHeader("Origin", "https://www.v2ex.com");
        client.addHeader("Referer", "https://www.v2ex.com/t/" + topicId);
        //client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        RequestParams params = new RequestParams();
        params.put("content", commentContent);
        params.put("once",code);
		client.post("https://www.v2ex.com/t/"+topicId,params,new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				try{
					JSONObject jsonObject=new JSONObject();
					jsonObject.put("result","fail");
				}catch(Exception e){
					e.printStackTrace();
				}
				handler.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] response, Throwable throwable) {
				JSONObject jsonObject=new JSONObject();
				try{
					if(statusCode==302){
						jsonObject.put("result","success");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				handler.onSuccess(statusCode, headers,jsonObject);
			}
		});
	}
	
	
	
}
