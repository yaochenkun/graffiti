package com.noahark.graffiti.touch;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.step.CrossfillStep;
import com.noahark.graffiti.step.Step;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;

public class CrossfillTouch extends Touch {
	private Point originPoint;
	private int fillColor;// 当前填充色
	private int oldColor;// 虚拟初始颜色
	private int initColor; // 实际初始颜色
	private int curColor;
	private int[] pixels;
	private Stack<Point> pointStack;// 源粒子栈
	private Bitmap whiteBitmap;// 白色底的填充信息副本
	private Bitmap backgroundBitmap;// 在填色时要被同时改变的背景图
	private Canvas backgroundCanvas;
	private Bitmap copyOfBackgroundBitmap;

	private int MAX_WIDTH;
	private int MAX_HEIGHT;
	private CanvasView canvasVi;
	/***********************************************************/
	private Paint fillPaint; // 填充画笔
	private int width, height; // 位图宽高
	private List<ScanLine> scanLinesList; // 扫描线链表
	private ProgressDialog progressDialog;
	private Thread fillThread; // 填充线程
	final Handler handler = new Handler() // 线程消息处理者
	{
		public void handleMessage(Message msg) {
			try {
				progressDialog.dismiss();
				undoStack.push(new CrossfillStep(null, initColor, fillColor,
						scanLinesList));// 将该“步”压入undo栈
				updateSavedBitmap();// 重绘位图
				canvasVi.invalidate();

				super.handleMessage(msg);
			} catch (Exception e) {
			}
		}
	};

	public CrossfillTouch() {
		super();
		originPoint = new Point();
		pointStack = new Stack<Point>();// 像素堆栈
		canvasVi = MainActivity.getCanvasView();
		backgroundCanvas = new Canvas();
		fillPaint = new Paint();
		fillPaint.setStrokeWidth(1);
	}

	public Bitmap createWhiteBitmap() {
		// 创建缓冲位图
		Bitmap bitmap = Bitmap.createBitmap(MAX_WIDTH, MAX_HEIGHT,
				Config.ARGB_8888);
		savedCanvas.setBitmap(bitmap);
		reprintFilledAreas(bitmap);

		ListIterator<Pel> pelIterator = pelList.listIterator();// 获取pelList对应的迭代器头结点
		while (pelIterator.hasNext()) {
			Pel pel = pelIterator.next();
			if (!pel.equals(selectedPel))
				savedCanvas.drawPath(pel.path, pel.paint);
		}

		return bitmap;
	}

	@Override
	public void down1() {
		/**
		 * 扫描线粒子填充算法
		 */

		// 落下点没有超出画布
		MAX_WIDTH = CanvasView.CANVAS_WIDTH;
		MAX_HEIGHT = CanvasView.CANVAS_HEIGHT;
		if (curPoint.x < MAX_WIDTH && curPoint.x > 0 && curPoint.y < MAX_HEIGHT
				&& curPoint.y > 0) {
			// 进度对话框处理填充耗时任务
			progressDialog = new ProgressDialog(
					com.noahark.graffiti.ui.activity.MainActivity.getContext());
			progressDialog.setMessage("正在填色，请稍等...");
			progressDialog.show();

			// 线性填充线链表（链表元素为填充直线的起始坐标点）
			scanLinesList = new LinkedList<ScanLine>();

			fillThread = new Thread(new FillRunnable());
			fillThread.start();
		}
	}

	// 填充操作线程的实现类
	class FillRunnable implements Runnable {
		public void run() {
			// 扫描线种子填充
			fill();
			handler.sendEmptyMessage(0);
		}
	}

