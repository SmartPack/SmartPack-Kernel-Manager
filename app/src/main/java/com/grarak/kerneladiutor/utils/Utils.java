/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.root.RootUtils;

/**
 * Created by willi on 30.11.14.
 */
public class Utils implements Constants {

    public static long stringToLong(String string) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int stringToInt(String string) {
        try {
            return Math.round(Float.parseFloat(string));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void launchUrl(Context context, String link) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }

    public static int getActionBarHeight(Context context) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        int actionBarSize = ta.getDimensionPixelSize(0, 100);
        ta.recycle();
        return actionBarSize;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static int getScreenOrientation(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels <
                context.getResources().getDisplayMetrics().heightPixels ?
                Configuration.ORIENTATION_PORTRAIT : Configuration.ORIENTATION_LANDSCAPE;
    }

    public static void toast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean getBoolean(String name, boolean defaults, Context context) {
        Log.i(TAG, "getting " + name);
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(name, defaults);
    }

    public static void saveBoolean(String name, boolean value, Context context) {
        Log.i(TAG, "saving " + name + " as " + value);
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(name, value).apply();
    }

    public static String getString(String name, String defaults, Context context) {
        Log.i(TAG, "getting " + name);
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(name, defaults);
    }

    public static void saveString(String name, String value, Context context) {
        Log.i(TAG, "saving " + name + " as " + value);
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(name, value).apply();
    }

    public static boolean existFile(String file) {
        String output = RootUtils.runCommand("[ -e " + file + " ] && echo true");
        return output != null && output.contains("true");
    }

    public static String readFile(String file) {
        return RootUtils.runCommand("cat " + file);
    }

}
