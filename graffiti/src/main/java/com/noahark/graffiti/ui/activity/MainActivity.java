package com.noahark.graffiti.ui.activity;
import android.R.drawable;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.dialog.ColorpickerDialog;
import com.noahark.graffiti.ui.dialog.PenDialog;
import com.noahark.graffiti.ui.fragment.GalleryFragment;
import com.noahark.graffiti.ui.fragment.HelpFragment;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pattern;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.snow.RecordThread;
import com.noahark.graffiti.step.CopypelStep;
import com.noahark.graffiti.step.DeletepelStep;
import com.noahark.graffiti.step.DrawpelStep;
import com.noahark.graffiti.step.FillpelStep;
import com.noahark.graffiti.step.Step;
import com.noahark.graffiti.step.TransformpelStep;
import com.noahark.graffiti.touch.CrossfillTouch;
import com.noahark.graffiti.touch.DrawBesselTouch;
import com.noahark.graffiti.touch.DrawBrokenlineTouch;
import com.noahark.graffiti.touch.DrawFreehandTouch;
import com.noahark.graffiti.touch.DrawLineTouch;
import com.noahark.graffiti.touch.DrawOvalTouch;
import com.noahark.graffiti.touch.DrawPolygonTouch;
import com.noahark.graffiti.touch.DrawRectTouch;
import com.noahark.graffiti.touch.DrawTouch;
import com.noahark.graffiti.touch.KeepDrawingTouch;
import com.noahark.graffiti.touch.Touch;
import com.noahark.graffiti.touch.TransformTouch;
import com.noahark.moments.ui.activity.CommunityActivity;
import com.noahark.moments.ui.activity.LoginActivity;
import com.noahark.moments.ui.activity.MeActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.BindView;


public class MainActivity extends FragmentActivity implements RecognizerDialogListener
{
	/*************************************************/
	private String softDir="/FreeGraffiti";
	public static Context context;
	public static int SCREEN_WIDTH,SCREEN_HEIGHT;
	/*************************************************/
	//抽屉中的各种碎片
	public  static final int MAIN_FRAGMENT=0;//主画布页面
	private final int GALLERY_FRAGMENT=1;//画廊页面
	private final int HELP_FRAGMENT=2;//问号页面
	public  static int curFragmentFlag=MAIN_FRAGMENT;//当前页面标志
	private Fragment curFragment; //当前页面
	/*************************************************/
	
	
	//内部算法
	private List<Pel> pelList;
	private Pel selectedPel;
	private Stack<Step> undoStack;
	private static Stack<Step> redoStack;	
	private static CanvasView canvasVi;
	
	/*************************************************/
	//退出系统用
	private Builder dialog;
	private boolean hasExitAppDialog=false;
	/*************************************************/
	
	//控件;
	public static View[] allBtns;
	public  static View topToolbarSclVi;
	private static View downToolbarSclVi;
	private static Button undoBtn;
	private static Button redoBtn;
	private Button openPelbarBtn;
	private static View transbarLinlayout;
	
	private static PopupWindow pelbarPopwin;
	private static PopupWindow canvasbgbarPopwin;
	
	private static Button extendBtn;
	public static Button colorBtn;//颜色按钮变字用
	
	static public DrawerLayout drawerLayout;//主抽屉
	private ActionBarDrawerToggle drawerToggle;//抽屉实现者
	
	//对话框
	private PenDialog penDialog;//调色板对话框
	private ColorpickerDialog colorpickerDialog;//调色板对话框
	
	//辅助用
	public static Button curToolVi;//工具条：当前选中的工具
	private static ImageView curPelVi;//图元条：当前选中的图元
	private ImageView curCanvasbgVi,whiteCanvasbgVi;//背景条：当前选中的背景
	/**************************************************************************************/
	//传感器
	public  static Thread keepdrawingThread;
	public  static PointF lastPoint=new PointF();
	public  static Pel   newPel;
	public  static SensorManager sensorManager;
	public  static SensorEventListener sensorEventListener;
	public  static SensorEventListener shakeSensorEventListener;
	public  static Sensor sensor;
	private int    responseCount=0;

	// 传感器常量，向TransformTouch中传递
	public static final int NOSENSOR = 0;// 没有传感器
	public static final int ACCELEROMETER = 1;// 加速度
	public static final int PROXIMITY =2; //逼近
	public static final int ORIENTATION = 3;// 方向
	public static int sensorMode=NOSENSOR;

	//摇一摇
	private Vibrator vibrator;
	private static boolean  sharkWaiting=false;
	
	//辅助数据
	private static Matrix transMatrix=new Matrix(); //存放变换后因子的零时变量
	private static Matrix savedMatrix=new Matrix();
	private Pel    savedPel;
	private Step   step;
	private PointF centerPoint=new PointF();
	private String savedPath;
	/**************************************************************************************/
	//拍照
    private static final int REQUEST_CODE_NONE = 0;   
    private static final int REQUEST_CODE_GRAPH = 1;//拍照
    private static final int REQUEST_CODE_PICTURE = 2; //缩放
    private static final String IMAGE_UNSPECIFIED = "image/*";
	/**************************************************************************************/
    //语音识别    
    private RecognizerDialog isrDialog;
    private final String APP_ID="514fb8d7";
    private String said;
	/**************************************************************************************/
    //雪花飞舞
    private static RecordThread recordThread = null;
    /**************************************************************************************/
	//进度对话框必要组件
	private ProgressDialog progressDialog;
	final Handler loadInBitmapHandler=new Handler() //载入图片线程消息处理者
	{
		public void handleMessage(Message msg)
		{	
			try
			{
				progressDialog.dismiss();
				canvasVi.setBackgroundBitmap((Bitmap)msg.getData().getParcelable("loadedBitmap"));
	
			    super.handleMessage(msg);
			}
			catch(Exception e){}
		} 
	};
	
	//摇一摇
    final Handler shakeHandler = new Handler() 
    {  
		public void handleMessage(Message msg)
		{	
			try
			{
				onClearBtn(null);
			    super.handleMessage(msg);
			}
			catch(Exception e){}
		}  
    }; 
    /**************************************************************************************/	
	protected void onCreate(Bundle savedInstanceState) 
	{			
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	
	
	/*
	 * 自定义成员函数
	 */
	
	//初始化组件
	public void initView()
	{
		setContentView(R.layout.activity_main);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);//抽屉
//		drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //关闭手势滑动
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.drawable.btn_selectpel_normal,0x5555,0x5555)
        {
            public void onDrawerOpened(View drawerView)
            {
        	    ensurePelbarClosed();
                ensureCanvasbgbarClosed();
                setToolsClickable(false);
            }
			public void onDrawerClosed(View drawerView) 
			{
				if(curFragmentFlag == MAIN_FRAGMENT && topToolbarSclVi.getVisibility() == View.VISIBLE)
				{
					setToolsClickable(true);
				}
			}            
        };
        drawerLayout.setDrawerListener(drawerToggle);//为抽屉注册监听实现类防止popwindow的驻留
					
		//根据id关联基本原始组件
		canvasVi=(CanvasView)findViewById(R.id.vi_canvas);
		extendBtn=(Button)findViewById(R.id.btn_extend);
		openPelbarBtn=(Button)findViewById(R.id.btn_openpelbar);
		topToolbarSclVi=(View)findViewById(R.id.sclvi_toptoolbar);
		downToolbarSclVi=(View)findViewById(R.id.sclvi_downtoolbar);
		undoBtn=(Button)findViewById(R.id.btn_undo);
		redoBtn=(Button)findViewById(R.id.btn_redo);
		transbarLinlayout=(View)findViewById(R.id.linlay_transbar);
		colorBtn=(Button)findViewById(R.id.btn_color);
		int[] btnIds=new int[]{R.id.btn_openpelbar,R.id.btn_opentransbar,R.id.btn_opencrossfill,
				R.id.btn_opencanvasbgbar,R.id.btn_openprocessingbar,R.id.btn_opendrawtext,R.id.btn_opendrawpicture,
				R.id.btn_opendrawer,R.id.btn_color,R.id.btn_pen,R.id.btn_clear,R.id.btn_save,
				R.id.btn_undo,R.id.btn_redo};
		allBtns=new View[btnIds.length];
		for(int i=0;i<btnIds.length;i++)
			allBtns[i]=(View)findViewById(btnIds[i]);
				
