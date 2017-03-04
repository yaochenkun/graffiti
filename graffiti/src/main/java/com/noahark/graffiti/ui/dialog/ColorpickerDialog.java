package com.noahark.graffiti.ui.dialog;


import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.activity.DrawTextActivity;
import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.ui.view.DrawTextView;
import com.noahark.graffiti.touch.DrawTouch;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

//调色板对话框
public class ColorpickerDialog extends Dialog implements OnClickListener
{
	//调色板控件相关
	public  ColorPicker picker;
	private OpacityBar opacityBar;
	private SaturationBar saturationBar;
	private ValueBar valueBar;
	
	private Button okBtn;
		
	public ColorpickerDialog(Context context,int theme) 
	{
		super(context,theme);
	}

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);        
		this.setContentView(R.layout.dialog_colorpicker);
		
		initView();
		initData();
	}
	
	//初始化界面对象
	private void initView()
	{
		//找到实例对象
		picker = (ColorPicker) findViewById(R.id.colorpicker_picker);
		opacityBar = (OpacityBar) findViewById(R.id.colorpicker_opacitybar);
		saturationBar = (SaturationBar) findViewById(R.id.colorpicker_saturationbar);
		valueBar = (ValueBar) findViewById(R.id.colorpicker_valuebar);
		
		//按钮对象
		okBtn=(Button)findViewById(R.id.btn_colorpicker_ok);
	}
	
	//初始化数据
	private void initData()
	{
		//使环形取色器和拖动条建立关系
		picker.addOpacityBar(opacityBar);
		picker.addSaturationBar(saturationBar);
		picker.addValueBar(valueBar);	
		picker.setColor(Color.parseColor("#ff298ecb"));//初始化为黄色
		
		okBtn.setOnClickListener(this);
	}
	
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.btn_colorpicker_ok:
			{
				int curColor=picker.getColor();
				DrawTouch.getCurPaint().setColor(curColor);//设置当前颜色
				(MainActivity.colorBtn).setTextColor(curColor);//改变颜色二字

				if(DrawTextActivity.drawTextVi != null)//是在编辑文字界面打开的
				{	
					(DrawTextView.drawTextPaint).setColor(curColor);
					(DrawTextActivity.drawTextVi).invalidate();
				}
				
				this.dismiss();//关闭对话框
			}break;
		}
	}
}
