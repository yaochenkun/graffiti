package com.noahark.graffiti.ui.activity;


import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.processing.BitmapProcessor;
import com.noahark.graffiti.processing.Colorful;
import com.noahark.graffiti.processing.DiPian;
import com.noahark.graffiti.processing.Flower;
import com.noahark.graffiti.processing.HeiBai;
import com.noahark.graffiti.processing.HuaiJiu;
import com.noahark.graffiti.processing.JiMu;
import com.noahark.graffiti.processing.LianHuanHua;
import com.noahark.graffiti.processing.Light;
import com.noahark.graffiti.processing.Nick;
import com.noahark.graffiti.processing.Oxpaper;
import com.noahark.graffiti.processing.Pencil;
import com.noahark.graffiti.processing.Rommantic;
import com.noahark.graffiti.processing.RuiHua;
import com.noahark.graffiti.processing.Star;
import com.noahark.graffiti.processing.SuMiao;
import com.noahark.graffiti.processing.Sunshine;
import com.noahark.graffiti.processing.XiangSu;
import com.noahark.graffiti.processing.YouHua;
import com.noahark.graffiti.processing.YuanTu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ProcessingActivity extends Activity
{
	private View previewVi;
	private Bitmap previewBitmap=null;
	private CanvasView canvasVi=MainActivity.getCanvasView();
	
	public ProgressDialog progressDialog;
	final Handler processBitmapHandler=new Handler() //图片处理线程消息处理者
	{
		public void handleMessage(Message msg)
		{	
			try
			{
				progressDialog.dismiss();		
				
				CanvasView.ensureBitmapRecycled(previewBitmap);
				previewBitmap=(Bitmap)msg.getData().getParcelable("processedBitmap");
				Drawable previewDrawable=new BitmapDrawable(previewBitmap);
				previewVi.setBackgroundDrawable(previewDrawable);			
				
			    super.handleMessage(msg);
			}
			catch(Exception e){}
		} 
	};
	
	
	
	protected void onCreate(Bundle savedInstanceState) 
	{			
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	
	//初始化组件
	public void initView()
	{
		setContentView(R.layout.activity_processing);
		previewVi=(View)findViewById(R.id.vi_preview);
	}
	
	//初始化数据
	public void initData()
	{
		Drawable backgroundDrawable=new BitmapDrawable(CanvasView.getCopyOfBackgroundBitmap());
		previewVi.setBackgroundDrawable(backgroundDrawable);
	}
	
	//原图
	public void onYuanTuStyleBtn(View v)
	{		
		Processing(v);
	}
	
	//怀旧
	public void onHuaijiuStyleBtn(View v)
	{
		Processing(v);
	}
	
	//黑白
	public void onHeibaiStyleBtn(View v)
	{
		Processing(v);
	}
	
	//底片
	public void onDipianStyleBtn(View v)
	{
		Processing(v);
	}
	
	//浮雕
	public void onFudiaoStyleBtn(View v)
	{
		Processing(v);
	}
	
	//冰冻
	public void onBingdongStyleBtn(View v)
	{
		Processing(v);
	}
	
	//雾化
	public void onWuhuaStyleBtn(View v)
	{
		Processing(v);
	}
	
	//积木
	public void onJimuStyleBtn(View v)
	{
		Processing(v);
	}
	
	//熔铸
	public void onRongzhuStyleBtn(View v)
	{
		Processing(v);
	}
	
	//连环画
	public void onLianhuanhuaStyleBtn(View v)
	{
		Processing(v);
	}
	
	//高亮
	public void onGaoliangStyleBtn(View v)
	{
		Processing(v);
	}
	
	//锐化
	public void onRuihuaStyleBtn(View v)
	{
		Processing(v);
	}
	
	//素描
	public void onSumiaoStyleBtn(View v)
	{
		Processing(v);
	}
	
	//模糊
	public void onMohuStyleBtn(View v)
	{
		Processing(v);
	}
	
	//油画
	public void onYouhuaStyleBtn(View v)
	{
		Processing(v);
	}
	
	//像素
	public void onXiangsuStyleBtn(View v)
	{
		Processing(v);
	}
	
	//花朵
	public void onFlowerStyleBtn(View v)
	{
		Processing(v);
	}
	
	//彩铅
	public void onColorfulStyleBtn(View v)
	{Log.v("v","asasdasd");
		Processing(v);
	}
	
	//素铅
	public void onPencilStyleBtn(View v)
	{
		Processing(v);
	}
	
	//牛皮纸
	public void onOxpaperStyleBtn(View v)
	{
		Processing(v);
	}
	
	//刻痕
	public void onNickStyleBtn(View v)
	{
		Processing(v);
	}
	
	//光晕
	public void onLightStyleBtn(View v)
	{
		Processing(v);
	}
	
	//浪漫
	public void onRommanticStyleBtn(View v)
	{
		Processing(v);
	}
	
	//星空
	public void onStarStyleBtn(View v)
	{
		Processing(v);
	}
	
	//夕阳
	public void onSunshineStyleBtn(View v)
	{
		Processing(v);
	}
	
	//图片处理公共步骤
	public void Processing(final View v)
	{
		//进度对话框
		progressDialog=new ProgressDialog(this);
		progressDialog.setMessage("图片渲染中，请稍等...");
		progressDialog.show();
		
		new Thread(new Runnable()
		{
			public void run() 
			{
				Bitmap originalBitmap=CanvasView.getOriginalBackgroundBitmap();
				BitmapProcessor processor = null;//图片处理抽象对象
				
				//筛选不同图片处理算法
				switch(v.getId())
				{
					case R.id.btn_style_yuantu://原图
					{
						processor=new YuanTu();
					}break;
					case R.id.btn_style_huaijiu://怀旧
					{
						processor=new HuaiJiu();
					}break;
					case R.id.btn_style_heibai://黑白
					{
						processor=new HeiBai();
					}break;
					case R.id.btn_style_dipian://底片
					{
						processor=new DiPian();
					}break;
					case R.id.btn_style_jimu://积木
					{
						processor=new JiMu();
					}break;
					case R.id.btn_style_lianhuanhua://连环画
					{
						processor=new LianHuanHua();
					}break;
					case R.id.btn_style_ruihua://锐化
					{
						processor=new RuiHua();
					}break;
					case R.id.btn_style_sumiao://素描
					{
						processor=new SuMiao();
					}break;
					case R.id.btn_style_youhua://油画
					{
						processor=new YouHua();
					}break;
					case R.id.btn_style_xiangsu://像素
					{
						processor=new XiangSu();
					}break;
					case R.id.btn_style_flower://花朵
					{
						processor=new Flower();	
					}break;
					case R.id.btn_style_colorful://彩铅
					{
						processor=new Colorful();
					}break;
					case R.id.btn_style_pencil://素铅
					{
						processor=new Pencil();
					}break;
					case R.id.btn_style_oxpaper://牛皮纸
					{
						processor=new Oxpaper();
					}break;
					case R.id.btn_style_nick://刻痕
					{
						processor=new Nick();
					}break;
					case R.id.btn_style_light://光晕
					{
						processor=new Light();
					}break;
					case R.id.btn_style_rommantic://浪漫
					{
						processor=new Rommantic();
					}break;
					case R.id.btn_style_star://星空
					{
						processor=new Star();
					}break;
					case R.id.btn_style_sunshine://夕阳
					{
						processor=new Sunshine();
					}break;
					
				}		

				//图片打包进消息中准备传递给线程处理者
				Bundle data=new Bundle();
				data.putParcelable("processedBitmap", processor.createProcessedBitmap(originalBitmap));
				Message msg=new Message();
				msg.setData(data);
				processBitmapHandler.sendMessage(msg);
			}					
		}).start();
	}
	
	//返回
	public void onProcessingBackBtn(View v)
	{
		finish();
	}
	
	//确定
	public void onProcessingOkBtn(View v)
	{
		if(previewBitmap != null) //说明进行过图片处理
		{
			canvasVi.setProcessedBitmap(previewBitmap);
			Toast.makeText(MainActivity.getContext(), "已更换背景", Toast.LENGTH_SHORT).show();
		}
		
		finish();
	}
}
