package com.noahark.graffiti.touch;

import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;

import android.graphics.PointF;


public class DrawFreehandTouch extends DrawTouch 
{
	private PointF lastPoint;
	
	public DrawFreehandTouch()
	{
		super();
		lastPoint=new PointF();
	}

	@Override
	public void down1()
	{
		super.down1();
		lastPoint.set(downPoint);

		newPel=new Pel();
		(newPel.path).moveTo(lastPoint.x, lastPoint.y);
	}
	
	@Override
	public void move()
	{
		super.move();
		
		movePoint.set(curPoint);
		
		(newPel.path).quadTo(lastPoint.x,lastPoint.y, (lastPoint.x+movePoint.x)/2, (lastPoint.y+movePoint.y)/2);
		lastPoint.set(movePoint);
		
		CanvasView.setSelectedPel(selectedPel = newPel);
	}
	
	@Override
	public void up()
	{
		newPel.closure=true;
		super.up();
	}
}
