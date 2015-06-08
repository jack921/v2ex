package com.jack.v2ex;

import java.util.ArrayList;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import com.jack.netutil.NetUtilImpl;
import com.loopj.android.http.JsonHttpResponseHandler;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends SwipeBackActivity implements OnClickListener{

	private EditText username;
	private EditText password;
	private Button login;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		username=(EditText)findViewById(R.id.login_user);
		password=(EditText)findViewById(R.id.login_pass);
		login=(Button)findViewById(R.id.login_bu);
		
		login.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v){
		if(username.getText().toString().equals("")){
			Toast.makeText(this,getString(R.string.usernull),Toast.LENGTH_SHORT).show();
		}if(password.getText().toString().equals("")){
			Toast.makeText(this,getString(R.string.passnull),Toast.LENGTH_SHORT).show();
		}else{
			getCode();
		}		
	}	
	
	public void getCode(){
	    progressDialog=ProgressDialog.show(this,null,getString(R.string.logining),true,true);
		NetUtilImpl.GetCode(this,new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,JSONObject response){
				try{
					if(response.getString("result").equals("success")){
					   login(response.getString("code"));
				   }else{
						Toast.makeText(getBaseContext(),getString(R.string.loginerror),Toast.LENGTH_SHORT).show();
						progressDialog.setMessage(getString(R.string.loginerror));
						progressDialog.dismiss();
				   }
				}catch(Exception e){
					e.printStackTrace();
					progressDialog.setMessage(getString(R.string.loginerror));
					progressDialog.dismiss();
				}
			}
			});
	}
	
	public void login(String code){
		NetUtilImpl.login(code,username.getText().toString(),password.getText().toString(),getBaseContext()
			,new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,JSONObject response) {
			try{
				if(response.get("result").equals("success")){
					getUserInfo();			
				}else{
					progressDialog.setMessage(getString(R.string.loginerror));
					progressDialog.dismiss();
				}
			    }catch(Exception e){
			    	e.printStackTrace();
			}
			}					
		});
	}
	
	public void getUserInfo(){ 
		NetUtilImpl.getinfo(this,new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,JSONObject response) {
				try{
					if(response.getString("result").equals("success")){
						Intent intent=new Intent();
						ArrayList<String> collect=new ArrayList<String>();
						JSONArray jsonArray=response.getJSONObject("content").getJSONArray("collections");
						for(int i=0;i<jsonArray.length();i++){
							collect.add(jsonArray.getString(i));
						}
						intent.putExtra("username",response.getJSONObject("content").getString("username"));
						intent.putStringArrayListExtra("collect",collect);
						setResult(RESULT_OK,intent);
						finish();
					}else{
						progressDialog.setMessage(getString(R.string.loginerror));
						progressDialog.dismiss();
					}
				}catch(Exception e){
				}
			}
		});
	}
	
}
