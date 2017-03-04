package com.noahark.graffiti.processing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

//图片处理接口
public class MergeHandler
{
	protected ColorMatrix cm = null;// 颜色轮对象
	
	MergeHandler()
	{
		cm = new ColorMatrix();
	}
	// 合并图像
	protected Bitmap merge(Bitmap frame, Bitmap src, int x, int y,
			Paint paintSrc, Paint paintFrame) 
	{
		int w = src.getWidth();
		int h = src.getHeight();

		Bitmap scaledFrame = Bitmap.createScaledBitmap(frame, w, h, true);
		Bitmap tmpBmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);

		Canvas canvas = new Canvas(tmpBmp);
		canvas.drawBitmap(src, x, y, paintSrc);
		canvas.drawBitmap(scaledFrame, x, y, paintFrame);

		return tmpBmp;

	}

	// 增加亮度
	protected Paint encreaseLight(int dl) 
	{
		cm.set(new float[] { 1, 0, 0, 0, dl, 0, 1, 0, 0, dl, 0, 0, 1, 0, dl, 0,
				0, 0, 1, 0 });

		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(cm));
		return paint;
	}

	// 增加对比度
	protected Paint encreaseContrast(float dc) 
	{
		cm.set(new float[] { dc, 0, 0, 0, 0, 0, dc, 0, 0, 0, 0, 0, dc, 0, 0, 0,
				0, 0, 1, 0 });

		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(cm));
		return paint;
	}
}
