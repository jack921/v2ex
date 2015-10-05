package com.jack.netutil;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.TextView;
import com.jack.v2ex.MainActivity;
import com.jack.v2ex.R;

public class VolleyImageGetter implements Html.ImageGetter{

	private Context context;
	private ImageRequest imageRequest;
	private static Drawable mDefaultDrawable;
	private TextView itemContent;
	private static int maxWidth;
	private final UrlDrawable urlDrawable;
	
	public VolleyImageGetter(Context context,TextView ItemContent){
		mDefaultDrawable=context.getResources().getDrawable(R.drawable.ic_launcher);
		maxWidth=VolleyImageGetter.getDisplayWidth(context)-VolleyImageGetter.dp(context,100);
		urlDrawable=new UrlDrawable();
		this.itemContent=ItemContent;
		this.context=context;
	}
	
	@Override
	public Drawable getDrawable(String source){
		Bitmap bitmap=getBitmapCache(source);
		if(bitmap!=null){
			Log.e("getBitmapCache","getBitmapCache");
			Log.d("byteCount",bitmap.getByteCount()+"");
			int width;
			int height;
			if(bitmap.getWidth()>maxWidth){
				width=maxWidth;
				height=maxWidth*bitmap.getHeight()/bitmap.getWidth();
			}else{
				width=bitmap.getWidth();
				height=bitmap.getHeight();
			}
			Drawable drawable=new BitmapDrawable(context.getResources(),bitmap);
			drawable.setBounds(0,0,width,height);
			return drawable;
	    }else{
	    	new getImage().execute(source);
			return urlDrawable;
	    }
	}
		
	class getImage extends AsyncTask<String,Void,TextView>{	
		@Override
		protected TextView doInBackground(String... params) {
			final String source=params[0];
			imageRequest=new ImageRequest(params[0],
					new Response.Listener<Bitmap>() {
						@Override
						public void onResponse(Bitmap bitmap) {
							if(bitmap!=null){
								Log.d("bitmapbyte",bitmap.getByteCount()+"");
								int width;
								int height;
								if(bitmap.getWidth()>maxWidth){
									width=maxWidth;
									height=maxWidth*bitmap.getHeight()/bitmap.getWidth();
								}else{
									width=bitmap.getWidth();
									height=bitmap.getHeight();
								}
								Drawable drawable=new BitmapDrawable(context.getResources(),bitmap);
								drawable.setBounds(0,0,width,height);
								urlDrawable.setBounds(0,0,width,height);
								urlDrawable.drawable=drawable;
								AddBitmapToMemory(source,((BitmapDrawable)drawable).getBitmap());
								itemContent.setText(itemContent.getText());
							}
						}
					},800,500,Config.RGB_565,
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError e) {
						}
					});
			MainActivity.mQueue.add(imageRequest);
			return itemContent;
		}
		
		@Override
		protected void onPostExecute(TextView result) {
			//itemContent.setText(itemContent.getText());
		}
	}
	
	public void AddBitmapToMemory(String key,Bitmap bitmap){
		if(getBitmapCache(key)==null){
			BitmapCache.mCache.put(key,bitmap);
		}
	}
	
	public Bitmap getBitmapCache(String key){
		return BitmapCache.mCache.get(key);
	}
	
    public interface VolleyDrawableCallback{
		void onSuccess(Drawable result);
	}

	@SuppressWarnings("deprecation")
	public class UrlDrawable extends BitmapDrawable{
		private Drawable drawable;
		
		@Override
		public void draw(Canvas canvas) {
			if(drawable!=null){
				drawable.draw(canvas);
			}else{
				mDefaultDrawable.draw(canvas);
			}
		}
	}
	
	public static int getDisplayWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        return displayWidth;
    }
	
	public static int dp(Context context, float dp){
        Resources resources = context.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return px;
    };
	
}