		//构造弹出式窗体
		//图元箱\变换箱\浏览箱\背景箱\填拷删箱
		View pelbarVi=this.getLayoutInflater().inflate(R.layout.popwin_pelbar, null);
		pelbarPopwin=new PopupWindow(pelbarVi,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		View canvasbgbarVi=this.getLayoutInflater().inflate(R.layout.popwin_canvasbgbar, null);
		canvasbgbarPopwin=new PopupWindow(canvasbgbarVi,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);			
	
		//根据id初始化关联选中的组件
		curToolVi=openPelbarBtn;//初始化选中图元按钮
		curPelVi=(ImageView)pelbarVi.findViewById(R.id.btn_freehand);//初始化选中自由手绘按钮
		curCanvasbgVi=whiteCanvasbgVi=(ImageView)canvasbgbarVi.findViewById(R.id.btn_canvasbg0);//初始化选中“黄纸背景”按钮
		/**************************************************************************************/
		// 得到传感器服务
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorEventListener=new singleHandSensorEventListener();
		
		//摇一摇
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE); 
		shakeSensorEventListener=new shakeSensorEventListener();
		sensorManager.registerListener(shakeSensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
		/**************************************************************************************/
		
		//对话框
		penDialog = new PenDialog(MainActivity.this,R.style.GraffitiDialog);
		colorpickerDialog = new ColorpickerDialog(MainActivity.this,R.style.GraffitiDialog);
		
		/**************************************************************************************/	
		//语音识别对话框
		isrDialog = new RecognizerDialog(this,"appid="+APP_ID);
		/**************************************************************************************/	
	}
	
	//初始化数据
	public void initData()
	{ 
        //事先生成图片被存储的文件夹  
        File file = new File(Environment.getExternalStorageDirectory().getPath()+softDir);
        if(!file.exists())
          file.mkdirs();

		//获取屏幕宽高
		WindowManager wm = this.getWindowManager();
		SCREEN_WIDTH=wm.getDefaultDisplay().getWidth();
		SCREEN_HEIGHT=wm.getDefaultDisplay().getHeight();
		
		context=MainActivity.this;
		/**************************************************************************************/	
		//数据结构
		pelList = CanvasView.getPelList();
		undoStack=CanvasView.getUndoStack();
		redoStack=CanvasView.getRedoStack();
		/**************************************************************************************/	
		//语音识别
		said="";
		isrDialog.setEngine("sms",null,null);
		isrDialog.setListener(this);
		/**************************************************************************************/
		//雪花飞舞
		blowHandler= new BlowHandler();
		canvasVi.LoadSnowImage();
		canvasVi.SetView(SCREEN_WIDTH, SCREEN_HEIGHT);
		/**************************************************************************************/
		//设置静音
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0); 
	}
	
