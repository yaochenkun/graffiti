package com.noahark.graffiti.touch;

import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.extra.Picture;
import com.noahark.graffiti.extra.Text;
import com.noahark.graffiti.step.Step;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Region;

//触摸类
public class Touch
{
	protected static Stack<Step> undoStack=CanvasView.getUndoStack();//获取undo
	protected static Stack<Step> redoStack=CanvasView.getRedoStack();//获取redo
	protected static Region clipRegion=CanvasView.getClipRegion(); // 画布裁剪区域
	protected static List<Pel> pelList=CanvasView.getPelList(); // 图元链表// 屏幕宽高
	protected Pel selectedPel; // 当前选中图元
	protected Bitmap savedBitmap; // 当前重绘位图	
	protected Canvas savedCanvas; //重绘画布
	
	public PointF curPoint; //当前第一只手指事件坐标
	public PointF secPoint; //当前第二只手指事件坐标
	public static Step   step=null; //当前touch事件结束以后将要压入undo栈的步骤
	public float dis;//整个触摸过程在x和y方向上的偏移总量
	protected PointF frontPoint1,frontPoint2;
	
	//特殊处理用
	public boolean control=false; //贝塞尔曲线切换时敲定
	public PointF  beginPoint;//多边形时敲定
	public boolean hasFinished=false;
	public static  Matrix  oriMatrix;//浏览图片的初始因子
	/*
	 * 需继承的方法
	 */
	public Touch() 
	{
		selectedPel = CanvasView.getSelectedPel();
		
		savedCanvas=new Canvas();
		curPoint=new PointF();
		secPoint=new PointF();
		beginPoint=new PointF();
		frontPoint1=new PointF();
		frontPoint2=new PointF();
		dis=0;
	}

	// 第一只手指按下
	public void down1()
	{
		frontPoint1.set(curPoint);
	}

	// 第二只手指按下
	public void down2()
	{
		frontPoint2.set(secPoint);
	}

	// 手指移动
	public void move()
	{
		float dis1=Math.abs(curPoint.x-frontPoint1.x)+Math.abs(curPoint.y-frontPoint1.y);
		float dis2=0;

		if(secPoint != null)
		{
			dis2=Math.abs(secPoint.x-frontPoint2.x)+Math.abs(secPoint.y-frontPoint2.y);
			frontPoint2.set(secPoint);
		}
		dis+=dis1+dis2;

		frontPoint1.set(curPoint);
	}

	// 手指抬起
	public void up()
	{
		if(dis < 10f)
		{	
			dis=0;
			MainActivity.openTools();
			return;
		}
		dis=0;	
	}
	
	//更新重绘背景位图用（当且仅当选择的图元有变化的时候才调用）
	protected void updateSavedBitmap()
	{
		//创建缓冲位图
		Bitmap backgroundBitmap=CanvasView.getBackgroundBitmap();
		CanvasView.ensureBitmapRecycled(savedBitmap);
		savedBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);//由画布背景创建缓冲位图
		savedCanvas.setBitmap(savedBitmap); //与画布建立联系

		drawPels();

		CanvasView.setSavedBitmap(savedBitmap); // 改变CanvasView中的savedBitmap方便更新
	}
	public void drawPels()
	{
		ListIterator<Pel> pelIterator = pelList.listIterator();// 获取pelList对应的迭代器头结点
		while (pelIterator.hasNext()) 
		{
			Pel pel = pelIterator.next();
			
			//若是文本图元
			if(pel.text != null)
			{
				Text text = pel.text;
				savedCanvas.save();
				savedCanvas.translate(text.getTransDx(),text.getTransDy());
				savedCanvas.scale(text.getScale(),text.getScale(), text.getCenterPoint().x,text.getCenterPoint().y);
				savedCanvas.rotate(text.getDegree(),text.getCenterPoint().x,text.getCenterPoint().y);
				savedCanvas.drawText(text.getContent(),text.getBeginPoint().x,text.getBeginPoint().y, text.getPaint());
				savedCanvas.restore();
			}
			else if(pel.picture != null)
			{
				Picture picture = pel.picture;
				savedCanvas.save();
				savedCanvas.translate(picture.getTransDx(),picture.getTransDy());
				savedCanvas.scale(picture.getScale(),picture.getScale(), picture.getCenterPoint().x,picture.getCenterPoint().y);
				savedCanvas.rotate(picture.getDegree(),picture.getCenterPoint().x,picture.getCenterPoint().y);
				savedCanvas.drawBitmap(picture.createContent(),picture.getBeginPoint().x,picture.getBeginPoint().y, null);
				savedCanvas.restore();
			}
			else if(!pel.equals(selectedPel))//若非选中的图元
				savedCanvas.drawPath(pel.path, pel.paint);
		}
	}
		
	public void setCurPoint(PointF point)
	{
		curPoint.set(point);
	}
	
	public void setSecPoint(PointF point)
	{
		secPoint.set(point);
	}
	
	public static Step getStep() //返回当前步骤包含的操作
	{
		return step;
	}
}
