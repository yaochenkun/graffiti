package com.noahark.graffiti.touch;

import android.graphics.Path;
import android.graphics.PointF;

import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;

public class DrawPolygonTouch extends DrawTouch 
{
	protected boolean firstDown = true;
	protected Path lastPath;

	protected final float MAX_CIRCLE = 50;

	public DrawPolygonTouch() 
	{
		super();
		lastPath = new Path();
	}

	@Override
	public void down1() 
	{
		super.down1();

		if (firstDown == true)// 画折线的第一笔
		{
			beginPoint.set(downPoint);

			newPel = new Pel();
			(newPel.path).moveTo(beginPoint.x, beginPoint.y);
			lastPath.set(newPel.path);
			
			firstDown = false;
		}
	}

	@Override
	public void move() 
	{
		super.move();
		
		movePoint.set(curPoint);

		(newPel.path).set(lastPath);
		(newPel.path).lineTo(movePoint.x, movePoint.y);

		CanvasView.setSelectedPel(selectedPel = newPel);
	}

	@Override
	public void up() 
	{	
		if(isNeedToOpenTools() == true)
		{
			return;
		}
		else
		{
			PointF endPoint=new PointF();
			endPoint.set(curPoint);
	
			if (distance(beginPoint, endPoint) <= MAX_CIRCLE) 
			{
				(newPel.path).set(lastPath);
				(newPel.path).close();
				newPel.closure=true;
				super.up();
	
				firstDown = true;
			}
			lastPath.set(newPel.path);
		}
	}
	
	public boolean isNeedToOpenTools()
	{
		if(dis < 10f)
		{	
			dis=0;
			MainActivity.openTools();
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	// 计算up与最先down下的距离是否在领域内
	public float distance(PointF begin, PointF end) 
	{
		float x = begin.x - end.x;
		float y = begin.y - end.y;
		return (float)Math.sqrt(x * x + y * y);
	}	
}
