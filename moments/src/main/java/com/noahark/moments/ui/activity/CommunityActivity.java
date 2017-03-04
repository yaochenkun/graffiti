package com.noahark.moments.ui.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

import com.noahark.moments.R;

import butterknife.BindView;

public class CommunityActivity extends TabActivity {

    private TabHost mTabHost;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    public void initView()
    {
        setContentView(R.layout.activity_community);

        //tabhost相关配置,缓存4个标签页面，以便可在一个页面中跳转
        mTabHost = this.getTabHost();

        //聊天
        Intent intent = new Intent().setClass(this,ChatActivity.class);
        TabHost.TabSpec tabSpec = mTabHost.newTabSpec("chat").setIndicator("chat").setContent(intent);
        mTabHost.addTab(tabSpec);

        //关注
        intent = new Intent().setClass(this,RelationActivity.class);
        tabSpec = mTabHost.newTabSpec("relation").setIndicator("relation").setContent(intent);
        mTabHost.addTab(tabSpec);

        //动态
        intent = new Intent().setClass(this,CircleActivity.class);
        tabSpec = mTabHost.newTabSpec("circle").setIndicator("circle").setContent(intent);
        mTabHost.addTab(tabSpec);

        //发现
        intent = new Intent().setClass(this,FindActivity.class);
        tabSpec = mTabHost.newTabSpec("find").setIndicator("find").setContent(intent);
        mTabHost.addTab(tabSpec);

        //初始选中
        mTabHost.setCurrentTabByTag("chat");
    }

    public void initData()
    {

    }

    //进入聊天页
    public void onChatBtn(View view)
    {
        mTabHost.setCurrentTabByTag("chat");
    }

    //进入画友页（关系）
    public void onRelationBtn(View view)
    {
        mTabHost.setCurrentTabByTag("relation");
    }

    //进入动态页
    public void onCircleBtn(View view)
    {
        mTabHost.setCurrentTabByTag("circle");
    }

    //进入发现页
    public void onFindBtn(View view)
    {
        mTabHost.setCurrentTabByTag("find");
    }
}
