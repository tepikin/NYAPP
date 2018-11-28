package com.lazard.nyapp.nyapp.util.views.imagezoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import androidx.appcompat.widget.AppCompatImageView;
import com.lazard.nyapp.nyapp.util.views.imagezoom.easing.Cubic;
import com.lazard.nyapp.nyapp.util.views.imagezoom.easing.Easing;
import com.lazard.nyapp.nyapp.util.views.imagezoom.graphics.FastBitmapDrawable;
import com.lazard.nyapp.nyapp.util.views.imagezoom.utils.IDisposable;


/**
 * Base View to manage image zoom/scrool/pinch operations
 *
 * @author alessandro
 */
public class ImageViewTouchBase extends AppCompatImageView implements IDisposable {
    public static final String LOG_TAG = "image";

    ;
    protected static final float MIN_ZOOM = 0.9f;
    protected final Matrix mDisplayMatrix = new Matrix();
    protected final float[] mMatrixValues = new float[9];
    final protected float MAX_ZOOM = 2.0f;
    final protected int DEFAULT_ANIMATION_DURATION = 200;
    protected Easing mEasing = new Cubic();
    protected Matrix mBaseMatrix = new Matrix();
    protected Matrix mSuppMatrix = new Matrix();
    protected Handler mHandler = new Handler();
    protected Runnable mOnLayoutRunnable = null;
    protected float mMaxZoom;
    protected float mMinZoom = -1;
    protected int mThisWidth = -1, mThisHeight = -1;
    protected boolean mFitToScreen = false;
    protected RectF mBitmapRect = new RectF();
    protected RectF mCenterRect = new RectF();
    protected RectF mScrollRect = new RectF();
    private OnBitmapChangedListener mListener;

    public ImageViewTouchBase(Context context) {
        super(context);
        init();
    }

    public ImageViewTouchBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageViewTouchBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void _setImageDrawable(final Drawable drawable,
                                     final boolean reset, final Matrix initial_matrix,
                                     final float maxZoom) {

        if (drawable != null) {
            if (mFitToScreen) {
                getProperBaseMatrix2(drawable, mBaseMatrix);
                setMinZoom(getScale(mBaseMatrix));
            } else {
                getProperBaseMatrix(drawable, mBaseMatrix);
                setMinZoom(getMinZoom());
            }
            super.setImageDrawable(drawable);
        } else {
            mBaseMatrix.reset();
            super.setImageDrawable(null);
        }

        if (reset) {
            mSuppMatrix.reset();
            if (initial_matrix != null) {
                mSuppMatrix = new Matrix(initial_matrix);
            }
        }

        setImageMatrix(getImageViewMatrix());

        if (maxZoom < 1) {
            mMaxZoom = maxZoom();
        } else {
            mMaxZoom = maxZoom;
        }

        onBitmapChanged(drawable);
    }

