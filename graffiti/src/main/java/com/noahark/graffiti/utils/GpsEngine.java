package com.noahark.graffiti.utils;

import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

/**
 * Created by chicken on 2016/11/18.
 */
public class GpsEngine {


    public static boolean m_bKeyRight = true;
    public static BMapManager mBMapManager = null;

    /**
     * 百度地图Gps导航引擎
     */
    public static void initGpsEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(new MyGeneralListener())) {
            Toast.makeText(GraffitiApplication.getInstance().getApplicationContext(),
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
    }

    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    public static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(GraffitiApplication.getInstance().getApplicationContext(), "您的网络出错啦！",
                        Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(GraffitiApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            //非零值表示key验证未通过
            if (iError != 0) {
                //授权Key错误：
                Toast.makeText(GraffitiApplication.getInstance().getApplicationContext(),
                        "请检查您的网络连接是否正常!", Toast.LENGTH_SHORT).show();
                m_bKeyRight = false;
            }
            else{
                m_bKeyRight = true;
                Toast.makeText(GraffitiApplication.getInstance().getApplicationContext(),
                        "key认证成功", Toast.LENGTH_LONG).show();
            }
        }
    }

}
