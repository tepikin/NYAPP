package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.animation.Animation;
import android.widget.ImageView;


import com.lazard.nyapp.nyapp.BaseApplication;
import com.lazard.nyapp.nyapp.R;
import com.lazard.nyapp.nyapp.ui.edit.EditActivity;
import com.lazard.nyapp.nyapp.util.Utils;
import com.lazard.nyapp.nyapp.util.UtilsRect;

import java.util.ArrayList;
import java.util.List;

public abstract class ControllerItem {

    protected Matrix matrix = new Matrix();

    protected Matrix matrixBorder = new Matrix();

    protected RectF imageRect = new RectF();

    protected int color = -1;

    protected ImageViewRotate imageView;

    private Paint paint = new Paint();

    private float strokeWidthDark = Utils.INSTANCE.dpToPx(2.5f,
                                                  BaseApplication.Companion.getInstance());

    private float strokeWidthLight = Utils.INSTANCE.dpToPx(1.5f,
            BaseApplication.Companion.getInstance());

    private int borderColorDark = Color.parseColor("#66BDBDBD");

    private int borderColorLight = Color.parseColor("#B21976D2");

    private Drawable drawableRotate;

    private float paddingDrawable;

    protected Context context;

    private float MAX_SCALE = 3;

    public ControllerItem(Context context, ImageViewRotate imageViewRotate) {
        super();
        this.context = context;
        setImageView(imageViewRotate);

        drawableRotate = context.getResources().getDrawable(
                R.drawable.edit_tool_masks_rotate);
        paddingDrawable = Utils.INSTANCE.dpToPx(8, context);
    }

    public abstract void draw(Canvas canvas);

    private void drawBorder(Canvas canvas, float[] points) {
        canvas.drawLine(points[0], points[1], points[2], points[3], paint);
        canvas.drawLine(points[2], points[3], points[4], points[5], paint);
        canvas.drawLine(points[4], points[5], points[6], points[7], paint);
        canvas.drawLine(points[6], points[7], points[0], points[1], paint);
    }

    public void drawBorder(Canvas canvas, int alpha, Matrix imageMatrix) {

        Matrix convert = new Matrix(imageMatrix);
        convert.preConcat(matrixBorder);

        float[] points = new float[] { imageRect.left, imageRect.top,//
                imageRect.right, imageRect.top,//
                imageRect.right, imageRect.bottom,//
                imageRect.left, imageRect.bottom, //
                (imageRect.left + imageRect.right) / 4, imageRect.bottom //
        };
        convert.mapPoints(points);

        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        paint.setStrokeWidth(strokeWidthDark);
        paint.setColor(borderColorDark);
        paint.setAlpha(alpha * 40 / 100);
        drawBorder(canvas, points);

        paint.setStrokeWidth(strokeWidthLight);
        paint.setColor(borderColorLight);
        paint.setAlpha(alpha * 70 / 100);
        drawBorder(canvas, points);

        drawButtons(canvas, points, alpha);

        drawDrawable(drawableRotate, points[4], points[5], alpha, canvas);

    }

    protected abstract void drawButtons(Canvas canvas, float[] points, int alpha);

    protected void drawDrawable(Drawable drawable, float x, float y, int alpha,
            Canvas canvas) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        int left = (int) (x - width / 2);
        int top = (int) (y - height / 2);
        int right = left + width;
        int bottom = top + height;

