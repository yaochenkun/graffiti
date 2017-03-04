package com.noahark.graffiti.touch;

import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;

import android.graphics.Path;
import android.graphics.RectF;

public class DrawRectTouch extends DrawTouch {

	public DrawRectTouch() 
	{
		super();
	}

	@Override
	public void move() 
	{
		super.move();
		
		newPel = new Pel();

		movePoint.set(curPoint);

		(newPel.path).addRect(new RectF(downPoint.x, downPoint.y, movePoint.x, movePoint.y), Path.Direction.CCW);

		CanvasView.setSelectedPel(selectedPel = newPel);
	}
	
	@Override
	public void up()
	{
		newPel.closure=true;
		super.up();
	}	
}
