package com.noahark.graffiti.touch;

import android.graphics.Matrix;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Rect;

import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.step.Step;
import com.noahark.graffiti.step.TransformpelStep;

import java.util.ListIterator;

//变换触摸类
public class TransformTouch extends Touch {

	private static Matrix savedMatrix; //选中图元的最初因子
	private Matrix transMatrix; //变换因子（平移、缩放、旋转）
	private PointF downPoint; //按下，移动，两指中点
	public  static PointF centerPoint; //缩放、旋转中心
	private Pel savedPel; //重绘图元
	
	private static final float MIN_ZOOM=10; //缩放下限	
	private static final float MAX_DY=70; //缩放与旋转切换上限
	
	private static final int NONE = 0; // 平移操作
	private static final int DRAG = 1; // 平移操作
	private static final int ZOOM = 2; // 缩放操作
	private static final int ROTATE = 3; // 旋转操作
	private static int mode = NONE; // 当前操作类型
	
	private float oriDist; // 缩放时两指最初放上时的距离
	private float dx,dy; //平移偏移量
	
	private Step step=null;
	
	public TransformTouch()
	{
		super();
		
		savedMatrix=new Matrix();
		transMatrix=new Matrix();
		
		downPoint=new PointF();
		centerPoint=new PointF();
		
		savedPel=new Pel();
	}
	
	// 第一只手指按下
	@Override
	public void down1()
	{
		// 获取down事件的发生位置
		downPoint.set(curPoint);
	
		// 判断是否相交
		Pel minDisPel=null;
		float minHorizontalDis=Float.MAX_VALUE;
		float minVerticalDis=Float.MAX_VALUE;
		
		ListIterator<Pel> pelIterator = pelList.listIterator(); // 获取pelList对应的迭代器头结点
		while (pelIterator.hasNext())
		{
			Pel pel = pelIterator.next();
			Rect rect=(pel.region).getBounds();
					
			float leftDis=Math.abs(rect.left-downPoint.x);
			float rightDis=Math.abs(rect.right-downPoint.x);
			float horizontalDis=leftDis+rightDis;
			
			float topDis=Math.abs(rect.top-downPoint.y);
			float bottomDis=Math.abs(rect.bottom-downPoint.y);
			float verticalDis=topDis+bottomDis;
			
			if(horizontalDis < minHorizontalDis || verticalDis < minVerticalDis)
			{
				if(leftDis + rightDis < rect.width()+5)
				{
					if(topDis + bottomDis < rect.height()+5)
					{
						minDisPel=pel;
						minHorizontalDis=leftDis+rightDis;
						minVerticalDis=topDis+bottomDis;	
					}			
				}
			}
		}
				
		// 圆域扩展到最大是否有选中任何图元
		if (minDisPel != null)
		{
			// 敲定该图元
			CanvasView.setSelectedPel(selectedPel = minDisPel);
			
			//计算选中图元的中心点
			centerPoint.set(calPelCenterPoint(selectedPel));

			// 获取选中图元的初始matrix
			savedMatrix.set(calPelSavedMatrix(selectedPel));
			
			//由已知信息构造该步骤
			step=new TransformpelStep(selectedPel);//设置该步骤对应图元

			(savedPel.path).set(selectedPel.path); // 原始选中图元所在位置记忆到零时图元中去
			updateSavedBitmap();

			mode = DRAG;			
		}
		else //超过阈值未选中
		{
			CanvasView.setSelectedPel(selectedPel = null); //同步CanvasView中当前选中的图元
			updateSavedBitmap();
		}
	}
	
	// 第二只手指按下
	@Override
	public void down2()
	{		
		oriDist = distance();
		if (oriDist > MIN_ZOOM && selectedPel != null) 
		{
			// 距离小于50px才算是缩放
			takeOverSelectedPel();
			mode = ZOOM;
		}
	}
	
