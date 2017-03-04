package com.noahark.graffiti.ui.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.ui.view.GalleryView;

import android.R.drawable;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class GalleryFragment extends Fragment implements
		AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {
	private String softDir="/FreeGraffiti";
	private List<String> ImageList;// sd卡中的图片路径
	private int screenWidth;
	private int screenHeight;
	public static Bitmap bmp;
	private ImageAdapter adapter;
	GalleryView g;
	private int NUM = 100;// 可以用来分享的图片总数
	private String[] path = new String[NUM];
	ImageView iv;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gallery, container, false);

		// 屏幕宽高****************
		screenWidth = (int) (MainActivity.SCREEN_WIDTH/1.5); // 屏幕宽（像素，如：480px）要在MainAcivity里面单独捕获
		screenHeight = (int) (MainActivity.SCREEN_HEIGHT/1.5); // 屏幕高（像素，如：800p）

		/**
		 * 取得SD卡上的图片文件,并将图片完整路径保存到ImageList中
		 */
		ImageList = getInSDPhoto();

		/**
		 * 将取得的路径集合ImageList转换成数组并存入list中 List集合中的toArray()方法经常用在集合与数组转换的
		 */
		g = (GalleryView) view.findViewById(R.id.mygallery);
		g.setFocusable(true);
		adapter = new ImageAdapter(view.getContext(), ImageList);
		if (adapter.getCount() != 0) { // 当图片链表非空时加载适配器
			g.setAdapter(adapter);
		}

		return view;
	}

	private List<String> getInSDPhoto() {

		/**
		 * 设定图片所在路径
		 */
		List<String> it = new ArrayList<String>();
		String photosPath=Environment.getExternalStorageDirectory().getPath()+softDir + "/";
		
		File f = new File(photosPath);
		File[] files = f.listFiles();

		/**
		 * 将所有文件存入ArrayList中,这个地方存的还是文件路径
		 */
		for (int i = 0; i < files.length; i++) 
		{
			File file = files[i];
			path[i] = file.getPath();
			if (getAllImage(path[i]))
				it.add(path[i]);
		}
		if(files.length == 0)
			Toast.makeText(MainActivity.getContext(),"画廊里暂时还没有您的作品哦",Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(MainActivity.getContext(), "长按图片可以分享或载入哦~", Toast.LENGTH_SHORT).show();
		
		return it;
	}

	private boolean getAllImage(String fName) {
		boolean re;

		/* 取得扩展名 */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* 按扩展名的类型决定MimeType */
		if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			re = true;
		} else {
			re = false;
		}

		return re;
	}

	// 适配器
	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;
		private List<String> lis;

		public ImageAdapter(Context context, List<String> list) {
			mContext = context;
			lis = list;

			TypedArray a = mContext.obtainStyledAttributes(R.styleable.Gallery);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			// 让对象的styleable属性能够反复使用
			a.recycle();
		}

		public int getCount() {
			return lis.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		/* 重写的方法getView,传并几View对象 */
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			iv = new ImageView(mContext);
			// 使用图片工厂载入并设置图片
			bmp = BitmapFactory.decodeFile(lis.get(position).toString());
			iv.setImageBitmap(bmp);

			/**
			 * 设定ImageView宽高 试了下ScaleType中除FIT_XY以外的其它选项，效果图分别如果下：
			 */
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			// 重新设定Layout的宽高
			iv.setLayoutParams(new Gallery.LayoutParams(4 * screenWidth / 5,
					4 * screenHeight / 5));
			// 设定Gallery背景图
			iv.setBackgroundResource(mGalleryItemBackground);
			// 传回imageView对象,画廊的item长按监听
			g.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						final int selection, long arg3) {
					
					//弹出对话框让用户选择是 分享or 载入图片 or 取消
					class shareClick implements DialogInterface.OnClickListener
					{
						public void onClick(DialogInterface dialog, int which) //分享
						{
							Intent intent = new Intent(Intent.ACTION_SEND);
							intent.setType("image/*");
							intent.putExtra(Intent.EXTRA_TEXT, "#分享自单手涂鸦(Free Graffiti)#");
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra(Intent.EXTRA_STREAM,android.net.Uri.parse(path[selection]));
							startActivity(Intent.createChooser(intent, null));
						}		
					}	
					class inputClick implements DialogInterface.OnClickListener //载入图片
					{
						public void onClick(DialogInterface dialog, int which) 
						{
							Bitmap inputBitmap=null;
							try 
							{
								File file=new File(path[selection]);
								FileInputStream fis;
								fis = new FileInputStream(file);
								Bitmap bmp = BitmapFactory.decodeStream(fis);
								inputBitmap=bmp.copy(Config.ARGB_8888, true);
								CanvasView.ensureBitmapRecycled(bmp);
								fis.close();
							} 
							catch (FileNotFoundException e) 
							{
								e.printStackTrace();
							} catch (IOException e) {
								// TODO 自动生成的 catch 块
								e.printStackTrace();
							}

							//刷新背景
							CanvasView canvasVi=MainActivity.getCanvasView();
							canvasVi.setBackgroundBitmap(inputBitmap);
							
							Toast.makeText(MainActivity.getContext(),"已将该图片成功载入到画布",Toast.LENGTH_SHORT).show();
						}		
					}
					class cancelClick implements DialogInterface.OnClickListener //取消
					{
						public void onClick(DialogInterface dialog, int which) 
						{
						}		
					}
					
					//实例化确认对话框
					Builder dialog=new AlertDialog.Builder(MainActivity.getContext());
					dialog.setIcon(drawable.ic_dialog_info);
					dialog.setMessage("您要对该图片进行什么操作？");
					dialog.setPositiveButton("分享", new shareClick());
					dialog.setNeutralButton("载入", new inputClick());
					dialog.setNegativeButton("取消", new cancelClick());
					dialog.create();
					dialog.show();
			
					return false;
				}
			});

			return iv;

		}
	}

	@Override
	public View makeView() {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
}
