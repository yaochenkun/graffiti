package com.noahark.graffiti.extra;


import com.noahark.graffiti.ui.activity.MainActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

public class Picture 
{
	private int contentId;
	private Bitmap content;
	private float transDx;
	private float transDy;
	private float scale;
	private float degree;
	private PointF centerPoint;
	private PointF beginPoint;
	
	public Picture(int contentId,float transDx,float transDy,float scale,
			float degree,PointF centerPoint,PointF beginPoint)
	{
		this.contentId = contentId;
		this.transDx=transDx;
		this.transDy=transDy;
		this.scale=scale;
		this.degree=degree;
		this.centerPoint=new PointF();
		(this.centerPoint).set(centerPoint);
		this.beginPoint=new PointF();
		(this.beginPoint).set(beginPoint);
	}
	
	public Bitmap createContent()
	{
		content = BitmapFactory.decodeResource(MainActivity.getContext().getResources(),
				contentId);
		return content;
	}
	
	public float getTransDx()
	{
		return transDx;
	}
	
	public float getTransDy()
	{
		return transDy;
	}
	
	public float getScale()
	{
		return scale;
	}

	public float getDegree()
	{
		return degree;
	}

	public PointF getCenterPoint()
	{
		return centerPoint;
	}
	
	public PointF getBeginPoint()
	{
		return beginPoint;
	}
}
