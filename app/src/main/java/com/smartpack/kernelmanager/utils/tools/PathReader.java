/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.kernelmanager.utils.tools;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 21, 2021
 */
public class PathReader {

    private static String mTitle, mPath, mError, mCategory;
    private static int mMin = -1, mMax = -1;

    public static String getTitle() {
        return mTitle;
    }

    public static String getPath() {
        return mPath;
    }

    public static String getError() {
        return mError;
    }

    public static String getCategory() {
        return mCategory;
    }

    public static int getMin() {
        return mMin;
    }

    public static int getMax() {
        return mMax;
    }

    public static void setTitle(String title) {
        mTitle = title;
    }

    public static void setPath(String path) {
        mPath = path;
    }

    public static void setError(String error) {
        mError = error;
    }

    public static void setCategory(String category) {
        mCategory = category;
    }

    public static void setMin(int min) {
        mMin = min;
    }

    public static void setMax(int max) {
        mMax = max;
    }

}