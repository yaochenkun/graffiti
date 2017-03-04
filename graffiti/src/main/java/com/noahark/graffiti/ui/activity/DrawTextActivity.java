 package com.noahark.graffiti.ui.activity;

import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.dialog.ColorpickerDialog;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.ui.view.DrawTextView;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.extra.Picture;
import com.noahark.graffiti.extra.Text;
import com.noahark.graffiti.step.DrawpelStep;
import com.noahark.graffiti.step.Step;
import com.noahark.graffiti.touch.DrawTextTouch;
import com.noahark.graffiti.touch.DrawTouch;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

public class DrawTextActivity extends Activity
{
	protected static List<Pel> pelList=CanvasView.getPelList(); // 图元链表// 屏幕宽高
	protected static Stack<Step> undoStack=CanvasView.getUndoStack();//获取undo
	protected Pel selectedPel; // 当前选中图元
	protected Bitmap savedBitmap; // 当前重绘位图	
	protected Canvas savedCanvas; //重绘画布
	private String content="意绘";
	private ColorpickerDialog colorpickerDialog;
	public static DrawTextView drawTextVi=null;
	private static Context context=null;
	public static View topToolbar;
	private static View downToolbar;
	public static View[] allBtns;
	
	protected void onCreate(Bundle savedInstanceState) 
	{			
		super.onCreate(savedInstanceState);
		initView();
		initData();
		demandContent();
	}
	
	//初始化组件
	public void initView()
	{
		setContentView(R.layout.activity_drawtext);
		drawTextVi=(DrawTextView)findViewById(R.id.drawtext_canvas);
		colorpickerDialog = new ColorpickerDialog(DrawTextActivity.this,R.style.GraffitiDialog);
		topToolbar=(View)findViewById(R.id.drawtext_toptoolbar);
		downToolbar=(View)findViewById(R.id.drawtext_downtoolbar);
		
		int[] btnIds=new int[]{R.id.drawtext_refuse,R.id.drawtext_sure,R.id.drawtext_content,R.id.drawtext_color};
		allBtns=new View[btnIds.length];
		for(int i=0;i<btnIds.length;i++)
			allBtns[i]=(View)findViewById(btnIds[i]);
	}
	
	//初始化数据
	public void initData()
	{
		savedCanvas=new Canvas();
		context=DrawTextActivity.this;
	}
	
	//返回
	public void onDrawtextBackBtn(View v)
	{
		finish();
	}
	
	//确定
	public void onDrawtextOkBtn(View v)
	{
		//构造该次的文本对象,并装入图元对象
		DrawTextTouch touch = DrawTextView.touch;
		Text text=new Text(content,
				touch.getDx(),touch.getDy(),touch.getScale(),touch.getDegree(),
				new PointF(touch.getCenterPoint().x,touch.getCenterPoint().y),
				new PointF(touch.getTextPoint().x, touch.getTextPoint().y));
		Pel newPel = new Pel();
		newPel.text = text;
		
		//添加至文本总链表
		(CanvasView.pelList).add(newPel);
		
		//记录栈中信息
		undoStack.push(new DrawpelStep(newPel));//将该“步”压入undo栈
		
		//更新画布
		updateSavedBitmap();
		
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
	
	public void onChangeContentBtn(View v)
	{
		demandContent();
	}
	
	public void onChangeColorBtn(View v)
	{
		colorpickerDialog.show();
		colorpickerDialog.picker.setOldCenterColor(DrawTouch.getCurPaint().getColor()); //获取当前画笔的颜色作为左半圆颜色
	}	
	
	public void demandContent()	
	{
		final EditText editTxt=new EditText(DrawTextActivity.this);//作品的名称编辑框
		editTxt.setText(content);
		class okClick implements DialogInterface.OnClickListener
		{
			public void onClick(DialogInterface dialog, int which) //ok
			{
				drawTextVi.setContent(content=editTxt.getText().toString());
				drawTextVi.invalidate();
			}		
		}		
		class cancelClick implements DialogInterface.OnClickListener //cancel
		{
			public void onClick(DialogInterface dialog, int which) 
			{
			}		
		}
		
		//实例化确认对话框
		Builder dialog=new AlertDialog.Builder(DrawTextActivity.this);
		dialog.setIcon(drawable.ic_dialog_info);
		dialog.setView(editTxt);
		dialog.setMessage("请输入文本");
		dialog.setPositiveButton("确定", new okClick());
		dialog.setNegativeButton("取消", new cancelClick());
		dialog.create();
		dialog.show();
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
}
