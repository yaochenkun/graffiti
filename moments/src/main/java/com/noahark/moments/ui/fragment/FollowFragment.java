/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.noahark.moments.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.noahark.moments.R;
import com.noahark.moments.bean.FollowBean;
import com.noahark.moments.ui.adapter.FollowAdapter;

import java.util.ArrayList;
import java.util.List;

public class FollowFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private ListView mFollowListView;
    private List<FollowBean> mFollowBeanList;
    private FollowAdapter mFollowListAdapter;
//	private ListView mFollowSelectListView;
//	private EditText mFollowSelectEditText;
//	private TextView mFollowSelectCancelBtn;

    private PtrClassicFrameLayout refreshLayout;
    Handler handler = new Handler();

    private int position;

    public static FollowFragment newInstance(int position) {
        FollowFragment followFragment = new FollowFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        followFragment.setArguments(bundle);
        return followFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_follow,container,false);
        mFollowListView = (ListView) rootView.findViewById(R.id.followlist);

//		mFollowSelectListView = (ListView)findViewById(R.id.Followlist_select);
//
//        mFollowSelectEditText = (EditText)findViewById(R.id.edittext_Follow_select);
//        mFollowSelectEditText.setOnFocusChangeListener(new SelectEditTextFocuser());
//        mFollowSelectEditText.addTextChangedListener(new SelectEditTextWatcher());
//
//        mFollowSelectCancelBtn = (TextView)findViewById(R.id.btn_Follow_selectcancel);

        //拉取头像、昵称！！！！！！！！！！
        mFollowBeanList = new ArrayList<FollowBean>();

        //用户数据
        FollowBean followBean = new FollowBean();
        followBean.setAvatar("http://tupian.enterdesk.com/2014/xll/11/01/4/5.1.jpg");
        followBean.setNickname("姚陈堃");

        //加入
        mFollowBeanList.add(followBean);

        FollowBean followBean1 = new FollowBean();
        followBean1.setAvatar("http://tupian.enterdesk.com/2014/mxy/11/5/4/10.jpg");
        followBean1.setNickname("李逍遥");

        mFollowBeanList.add(followBean1);
        mFollowBeanList.add(followBean);
        mFollowBeanList.add(followBean1);
        mFollowBeanList.add(followBean);
        mFollowBeanList.add(followBean1);
        mFollowBeanList.add(followBean);
        mFollowBeanList.add(followBean1);
        mFollowBeanList.add(followBean);


        mFollowListAdapter = new FollowAdapter(getContext(),mFollowBeanList);
        mFollowListView.setAdapter(mFollowListAdapter);

        refreshLayout = (PtrClassicFrameLayout)rootView.findViewById(R.id.refresh_layout_follow);

        //初次自动加载
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.autoRefresh(true);
            }
        }, 150);

        //下拉加载
        refreshLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        refreshLayout.refreshComplete();
                    }
                }, 1500);
            }
        });

//		ViewCompat.setElevation(rootView, 50);

        return rootView;
    }

    //向列表添加表项
    public void addListItem(FollowBean followBean)
    {
        mFollowBeanList.add(followBean);
        mFollowListAdapter.notifyDataSetChanged(); //通知更新列表
    }
}