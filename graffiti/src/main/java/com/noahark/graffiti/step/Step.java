package com.noahark.graffiti.step;

import java.util.List;

import android.graphics.Matrix;

import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;

//步骤类
public class Step 
{
	protected static List<Pel> pelList=CanvasView.getPelList(); // 图元链表
	protected static CanvasView canvasVi=MainActivity.getCanvasView(); //通知重绘用
	protected Pel    curPel;//最早放入undo的图元
	
	public Step(Pel pel) //构造
	{
		this.curPel=pel;
	}
	
	public void toUndoUpdate() //进undo栈时对List中图元的更新（子类覆写）
	{
	}
	
	public void toRedoUpdate()//进redo栈时对List中图元的反悔（子类覆写）
	{		
	}
	
	public void setToUndoMatrix(Matrix matrix)
	{
	}
}
