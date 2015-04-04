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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.kernel.BatteryFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUHotplugFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUVoltageFragment;
import com.grarak.kerneladiutor.fragments.kernel.GPUFragment;
import com.grarak.kerneladiutor.fragments.kernel.IOFragment;
import com.grarak.kerneladiutor.fragments.kernel.KSMFragment;
import com.grarak.kerneladiutor.fragments.kernel.LMKFragment;
import com.grarak.kerneladiutor.fragments.kernel.MiscFragment;
import com.grarak.kerneladiutor.fragments.kernel.ScreenFragment;
import com.grarak.kerneladiutor.fragments.kernel.SoundFragment;
import com.grarak.kerneladiutor.fragments.kernel.VMFragment;
import com.grarak.kerneladiutor.fragments.kernel.WakeFragment;
import com.grarak.kerneladiutor.utils.kernel.CPU;
import com.grarak.kerneladiutor.utils.root.RootFile;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by willi on 30.11.14.
 */
public class Utils implements Constants {

    public static boolean DARKTHEME = false;

    public static String readAssetFile(Context context, String file) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(file);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst) isFirst = false;
                else buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e(TAG, "Unable to read " + file);
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException e) {
                Log.e(TAG, "Unable to close Reader " + file);
            }
        }
        return null;
    }

    public static void setLocale(String lang, Context context) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(lang);
        res.updateConfiguration(conf, dm);
    }

    public static void vibrate(int duration) {
        RootUtils.runCommand("echo " + duration + " > " + VIBRATION_ENABLE);
    }

    public static List<String> getApplys(Class mClass) {
        List<String> applys = new ArrayList<>();

        if (mClass == BatteryFragment.class)
            applys.addAll(new ArrayList<>(Arrays.asList(BATTERY_ARRAY)));

        if (mClass == CPUFragment.class) {
            for (String[] array : CPU_ARRAY)
                for (String cpu : array)
                    if (cpu.startsWith("/sys/devices/system/cpu/cpu%d/cpufreq"))
                        for (int i = 0; i < CPU.getCoreCount(); i++)
                            applys.add(String.format(cpu, i));
                    else applys.add(cpu);
        } else if (mClass == CPUHotplugFragment.class) for (String[] array : CPU_HOTPLUG_ARRAY)
            applys.addAll(new ArrayList<>(Arrays.asList(array)));
        else if (mClass == CPUVoltageFragment.class)
            applys.addAll(new ArrayList<>(Arrays.asList(CPU_VOLTAGE_ARRAY)));
        else if (mClass == GPUFragment.class) for (String[] arrays : GPU_ARRAY)
            applys.addAll(new ArrayList<>(Arrays.asList(arrays)));
        else if (mClass == IOFragment.class)
            applys.addAll(new ArrayList<>(Arrays.asList(IO_ARRAY)));
        else if (mClass == KSMFragment.class) applys.add(KSM_FOLDER);
        else if (mClass == LMKFragment.class) applys.add(LMK_MINFREE);
        else if (mClass == MiscFragment.class) for (String[] arrays : MISC_ARRAY)
            applys.addAll(new ArrayList<>(Arrays.asList(arrays)));
        else if (mClass == ScreenFragment.class) for (String[] arrays : SCREEN_ARRAY)
            applys.addAll(new ArrayList<>(Arrays.asList(arrays)));
        else if (mClass == SoundFragment.class)
            applys.addAll(new ArrayList<>(Arrays.asList(SOUND_ARRAY)));
        else if (mClass == VMFragment.class)
            applys.addAll(new ArrayList<>(Arrays.asList(VM_ARRAY)));
        else if (mClass == WakeFragment.class) for (String[] arrays : WAKE_ARRAY)
            applys.addAll(new ArrayList<>(Arrays.asList(arrays)));

        return applys;
    }

    public static float celsiusToFahrenheit(float celsius) {
        return celsius * 9 / 5 + 32;
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

    public static int getInt(String name, int defaults, Context context) {
        int i = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(name, defaults);
        Log.i(TAG, "getting " + name + ": " + i);
        return i;
    }

    public static void saveInt(String name, int value, Context context) {
        Log.i(TAG, "saving " + name + " as " + value);
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putInt(name, value).apply();
    }

    public static boolean getBoolean(String name, boolean defaults, Context context) {
        boolean bool = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(name, defaults);
        Log.i(TAG, "getting " + name + ": " + bool);
        return bool;
    }

    public static void saveBoolean(String name, boolean value, Context context) {
        Log.i(TAG, "saving " + name + " as " + value);
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(name, value).apply();
    }

    public static String getString(String name, String defaults, Context context) {
        String ret = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(name, defaults);
        Log.i(TAG, "getting " + name + ": " + ret);
        return ret;
    }

    public static void saveString(String name, String value, Context context) {
        Log.i(TAG, "saving " + name + " as " + value);
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(name, value).apply();
    }

    public static boolean isServiceActive(String service) {
        String output = RootUtils.runCommand("echo `ps | grep " + service + " | grep -v \"grep "
                + service + "\" | awk '{print $1}'`");
        return output != null && output.length() > 0;
    }

    public static void writeFile(String path, String text, boolean append) {
        try {
            FileWriter fWriter = new FileWriter(path, append);
            fWriter.write(text);
            fWriter.flush();
            fWriter.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to write " + path);
        }
    }

    public static boolean existFile(String file) {
        return new RootFile(file).exists();
    }

    public static String readFile(String file) {
        return new RootFile(file).readFile();
    }

}
