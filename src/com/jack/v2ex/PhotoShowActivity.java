package com.jack.v2ex;

import com.jack.netutil.BitmapCache;
import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

public class PhotoShowActivity extends Activity{

	private ImageView imageView;
	private PhotoViewAttacher mAttacher;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photoview);
		imageView=(ImageView)findViewById(R.id.photoviewshow);
	    imageView.setImageBitmap(BitmapCache.mCache.get(getIntent().getStringExtra("photo")));
	    mAttacher = new PhotoViewAttacher(imageView);
		mAttacher.update();
	}
	
}
