package com.noahark.graffiti.processing;

import android.graphics.Bitmap;
import android.graphics.Color;

//ºÚ°×Ð§¹û
public class HeiBai implements BitmapProcessor
{
	@Override
	public Bitmap createProcessedBitmap(Bitmap originalBitmap) 
	{
		int width = originalBitmap.getWidth();
		int height = originalBitmap.getHeight();		
		int[] pixels = new int[width * height];
		originalBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		
		for (int i = 0; i < height; i++)
		{
			for (int k = 0; k < width; k++)
			{
				int pixColor = pixels[width * i + k];
				int pixR = Color.red(pixColor);
				int pixG = Color.green(pixColor);
				int pixB = Color.blue(pixColor);
				
				int newR = (int)(pixR+pixG+pixB) / 3;
				int newG = (int)(pixR+pixG+pixB) / 3;
				int newB = (int)(pixR+pixG+pixB) / 3;
				int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
				pixels[width * i + k] = newColor;
			}
		}		
	    
	    Bitmap processedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		processedBitmap.setPixels(pixels, 0, width, 0, 0, width, height);	
	    
	    return processedBitmap;
	}
}