/**
* 按钮事件
*/	
	
	/**
	 * Open/Close布局按钮
	 */
	
	//打开抽屉
	public void onOpenDrawerBtn(View v)
	{
		ensurePelbarClosed();
		ensureCanvasbgbarClosed();
	
		drawerLayout.openDrawer(Gravity.LEFT);
	}

	//打开编辑个人信息界面
	public void onEnterMeBtn(View v)
	{
		boolean hasLogined = true; //访问服务端检查是否已登录%%%%%%%%%%%%%%%%
		if(hasLogined) //已登录
		{
			Intent intent = new Intent(); // 绑定主活动
			intent.setClass(MainActivity.this, MeActivity.class);
			startActivity(intent);
		}
		else //未登录
		{
			Intent intent = new Intent(); // 绑定主活动
			intent.setClass(MainActivity.this, LoginActivity.class);
			startActivity(intent);
		}
	}


	//打开绘图社区
	public void onOpenCommunityBtn(View v)
	{
		boolean hasLogined = true; //访问服务端检查是否已登录%%%%%%%%%%%%%%%%
		if(hasLogined) //已登录
		{
			Intent intent = new Intent(); // 绑定主活动
			intent.setClass(MainActivity.this, CommunityActivity.class);
			startActivity(intent);
		}
		else //未登录
		{
			Intent intent = new Intent(); // 绑定主活动
			intent.setClass(MainActivity.this, LoginActivity.class);
			startActivity(intent);
		}
	}

	//打开返回画布
	public void onCloseDrawerBtn(View v)
	{
		if(curFragmentFlag != MAIN_FRAGMENT)
		{
			curFragmentFlag = MAIN_FRAGMENT;
			getSupportFragmentManager().beginTransaction().remove(curFragment).commit();
			if(canvasVi.getVisibility() != View.VISIBLE)
				canvasVi.setVisibility(View.VISIBLE);
		}
		drawerLayout.closeDrawer(Gravity.LEFT);
	}		
	
	//打开马上分享
	public void onOpenShareBtn(View v) throws IOException
	{
    	String tmpPath=Environment.getExternalStorageDirectory().getPath()+ "/tmp"+".jpg";	
		Bitmap bitmap=CanvasView.getSavedBitmap();
		
        FileOutputStream fileOutputStream = new FileOutputStream(tmpPath);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.close();
        Toast.makeText(MainActivity.this, "图片已保存("+tmpPath+")", Toast.LENGTH_SHORT).show();
        
        //跳转至分享定位器
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_TEXT, "#分享自单手涂鸦(Free Graffiti)#");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Intent.EXTRA_STREAM,android.net.Uri.parse(tmpPath));
		startActivity(Intent.createChooser(intent, null));
	}
	
	//打开我的画廊
	public void onOpenGalleryBtn(View v)
	{
		if(curFragmentFlag != GALLERY_FRAGMENT)
		{
			curFragmentFlag = GALLERY_FRAGMENT;
			curFragment = new GalleryFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, curFragment).commit();
		}
		drawerLayout.closeDrawer(Gravity.LEFT);
	}
	
	//打开帮助
	public void onOpenHelpBtn(View v)
	{
		if(curFragmentFlag != HELP_FRAGMENT)
		{
			curFragmentFlag = HELP_FRAGMENT;
			curFragment = new HelpFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, curFragment).commit();
		}
		drawerLayout.closeDrawer(Gravity.LEFT);
		Toast.makeText(MainActivity.this, "往下翻还有内容哦~", Toast.LENGTH_SHORT).show();
	}
	
	//打开工具箱
	public void onOpenToolsBtn(View v)
	{
		ensurePelFinished();//确保图形已经完全画好
		
		if(sensorMode != NOSENSOR) //取消注册传感器、包装好步骤step存入undo栈、敲定该次步骤图元
		{
			ensureSensorFinished();
		}
		else
		{
			openTools();
		}
	}
	
	//关闭工具箱
	public static void closeTools()
	{
		ensurePelbarClosed();
		ensureCanvasbgbarClosed();
		clearRedoStack();//清空重做栈
		
		if(curToolVi.getId() == R.id.btn_opentransbar
				||curToolVi.getId() == R.id.btn_opencrossfill
				||curPelVi.getId() == R.id.btn_keepdrawing)
			extendBtn.setVisibility(View.VISIBLE);
		else
			extendBtn.setVisibility(View.GONE);
		
		Animation downDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.downdisappear);  		
		Animation topDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.topdisappear);  			
		Animation leftDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.leftdisappear);  
		Animation rightDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.rightdisappear);  
		
		downToolbarSclVi.startAnimation(downDisappearAnim);
		topToolbarSclVi.startAnimation(topDisappearAnim);
		undoBtn.startAnimation(rightDisappearAnim);	
		redoBtn.startAnimation(leftDisappearAnim);
		
		downToolbarSclVi.setVisibility(View.GONE);
		topToolbarSclVi.setVisibility(View.GONE);
		undoBtn.setVisibility(View.GONE);
		redoBtn.setVisibility(View.GONE);
		
		setToolsClickable(false);
		
		//无传感器注册
		if(sensorMode == NOSENSOR)
		{
			if(curToolVi.getId() == R.id.btn_opentransbar) //若为选中模式
			{
				Animation leftAppearAnim = AnimationUtils.loadAnimation(context, R.anim.leftappear);  
				transbarLinlayout.setVisibility(View.VISIBLE); //显示变换箱
				transbarLinlayout.startAnimation(leftAppearAnim);
			}
			else if(curPelVi.getId() == R.id.btn_brokenline
					||curPelVi.getId() == R.id.btn_polygon)
			{
				extendBtn.setVisibility(View.VISIBLE);
				extendBtn.setBackgroundResource(R.drawable.btn_extend_normal);
			}
		}
	}
	
	//打开图形条
	public void onOpenPelbarBtn(View v)
	{
		ensureCanvasbgbarClosed();
		updateToolbarIcons(v);//更新工具条图标显示
		
		if(pelbarPopwin.isShowing())//如果悬浮栏打开
			pelbarPopwin.dismiss();//关闭	
		else
			pelbarPopwin.showAtLocation(downToolbarSclVi,Gravity.BOTTOM, 0, downToolbarSclVi.getHeight());//打开悬浮窗

		//按下也要注册当前选中图元的touch
		switch(curPelVi.getId())
		{
			case R.id.btn_rect:	     	CanvasView.setTouch(new DrawRectTouch());break;
			case R.id.btn_bessel:    	CanvasView.setTouch(new DrawBesselTouch());break;
			case R.id.btn_oval:      	CanvasView.setTouch(new DrawOvalTouch());break;
			case R.id.btn_polygon:   	CanvasView.setTouch(new DrawPolygonTouch());break;
			case R.id.btn_brokenline:	CanvasView.setTouch(new DrawBrokenlineTouch());break;
			case R.id.btn_line:      	CanvasView.setTouch(new DrawLineTouch());break;
			case R.id.btn_freehand:  	CanvasView.setTouch(new DrawFreehandTouch());break;
			case R.id.btn_keepdrawing:  CanvasView.setTouch(new KeepDrawingTouch());break;
		}
	}
	
	//打开工具箱
	public static void openTools()
	{
		if(transbarLinlayout.getVisibility() == View.VISIBLE) //如果变换箱为打开状态
			transbarLinlayout.setVisibility(View.GONE);//关闭
				
		//弹出上下工具栏的动画
		Animation downAppearAnim = AnimationUtils.loadAnimation(context, R.anim.downappear);  		
		Animation topAppearAnim = AnimationUtils.loadAnimation(context, R.anim.topappear);  
		Animation leftAppearAnim = AnimationUtils.loadAnimation(context, R.anim.leftappear);	
		Animation rightAppearAnim = AnimationUtils.loadAnimation(context, R.anim.rightappear);	
		downToolbarSclVi.startAnimation(downAppearAnim);
		topToolbarSclVi.startAnimation(topAppearAnim);
		redoBtn.startAnimation(leftAppearAnim);	
		undoBtn.startAnimation(rightAppearAnim);
		downToolbarSclVi.setVisibility(View.VISIBLE);
		topToolbarSclVi.setVisibility(View.VISIBLE);
		undoBtn.setVisibility(View.VISIBLE);
		redoBtn.setVisibility(View.VISIBLE);
		setToolsClickable(true);
	}
	
	//打开变换条
	public void onOpenTransbarBtn(View v)
	{	
		ensurePelbarClosed();
		ensureCanvasbgbarClosed();
		updateToolbarIcons(v);
		closeTools();
		
		sensorManager.unregisterListener(sensorEventListener);// 取消上一个传感器的注册
		sensorMode = NOSENSOR;
		
		CanvasView.setTouch(new TransformTouch());
	}

	//打开背景条
	public void onOpenCanvasbgbarBtn(View v)
	{
		ensurePelbarClosed();
		if(canvasbgbarPopwin.isShowing())//如果悬浮栏打开
			canvasbgbarPopwin.dismiss();//关闭	
		else
			canvasbgbarPopwin.showAtLocation(downToolbarSclVi,Gravity.BOTTOM, 0, downToolbarSclVi.getHeight());//打开悬浮窗
	}

	//交叉填充
	public void onOpenCrossfillBtn(View v)
	{
		updateToolbarIcons(v);
		CanvasView.setTouch(new CrossfillTouch());
	}
	
	//切换到图片处理页面
	public void onOpenProcessingbarBtn(View v)
	{
		Intent intent = new Intent(); // 绑定主活动
		intent.setClass(MainActivity.this, ProcessingActivity.class);
		startActivity(intent);
	}

	public void onOpenDrawtextBtn(View v)
	{
		Intent intent = new Intent(); // 绑定主活动
		intent.setClass(MainActivity.this, DrawTextActivity.class);
		startActivity(intent);
	}
	
	public void onOpenDrawpictureBtn(View v)
	{
		Intent intent = new Intent(); // 绑定主活动
		intent.setClass(MainActivity.this, DrawPictureActivity.class);
		startActivity(intent);
	}
	/**
	 * 图形箱
	 */

	//画矩形（子）
	public void onRectBtn(View v)
	{
		updatePelbarIcons((ImageView)v);//加框去框、改变父菜单
		CanvasView.setTouch(new DrawRectTouch());
	}	
	
	//画贝塞尔（子）
	public void onBesselBtn(View v)
	{
		updatePelbarIcons((ImageView)v);	
		CanvasView.setTouch(new DrawBesselTouch());	
	}	
	
	//画椭圆（子）
	public void onOvalBtn(View v)
	{
		updatePelbarIcons((ImageView)v);			
		CanvasView.setTouch(new DrawOvalTouch());	
	}
	
	//画直线（子）
	public void onLineBtn(View v)
	{
		updatePelbarIcons((ImageView)v);	
		CanvasView.setTouch(new DrawLineTouch());
	}
	
	//画折线（子）
	public void onBrokenlineBtn(View v)
	{
		updatePelbarIcons((ImageView)v);	
		CanvasView.setTouch(new DrawBrokenlineTouch());
	}
	
	//自由手绘（子）
	public void onFreehandBtn(View v)
	{
		updatePelbarIcons((ImageView)v);		
		CanvasView.setTouch(new DrawFreehandTouch());
	}
	
	//画多边形（子）
	public void onPolygonBtn(View v)
	{
		updatePelbarIcons((ImageView)v);	
		CanvasView.setTouch(new DrawPolygonTouch());
	}
	
	//单手加速度作图
	public void onKeepdrawingBtn(View v)
	{
		updatePelbarIcons((ImageView)v);	
		CanvasView.setTouch(new KeepDrawingTouch());
		Toast.makeText(this, "选择起始点进行重力感应画图", Toast.LENGTH_SHORT).show();
	}
	
	//麦克风语音识别（子）
	public void onMicroBtn(View v)
	{
		closeTools();//自动关闭工具箱进入作图		
		isrDialog.show();//显示语音对话框
		Toast.makeText(this, "进入语音识图：人、花朵、太阳、房子、小草、笔、笑脸、指环", Toast.LENGTH_LONG).show();
	}
	
	//Gps定位画图（子）
	public void onGpsBtn(View v)
	{
		closeTools();//自动关闭工具箱进入作图
		Toast.makeText(this, "您现在可以通过行走的路线来画图哦~", Toast.LENGTH_LONG).show();

		Intent intent = new Intent();
		intent.setClass(MainActivity.this, GpsActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_in,R.anim.push_out);
	}	
	
	/**
	 * 变换箱
	 */
	//拷贝图元
	public void onCopypelBtn(View v)
	{		
		ensureSensorTransFinished();
		
		selectedPel = CanvasView.getSelectedPel();
		if(selectedPel != null)//选中了图元才能进行删除操作
		{		
			Pel pel=(Pel)(selectedPel).clone();//以选中图元为模型，拷贝一个新对象
			(pel.path).offset(10, 10);//偏移一定距离友好示意
			(pel.region).setPath(pel.path, CanvasView.getClipRegion());
			
			(pelList).add(pel);
			undoStack.push(new CopypelStep(pel));//将该“步”压入undo栈
			
			CanvasView.setSelectedPel(selectedPel=null);
			canvasVi.updateSavedBitmap();
		}
		else
		{
			Toast.makeText(MainActivity.this, "请先选中一个图形！", Toast.LENGTH_SHORT).show();
		}
	}
	
	//填充图元
	public void onFillpelBtn(View v)
	{
		ensureSensorTransFinished();
		
		selectedPel = CanvasView.getSelectedPel();	
		if(selectedPel != null)//选中了图元才能进行删除操作
		{
			Paint oldPaint=new Paint(selectedPel.paint);//设置旧画笔（undo用）

			(selectedPel.paint).set(DrawTouch.getCurPaint());//以当前画笔的色态作为选中画笔的
			if(selectedPel.closure == true)//封闭图形
				(selectedPel.paint).setStyle(Paint.Style.FILL);//填充区域
			else
				(selectedPel.paint).setStyle(Paint.Style.STROKE);//填充边框
			
			Paint newPaint=new Paint(selectedPel.paint);////设置新画笔（undo用）
			undoStack.push(new FillpelStep(selectedPel,oldPaint,newPaint));//将该“步”压入undo栈
			
			CanvasView.setSelectedPel(selectedPel=null);
			canvasVi.updateSavedBitmap();//填充了图元就自然更新缓冲画布
		}
		else
		{
			Toast.makeText(MainActivity.this, "请先选中一个图形！", Toast.LENGTH_LONG).show();
		}
	}	
		
	//删除图元
	public void onDeletepelBtn(View v)
	{		
		ensureSensorTransFinished();
		
		selectedPel = CanvasView.getSelectedPel();
		if(selectedPel != null)//选中了图元才能进行删除操作
		{
			undoStack.push(new DeletepelStep(selectedPel));//将该“步”压入undo栈
			(pelList).remove(selectedPel);
			
			CanvasView.setSelectedPel(selectedPel=null);
			canvasVi.updateSavedBitmap();//删除了图元就自然更新缓冲画布
		}
		else
		{
			Toast.makeText(MainActivity.this, "请先选中一个图形！", Toast.LENGTH_LONG).show();
		}
	}
	

