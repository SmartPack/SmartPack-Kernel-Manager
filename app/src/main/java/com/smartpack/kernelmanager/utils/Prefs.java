/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.smartpack.kernelmanager.utils;

import android.content.Context;

import java.util.Set;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/**
 * Created by willi on 01.01.16.
 */
public class Prefs {

    public static int getInt(String name, int defaults, Context context) {
        return sCommonUtils.getInt(name, defaults, context);
    }

    public static long getLong(String name, long defaults, Context context) {
        return sCommonUtils.getLong(name, defaults, context);
    }

    public static void saveInt(String name, int value, Context context) {
        sCommonUtils.saveInt(name, value, context);
    }

    public static void saveLong(String name, long value, Context context) {
        sCommonUtils.saveLong(name, value, context);
    }

    public static boolean getBoolean(String name, boolean defaults, Context context) {
        return sCommonUtils.getBoolean(name, defaults, context);
    }

    public static void saveBoolean(String name, boolean value, Context context) {
        sCommonUtils.saveBoolean(name, value, context);
    }

    public static String getString(String name, String defaults, Context context) {
        return sCommonUtils.getString(name, defaults, context);
    }

    public static void saveString(String name, String value, Context context) {
        sCommonUtils.saveString(name, value, context);
    }

    public static void saveStringSet(String name, Set<String> value, Context context) {
        sCommonUtils.saveStringSet(name, value, context);
    }

    public static Set<String> getStringSet(String name, Set<String> defaults, Context context) {
        return sCommonUtils.getStringSet(name, defaults, context);
    }
}