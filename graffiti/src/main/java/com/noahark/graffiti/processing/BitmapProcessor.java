package com.noahark.graffiti.processing;

import android.graphics.Bitmap;

//图片处理接口
public interface BitmapProcessor 
{
	abstract Bitmap createProcessedBitmap(Bitmap originalBitmap);
}
