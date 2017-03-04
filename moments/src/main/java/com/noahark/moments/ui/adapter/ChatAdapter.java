package com.noahark.moments.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.noahark.moments.R;
import com.noahark.moments.bean.ChatBean;

import java.util.List;

/**
 * Created by chicken on 2016/11/21.
 */
public class ChatAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<ChatBean> mChatBeanList;
    private final int mListItemLayoutId = R.layout.listitem_chat;
    private final int mListItemAvatarViewId = R.id.chatlistitem_avatar;
    private final int mListItemNicknameViewId = R.id.chatlistitem_nickname;
    private final int mListItemDateViewId = R.id.chatlistitem_date;
    private final int mListItemContentViewId = R.id.chatlistitem_content;

    public ChatAdapter(Context context, List<ChatBean> chatBeanList)
    {
        mInflater = LayoutInflater.from(context);
        mChatBeanList = chatBeanList;
    }

    @Override
    public int getCount() {
        return mChatBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChatBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        //convertView是否存在
        if(convertView == null) //不存在
        {
            //生成
            convertView = mInflater.inflate(mListItemLayoutId,null);

            //装入ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.avatarImageView = (SimpleDraweeView) convertView.findViewById(mListItemAvatarViewId);
            viewHolder.nicknameTextView = (TextView) convertView.findViewById(mListItemNicknameViewId);
            viewHolder.dateTextView = (TextView) convertView.findViewById(mListItemDateViewId);
            viewHolder.contentTextView = (TextView) convertView.findViewById(mListItemContentViewId);

            convertView.setTag(viewHolder);
        }
        else
        {
            //获取缓存的Views
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //说说静态文本
        ChatBean chatBean = mChatBeanList.get(position);
        viewHolder.avatarImageView.setImageURI(chatBean.getAvatar());
        viewHolder.nicknameTextView.setText(chatBean.getNickname());
        viewHolder.dateTextView.setText(chatBean.getDate());
        viewHolder.contentTextView.setText(chatBean.getContent());

        return convertView;
    }

    static final class ViewHolder{
        private SimpleDraweeView avatarImageView;
        private TextView nicknameTextView;
        private TextView dateTextView;
        private TextView contentTextView;
    }


}
