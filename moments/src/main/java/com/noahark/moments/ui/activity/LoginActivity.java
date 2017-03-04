package com.noahark.moments.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.noahark.moments.R;
import com.noahark.moments.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends Activity {
    @BindView(R2.id.edittext_login_username) EditText mUsernameEditText;
    @BindView(R2.id.edittext_login_password) EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    public void initView()
    {

    }

    public void initData()
    {

    }

    //??????
    public void onLoginBtn(View view)
    {
//        Intent intent = new Intent(); // ??????
//        intent.setClass(this, MeActivity.class);
//        startActivity(intent);
        finish();// ????Activity
        overridePendingTransition(R.anim.push_in,R.anim.push_out);
    }

    //??????????
    public void onEnterRegisterBtn(View view)
    {
        Intent intent = new Intent(); // ??????
        intent.setClass(this, RegisterActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_in,R.anim.push_out);
    }

    public void onUsernameEditClearBtn(View view) {
        mUsernameEditText.setText("");
    }

    public void onPasswordEditClearBtn(View view) {
        mPasswordEditText.setText("");
    }
}
