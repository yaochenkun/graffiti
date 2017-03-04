package com.noahark.graffiti.utils;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by chicken on 2016/11/18.
 */
public class GraffitiApplication extends Application{

    private static GraffitiApplication mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        Fresco.initialize(this); //Fresco图片异步加载工具
//        GpsEngine.initGpsEngineManager(this); //百度地图gps
    }


    //获取单例对象
    public static GraffitiApplication getInstance() {
        return mInstance;
    }
}