    protected void center(boolean horizontal, boolean vertical) {
        final Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }
        RectF rect = getCenter(mSuppMatrix, horizontal, vertical);
        if (rect.left != 0 || rect.top != 0) {
            Log.d(LOG_TAG, "center.rect: " + rect.left + "x" + rect.top);
            postTranslate(rect.left, rect.top);
        }
    }

    public void clear() {
        setImageBitmap(null, true);
    }

    @Override
    public void dispose() {
        clear();
    }

    protected RectF getBitmapRect() {
        return getBitmapRect(mSuppMatrix);
    }

    protected RectF getBitmapRect(Matrix supportMatrix) {
        final Drawable drawable = getDrawable();

        if (drawable == null) {
            return null;
        }
        Matrix m = getImageViewMatrix(supportMatrix);
        mBitmapRect.set(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        m.mapRect(mBitmapRect);
        return mBitmapRect;
    }

    protected RectF getCenter(Matrix supportMatrix, boolean horizontal,
                              boolean vertical) {
        final Drawable drawable = getDrawable();

        if (drawable == null) {
            return new RectF(0, 0, 0, 0);
        }

        mCenterRect.set(0, 0, 0, 0);
        RectF rect = getBitmapRect(supportMatrix);
        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;
        if (vertical) {
            int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
            if (height < viewHeight) {
                deltaY = (viewHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < viewHeight) {
                deltaY = getHeight() - rect.bottom;
            }
        }
        if (horizontal) {
            int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            if (width < viewWidth) {
                deltaX = (viewWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right;
            }
        }
        mCenterRect.set(deltaX, deltaY, 0, 0);
        return mCenterRect;
    }

    /**
     * Returns the current image display matrix. This matrix can be used in the
     * next call to the {@link #setImageBitmap(Bitmap, boolean, Matrix)} to
     * restore the same view state of the previous {@link Bitmap}
     *
     * @return
     */
    public Matrix getDisplayMatrix() {
        return new Matrix(mSuppMatrix);
    }

    public Matrix getImageViewMatrix() {
        return getImageViewMatrix(mSuppMatrix);
    }

    public Matrix getBaseMatrix() {
        return new Matrix(mBaseMatrix);
    }

    public void setBaseMatrix(Matrix baseMatrix) {
        mBaseMatrix.set(baseMatrix);
    }

    public Matrix getImageViewMatrix(Matrix supportMatrix) {
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(supportMatrix);
        return mDisplayMatrix;
    }

    public float getMaxZoom() {
        if (mMaxZoom < 1) {
            mMaxZoom = maxZoom();
        }
        return mMaxZoom;
    }

    public float getMinZoom() {
        if (mMinZoom < 0) {
            mMinZoom = minZoom();
        }
        return mMinZoom;
    }

    public void setMinZoom(float value) {
        Log.d(LOG_TAG, "minZoom: " + value);
        mMinZoom = value;
    }

    /**
     * Setup the base matrix so that the image is centered and scaled properly.
     *
     * @param bitmap
     * @param matrix
     */
    protected void getProperBaseMatrix(Drawable drawable, Matrix matrix) {
        Log.i(LOG_TAG, "getProperBaseMatrix");
        float viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();
        matrix.reset();

        // if (w > viewWidth || h > viewHeight) {
        // float widthScale = Math.min(viewWidth / w, 2.0f);
        // float heightScale = Math.min(viewHeight / h, 2.0f);

        float widthScale = viewWidth / w;
        float heightScale = viewHeight / h;

        float scale = Math.max(widthScale, heightScale);
        Log.d(LOG_TAG, "scale: " + scale);
        matrix.postScale(scale, scale);
        float tw = (viewWidth - w * scale) / 2.0f;
        float th = (viewHeight - h * scale) / 2.0f;
        matrix.postTranslate(tw, th);
        // } else {
        // float widthScale = viewWidth / w;
        // float heightScale = viewHeight / h;
        // float scale = Math.max(widthScale, heightScale);
        // Log.d(LOG_TAG, "scale: " + scale);
        // matrix.postScale(scale, scale);
        // float tw = (viewWidth - w) / 2.0f;
        // float th = (viewHeight - h) / 2.0f;
        // matrix.postTranslate(tw, th);
        // }
    }

    /**
     * Setup the base matrix so that the image is centered and scaled properly.
     *
     * @param bitmap
     * @param matrix
     */
    protected void getProperBaseMatrix2(Drawable bitmap, Matrix matrix) {
        float viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        float w = bitmap.getIntrinsicWidth();
        float h = bitmap.getIntrinsicHeight();
        matrix.reset();
        float widthScale = Math.min(viewWidth / w, MAX_ZOOM);
        float heightScale = Math.min(viewHeight / h, MAX_ZOOM);
        float scale = Math.max(widthScale, heightScale);
        matrix.postScale(scale, scale);
        matrix.postTranslate((viewWidth - w * scale) / MAX_ZOOM,
                (viewHeight - h * scale) / MAX_ZOOM);
    }

    @Override
    public float getRotation() {
        return 0;
    }

    public float getScale() {
        return getScale(mSuppMatrix);
    }

    protected float getScale(Matrix matrix) {
        return getValue(matrix, Matrix.MSCALE_X);
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    protected void init() {
        setScaleType(ScaleType.MATRIX);
    }

    /**
     * compute the max allowed zoom fator
     *
     * @return
     */
    protected float maxZoom() {
        final Drawable drawable = getDrawable();

        if (drawable == null) {
            return 1F;
        }

        float fw = (float) drawable.getIntrinsicWidth() / (float) mThisWidth;
        float fh = (float) drawable.getIntrinsicHeight() / (float) mThisHeight;
        float max = Math.max(fw, fh);// TODO *4 is default
        max = Math.max(max, getMinZoom()) * 4;// TODO *4 is default
        return max;
    }

    protected float minZoom() {
        return 1F;
    }

    protected void onBitmapChanged(final Drawable bitmap) {
        if (mListener != null) {
            mListener.onBitmapChanged(bitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            if (getVisibility() == GONE || getVisibility() == INVISIBLE) return;
            super.onDraw(canvas);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mThisWidth = right - left - getPaddingLeft() - getPaddingRight();
        mThisHeight = bottom - top - getPaddingTop() - getPaddingBottom();
        Runnable r = mOnLayoutRunnable;
        if (r != null) {
            mOnLayoutRunnable = null;
            r.run();
        }
        if (getDrawable() != null) {

            float scaleOld = getValue(getImageViewMatrix(), Matrix.MSCALE_X);

            if (mFitToScreen) {
                getProperBaseMatrix2(getDrawable(), mBaseMatrix);
                setMinZoom(1.0f);
            } else {
                getProperBaseMatrix(getDrawable(), mBaseMatrix);
                setMinZoom(getMinZoom());
            }

            float translateX = getValue(mSuppMatrix, Matrix.MTRANS_X);
            float translateY = getValue(mSuppMatrix, Matrix.MTRANS_Y);

            float translateBaseX = getValue(mBaseMatrix, Matrix.MTRANS_X);
            float translateBaseY = getValue(mBaseMatrix, Matrix.MTRANS_Y);

            float scaleNew = getValue(getImageViewMatrix(), Matrix.MSCALE_X);

            float translateFixedX = translateX * scaleNew / scaleOld;
            float translateFixedY = translateY * scaleNew / scaleOld;

            setValue(mSuppMatrix, Matrix.MTRANS_X, translateFixedX);
            setValue(mSuppMatrix, Matrix.MTRANS_Y, translateFixedY);

            setImageMatrix(getImageViewMatrix());
            if (mSuppMatrix.isIdentity()) {
                zoomTo(getMinZoom());
            }

        }
    }

    protected void onZoom(float scale) {
    }

    protected void onZoomAnimationCompleted(float scale) {
    }

    protected void panBy(double dx, double dy) {
        RectF rect = getBitmapRect();
        mScrollRect.set((float) dx, (float) dy, 0, 0);
        updateRect(rect, mScrollRect);
        postTranslate(mScrollRect.left, mScrollRect.top);
        center(true, true);
    }

    protected void postScale(float scale, float centerX, float centerY) {
        mSuppMatrix.postScale(scale, scale, centerX, centerY);
        setImageMatrix(getImageViewMatrix());
    }

    protected void postTranslate(float deltaX, float deltaY) {
        mSuppMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(getImageViewMatrix());
    }

    public void scrollBy(float x, float y) {
        panBy(x, y);
    }

    protected void scrollBy(float distanceX, float distanceY,
                            final double durationMs) {
        final double dx = distanceX;
        final double dy = distanceY;
        final long startTime = System.currentTimeMillis();
        mHandler.post(new Runnable() {

            double old_x = 0;

            double old_y = 0;

            @Override
            public void run() {
                long now = System.currentTimeMillis();
                double currentMs = Math.min(durationMs, now - startTime);
                double x = mEasing.easeOut(currentMs, 0, dx, durationMs);
                double y = mEasing.easeOut(currentMs, 0, dy, durationMs);
                panBy(x - old_x, y - old_y);
                old_x = x;
                old_y = y;
                if (currentMs < durationMs) {
                    mHandler.post(this);
                } else {
                    RectF centerRect = getCenter(mSuppMatrix, true, true);
                    if (centerRect.left != 0 || centerRect.top != 0) {
                        scrollBy(centerRect.left, centerRect.top);
                    }
                }
            }
        });
    }

    public void setFitToScreen(boolean value) {
        if (value != mFitToScreen) {
            mFitToScreen = value;
            requestLayout();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        setImageBitmap(bm, true);
    }

    /**
     * Set the new image to display and reset the internal matrix.
     *
     * @param bitmap - the {@link Bitmap} to display
     * @param reset  - if true the image bounds will be recreated, otherwise the
     *               old {@link Matrix} is used to display the new bitmap
     * @see #setImageBitmap(Bitmap)
     */
    public void setImageBitmap(final Bitmap bitmap, final boolean reset) {
        setImageBitmap(bitmap, reset, null);
    }

    /**
     * Similar to {@link #setImageBitmap(Bitmap, boolean)} but an optional view
     * {@link Matrix} can be passed to determine the new bitmap view matrix.<br />
     * This method is useful if you need to restore a Bitmap with the same
     * zoom/pan values from a previous state
     *
     * @param bitmap - the {@link Bitmap} to display
     * @param reset
     * @param matrix - the {@link Matrix} to be used to display the new bitmap
     * @see #setImageBitmap(Bitmap, boolean)
     * @see #setImageBitmap(Bitmap)
     * @see #getImageViewMatrix()
     * @see #getDisplayMatrix()
     */
    public void setImageBitmap(final Bitmap bitmap, final boolean reset,
                               Matrix matrix) {
        setImageBitmap(bitmap, reset, matrix, -1);
    }

    /**
     * @param bitmap
     * @param reset
     * @param matrix
     * @param maxZoom - maximum zoom allowd during zoom gestures
     * @see #setImageBitmap(Bitmap, boolean, Matrix)
     */
    public void setImageBitmap(final Bitmap bitmap, final boolean reset,
                               Matrix matrix, float maxZoom) {

        if (bitmap != null) {
            setImageDrawable(new FastBitmapDrawable(bitmap), reset, matrix,
                    maxZoom);
        } else {
            setImageDrawable(null, reset, matrix, maxZoom);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        setImageDrawable(drawable, true, null, -1);
    }

    public void setImageDrawable(final Drawable drawable, final boolean reset,
                                 final Matrix initial_matrix, final float maxZoom) {

        final int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if (initial_matrix != null) {
            mSuppMatrix = new Matrix(initial_matrix);
        }
        if (viewWidth <= 0) {
            mOnLayoutRunnable = new Runnable() {

                @Override
                public void run() {
                    setImageDrawable(drawable, reset, initial_matrix, maxZoom);
                }
            };
            return;
        }

        _setImageDrawable(drawable, reset, initial_matrix, maxZoom);
    }

    @Override
    public void setImageResource(int resId) {
        setImageDrawable(getContext().getResources().getDrawable(resId));
    }

    public void setOnBitmapChangedListener(OnBitmapChangedListener listener) {
        mListener = listener;
    }

    public void setSuppMatrix(Matrix m) {
        mSuppMatrix.set(m);
    }

    private void setValue(Matrix mSuppMatrix, int field, float value) {
        float[] values = new float[10];
        mSuppMatrix.getValues(values);
        values[field] = value;
        mSuppMatrix.setValues(values);
    }

    protected void updateRect(RectF bitmapRect, RectF scrollRect) {
        if (bitmapRect == null) {
            return;
        }

        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        float height = getHeight() - getPaddingTop() - getPaddingBottom();

        if (bitmapRect.top >= 0 && bitmapRect.bottom <= height) {
            scrollRect.top = 0;
        }
        if (bitmapRect.left >= 0 && bitmapRect.right <= width) {
            scrollRect.left = 0;
        }
        if (bitmapRect.top + scrollRect.top >= 0 && bitmapRect.bottom > height) {
            scrollRect.top = (int) (0 - bitmapRect.top);
        }
        if (bitmapRect.bottom + scrollRect.top <= height - 0
                && bitmapRect.top < 0) {
            scrollRect.top = (int) (height - 0 - bitmapRect.bottom);
        }
        if (bitmapRect.left + scrollRect.left >= 0) {
            scrollRect.left = (int) (0 - bitmapRect.left);
        }
        if (bitmapRect.right + scrollRect.left <= width - 0) {
            scrollRect.left = (int) (width - 0 - bitmapRect.right);
        }
    }

    protected void zoomTo(float scale) {
        float cx = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2F;
        float cy = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2F;
        zoomTo(scale, cx, cy);
    }

    public void zoomTo(float scale, float durationMs) {
        float cx = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2F;
        float cy = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2F;
        zoomTo(scale, cx, cy, durationMs);
    }

    protected void zoomTo(float scale, float centerX, float centerY) {
        if (scale > mMaxZoom) {
            scale = mMaxZoom;
        }
        float oldScale = getScale();
        float deltaScale = scale / oldScale;
        Log.d(LOG_TAG, "zoomTo: " + scale + ", center: " + centerX + "x"
                + centerY + "   minzoom=" + getMinZoom());
        postScale(deltaScale, centerX, centerY);
        onZoom(getScale());
        center(true, true);
    }

    protected void zoomTo(float scale, float centerX, float centerY,
                          final float durationMs) {
        if (scale > getMaxZoom()) {
            scale = getMaxZoom();
        }
        final long startTime = System.currentTimeMillis();
        final float oldScale = getScale();

        final float deltaScale = scale - oldScale;

        Matrix m = new Matrix(mSuppMatrix);
        m.postScale(scale, scale, centerX, centerY);
        RectF rect = getCenter(m, true, true);

        final float destX = centerX + rect.left * scale;
        final float destY = centerY + rect.top * scale;

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                long now = System.currentTimeMillis();
                float currentMs = Math.min(durationMs, now - startTime);
                float newScale = (float) mEasing.easeInOut(currentMs, 0,
                        deltaScale, durationMs);
                zoomTo(oldScale + newScale, destX, destY);
                if (currentMs < durationMs) {
                    mHandler.post(this);
                } else {
                    onZoomAnimationCompleted(getScale());
                    center(true, true);
                }
            }
        });
    }

    public interface OnBitmapChangedListener {

        void onBitmapChanged(Drawable drawable);
    }
}
