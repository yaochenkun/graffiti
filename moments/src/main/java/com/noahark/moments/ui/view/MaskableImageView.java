package com.noahark.moments.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;

public class MaskableImageView extends SimpleDraweeView {

    public MaskableImageView(Context context) {
        super(context);
    }

    public MaskableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public MaskableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        super.dispatchSetPressed(pressed);
        if (pressed) {
           this.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        } else {
            this.clearColorFilter();
        }

    }
}