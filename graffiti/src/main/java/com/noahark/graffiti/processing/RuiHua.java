package com.noahark.graffiti.processing;

import android.graphics.Bitmap;
import android.graphics.Color;

//Èñ»¯Ð§¹û
public class RuiHua implements BitmapProcessor
{
	@Override
	public Bitmap createProcessedBitmap(Bitmap originalBitmap) 
	{
		int width = originalBitmap.getWidth();
		int height = originalBitmap.getHeight();
		
		int[] pixels = new int[width * height];
		originalBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		
		int[] laplacian = new int[] { -1, -1, -1, -1, 9, -1, -1, -1, -1 };
        int newR=0,newG=0,newB=0;
        int idx=0;
        float alpha = 0.3F;
	    for (int i = 1, length = height - 1; i < length; i++) 
	    {
	    	for (int k = 1, len = width - 1; k < len; k++) 
	    	{
	    		idx = 0;
	            for (int m = -1; m <= 1; m++) 
	            {
	            	for (int n = -1; n <= 1; n++) 
	                {
	            		int pixColor = pixels[(i + n) * width + k + m];
                        int pixR = Color.red(pixColor);
                        int pixG = Color.green(pixColor);
                        int pixB = Color.blue(pixColor);

                        newR = newR + (int) (pixR * laplacian[idx] * alpha);
                        newG = newG + (int) (pixG * laplacian[idx] * alpha);
                        newB = newB + (int) (pixB * laplacian[idx] * alpha);
                        idx++;
	                }
	            }

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));

                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
                newR = 0;
                newG = 0;
                newB = 0;
            }
	    }
	    
	    Bitmap processedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		processedBitmap.setPixels(pixels, 0, width, 0, 0, width, height);	
	    
	    return processedBitmap;
	}
}
