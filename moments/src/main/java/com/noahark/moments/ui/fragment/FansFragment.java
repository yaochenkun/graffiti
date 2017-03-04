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
import com.noahark.moments.bean.FanBean;
import com.noahark.moments.ui.adapter.FansAdapter;

import java.util.ArrayList;
import java.util.List;

public class FansFragment extends Fragment {

	private static final String ARG_POSITION = "position";

	private ListView mFanListView;
	private List<FanBean> mFanBeanList;
	private FansAdapter mFanListAdapter;
	private PtrClassicFrameLayout refreshLayout;
//	private ListView mFanSelectListView;
//	private EditText mFanSelectEditText;
//	private TextView mFanSelectCancelBtn;
	Handler handler = new Handler();
	private int position;

	public static FansFragment newInstance(int position) {
		FansFragment FanFragment = new FansFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(ARG_POSITION, position);
		FanFragment.setArguments(bundle);
		return FanFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fans,container,false);
		mFanListView = (ListView) rootView.findViewById(R.id.fanlist);

//		mFanSelectListView = (ListView)findViewById(R.id.Fanlist_select);
//
//        mFanSelectEditText = (EditText)findViewById(R.id.edittext_Fan_select);
//        mFanSelectEditText.setOnFocusChangeListener(new SelectEditTextFocuser());
//        mFanSelectEditText.addTextChangedListener(new SelectEditTextWatcher());
//
//        mFanSelectCancelBtn = (TextView)findViewById(R.id.btn_Fan_selectcancel);

		//拉取头像、昵称！！！！！！！！！！
		mFanBeanList = new ArrayList<FanBean>();

		//用户数据
		FanBean FanBean = new FanBean();
		FanBean.setAvatar("http://img3.imgtn.bdimg.com/it/u=4151049131,570330146&fm=23&gp=0.jpg");
		FanBean.setNickname("唐雪见");

		//加入
		mFanBeanList.add(FanBean);

		FanBean FanBean1 = new FanBean();
		FanBean1.setAvatar("http://e.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=9e359aa2748b4710ce7af5c8f6feefcb/b90e7bec54e736d10bbaee8b9d504fc2d4626983.jpg");
		FanBean1.setNickname("姜云凡");

		mFanBeanList.add(FanBean1);
		mFanBeanList.add(FanBean);
		mFanBeanList.add(FanBean1);
		mFanBeanList.add(FanBean);
		mFanBeanList.add(FanBean1);
		mFanBeanList.add(FanBean);
		mFanBeanList.add(FanBean1);
		mFanBeanList.add(FanBean);


		mFanListAdapter = new FansAdapter(getContext(),mFanBeanList);
		mFanListView.setAdapter(mFanListAdapter);

		refreshLayout = (PtrClassicFrameLayout)rootView.findViewById(R.id.refresh_layout_fan);

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

		return rootView;
	}

	//向列表添加表项
	public void addListItem(FanBean FanBean)
	{
		mFanBeanList.add(FanBean);
		mFanListAdapter.notifyDataSetChanged(); //通知更新列表
	}
}