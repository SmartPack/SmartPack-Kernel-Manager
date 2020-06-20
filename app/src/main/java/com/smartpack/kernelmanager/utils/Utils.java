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
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;

import com.google.android.material.snackbar.Snackbar;
import com.smartpack.kernelmanager.BuildConfig;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.StartActivity;
import com.smartpack.kernelmanager.activities.StartActivityMaterial;
import com.smartpack.kernelmanager.utils.root.RootFile;
import com.smartpack.kernelmanager.utils.root.RootUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by willi on 14.04.16.
 */
public class Utils {

    public static boolean mBattery = false;
    public static boolean mCPU = false;
    public static boolean mCPUTimes = false;
    public static boolean mDevice = false;
    public static boolean mGPU = false;
    public static boolean mMemory = false;

    private static final String TAG = Utils.class.getSimpleName();

    public static boolean isPackageInstalled(String id, Context context) {
        try {
            context.getPackageManager().getApplicationInfo(id, 0);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
    }

    private static boolean isKADonated(Context context) {
        return isPackageInstalled("com.grarak.kerneladiutordonate", context);
    }

    public static boolean isSPDonated(Context context) {
        return isPackageInstalled("com.smartpack.donate", context);
    }

    public static boolean isDonated(Context context) {
        return BuildConfig.DEBUG || isKADonated(context) || isSPDonated(context);
    }

    public static boolean isPlayStoreInstalled(Context context) {
        return isPackageInstalled("com.android.vending", context);
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

    public static String decodeString(String text) {
        return new String(Base64.decode(text, Base64.DEFAULT), StandardCharsets.UTF_8);
    }

    public static String encodeString(String text) {
        return Base64.encodeToString(text.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
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

    public static String getInternalDataStorage() {
        return Environment.getExternalStorageDirectory().toString() + "/SP";
    }

    public static void prepareInternalDataStorage() {
        File file = new File(getInternalDataStorage());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
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
        Snackbar snackbar;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void launchUrl(String url, Context context) {
        if (!isNetworkAvailable(context)) {
            toast(R.string.no_internet, context);
            return;
        }
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void shareItem(Context context, String name, String path, String string) {
        Uri uriFile = FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider", new File(path));
        Intent shareScript = new Intent(Intent.ACTION_SEND);
        shareScript.setType("*/*");
        shareScript.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_by, name));
        shareScript.putExtra(Intent.EXTRA_TEXT, string);
        shareScript.putExtra(Intent.EXTRA_STREAM, uriFile);
        shareScript.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareScript, context.getString(R.string.share_with)));
    }

    public static int getOrientation(Activity activity) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && activity.isInMultiWindowMode() ?
                Configuration.ORIENTATION_PORTRAIT : activity.getResources().getConfiguration().orientation;
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
        if (root) {
            return new RootFile(file).readFile();
        }

        BufferedReader buf = null;
        try {
            buf = new BufferedReader(new FileReader(file));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = buf.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            return stringBuilder.toString().trim();
        } catch (IOException ignored) {
        } finally {
            try {
                if (buf != null) buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean existFile(String file) {
        return existFile(file, true);
    }

    public static boolean existFile(String file, boolean root) {
        return !root ? new File(file).exists() : new RootFile(file).exists();
    }

    public static void create(String text, String path) {
        try {
            File logFile = new File(path);
            logFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(logFile);
            OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
            myOutWriter.append(text);
            myOutWriter.close();
            fOut.close();
        } catch (Exception ignored) {
        }
    }

    public static void append(String text, String path) {
        RootUtils.runCommand("echo '" + text + "' >> " + path);
    }

    public static void delete(String path) {
        if (Utils.existFile(path)) {
            RootUtils.runCommand("rm -r " + path);
        }
    }

    public static void sleep(int sec) {
        RootUtils.runCommand("sleep " + sec);
    }

    public static void copy(String source, String dest) {
        RootUtils.runCommand("cp -r " + source + " " + dest);
    }

    public static void mount(String command, String source, String dest) {
        RootUtils.runCommand("mount " + command + " " + source + " " + dest);
    }

    public static String getChecksum(String path) {
        return RootUtils.runAndGetOutput("sha1sum " + path);
    }

    public static boolean isDownloadBinaries() {
        return Utils.existFile("/system/bin/curl") || Utils.existFile("/system/bin/wget");
    }

    public static boolean isUnzipAvailable() {
        return !RootUtils.runAndGetError("unzip --help").contains(
                "/system/bin/sh: unzip: inaccessible or not found");
    }

    public static void downloadFile(String path, String url, Context context) {
        if (!isNetworkAvailable(context)) {
            toast(R.string.no_internet, context);
            return;
        }
        if (isDownloadBinaries()) {
            RootUtils.runCommand((Utils.existFile("/system/bin/curl") ?
                    "curl -L -o " : "wget -O ") + path + " " + url);
        } else {
            /*
             * Based on the following stackoverflow discussion
             * Ref: https://stackoverflow.com/questions/15758856/android-how-to-download-file-from-webserver
             */
            try (InputStream input = new URL(url).openStream();
                 OutputStream output = new FileOutputStream(path)) {
                byte[] data = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return (cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static String getFilePath(File file) {
        String path = file.getAbsolutePath();
        if (path.startsWith("/document/raw:")) {
            path = path.replace("/document/raw:", "");
        } else if (path.startsWith("/document/primary:")) {
            path = (Environment.getExternalStorageDirectory() + ("/") + path.replace("/document/primary:", ""));
        } else if (path.startsWith("/document/")) {
            path = path.replace("/document/", "/storage/").replace(":", "/");
        }
        if (path.startsWith("/storage_root/storage/emulated/0")) {
            path = path.replace("/storage_root/storage/emulated/0", "/storage/emulated/0");
        } else if (path.startsWith("/storage_root")) {
            path = path.replace("storage_root", "storage/emulated/0");
        }
        if (path.startsWith("/external")) {
            path = path.replace("external", "storage/emulated/0");
        } if (path.startsWith("/root/")) {
            path = path.replace("/root", "");
        }
        if (path.contains("file%3A%2F%2F%2F")) {
            path = path.replace("file%3A%2F%2F%2F", "").replace("%2F", "/");
        }
        return path;
    }

    public static boolean isDocumentsUI(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
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

    public static String prepareReboot() {
        String prepareReboot = "am broadcast android.intent.action.ACTION_SHUTDOWN " + "&&" +
                " sync " + "&&" +
                " echo 3 > /proc/sys/vm/drop_caches " + "&&" +
                " sync " + "&&" +
                " sleep 3 " + "&&" +
                " reboot";
        return prepareReboot;
    }

    public static void rebootCommand(Context context) {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog mProgressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setMessage(context.getString(R.string.rebooting) + ("..."));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
            @Override
            protected Void doInBackground(Void... voids) {
                RootUtils.runCommand(prepareReboot());
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
            }
        }.execute();
    }

    /**
     * Taken and used almost as such from the following stackoverflow discussion
     * https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
     */
    public static String getExtension(String string) {
        return android.webkit.MimeTypeMap.getFileExtensionFromUrl(string);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setCopyRightText(WeakReference<Activity> activityRef) {
        if (activityRef.get().checkCallingOrSelfPermission(android.Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activityRef.get().requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Utils.toast(R.string.permission_denied_write_storage, activityRef.get());
            return;
        }
        ViewUtils.dialogEditText(existFile(getInternalDataStorage() + "/copyright") ?
                        readFile(getInternalDataStorage() + "/copyright") : null,
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.equals(readFile(getInternalDataStorage() + "/copyright"))) return;
                    if (text.isEmpty()) {
                        delete(getInternalDataStorage() + "/copyright");
                        toast(activityRef.get().getString(R.string.copyright_default, activityRef.get().getString(R.string.copyright)), activityRef.get());
                        return;
                    }
                    create(text, getInternalDataStorage() + "/copyright");
                    toast(activityRef.get().getString(R.string.copyright_message, text), activityRef.get());
                }, activityRef.get()).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    public static String removeSuffix(@Nullable String s, @Nullable String suffix) {
        if (s != null && suffix != null && s.endsWith(suffix)) {
            return s.substring(0, s.length() - suffix.length());
        }
        return s;
    }
}