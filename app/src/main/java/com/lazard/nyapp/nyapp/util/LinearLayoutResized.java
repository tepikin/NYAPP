package com.lazard.nyapp.nyapp.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class LinearLayoutResized extends LinearLayout {
    public interface OnLayoutListener_ {

        void onLayout_(boolean changed, int l, int t, int r, int b);

    }

    private OnLayoutListener_ onLayoutListener;
    private OnLayoutListener_ onLayoutListenerPost;

    public LinearLayoutResized(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public LinearLayoutResized(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public LinearLayoutResized(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (onLayoutListener != null) {
            onLayoutListener.onLayout_(changed, l, t, r, b);
        }
        super.onLayout(changed, l, t, r, b);
        if (onLayoutListenerPost != null) {
            onLayoutListenerPost.onLayout_(changed, l, t, r, b);
        }
    }

    public void setOnLayoutListener(OnLayoutListener_ onLayoutListener) {
        this.onLayoutListener = onLayoutListener;
    }

    public void setOnLayoutListenerPost(OnLayoutListener_ onLayoutListener) {
        this.onLayoutListenerPost = onLayoutListener;
    }
}
