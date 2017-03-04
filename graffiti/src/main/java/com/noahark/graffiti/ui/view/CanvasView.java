package com.noahark.graffiti.ui.view;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Stack;

import com.noahark.graffiti.snow.Snow;
import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.extra.Picture;
import com.noahark.graffiti.extra.Text;
import com.noahark.graffiti.step.Step;
import com.noahark.graffiti.touch.CrossfillTouch;
import com.noahark.graffiti.touch.DrawFreehandTouch;
import com.noahark.graffiti.touch.DrawTouch;
import com.noahark.graffiti.touch.Touch;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class CanvasView extends View 
{
	// 图元平移缩放

	private float phase;// 动画画笔（变换相位用）
	public static Paint animPelPaint;// 动画效果画笔
	public static Paint drawPelPaint;// 画画用的画笔
	private Paint drawTextPaint;
	private Paint drawPicturePaint;

	public static int CANVAS_WIDTH;//画布宽
	public static int CANVAS_HEIGHT;//画布高

	public static Stack<Step> undoStack;//undo栈
	public static Stack<Step> redoStack;//redo栈	
	
	public static List<Pel> pelList;// 图元链表
	public static Region clipRegion; // 画布裁剪区域
	public static Pel selectedPel = null; // 当前被选中的图元
	public static Bitmap savedBitmap; // 重绘位图
	private Canvas savedCanvas; //重绘画布
	public static Bitmap backgroundBitmap;
	public static Bitmap copyOfBackgroundBitmap;//原图片副本，清空或还原时用
	public static Bitmap originalBackgroundBitmap;

	public static Touch touch;//触摸操作
	/*******************************************************************************/	
	//雪花飞舞
	
	int MAX_SNOW_COUNT = 50;
	// 雪花图片
	Bitmap bitmap_snows = null;
	// 画笔
	private final Paint mPaint = new Paint();
	// 随即生成器
	private static final Random random = new Random();
	// 雪花的位置
	private Snow[] snows = new Snow[MAX_SNOW_COUNT];
	// 屏幕的高度和宽度
	int view_height = 0;
	int view_width = 0;
	int MAX_SPEED = 25;
	boolean draw = false;
	private int gravity = 1  ;
	/*******************************************************************************/		
	Canvas cacheCanvas;
	public CanvasView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
	
		//初始化画布宽高为屏幕宽高
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		CANVAS_WIDTH = wm.getDefaultDisplay().getWidth();
		CANVAS_HEIGHT = wm.getDefaultDisplay().getHeight();

		undoStack=new Stack<Step>();//初始化undo redo栈
		redoStack=new Stack<Step>();
		pelList = new LinkedList<Pel>(); // 图元总链表
		savedCanvas = new Canvas();
		
		clipRegion = new Region(); //获取画布裁剪区域
		touch=new DrawFreehandTouch();//初始化为自由手绘操作
		drawPelPaint=DrawFreehandTouch.getCurPaint();
		animPelPaint=new Paint(drawPelPaint);
		drawTextPaint=new Paint();
		drawTextPaint.setColor(DrawTouch.getCurPaint().getColor());
		drawTextPaint.setTextSize(50);
		drawPicturePaint=new Paint();
		
		initBitmap();
		updateSavedBitmap();
	}
	
	public void initBitmap()
	{
		clipRegion.set(new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT));
		BitmapDrawable backgroundDrawable=(BitmapDrawable)this.getResources().getDrawable(R.drawable.bg_canvas0);
		Bitmap scaledBitmap=Bitmap.createScaledBitmap(backgroundDrawable.getBitmap(),CANVAS_WIDTH, CANVAS_HEIGHT,true);
		
		ensureBitmapRecycled(backgroundBitmap);
		backgroundBitmap=scaledBitmap.copy(Config.ARGB_8888, true);
		ensureBitmapRecycled(scaledBitmap);
		
		ensureBitmapRecycled(copyOfBackgroundBitmap);
		copyOfBackgroundBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);
		
		ensureBitmapRecycled(originalBackgroundBitmap);
		originalBackgroundBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);
		
		cacheCanvas=new Canvas();
		cacheCanvas.setBitmap(backgroundBitmap);
	}
	//触摸事件
	public boolean onTouchEvent(MotionEvent event) 
	{
		if(MainActivity.getSensorMode() == MainActivity.NOSENSOR
		&& MainActivity.curFragmentFlag == MainActivity.MAIN_FRAGMENT
		&& !MainActivity.drawerLayout.isDrawerOpen(Gravity.LEFT)) //非传感器模式才响应屏幕
		{
			//第一只手指坐标
			touch.setCurPoint(new PointF(event.getX(0),event.getY(0)));
			
			//第二只手指坐标（可能在第二只手指还没按下时发生异常）
			try{touch.setSecPoint(new PointF(event.getX(1),event.getY(1)));}
			catch(Exception e){touch.setSecPoint(new PointF(1,1));}
			
			switch (event.getAction() & MotionEvent.ACTION_MASK) 
			{
				case MotionEvent.ACTION_DOWN:// 第一只手指按下
					{
						if(MainActivity.topToolbarSclVi.getVisibility() == View.VISIBLE)
						{
							MainActivity.closeTools();
							touch.dis=Float.MAX_VALUE;
						}
							
						touch.down1();
					}
					break;
				case MotionEvent.ACTION_POINTER_DOWN:// 第二个手指按下
					touch.down2();
					break;
				case MotionEvent.ACTION_MOVE:
					touch.move();
					break;
				case MotionEvent.ACTION_UP:// 第一只手指抬起
				case MotionEvent.ACTION_POINTER_UP://第二只手抬起
					touch.up();
					break;
			}
			invalidate();
		}
		
		return true;
	}

	//重绘
	protected void onDraw(Canvas canvas) 
	{
		canvas.drawBitmap(savedBitmap, 0, 0, new Paint());// 画其余图元
		if (selectedPel != null) 
		{
			if(touch.getClass().getSimpleName().equals("TransformTouch")) //选中状态才产生动态画笔效果
			{
				setAnimPaint();
				canvas.drawPath(selectedPel.path, animPelPaint);
				invalidate();// 画笔动画效果
			}
			else //画图状态不产生动态画笔效果
			{
				canvas.drawPath(selectedPel.path, drawPelPaint);
			}
		}
//		else
//		{
//			Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.brush); 
//			
//			
//			canvas.setBitmap(savedBitmap);
//			canvas.drawBitmap(bitmap, touch.curPoint.x, touch.curPoint.y, DrawTouch.getCurPaint());
//		}
		
		/**********************************************************************/
		//雪花飞舞
		if(draw)
		{
			int outOfBoundCount = 0;
			for (int i = 0; i < MAX_SNOW_COUNT; i += 1) 
			{
				//判断是否还在显示区内
				if (snows[i].coordinate.x > view_width || snows[i].coordinate.y > view_height) 
				{
					outOfBoundCount++;
					//如果所有的雪花都不在显示区内了，则下次就不需要再绘制雪花了
					if(outOfBoundCount >= MAX_SNOW_COUNT)
					{
						setStatus(false);
					}
					continue;
				}
				//为雪花加上重力。
				snows[i].speed +=gravity;
				// 雪花下落的速度
				snows[i].coordinate.y += snows[i].speed;
				//雪花飘动的效果
	
				canvas.drawBitmap(bitmap_snows, ((float) snows[i].coordinate.x),
						((float) snows[i].coordinate.y), mPaint);
			}
		}
		/**********************************************************************/
	}

	/*
	 * 自定义成员函数
	 */
	public void updateSavedBitmap() //更新重绘背景位图用（当且仅当选择的图元有变化的时候才调用）
	{
		//创建缓冲位图
		ensureBitmapRecycled(savedBitmap);
		savedBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);//由画布背景创建缓冲位图
		savedCanvas.setBitmap(savedBitmap);

		//画除selectedPel外的所有图元
		drawPels();
		
		invalidate();
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
				savedCanvas.drawBitmap(picture.createContent(),picture.getBeginPoint().x,picture.getBeginPoint().y, drawPicturePaint);
				savedCanvas.restore();
			}
			else if(!pel.equals(selectedPel))//若非选中的图元
				savedCanvas.drawPath(pel.path, pel.paint);
		}
	}
	
	// 动画画笔更新
	private void setAnimPaint() 
	{
		phase++; // 变相位

		Path p = new Path();
		p.addRect(new RectF(0, 0, 6, 3), Path.Direction.CCW); // 路径单元是矩形（也可以为椭圆）
		PathDashPathEffect effect = new PathDashPathEffect(p, 12, phase, // 设置路径效果
				PathDashPathEffect.Style.ROTATE);
		animPelPaint.setColor(Color.BLACK);
		animPelPaint.setPathEffect(effect);
	}
	
	/**
	 * get()方法:获取CanvasView下指定成员
	 */
	public static int getCanvasWidth()
	{
		return CANVAS_WIDTH;
	}
	
	public static int getCanvasHeight()
	{
		return CANVAS_HEIGHT;
	}	

	public static Region getClipRegion()
	{
		return clipRegion;
	}
	
	public static List<Pel> getPelList()
	{
		return pelList;
	}
	
	public static Pel getSelectedPel()
	{
		return selectedPel;
	}	
	
	public static Bitmap getSavedBitmap()
	{
		return savedBitmap;
	}

	public static Bitmap getBackgroundBitmap()
	{
		return backgroundBitmap;
	}
	
	public static Bitmap getCopyOfBackgroundBitmap()
	{
		return copyOfBackgroundBitmap;
	}
	
	public static Bitmap getOriginalBackgroundBitmap()
	{
		return originalBackgroundBitmap;
	}
	
	public static Touch getTouch()
	{
		return touch;
	}
	
	public static Stack<Step> getUndoStack()
	{
		return undoStack;
	}	

	public static Stack<Step> getRedoStack()
	{
		return redoStack;
	}	
	/*
	 * set()方法:设置CanvasView下指定成员
	 */	
	public static void setSelectedPel(Pel pel)
	{
		selectedPel=pel;
	}	
	
	public static void setSavedBitmap(Bitmap bitmap)
	{
		savedBitmap=bitmap;
	}
	
	public void setBackgroundBitmap(int id) //以已提供选择的背景图片换画布
	{
		BitmapDrawable backgroundDrawable=(BitmapDrawable)this.getResources().getDrawable(id);
		Bitmap offeredBitmap=backgroundDrawable.getBitmap();
		
		ensureBitmapRecycled(backgroundBitmap);
		backgroundBitmap=Bitmap.createScaledBitmap(offeredBitmap,CANVAS_WIDTH, CANVAS_HEIGHT,true);
		
		
		
		ensureBitmapRecycled(copyOfBackgroundBitmap);
		copyOfBackgroundBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);
		
		ensureBitmapRecycled(originalBackgroundBitmap);
		originalBackgroundBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);
		
		CrossfillTouch.reprintFilledAreas(backgroundBitmap);//填充区域重新打印
		updateSavedBitmap();
	}	
	
	public void setBackgroundBitmap(Bitmap photo)//以图库或拍照得到的背景图片换画布
	{
		ensureBitmapRecycled(backgroundBitmap);
		backgroundBitmap=Bitmap.createScaledBitmap(photo,CANVAS_WIDTH,CANVAS_HEIGHT,true);
		
		ensureBitmapRecycled(copyOfBackgroundBitmap);
		copyOfBackgroundBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);
		
		ensureBitmapRecycled(originalBackgroundBitmap);
		originalBackgroundBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);
		
		CrossfillTouch.reprintFilledAreas(backgroundBitmap);//填充区域重新打印
		updateSavedBitmap();
	}
	
	public void setProcessedBitmap(Bitmap imgPro)//设置处理后的图片作为背景
	{
		ensureBitmapRecycled(backgroundBitmap);
		backgroundBitmap=Bitmap.createScaledBitmap(imgPro,CANVAS_WIDTH,CANVAS_HEIGHT,true);
		
		ensureBitmapRecycled(copyOfBackgroundBitmap);
		copyOfBackgroundBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);
		
		CrossfillTouch.reprintFilledAreas(backgroundBitmap);//填充区域重新打印
		updateSavedBitmap();
	}
	
	public void setBackgroundBitmap() //清空画布时将之前保存的副本背景作为重绘（去掉填充）
	{
		ensureBitmapRecycled(backgroundBitmap);
		backgroundBitmap=copyOfBackgroundBitmap.copy(Config.ARGB_8888, true);
		
		CrossfillTouch.reprintFilledAreas(backgroundBitmap);//填充区域重新打印
		updateSavedBitmap();
	}
	
	public static void setTouch(Touch childTouch)
	{
		touch=childTouch;
	}
	
	public static void setCanvasSize(int width,int height)
	{
		CANVAS_WIDTH=width;
		CANVAS_HEIGHT=height;
	}
	
	public static void ensureBitmapRecycled(Bitmap bitmap) //确保传入位图已经回收
	{
		if(bitmap != null && !bitmap.isRecycled())	
			bitmap.recycle();
	}
/*******************************************************************************/	
	/**
	 * 雪花飞舞
	 */
	/**
	 * 加载天女散花的花图片到内存中
	 * 
	 */
	public void LoadSnowImage() {
		Resources r = this.getContext().getResources();
		bitmap_snows = ((BitmapDrawable) r.getDrawable(R.drawable.snow))
				.getBitmap();
	}

	/**
	 * 设置当前窗体的实际高度和宽度
	 * 
	 */
	public void SetView(int height, int width) {
		view_height = height;
		view_width = width;

	}
	
	public void setStatus(boolean draw)
	{
		this.draw = draw;
	}
	
	public boolean getStatus()
	{
		return this.draw;
	}

	/**
	 * 随机的生成花朵的位置
	 * 
	 */
	public void addRandomSnow() {
		for(int i =0; i< MAX_SNOW_COUNT;i++){
			snows[i] = new Snow(random.nextInt(view_width), view_height,-(random.nextInt(MAX_SPEED)));
		}
	}
}