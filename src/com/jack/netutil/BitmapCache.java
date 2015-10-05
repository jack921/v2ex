package com.jack.netutil;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapCache implements ImageCache{
	
	public static LruCache<String,Bitmap> mCache=new LruCache<String,Bitmap>(
			((int)(Runtime.getRuntime().maxMemory()/1024))/8){
			@Override
			protected int sizeOf(String key, Bitmap bitmap){
				return bitmap.getByteCount()/1024;
		}
	};
	
	@Override
	public Bitmap getBitmap(String arg0){
		return mCache.get(arg0);
	}
	
	@Override
	public void putBitmap(String arg0, Bitmap arg1) {
		mCache.put(arg0,arg1);
	}
	
}
