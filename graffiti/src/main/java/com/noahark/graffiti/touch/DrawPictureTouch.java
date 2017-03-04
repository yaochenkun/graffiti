package com.noahark.graffiti.touch;

import android.graphics.PointF;

import com.noahark.graffiti.ui.activity.DrawPictureActivity;

public class DrawPictureTouch extends Touch
{
	
	private final float MIN_ZOOM=10; //缩放下限	
	private final float MAX_DY=70; //缩放与旋转切换上限
	private final int NONE = 0; // 平移操作
	private final int DRAG = 1; // 平移操作
	private final int ZOOM = 2; // 缩放操作
	private final int ROTATE = 3; // 旋转操作
	private int mode = NONE; // 当前操作类型
	
	private float dx,dy,oridx,oridy; //平移偏移量
	private float scale,oriscale; // 缩放时两指最初放上时的距离
	private float degree,oridegree;//旋转量
	
	private PointF downPoint;
	private float oriDist; // 缩放时两指最初放上时的距离
	
	
	public DrawPictureTouch()
	{
		downPoint=new PointF();
		
		dx=dy=oridx=oridy=0;
		scale=oriscale=1;
		degree=oridegree=0;
	}
	
	// 第一只手指按下
	@Override
	public void down1()
	{
		super.down1();
		// 获取down事件的发生位置
		downPoint.set(curPoint);
		mode = DRAG;
	}
	
	// 第二只手指按下
	@Override
	public void down2()
	{
		oriDist = distance();
		if (oriDist > MIN_ZOOM) 
		{
			// 距离小于50px才算是缩放
			mode = ZOOM;
		}
	}
	
	// 手指移动
	@Override
	public void move()
	{		
		super.move();
		if (mode == DRAG)// 平移操作
		{			
			dx = oridx + (curPoint.x - downPoint.x);//计算距离
			dy = oridy + (curPoint.y - downPoint.y);					
		} 
		else if (mode == ZOOM) // 缩放操作
		{	
			float newDist = distance();					
			float dy=Math.abs(curPoint.y-secPoint.y);//两指的垂直间距				
			if(dy >= MAX_DY)//判断是否需要转变为旋转模式
			{
				//延续准备操作
				mode=ROTATE;
				downPoint.set(curPoint);
			}
			else if (newDist > MIN_ZOOM) 
			{
				//<100仍然是正常缩放										
				scale = oriscale*(newDist / oriDist);
			}
		} 
		else if (mode == ROTATE) // 旋转操作
		{			
			float dy=Math.abs(curPoint.y-secPoint.y);//两指的垂直间距					
			if(dy < MAX_DY)//判断是否需要转变为缩放模式
			{
				mode=ZOOM;
				oriDist=distance();
			}
			else//>100仍然是正常旋转
			{							
				degree=(oridegree % 360) + degree();
			}
		}
	}
	
	// 手指抬起
	@Override
	public void up()
	{
		if(dis < 10f)
		{	
			dis=0;
			DrawPictureActivity.openTools();
			return;
		}
		dis=0;
		
		//改变文字的坐标
		oridx=dx;oridy=dy;
		oriscale=scale;
		oridegree=degree;
		mode = NONE;
	}
	
	/*
	 * 自定义函数
	 */
	
	// 计算两个触摸点之间的距离
	private float distance() 
	{
		float x = curPoint.x - secPoint.x;
		float y = curPoint.y - secPoint.y;
		return (float)Math.sqrt(x * x + y * y);
	}

	// 旋转角度的计算
	private float degree() 
	{
		// 获得两次down下时的距离
		float x=curPoint.x-downPoint.x;
		float y=curPoint.y-downPoint.y;
		
		float arc=(float)Math.sqrt(x * x + y * y);//弧长
		float radius=distance()/2;//半径

		float degrees=(arc/radius)*(180/3.14f);
				
		return degrees;
	}
	
	public float getDx()
	{
		return dx;
	}
	
	public float getDy()
	{
		return dy;
	}
	
	public float getScale()
	{
		return scale;
	}
	
	public float getDegree()
	{
		return degree;
	}
	
	public void clear()
	{
		dx=dy=oridx=oridy=0;
		scale=oriscale=1;
		degree=oridegree=0;
	}
}
