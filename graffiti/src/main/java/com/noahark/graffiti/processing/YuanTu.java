package com.noahark.graffiti.processing;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

//µ×Æ¬Ð§¹û
public class YuanTu implements BitmapProcessor
{
	@Override
	public Bitmap createProcessedBitmap(Bitmap originalBitmap) 
	{	    
		Bitmap processedBitmap = originalBitmap.copy(Config.ARGB_8888,true);
		 
	    return processedBitmap;
	}
}
