package com.jack.mainfragment;

import org.apache.http.Header;
import org.json.JSONObject;
import com.jack.netutil.NetUtilImpl;
import com.jack.v2ex.LoginActivity;
import com.jack.v2ex.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

public class SettingFragment extends PreferenceFragment{

	private boolean LoginState;
	private String username;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting_fragment);
		if((LoginState=getPreferenceManager().getSharedPreferences().contains("username"))){
			username=getPreferenceManager().getSharedPreferences().getString("username",null);
			if(username!=null){
				findPreference("login").setTitle(username);
			}else{
				findPreference("updateuser").setEnabled(false);
			}
		}else{
			findPreference("updateuser").setEnabled(false);
		}
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,Preference preference) {
		switch(preference.getKey()){
		case "login":
			startLogin();
		break;
		case "updateuser":
			updateUserInfo();
		break;	
		}
		return true;
	}
	
	public void startLogin(){
		if(LoginState){
			AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
			builder.setMessage("µÇ³ö£¿");
			builder.setPositiveButton(R.string.posititve,new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					getPreferenceManager().getSharedPreferences().edit().remove("username").commit();
				    LoginState=false;
					findPreference("login").setTitle(R.string.login);
					findPreference("updateuser").setEnabled(false);
				}
			});
			builder.setNegativeButton(R.string.negative,null);
			builder.show();
		}else{
			Intent intent=new Intent(getActivity(),LoginActivity.class);
			startActivityForResult(intent,1);
		}
	}
	
	public void updateUserInfo(){
		      final ProgressDialog progressDialog=ProgressDialog.show(getActivity(),null
		    		  ,getString(R.string.updateing),true,true);	
				NetUtilImpl.getinfo(getActivity(), new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(int statusCode, Header[] headers,JSONObject response) {
						try{
							username=response.getJSONObject("content").getString("username");
							Log.e("username",username);
							findPreference("login").setTitle(username);
							PreferenceManager.getDefaultSharedPreferences(getActivity())
								.edit().putString("username",username).commit();
							Thread.sleep(1000);
							progressDialog.setMessage("Íê³É");
							progressDialog.dismiss();
						}catch(Exception e){
							e.printStackTrace();
						}
					}	
				});
	}
	
	
	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
			if(resultCode==getActivity().RESULT_OK){
				username=data.getExtras().getString("username");
				LoginState=true;
				findPreference("login").setTitle(username);
				findPreference("updateuser").setEnabled(true);
				PreferenceManager.getDefaultSharedPreferences(getActivity())
					.edit().putString("username",username).commit();
			}
	}
}
