package com.noahark.moments.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.noahark.moments.R;

public class UserNoteSettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notesetting);
    }

    public void onCloseUserNoteSettingBtn(View view)
    {
        finish();
    }


}
