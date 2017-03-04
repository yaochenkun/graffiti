package com.noahark.graffiti.touch;

import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.extra.Pel;

import android.R.drawable;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Toast;
import android.app.AlertDialog.Builder;

public class KeepDrawingTouch extends Touch
{
	private PointF downPoint;
	public  static Pel newPel;
	
	public KeepDrawingTouch()
	{
		super();
		downPoint=new PointF();
	}
	
	// 第一只手指按下
	@Override
	public void down1()
	{
		downPoint.set(curPoint);
		updateSavedBitmap();
		
		//弹出再次确认对话框
		class okClick implements DialogInterface.OnClickListener
		{
			public void onClick(DialogInterface dialog, int which) //ok
			{	
				(MainActivity.lastPoint).set(downPoint);
				MainActivity.registerKeepdrawingSensor(null);
				Toast.makeText(MainActivity.getContext(), "摆动手机画图吧，吹一吹即可停止哦~", Toast.LENGTH_SHORT).show();
			}		
		}		
		class cancelClick implements DialogInterface.OnClickListener //cancel
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				Toast.makeText(MainActivity.getContext(), "请重新选择起点", Toast.LENGTH_SHORT).show();
			}		
		}
		
		//实例化确认对话框
		Builder dialog=new AlertDialog.Builder(MainActivity.getContext());
		dialog.setIcon(drawable.ic_dialog_info);
		dialog.setMessage("您确定以("+Integer.toString((int)downPoint.x)+","+Integer.toString((int)downPoint.y)+")为起始点并开始重力绘图？");
		dialog.setPositiveButton("确定", new okClick());
		dialog.setNegativeButton("取消", new cancelClick());
		dialog.create();
		dialog.show();
	}
	
	@Override
	public void updateSavedBitmap()
	{
		super.updateSavedBitmap();
		
		//调取起始点标志图片
		BitmapDrawable startFlag=(BitmapDrawable)MainActivity.getContext().getResources().getDrawable(R.drawable.img_startflag);
		savedCanvas.drawBitmap(startFlag.getBitmap(), downPoint.x, downPoint.y, null);	
	}
}
