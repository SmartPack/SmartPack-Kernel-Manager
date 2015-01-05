package com.grarak.kerneladiutor.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by willi on 30.11.14.
 */
public class Utils implements Constants {

    public static void launchUrl(Context context, String link) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
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
        return context.getSharedPreferences(PREF_NAME, 0).getBoolean(name, defaults);
    }

    public static void saveBoolean(String name, boolean value, Context context) {
        Log.i(TAG, "saving " + name + " as " + value);
        context.getSharedPreferences(PREF_NAME, 0).edit().putBoolean(name, value).apply();
    }

    public static boolean existFile(String file) {
        return new File(file).exists();
    }

    public static String readFile(String filepath) {
        if (filepath == null) return null;

        try {
            BufferedReader buffreader = new BufferedReader(new FileReader(filepath), 256);
            String line;
            StringBuilder text = new StringBuilder();
            while ((line = buffreader.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
            buffreader.close();
            return text.toString().trim();
        } catch (FileNotFoundException e) {
            Log.e(TAG, filepath + " does not exist");
        } catch (IOException e) {
            Log.e(TAG, "I/O read error: " + filepath);
        }
        return null;
    }

}
