package com.noahark.moments.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.noahark.moments.R;
import com.noahark.moments.R2;
import com.noahark.moments.bean.UserZoneBean;
import com.noahark.moments.ui.adapter.UserZoneAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.sql.Date;

public class UserZoneActivity extends Activity {

    //下拉控件
    @BindView(R2.id.refresh_layout_userzone)
    PtrClassicFrameLayout mRefreshLayout;
    Handler handler = new Handler();

    //相册背景

    //头像
    SimpleDraweeView mAvatarImgVi;

    //列表
    @BindView(R2.id.userzonelist)
    ListView mListView;
    private List<UserZoneBean> mUsrZoneList = new ArrayList<UserZoneBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_zone);
        ButterKnife.bind(this);

        initView();
        initData();
    }


    public void initView()
    {
        RelativeLayout userZoneListHeaderView = (RelativeLayout) View.inflate(this,R.layout.listitem_userzone_header,null);
        mListView.addHeaderView(userZoneListHeaderView);
        mAvatarImgVi = (SimpleDraweeView) userZoneListHeaderView.findViewById(R.id.avatar_userzone_imgvi);
    }

    public void initData() {
        //加载头像
        mAvatarImgVi.setImageURI("http://p4.music.126.net/18iXoSRlosMxEV8gXH-JfA==/3273246125578191.jpg");

        //初次自动加载
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setLoadMoreEnable(true);
                mRefreshLayout.autoRefresh(true);
            }
        }, 150);

        //下拉加载
        mRefreshLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        initListData();
                        mRefreshLayout.refreshComplete();

                    }
                }, 1500);
            }
        });

        //上拉加载
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {

//                        if (page == 1) {
//                            //set load more disable
//                            ptrClassicFrameLayout.setLoadMoreEnable(false);
//                        }
                        mRefreshLayout.loadMoreComplete(true);
                    }
                }, 1000);
            }
        });
    }

    public void initListData()
    {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(UserZoneActivity.this,"跳转到该条说说详情界面!",Toast.LENGTH_SHORT).show();
            }
        });

        //用户数据！！直接读取数据库中关注的人
        UserZoneBean userZoneBean = new UserZoneBean();
        userZoneBean.setTime(new Date(System.currentTimeMillis()));
        userZoneBean.setPicture("http://i1.w.hjfile.cn/doc/201201/BrunoMarssongPics1UjEywkLCp66EnM60645.jpg");
        userZoneBean.setContent("那是我一生中永永远远都没法忘记的时光，是我们三人一起共度之时光");
        mUsrZoneList.add(userZoneBean);

        UserZoneBean userZoneBean1 = new UserZoneBean();
        userZoneBean1.setTime(new Date(System.currentTimeMillis()));
        userZoneBean1.setContent("今天玩了天之痕，感觉又回到了童年时代，满满的回忆！");
        mUsrZoneList.add(userZoneBean1);


        //配置表项
        UserZoneAdapter adapter = new UserZoneAdapter(UserZoneActivity.this, mUsrZoneList);
        mListView.setAdapter(adapter);
    }


    @OnClick(R2.id.close_userzone)
    public void onCloseUserZoneBtn(View view) {
        finish();
    }
}
