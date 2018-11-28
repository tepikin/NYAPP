package com.lazard.nyapp.nyapp.util.views.imagezoom;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * <com.camlyapp.Camly.utils.view.ImageViewTouchScaled
 *
 * @author Egor
 */
public class ImageViewTouchScaled extends ImageViewTouch {

    public ImageViewTouchScaled(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewTouchScaled(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImageViewTouchScaled(Context context) {
        super(context);
    }

    @Override
    protected void getProperBaseMatrix(Drawable drawable, Matrix matrix) {
        // sendMessages(drawable);
        float viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();
        matrix.reset();

        if (w > viewWidth || h > viewHeight) {
            float widthScale = Math.min(viewWidth / w, 2.0f);
            float heightScale = Math.min(viewHeight / h, 2.0f);
            float scale = Math.min(widthScale, heightScale);
            matrix.postScale(scale, scale);
            float tw = (viewWidth - w * scale) / 2.0f;
            float th = (viewHeight - h * scale) / 2.0f;
            matrix.postTranslate(tw, th);
        } else {
            float widthScale = viewWidth / w;
            float heightScale = viewHeight / h;
            float scale = Math.min(widthScale, heightScale);
            matrix.postScale(scale, scale);
            float tw = (viewWidth - scale * w) / 2.0f;
            float th = (viewHeight - scale * h) / 2.0f;
            matrix.postTranslate(tw, th);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap, true);
        postScale(1, (getWidth() - getPaddingLeft() - getPaddingRight()) / 2, (getHeight() - getPaddingTop() - getPaddingBottom()) / 2);
        postTranslate(0, 0);

    }

}
