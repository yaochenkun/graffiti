package com.noahark.graffiti.step;

import com.noahark.graffiti.extra.Pel;

//删除图元步骤
public class DeletepelStep extends DrawpelStep 
{
	public DeletepelStep(Pel pel) 
	{
		super(pel);
	}
	
	@Override
	public void toUndoUpdate() //覆写
	{
		pelList.remove(location); //删除链表对应索引位置图元
		canvasVi.updateSavedBitmap();
	}
	
	@Override
	public void toRedoUpdate() //覆写
	{	
		pelList.add(location,curPel); //更新图元链表数据
		canvasVi.updateSavedBitmap();
	}
}