	// 手指移动
	@Override
	public void move()
	{
		// 获取move事件的发生位置
		if (selectedPel != null)// 前提是要选中了图元
		{			
			if (mode == DRAG)// 平移操作
			{				
				dx = curPoint.x - downPoint.x;//计算距离
				dy = curPoint.y - downPoint.y;					
				
				// 对选中图元施行平移变换
				transMatrix.set(savedMatrix);
				transMatrix.postTranslate(dx, dy); // 作用于平移变换因子
				
				(selectedPel.path).set(savedPel.path);
				(selectedPel.path).transform(transMatrix); // 作用于图元
				(selectedPel.region).setPath(selectedPel.path, clipRegion); // 更新平移后路径所在区域
			} 
			else if (mode == ZOOM) // 缩放操作
			{	
				float newDist = distance();					
				float dy=Math.abs(curPoint.y-secPoint.y);//两指的垂直间距				
				if(dy >= MAX_DY)//判断是否需要转变为旋转模式
				{
					//延续准备操作
					mode=ROTATE;
					takeOverSelectedPel();
					(savedPel.path).set(selectedPel.path);
					downPoint.set(curPoint);
				}
				else if (newDist > MIN_ZOOM) 
				{
					//<100仍然是正常缩放										
					float scale = newDist / oriDist;

					transMatrix.set(savedMatrix);
					transMatrix.postScale(scale, scale, centerPoint.x,centerPoint.y); // 作用于缩放变换因子

					(selectedPel.path).set(savedPel.path);
					(selectedPel.path).transform(transMatrix); // 作用于图元
					(selectedPel.region).setPath(selectedPel.path, clipRegion); // 更新平移后路径所在区域
				}
			} 
			else if (mode == ROTATE) // 旋转操作
			{			
				float dy=Math.abs(curPoint.y-secPoint.y);//两指的垂直间距					
				if(dy < MAX_DY)//判断是否需要转变为缩放模式
				{
					mode=ZOOM;
					takeOverSelectedPel();
					(savedPel.path).set(selectedPel.path);
					oriDist=distance();
				}
				else//>100仍然是正常旋转
				{							
					transMatrix.set(savedMatrix);
					transMatrix.setRotate(degree(),centerPoint.x,centerPoint.y);

					(selectedPel.path).set(savedPel.path);
					(selectedPel.path).transform(transMatrix); // 作用于图元
					(selectedPel.region).setPath(selectedPel.path, clipRegion); // 更新平移后路径所在区域
				}
			}
		}
	}
	
	// 手指抬起
	@Override
	public void up()
	{
		//为判断是否属于“选中（即秒抬）”情况
		float disx=Math.abs(curPoint.x-downPoint.x);
		float disy=Math.abs(curPoint.y-downPoint.y);
		
		if((disx > 2f || disy >2f) && step != null) //移动距离至少要满足大于2f
		{
			//敲定当前对应步骤
			savedMatrix.set(transMatrix);
			step.setToUndoMatrix(transMatrix);//设置进行该次步骤后的变换因子
			undoStack.push(step);//将该“步”压入undo栈
				
			// 敲定此次操作的最终区域 
			if (selectedPel != null)
				(savedPel.path).set(selectedPel.path); //初始位置也同步更新
		}

		mode = NONE;
	}
	
	/*
	 * 自定义函数
	 */
	
	// 计算两个触摸点之间的距离
	private float distance() {
		float x = curPoint.x - secPoint.x;
		float y = curPoint.y - secPoint.y;
		return (float)Math.sqrt(x * x + y * y);
	}

	// 旋转角度的计算
	private float degree() {
		// 获得两次down下时的距离
		float x=curPoint.x-downPoint.x;
		float y=curPoint.y-downPoint.y;
		
		float arc=(float)Math.sqrt(x * x + y * y);//弧长
		float radius=distance()/2;//半径

		float degrees=(arc/radius)*(180/3.14f);
		
		return degrees;
	}

	public static Matrix getSavedMatrix()
	{
		return savedMatrix;
	}
	
	public static PointF calPelCenterPoint(Pel selectedPel)
	{
		Rect boundRect=new Rect();
		selectedPel.region.getBounds(boundRect);

		return new PointF((boundRect.right+boundRect.left)/2,(boundRect.bottom+boundRect.top)/2);
	}
	
	public static Matrix calPelSavedMatrix(Pel selectedPel)
	{
		Matrix savedMatrix = new Matrix();
		PathMeasure pathMeasure = new PathMeasure(
				selectedPel.path, true);// 将Path封装成PathMeasure，方便获取path内的matrix用
		pathMeasure.getMatrix(pathMeasure.getLength(),
				savedMatrix, PathMeasure.POSITION_MATRIX_FLAG
						& PathMeasure.TANGENT_MATRIX_FLAG);
		
		return savedMatrix;
	}	
	
	public void takeOverSelectedPel() //接手变换到一般要进行其它不同变换操作的图元（如平移到某处后马上又缩放，如缩放到某处后马上又旋转）
	{	
		savedMatrix.set(transMatrix);//起始变换因子为刚才的变换后因子
		centerPoint.set(calPelCenterPoint(selectedPel)); //重新计算图元中心点
	}
}