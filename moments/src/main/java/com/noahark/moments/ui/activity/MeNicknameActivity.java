package com.noahark.moments.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.noahark.moments.R;

public class MeNicknameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_nickname);
    }

    public void onCloseMeNicknameBtn(View view)
    {
        finish();
    }
}
