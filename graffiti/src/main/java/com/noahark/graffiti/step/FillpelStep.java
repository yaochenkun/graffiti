package com.noahark.graffiti.step;

import android.graphics.Paint;

import com.noahark.graffiti.extra.Pel;

//Ìî³äÍ¼Ôª²½Öè
public class FillpelStep extends Step 
{
	private Paint oldPaint,newPaint;
	
	public FillpelStep(Pel pel,Paint oldPaint,Paint newPaint) 
	{
		super(pel);
		this.oldPaint=new Paint(oldPaint);
		this.newPaint=new Paint(newPaint);
	}
	
	@Override
	public void toUndoUpdate() //¸²Ð´
	{
		(curPel.paint).set(newPaint);
		canvasVi.updateSavedBitmap();
	}
	
	@Override
	public void toRedoUpdate() //¸²Ð´
	{
		(curPel.paint).set(oldPaint);
		canvasVi.updateSavedBitmap();
	}
}
