package com.noahark.graffiti.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.view.PeneffectView;
import com.noahark.graffiti.touch.DrawTouch;

//调色板对话框
public class PenDialog extends Dialog implements OnClickListener,OnSeekBarChangeListener 
{
	private Paint paint = DrawTouch.getCurPaint();//获取当前绘制画笔
	// 调整笔触相关控件
	private PeneffectView peneffectVi;
	private SeekBar penwidthSeekBar;
	private TextView penwidthTextVi;
	private Button cancelBtn;
	
	// 线形按钮
	private Matrix matrix;
	private static Button curShapeBtn;
	private Button[] penshapeBtns = new Button[8];
	private int[] penshapeBtnsId = new int[] 
	{ R.id.btn_penshape1,R.id.btn_penshape2, R.id.btn_penshape3, R.id.btn_penshape4,
	  R.id.btn_penshape5,R.id.btn_penshape6, R.id.btn_penshape7, R.id.btn_penshape8};
	
	//特效按钮
	private static Button curEffectBtn;
	private Button[] peneffectBtns = new Button[4];
	private int[] peneffectBtnsId = new int[] 
	{ R.id.btn_peneffect1,R.id.btn_peneffect2, R.id.btn_peneffect3, R.id.btn_peneffect4};
	
	// 构造函数
	public PenDialog(Context context, int theme) {
		super(context, theme);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_pen);

