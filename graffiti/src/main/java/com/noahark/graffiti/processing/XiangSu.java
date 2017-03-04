package com.noahark.graffiti.processing;


import android.graphics.Bitmap;

//Îí»¯Ð§¹û
public class XiangSu implements BitmapProcessor
{
	@Override
	public Bitmap createProcessedBitmap(Bitmap originalBitmap) 
	{
		int width = originalBitmap.getWidth();
		int height = originalBitmap.getHeight();
		
		int[] pixels = new int[width * height];
		originalBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		
		int color = 0;
		int radius=5;
		for (int j = radius; j < height; j += 2 * radius) {
			for (int i = radius; i < width; i += 2 * radius) {
				color = pixels[width*j+i];
				for (int subRow = -radius; subRow < radius; subRow++) {
					for (int subCol = -radius; subCol < radius; subCol++) {
						pixels[(j + subRow)*width+(i + subCol)] = color;

					}
				}

			}
		}

	    Bitmap processedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		processedBitmap.setPixels(pixels, 0, width, 0, 0, width, height);	
	    
	    return processedBitmap;
	}
}
