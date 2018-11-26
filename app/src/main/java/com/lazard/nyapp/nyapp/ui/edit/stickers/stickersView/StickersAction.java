package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView.text.TextModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Egor on 21.09.2015.
 */
public class StickersAction extends BaseAction {
private transient  Context context;

    public StickersAction(Context context, List<StickerItem> items) {
        this.context = context;
        this.items = items;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public StickersAction() {
    }

    public static class StickerItem {
        public enum Type {svg,png,text};
        private int color =-1;
        private RectF initRect;
        private String name;
        private PointF[] points;
        private TextModel textModel;
        private Type type;

        public int getColor() {
            return color;
        }

        public RectF getInitRect() {
            return initRect;
        }

        public String getName() {
            return name;
        }

        public PointF[] getPoints() {
            return points;
        }

        public TextModel getTextModel() {
            return textModel;
        }

        public Type getType() {
            return type;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public void setInitRect(RectF initRect) {
            this.initRect = initRect;
        }

        public void setName(String name) {
            this.name = name;
        }


        public void setPoints(PointF[] points) {
            this.points = points;
        }

        public void setTextModel(TextModel textModel) {
            this.textModel = textModel;
        }

        public void setType(Type type) {
            this.type = type;
        }

    }
    private List<StickerItem> items= new ArrayList<>();



    @Override
    protected Bitmap applyWithOOM(Bitmap bitmap) throws OutOfMemoryError {
        Canvas canvas = new Canvas(bitmap);
        StickerItemDrawer drawer = new StickerItemDrawer(context);
        for (StickerItem item : items) {
            drawer.draw(canvas,bitmap,item);
        }
        return bitmap;
    }
    public int getProgressWeight(){
        return 1;
    }
}
