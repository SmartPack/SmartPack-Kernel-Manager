/*
 * Copyright (C) 2015-2017 Willi Ye <williye97@gmail.com>
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.view.ViewCompat;

import com.smartpack.kernelmanager.BuildConfig;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.StartActivity;
import com.smartpack.kernelmanager.activities.StartActivityMaterial;
import com.smartpack.kernelmanager.activities.WebViewActivity;
import com.smartpack.kernelmanager.utils.root.RootFile;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.topjohnwu.superuser.io.SuFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/**
 * Created by willi on 14.04.16.
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static boolean isDonated() {
        return isPackageInstalled("com.smartpack.donate");
    }

    public static boolean isFDroidFlavor(Context context) {
        return context.getPackageName().equals("com.smartpack.kernelmanager");
    }

    public static boolean isPackageInstalled(String packageName) {
        return RootUtils.runAndGetOutput("pm list packages " + packageName).equals("package:" + packageName);
    }

    public static boolean isPermissionDenied(String permission, Context context) {
        return (context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED);
    }

    public static void requestPermission(String[] permissions, Activity activity) {
        ActivityCompat.requestPermissions(activity, permissions, 0);
    }

    public static void initializeAppTheme(Context context) {
        if (Prefs.getBoolean("darktheme", true, context)) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static void startService(Context context, Intent intent) {
        if (!Prefs.getBoolean("enable_onboot", true, context)) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

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

    public static boolean isTv(Context context) {
        return ((UiModeManager) Objects.requireNonNull(context.getSystemService(Context.UI_MODE_SERVICE)))
                .getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
    }

    public static boolean isFingerprintAvailable(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return FingerprintManagerCompat.from(context).hasEnrolledFingerprints();
        }
        return false;
    }

    public static void setupStartActivity(Context context) {
        PackageManager pm = context.getPackageManager();
        if (Utils.hideStartActivity()) {
            pm.setComponentEnabledSetting(new ComponentName(context, StartActivity.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(new ComponentName(context, StartActivityMaterial.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        } else {
            setStartActivity(Prefs.getBoolean("materialicon", false, context), context);
        }
    }

    public static void setStartActivity(boolean material, Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, StartActivity.class),
                material ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(context, StartActivityMaterial.class),
                material ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    public static boolean hideStartActivity() {
        String prop = RootUtils.runAndGetOutput("getprop ro.kerneladiutor.hide");
        return prop.equals("true");
    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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

    public static File getInternalDataStorage() {
        return SuFile.open(Environment.getExternalStorageDirectory(), "SP");
    }

    public static void prepareInternalDataStorage() {
        if (getInternalDataStorage().exists() && getInternalDataStorage().isFile()) {
            delete(getInternalDataStorage().getAbsolutePath());
        }
        mkdir(getInternalDataStorage().getAbsolutePath());
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
        return sFileUtils.readAssetFile(file, context);
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

    public static String sToString(long tSec) {
        int h = (int) (tSec / (60 * 60));
        int m = ((int) tSec % (60 * 60)) / 60;
        int s = ((int) tSec % (60 * 60)) % 60;
        String sDur = "";
        if(h != 0) sDur = h + "h ";
        if(m != 0) sDur += m + "m ";
        sDur += s + "s";

        return sDur;
    }

    public static String getChangelog(Context context) {
        try {
            return new JSONObject(Objects.requireNonNull(readAssetFile(context,
                    "release.json"))).getString("releaseNotes");
        } catch (JSONException ignored) {
        }
        return null;
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

    public static void snackbar(View view, String message) {
        sCommonUtils.snackBar(view, message).show();
    }

    public static void launchUrl(String url, Activity activity) {
        sCommonUtils.launchUrl(url, activity);
    }

    public static void launchWebView(String title, String url, Activity activity) {
        Common.setWebViewTitle(title);
        Common.setUrl(url);
        Intent intent = new Intent(activity, WebViewActivity.class);
        activity.startActivity(intent);
    }

    public static void shareItem(Context context, String name, String path, String string) {
        Uri uriFile = FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider", SuFile.open(path));
        Intent shareScript = new Intent(Intent.ACTION_SEND);
        shareScript.setType("*/*");
        shareScript.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_by, name));
        shareScript.putExtra(Intent.EXTRA_TEXT, string);
        shareScript.putExtra(Intent.EXTRA_STREAM, uriFile);
        shareScript.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareScript, context.getString(R.string.share_with)));
    }

    public static int getOrientation(Activity activity) {
        return sCommonUtils.getOrientation(activity);
    }

    public static boolean isPropRunning(String key) {
        try {
            return RootUtils.runAndGetOutput("getprop | grep " + key).split("]:")[1].contains("running");
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean hasProp(String key) {
        try {
            return !RootUtils.runAndGetOutput("getprop | grep " + key).isEmpty();
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void writeFile(String path, String text, boolean append, boolean asRoot) {
        if (asRoot) {
            RootFile.write(path, text, append);
            return;
        }

        create(text, path);
    }

    public static String readFile(String file) {
        return readFile(file, true);
    }

    public static String readFile(String file, boolean root) {
        if (root) {
            return RootFile.read(file);
        }

        return sFileUtils.read(new File(file));
    }

    public static boolean existFile(String file) {
        return existFile(file, true);
    }

    public static boolean existFile(String file, boolean root) {
        return !root ? sFileUtils.exist(new File(file)) : RootFile.exists(file);
    }

    public static void create(String text, String path) {
        RootUtils.runCommand("echo '" + text + "' > " + path);
    }

    public static boolean mkdir(String path) {
        return SuFile.open(path).mkdirs();
    }

    public static void append(String text, String path) {
        RootUtils.runCommand("echo '" + text + "' >> " + path);
    }

    public static void delete(String file) {
        delete(file, true);
    }

    public static void delete(String file, boolean root) {
        if (root) {
            RootFile.delete(file);
        } else {
            sFileUtils.delete(new File(file));
        }
    }

    public static void sleep(int sec) {
        sCommonUtils.sleep(sec);
    }

    public static void copy(String source, String dest) {
        RootUtils.runCommand("cp -r " + source + " " + dest);
    }

    public static String getChecksum(String path) {
        return RootUtils.runAndGetOutput("sha1sum " + path);
    }

    public static boolean isMagiskBinaryExist(String command) {
        return !RootUtils.runAndGetError("/data/adb/magisk/busybox " + command).contains("applet not found");
    }

    public static String magiskBusyBox() {
        return "/data/adb/magisk/busybox";
    }

    public static void downloadFile(String path, String url) {
        /*
         * Based on the following stackoverflow discussion
         * Ref: https://stackoverflow.com/questions/15758856/android-how-to-download-file-from-webserver
         */
        try (InputStream input = new URL(url).openStream();
             FileOutputStream output = new FileOutputStream(path)) {
            byte[] data = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
        } catch (Exception ignored) {
        }
    }

    /*
     * Taken and used almost as such from yoinx's Kernel Adiutor Mod (https://github.com/yoinx/kernel_adiutor/)
     */
    @SuppressLint("DefaultLocale")
    public static String getDurationBreakdown(long millis)
    {
        StringBuilder sb = new StringBuilder(64);
        if(millis <= 0)
        {
            sb.append("00 min 00 s");
            return sb.toString();
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        if (days > 0) {
            sb.append(days);
            sb.append(" day ");
        }
        if (hours > 0) {
            sb.append(hours);
            sb.append(" hr ");
        }
        sb.append(String.format("%02d", minutes));
        sb.append(" min ");
        sb.append(String.format("%02d", seconds));
        sb.append(" s");
        return sb.toString();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTimeStamp() {
        return new SimpleDateFormat("yyyyMMdd_HH-mm").format(new Date());
    }

    public static int getScreenDPI(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.densityDpi;
    }

    public static String prepareReboot() {
        return "am broadcast android.intent.action.ACTION_SHUTDOWN " + "&&" +
                " sync " + "&&" +
                " echo 3 > /proc/sys/vm/drop_caches " + "&&" +
                " sync " + "&&" +
                " sleep 3 " + "&&" +
                " reboot";
    }

    public static void rebootCommand(Context context) {
        new sExecutor() {
            private ProgressDialog mProgressDialog;
            @Override
            public void onPreExecute() {
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setMessage(context.getString(R.string.rebooting) + ("..."));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
            @Override
            public void doInBackground() {
                RootUtils.runCommand(prepareReboot());
            }
            @Override
            public void onPostExecute() {
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
            }
        }.execute();
    }

    /*
     * Taken and used almost as such from the following stackoverflow discussion
     * https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
     */
    public static String getExtension(String string) {
        return android.webkit.MimeTypeMap.getFileExtensionFromUrl(string);
    }

    public static String removeSuffix(@Nullable String s, @Nullable String suffix) {
        if (s != null && suffix != null && s.endsWith(suffix)) {
            return s.substring(0, s.length() - suffix.length());
        }
        return s;
    }

    public static String getOutput(List<String> output) {
        List<String> mData = new ArrayList<>();
        for (String line : output.toString().substring(1, output.toString().length() - 1).replace(
                ", ","\n").replace("ui_print","").split("\\r?\\n")) {
            if (!line.startsWith("progress")) {
                mData.add(line);
            }
        }
        return mData.toString().substring(1, mData.toString().length() - 1).replace(", ","\n")
                .replaceAll("(?m)^[ \t]*\r?\n", "");
    }

    public static void setLanguage(String locale, Context context) {
        Locale myLocale = new Locale(locale);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

}