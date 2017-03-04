package com.noahark.moments.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.noahark.moments.R;

public class MeQRCodeActivity extends Activity {

    private SimpleDraweeView mAvatarImgVi;
    private SimpleDraweeView mQRCodeImgVi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_qrcode);

        initView();
    }

    public void  initView()
    {
        mAvatarImgVi = (SimpleDraweeView)findViewById(R.id.avatar_qrcode_imgvi);
        mQRCodeImgVi = (SimpleDraweeView)findViewById(R.id.qrcode_imgvi);

        mAvatarImgVi.setImageURI("http://ent.taiwan.cn/list/201403/W020140321365741707073.jpg");
        mQRCodeImgVi.setImageURI("http://image.9928.tv/Mobile/admin/20151124/20151124160303495.jpg");
    }

    public void onCloseMeQrcodeBtn(View view){finish();}
}
