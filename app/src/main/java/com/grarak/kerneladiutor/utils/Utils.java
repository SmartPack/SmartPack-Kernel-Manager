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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.kernel.BatteryFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUHotplugFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUVoltageFragment;
import com.grarak.kerneladiutor.fragments.kernel.EntropyFragment;
import com.grarak.kerneladiutor.fragments.kernel.GPUFragment;
import com.grarak.kerneladiutor.fragments.kernel.IOFragment;
import com.grarak.kerneladiutor.fragments.kernel.KSMFragment;
import com.grarak.kerneladiutor.fragments.kernel.LMKFragment;
import com.grarak.kerneladiutor.fragments.kernel.MiscFragment;
import com.grarak.kerneladiutor.fragments.kernel.ScreenFragment;
import com.grarak.kerneladiutor.fragments.kernel.SoundFragment;
import com.grarak.kerneladiutor.fragments.kernel.ThermalFragment;
import com.grarak.kerneladiutor.fragments.kernel.VMFragment;
import com.grarak.kerneladiutor.fragments.kernel.WakeFragment;
import com.grarak.kerneladiutor.utils.kernel.CPU;
import com.grarak.kerneladiutor.utils.root.RootFile;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by willi on 30.11.14.
 */
public class Utils implements Constants {

    public static boolean DARKTHEME = false;

    // MD5 code from
    // https://github.com/CyanogenMod/android_packages_apps_CMUpdater/blob/cm-12.1/src/com/cyanogenmod/updater/utils/MD5.java
    public static boolean checkMD5(String md5, File updateFile) {
        if (TextUtils.isEmpty(md5) || updateFile == null) {
            Log.e(TAG, "MD5 string empty or updateFile null");
            return false;
        }

        String calculatedDigest = calculateMD5(updateFile);
        if (calculatedDigest == null) {
            Log.e(TAG, "calculatedDigest null");
            return false;
        }

        Log.v(TAG, "Calculated digest: " + calculatedDigest);
        Log.v(TAG, "Provided digest: " + md5);

        return calculatedDigest.equalsIgnoreCase(md5);
    }

