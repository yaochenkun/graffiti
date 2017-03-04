package com.noahark.moments.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noahark.moments.R;

/**
 * Created by chicken on 2016/11/19.
 */
/*
 * Created by Alexander Krol (troy379) on 29.08.16.
 */
public class ImageOverlayView extends RelativeLayout {

    private TextView tvDescription;

    private String sharingText;

    public ImageOverlayView(Context context) {
        super(context);
        init();
    }

    public ImageOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setDescription(String description) {
        tvDescription.setText(description);
    }

    private void init() {
        View view = inflate(getContext(), R.layout.imgoverlay_circle, this);
    }
}