package com.noahark.moments.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.noahark.moments.R;

public class RegisterActivity extends Activity {

    private EditText mCreateUsernameEditText;
    private EditText mCreatePasswordEditText;
    private EditText mConfirmPasswordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        initData();
    }

    public void initView() {
        mCreateUsernameEditText = (EditText) findViewById(R.id.edittext_register_username);
        mCreatePasswordEditText = (EditText) findViewById(R.id.edittext_register_password);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.edittext_register_confirmpassword);
    }

    public void initData() {

    }


    //注册帐号
    public void onRegisterBtn(View view) {

    }

    //返回登录页面
    public void onBackToLoginBtn(View view) {
        Intent intent = new Intent(); // 绑定主活动
        intent.setClass(this, LoginActivity.class);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.push_in, R.anim.push_out);
    }

    public void onCreateUsernameEditClearBtn(View view) {
        mCreateUsernameEditText.setText("");
    }

    public void onCreatePasswordEditClearBtn(View view) {
        mCreatePasswordEditText.setText("");
    }

    public void onConfirmPasswordEditClearBtn(View view) {
        mConfirmPasswordEditText.setText("");
    }
}
