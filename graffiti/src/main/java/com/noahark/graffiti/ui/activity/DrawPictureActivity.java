 package com.noahark.graffiti.ui.activity;

import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.dialog.PictureDialog;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.ui.view.DrawPictureView;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.extra.Picture;
import com.noahark.graffiti.extra.Text;
import com.noahark.graffiti.step.DrawpelStep;
import com.noahark.graffiti.step.Step;
import com.noahark.graffiti.touch.DrawPictureTouch;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

public class DrawPictureActivity extends Activity
{
	protected static List<Pel> pelList=CanvasView.getPelList(); // 图元链表// 屏幕宽高
	protected static Stack<Step> undoStack=CanvasView.getUndoStack();//获取undo
	protected Pel selectedPel; // 当前选中图元
	protected Bitmap savedBitmap; // 当前重绘位图	
	protected Canvas savedCanvas; //重绘画布
	private static DrawPictureView drawPictureVi=null;
	private static Context context=null;
	public static View topToolbar;
	private static View downToolbar;
	private PictureDialog pictureDialog;//调色板对话框
	public static View[] allBtns;
	
	protected void onCreate(Bundle savedInstanceState) 
	{			
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	
	//初始化组件
	public void initView()
	{
		setContentView(R.layout.activity_drawpicture);
		drawPictureVi=(DrawPictureView)findViewById(R.id.drawpicture_canvas);
		pictureDialog=new PictureDialog(DrawPictureActivity.this,R.style.GraffitiDialog);
		topToolbar=(View)findViewById(R.id.drawpicture_toptoolbar);
		downToolbar=(View)findViewById(R.id.drawpicture_downtoolbar);
		
		int[] btnIds=new int[]{R.id.drawpicture_refuse,R.id.drawpicture_sure,R.id.drawpicture_select};
		allBtns=new View[btnIds.length];
		for(int i=0;i<btnIds.length;i++)
			allBtns[i]=(View)findViewById(btnIds[i]);
	}
	
	//初始化数据
	public void initData()
	{
		savedCanvas=new Canvas();
		context=DrawPictureActivity.this;
	}
	
	//返回
	public void onDrawPictureBackBtn(View v)
	{
		finish();
	}
	
	//确定
	public void onDrawPictureOkBtn(View v)
	{
		//插入了图
		if(drawPictureVi.getContent() != null)
		{
			//构造该次的文本对象,并装入图元对象
			DrawPictureTouch touch = drawPictureVi.getTouch();
	
			PointF centerPoint=drawPictureVi.getCenterPoint();
			PointF textPoint=drawPictureVi.getTextPoint();
			int contentId=drawPictureVi.getContentId();
			
			Picture picture=new Picture(contentId,
					touch.getDx(),touch.getDy(),touch.getScale(),touch.getDegree(),
					new PointF(centerPoint.x,centerPoint.y),
					new PointF(textPoint.x,textPoint.y));
			Pel newPel = new Pel();
			newPel.picture = picture;
			
			//添加至文本总链表
			(CanvasView.pelList).add(newPel);
			
			//记录栈中信息
			undoStack.push(new DrawpelStep(newPel));//将该“步”压入undo栈
			
			//更新画布
			updateSavedBitmap();
		}
		
		//结束该活动
		finish();
	}
	
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
	
	public void onSelectPictureBtn(View v)
	{
		//弹出
		pictureDialog.show();
		Toast.makeText(this, "吹一吹试下，可以语音识图哦", Toast.LENGTH_LONG).show();
	}
	
	public static DrawPictureView getDrawPictureView()
	{
		return drawPictureVi;
	}
	
	//关闭工具箱
	public static void closeTools()
	{			
		Animation downDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.downdisappear);  		
		Animation topDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.topdisappear);  			
		
		downToolbar.startAnimation(downDisappearAnim);
		topToolbar.startAnimation(topDisappearAnim);
		
		downToolbar.setVisibility(View.GONE);
		topToolbar.setVisibility(View.GONE);
		setToolsClickable(false);
	}
	
	//打开工具箱
	public static void openTools()
	{				
		Animation downAppearAnim = AnimationUtils.loadAnimation(context, R.anim.downappear);  		
		Animation topAppearAnim = AnimationUtils.loadAnimation(context, R.anim.topappear);  			
		
		downToolbar.startAnimation(downAppearAnim);
		topToolbar.startAnimation(topAppearAnim);
		
		downToolbar.setVisibility(View.VISIBLE);
		topToolbar.setVisibility(View.VISIBLE);
		setToolsClickable(true);
	}
	
	public static void setToolsClickable(boolean bool)
	{	
		for(int i=0;i<allBtns.length;i++)
			allBtns[i].setClickable(bool);
	}
	
	public static Context getContext()
	{
		return context;
	}
}
