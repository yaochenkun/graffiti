package com.noahark.graffiti.processing;


import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.activity.MainActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//µ×Æ¬Ð§¹û
public class Colorful extends MergeHandler implements BitmapProcessor
{
	@Override
	public Bitmap createProcessedBitmap(Bitmap originalBitmap) 
	{
		Bitmap mergeBitmap = BitmapFactory.decodeResource(MainActivity.getContext().getResources(),
				R.drawable.pro_colorful);
		Bitmap processedBitmap = merge(mergeBitmap, originalBitmap, 0, 0, encreaseLight(-9),encreaseLight(0));
	    
	    return processedBitmap;
	}
}
