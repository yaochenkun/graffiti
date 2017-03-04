package com.noahark.graffiti.extra;

import com.noahark.graffiti.touch.DrawTouch;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;

public class Text 
{
	private String content;
	private float transDx;
	private float transDy;
	private float scale;
	private float degree;
	private PointF centerPoint;
	private PointF beginPoint;
	private Paint paint;
	
	public Text(String content,float transDx,float transDy,float scale,
			float degree,PointF centerPoint,PointF beginPoint)
	{
		this.content = content;
		this.transDx=transDx;
		this.transDy=transDy;
		this.scale=scale;
		this.degree=degree;
		this.centerPoint=new PointF();
		(this.centerPoint).set(centerPoint);
		this.beginPoint=new PointF();
		(this.beginPoint).set(beginPoint);
		paint=new Paint();
		paint.setColor(DrawTouch.getCurPaint().getColor());
		paint.setTextSize(50);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
	}
	
	public String getContent()
	{
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
	
	public Paint getPaint()
	{
		return paint;
	}
}
