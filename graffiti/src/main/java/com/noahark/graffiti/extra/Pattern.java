package com.noahark.graffiti.extra;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;

import com.noahark.graffiti.ui.view.CanvasView;

public class Pattern
{
	PointF beginPoint;//起始随机点
	float width=CanvasView.CANVAS_WIDTH;
	float height=CanvasView.CANVAS_HEIGHT;
	
	//产生随机初始点
	public void randBeginPoint()
	{
		float offset=(float)(100*Math.random()-50); //随机偏移距离
		beginPoint=new PointF(width/4+offset,height/4+offset);//随机化左上起点
	}
	
	//计算路径的初始变换因子
	public Matrix calPathSavedMatrix(Path path)
	{
		Matrix savedMatrix = new Matrix();
		PathMeasure pathMeasure = new PathMeasure(
				path, true);// 将Path封装成PathMeasure，方便获取path内的matrix用
		pathMeasure.getMatrix(pathMeasure.getLength(),
				savedMatrix, PathMeasure.POSITION_MATRIX_FLAG
						& PathMeasure.TANGENT_MATRIX_FLAG);
		
		return savedMatrix;
	}
	
	//人
	public Pel drawMan()
	{
		randBeginPoint();
		Pel newPel=new Pel();
		
		(newPel.path).addOval(new RectF(beginPoint.x,beginPoint.y,beginPoint.x+50,beginPoint.y+50), Path.Direction.CCW);		
		(newPel.path).moveTo(beginPoint.x+25, beginPoint.y+50);
		(newPel.path).lineTo(beginPoint.x+25, beginPoint.y+100);
		(newPel.path).lineTo(beginPoint.x+26, beginPoint.y+100);
		(newPel.path).lineTo(beginPoint.x-4, beginPoint.y+130);
		(newPel.path).moveTo(beginPoint.x+26, beginPoint.y+100);
		(newPel.path).lineTo(beginPoint.x+56, beginPoint.y+130);
		(newPel.path).moveTo(beginPoint.x-5 , beginPoint.y+80);
		(newPel.path).lineTo(beginPoint.x+55, beginPoint.y+80);	
		
		return newPel;
	}

	//花朵
	public Pel drawFlower()
	{
		randBeginPoint();
		Pel newPel=new Pel();
		
		(newPel.path).addOval(new RectF(beginPoint.x,beginPoint.y,beginPoint.x+50,beginPoint.y+50), Path.Direction.CCW);
		Path arcPath=new Path(); 
		arcPath.addArc(new RectF(beginPoint.x,beginPoint.y+50,beginPoint.x+50,beginPoint.y+100), 0, 180);
		(newPel.path).addPath(arcPath);
			
		Matrix savedMatrix = calPathSavedMatrix(arcPath);
		Matrix transMatrix = new Matrix();
				
		for(int n=1;n<=7;n++)
		{
			transMatrix.set(savedMatrix);
			transMatrix.postRotate(45*n, beginPoint.x+25, beginPoint.y+25);
			Path path=new Path(arcPath);
			path.transform(transMatrix);// 作用于图元
			(newPel.path).addPath(path);
		}
		
		return newPel;
	}
	
	//太阳
	public Pel drawSun()
	{
		randBeginPoint();
		Pel newPel=new Pel();
		
		(newPel.path).addOval(new RectF(beginPoint.x,beginPoint.y,beginPoint.x+50,beginPoint.y+50), Path.Direction.CCW);
		Path linePath=new Path();
		linePath.moveTo(beginPoint.x+25,beginPoint.y+60);
		linePath.lineTo(beginPoint.x+25,beginPoint.y+80);
		(newPel.path).addPath(linePath);
		
		
		Matrix savedMatrix = calPathSavedMatrix(linePath);
		Matrix transMatrix = new Matrix();
				
		for(int n=1;n<=7;n++)
		{
			transMatrix.set(savedMatrix);
			transMatrix.postRotate(45*n, beginPoint.x+25, beginPoint.y+25);
			Path path=new Path(linePath);
			path.transform(transMatrix);// 作用于图元
			(newPel.path).addPath(path);
		}
		
		return newPel;
	}
	
