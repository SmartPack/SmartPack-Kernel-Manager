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
package com.grarak.kerneladiutor.utils;

import android.app.ActivityManager;
import android.app.UiModeManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.grarak.kerneladiutor.BuildConfig;
import com.grarak.kerneladiutor.activities.StartActivity;
import com.grarak.kerneladiutor.utils.root.RootFile;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;

/**
 * Created by willi on 14.04.16.
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();
    public static boolean DONATED = BuildConfig.DEBUG;
    public static boolean DARK_THEME;

    public static String upperCaseEachWord(String text) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i == 0) {
                chars[i] = Character.toUpperCase(chars[0]);
            } else if (Character.isWhitespace(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
            }
        }

        return new String(chars);
    }

    public static boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return powerManager.isInteractive();
        }
        return powerManager.isScreenOn();
    }

    public static float getAverage(float... numbers) {
        float average = 0;
        for (float num : numbers) {
            average += num;
        }
        average /= numbers.length;
        return average;
    }

    public static String getRandomString(int length) {
        Random random = new Random();
        String text = "";
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < length; i++) {
            text += chars.charAt(random.nextInt(chars.length()));
        }
        return text;
    }

    public static long computeSHAHash(String password) throws Exception {
        long begin = System.nanoTime();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(password.getBytes("ASCII"));
        byte[] data = messageDigest.digest();
        Base64.encodeToString(data, 0, data.length, 0);
        return System.nanoTime() - begin;
    }

    public static String getAndroidId(Context context) {
        String id;
        if ((id = Prefs.getString("android_id", "", context)).isEmpty()) {
            Prefs.saveString("android_id", id = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID), context);
        }
        return id;
    }

    public static boolean isTv(Context context) {
        return ((UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE))
                .getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
    }

    public static void setStartActivity(boolean material, Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, StartActivity.class),
                material ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(BuildConfig.APPLICATION_ID,
                        BuildConfig.APPLICATION_ID + ".activities.StartActivity-Material"),
                material ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    public static boolean hideStartActivity() {
        RootUtils.SU su = new RootUtils.SU(false, null);
        String prop = su.runCommand("getprop ro.kerneladiutor.hide");
        su.close();
        return prop != null && prop.equals("true");
    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String decodeString(String text) {
        try {
            return new String(Base64.decode(text, Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encodeString(String text) {
        try {
            return Base64.encodeToString(text.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setLocale(String lang, Context context) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
    }

    public static boolean hasCMSDK() {
        return cyanogenmod.os.Build.CM_VERSION.SDK_INT >= cyanogenmod.os.Build.CM_VERSION_CODES.APRICOT;
    }

    public static CharSequence htmlFrom(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

    public static String getPath(Uri uri, Context context) {
        String path = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                path = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return path;
    }

    public static String getExternalStorage() {
        String path = RootUtils.runCommand("echo ${SECONDARY_STORAGE%%:*}");
        return path.contains("/") ? path : null;
    }

    public static String getInternalStorage() {
        String dataPath = existFile("/data/media/0", true) ? "/data/media/0" : "/data/media";
        if (!new RootFile(dataPath).isEmpty()) {
            return dataPath;
        }
        if (existFile("/sdcard", true)) {
            return "/sdcard";
        }
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static String getInternalDataStorage() {
        return Environment.getExternalStorageDirectory().toString() + "/Android/data/" +
                BuildConfig.APPLICATION_ID;
    }

    // Sorry pirates!
    public static boolean isPatched(ApplicationInfo applicationInfo) {
        try {
            boolean withBase = new File(applicationInfo.publicSourceDir).getName().equals("base.apk");
            if (withBase) {
                RootFile parent = new RootFile(applicationInfo.publicSourceDir).getParentFile();
                RootFile odex = new RootFile(parent.toString() + "/oat/*/base.odex");
                if (odex.exists()) {
                    String text = RootUtils.runCommand("strings " + odex.toString());
                    if (text.contains("--dex-file") || text.contains("--oat-file")) {
                        return true;
                    }
                }

                String dex = "/data/dalvik-cache/*/data@app@" + applicationInfo.packageName + "*@classes.dex";
                if (Utils.existFile(dex)) {
                    String path = RootUtils.runCommand("realpath " + dex);
                    if (path != null) {
                        String text = RootUtils.runCommand("strings " + path);
                        if (text.contains("--dex-file") || text.contains("--oat-file")) {
                            return true;
                        }
                    }
                }
            } else if (Utils.existFile(applicationInfo.publicSourceDir.replace(".apk", ".odex"))) {
                new RootFile(applicationInfo.publicSourceDir.replace(".apk", ".odex")).delete();
                RootUtils.runCommand("pkill " + applicationInfo.packageName);
                return false;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    // MD5 code from
    // https://github.com/CyanogenMod/android_packages_apps_CMUpdater/blob/cm-12.1/src/com/cyanogenmod/updater/utils/MD5.java
    public static boolean checkMD5(String md5, File updateFile) {
        if (md5 == null || updateFile == null || md5.isEmpty()) {
            Log.e(TAG, "MD5 string empty or updateFile null");
            return false;
        }

        String calculatedDigest = calculateMD5(updateFile);
        if (calculatedDigest == null) {
            Log.e(TAG, "calculatedDigest null");
            return false;
        }

        return calculatedDigest.equalsIgnoreCase(md5);
    }

    private static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String readAssetFile(Context context, String file) {
        InputStream input = null;
        BufferedReader buf = null;
        try {
            StringBuilder s = new StringBuilder();
            input = context.getAssets().open(file);
            buf = new BufferedReader(new InputStreamReader(input));

            String str;
            while ((str = buf.readLine()) != null) {
                s.append(str).append("\n");
            }
            return s.toString().trim();
        } catch (IOException e) {
            Log.e(TAG, "Unable to read " + file);
        } finally {
            try {
                if (input != null) input.close();
                if (buf != null) buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean useFahrenheit(Context context) {
        return Prefs.getBoolean("useretardedmeasurement", false, context);
    }

    public static double celsiusToFahrenheit(double celsius) {
        return (9d / 5d) * celsius + 32;
    }

    public static double roundTo2Decimals(double val) {
        BigDecimal bd = new BigDecimal(val);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String strFormat(String text, Object... format) {
        return String.format(text, format);
    }

    public static Float strToFloat(String text) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException ignored) {
            return 0f;
        }
    }

    public static Long strToLong(String text) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException ignored) {
            return 0L;
        }
    }

    public static int strToInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    public static boolean isRTL(View view) {
        return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    public static void toast(String message, Context context) {
        toast(message, context, Toast.LENGTH_SHORT);
    }

    public static void toast(@StringRes int id, Context context) {
        toast(context.getString(id), context);
    }

    public static void toast(@StringRes int id, Context context, int duration) {
        toast(context.getString(id), context, duration);
    }

    public static void toast(String message, Context context, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static void launchUrl(String url, Context context) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int getOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    public static boolean isPropRunning(String key) {
        return isPropRunning(key, RootUtils.getSU());
    }

    public static boolean isPropRunning(String key, RootUtils.SU su) {
        try {
            return su.runCommand("getprop | grep " + key).split("]:")[1].contains("running");
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean hasProp(String key) {
        return hasProp(key, RootUtils.getSU());
    }

    public static boolean hasProp(String key, RootUtils.SU su) {
        try {
            return su.runCommand("getprop | grep " + key).split("]:").length > 1;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void writeFile(String path, String text, boolean append, boolean asRoot) {
        if (asRoot) {
            new RootFile(path).write(text, append);
            return;
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(path, append);
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            Log.e(TAG, "Failed to write " + path);
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readFile(String file) {
        return readFile(file, true);
    }

    public static String readFile(String file, boolean root) {
        return readFile(file, root ? RootUtils.getSU() : null);
    }

    public static String readFile(String file, RootUtils.SU su) {
        if (su != null) return new RootFile(file, su).readFile();

        StringBuilder s = null;
        FileReader fileReader = null;
        BufferedReader buf = null;
        try {
            fileReader = new FileReader(file);
            buf = new BufferedReader(fileReader);

            String line;
            s = new StringBuilder();
            while ((line = buf.readLine()) != null) s.append(line).append("\n");
        } catch (FileNotFoundException ignored) {
            Log.e(TAG, "File does not exist " + file);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read " + file);
        } finally {
            try {
                if (fileReader != null) fileReader.close();
                if (buf != null) buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return s == null ? null : s.toString().trim();
    }

    public static boolean existFile(String file) {
        return existFile(file, true);
    }

    public static boolean existFile(String file, boolean root) {
        return existFile(file, root ? RootUtils.getSU() : null);
    }

    public static boolean existFile(String file, RootUtils.SU su) {
        return su == null ? new File(file).exists() : new RootFile(file, su).exists();
    }

}
