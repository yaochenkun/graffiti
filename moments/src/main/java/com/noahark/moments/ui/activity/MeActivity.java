package com.noahark.moments.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.noahark.moments.R;

import butterknife.BindView;

public class MeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
    }

    public void onCloseMeBtn(View view)
    {
        finish();
    }


    public void onEnterMeAvatarBtn(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this,MeAvatarActivity.class);
        startActivity(intent);
    }

    public void onEnterMeNicknameBtn(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this,MeNicknameActivity.class);
        startActivity(intent);
    }

    public void onEnterMeQRCodeBtn(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this,MeQRCodeActivity.class);
        startActivity(intent);
    }

    public void onEnterMeSexBtn(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this,MeSexActivity.class);
        startActivity(intent);
    }

    public void onEnterMeDistrictBtn(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this,MeDistrictActivity.class);
        startActivity(intent);
    }

    public void onEnterMeMottoBtn(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this,MeMottoActivity.class);
        startActivity(intent);
    }

    public void onLogoutBtn(View view)
    {

    }
}