	/**************************************************************************/
	public void fill() {
		/**
		 * 处理耗时操作
		 */
		whiteBitmap = createWhiteBitmap();// 将当前有非白色背景的缓冲位图转换成白色背景的
		backgroundBitmap = CanvasView.getBackgroundBitmap();// 获取当前背景图片
		copyOfBackgroundBitmap = CanvasView.getCopyOfBackgroundBitmap();
		backgroundCanvas.setBitmap(backgroundBitmap);
		fillColor = DrawTouch.getCurPaint().getColor();// 获得填充颜色
		oldColor = whiteBitmap.getPixel((int) curPoint.x, (int) curPoint.y);// 该点虚拟初始颜色
		initColor = backgroundBitmap.getPixel((int) curPoint.x,
				(int) curPoint.y);
		if (initColor == copyOfBackgroundBitmap.getPixel((int) curPoint.x,
				(int) curPoint.y))
			initColor = Color.TRANSPARENT;

		// 算法初始化
		pointStack.clear();// 清空源粒子栈
		originPoint.set((int) curPoint.x, (int) curPoint.y);// 以当前down下坐标作为初始源粒子
		pointStack.push(originPoint);// 入栈

		// 设置填充画笔颜色
		fillPaint.setColor(fillColor);

		width = whiteBitmap.getWidth();
		height = whiteBitmap.getHeight();
		pixels = new int[width * height];
		whiteBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		Point tmp;
		int x, y, XLeft, XRight, index;
		while (!pointStack.isEmpty()) {
			tmp = pointStack.pop();
			x = tmp.x;
			y = tmp.y;
			XLeft = x;
			XRight = x;

			while (x > 0
					&& (curColor = pixels[index = width * y + x]) == oldColor
					&& curColor != fillColor) {
				whiteBitmap.setPixel(x, y, fillColor);
				pixels[index] = fillColor;
				x--;
			}
			XLeft = x + 1;

			x = tmp.x + 1;
			while (x < width
					&& (curColor = pixels[index = width * y + x]) == oldColor
					&& curColor != fillColor) {
				whiteBitmap.setPixel(x, y, fillColor);
				pixels[index] = fillColor;
				x++;
			}
			XRight = x - 1;

			backgroundCanvas.drawLine(XLeft - 1, y, XRight + 2, y, fillPaint);
			
			ScanLine scanLine = new ScanLine();
			(scanLine.from).set(XLeft - 1, y);
			(scanLine.to).set(XRight + 2, y);
			scanLinesList.add(scanLine);

			if (y > 0) {
				findNewSeedInline(XLeft, XRight, y - 1, fillPaint);
			}
			if (y + 1 < height) {
				findNewSeedInline(XLeft, XRight, y + 1, fillPaint);
			}
		}
	}

	public void findNewSeedInline(int XLeft, int XRight, int y, Paint paint) {
		Point p;
		Boolean pflag;
		int x = XLeft + 1;
		while (x <= XRight) {
			pflag = false;

			while ((curColor = pixels[width * y + x]) == oldColor && x < XRight
					&& curColor != fillColor) {
				if (pflag == false) {
					pflag = true;
				}

				x++;
			}
			if (pflag == true) {
				if ((x == XRight)
						&& (curColor = pixels[width * y + x]) == oldColor
						&& curColor != fillColor) {
					p = new Point(x, y);
					pointStack.push(p);
				} else {
					p = new Point(x - 1, y);
					pointStack.push(p);
				}
				pflag = false;
			}

			// 处理向右跳过内部的无效点（处理区间右端有障碍点的情况）
			int xenter = x;
			while (pixels[width * y + x] != oldColor) {
				if (x >= XRight || x >= width) {
					break;
				}

				x++;
			}
			if (xenter == x) {
				x++;
			}
		}
	}

	public class ScanLine // 扫描线类
	{
		public Point from; // 起始点
		public Point to; // 终止点

		ScanLine() {
			from = new Point();
			to = new Point();
		}
	}

	// 填充区域重新打印
	public static void reprintFilledAreas(Bitmap bitmap) {
		for (Step step : undoStack) {
			// 若为填充步骤
			if (step.getClass().getSimpleName().equals("CrossfillStep")) {
				((CrossfillStep) step).fillInWhiteBitmap(bitmap);
			}
		}
	}
}