/**
 * 更新图标
 */
	
	//更新图元箱相关图标
	public void updatePelbarIcons(ImageView v)
	{
		//去框、加框
		curPelVi.setImageDrawable(null);//上次选中的图元去框	
		v.setImageResource(R.drawable.bg_highlight_frame);//改变子菜单的图片（加框）
		curPelVi=v;//转接当前选中

		//修改父菜单图标
		int fatherDrawableId=0;
		switch(v.getId())
		{
			case R.id.btn_bessel:	  fatherDrawableId=R.drawable.btn_bessel_pressed;break;
			case R.id.btn_brokenline: fatherDrawableId=R.drawable.btn_brokenline_pressed;break;
			case R.id.btn_freehand:   fatherDrawableId=R.drawable.btn_freehand_pressed;break;
			case R.id.btn_line:		  fatherDrawableId=R.drawable.btn_line_pressed;break;
			case R.id.btn_oval:		  fatherDrawableId=R.drawable.btn_oval_pressed;break;
			case R.id.btn_polygon:	  fatherDrawableId=R.drawable.btn_polygon_pressed;break;
			case R.id.btn_rect:		  fatherDrawableId=R.drawable.btn_rect_pressed;break;
			case R.id.btn_keepdrawing:fatherDrawableId=R.drawable.btn_keepdrawing_pressed;break;
		}
		
		final Drawable fatherDrawable = getResources().getDrawable(fatherDrawableId);
		curToolVi.setCompoundDrawablesWithIntrinsicBounds(null, fatherDrawable , null, null);
	}
	
	//更新画布背景箱相关图标
	public void updateCanvasbgAndIcons(ImageView v)
	{
		//去框、加框
		curCanvasbgVi.setImageDrawable(null);//上次选中的图元去框	
		v.setImageResource(R.drawable.bg_highlight_frame);//改变子菜单的图片（加框）
		curCanvasbgVi=v;//转接当前选中
		
		int backgroundDrawable=0;
		switch(v.getId())
		{
			case R.id.btn_canvasbg0:backgroundDrawable=R.drawable.bg_canvas0;break;
			case R.id.btn_canvasbg1:backgroundDrawable=R.drawable.bg_canvas1;break;
			case R.id.btn_canvasbg2:backgroundDrawable=R.drawable.bg_canvas2;break;
			case R.id.btn_canvasbg3:backgroundDrawable=R.drawable.bg_canvas3;break;
			case R.id.btn_canvasbg4:backgroundDrawable=R.drawable.bg_canvas4;break;
			case R.id.btn_canvasbg5:backgroundDrawable=R.drawable.bg_canvas5;break;
			case R.id.btn_canvasbg6:backgroundDrawable=R.drawable.bg_canvas6;break;
			case R.id.btn_canvasbg7:backgroundDrawable=R.drawable.bg_canvas7;break;
			default:return;
		
		}
		canvasVi.setBackgroundBitmap(backgroundDrawable);
	}
	
	//更新工具条相关图标
	public void updateToolbarIcons(View v)
	{
		Button btn=(Button)v;
		//变白、变蓝(筛选图片资源)
		int lastDrawableId=0;//上次选中的按钮需变回的图片
		switch(curToolVi.getId())
		{
			case R.id.btn_openpelbar:
			{
				//之前曾选中了哪个图形，就以那个图形的蓝色底作为背景
				switch(curPelVi.getId())
				{
					case R.id.btn_bessel:	  lastDrawableId=R.drawable.btn_bessel_normal;break;
					case R.id.btn_brokenline: lastDrawableId=R.drawable.btn_brokenline_normal;break;
					case R.id.btn_freehand:	  lastDrawableId=R.drawable.btn_freehand_normal;break;
					case R.id.btn_line:		  lastDrawableId=R.drawable.btn_line_normal;break;
					case R.id.btn_oval:		  lastDrawableId=R.drawable.btn_oval_normal;break;
					case R.id.btn_polygon:    lastDrawableId=R.drawable.btn_polygon_normal;break;
					case R.id.btn_rect:		  lastDrawableId=R.drawable.btn_rect_normal;break;
					case R.id.btn_keepdrawing:lastDrawableId=R.drawable.btn_keepdrawing_normal;break;
				}
			}break;//图元
			case R.id.btn_opentransbar:  lastDrawableId=R.drawable.btn_selectpel_normal;break;//选中
			case R.id.btn_opencrossfill:lastDrawableId=R.drawable.btn_crossfill_normal;break;//浏览
		}		
			
		int nextDrawableId=0;//刚才按下的按钮将要变成的图片
		switch(v.getId())
		{
			case R.id.btn_openpelbar:
			{
				//之前曾选中了哪个图形，就以那个图形的蓝色底作为背景
				switch(curPelVi.getId())
				{
					case R.id.btn_bessel:	  nextDrawableId=R.drawable.btn_bessel_pressed;break;
					case R.id.btn_brokenline: nextDrawableId=R.drawable.btn_brokenline_pressed;break;
					case R.id.btn_freehand:	  nextDrawableId=R.drawable.btn_freehand_pressed;break;
					case R.id.btn_line:		  nextDrawableId=R.drawable.btn_line_pressed;break;
					case R.id.btn_oval:		  nextDrawableId=R.drawable.btn_oval_pressed;break;
					case R.id.btn_polygon:    nextDrawableId=R.drawable.btn_polygon_pressed;break;
					case R.id.btn_rect:		  nextDrawableId=R.drawable.btn_rect_pressed;break;
					case R.id.btn_keepdrawing:nextDrawableId=R.drawable.btn_keepdrawing_pressed;break;
				}
			}break;//图元
			case R.id.btn_opentransbar:  nextDrawableId=R.drawable.btn_selectpel_pressed;break;//选中
			case R.id.btn_opencrossfill: nextDrawableId=R.drawable.btn_crossfill_pressed;break;//交叉填充
		}
		
		final Drawable lastDrawable = getResources().getDrawable(lastDrawableId);
		curToolVi.setCompoundDrawablesWithIntrinsicBounds(null, lastDrawable , null, null);
		curToolVi.setTextColor(Color.WHITE);
		
		final Drawable nextDrawable = getResources().getDrawable(nextDrawableId);
		btn.setCompoundDrawablesWithIntrinsicBounds(null, nextDrawable , null, null);
		btn.setTextColor(Color.parseColor("#0099CC"));

		curToolVi=btn;//转接当前选中
	}

