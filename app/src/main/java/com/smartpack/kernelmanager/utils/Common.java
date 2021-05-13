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

package com.smartpack.kernelmanager.utils;

import android.content.Context;

import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;

import com.smartpack.kernelmanager.R;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 08, 2021
 */
public class Common {

    private static BiometricPrompt.PromptInfo mPromptInfo;
    private static boolean mBusybox, mFlashing = false, mMagiskModule = false, mRoot, mUpdateCheck = false, mWritableRoot = true;
    private static Fragment mSelectedFragment = null;
    private static final List<String> mFlashingOutput = new ArrayList<>();
    private static String mDetailsTitle = null, mDetailsTxt = null, mUrl, mWebViewTitle = null, mZipName;
    private static final StringBuilder mFlashingResult = new StringBuilder();

    public static BiometricPrompt.PromptInfo getPromptInfo() {
        return mPromptInfo;
    }

    public static boolean hasBusyBox() {
        return mBusybox;
    }

    public static boolean isFlashing() {
        return mFlashing;
    }

    public static boolean isMagiskModule() {
        return mMagiskModule;
    }

    public static boolean hasRoot() {
        return mRoot;
    }

    public static boolean isUpdateCheckEnabled() {
        return mUpdateCheck;
    }

    public static boolean isWritableRoot() {
        return mWritableRoot;
    }

    public static Fragment getSelectedFragment() {
        return mSelectedFragment;
    }

    public static List<String> getFlashingOutput() {
        return mFlashingOutput;
    }

    public static StringBuilder getFlashingResult() {
        return mFlashingResult;
    }

    public static String getDetailsTitle() {
        return mDetailsTitle;
    }

    public static String getDetailsTxt() {
        return mDetailsTxt;
    }

    public static String getUrl() {
        return mUrl;
    }

    public static String getWebViewTitle() {
        return mWebViewTitle;
    }

    public static String getZipName() {
        return mZipName;
    }

    public static void showBiometricPrompt(Context context) {
        mPromptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.authenticate))
                .setNegativeButtonText(context.getString(R.string.cancel))
                .build();
    }

    public static void hasBusyBox(boolean b) {
        mBusybox = b;
    }

    public static void isFlashing(boolean b) {
        mFlashing = b;
    }

    public static void isMagiskModule(boolean b) {
        mMagiskModule = b;
    }

    public static void hasRoot(boolean b) {
        mRoot = b;
    }

    public static void enableUpdateCheck(boolean b) {
        mUpdateCheck = b;
    }

    public static void isWritableRoot(boolean b) {
        mWritableRoot = b;
    }

    public static void setSelectedFragment(Fragment fragment) {
        mSelectedFragment = fragment;
    }

    public static void setDetailsTitle(String detailsTitle) {
        mDetailsTitle = detailsTitle;
    }

    public static void setDetailsTxt(String detailsTxt) {
        mDetailsTxt = detailsTxt;
    }

    public static void setUrl(String url) {
        mUrl = url;
    }

    public static void setWebViewTitle(String webViewTitle) {
        mWebViewTitle = webViewTitle;
    }

    public static void setZipName(String zipName) {
        mZipName = zipName;
    }

}