        drawable.setAlpha(alpha);
        drawable.setBounds(left, top, right, bottom);
        drawable.draw(canvas);
    }

    public void flipHofizontal() {
        // matrix.preScale(-1, 1, imageRect.centerX(), imageRect.centerY());

        if (imageView.getAnimation() != null) {
            imageView.getAnimation().cancel();
        }
        Animation anim = new AnimationScale() {

            @Override
            public void onScale(float scale) {
                scaleAnimate(scale, 1);
                imageView.invalidate();
            }
        };
        imageView.startAnimation(anim);
        imageView.invalidate();

    }

    public void flipVertical() {
        // matrix.preScale(1, -1, imageRect.centerX(), imageRect.centerY());

        if (imageView.getAnimation() != null) {
            imageView.getAnimation().cancel();
        }
        Animation anim = new AnimationScale() {

            @Override
            public void onScale(float scale) {
                scaleAnimate(1, scale);
                imageView.invalidate();
            }
        };
        imageView.startAnimation(anim);

        imageView.invalidate();

    }

    public List<Drawable> getAllDrawables() {
        List<Drawable> list = new ArrayList<Drawable>();
        list.add(getRotateDrawable());
        return list;
    }

    public PointF getCenter() {
        float[] center = new float[] { (imageRect.right + imageRect.left) / 2,
                (imageRect.top + imageRect.bottom) / 2 };
        matrix.mapPoints(center);
        return new PointF(center[0], center[1]);
    }

    public int getColor() {
        return color;
    }

    public RectF getImageRect() {
        return imageRect;
    }