/**
 * 确保正确关闭和完成
 */
	//确保悬浮图形条关闭
	private static void ensurePelbarClosed()
	{
		if(pelbarPopwin.isShowing())//如果悬浮栏打开
			pelbarPopwin.dismiss();//关闭	
	}
	
	//确保悬浮背景条关闭
	private static void ensureCanvasbgbarClosed()
	{
		if(canvasbgbarPopwin.isShowing())
			canvasbgbarPopwin.dismiss();//关闭
	}
	
	//确保未画完的图元能够真正敲定
	private void ensurePelFinished()
	{	
		Touch touch=CanvasView.getTouch();
		String className=touch.getClass().getSimpleName();	
		selectedPel = CanvasView.getSelectedPel();
		
		if(selectedPel != null)
		{					
			//使人为敲定图元的操作(贝塞尔、折线、多边形)
			if(className.equals("DrawBesselTouch"))
			{
				touch.control=true;
				touch.up();
			}
			else if(className.equals("DrawBrokenlineTouch"))
			{
				touch.hasFinished=true;
				touch.up();
			}
			else if(className.equals("DrawPolygonTouch"))
			{
				(touch.curPoint).set(touch.beginPoint);
				touch.up();
			}
			else //单纯选中
			{
				CanvasView.setSelectedPel(null);//失去焦点
				canvasVi.updateSavedBitmap();//重绘位图
			}
		}
	}
	
	private void ensureSensorTransFinished()
	{
		//是否是传感器平移、缩放、旋转途中点击的
		if(sensorMode != NOSENSOR)
		{
			// 取消当前传感器的注册
			sensorManager.unregisterListener(sensorEventListener);// 取消上一个传感器的注册
			sensorMode = NOSENSOR;
			
			//selectedPel的路径组成的区域敲定
			(selectedPel.region).setPath(selectedPel.path, CanvasView.getClipRegion());
			
			//undo栈处理
			step.setToUndoMatrix(transMatrix);//设置进行该次步骤后的变换因子
			undoStack.push(step);//将该“步”压入undo栈
		}
	}
	
	private void ensureSensorFinished() //保证传感器关闭
	{
		if(curToolVi.getId() == R.id.btn_opentransbar) //变换图元
		{
			decideTranspel();
		}
		else //单手作图
		{
			decideKeepdrawing();
		}

		if(recordThread != null)
		{
			recordThread.stopRecord(); //终止音频监听进程
			recordThread=null;
		}
		
		sharkWaiting=false;//重新开启摇一摇监听
		extendBtn.setBackgroundResource(R.drawable.btn_extend_normal);//消去ok按钮
	}
	
//	private void setToolsInClickable()//抽屉打开后禁止上下所有按钮
//	{
//		((Button)findViewById(R.id.btn_openpelbar)).setClickable(false);
//		((Button)findViewById(R.id.btn_opentransbar)).setClickable(false);
//		((Button)findViewById(R.id.btn_opencrossfill)).setClickable(false);
//		((Button)findViewById(R.id.btn_opencanvasbgbar)).setClickable(false);
//		((Button)findViewById(R.id.btn_openprocessingbar)).setClickable(false);
//		
//		((Button)findViewById(R.id.btn_opendrawer)).setClickable(false);
//		((Button)findViewById(R.id.btn_color)).setClickable(false);
//		((Button)findViewById(R.id.btn_pen)).setClickable(false);
//		((Button)findViewById(R.id.btn_clear)).setClickable(false);
//		((Button)findViewById(R.id.btn_save)).setClickable(false);
//		
//		extendBtn.setClickable(false);
//	}
//	
//	private void setToolsClickable() //抽屉关闭后解放上下所有按钮
//	{
//		((Button)findViewById(R.id.btn_openpelbar)).setClickable(true);
//		((Button)findViewById(R.id.btn_opentransbar)).setClickable(true);
//		((Button)findViewById(R.id.btn_opencrossfill)).setClickable(true);
//		((Button)findViewById(R.id.btn_opencanvasbgbar)).setClickable(true);
//		((Button)findViewById(R.id.btn_openprocessingbar)).setClickable(true);
//		
//		((Button)findViewById(R.id.btn_opendrawer)).setClickable(true);
//		((Button)findViewById(R.id.btn_color)).setClickable(true);
//		((Button)findViewById(R.id.btn_pen)).setClickable(true);
//		((Button)findViewById(R.id.btn_clear)).setClickable(true);
//		((Button)findViewById(R.id.btn_save)).setClickable(true);
//
//		extendBtn.setClickable(true);
//	}
	
	private void decideTranspel()
	{
		// 取消当前传感器的注册
		sensorManager.unregisterListener(sensorEventListener);// 取消上一个传感器的注册
		sensorMode = NOSENSOR;
		extendBtn.setBackgroundResource(R.drawable.btn_extend_normal);
		
		//selectedPel的路径组成的区域敲定
		(selectedPel.region).setPath(selectedPel.path, CanvasView.getClipRegion());
		
		//undo栈处理
		step.setToUndoMatrix(transMatrix);//设置进行该次步骤后的变换因子
		undoStack.push(step);//将该“步”压入undo栈
		
		//屏幕
		CanvasView.setSelectedPel(null);//失去焦点
		canvasVi.updateSavedBitmap();//重绘位图
	}
	
	private void decideKeepdrawing()
	{
		// 取消当前传感器的注册
		sensorManager.unregisterListener(sensorEventListener);// 取消上一个传感器的注册
		sensorMode = NOSENSOR;
		extendBtn.setBackgroundResource(R.drawable.btn_extend_normal);
		
		//敲定该图元的路径，区域，画笔,名称
		(newPel.region).setPath(newPel.path, CanvasView.getClipRegion());
		(newPel.paint).set(DrawTouch.getCurPaint());

		/**
		 * 更新操作
		 */
		
		//1.将新画好的图元存入图元链表中
		pelList.add(newPel);
		
		//2.包装好当前步骤 内的操作
		undoStack.push(new DrawpelStep(newPel));//将该“步”压入undo栈

		//3.更新重绘位图
		CanvasView.setSelectedPel(selectedPel = null);//刚才画的图元失去焦点
		canvasVi.updateSavedBitmap();//重绘位图	
	}
	
	//清空重做栈
	public static void clearRedoStack()
	{
		if(!redoStack.empty())//redo栈不空
			redoStack.clear();//清空redo栈
	}
	
