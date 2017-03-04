package com.noahark.graffiti.ui.view;

import com.noahark.graffiti.ui.activity.DrawPictureActivity;
import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.touch.DrawPictureTouch;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawPictureView extends View
{
	private Bitmap savedBitmap;
	private DrawPictureTouch touch;
	private PointF textPoint;//文字坐标
	private PointF centerPoint;//文字中心
	private int contentId;
	private Bitmap content=null;
	
	public DrawPictureView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);

		touch=new DrawPictureTouch();
		textPoint=new PointF();
		centerPoint=new PointF();
		savedBitmap=Bitmap.createScaledBitmap(CanvasView.savedBitmap,CanvasView.CANVAS_WIDTH,CanvasView.CANVAS_HEIGHT,true);
		
		textPoint.set(CanvasView.CANVAS_WIDTH/2.5f,CanvasView.CANVAS_HEIGHT/2.5f);
		centerPoint.set(textPoint);
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
				if(DrawPictureActivity.topToolbar.getVisibility() == View.VISIBLE)
				{
					DrawPictureActivity.closeTools();
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
		
		//防止还没有得到图片时的第一次刷新
		if(content != null)
		{
			canvas.translate(touch.getDx(),touch.getDy());
			canvas.scale(touch.getScale(), touch.getScale(),centerPoint.x,centerPoint.y);
			canvas.rotate(touch.getDegree(),centerPoint.x,centerPoint.y);
			canvas.drawBitmap(content,textPoint.x, textPoint.y, null);
		}
	}
	
	public void setContentAndCenterPoint(int contentId)
	{	
		this.contentId=contentId;
		ensureBitmapRecycled(content);
		this.content=BitmapFactory.decodeResource(MainActivity.getContext().getResources(),
				contentId);
		centerPoint.set(textPoint.x + content.getWidth()/2,textPoint.y+content.getHeight()/2);
	}
	
	public static void ensureBitmapRecycled(Bitmap bitmap) //确保传入位图已经回收
	{
		if(bitmap != null && !bitmap.isRecycled())	
			bitmap.recycle();
	}
	
	public DrawPictureTouch getTouch()
	{
		return touch;
	}
	
	public PointF getTextPoint()
	{
		return textPoint;
	}
	
	public PointF getCenterPoint()
	{
		return centerPoint;
	}
	
	public int getContentId()
	{
		return contentId;
	}
	
	public Bitmap getContent()
	{
		return content;
	}
}