    public static String calculateMD5(File updateFile) {
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

    public static boolean isRTL(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    public static Bitmap scaleDownBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int newWidth = width;
        int newHeight = height;

        if (maxWidth != 0 && newWidth > maxWidth) {
            newHeight = Math.round((float) maxWidth / newWidth * newHeight);
            newWidth = maxWidth;
        }

        if (maxHeight != 0 && newHeight > maxHeight) {
            newWidth = Math.round((float) maxHeight / newHeight * newWidth);
            newHeight = maxHeight;
        }

        return width != newWidth || height != newHeight ? resizeBitmap(bitmap, newWidth, newHeight) : bitmap;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }

    private static final Set<CustomTarget> protectedFromGarbageCollectorTargets = new HashSet<>();

    public static void loadImagefromUrl(String url, ImageView imageView) {
        CustomTarget target = new CustomTarget().setImageView(imageView);
        protectedFromGarbageCollectorTargets.add(target);
        Picasso.with(imageView.getContext()).load(url).into(target);
    }

    private static class CustomTarget implements Target {
        private ImageView imageView;

        public CustomTarget setImageView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            imageView.setImageBitmap(scaleDownBitmap(bitmap, 1920, 1920));
            protectedFromGarbageCollectorTargets.remove(this);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            protectedFromGarbageCollectorTargets.remove(this);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }

    public static String getDeviceName() {
        return Build.DEVICE;
    }

    public static String getVendorName() {
        return Build.MANUFACTURER;
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

    public static void errorDialog(Context context, Exception e) {
        new AlertDialog.Builder(context).setMessage(e.getMessage()).setNeutralButton(context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    public static void circleAnimate(final View view, int cx, int cy) {
        if (view == null) return;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setVisibility(View.INVISIBLE);

                int finalRadius = Math.max(view.getWidth(), view.getHeight());
                Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
                anim.setDuration(500);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                });
                anim.start();
            }
        } catch (IllegalStateException e) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static String getExternalStorage() {
        String path = RootUtils.runCommand("echo ${SECONDARY_STORAGE%%:*}");
        return path.contains("/") ? path : null;
    }

    public static String getInternalStorage() {
        String dataPath = existFile("/data/media/0") ? "/data/media/0" : "/data/media";
        if (!new RootFile(dataPath).isEmpty()) return dataPath;
        if (existFile("/sdcard")) return "/sdcard";
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static void confirmDialog(String title, String message, DialogInterface.OnClickListener onClickListener,
                                     Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);
        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setPositiveButton(context.getString(R.string.ok), onClickListener).show();
    }

    public static String readAssetFile(Context context, String file) {
        InputStream input = null;
        BufferedReader buf = null;
        try {
            StringBuilder s = new StringBuilder();
            input = context.getAssets().open(file);
            buf = new BufferedReader(new InputStreamReader(input));

            String str;
            while ((str = buf.readLine()) != null) s.append(str).append("\n");
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

    public static void setLocale(String lang, Context context) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
    }

    public static void vibrate(int duration) {
        RootUtils.runCommand("echo " + duration + " > /sys/class/timed_output/vibrator/enable");
    }

    public static List<String> getApplys(Class mClass) {
        List<String> applys = new ArrayList<>();

        if (mClass == BatteryFragment.class)
            applys.addAll(new ArrayList<>(Arrays.asList(BATTERY_ARRAY)));
        else if (mClass == CPUFragment.class) {
            for (String cpu : CPU_ARRAY)
                if (cpu.startsWith("/sys/devices/system/cpu/cpu%d/cpufreq"))
                    for (int i = 0; i < CPU.getCoreCount(); i++)
                        applys.add(String.format(cpu, i));
                else applys.add(cpu);
        } else if (mClass == CPUHotplugFragment.class) for (String[] array : CPU_HOTPLUG_ARRAY)
            applys.addAll(new ArrayList<>(Arrays.asList(array)));
        else if (mClass == CPUVoltageFragment.class)
            applys.addAll(new ArrayList<>(Arrays.asList(CPU_VOLTAGE_ARRAY)));
        else if (mClass == EntropyFragment.class)
            applys.addAll(new ArrayList<>(Arrays.asList(ENTROPY_ARRAY)));
        else if (mClass == GPUFragment.class) for (String[] arrays : GPU_ARRAY)
            applys.addAll(new ArrayList<>(Arrays.asList(arrays)));
        else if (mClass == IOFragment.class)
            applys.addAll(new ArrayList<>(Arrays.asList(IO_ARRAY)));
        else if (mClass == KSMFragment.class)
            applys.addAll(new ArrayList<>(Arrays.asList(KSM_ARRAY)));
        else if (mClass == LMKFragment.class) applys.add(LMK_MINFREE);
        else if (mClass == MiscFragment.class) for (String[] arrays : MISC_ARRAY)
            applys.addAll(new ArrayList<>(Arrays.asList(arrays)));
        else if (mClass == ScreenFragment.class) for (String[] arrays : SCREEN_ARRAY)
            applys.addAll(new ArrayList<>(Arrays.asList(arrays)));
        else if (mClass == SoundFragment.class) for (String[] arrays : SOUND_ARRAY)
            applys.addAll(new ArrayList<>(Arrays.asList(arrays)));
        else if (mClass == ThermalFragment.class) for (String[] arrays : THERMAL_ARRAYS)
            applys.addAll(new ArrayList<>(Arrays.asList(arrays)));
        else if (mClass == VMFragment.class)
            applys.addAll(new ArrayList<>(Arrays.asList(VM_ARRAY)));
        else if (mClass == WakeFragment.class) for (String[] arrays : WAKE_ARRAY)
            applys.addAll(new ArrayList<>(Arrays.asList(arrays)));

        return applys;
    }

    public static String formatCelsius(double celsius) {
        return round(celsius, 2) + "°C";
    }

    public static String celsiusToFahrenheit(double celsius) {
        return round(celsius * 9 / 5 + 32, 2) + "°F";
    }

    public static String round(double value, int places) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < places; i++) stringBuilder.append("#");
        DecimalFormat df = new DecimalFormat("#." + stringBuilder.toString());
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(value);
    }

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
        int actionBarSize = ta.getDimensionPixelSize(0, 112);
        ta.recycle();
        return actionBarSize;
    }

    public static boolean isTV(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2 && ((UiModeManager) context
                .getSystemService(Context.UI_MODE_SERVICE))
                .getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
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
        toast(message, context, Toast.LENGTH_SHORT);
    }

    public static void toast(String message, Context context, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static int getInt(String name, int defaults, Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(name, defaults);
    }

    public static void saveInt(String name, int value, Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putInt(name, value).apply();
    }

    public static boolean getBoolean(String name, boolean defaults, Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(name, defaults);
    }

    public static void saveBoolean(String name, boolean value, Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(name, value).apply();
    }

    public static String getString(String name, String defaults, Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(name, defaults);
    }

    public static void saveString(String name, String value, Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(name, value).apply();
    }

    public static String getProp(String key) {
        return RootUtils.runCommand("getprop " + key);
    }

    public static boolean isPropActive(String key) {
        try {
            return RootUtils.runCommand("getprop | grep " + key).split("]:")[1].contains("running");
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean hasProp(String key) {
        try {
            return RootUtils.runCommand("getprop | grep " + key).split("]:").length > 1;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void writeFile(String path, String text, boolean append) {
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

    public static String readFile(String file, boolean root) {
        if (root) return new RootFile(file).readFile();

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
        return new RootFile(file).exists();
    }

    public static String readFile(String file) {
        return readFile(file, true);
    }

}
