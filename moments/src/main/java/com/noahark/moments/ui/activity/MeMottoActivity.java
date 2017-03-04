package com.noahark.moments.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.noahark.moments.R;

public class MeMottoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_motto);
    }

    public void onCloseMeMottoBtn(View view)
    {
        finish();
    }
}
