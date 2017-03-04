package com.noahark.moments.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.noahark.moments.R;

public class MeSexActivity extends Activity {

    private String sex = "M";
    private ImageView maleCheckedImgVi;
    private ImageView femaleCheckedImgVi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_sex);

        maleCheckedImgVi = (ImageView)findViewById(R.id.sex_male_imgvi);
        femaleCheckedImgVi = (ImageView)findViewById(R.id.sex_female_imgvi);
    }

    public void onCloseMeSexBtn(View view)
    {
        finish();
    }

    public void onCheckMaleRadiobtn(View view)
    {
        sex="M";
        maleCheckedImgVi.setVisibility(View.VISIBLE);
        femaleCheckedImgVi.setVisibility(View.INVISIBLE);
    }

    public void onCheckFemaleRadiobtn(View view)
    {
        sex="F";
        maleCheckedImgVi.setVisibility(View.INVISIBLE);
        femaleCheckedImgVi.setVisibility(View.VISIBLE);
    }
}