	//房子
	public Pel drawHouse()
	{
		randBeginPoint();
		Pel newPel=new Pel();
		
		(newPel.path).addRect(new RectF(beginPoint.x,beginPoint.y,beginPoint.x+120,beginPoint.y+70), Path.Direction.CCW);
		(newPel.path).moveTo(beginPoint.x-20,beginPoint.y);
		(newPel.path).lineTo(beginPoint.x+140,beginPoint.y);
		(newPel.path).lineTo(beginPoint.x+60,beginPoint.y-50);
		(newPel.path).lineTo(beginPoint.x-20,beginPoint.y);
		
		return newPel;
	}
	
	//小草
	public Pel drawGrass()
	{
		randBeginPoint();
		Pel newPel=new Pel();
		
		(newPel.path).moveTo(beginPoint.x,beginPoint.y);
		(newPel.path).lineTo(beginPoint.x-35,beginPoint.y-60);
		(newPel.path).lineTo(beginPoint.x-10,beginPoint.y-30);
		(newPel.path).lineTo(beginPoint.x,beginPoint.y-100);
		(newPel.path).lineTo(beginPoint.x+10,beginPoint.y-30);
		(newPel.path).lineTo(beginPoint.x+35,beginPoint.y-60);
		(newPel.path).lineTo(beginPoint.x,beginPoint.y);
		
		return newPel;
	}
	
	//笔
	public Pel drawPen()
	{
		randBeginPoint();
		Pel newPel=new Pel();
		
		(newPel.path).addRect(new RectF(beginPoint.x,beginPoint.y,beginPoint.x+120,beginPoint.y+70), Path.Direction.CCW);
		(newPel.path).moveTo(beginPoint.x-20,beginPoint.y);
		(newPel.path).lineTo(beginPoint.x+140,beginPoint.y);
		(newPel.path).lineTo(beginPoint.x+60,beginPoint.y-50);
		(newPel.path).lineTo(beginPoint.x-20,beginPoint.y);
		
		return newPel;
	}
	
	//笑脸
	public Pel drawSmileFace()
	{
		randBeginPoint();
		Pel newPel=new Pel();
		
		(newPel.path).addOval(new RectF(beginPoint.x,beginPoint.y,beginPoint.x+90,beginPoint.y+90), Path.Direction.CCW);
		(newPel.path).addArc(new RectF(beginPoint.x+30,beginPoint.y+40,beginPoint.x+60,beginPoint.y+70), 0, 180);
		
		Path eyesPath=new Path();
		eyesPath.addArc(new RectF(beginPoint.x+30,beginPoint.y+40,beginPoint.x+50,beginPoint.y+60), 0, 180);
		
		
		Matrix savedMatrix = calPathSavedMatrix(eyesPath);
		Matrix transMatrix = new Matrix();
			
		transMatrix.set(savedMatrix);
		transMatrix.postRotate(180,beginPoint.x+45,beginPoint.y+45);
		Path eye1=new Path(eyesPath);
		eye1.transform(transMatrix);// 作用于图元
		eye1.offset(-25, 0);
		(newPel.path).addPath(eye1);
		Path eye2=new Path(eye1);
		eye2.offset(40, 0);
		(newPel.path).addPath(eye2);
		
		return newPel;
	}
	
	//环
	public Pel drawRing()
	{
		randBeginPoint();
		Pel newPel=new Pel();
		
		(newPel.path).addOval(new RectF(beginPoint.x,beginPoint.y,beginPoint.x+90,beginPoint.y+90), Path.Direction.CCW);
		(newPel.path).addOval(new RectF(beginPoint.x+30,beginPoint.y+30,beginPoint.x+60,beginPoint.y+60), Path.Direction.CCW);
	
		return newPel;
	}
	
	
}
