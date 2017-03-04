package com.noahark.moments.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.noahark.moments.R;

public class UserSnapshotActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_snapshot);
        initView();
        initData();
    }

    public void initView(){

        SimpleDraweeView avatarImgVi = (SimpleDraweeView) findViewById(R.id.usersnapshot_avatar_imgvi);
        avatarImgVi.setImageURI("http://p3.gexing.com/G1/M00/D7/CA/rBABE1HicgrhkBrUAAATcz-kWeg656_200x200_3.jpg");

        SimpleDraweeView zonePhotoImgVi1 = (SimpleDraweeView) findViewById(R.id.usersnapshot_zonephoto_imgvi1);
        zonePhotoImgVi1.setImageURI("http://h.hiphotos.baidu.com/zhidao/pic/item/08f790529822720ee31b9c4c7ecb0a46f31fabab.jpg");

        SimpleDraweeView zonePhotoImgVi2 = (SimpleDraweeView) findViewById(R.id.usersnapshot_zonephoto_imgvi1);
        zonePhotoImgVi2.setImageURI("http://img.bimg.126.net/photo/EYXAZ97XP0uwCn_B47gZMQ==/3944590323639952873.jpg");

        SimpleDraweeView zonePhotoImgVi3 = (SimpleDraweeView) findViewById(R.id.usersnapshot_zonephoto_imgvi1);
        zonePhotoImgVi3.setImageURI("http://image.tianjimedia.com/uploadImages/2014/196/28/IC969PU12K2P_1000x500.jpg");
    }

    public void initData(){

    }

    public void onCloseUserSnapshotBtn(View view)
    {
        finish();
    }

    public void onEnterUserNoteSettingBtn(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this,UserNoteSettingActivity.class);
        startActivity(intent);
    }

    public void onEnterUserZoneBtn(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this,UserZoneActivity.class);
        startActivity(intent);
    }
}
