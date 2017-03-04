package com.noahark.moments.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.noahark.moments.R;
import com.noahark.moments.bean.FollowBean;


import java.util.List;

/**
 * Created by chicken on 2016/11/21.
 */
public class FollowAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<FollowBean> mFollowBeanList;
    private final int mListItemLayoutId = R.layout.listitem_follow;
    private final int mListItemAvatarViewId = R.id.followlistitem_avatar;
    private final int mListItemNicknameViewId = R.id.followlistitem_nickname;

    public FollowAdapter(Context context, List<FollowBean> followBeanList)
    {
        mInflater = LayoutInflater.from(context);
        mFollowBeanList = followBeanList;
    }

    @Override
    public int getCount() {
        return mFollowBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFollowBeanList.get(position);
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

            convertView.setTag(viewHolder);
        }
        else
        {
            //获取缓存的Views
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //说说静态文本
        FollowBean followBean = mFollowBeanList.get(position);
        viewHolder.avatarImageView.setImageURI(followBean.getAvatar());
        viewHolder.nicknameTextView.setText(followBean.getNickname());

        return convertView;
    }

    static final class ViewHolder{
        private SimpleDraweeView avatarImageView;
        private TextView nicknameTextView;
    }
}
