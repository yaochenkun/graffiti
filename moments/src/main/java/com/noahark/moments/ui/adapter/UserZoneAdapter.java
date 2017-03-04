package com.noahark.moments.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.noahark.moments.R;
import com.noahark.moments.bean.UserZoneBean;
import com.rd.PageIndicatorView;
import com.stfalcon.frescoimageviewer.ImageViewer.Builder;
import com.stfalcon.frescoimageviewer.ImageViewer.OnImageChangeListener;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

/**
 * Created by chicken on 2016/11/9.
 */
public class UserZoneAdapter extends BaseAdapter{

    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private List<UserZoneBean> mUserZoneBeanList = null;

    private final int mListItemLayout = R.layout.listitem_userzone;
    private final int mListItemDayViewId = R.id.userzonelistitem_day;
    private final int mListItemMonthViewId = R.id.userzonelistitem_month;
    private final int mListItemSnapshotViewId = R.id.userzonelistitem_snapshot;
    private final int mListItemContentViewId = R.id.userzonelistitem_content;

    public UserZoneAdapter(Context context, List<UserZoneBean> userZoneBeanList)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mUserZoneBeanList = userZoneBeanList;
    }

    @Override
    public int getCount() {
        return mUserZoneBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mUserZoneBeanList.get(position);
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
            convertView = mInflater.inflate(mListItemLayout,null);

            //装入ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.dayTextView = (TextView) convertView.findViewById(mListItemDayViewId);
            viewHolder.monthTextView = (TextView) convertView.findViewById(mListItemMonthViewId);
            viewHolder.snapshotImageView = (SimpleDraweeView) convertView.findViewById(mListItemSnapshotViewId);
            viewHolder.contentTextView = (TextView)convertView.findViewById(mListItemContentViewId);
            convertView.setTag(viewHolder);
        }
        else
        {
            //获取缓存的Views
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //说说静态文本
        UserZoneBean userZoneBean = mUserZoneBeanList.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        viewHolder.dayTextView.setText(Integer.toString(calendar.get(Calendar.DATE)));
        viewHolder.monthTextView.setText(Integer.toString(calendar.get(Calendar.MONTH) + 1) + "月");
        viewHolder.contentTextView.setText(userZoneBean.getContent());

        //说说快照图片
        String pictureUri = userZoneBean.getPicture();
        if(pictureUri == null || pictureUri.isEmpty())
            viewHolder.snapshotImageView.setVisibility(View.GONE); //隐藏
        else
        {
            viewHolder.snapshotImageView.setVisibility(View.VISIBLE);
            viewHolder.snapshotImageView.setImageURI(pictureUri);
        }

        return convertView;
    }


    static final class ViewHolder{

        private TextView dayTextView;
        private TextView monthTextView;
        private SimpleDraweeView snapshotImageView;
        private TextView contentTextView;
    }

    //点开某个图片后可以进入当前说说的所有图片的查看界面
    class UserZoneImageViewOnClickListener implements OnClickListener{

        private Builder imageViewer;
        private int pictureTotalNum;

        UserZoneImageViewOnClickListener(Builder imageViewer, int pictureTotalNum)
        {
            this.imageViewer = imageViewer;
            this.pictureTotalNum = pictureTotalNum;
        }

        @Override
        public void onClick(View view) {

            //进入该条说说详情页
            Toast.makeText(mContext, "跳转到该条说说详情界面!", Toast.LENGTH_SHORT).show();

//            //获取当前点击的这个图片页码
//            int curPageIndex = Integer.parseInt((String)view.getTag());
//
//            //设置页码小圆点
//            RelativeLayout overlayLayout = (RelativeLayout)View.inflate(mContext,R.layout.imgoverlay_circle,null);
//            PageIndicatorView pageIndicatorView = (PageIndicatorView) overlayLayout.findViewById(R.id.page_indicator);
//            pageIndicatorView.setCount(pictureTotalNum);
//            pageIndicatorView.setSelection(curPageIndex);
//
//            //翻页时相应的动作
//            OnImageChangeListener imageViewerTurnPageListener = new ImageViewerTurnPageListener(pageIndicatorView);
//
//            //显示细致查看界面
//            imageViewer.setStartPosition(curPageIndex)
//                    .setOverlayView(overlayLayout)
//                    .setImageChangeListener(imageViewerTurnPageListener)
//                    .show();
        }
    }

    class ImageViewerTurnPageListener implements OnImageChangeListener{

        private PageIndicatorView pageIndicatorView;

        ImageViewerTurnPageListener(PageIndicatorView pageIndicatorView)
        {
            this.pageIndicatorView = pageIndicatorView;
        }

        @Override
        public void onImageChange(int curPageIndex) {

            pageIndicatorView.setSelection(curPageIndex);//设置页码小圆点
        }
    }

    //构造含有pictureList的图片详情查看器ImageViewer
    private Builder createImageViewer(Context context, List<String> pictureList, GenericDraweeHierarchyBuilder hierarchyBuilder)
    {
        return new Builder(context, pictureList)
                .setCustomDraweeHierarchyBuilder(hierarchyBuilder);
    }
}

