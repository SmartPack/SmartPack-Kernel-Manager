/*
 * Copyright (c) Gustavo Claramunt (AnderWeb) 2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.adw.library.widgets.discreteseekbar.internal.compat;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import org.adw.library.widgets.discreteseekbar.internal.drawable.MarkerDrawable;

public class SeekBarCompat {

    public static void setOutlineProvider(View view, final MarkerDrawable markerDrawable) {
        SeekBarCompatDontCrash.setOutlineProvider(view, markerDrawable);
    }

    public static Drawable getRipple(ColorStateList colorStateList) {
        return SeekBarCompatDontCrash.getRipple(colorStateList);
    }

    public static void setRippleColor(@NonNull Drawable drawable, ColorStateList colorStateList) {
        ((RippleDrawable) drawable).setColor(colorStateList);
    }

    public static void setHotspotBounds(Drawable drawable, int left, int top, int right, int bottom) {
        int size = (right - left) / 8;
        DrawableCompat.setHotspotBounds(drawable, left + size, top + size, right - size, bottom - size);
    }

    public static void setBackground(View view, Drawable background) {
        SeekBarCompatDontCrash.setBackground(view, background);
    }

    public static void setTextDirection(TextView textView, int textDirection) {
        SeekBarCompatDontCrash.setTextDirection(textView, textDirection);
    }

    public static boolean isInScrollingContainer(ViewParent p) {
        return SeekBarCompatDontCrash.isInScrollingContainer(p);
    }

    public static boolean isHardwareAccelerated(View view) {
        return SeekBarCompatDontCrash.isHardwareAccelerated(view);
    }
}
