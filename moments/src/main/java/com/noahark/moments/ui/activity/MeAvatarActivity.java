package com.noahark.moments.ui.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.noahark.moments.R;

import me.relex.photodraweeview.PhotoDraweeView;

public class MeAvatarActivity extends Activity {

    private PhotoDraweeView mDetailedAvatarVi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_avatar);

        initView();
    }

    public void  initView()
    {

        mDetailedAvatarVi = (PhotoDraweeView)findViewById(R.id.detailavatar_imgvi);
        mDetailedAvatarVi.setPhotoUri(Uri.parse("http://news.shangqiuw.com/upload/News/2016-3-15/2016315123740276kdc9d.jpg"));
    }

    public void onCloseMeAvatarBtn(View view){finish();}
}
