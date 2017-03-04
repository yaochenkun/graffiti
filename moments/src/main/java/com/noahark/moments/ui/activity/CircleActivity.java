package com.noahark.moments.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.noahark.moments.bean.CircleBean;
import com.noahark.moments.ui.adapter.CircleAdapter;

import java.util.ArrayList;
import java.util.List;

public class CircleActivity extends Activity{

    //下拉控件
    private PtrClassicFrameLayout mRefreshLayout;
    Handler handler = new Handler();

    //相册背景

    //头像
    private SimpleDraweeView mAvatarImgVi;

    //列表
    private ListView mListView;
    private List<CircleBean> mCircleBeanList = new ArrayList<CircleBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);

        initView();
        initData();
    }

    public void initView()
    {
        mListView = (ListView)findViewById(R.id.circlelist);
        mRefreshLayout = (PtrClassicFrameLayout)findViewById(R.id.refresh_layout_circle);

        RelativeLayout circleListHeaderView = (RelativeLayout) View.inflate(this,R.layout.listitem_circle_header,null);
        mListView.addHeaderView(circleListHeaderView);
        mAvatarImgVi = (SimpleDraweeView)circleListHeaderView.findViewById(R.id.avatar_circle_imgvi);
    }

    public void initData()
    {
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
                Toast.makeText(CircleActivity.this,"跳转到该条说说详情界面!",Toast.LENGTH_SHORT).show();
            }
        });

        String[] pictureUrls = {
                "http://i1.w.hjfile.cn/doc/201201/BrunoMarssongPics1UjEywkLCp66EnM60645.jpg",
                "http://img3.yxlady.com/yl/uploadfiles_5361/20140321/20140321105300422.jpg",
                "http://c.hiphotos.baidu.com/ting/pic/item/e824b899a9014c08fbc995ec0b7b02087af4f48d.jpg",
                "http://p4.music.126.net/18iXoSRlosMxEV8gXH-JfA==/3273246125578191.jpg",
                "http://imgsrc.baidu.com/forum/pic/item/3c6d55fbb2fb4316c964dc3020a4462309f7d305.jpg",
                "http://imgsrc.baidu.com/forum/w%3D580/sign=d4c120868d5494ee87220f111df5e0e1/d0c8a786c9177f3e40165d3471cf3bc79f3d56ec.jpg",
                "http://img.xiami.net/images/collect/427/27/106516427_1438570950_kpi0.jpg",
                "http://imgsrc.baidu.com/forum/w%3D580/sign=2e9a40eb073b5bb5bed720f606d2d523/3125ba22720e0cf3d1640fbb0e46f21fbf09aa52.jpg",
                "http://imgsrc.baidu.com/forum/w%3D580/sign=6394006d632762d0803ea4b790ed0849/2676838b87d6277f54578d7628381f30e824fcab.jpg"};


        //用户数据！！直接读取数据库中关注的人
        CircleBean circleBean = new CircleBean();
        circleBean.setAvatar("http://i1.w.hjfile.cn/doc/201201/BrunoMarssongPics1UjEywkLCp66EnM60645.jpg");
        circleBean.setNickname("姚陈堃");
        circleBean.setTime("9:30");
        circleBean.setContent("从今天开始，都监督我，绝不熬夜，发现我晚睡的小伙伴请强制我去碎觉！！重要的事情说三遍");
        List<String> picturesList = new ArrayList<String>(); //9宫格中照片的uri
        for(int i=0;i<5;i++)
        {
            //拉取图片Bitmap!!!!!!!

            picturesList.add(pictureUrls[i]);
        }
        circleBean.setPictureList(picturesList);


        //加入到数据集
        mCircleBeanList.add(circleBean);

        CircleBean circleBean2 = new CircleBean();
        circleBean2.setAvatar("http://img3.yxlady.com/yl/uploadfiles_5361/20140321/20140321105300422.jpg");
        circleBean2.setNickname("李逍遥");
        circleBean2.setTime("22:30");
        circleBean2.setContent("昨天看了千与千寻，不愧为神作！");
        picturesList = new ArrayList<String>(); //9宫格中照片的uri
        for(int i=0;i<9;i++)
        {
            //拉取图片Bitmap!!!!!!!

            picturesList.add(pictureUrls[i]);
        }
        circleBean2.setPictureList(picturesList);


        mCircleBeanList.add(circleBean2);


        //配置表项
        CircleAdapter adapter = new CircleAdapter(CircleActivity.this,mCircleBeanList);
        mListView.setAdapter(adapter);
    }

    public void onCloseTabPageBtn(View view)
    {
        this.finish();
    }

    //点击修改相册，进入相册封面图片选择界面
    public void onEnterUserZoneCoverSettingBtn(View view)
    {
        Toast.makeText(this,"进入相册选择界面",Toast.LENGTH_SHORT).show();
    }

    //点击个人头像->进入个人详情界面
    public void onEnterUserSnapshotBtn(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this,UserSnapshotActivity.class);
        startActivity(intent);
    }
}