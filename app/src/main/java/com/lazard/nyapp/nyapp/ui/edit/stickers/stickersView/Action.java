package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.graphics.Bitmap;
import android.graphics.PointF;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Created by Egor on 21.09.2015.
 */
public interface Action {
    Bitmap apply(Bitmap bitmap) throws IOException;


}
