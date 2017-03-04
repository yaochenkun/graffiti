package com.noahark.graffiti.ui.view;

import com.noahark.graffiti.ui.activity.DrawTextActivity;
import com.noahark.graffiti.touch.DrawTextTouch;
import com.noahark.graffiti.touch.DrawTouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawTextView extends View
{
	private Bitmap savedBitmap;
	public static DrawTextTouch touch;
	public static Paint drawTextPaint;
	private PointF textPoint;//文字坐标
	private PointF centerPoint;//文字中心
	boolean firstTime=true;
	int width,height;
	private String content="意绘";
	
	public DrawTextView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		drawTextPaint=new Paint();
		drawTextPaint.setColor(DrawTouch.getCurPaint().getColor());
		drawTextPaint.setTextSize(50);	
		drawTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
		touch=new DrawTextTouch();
		textPoint=new PointF();
		textPoint.set(touch.getTextPoint());
		centerPoint=new PointF();
		centerPoint.set(touch.getCenterPoint());
		savedBitmap=Bitmap.createScaledBitmap(CanvasView.savedBitmap,CanvasView.CANVAS_WIDTH,CanvasView.CANVAS_HEIGHT,true);
	}
	
	//触摸事件
	public boolean onTouchEvent(MotionEvent event) 
	{
		touch.setCurPoint(new PointF(event.getX(0),event.getY(0)));
		
		//第二只手指坐标（可能在第二只手指还没按下时发生异常）
		try{touch.setSecPoint(new PointF(event.getX(1),event.getY(1)));}
		catch(Exception e){touch.setSecPoint(new PointF(1,1));}
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) 
		{
			case MotionEvent.ACTION_DOWN:// 第一只手指按下
			{
				if(DrawTextActivity.topToolbar.getVisibility() == View.VISIBLE)
				{
					DrawTextActivity.closeTools();
					touch.dis=Float.MAX_VALUE;
				}
				touch.down1();
			}break;
			case MotionEvent.ACTION_POINTER_DOWN:// 第二个手指按下
			{
				touch.down2();
			}break;
			case MotionEvent.ACTION_MOVE:
			{
				touch.move();
			}break;
			case MotionEvent.ACTION_UP:// 第一只手指抬起
			case MotionEvent.ACTION_POINTER_UP://第二只手抬起
			{
				touch.up();
			}break;
		}
		invalidate();
	
		return true;
	}

	//重绘
	protected void onDraw(Canvas canvas) 
	{
		canvas.drawBitmap(savedBitmap, 0, 0, new Paint());// 画其余图元
		
		canvas.translate(touch.getDx(),touch.getDy());
		canvas.scale(touch.getScale(), touch.getScale(),centerPoint.x,centerPoint.y);
		canvas.rotate(touch.getDegree(),centerPoint.x,centerPoint.y);
		canvas.drawText(content,textPoint.x, textPoint.y, drawTextPaint);
	}
	
	public static void ensureBitmapRecycled(Bitmap bitmap) //确保传入位图已经回收
	{
		if(bitmap != null && !bitmap.isRecycled())	
			bitmap.recycle();
	}
	
	public void setContent(String content)
	{
		this.content=content;
	}
}