/**
 * 传感器相关
 */	
	/**
	 *单手传感器模式
	 */
	
	/**
	 * 注册加速度传感器
	 */
	
	//平移图元
	public void onTranslatepelBtn(View v)
	{
		registerTranspelSensor(v);
	}

	/**
	 * 注册逼近传感器
	 */
	//缩放图元
	public void onZoompelBtn(View v)
	{
		registerTranspelSensor(v);
	}
	
	/**
	 * 注册方向传感器
	 */
	//旋转图元
	public void onRotatepelBtn(View v)
	{
		registerTranspelSensor(v);
	}	
	
	/**
	 * 设置传感器
	 */
	//单手加速度作图传感器
	public static void registerKeepdrawingSensor(View v)
	{
		sharkWaiting=true;//关闭摇一摇功能响应
		
		newPel=new Pel();
		newPel.closure=true;
		lastPoint.set(CanvasView.getTouch().curPoint);
		(newPel.path).moveTo(lastPoint.x,lastPoint.y);
		
		sensorMode = ACCELEROMETER; //加速度注册标志
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		//开启飘花线程和加速度传感器监听
		registerSnowThread();
		sensorManager.registerListener(sensorEventListener, sensor,SensorManager.SENSOR_DELAY_GAME);
		extendBtn.setBackgroundResource(R.drawable.selector_ok);
	}
	
	//变换图元传感器
	public void registerTranspelSensor(View v)
	{
		sharkWaiting=true;//关闭摇一摇功能响应
		
		selectedPel = CanvasView.getSelectedPel();
		savedPel = new Pel();
		if(selectedPel != null) //选中了图元后才可以注册传感器
		{
			//提前捕获该选中图元的初始路径，初始因子
			(savedPel.path).set(selectedPel.path);
			savedMatrix.set(TransformTouch.calPelSavedMatrix(savedPel));
			
			//其它操作
			step=new TransformpelStep(selectedPel);//由初始图元创建变换型步骤
			
			//传感器相关
			sensorManager.unregisterListener(sensorEventListener);// 取消上一个传感器的注册
			//由v决定注册哪个传感器
			switch(v.getId())
			{
				case R.id.btn_translatepel:
				{			
					sensorMode = ACCELEROMETER;
					sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

					Toast.makeText(this, "摆动手机", Toast.LENGTH_SHORT).show();
					Toast.makeText(this, "吹一吹即可停止", Toast.LENGTH_SHORT).show();
				}break;
				case R.id.btn_zoompel:
				{
					sensorMode = PROXIMITY;
					sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
					
					//再次确认缩放中心
					centerPoint.set(TransformTouch.calPelCenterPoint(selectedPel));				
					
					Toast.makeText(this, "前后侧地翻转手机", Toast.LENGTH_SHORT).show();
					Toast.makeText(this, "吹一吹即可停止", Toast.LENGTH_SHORT).show();
				}break;
				case R.id.btn_rotatepel:
				{
					sensorMode = ORIENTATION;
					sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

					//再次确认缩放中心
					centerPoint.set(TransformTouch.calPelCenterPoint(selectedPel));
					
					Toast.makeText(this, "左右侧地翻转手机", Toast.LENGTH_SHORT).show();
					Toast.makeText(this, "吹一吹即可停止", Toast.LENGTH_SHORT).show();
				}break;
			}	
			registerSnowThread();
			sensorManager.registerListener(sensorEventListener, sensor,SensorManager.SENSOR_DELAY_GAME);
			extendBtn.setBackgroundResource(R.drawable.selector_ok); //变勾图标
		}
		else
		{
			Toast.makeText(MainActivity.this, "请先选中一个图形！", Toast.LENGTH_SHORT).show();
		}
	}
	
	//注册飘雪线程
	private static void registerSnowThread()
	{
		if(recordThread == null) //飘雪线程未启动
		{
			recordThread = new RecordThread(blowHandler, 1); // 点击按钮，启动线程
			recordThread.start();
			canvasVi.setStatus(false);
		}
	}
	
	//单手操作传感器监听者
	class singleHandSensorEventListener implements SensorEventListener
	{
		public void onSensorChanged(SensorEvent event) //传感器变化
		{	
			if(curToolVi.getId() == R.id.btn_opentransbar) //变换图元
			{	
				if (sensorMode == ACCELEROMETER) //平移
				{
					float dx = -50 * event.values[0];
					float dy =  90 * event.values[1];
					
					transMatrix.set(savedMatrix);
					transMatrix.postTranslate(dx,dy);
				}  
				else if (sensorMode == PROXIMITY) //缩放
				{
					float scale = -event.values[1]/30;
					
					transMatrix.set(savedMatrix);
					transMatrix.postScale(scale, scale,centerPoint.x,centerPoint.y);
				}
				else if (sensorMode == ORIENTATION) //旋转
				{
					float degree= event.values[2]*4;
			
					transMatrix.set(savedMatrix);
					transMatrix.setRotate(degree,centerPoint.x,centerPoint.y);	
				}

				if(selectedPel != null)
				{
					(selectedPel.path).set(savedPel.path);
					(selectedPel.path).transform(transMatrix);// 作用于图元
		
					canvasVi.invalidate();//刷新
				}
			}
			else if(curToolVi.getId() == R.id.btn_openpelbar)//单手加速度作图
			{
				if(responseCount % 2 == 0)
				{	
					float dx = -event.values[0];
					float dy =  event.values[1];
					PointF nowPoint=new PointF(lastPoint.x+dx,lastPoint.y+dy);

					(newPel.path).quadTo(lastPoint.x,lastPoint.y, (lastPoint.x+nowPoint.x)/2, (lastPoint.y+nowPoint.y)/2);
					lastPoint.set(nowPoint);

					CanvasView.setSelectedPel(selectedPel = newPel);
					canvasVi.invalidate();//刷新
				}
				responseCount++;
			}
		}
		public void onAccuracyChanged(Sensor sensor, int accuracy) //精度
		{	
		}
	}
	
	//单手操作传感器监听者
	class shakeSensorEventListener implements SensorEventListener
	{
		public void onSensorChanged(SensorEvent event) //传感器变化
		{	
			if(sharkWaiting == false)
			{
	           float[] values = event.values;  
	            float x = values[0]; // x轴方向的重力加速度，向右为正   
	            float y = values[1]; // y轴方向的重力加速度，向前为正   
	            float z = values[2]; // z轴方向的重力加速度，向上为正   
	          
	            // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。   
	            int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了   
	            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {  
	            	sharkWaiting=true;//正在等待用户选择是否清空
	            	vibrator.vibrate(200);   
	                shakeHandler.sendEmptyMessage(0);  
	            } 
			}
		}
		public void onAccuracyChanged(Sensor sensor, int accuracy) //精度
		{	
		}
	}
	
