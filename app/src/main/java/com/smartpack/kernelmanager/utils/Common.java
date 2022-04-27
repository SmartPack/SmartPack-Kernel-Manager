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

import in.sunilpaulmathew.sCommon.Utils.sSerializableItems;

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

    public static List<sSerializableItems> getCredits() {
        List<sSerializableItems> mData = new ArrayList<>();
        mData.add(new sSerializableItems(null, "Willi Ye", "Kernel Adiutor", "https://github.com/Grarak/KernelAdiutor"));
        mData.add(new sSerializableItems(null, "Chris Renshaw", "Auto-flashing", "https://github.com/osm0sis"));
        mData.add(new sSerializableItems(null, "John Wu", "libsu", "https://github.com/topjohnwu/libsu"));
        mData.add(new sSerializableItems(null, "Joe Maples", "Spectrum", "https://github.com/frap129/spectrum"));
        mData.add(new sSerializableItems(null, "CyanogenMod", "Platform SDK", "https://github.com/CyanogenMod/cm_platform_sdk"));
        mData.add(new sSerializableItems(null, "Google", "AndroidX & NavigationView", null));
        mData.add(new sSerializableItems(null, "Ozodrukh", "CircularReveal", "https://github.com/ozodrukh/CircularReveal"));
        mData.add(new sSerializableItems(null, "Roman Nurik", "DashClock", "https://github.com/romannurik/dashclock"));
        mData.add(new sSerializableItems(null, "Square", "Picasso", "https://github.com/square/picasso"));
        mData.add(new sSerializableItems(null, "Akexorcist","Round Corner Progress Bar", "https://github.com/akexorcist/Android-RoundCornerProgressBar"));

        mData.add(new sSerializableItems(null, "Toxinpiper", "App Icon", "https://t.me/toxinpiper"));

        mData.add(new sSerializableItems(null, "jason5545", "Chinese (Traditional) Translations", "https://github.com/jason5545"));
        mData.add(new sSerializableItems(null, "Roiyaru", "Chinese (simplified) Translations", "https://github.com/Roiyaru"));
        mData.add(new sSerializableItems(null, "YFdyh000", "Chinese (simplified) Translations", "https://github.com/yfdyh000"));
        mData.add(new sSerializableItems(null, "Andrey","Russian Translations", "https://github.com/andrey167"));
        mData.add(new sSerializableItems(null, "tommynok", "Russian & Ukrainian Translations", null));
        mData.add(new sSerializableItems(null, "kiratt","Russian & Ukrainian Translations", "http://4pda.ru/forum/index.php?showuser=5859577"));
        mData.add(new sSerializableItems(null, "Lennoard Silva", "Portuguese (Brazilian) Translations", "https://github.com/Lennoard"));
        mData.add(new sSerializableItems(null, "FiestaLake", "Korean Translations", "https://github.com/FiestaLake"));
        mData.add(new sSerializableItems(null, "Mikesew1320", "Amharic Translations", "https://github.com/Mikesew1320"));
        mData.add(new sSerializableItems(null, "free-bots", "German Translations", "https://github.com/free-bots"));
        mData.add(new sSerializableItems(null, "Lars S", "German Translations", null));
        mData.add(new sSerializableItems(null, "Alejandro YT", "Spanish Translations", null));
        mData.add(new sSerializableItems(null, "Cold", "Spanish Translations", null));
        mData.add(new sSerializableItems(null, "Fruity-0", "Polish Translations", "https://github.com/Fruity-0"));
        mData.add(new sSerializableItems(null, "Fatih Fırıncı", "Turkish Translations", "https://github.com/Fatih-BaKeR"));
        mData.add(new sSerializableItems(null, "omerakgoz34 (BSÇE)", "Turkish Translations", "https://github.com/omerakgoz34"));
        mData.add(new sSerializableItems(null, "Phil", "Portuguese (Portugal) Translations", null));
        mData.add(new sSerializableItems(null, "Catellone94", "Italian Translations", null));
        mData.add(new sSerializableItems(null, "Gianfranco. liguori.96", "Italian Translations", null));
        mData.add(new sSerializableItems(null, "Re*Index.(ot_inc)", "Japanese Translations", null));
        return mData;
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