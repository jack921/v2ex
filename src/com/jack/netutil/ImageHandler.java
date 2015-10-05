package com.jack.netutil;

import org.xml.sax.XMLReader;
import com.jack.v2ex.PhotoShowActivity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.Spanned;
import android.text.Html.TagHandler;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;

public class ImageHandler implements TagHandler{

	private Context context;
	
	public ImageHandler(Context context) {
		this.context = context;
	}
	
	@Override
	public void handleTag(boolean opening, String tag, Editable output,XMLReader xmlReader) {

		// �����ǩ<img> 		
		if (tag.toLowerCase().equals("img")) {
			// ��ȡ����
			int len = output.length();
			ImageSpan[] images=output.getSpans(len-1,len,ImageSpan.class);
		    String imageUrl = images[0].getSource();
		    //ʹͼƬ�ɵ������������¼�
			output.setSpan(new ImageClick(this.context,imageUrl), len-1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			Log.e("error",imageUrl);
		}
	}
	
	private class ImageClick extends ClickableSpan{

		private Context context;
		private String imageUrl;
		
		public ImageClick(Context context,String imageUrl) {
			this.context = context;
			this.imageUrl=imageUrl;
		    Log.e("imageUrl",this.imageUrl);
		}
		
		@Override
		public void onClick(View widget) {
			// ��ͼƬURLת��Ϊ����·�������Խ�ͼƬ���������ͼƬ�������дΪһ���������������
		    Intent intent=new Intent(context,PhotoShowActivity.class);
		    intent.putExtra("photo",imageUrl);
		    context.startActivity(intent);
		}
	}
	
}
