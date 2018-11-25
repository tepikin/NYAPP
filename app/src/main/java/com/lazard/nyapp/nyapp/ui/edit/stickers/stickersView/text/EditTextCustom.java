package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.lazard.nyapp.nyapp.util.Utils;

/**
 * com.camlyapp.Camly.ui.edit.view.scale.text.EditTextCustom
 * 
 * @author Egor
 */
public class EditTextCustom extends AppCompatEditText {

    private TextModel model = new TextModel();

    private float maxTextSize;

    public EditTextCustom(Context context) {
        super(context);
        init();
    }

    public EditTextCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextModel getModel() {
        return model;
    }

    private int getRowsCount(CharSequence text) {
        int rows = 1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == "\n".charAt(0)) {
                rows++;
            }
        }
        return rows;
    }

    private Rect getTextRect(CharSequence text) {
        TextPaint paint = getPaint();
        int widthIn = (int) StaticLayout.getDesiredWidth(text, paint);
        StaticLayout staticLayout = new StaticLayout(text, getPaint(), widthIn,
                Alignment.ALIGN_CENTER, 1.0f, 0, false);
        return new Rect(0, 0, staticLayout.getWidth(), staticLayout.getHeight());

    }

    private void init() {
        maxTextSize = getTextSize();
        try {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private boolean isTextContains(CharSequence text, int width, int height) {

        float aditionalArea = Utils.INSTANCE.dpToPx(10, getContext());

        float externalWidth = width - aditionalArea;
        float externalHeight = height - aditionalArea;

        Rect rect = getTextRect(text);
        if (rect.width() > externalWidth || rect.height() > externalHeight) {
            return false;
        }
        return true;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        return super.onCreateInputConnection(outAttrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setScrollX(0);
            setScrollY(0);
        }

        int restoreCount1 = canvas.save();

        setTextColor(model.getColor());
        getPaint().setColor(model.getColor());
        getPaint().setStyle(Paint.Style.FILL);
        getPaint().setStrokeWidth(0);
        getPaint().setUnderlineText(false);

        // int width = (int) StaticLayout.getDesiredWidth(getText(),
        // getPaint());
        // StaticLayout layout = new StaticLayout(getText(), getPaint(), width,
        // Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

        // //
        // canvas.save();
        // canvas.translate((getWidth() - width) / 2,
        // (getHeight() - layout.getHeight()) / 2);
        // layout.draw(canvas);
        // canvas.restore();
        // /

        super.onDraw(canvas);
        canvas.restoreToCount(restoreCount1);

        int restoreCount2 = canvas.save();
        if (model != null && model.getBorderPercent() != 0) {
            int borderColor = model.getBorderColor();
            float borderWidth = model.getBorderWidth();

            setTextColor(borderColor);
            getPaint().setColor(borderColor);
            getPaint().setStyle(Paint.Style.STROKE);
            getPaint().setStrokeWidth(borderWidth);
            getPaint().setUnderlineText(false);

            // //
            // canvas.save();
            // canvas.translate((getWidth() - width) / 2,
            // (getHeight() - layout.getHeight()) / 2);
            // layout.draw(canvas);
            // canvas.restore();
            // //

            super.onDraw(canvas);

        }
        canvas.restoreToCount(restoreCount2);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        if (changed) {
            resizeText(getText(), right - left, bottom - top);
            new Handler().post(new Runnable() {

                @Override
                public void run() {
                    resizeText(getText().toString());
                    requestLayout();
                }
            });

        }
        super.onLayout(changed, left, top, right, bottom);
    }

    public void resizeText(CharSequence text) {
        resizeText(text, getWidth(), getHeight());
    }

    public void resizeText(CharSequence text, int width, int height) {
        try {
            int paddings = (int) Utils.INSTANCE.dpToPx(20, getContext());
            width -= paddings;
            height -= paddings / getRowsCount(text);
            if (TextUtils.isEmpty(text)) {
                return;
            }
            if (width <= 0 || height <= 0) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        resizeText(getText());
                    }
                });
                return;
            }

            // setTextSize(TypedValue.COMPLEX_UNIT_PX, maxTextSize);

            TextPaint textPaint = getPaint();
            float targetTextSize = textPaint.getTextSize();

            // scale up text size
            while (isTextContains(text, width, height)
                    && targetTextSize < maxTextSize) {
                targetTextSize++;
                setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
                textPaint.setTextSize(targetTextSize);
            }

            while (!isTextContains(text, width, height) && targetTextSize > 0) {
                targetTextSize--;
                setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
                textPaint.setTextSize(targetTextSize);
            }

            setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
            textPaint.setTextSize(targetTextSize);

            postInvalidate();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setModel(TextModel model) {
        this.model = model;
    }
}
