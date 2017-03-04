package com.noahark.graffiti.extra;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;

public class Pel{//图元类
	public Path path; //路径
	public Region region;//区域
	public Paint paint;//画笔
	public Text text;//文本
	public Picture picture;//插画
	public boolean closure;//是否封闭
	
	//构造（实际使用时应该把Pel构造成Pel(path region paint name)的形式，形参均在外部都已经定义好了的）
	public Pel()
	{
		path=new Path();
		region=new Region();
		paint=new Paint();
		text=null;
		picture=null;
	}
	
	//深拷贝
	public Pel clone()
	{	
		Pel pel=new Pel();
		(pel.path).set(path);
		(pel.region).set(region);
		(pel.paint).set(paint);
		pel.closure=closure;
		
		return pel;	
	}
}
