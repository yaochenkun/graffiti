package com.noahark.graffiti.processing;

import android.graphics.Bitmap;
import android.graphics.Color;

//»ýÄ¾Ð§¹û
public class JiMu implements BitmapProcessor
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
				
				pixColor=(int)(pixR+pixG+pixB) / 3;
				if(pixColor >= 128)	
					pixColor = 255;
				else				
					pixColor = 0;
				
				int newR = pixColor;
				int newG = pixColor;
				int newB = pixColor;
				
				int newColor = Color.argb(255, newR, newG, newB);
				pixels[width * i + k] = newColor;
			}
		}
	    
	    Bitmap processedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		processedBitmap.setPixels(pixels, 0, width, 0, 0, width, height);	
	    
	    return processedBitmap;
	}
}
