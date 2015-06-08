package com.jack.v2ex;

import java.util.List;
import org.apache.http.Header;
import org.json.JSONObject;
import com.jack.dao.DaoBaseUtil;
import com.jack.netutil.NetUtilImpl;
import com.loopj.android.http.JsonHttpResponseHandler;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class AddTopic extends SwipeBackActivity{

	private ProgressDialog progressDialog;
	private AutoCompleteTextView autotext;
	private EditText topictitle;
	private EditText topicontent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_topic);
		
		topictitle=(EditText)findViewById(R.id.topictitle);
		topicontent=(EditText)findViewById(R.id.topicontent);
		autotext=(AutoCompleteTextView)findViewById(R.id.autotext);
		new getNodeInfo().execute();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.finishadd, menu);	
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.addcontenttopic:
	    if(topictitle.getText().toString().equals("")){
	    	Toast.makeText(getBaseContext(),getString(R.string.topicnull),Toast.LENGTH_SHORT).show();
	    }else if(autotext.getText().toString().equals("")){
	    	Toast.makeText(getBaseContext(),getString(R.string.nodenull),Toast.LENGTH_SHORT).show();
	    }else{
	    	progressDialog=ProgressDialog.show(this,null,getString(R.string.adding));
		    postTopic(getBaseContext(),autotext.getText().toString());
	    }
		break;
		default:
		break;	
		}
		return true;
	}

	public void postTopic(final Context context,final String node){
		
		NetUtilImpl.getCode(context,node,new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,JSONObject response) {
				try{
					if(response.getString("result").equals("success")){
						NetUtilImpl.addTopic(context, node,response.getString("code"),topictitle.getText().toString()
								,topicontent.getText().toString(),new JsonHttpResponseHandler(){
							@Override
							public void onSuccess(int statusCode,Header[] headers,JSONObject response) {
								try{
									if(response.getString("result").equals("success")){
										progressDialog.setMessage(getString(R.string.addsuccess));
										progressDialog.dismiss();
										finish();
									}else{
										progressDialog.setMessage(getString(R.string.adderror));
										progressDialog.dismiss();
									}
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						});
					}else{
						progressDialog.setMessage(getString(R.string.adderror));
						progressDialog.dismiss();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	class getNodeInfo extends AsyncTask<Void,Void,ArrayAdapter<String>>{
		
		@Override
		protected ArrayAdapter<String> doInBackground(Void... params){
			 List<String> temp=DaoBaseUtil.GetCollectNodeTitle(getBaseContext());
			 ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(
					 getBaseContext(),android.R.layout.simple_list_item_1,temp);
	        return arrayAdapter;
		}
		
		@Override
		protected void onPostExecute(ArrayAdapter<String> result) {
			autotext.setAdapter(result);
		}
		
	}
	
	
}

