package com.noahark.graffiti.step;

import android.graphics.Matrix;
import android.graphics.Region;

import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;

//变换图元步骤
public class TransformpelStep extends Step 
{
	private Matrix toUndoMatrix;//变换前的matrix
	private static Region clipRegion=CanvasView.getClipRegion();
	private Pel    savedPel;
	
	public TransformpelStep(Pel pel) //构造
	{
		super(pel);//重写父类
		toUndoMatrix=new Matrix();
		savedPel=curPel.clone();
	}
	
	@Override
	public void toUndoUpdate() //覆写
	{
		(curPel.path).transform(toUndoMatrix);
		(curPel.region).setPath(curPel.path, clipRegion);

		CanvasView.setSelectedPel(null);
		canvasVi.updateSavedBitmap();
	}
	
	@Override
	public void toRedoUpdate() //覆写
	{
		(curPel.path).set(savedPel.path);
		(curPel.region).setPath(curPel.path, clipRegion);
		
		CanvasView.setSelectedPel(null);
		canvasVi.updateSavedBitmap();
	}
	
	/*
	 * set()方法
	 */
	public void setToUndoMatrix(Matrix matrix)
	{
		toUndoMatrix.set(matrix);
	}
}
