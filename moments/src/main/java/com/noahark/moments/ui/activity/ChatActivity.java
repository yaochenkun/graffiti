package com.noahark.moments.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.noahark.moments.R;
import com.noahark.moments.bean.ChatBean;
import com.noahark.moments.ui.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends Activity{

    private Context mContext;

    private ListView mChatListView;
    private ListView mChatSelectListView;
    private EditText mChatSelectEditText;
    private TextView mChatSelectCancelBtn;

    private PtrClassicFrameLayout refreshLayout;
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        initData();
    }

    public void initView()
    {
        mContext = this;

        mChatListView = (ListView)findViewById(R.id.chatlist);
        //添加列表头
        LinearLayout mChatListHeaderView = (LinearLayout) View.inflate(this,R.layout.listitem_chat_header,null);
        mChatListView.addHeaderView(mChatListHeaderView);


        mChatSelectListView = (ListView)findViewById(R.id.chatlist_select);

        mChatSelectEditText = (EditText)findViewById(R.id.edittext_chat_select);
        mChatSelectEditText.setOnFocusChangeListener(new SelectEditTextFocuser());
        mChatSelectEditText.addTextChangedListener(new SelectEditTextWatcher());

        mChatSelectCancelBtn = (TextView)findViewById(R.id.btn_chat_selectcancel);


        //读取本地存放的，头像、昵称、时间、最近一条聊天记录！！！！！！！！！！
        List<ChatBean> chatBeanList = new ArrayList<ChatBean>();

        //用户数据
        ChatBean chatBean = new ChatBean();
        chatBean.setAvatar("http://imgsrc.baidu.com/forum/w%3D580/sign=537a10b1b899a9013b355b3e2d940a58/00bacbef76094b36e0e5d748a3cc7cd98d109d33.jpg");
        chatBean.setNickname("姚陈堃");
        chatBean.setDate("2016/10/11");
        chatBean.setContent("当我们的身体行走在世界各地的时候，灵魂却需要独处");

        //加入
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);

        //表项
        ChatAdapter adapter = new ChatAdapter(this,chatBeanList);
        mChatListView.setAdapter(adapter);



        refreshLayout = (PtrClassicFrameLayout)findViewById(R.id.refresh_layout_chat);

//		//初次自动加载
//		refreshLayout.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				refreshLayout.autoRefresh(true);
//			}
//		}, 150);

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
    }

    public void initData()
    {

    }

    public void onCloseTabPageBtn(View view)
    {
        this.finish();
    }


    //焦点落在EditText上时的响应动作
    class SelectEditTextFocuser implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean hasFocused) {

            if(hasFocused)
            {
                mChatListView.setVisibility(View.INVISIBLE);
                mChatSelectListView.setVisibility(View.VISIBLE);
                mChatSelectCancelBtn.setVisibility(View.VISIBLE);
            }
        }
    }


    //搜索EditText输入文本时响应动作
    class SelectEditTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence selection, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence selection, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }


    //点击搜索框的“X”
    public void onSelectClearBtn(View view)
    {
        mChatSelectEditText.setText(""); //清空
    }

    //点击搜索框的“取消”
    public void onSelectCancelBtn(View view)
    {
        mChatSelectEditText.setText(""); //清空
        mChatListView.setVisibility(View.VISIBLE);
        mChatSelectListView.setVisibility(View.INVISIBLE);
        mChatSelectCancelBtn.setVisibility(View.GONE);
        mChatSelectEditText.clearFocus();
    }

    public void onEnterCommentAreaBtn(View view)
    {
        Toast.makeText(this,"进入评论界面",Toast.LENGTH_SHORT).show();
    }

    public void onEnterLikeAreaBtn(View view)
    {
        Toast.makeText(this,"进入点赞界面",Toast.LENGTH_SHORT).show();
    }
}