/**
 * 中间部分按钮
 */
	//撤销
	public void onUndoBtn(View v)
	{
		if(!undoStack.empty())//非浏览模式，且栈不为空
		{		
			Step step=undoStack.pop();//从undo栈弹出栈顶
			step.toRedoUpdate();//调用栈顶步骤的更新方法
			redoStack.push(step);//将栈顶转移进redo栈
		}
	}	
	
	//重做
	public void onRedoBtn(View v)
	{	
		if(!redoStack.empty())//非浏览模式，且栈不为空
		{
			Step step=redoStack.pop(); //从redo栈弹出栈顶
			step.toUndoUpdate();//调用栈顶步骤的更新方法
			undoStack.push(step);//将栈顶转移进undo栈
		}
	}	
	
	//清空
	public void onClearBtn(View v)
	{
		//弹出再次确认对话框
		class okClick implements DialogInterface.OnClickListener
		{
			public void onClick(DialogInterface dialog, int which) //ok
			{
				clearData();
				sharkWaiting=false;
			}		
		}		
		class cancelClick implements DialogInterface.OnClickListener //cancel
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				sharkWaiting=false;
			}		
		}
		
		//实例化确认对话框
		Builder dialog=new AlertDialog.Builder(MainActivity.this);
		dialog.setIcon(drawable.ic_dialog_info);
		dialog.setMessage("您确定要清空？");
		dialog.setPositiveButton("确定", new okClick());
		dialog.setNegativeButton("取消", new cancelClick());
		dialog.create();
		dialog.show();
	}
	//清空内部所有数据
	public void clearData()
	{
		pelList.clear();
		undoStack.clear();
		redoStack.clear();
		CanvasView.setSelectedPel(null);//若有选中的图元失去焦点
		updateCanvasbgAndIcons(whiteCanvasbgVi);//画布背景图标复位
		canvasVi.setBackgroundBitmap();//清除填充过颜色的地方
	}
	//笔触
	public void onPenBtn(View v)
	{
		penDialog.show();
	}
	
	//调色板
	public void onColorBtn(View v)
	{
		colorpickerDialog.show();
		colorpickerDialog.picker.setOldCenterColor(DrawTouch.getCurPaint().getColor()); //获取当前画笔的颜色作为左半圆颜色
	}
	
	//保存
	public void onSaveBtn(final View v)
	{
		final EditText editTxt=new EditText(MainActivity.this);//作品的名称编辑框
		//弹出编辑对话框
		class okClick implements DialogInterface.OnClickListener
		{
			public void onClick(DialogInterface dialog, int which) //ok
			{
		        try 
		        {
		        	savedPath=Environment.getExternalStorageDirectory().getPath()+softDir + "/"+editTxt.getText().toString()+".jpg";
		        	File file=new File(savedPath);
		        	
		        	if(!file.exists()) //文件不存在
		        	{
		        		Bitmap bitmap=CanvasView.getSavedBitmap();
			            FileOutputStream fileOutputStream = new FileOutputStream(savedPath);
			            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
			            fileOutputStream.close();
			            
			            Toast.makeText(MainActivity.this, "图片已保存("+savedPath+")", Toast.LENGTH_SHORT).show();
		        	}
		        	else //文件存在
		        	{   
			            //询问用户是否覆盖提示框
			    		Builder dialog1=new AlertDialog.Builder(MainActivity.this);
			    		dialog1.setIcon(drawable.ic_dialog_info);
			    		dialog1.setMessage("该名称已存在，是否覆盖？");
			    		dialog1.setPositiveButton("覆盖", new OnClickListener()
			    		{
							public void onClick(DialogInterface dialog, int which) 
							{
								try 
								{
					        		Bitmap bitmap=CanvasView.getSavedBitmap();
						            FileOutputStream fileOutputStream = new FileOutputStream(savedPath);
						            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
						            try 
						            {
										fileOutputStream.close();
							            Toast.makeText(MainActivity.this, "图片已保存("+savedPath+")", Toast.LENGTH_SHORT).show();
									} catch (IOException e) 
									{
										e.printStackTrace();
									}
								} 
								catch (FileNotFoundException e) 
								{
									e.printStackTrace();
								}
							}		
			    		});
			    		dialog1.setNegativeButton("取消", new OnClickListener()
			    		{
							public void onClick(DialogInterface dialog, int which) 
							{
							}		
			    		});
			    		dialog1.create();
			    		dialog1.show();
		        	}
		        } 
		        catch (Exception e) 
		        {
		        	e.printStackTrace();
		        }
			}
		}		
		class cancelClick implements DialogInterface.OnClickListener //cancel
		{
			public void onClick(DialogInterface dialog, int which) 
			{
			}		
		}
		
		//实例化确认对话框
		Builder dialog=new AlertDialog.Builder(MainActivity.this);
		dialog.setMessage("给您的作品取个名字吧~");
		dialog.setView(editTxt);
		dialog.setPositiveButton("保存", new okClick());
		dialog.setNegativeButton("取消", new cancelClick());
		dialog.create();
		dialog.show();
/*******************************************************************************/		
	}	
	
	//换背景
	public void onCanvasbg0Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}	
	
	public void onCanvasbg1Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}
	
	public void onCanvasbg2Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}
	
	public void onCanvasbg3Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}
	
	public void onCanvasbg4Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}
	
	public void onCanvasbg5Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}
	
	public void onCanvasbg6Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}
	
	public void onCanvasbg7Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}
	
	//图库
	public void onCanvasbg8Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
		
        Intent intent = new Intent(Intent.ACTION_PICK, null);                
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);    
        startActivityForResult(intent, REQUEST_CODE_PICTURE);

	}
	
	//拍照
	public void onCanvasbg9Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
		
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);                
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"temp.jpg"))); 
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.name());
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, Configuration.ORIENTATION_LANDSCAPE);
        startActivityForResult(intent, REQUEST_CODE_GRAPH);

	}
	
	//打开图库、拍照完毕后需要执行抉择的动作
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) 
	{
		//程序初始化时调用
        if (resultCode == REQUEST_CODE_NONE)            
        	return;        
        
        // 触发拍照模式的“确定”、“取消”、“返键”按钮后       
        if (requestCode == REQUEST_CODE_GRAPH) 
        {   
    		//进度对话框
    		progressDialog=new ProgressDialog(com.noahark.graffiti.ui.activity.MainActivity.getContext());
    		progressDialog.setMessage("正在载入，请稍等...");
    		progressDialog.show();
    		
        	new Thread(new Runnable()
        	{
				public void run() 
				{
					try 
					{
						//获取图片
						File file=new File(Environment.getExternalStorageDirectory()+ "/temp.jpg");
						FileInputStream fis = new FileInputStream(file);
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inJustDecodeBounds = false;
						opts.inSampleSize = 2;
						Bitmap photo = BitmapFactory.decodeStream(fis, null, opts);
									
						//传递位图消息并刷新
						Bundle data=new Bundle();
						data.putParcelable("loadedBitmap", photo);
						Message msg=new Message();
						msg.setData(data);
						loadInBitmapHandler.sendMessage(msg);
						
						//删除图片
						file.delete();
					}
					catch (Exception e) //图片不存在异常
					{
						e.printStackTrace();
					} 
				}	
        	}).start();
        }        
        
        // 触发图库模式的“选择”、“返键”按钮后       
        if (requestCode == REQUEST_CODE_PICTURE) 
        {    
    		//进度对话框
    		progressDialog=new ProgressDialog(com.noahark.graffiti.ui.activity.MainActivity.getContext());
    		progressDialog.setMessage("正在载入，请稍等...");
    		progressDialog.show();
    		
        	new Thread(new Runnable()
        	{
				public void run() 
				{
					try 
					{
						//获取选择的图片
						Uri uri=data.getData();
						File file=new File(uri.getPath());
						FileInputStream fis = new FileInputStream(file);
						Bitmap pic = BitmapFactory.decodeStream(fis);
						fis.close();
						
						//传递位图消息并刷新
						Bundle data=new Bundle();
						data.putParcelable("loadedBitmap", pic);
						Message msg=new Message();
						msg.setData(data);
						loadInBitmapHandler.sendMessage(msg);
					}
					catch (Exception e) //图片不存在异常
					{
						//获取选择的图片路径
						Uri uri = data.getData();
						String[] filePathColumn = { MediaStore.Images.Media.DATA };
						Cursor cursor = getContentResolver().query(uri,
						filePathColumn, null, null, null);
						cursor.moveToFirst();
						int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
						String picturePath = cursor.getString(columnIndex);
						cursor.close();
						
						Options op = new Options(); 
						op.inJustDecodeBounds = true; 
						Bitmap pic = BitmapFactory.decodeFile(picturePath, op); 
						int xScale = op.outWidth / CanvasView.getCanvasWidth(); 
						int yScale = op.outHeight / CanvasView.getCanvasHeight(); 
						op.inSampleSize = xScale > yScale ? xScale : yScale; 
						op.inJustDecodeBounds = false; 
						pic = BitmapFactory.decodeFile(picturePath, op); 	
						
						//传递位图消息并刷新
						Bundle data=new Bundle();
						data.putParcelable("loadedBitmap", pic);
						Message msg=new Message();
						msg.setData(data);
						loadInBitmapHandler.sendMessage(msg);
					}
				}	
        	}).start();
        }  
		super.onActivityResult(requestCode, resultCode, data);
	}
	/*************************************************************************************/	
