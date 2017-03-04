package com.noahark.graffiti.step;

import java.util.List;
import java.util.ListIterator;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;

import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.touch.CrossfillTouch.ScanLine;

public class CrossfillStep extends Step 
{	
	private int   initColor; //初始色（若为白色，则undo的时候恢复成背景色；若为非白色，则undo的时候恢复成该色）
	private int   fillColor; //填充色
	private List<ScanLine> scanLinesList; //扫描线链表
	
	//进度对话框必要组件
	private ProgressDialog progressDialog;
	private Thread fillThread;  //填充线程
	final Handler handler=new Handler() //线程消息处理者
	{
		public void handleMessage(Message msg)
		{		
			progressDialog.dismiss();
			canvasVi.updateSavedBitmap();//重绘位图
			
		    super.handleMessage(msg);
		} 
	};
	
	public CrossfillStep(Pel pel,int initC,int fillC,List<ScanLine> scanLL) 
	{
		super(pel);
		initColor=initC;
		fillColor=fillC;
		scanLinesList=scanLL;
	}
	
	@Override
	public void toUndoUpdate() //重做操作（子类覆写）
	{
		//进度对话框处理填充耗时任务
		progressDialog=new ProgressDialog(com.noahark.graffiti.ui.activity.MainActivity.getContext());
		progressDialog.setMessage("正在重做，请稍等...");
		progressDialog.show();
		
		fillThread=new Thread(new toUndoFillRunnable());
		fillThread.start();
	}
	
	@Override
	public void toRedoUpdate()//撤销操作（子类覆写）
	{			
		//进度对话框处理填充耗时任务
		progressDialog=new ProgressDialog(com.noahark.graffiti.ui.activity.MainActivity.getContext());
		progressDialog.setMessage("正在撤销，请稍等...");
		progressDialog.show();
		
		fillThread=new Thread(new toRedoFillRunnable());
		fillThread.start();
	}
	
	/***********************************************************************/
	/**
	 * 线程相关
	 */
	//重做填充线程
	class toUndoFillRunnable implements Runnable
	{
		public void run() 
		{	
			/**
			 * 处理耗时操作
			 */
			Bitmap backgroundBitmap=CanvasView.getBackgroundBitmap();//获取当前背景图片
			
			//设置重做填充颜色
			Canvas backgroundCanvas=new Canvas();//构造画布
			backgroundCanvas.setBitmap(backgroundBitmap);
			Paint paint=new Paint();
			paint.setColor(fillColor);
			
			ListIterator<ScanLine> scanlineIterator = scanLinesList.listIterator();// 获取pelList对应的迭代器头结点
			while (scanlineIterator.hasNext()) 
			{
				ScanLine scanLine = scanlineIterator.next();
				backgroundCanvas.drawLine(scanLine.from.x, scanLine.from.y, scanLine.to.x, scanLine.to.y, paint);
			}
			
			handler.sendEmptyMessage(0); 
		}	
	}
	
	//撤销填充线程
	class toRedoFillRunnable implements Runnable
	{
		public void run() 
		{	
			/**
			 * 处理耗时操作
			 */
			//扫描线种子填充
			Bitmap backgroundBitmap=CanvasView.getBackgroundBitmap();//获取当前背景图片
			
			//设置填充还原色
			ListIterator<ScanLine> scanlineIterator = scanLinesList.listIterator();// 获取pelList对应的迭代器头结点
			if(initColor == Color.TRANSPARENT) //背景色填充
			{
				Bitmap copyOfBackgroundBitmap=CanvasView.getCopyOfBackgroundBitmap();//获取当前背景图片
				while (scanlineIterator.hasNext()) 
				{
					ScanLine scanLine = scanlineIterator.next();
					
					for(int x=scanLine.from.x,y=scanLine.from.y;x < scanLine.to.x;x++)
					{
						if(x < copyOfBackgroundBitmap.getWidth())
							backgroundBitmap.setPixel(x, y, copyOfBackgroundBitmap.getPixel(x, y));
					}
				}
			}
			else //用上一次的颜色填充
			{
				Canvas backgroundCanvas=new Canvas();//构造画布
				backgroundCanvas.setBitmap(backgroundBitmap);
				Paint paint=new Paint();
				paint.setColor(initColor);
				while (scanlineIterator.hasNext()) 
				{
					ScanLine scanLine = scanlineIterator.next();
					backgroundCanvas.drawLine(scanLine.from.x, scanLine.from.y, scanLine.to.x, scanLine.to.y, paint);
				}
			}
			
			handler.sendEmptyMessage(0); 
		}	
	}
	
	//填充到白底位图上
	public void fillInWhiteBitmap(Bitmap bitmap)
	{		
		//设置重做填充颜色
		Canvas whiteCanvas=new Canvas();//构造画布
		whiteCanvas.setBitmap(bitmap);
		Paint paint=new Paint();
		paint.setColor(fillColor);
		
		ListIterator<ScanLine> scanlineIterator = scanLinesList.listIterator();// 获取pelList对应的迭代器头结点
		while (scanlineIterator.hasNext()) 
		{
			ScanLine scanLine = scanlineIterator.next();
			whiteCanvas.drawLine(scanLine.from.x, scanLine.from.y, scanLine.to.x, scanLine.to.y, paint);
		}
	}
}