public PointF[] getDrawRect01(int bitmapWidth,int bitmapHeight){
    PointF[] point = UtilsRect.rectToPoints(imageRect);
    Matrix m = new Matrix(matrix);
    m.postScale(1f / bitmapWidth, 1f / bitmapHeight);
    point = UtilsRect.applayMatrixToPoints(point,m);
return point ;
}
    public ImageView getImageView() {
        return imageView;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public Matrix getMatrixBorder() {
        return matrixBorder;
    }

    public Drawable getRotateDrawable() {
        return drawableRotate;
    }

    protected void increaseRect(float padding, RectF rect) {
        rect.top -= padding;
        rect.left -= padding;
        rect.right += padding;
        rect.bottom += padding;
    }

    public boolean inDrawable(Drawable drawable, PointF point) {
        RectF rect = new RectF();
        rect.set(drawable.getBounds());
        increaseRect(paddingDrawable, rect);
        if (rect.contains(point.x, point.y)) {
            return true;
        }
        return false;
    }

    public boolean isPointIn(PointF point, boolean byTransparent) {
        float[] pointF = new float[] { point.x, point.y };
        Matrix inverse = new Matrix(matrix);
        Matrix matrixInvert2 = new Matrix();
        inverse.invert(matrixInvert2);
        matrixInvert2.mapPoints(pointF);
        if (pointF[0] >= imageRect.left && pointF[0] < imageRect.right
                && pointF[1] >= imageRect.top && pointF[1] < imageRect.bottom) {
            // return true;

            if (!byTransparent) {
                return true;
            }

            Bitmap bitmap = null;
            try {
                int width = (int) imageRect.width();
                int height = (int) imageRect.height();
                bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                canvas.save();
                Matrix matrixInvert = new Matrix(matrix);
                Matrix matrixInvert3 = new Matrix();
                matrixInvert.invert(matrixInvert3);
                canvas.translate(-imageRect.left, -imageRect.top);
                canvas.concat(matrixInvert3);
                draw(canvas);
                canvas.restore();

                pointF[0] += -imageRect.left;
                pointF[1] += -imageRect.top;

                Paint paint2 = new Paint();
                paint2.setStrokeWidth(5);
                paint2.setColor(Color.RED);
                canvas.drawLine(pointF[0], pointF[1], pointF[0], pointF[1],
                        paint2);

                // imageView.setImageBitmap(bitmap);

                float initialToucSlop = 10f;
                int touchSlop = (int) matrixInvert3.mapRadius(initialToucSlop);
                touchSlop = (int) Math.min(initialToucSlop, touchSlop);
                for (int x = (int) pointF[0] - touchSlop; x <= (int) pointF[0]
                        + touchSlop; x++) {
                    for (int y = (int) pointF[1] - touchSlop; y <= (int) pointF[1]
                            + touchSlop; y++) {
                        if (x < 0 || y < 0 || x >= bitmap.getWidth()
                                || y >= bitmap.getHeight()) {
                            continue;
                        }
                        if (bitmap.getPixel(x, y) != Color.TRANSPARENT) {
                            bitmap.recycle();
                            return true;
                        }
                    }
                }
                bitmap.recycle();
            } catch (Throwable e) {
                e.printStackTrace();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }

        }
        return false;
    }

    public boolean isScaleAllow(float scaleFactor, PointF center) {
        // if (Math.abs(1 - scaleFactor) > 0.5) {
        // return false;
        // }
        float maxScale = MAX_SCALE;
        if (getImageView().getContext() instanceof EditActivity) {
            EditActivity activity = (EditActivity) getImageView().getContext();
            Bitmap bitmap = activity.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                float width = bitmap.getWidth();
                float height = bitmap.getHeight();
                float widthRect = imageRect.width();
                float heightRect = imageRect.height();

                if (width > 0 && height > 0 && widthRect > 0 && heightRect > 0) {
                    maxScale = width / widthRect;
                    maxScale = Math.max(height / heightRect, maxScale);
                }

            }
        }

        Matrix matrixTemp = new Matrix(getMatrix());
        matrixTemp.postScale(scaleFactor, scaleFactor, center.x, center.y);
        float initRadius = 1;
        float radius = matrixTemp.mapRadius(initRadius);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return radius < initRadius * maxScale
                    && radius > initRadius * maxScale / 10;
        } else {
            return radius < initRadius * maxScale;
        }
    }

    public abstract boolean onDoubleTap(PointF point);

    public abstract boolean onSingleTapUp(PointF point);

    public void rotateCenter(float digres) {
        PointF center = getCenter();
        matrix.postRotate(digres, center.x, center.y);
        matrixBorder.postRotate(digres, center.x, center.y);
    }

    public void rotateCenterAnimation(float digres) {
        if (imageView.getAnimation() != null) {
            imageView.getAnimation().cancel();
        }
        Animation anim = new AnimationRotate(digres) {

            @Override
            protected void onRotate(double angleDiff) {
                rotateCenter((float) angleDiff);
                imageView.invalidate();
            }

        };
        imageView.startAnimation(anim);
        imageView.invalidate();
    }

    public void rotateCenterAnimationShake() {
        if (imageView.getAnimation() != null) {
            imageView.getAnimation().cancel();
        }
        Animation anim = new AnimationRotateShake() {

            @Override
            protected void onRotate(double angleDiff) {
                rotateCenter((float) angleDiff);
                imageView.invalidate();
            }

        };
        imageView.startAnimation(anim);
        imageView.invalidate();
    }

    public void scaleAnimate(float scaleX, float scaleY) {
        matrix.preScale(scaleX, scaleY, imageRect.centerX(),
                imageRect.centerY());
    }

    public void scaleCenter(float scaleFactor) {
        PointF center = getCenter();
        if (!isScaleAllow(scaleFactor, center)) {
            return;
        }

        matrix.postScale(scaleFactor, scaleFactor, center.x, center.y);
        matrixBorder.postScale(scaleFactor, scaleFactor, center.x, center.y);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setImageView(ImageViewRotate imageView) {
        this.imageView = imageView;
    }

    public void translate(float distanceX, float distanceY) {
        matrix.postTranslate(distanceX, distanceY);
        matrixBorder.postTranslate(distanceX, distanceY);
    }

    public void translateAnimation(float distance) {

        if (imageView.getAnimation() != null) {
            imageView.getAnimation().cancel();
        }
        Animation anim = new AnimationTranslate(distance) {

            @Override
            protected void onTranslate(float translateDiff) {
                translate(translateDiff, translateDiff);
                imageView.invalidate();
            }
        };
        imageView.startAnimation(anim);

        imageView.invalidate();

    }

}