/**
 * 辅助方法
 */
	/**
	 * get()
	 */
	public static int getSensorMode()
	{
		return sensorMode;
	}

	public static CanvasView getCanvasView()
	{
		return canvasVi;
	}
	
	public static Context getContext()
	{
		return context;
	}

	/**
	 * back键彻底退出应用
	 */	
	public boolean onKeyDown(int keyCode, KeyEvent event) // 捕获View的按键事件
	{
		//按下反键
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			if(hasExitAppDialog == false) //没有退出对话框
			{
				//弹出再次确认对话框
				class okClick implements DialogInterface.OnClickListener
				{
					public void onClick(DialogInterface dialog, int which) //ok
					{
						android.os.Process.killProcess(android.os.Process.myPid());//杀死进程
						MainActivity.this.onDestroy();//摧毁活动
						System.exit(0);//返回系统
					}		
				}		
				class cancelClick implements DialogInterface.OnClickListener //cancel
				{
					public void onClick(DialogInterface dialog, int which) 
					{
					}		
				}
				
				//实例化确认对话框
				dialog=new AlertDialog.Builder(MainActivity.this);
				dialog.setIcon(drawable.ic_dialog_info);
				dialog.setMessage("您确定要退出？");
				dialog.setPositiveButton("确定", new okClick());
				dialog.setNegativeButton("取消", new cancelClick());
				dialog.create();
				dialog.show();
			}
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && sensorMode == NOSENSOR) //音量增大键响应撤销
		{
			onUndoBtn(null);
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP && sensorMode == NOSENSOR)//音量减小键响应重做
		{
			onRedoBtn(null);
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_MENU) //菜单键弹出工具箱
		{
			if(topToolbarSclVi.getVisibility() == View.VISIBLE)
				closeTools();
			else
				onOpenToolsBtn(null);
		}
		return super.onKeyDown(keyCode, event);//返回上级页面继续处理onkeydown事件
	}
	/**********************************************************************************/
	/**
	 * 语音识别
	 */
	//一个说话周期
	@Override
	public void onResults(ArrayList<RecognizerResult> results, boolean isLast) 
	{
		said+=results.get(0).text;
	}
	
	//按下“说完了”按钮
	@Override
	public void onEnd(SpeechError error) 
	{			
		//识别出said的形状并画图	
		Pattern pattern=new Pattern();
		if(!said.equals("") && said != null)
		{
			if(said.equals("人。"))
			{
				Pel recognizedPel=pattern.drawMan();
				addRecognizedPel(recognizedPel);
			}
			else if(said.equals("花朵。"))
			{
				Pel recognizedPel=pattern.drawFlower();
				addRecognizedPel(recognizedPel);
			}
			else if(said.equals("太阳。"))
			{
				Pel recognizedPel=pattern.drawSun();
				addRecognizedPel(recognizedPel);
			}
			else if(said.equals("房子。"))
			{
				Pel recognizedPel=pattern.drawHouse();
				addRecognizedPel(recognizedPel);
			}
			else if(said.equals("小草。"))
			{
				Pel recognizedPel=pattern.drawGrass();
				addRecognizedPel(recognizedPel);
			}
			else if(said.equals("笔。"))
			{
				Pel recognizedPel=pattern.drawHouse();
				addRecognizedPel(recognizedPel);
			}
			else if(said.equals("笑脸。"))
			{
				Pel recognizedPel=pattern.drawSmileFace();
				addRecognizedPel(recognizedPel);
			}
			else if(said.equals("指环。"))
			{
				Pel recognizedPel=pattern.drawRing();
				addRecognizedPel(recognizedPel);
			}			
			else
			{
				Toast.makeText(this, "抱歉，刚才没明白您的意思",Toast.LENGTH_SHORT).show();
			}

			//清空字串
			said="";
		}
	}
	public void addRecognizedPel(Pel recognizedPel)
	{
		/**
		 * 公共操作
		 */
		//包装图元
		(recognizedPel.region).setPath(recognizedPel.path, CanvasView.getClipRegion());
		(recognizedPel.paint).set(DrawTouch.getCurPaint());
		
		//更新数据
		pelList.add(recognizedPel);//加入链表		
		undoStack.push(new DrawpelStep(recognizedPel));//将该“步”压入undo栈
		
		//更新画布
		CanvasView.setSelectedPel(selectedPel = null);//刚才画的图元失去焦点
		canvasVi.updateSavedBitmap();//重绘位图
	}
	/**********************************************************************************/
	public void onOpenTransChildren(View v)
	{
		View parentBtn=(View)findViewById(R.id.btn_opentranschildren);
		
		View deletepelBtn=(View)findViewById(R.id.btn_deletepel);
		View copypelBtn=(View)findViewById(R.id.btn_copypel);
		View fillpelBtn=(View)findViewById(R.id.btn_fillpel);
		View rotatepelBtn=(View)findViewById(R.id.btn_rotatepel);
		View zoompelBtn=(View)findViewById(R.id.btn_zoompel);
		View translatepelBtn=(View)findViewById(R.id.btn_translatepel);
		
		if(deletepelBtn.getVisibility() == View.GONE)
		{
			parentBtn.setBackgroundDrawable(null);
			parentBtn.setBackgroundResource(R.drawable.btn_arrow_close);
	
			deletepelBtn.setVisibility(View.VISIBLE);
			copypelBtn.setVisibility(View.VISIBLE);
			fillpelBtn.setVisibility(View.VISIBLE);			
			rotatepelBtn.setVisibility(View.VISIBLE);
			zoompelBtn.setVisibility(View.VISIBLE);
			translatepelBtn.setVisibility(View.VISIBLE);
		}
		else
		{
			parentBtn.setBackgroundDrawable(null);
			parentBtn.setBackgroundResource(R.drawable.btn_arrow_open);
			
			deletepelBtn.setVisibility(View.GONE);
			copypelBtn.setVisibility(View.GONE);
			fillpelBtn.setVisibility(View.GONE);	
			rotatepelBtn.setVisibility(View.GONE);
			zoompelBtn.setVisibility(View.GONE);
			translatepelBtn.setVisibility(View.GONE);
		}
	}
	
	public void onBackBtn(View v)
	{
		drawerLayout.openDrawer(Gravity.LEFT);
	}
	/**********************************************************************/
	/**
	 * 雪花飞舞
	 */
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
					recordThread.stopRecord();
					update();
					break;
				case 2:
					if(canvasVi.getStatus()) //雪花未出界
					{
						canvasVi.invalidate();
						sleep(100);
					}
					else //雪花飘完
					{
						if(curToolVi.getId() == R.id.btn_opentransbar)
						{
							decideTranspel();//敲定图元
						}
						else
						{
							decideKeepdrawing();//敲定单手作图
						}
						
						sharkWaiting=false; //继续进入摇一摇监听
						recordThread.stopRecord(); //终止音频监听进程
						recordThread=null;
					}
					break;
				default:
					break;
			}
		}
	};
	//雪花飞舞
	public void update() 
	{
		canvasVi.addRandomSnow();
		canvasVi.setStatus(true);
		blowHandler.sleep(200);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
	}
	
	public static void setToolsClickable(boolean bool)
	{	
		for(int i=0;i<allBtns.length;i++)
			allBtns[i].setClickable(bool);
	}
}
