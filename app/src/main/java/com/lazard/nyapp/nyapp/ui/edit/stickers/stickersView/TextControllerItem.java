package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.lazard.nyapp.nyapp.R;
import com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView.text.TextModel;
import com.lazard.nyapp.nyapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class TextControllerItem extends ControllerItem {

    private static final String TEST_STRING = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";

    private Drawable drawableEdit;

    private TextPaint paint;

    // private Drawable drawableOrientation;

    // protected boolean orientationIsHorizontal = true;

    private int HEIGHT_TEXT_SPACER = 4;

    private int heightTextSpacer = 0;

    private TextModel textModel;

    protected boolean isVisible = true;

    public TextControllerItem(TextModel model, Context context,
            ImageViewRotate imageViewRotate) {
        super(context, imageViewRotate);

        heightTextSpacer = (int) Utils.INSTANCE.dpToPx(HEIGHT_TEXT_SPACER, context);

        paint = new TextPaint();
        paint.setFlags(paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextSize(Utils.INSTANCE.dpToPx(60, context));
        paint.setTypeface(Typeface.MONOSPACE);

        this.textModel = model;

        paint.setColor(textModel.getColor());
        paint.setTextScaleX(1 + textModel.getScalePercent());

        if (!TextUtils.isEmpty(textModel.getFont())) {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                    textModel.getFont());
            paint.setTypeface(typeface);
        }

        updateImageRect();

        setColor(textModel.getColor());

        // initMatrix
        matrix = new Matrix();
        matrixBorder = new Matrix();

        drawableEdit = context.getResources().getDrawable(
                R.drawable.edit_tool_masks_text_edit);
        // drawableOrientation = context.getResources().getDrawable(
        // R.drawable.edit_tool_masks_orientation);

    }

    @Override
    public void draw(Canvas canvas) {
        if (!isVisible) {
            return;
        }
        canvas.save();
        canvas.concat(matrix);
        // canvas.drawText(text, 0, 0, paint);
        paint.setColor(textModel.getColor());
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        drawText(canvas);
        if (textModel.getBorderPercent() > 0) {
            paint.setColor(textModel.getBorderColor());
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(textModel.getBorderWidth());
            drawText(canvas);
        }

        canvas.restore();
    }

    @Override
    public void drawBorder(Canvas canvas, int alpha, Matrix imageMatrix) {
        if (!isVisible) {
            return;
        }
        super.drawBorder(canvas, alpha, imageMatrix);
    }

    @Override
    protected void drawButtons(Canvas canvas, float[] points, int alpha) {
        drawDrawable(drawableEdit, points[0], points[1], alpha, canvas);
        // drawDrawable(drawableOrientation, points[6], points[7], alpha,
        // canvas);
    }

    private void drawText(Canvas canvas) {
        int save = canvas.save();

        // work around for android 2.3 round issue
        canvas.translate(-1, 0);

        String text = textModel.getText();
        int width = (int) StaticLayout.getDesiredWidth(text, paint);
        StaticLayout layout = new StaticLayout(text, paint, width + 2,
                Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        layout.draw(canvas);

        canvas.restoreToCount(save);
    }

    @Override
    public List<Drawable> getAllDrawables() {
        List<Drawable> list = new ArrayList<Drawable>();
        list.add(getRotateDrawable());
        list.add(drawableEdit);
        // list.add(drawableOrientation);
        return list;
    }

    public TextModel getTextModel() {
        return textModel;
    }

    @Override
    public boolean onDoubleTap(PointF point) {

        float[] pointF = new float[] { point.x, point.y };
        Matrix inverse = new Matrix(imageView.getImageMatrix());
        Matrix matrixInvert = new Matrix();
        inverse.invert(matrixInvert);
        matrixInvert.mapPoints(pointF);
        point.set(pointF[0], pointF[1]);

        pointF = new float[] { point.x, point.y };
        inverse = new Matrix(matrix);
        inverse.invert(matrixInvert);
        matrixInvert.mapPoints(pointF);
        if (pointF[0] >= imageRect.left && pointF[0] < imageRect.right
                && pointF[1] >= imageRect.top && pointF[1] < imageRect.bottom) {
            imageView.onTextEditClick();
            return true;
        }

        return false;
    }

    @Override
    public boolean onSingleTapUp(PointF point) {
        float padding = Utils.INSTANCE.dpToPx(8, imageView.getContext());

        if (inDrawable(drawableEdit, point)) {
            imageView.onTextEditClick();
            return true;
        }

        return false;
    }

    public void setTextModel(TextModel model) {
        this.textModel = model;

        paint.setColor(textModel.getColor());
        paint.setTextScaleX(1 + textModel.getScalePercent());

        if (!TextUtils.isEmpty(textModel.getFont())) {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                    textModel.getFont());
            paint.setTypeface(typeface);
        }

        updateImageRect();

        setColor(textModel.getColor());
    }

    public void updateImageRect() {
        float paddings = Utils.INSTANCE.dpToPx(10,context);

        Rect bounds = new Rect();
        paint.getTextBounds(textModel.getText(), 0, textModel.getText()
                .length(), bounds);

        int width = (int) StaticLayout.getDesiredWidth(textModel.getText(),
                paint);

        StaticLayout layout = new StaticLayout(textModel.getText(), paint,
                width, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

        int width2 = layout.getWidth();
        int height = layout.getHeight();
        imageRect = new RectF(0, 0, width2, height);
        increaseRect(paddings, imageRect);

    }
}
