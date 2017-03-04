package com.noahark.graffiti.ui.dialog;

import java.util.ArrayList;

import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.activity.DrawPictureActivity;
import com.noahark.graffiti.ui.view.DrawPictureView;
import com.noahark.graffiti.snow.RecordThread;
import com.noahark.graffiti.touch.DrawPictureTouch;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

//调色板对话框
public class PictureDialog extends Dialog implements OnClickListener,RecognizerDialogListener
{	
	// 线形按钮
	private Button[] pictureBtns = new Button[68];
	private int[] pictureIds = new int[] 
	{R.id.btn_aixin,R.id.btn_baizhi,R.id.btn_baozhi,R.id.btn_bianqian,
	 R.id.btn_caihong,R.id.btn_keche,R.id.btn_shuidi,R.id.btn_huakuang,
	 R.id.btn_qianbi,R.id.btn_xianhua,R.id.btn_liwu,R.id.btn_shouji,
	 R.id.btn_diannao,R.id.btn_wujiaoxing,R.id.btn_ren,R.id.btn_yinfu,
	 
	 R.id.btn_yaoshi,R.id.btn_zifu,R.id.btn_yusan,R.id.btn_xinfeng,
	 R.id.btn_yonghu,R.id.btn_sanjiaoban,R.id.btn_huojian,R.id.btn_jianpan,
	 R.id.btn_jiandao,R.id.btn_jiangbei,R.id.btn_huihua,R.id.btn_ditie,
	 R.id.btn_tingtong,R.id.btn_diqiu,R.id.btn_dingwei,R.id.btn_duoyun,
	 
	 R.id.btn_bingzhuangtu,R.id.btn_fanchuan,R.id.btn_fangdajing,R.id.btn_bangbangtang,
	 R.id.btn_gaogenxie,R.id.btn_feiji,R.id.btn_gongwenbao,R.id.btn_chuzuche,
	 R.id.btn_huabi,R.id.btn_kache,R.id.btn_lunchuan,R.id.btn_pingban,
	 R.id.btn_shalou,R.id.btn_shouyinji,R.id.btn_shoubing,R.id.btn_xiyiji,
	 
	 R.id.btn_yinxiang,R.id.btn_zhexian,R.id.btn_zhezhi,R.id.btn_men,
	 R.id.btn_maozi,R.id.btn_neicunka,R.id.btn_pukepai,R.id.btn_reqiqiu,
	 R.id.btn_moshubang,R.id.btn_hongqi,R.id.btn_dianshiji,R.id.btn_biaoqian,
	 R.id.btn_dengpao,R.id.btn_gongju,R.id.btn_xiangpian,R.id.btn_youqitong,
	 R.id.btn_huaban,R.id.btn_motuoche,R.id.btn_maikefeng,R.id.btn_zhaoxiangji};
	

	private DrawPictureView drawPictureVi;
	
	//语音识别
	private String said;
    private RecognizerDialog isrDialog;
    private final String APP_ID="514fb8d7";
    private RecordThread recordThread = null;
    
    
	// 构造函数
	public PictureDialog(Context context, int theme) {
		super(context, theme);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_picture);

