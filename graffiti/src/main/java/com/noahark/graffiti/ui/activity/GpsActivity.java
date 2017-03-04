package com.noahark.graffiti.ui.activity;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.utils.GpsEngine;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GpsActivity extends Activity
{
    private enum E_BUTTON_TYPE
    {
        LOC, COMPASS, FOLLOW
    }

    GpsEngine mGpsEngine;
    private E_BUTTON_TYPE mCurBtnType;

    // 路径
    private static Path path = new Path();
    // 定位相关
    LocationClient mLocClient;
    LocationData locData = null;
    public MyLocationListenner myListener = new MyLocationListenner();

    // 定位图层
    MyLocationOverlay myLocationOverlay = null;

    // 如果不处理touch事件，则无需继承，直接使用MapView即可
    MapView mMapView = null; // 地图View
    private MapController mMapController = null;

    // UI相关
    OnCheckedChangeListener radioButtonListener = null;
    Button requestLocButton = null;
    boolean isRequest = false;// 是否手动触发请求定位
    boolean isFirstLoc = true;// 是否首次定位

    // 绘制图层
    private GraphicsOverlay graphicsOverlay;

    // 新旧经纬度
    private double lastLong;
    private double lastLati;
    private double newLong;
    private double newLati;

    // 临时变量，修复百度bug
    private double tempLati;
    private double tempLong;

    private String softDir="/FreeGraffiti";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /**
         * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */

        if (GpsEngine.mBMapManager == null) {
            GpsEngine.mBMapManager = new BMapManager(getApplicationContext());
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */

            GpsEngine.mBMapManager.init(new GpsEngine.MyGeneralListener());
        }

        setContentView(R.layout.relay_gps);
        requestLocButton = (Button) findViewById(R.id.btn_setlocation);
        mCurBtnType = E_BUTTON_TYPE.LOC;
        OnClickListener btnClickListener = new OnClickListener()
        {
            public void onClick(View v)
            {
                switch (mCurBtnType)
                {
                    case LOC:
                        // 手动定位请求
                        requestLocClick();
                        break;
                    case COMPASS:
                        myLocationOverlay.setLocationMode(LocationMode.NORMAL);
                        requestLocButton.setText("定位");
                        mCurBtnType = E_BUTTON_TYPE.LOC;
                        break;
                    case FOLLOW:
                        myLocationOverlay.setLocationMode(LocationMode.COMPASS);
                        requestLocButton.setText("罗盘");
                        mCurBtnType = E_BUTTON_TYPE.COMPASS;
                        break;
                }
            }
        };
        requestLocButton.setOnClickListener(btnClickListener);

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(false);

        graphicsOverlay = new GraphicsOverlay(mMapView);
        mMapView.getOverlays().add(graphicsOverlay);

        // 定位初始化
        mLocClient = new LocationClient(this);
        locData = new LocationData();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        // 定位图层初始化
        myLocationOverlay = new MyLocationOverlay(mMapView);
        // 设置定位数据
        myLocationOverlay.setData(locData);
        // 添加定位图层
        mMapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableCompass();
        // 修改定位数据后刷新图层生效
        mMapView.refresh();

        /****************************************************************/
        // 地图视图操作
        mMapView.regMapViewListener(mGpsEngine.mBMapManager, new MKMapViewListener() {

            @Override
            public void onClickMapPoi(MapPoi arg0) {
                // TODO 自动生成的方法存根

            }

            @Override
            public void onGetCurrentMap(Bitmap bmp)
            {
                try
                {
                    // 刷新背景
                    CanvasView canvasVi = MainActivity.getCanvasView();

                    Bitmap clipedBitmap=bmp.copy(Config.ARGB_8888, true);
                    CanvasView.ensureBitmapRecycled(bmp);

                    canvasVi.setBackgroundBitmap(clipedBitmap);
                    canvasVi.updateSavedBitmap();
                    Toast.makeText(MainActivity.getContext(), "已将当前地图作为画布背景",Toast.LENGTH_SHORT).show();
                }
                catch(Exception e)
                {
                    //如果发生异常无法载入图片则只保存
                    try
                    {
                        // 保存到SDcard
                        String mapPath = Environment.getExternalStorageDirectory().getPath() + softDir + "/" +getTimeName()+ ".jpg";
                        Bitmap bitmap = CanvasView.getSavedBitmap();
                        FileOutputStream fileOutputStream;
                        fileOutputStream = new FileOutputStream(mapPath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100,fileOutputStream);
                        fileOutputStream.close();
                        Toast.makeText(MainActivity.getContext(), "地图已保存(" + mapPath + ")",Toast.LENGTH_SHORT).show();
                    }
                    catch (FileNotFoundException e1)
                    {
                        e1.printStackTrace();
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void onMapAnimationFinish()
            {
            }

            @Override
            public void onMapLoadFinish()
            {
            }

            @Override
            public void onMapMoveFinish()
            {
            }

        });
        /****************************************************************/
    }

    /**
     * 手动触发一次定位请求
     */
    public void requestLocClick()
    {
        isRequest = true;
        mLocClient.requestLocation();
        Toast.makeText(GpsActivity.this, "正在定位……", Toast.LENGTH_SHORT).show();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener
    {

        @Override
        public void onReceiveLocation(BDLocation location)
        {
            if (location == null)
                return;

            // 得到最新的经纬度
            newLati = locData.latitude = location.getLatitude();
            newLong = locData.longitude = location.getLongitude();

            // 折线
            path.lineTo((float) newLati, (float) newLong);
            // 如果不显示定位精度圈，将accuracy赋值为0即可
            locData.accuracy = location.getRadius();
            // 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
            locData.direction = location.getDerect();
            // 更新定位数据
            myLocationOverlay.setData(locData);

            // 在绘制图层上绘制折线
            graphicsOverlay.setData(drawLine());

            // 更新图层数据执行刷新后生效
            mMapView.refresh();
            // 是手动触发请求或首次定位时，移动到定位点
            if (isRequest || isFirstLoc)
            {
                // 百度自带的动态移动
                mMapController.animateTo(new GeoPoint(
                        (int) (locData.latitude * 1e6),
                        (int) (locData.longitude * 1e6)));

                // 首次定位的经纬度
                tempLati = lastLong = locData.longitude;
                tempLong = lastLati = locData.latitude;
                // 将第一个点移动到首次定位点
                path.moveTo((float) lastLati, (float) lastLong);

                isRequest = false;
                myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
                requestLocButton.setText("跟随");
                mCurBtnType = E_BUTTON_TYPE.FOLLOW;
            }
            // 首次定位完成
            // isFirstLoc = false;
        }

        private Graphic drawLine()
        {
            double mLat = lastLati;
            double mLon = lastLong;

            int lat = (int) (mLat * 1e6);
            int lon = (int) (mLon * 1e6);
            GeoPoint pt1 = new GeoPoint(lat, lon);

            if (isFirstLoc) {
                mLat = tempLati;
                mLon = tempLong;
                // 首次定位完成
                isFirstLoc = false;
            } else {
                mLat = newLati;
                mLon = newLong;
            }
            lat = (int) (mLat * 1e6);
            lon = (int) (mLon * 1e6);
            GeoPoint pt2 = new GeoPoint(lat, lon);

            // 构建线
            Geometry lineGeometry = new Geometry();
            // 设定折线点坐标
            GeoPoint[] linePoints = new GeoPoint[2];
            linePoints[0] = pt1;
            linePoints[1] = pt2;
            lineGeometry.setPolyLine(linePoints);

            // 新值变旧值
            lastLong = newLong;
            lastLati = newLati;

            // 设定样式
            Symbol lineSymbol = new Symbol();
            Symbol.Color lineColor = lineSymbol.new Color();
            lineColor.red = 139;
            lineColor.green = 0;
            lineColor.blue = 255;
            lineColor.alpha = 255;
            lineSymbol.setLineSymbol(lineColor, 10);
            // 生成Graphic对象
            Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
            return lineGraphic;
        }

        public void onReceivePoi(BDLocation poiLocation)
        {
            if (poiLocation == null)
            {
                return;
            }
        }
    }

    @Override
    protected void onPause()
    {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        // 退出时销毁定位
        if (mLocClient != null)
            mLocClient.stop();
        mMapView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        mMapView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    public void onCancelBtn(View v)
    {
        class okClick implements DialogInterface.OnClickListener // ok
        {
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        }

        class cancelClick implements DialogInterface.OnClickListener // cancel
        {
            public void onClick(DialogInterface dialog, int which)
            {
            }
        }

        // 实例化确认对话框
        Builder dialog = new AlertDialog.Builder(GpsActivity.this);
        dialog.setIcon(drawable.ic_dialog_info);
        dialog.setMessage("您确定要离开定位导航页面？");
        dialog.setPositiveButton("确定", new okClick());
        dialog.setNegativeButton("取消", new cancelClick());
        dialog.create();
        dialog.show();
    }

    /****************************************************************/
    public void onClipScreenBtn(View v)
    {
        mMapView.getCurrentMap(); // 截取当前地图,回调其onGetCurrentMap()方法
    }

    /****************************************************************/
    //获取当前时间
    public static String getTimeName()
    {
        String rel = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        return rel;
    }

}
