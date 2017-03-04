package com.noahark.graffiti.processing;

import android.graphics.Bitmap;

//雾化效果
public class YouHua implements BitmapProcessor
{
	@Override
	public Bitmap createProcessedBitmap(Bitmap originalBitmap) 
	{
		int width = originalBitmap.getWidth();
		int height = originalBitmap.getHeight();
		
		int[] inPixels = new int[width * height];
		int[] outPixels = new int[width * height];
		originalBitmap.getPixels(inPixels, 0, width, 0, 0, width, height);
		
		int radius = 8; // default value
		int intensity = 8; // default value
		int index = 0;
		int subradius = radius / 2;
		int[] intensityCount = new int[intensity + 1];
		int[] ravg = new int[intensity + 1];
		int[] gavg = new int[intensity + 1];
		int[] bavg = new int[intensity + 1];
		for (int i = 0; i <= intensity; i++) {
			intensityCount[i] = 0;
			ravg[i] = 0;
			gavg[i] = 0;
			bavg[i] = 0;
		}
		for (int row = 0; row < height; row++) {
			int ta = 0, tr = 0, tg = 0, tb = 0;
			for (int col = 0; col < width; col++) {

				for (int subRow = -subradius; subRow <= subradius; subRow++) {
					for (int subCol = -subradius; subCol <= subradius; subCol++) {
						int nrow = row + subRow;
						int ncol = col + subCol;
						if (nrow >= height || nrow < 0) {
							nrow = 0;
						}
						if (ncol >= width || ncol < 0) {
							ncol = 0;
						}
						index = nrow * width + ncol;

						// 获得的颜色值为32位整形 分别为argb 移位并与0xff相与得到a r g b的值
						ta = (inPixels[index] >> 24) & 0xff;
						tr = (inPixels[index] >> 16) & 0xff;
						tg = (inPixels[index] >> 8) & 0xff;
						tb = inPixels[index] & 0xff;

						// 权重
						int curIntensity = (int) (((double) ((tr + tg + tb) / 3) * intensity) / 255.0f);
						intensityCount[curIntensity]++;
						ravg[curIntensity] += tr;
						gavg[curIntensity] += tg;
						bavg[curIntensity] += tb;
					}
				}

				// find the max number of same gray level pixel
				int maxCount = 0, maxIndex = 0;
				for (int m = 0; m < intensityCount.length; m++) {
					if (intensityCount[m] > maxCount) {
						maxCount = intensityCount[m];
						maxIndex = m;
					}
				}

				// get average value of the pixel
				int nr = ravg[maxIndex] / maxCount;
				int ng = gavg[maxIndex] / maxCount;
				int nb = bavg[maxIndex] / maxCount;
				index = row * width + col;
				outPixels[index] = (ta << 24) | (nr << 16) | (ng << 8) | nb;

				// post clear values for next pixel
				for (int i = 0; i <= intensity; i++) {
					intensityCount[i] = 0;
					ravg[i] = 0;
					gavg[i] = 0;
					bavg[i] = 0;
				}

			}
		}
		    
	    Bitmap processedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		processedBitmap.setPixels(outPixels, 0, width, 0, 0, width, height);	
	    
	    return processedBitmap;
	}
}