		initView();
		initData();
	}

	// 初始化界面对象
	private void initView() 
	{
		for (int i = 0; i < pictureBtns.length; i++) 
		{
			pictureBtns[i] = (Button) findViewById(pictureIds[i]);
		}
		//4特效按钮
		for(int i=0;i<pictureBtns.length;i++)
		{
			pictureBtns[i]=(Button) findViewById(pictureIds[i]);
		}
		drawPictureVi=DrawPictureActivity.getDrawPictureView();
		
		//语音识别
		isrDialog = new RecognizerDialog(DrawPictureActivity.getContext(),"appid="+APP_ID);
	}

	// 初始化数据
	private void initData() 
	{
		// 设置监听
		for (int i = 0; i < pictureBtns.length; i++) //线形按钮
		{
			pictureBtns[i].setOnClickListener(this);
		}
		for (int i = 0; i < pictureBtns.length; i++) //特效按钮
		{
			pictureBtns[i].setOnClickListener(this);
		}	
		
		//语音识别
		said="";
		isrDialog = new RecognizerDialog(DrawPictureActivity.getContext(),"appid="+APP_ID);
		isrDialog.setEngine("sms",null,null);
		isrDialog.setListener(this);
		
		blowHandler= new BlowHandler();
	}
	
	public static BlowHandler blowHandler;
	class BlowHandler extends Handler 
	{
		public void sleep(long delayMillis) 
		{
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(2), delayMillis);
		}
		@Override
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
				case 1:
				{
					recordThread.stopRecord();
					//开启语音识别对话框
					isrDialog.show();
				}break;
				case 2:
				{
					recordThread.stopRecord(); //终止音频监听进程
					recordThread=null;
					
					//开启语音识别对话框
					isrDialog.show();
				}break;
				default:break;
			}
		}
	};

	public void onClick(View v) 
	{
		switch (v.getId()) 
		{		
			case R.id.btn_caihong:
			{
				updateDataAndCanvas(R.drawable.pic_caihong);
			}break;
			case R.id.btn_liwu:
			{
				updateDataAndCanvas(R.drawable.pic_liwu);
			}break;
			case R.id.btn_xianhua:
			{
				updateDataAndCanvas(R.drawable.pic_xianhua);
			}break;
			case R.id.btn_shuidi:
			{
				updateDataAndCanvas(R.drawable.pic_shuidi);
			}break;
			case R.id.btn_shouji:
			{
				updateDataAndCanvas(R.drawable.pic_shouji);
			}break;
			case R.id.btn_baizhi:
			{
				updateDataAndCanvas(R.drawable.pic_baizhi);
			}break;
			case R.id.btn_keche:
			{
				updateDataAndCanvas(R.drawable.pic_keche);
			}break;
			case R.id.btn_bianqian:
			{
				updateDataAndCanvas(R.drawable.pic_bianqian);
			}break;
			case R.id.btn_baozhi:
			{
				updateDataAndCanvas(R.drawable.pic_baozhi);
			}break;
			case R.id.btn_diannao:
			{
				updateDataAndCanvas(R.drawable.pic_diannao);
			}break;
			case R.id.btn_wujiaoxing:
			{
				updateDataAndCanvas(R.drawable.pic_wujiaoxing);
			}break;
			case R.id.btn_huakuang:
			{
				updateDataAndCanvas(R.drawable.pic_huakuang);
			}break;
			case R.id.btn_qianbi:
			{
				updateDataAndCanvas(R.drawable.pic_qianbi);
			}break;
			case R.id.btn_ren:
			{
				updateDataAndCanvas(R.drawable.pic_ren);
			}break;
			case R.id.btn_aixin:
			{
				updateDataAndCanvas(R.drawable.pic_aixin);
			}break;
			case R.id.btn_yinfu:
			{
				updateDataAndCanvas(R.drawable.pic_yinfu);
			}break;
			case R.id.btn_yaoshi:
			{
				updateDataAndCanvas(R.drawable.pic_yaoshi);
			}break;
			case R.id.btn_zifu:
			{
				updateDataAndCanvas(R.drawable.pic_zifu);
			}break;
			case R.id.btn_yusan:
			{
				updateDataAndCanvas(R.drawable.pic_yusan);
			}break;
			case R.id.btn_xinfeng:
			{
				updateDataAndCanvas(R.drawable.pic_xinfeng);
			}break;
			case R.id.btn_yonghu:
			{
				updateDataAndCanvas(R.drawable.pic_yonghu);
			}break;
			case R.id.btn_sanjiaoban:
			{
				updateDataAndCanvas(R.drawable.pic_sanjiaoban);
			}break;
			case R.id.btn_huojian:
			{
				updateDataAndCanvas(R.drawable.pic_huojian);
			}break;
			case R.id.btn_jianpan:
			{
				updateDataAndCanvas(R.drawable.pic_jianpan);
			}break;		
			case R.id.btn_jiandao:
			{
				updateDataAndCanvas(R.drawable.pic_jiandao);
			}break;
			case R.id.btn_jiangbei:
			{
				updateDataAndCanvas(R.drawable.pic_jiangbei);
			}break;
			case R.id.btn_huihua:
			{
				updateDataAndCanvas(R.drawable.pic_huihua);
			}break;
			case R.id.btn_ditie:
			{
				updateDataAndCanvas(R.drawable.pic_ditie);
			}break;		
			case R.id.btn_tingtong:
			{
				updateDataAndCanvas(R.drawable.pic_tingtong);
			}break;
			case R.id.btn_diqiu:
			{
				updateDataAndCanvas(R.drawable.pic_diqiu);
			}break;
			case R.id.btn_dingwei:
			{
				updateDataAndCanvas(R.drawable.pic_dingwei);
			}break;
			case R.id.btn_duoyun:
			{
				updateDataAndCanvas(R.drawable.pic_duoyun);
			}break;		
			case R.id.btn_bingzhuangtu:
			{
				updateDataAndCanvas(R.drawable.pic_bingzhuangtu);
			}break;
			case R.id.btn_fanchuan:
			{
				updateDataAndCanvas(R.drawable.pic_fanchuan);
			}break;
			case R.id.btn_fangdajing:
			{
				updateDataAndCanvas(R.drawable.pic_fangdajing);
			}break;
			case R.id.btn_bangbangtang:
			{
				updateDataAndCanvas(R.drawable.pic_bangbangtang);
			}break;		
			case R.id.btn_gaogenxie:
			{
				updateDataAndCanvas(R.drawable.pic_gaogenxie);
			}break;
			case R.id.btn_feiji:
			{
				updateDataAndCanvas(R.drawable.pic_feiji);
			}break;
			case R.id.btn_gongwenbao:
			{
				updateDataAndCanvas(R.drawable.pic_gongwenbao);
			}break;
			case R.id.btn_chuzuche:
			{
				updateDataAndCanvas(R.drawable.pic_chuzuche);
			}break;		
			case R.id.btn_huabi:
			{
				updateDataAndCanvas(R.drawable.pic_huabi);
			}break;
			case R.id.btn_kache:
			{
				updateDataAndCanvas(R.drawable.pic_kache);
			}break;
			case R.id.btn_lunchuan:
			{
				updateDataAndCanvas(R.drawable.pic_lunchuan);
			}break;
			case R.id.btn_pingban:
			{
				updateDataAndCanvas(R.drawable.pic_pingban);
			}break;		
			case R.id.btn_zhaoxiangji:
			{
				updateDataAndCanvas(R.drawable.pic_zhaoxiangji);
			}break;	
			case R.id.btn_shalou:
			{
				updateDataAndCanvas(R.drawable.pic_shalou);
			}break;
			case R.id.btn_shouyinji:
			{
				updateDataAndCanvas(R.drawable.pic_shouyinji);
			}break;
			case R.id.btn_shoubing:
			{
				updateDataAndCanvas(R.drawable.pic_shoubing);
			}break;
			case R.id.btn_xiyiji:
			{
				updateDataAndCanvas(R.drawable.pic_xiyiji);
			}break;	
			case R.id.btn_yinxiang:
			{
				updateDataAndCanvas(R.drawable.pic_yinxiang);
			}break;
			case R.id.btn_zhexian:
			{
				updateDataAndCanvas(R.drawable.pic_zhexian);
			}break;
			case R.id.btn_zhezhi:
			{
				updateDataAndCanvas(R.drawable.pic_zhezhi);
			}break;
			case R.id.btn_men:
			{
				updateDataAndCanvas(R.drawable.pic_men);
			}break;		
			case R.id.btn_maozi:
			{
				updateDataAndCanvas(R.drawable.pic_maozi);
			}break;
			case R.id.btn_neicunka:
			{
				updateDataAndCanvas(R.drawable.pic_neicunka);
			}break;
			case R.id.btn_pukepai:
			{
				updateDataAndCanvas(R.drawable.pic_pukepai);
			}break;
			case R.id.btn_reqiqiu:
			{
				updateDataAndCanvas(R.drawable.pic_reqiqiu);
			}break;		
			case R.id.btn_moshubang:
			{
				updateDataAndCanvas(R.drawable.pic_moshubang);
			}break;
			case R.id.btn_hongqi:
			{
				updateDataAndCanvas(R.drawable.pic_hongqi);
			}break;
			case R.id.btn_dianshiji:
			{
				updateDataAndCanvas(R.drawable.pic_dianshiji);
			}break;
			case R.id.btn_biaoqian:
			{
				updateDataAndCanvas(R.drawable.pic_biaoqian);
			}break;	
			case R.id.btn_dengpao:
			{
				updateDataAndCanvas(R.drawable.pic_dengpao);
			}break;
			case R.id.btn_gongju:
			{
				updateDataAndCanvas(R.drawable.pic_gongju);
			}break;
			case R.id.btn_xiangpian:
			{
				updateDataAndCanvas(R.drawable.pic_xiangpian);
			}break;
			case R.id.btn_youqitong:
			{
				updateDataAndCanvas(R.drawable.pic_youqitong);
			}break;		
			case R.id.btn_huaban:
			{
				updateDataAndCanvas(R.drawable.pic_huaban);
			}break;
			case R.id.btn_motuoche:
			{
				updateDataAndCanvas(R.drawable.pic_motuoche);
			}break;
			case R.id.btn_maikefeng:
			{
				updateDataAndCanvas(R.drawable.pic_maikefeng);
			}break;
		}
	}

	@Override
	public void onEnd(SpeechError arg0) 
	{
		if(!said.equals("") && said != null)
		{
			if(said.equals("爱心。"))
			{
				updateDataAndCanvas(R.drawable.pic_aixin);
			}
			else if(said.equals("白纸。"))
			{
				updateDataAndCanvas(R.drawable.pic_baizhi);
			}
			else if(said.equals("报纸。"))
			{
				updateDataAndCanvas(R.drawable.pic_baozhi);
			}
			else if(said.equals("便签。"))
			{
				updateDataAndCanvas(R.drawable.pic_bianqian);
			}
			else if(said.equals("彩虹。"))
			{
				updateDataAndCanvas(R.drawable.pic_caihong);
			}
			else if(said.equals("客车。"))
			{
				updateDataAndCanvas(R.drawable.pic_keche);
			}
			else if(said.equals("水滴。"))
			{
				updateDataAndCanvas(R.drawable.pic_shuidi);
			}
			else if(said.equals("画框。"))
			{
				updateDataAndCanvas(R.drawable.pic_huakuang);
			}
			else if(said.equals("铅笔。"))
			{
				updateDataAndCanvas(R.drawable.pic_qianbi);
			}
			else if(said.equals("鲜花。"))
			{
				updateDataAndCanvas(R.drawable.pic_xianhua);
			}
			else if(said.equals("礼物。"))
			{
				updateDataAndCanvas(R.drawable.pic_liwu);
			}
			else if(said.equals("手机。"))
			{
				updateDataAndCanvas(R.drawable.pic_shouji);
			}
			else if(said.equals("电脑。"))
			{
				updateDataAndCanvas(R.drawable.pic_diannao);
			}
			else if(said.equals("五角星。"))
			{
				updateDataAndCanvas(R.drawable.pic_wujiaoxing);
			}
			else if(said.equals("人。"))
			{
				updateDataAndCanvas(R.drawable.pic_ren);
			}
			else if(said.equals("音符。"))
			{
				updateDataAndCanvas(R.drawable.pic_yinfu);
			}
			else if(said.equals("钥匙。"))
			{
				updateDataAndCanvas(R.drawable.pic_yaoshi);
			}
			else if(said.equals("字符。"))
			{
				updateDataAndCanvas(R.drawable.pic_zifu);
			}
			else if(said.equals("雨伞。"))
			{
				updateDataAndCanvas(R.drawable.pic_yusan);
			}
			else if(said.equals("信封。"))
			{
				updateDataAndCanvas(R.drawable.pic_xinfeng);
			}
			else if(said.equals("用户。"))
			{
				updateDataAndCanvas(R.drawable.pic_yonghu);
			}
			else if(said.equals("三角板。"))
			{
				updateDataAndCanvas(R.drawable.pic_sanjiaoban);
			}
			else if(said.equals("火箭。"))
			{
				updateDataAndCanvas(R.drawable.pic_huojian);
			}
			else if(said.equals("键盘。"))
			{
				updateDataAndCanvas(R.drawable.pic_jianpan);
			}
			else if(said.equals("剪刀。"))
			{
				updateDataAndCanvas(R.drawable.pic_jiandao);
			}
			else if(said.equals("奖杯。"))
			{
				updateDataAndCanvas(R.drawable.pic_jiangbei);
			}
			else if(said.equals("对话。"))
			{
				updateDataAndCanvas(R.drawable.pic_huihua);
			}
			else if(said.equals("地铁。"))
			{
				updateDataAndCanvas(R.drawable.pic_ditie);
			}
			else if(said.equals("听筒。"))
			{
				updateDataAndCanvas(R.drawable.pic_tingtong);
			}
			else if(said.equals("地球。"))
			{
				updateDataAndCanvas(R.drawable.pic_diqiu);
			}
			else if(said.equals("定位。"))
			{
				updateDataAndCanvas(R.drawable.pic_dingwei);
			}
			else if(said.equals("多云。"))
			{
				updateDataAndCanvas(R.drawable.pic_duoyun);
			}
			else if(said.equals("饼状图。"))
			{
				updateDataAndCanvas(R.drawable.pic_bingzhuangtu);
			}
			else if(said.equals("帆船。"))
			{
				updateDataAndCanvas(R.drawable.pic_fanchuan);
			}
			else if(said.equals("放大镜。"))
			{
				updateDataAndCanvas(R.drawable.pic_fangdajing);
			}
			else if(said.equals("棒棒糖。"))
			{
				updateDataAndCanvas(R.drawable.pic_bangbangtang);
			}
			else if(said.equals("高跟鞋。"))
			{
				updateDataAndCanvas(R.drawable.pic_gaogenxie);
			}
			else if(said.equals("飞机。"))
			{
				updateDataAndCanvas(R.drawable.pic_feiji);
			}
			else if(said.equals("公文包。"))
			{
				updateDataAndCanvas(R.drawable.pic_gongwenbao);
			}
			else if(said.equals("出租车。"))
			{
				updateDataAndCanvas(R.drawable.pic_chuzuche);
			}
			else if(said.equals("画笔。"))
			{
				updateDataAndCanvas(R.drawable.pic_huabi);
			}
			else if(said.equals("卡车。"))
			{
				updateDataAndCanvas(R.drawable.pic_kache);
			}
			else if(said.equals("轮船。"))
			{
				updateDataAndCanvas(R.drawable.pic_lunchuan);
			}
			else if(said.equals("平板。"))
			{
				updateDataAndCanvas(R.drawable.pic_pingban);
			}
			else if(said.equals("沙漏。"))
			{
				updateDataAndCanvas(R.drawable.pic_shalou);
			}
			else if(said.equals("收音机。"))
			{
				updateDataAndCanvas(R.drawable.pic_shouyinji);
			}
			else if(said.equals("手柄。"))
			{
				updateDataAndCanvas(R.drawable.pic_shoubing);
			}
			else if(said.equals("洗衣机。"))
			{
				updateDataAndCanvas(R.drawable.pic_xiyiji);
			}
			else if(said.equals("音响。"))
			{
				updateDataAndCanvas(R.drawable.pic_yinxiang);
			}
			else if(said.equals("折线。"))
			{
				updateDataAndCanvas(R.drawable.pic_zhexian);
			}
			else if(said.equals("折纸。"))
			{
				updateDataAndCanvas(R.drawable.pic_zhezhi);
			}
			else if(said.equals("门。")||said.equals("们。"))
			{
				updateDataAndCanvas(R.drawable.pic_men);
			}
			else if(said.equals("帽子。"))
			{
				updateDataAndCanvas(R.drawable.pic_maozi);
			}
			else if(said.equals("内存卡。"))
			{
				updateDataAndCanvas(R.drawable.pic_neicunka);
			}
			else if(said.equals("扑克牌。"))
			{
				updateDataAndCanvas(R.drawable.pic_pukepai);
			}
			else if(said.equals("热气球。"))
			{
				updateDataAndCanvas(R.drawable.pic_reqiqiu);
			}
			else if(said.equals("魔术棒。"))
			{
				updateDataAndCanvas(R.drawable.pic_moshubang);
			}
			else if(said.equals("红旗。"))
			{
				updateDataAndCanvas(R.drawable.pic_hongqi);
			}
			else if(said.equals("电视机。"))
			{
				updateDataAndCanvas(R.drawable.pic_dianshiji);
			}
			else if(said.equals("标签。"))
			{
				updateDataAndCanvas(R.drawable.pic_biaoqian);
			}
			else if(said.equals("灯泡。"))
			{
				updateDataAndCanvas(R.drawable.pic_dengpao);
			}
			else if(said.equals("工具。"))
			{
				updateDataAndCanvas(R.drawable.pic_gongju);
			}
			else if(said.equals("相片。"))
			{
				updateDataAndCanvas(R.drawable.pic_xiangpian);
			}
			else if(said.equals("油漆桶。"))
			{
				updateDataAndCanvas(R.drawable.pic_youqitong);
			}
			else if(said.equals("画板。"))
			{
				updateDataAndCanvas(R.drawable.pic_huaban);
			}
			else if(said.equals("摩托车。"))
			{
				updateDataAndCanvas(R.drawable.pic_motuoche);
			}
			else if(said.equals("麦克风。"))
			{
				updateDataAndCanvas(R.drawable.pic_maikefeng);
			}
			else if(said.equals("照相机。"))
			{
				updateDataAndCanvas(R.drawable.pic_zhaoxiangji);
			}
			else
			{
				Toast.makeText(DrawPictureActivity.getContext(), "抱歉，没有明白您的意思",Toast.LENGTH_SHORT).show();
				recordThread = new RecordThread(blowHandler, 1); //启动线程
				recordThread.start();
				said="";
			}
		}
		else
		{
			recordThread = new RecordThread(blowHandler, 1); //启动线程
			recordThread.start();
			said="";
		}
	}

	@Override
	public void onResults(ArrayList<RecognizerResult> results, boolean isLast) 
	{
		said+=results.get(0).text;		
	}
	
	public void updateDataAndCanvas(int contentId)
	{		
		//重置插图位置、缩放、旋转信息
		DrawPictureTouch touch=drawPictureVi.getTouch();
		touch.clear();

		//传入插图信息
		drawPictureVi.setContentAndCenterPoint(contentId);
		drawPictureVi.invalidate();
		
		this.dismiss();//关闭窗口
	}

	@Override
	public void show() 
	{
		// TODO Auto-generated method stub
		super.show();
		
		recordThread = new RecordThread(blowHandler, 1); //启动线程
		recordThread.start();
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		
		recordThread.stopRecord(); //终止音频监听进程
		recordThread=null;
	}
}