		initView();
		initData();
	}

	// 初始化界面对象
	private void initView() {
		peneffectVi = (PeneffectView) findViewById(R.id.vi_peneffect);
		penwidthSeekBar = (SeekBar) findViewById(R.id.seekbar_penwidth);
		penwidthTextVi = (TextView) findViewById(R.id.textvi_penwidth);
		cancelBtn = (Button) findViewById(R.id.btn_pen_cancel);

		matrix=new Matrix();
		matrix.setSkew(2,2);
		curShapeBtn=(Button)findViewById(R.id.btn_penshape1);
		curEffectBtn=(Button)findViewById(R.id.btn_peneffect1);
		//12线形按钮
		for (int i = 0; i < penshapeBtns.length; i++) 
		{
			penshapeBtns[i] = (Button) findViewById(penshapeBtnsId[i]);
		}
		//4特效按钮
		for(int i=0;i<peneffectBtns.length;i++)
		{
			peneffectBtns[i]=(Button) findViewById(peneffectBtnsId[i]);
		}
	}

	// 初始化数据
	private void initData() 
	{
		// 设置监听
		penwidthSeekBar.setOnSeekBarChangeListener(this);
		for (int i = 0; i < penshapeBtns.length; i++) //线形按钮
		{
			penshapeBtns[i].setOnClickListener(this);
		}
		for (int i = 0; i < peneffectBtns.length; i++) //特效按钮
		{
			peneffectBtns[i].setOnClickListener(this);
		}		
		cancelBtn.setOnClickListener(this);

		// 以当前画笔风格初始化特效区域
		int curWidth = (int) (DrawTouch.getCurPaint()).getStrokeWidth();
		penwidthSeekBar.setProgress(curWidth);
		penwidthTextVi.setText(Integer.toString(curWidth));

		peneffectVi.invalidate();
	}

	public void onClick(View v) 
	{
		switch (v.getId()) 
		{		
			/**
			 * 8个线形按钮
			 */
			case R.id.btn_penshape1: //正常
			{
				updatePenshapeIcons(v);
				paint.setPathEffect(new CornerPathEffect(10));
			}break;
			case R.id.btn_penshape2: //虚线1
			{
				updatePenshapeIcons(v);
				paint.setPathEffect(new DashPathEffect(new float[] { 20,20}, 0.5f));
			}break;
			case R.id.btn_penshape3: //虚线2
			{
				updatePenshapeIcons(v);
				paint.setPathEffect(new DashPathEffect(new float[] { 40, 20,10,20 }, 0.5f));
			}break;	
			case R.id.btn_penshape4: //杂乱
			{
				updatePenshapeIcons(v);
				paint.setPathEffect(new DiscretePathEffect(5f, 9f));
			}break;
			case R.id.btn_penshape5: // 椭圆
			{
				updatePenshapeIcons(v);

				float width=penwidthSeekBar.getProgress();
				Path p = new Path();
				p.addOval(new RectF(0, 0, width, width), Path.Direction.CCW);
				paint.setPathEffect(new PathDashPathEffect(p, width+10, 0,PathDashPathEffect.Style.ROTATE));
			}break;
			case R.id.btn_penshape6: //正方形
			{
				updatePenshapeIcons(v);
				
				float width=penwidthSeekBar.getProgress();
				Path p = new Path();
				p.addRect(new RectF(0, 0, width, width), Path.Direction.CCW);
				paint.setPathEffect(new PathDashPathEffect(p, width+10, 0,PathDashPathEffect.Style.ROTATE));
			}break;
			case R.id.btn_penshape7: //毛笔
			{
				updatePenshapeIcons(v);
				
				float width=penwidthSeekBar.getProgress();
				Path p = new Path();
				p.addRect(new RectF(0, 0, width, width), Path.Direction.CCW);
				p.transform(matrix);
				paint.setPathEffect(new PathDashPathEffect(p, 2, 0,PathDashPathEffect.Style.TRANSLATE));
			}break;
			case R.id.btn_penshape8://马克笔
			{
				updatePenshapeIcons(v);
				
				float width=penwidthSeekBar.getProgress();
				Path p = new Path();
				p.addArc(new RectF(0, 0, width+4, width+4), -90, 90);
				p.addArc(new RectF(0, 0, width+4, width+4), 90, -90);
				paint.setPathEffect(new PathDashPathEffect(p, 2, 0,PathDashPathEffect.Style.TRANSLATE));
			}break;
	
			/**
			 * 4个特效按钮
			 */
			case R.id.btn_peneffect1://无
			{
				updatePeneffectIcons(v);
				paint.setMaskFilter(null);
			}break;
			case R.id.btn_peneffect2:// 模糊
			{
				updatePeneffectIcons(v);
				paint.setMaskFilter(new BlurMaskFilter(8,BlurMaskFilter.Blur.NORMAL));
			}break;
			case R.id.btn_peneffect3:// 边框
			{		
				updatePeneffectIcons(v);
				paint.setMaskFilter(new BlurMaskFilter(8,BlurMaskFilter.Blur.OUTER));
			}break;
			case R.id.btn_peneffect4://浮雕
			{
				updatePeneffectIcons(v);
				paint.setMaskFilter(new EmbossMaskFilter(new float[] { 1,1, 1 }, 0.4f, 6, 3.5f));
			}break;
	
			// 取消按钮
			case R.id.btn_pen_cancel: 
			{
				this.dismiss();
			}break;
		}

		peneffectVi.invalidate();// 刷新特效区域
	}

	// 拖动的时候时刻更新粗细文本
	public void onProgressChanged(SeekBar seekBar, int curWidth,boolean fromUser) 
	{
		// 移到0的时候自动转换成1
		if (curWidth == 0)
		{
			seekBar.setProgress(1);
			curWidth=1;
		}

		penwidthTextVi.setText(Integer.toString(curWidth));//更新粗细文本

		//对于PathDashPathEffect特效要特殊处理
		switch(curShapeBtn.getId())
		{
			case R.id.btn_penshape5: //椭圆
			{				
				Path p = new Path();
				p.addOval(new RectF(0, 0, curWidth, curWidth), Path.Direction.CCW);		
				paint.setPathEffect(new PathDashPathEffect(p, curWidth+10, 0,PathDashPathEffect.Style.ROTATE));
			}break;
			case R.id.btn_penshape6://正方形
			{
				Path p = new Path();
				p.addRect(new RectF(0, 0, curWidth, curWidth), Path.Direction.CCW);
				paint.setPathEffect(new PathDashPathEffect(p, curWidth+10, 0,PathDashPathEffect.Style.ROTATE));
			}break;
			case R.id.btn_penshape7://毛笔
			{
				Path p = new Path();
				p.addRect(new RectF(0, 0, curWidth, curWidth), Path.Direction.CCW);
				p.transform(matrix);
				paint.setPathEffect(new PathDashPathEffect(p, 2, 0,PathDashPathEffect.Style.TRANSLATE));
			}break;
			case R.id.btn_penshape8://马克笔
			{
				Path p = new Path();
				p.addArc(new RectF(0, 0, curWidth+4, curWidth+4), -90, 90);
				p.addArc(new RectF(0, 0, curWidth+4, curWidth+4), 90, -90);
				paint.setPathEffect(new PathDashPathEffect(p, 2, 0,PathDashPathEffect.Style.TRANSLATE));
			}break;
		}
		
		paint.setStrokeWidth(curWidth);//改变粗细
		peneffectVi.invalidate();// 更新示意view
	}

	public void onStartTrackingTouch(SeekBar seekBar) 
	{

	}

	// 放开拖动条后 重绘特效示意区域
	public void onStopTrackingTouch(SeekBar seekBar) 
	{
		
	}
	
	/**
	 * 辅助用
	 */
	//更新线形箱相关图标
	public void updatePenshapeIcons(View v)
	{
		curShapeBtn.setTextColor(Color.parseColor("#ff666666"));
		curShapeBtn=(Button)v;
		curShapeBtn.setTextColor(Color.parseColor("#0099CC"));
	}
	
	//更新特效箱相关图标
	public void updatePeneffectIcons(View v)
	{	
		curEffectBtn.setTextColor(Color.parseColor("#ff666666"));
		curEffectBtn=(Button)v;
		curEffectBtn.setTextColor(Color.parseColor("#0099CC"));
	}
}
