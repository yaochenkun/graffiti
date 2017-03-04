package com.noahark.moments.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.noahark.moments.R;
import com.noahark.moments.bean.CircleBean;
import com.noahark.moments.ui.view.MaskableImageView;
import com.rd.PageIndicatorView;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.stfalcon.frescoimageviewer.ImageViewer.Builder;
import com.stfalcon.frescoimageviewer.ImageViewer.OnImageChangeListener;

import java.util.List;

/**
 * Created by chicken on 2016/11/9.
 */
public class CircleAdapter extends BaseAdapter{

    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private List<CircleBean> mCircleBeanList = null;

    private final int mListItemLayout = R.layout.listitem_circle;
    private final int mListItemAvatarViewId = R.id.circlelistitem_avatar;
    private final int mListItemNicknameViewId = R.id.circlelistitem_nickname;
    private final int mListItemTimeViewId = R.id.circlelistitem_time;
    private final int mListItemContentViewId = R.id.circlelistitem_content;
    private final int mGridLayoutId = R.id.circlegrid_pictures;
    private final int[] mGridImageViewIds = {R.id.circlegrid_picture_1,R.id.circlegrid_picture_2,
            R.id.circlegrid_picture_3,R.id.circlegrid_picture_4,R.id.circlegrid_picture_5,
            R.id.circlegrid_picture_6,R.id.circlegrid_picture_7,R.id.circlegrid_picture_8,
            R.id.circlegrid_picture_9};

    public CircleAdapter(Context context, List<CircleBean> circleBeanList)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mCircleBeanList = circleBeanList;
    }

    @Override
    public int getCount() {
        return mCircleBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCircleBeanList.get(position);
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
            viewHolder.avatarImageView = (SimpleDraweeView) convertView.findViewById(mListItemAvatarViewId);
            viewHolder.nicknameTextView = (TextView) convertView.findViewById(mListItemNicknameViewId);
            viewHolder.contentTextView = (TextView)convertView.findViewById(mListItemContentViewId);
            viewHolder.timeTextView = (TextView)convertView.findViewById(mListItemTimeViewId);
            viewHolder.picturesGridLayout = (GridLayout) convertView.findViewById(mGridLayoutId);
            for(int i=0;i<mGridImageViewIds.length;i++) {
                viewHolder.pictureImageViews[i] = (MaskableImageView) viewHolder.picturesGridLayout.findViewById(mGridImageViewIds[i]);
            }
            convertView.setTag(viewHolder);
        }
        else
        {
            //获取缓存的Views
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //说说静态文本
        CircleBean circleBean = mCircleBeanList.get(position);
        viewHolder.avatarImageView.setImageURI(circleBean.getAvatar());
        viewHolder.nicknameTextView.setText(circleBean.getNickname());
        viewHolder.contentTextView.setText(circleBean.getContent());
        viewHolder.timeTextView.setText(circleBean.getTime());

        //九宫格图片
        List<String> pictureList = circleBean.getPictureList();
        if(pictureList == null || pictureList.isEmpty())
            viewHolder.picturesGridLayout.setVisibility(View.GONE); //隐藏
        else
        {
            int i=0;

            //设置加载进度条
            ProgressBarDrawable progressbar = new ProgressBarDrawable();
            progressbar.setBackgroundColor(Color.WHITE);
            GenericDraweeHierarchyBuilder hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(mContext.getResources())
                    .setProgressBarImage(progressbar);


            //点击图片时的动作
            Builder imageViewer = createImageViewer(mContext,pictureList,hierarchyBuilder);
            CircleImageViewOnClickListener circleImageViewOnClickListener = new CircleImageViewOnClickListener(imageViewer,pictureList.size());

            for(;i<pictureList.size();i++)
            {
                Uri pictureUri = Uri.parse(pictureList.get(i));
                ImageView pictureImageView = viewHolder.pictureImageViews[i];
                pictureImageView.setVisibility(View.VISIBLE);

                //利用Fressco服务器加载图片资源
                pictureImageView.setImageURI(pictureUri);

                //设置当pictureImageView点击后，进入当前说说所有图片的查看界面
                pictureImageView.setOnClickListener(circleImageViewOnClickListener);
            }

            //把之后的图片都屏蔽掉
            for(int j=i;j<ViewHolder.pictureNum;j++)
            {
                ImageView pictureImageView = viewHolder.pictureImageViews[j];
                pictureImageView.setVisibility(View.GONE);
            }
        }

        return convertView;
    }


    static final class ViewHolder{
        private static final int pictureNum = 9;
        private SimpleDraweeView avatarImageView;
        private TextView nicknameTextView;
        private TextView contentTextView;
        private TextView timeTextView;
        private GridLayout picturesGridLayout;
        private ImageView[] pictureImageViews = new ImageView[pictureNum];
    }

    //点开某个图片后可以进入当前说说的所有图片的查看界面
    class CircleImageViewOnClickListener implements OnClickListener{

        private Builder imageViewer;
        private int pictureTotalNum;

        CircleImageViewOnClickListener(Builder imageViewer, int pictureTotalNum)
        {
            this.imageViewer = imageViewer;
            this.pictureTotalNum = pictureTotalNum;
        }

        @Override
        public void onClick(View view) {

            //获取当前点击的这个图片页码
            int curPageIndex = Integer.parseInt((String)view.getTag());

            //设置页码小圆点
            RelativeLayout overlayLayout = (RelativeLayout)View.inflate(mContext,R.layout.imgoverlay_circle,null);
            PageIndicatorView pageIndicatorView = (PageIndicatorView) overlayLayout.findViewById(R.id.page_indicator);
            pageIndicatorView.setCount(pictureTotalNum);
            pageIndicatorView.setSelection(curPageIndex);

            //翻页时相应的动作
            OnImageChangeListener imageViewerTurnPageListener = new ImageViewerTurnPageListener(pageIndicatorView);

            //显示细致查看界面
            imageViewer.setStartPosition(curPageIndex)
                    .setOverlayView(overlayLayout)
                    .setImageChangeListener(imageViewerTurnPageListener)
                    .show();
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
        return new ImageViewer.Builder(context, pictureList)
                .setCustomDraweeHierarchyBuilder(hierarchyBuilder);
    }
}

