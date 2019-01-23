package com.github.javiersantos.appupdater;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.github.javiersantos.appupdater.enums.Duration;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.GitHub;
import com.github.javiersantos.appupdater.objects.Update;
import com.github.javiersantos.appupdater.objects.Version;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
import static android.content.Context.DOWNLOAD_SERVICE;
import static android.widget.Toast.LENGTH_LONG;

class UtilsLibrary {

    private static DownloadManager downloadManager;
    private static long id;

    static String getAppName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    static String getAppInstalledVersion(Context context) {
        String version = "0.0.0.0";

        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    static Integer getAppInstalledVersionCode(Context context) {
        Integer versionCode = 0;

        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    static Boolean isUpdateAvailable(Update installedVersion, Update latestVersion) {
        if (latestVersion.getLatestVersionCode() != null && latestVersion.getLatestVersionCode() > 0) {
            return latestVersion.getLatestVersionCode() > installedVersion.getLatestVersionCode();
        } else {
            if (!TextUtils.equals(installedVersion.getLatestVersion(), "0.0.0.0") && !TextUtils.equals(latestVersion.getLatestVersion(), "0.0.0.0")) {
                try
                {
                    final Version installed = new Version(installedVersion.getLatestVersion());
                    final Version latest = new Version(latestVersion.getLatestVersion());
                    return installed.compareTo(latest) < 0;
                } catch (Exception e)
                {
                    e.printStackTrace();
                    return false;
                }
            } else return false;
        }
    }

    static Boolean isStringAVersion(String version) {
        return version.matches(".*\\d+.*");
    }

    static Boolean isStringAnUrl(String s) {
        Boolean res = false;
        try {
            new URL(s);
            res = true;
        } catch (MalformedURLException ignored) {}

        return res;
    }

    static Boolean getDurationEnumToBoolean(Duration duration) {
        Boolean res = false;

        switch (duration) {
            case INDEFINITE:
                res = true;
                break;
        }

        return res;
    }

    static URL getUpdateURL(Context context, UpdateFrom updateFrom, GitHub gitHub) {
        String res;

        switch (updateFrom) {
            default:
                res = String.format(Config.PLAY_STORE_URL, getAppPackageName(context), Locale.getDefault().getLanguage());
                break;
            case GITHUB:
                res = Config.GITHUB_URL + gitHub.getGitHubUser() + "/" + gitHub.getGitHubRepo() + "/releases";
                break;
            case AMAZON:
                res = Config.AMAZON_URL + getAppPackageName(context);
                break;
            case FDROID:
                res = Config.FDROID_URL + getAppPackageName(context);
                break;
        }

        try {
            return new URL(res);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    static Update getLatestAppVersionHttp(Context context, UpdateFrom updateFrom, GitHub gitHub) {
        Boolean isAvailable = false;
        String source = "";
        OkHttpClient client = new OkHttpClient();
        URL url = getUpdateURL(context, updateFrom, gitHub);
        Request request = new Request.Builder()
                .url(url)
                .build();
        ResponseBody body = null;

        try {
            Response response = client.newCall(request).execute();
            body = response.body();
            BufferedReader reader = new BufferedReader(new InputStreamReader(body.byteStream(), "UTF-8"));
            StringBuilder str = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                switch (updateFrom) {
                    default:
                        if (line.contains(Config.PLAY_STORE_TAG_RELEASE)) {
                            str.append(line);
                            isAvailable = true;
                        }
                        break;
                    case GITHUB:
                        if (line.contains(Config.GITHUB_TAG_RELEASE)) {
                            str.append(line);
                            isAvailable = true;
                        }
                        break;
                    case AMAZON:
                        if (line.contains(Config.AMAZON_TAG_RELEASE)) {
                            str.append(line);
                            isAvailable = true;
                        }
                        break;
                    case FDROID:
                        if (line.contains(Config.FDROID_TAG_RELEASE)) {
                            str.append(line);
                            isAvailable = true;
                        }
                }
            }

            if (str.length() == 0) {
                Log.e("AppUpdater", "Cannot retrieve latest version. Is it configured properly?");
            }

            response.body().close();
            source = str.toString();
        } catch (FileNotFoundException e) {
            Log.e("AppUpdater", "App wasn't found in the provided source. Is it published?");
        } catch (IOException ignore) {

        } finally {
            if (body != null) {
                body.close();
            }
        }

        final String version = getVersion(updateFrom, isAvailable, source);
        final String recentChanges = getRecentChanges(updateFrom, isAvailable, source);
        final URL updateUrl = getUpdateURL(context, updateFrom, gitHub);

        return new Update(version, recentChanges, updateUrl);
    }

    private static String getVersion(UpdateFrom updateFrom, Boolean isAvailable, String source) {
        String version = "0.0.0.0";
        if (isAvailable) {
            switch (updateFrom) {
                default:
                    String[] splitPlayStore = source.split(Config.PLAY_STORE_TAG_RELEASE);
                    if (splitPlayStore.length > 1) {
                        splitPlayStore = splitPlayStore[1].split("(<)");
                        version = splitPlayStore[0].trim();
                    }
                    break;
                case GITHUB:
                    String[] splitGitHub = source.split(Config.GITHUB_TAG_RELEASE);
                    if (splitGitHub.length > 1) {
                        splitGitHub = splitGitHub[1].split("(\")");
                        version = splitGitHub[0].trim();
                        if (version.startsWith("v"))
                        { // Some repo uses vX.X.X
                            splitGitHub = version.split("(v)", 2);
                            version = splitGitHub[1].trim();
                        }
                    }
                    break;
                case AMAZON:
                    String[] splitAmazon = source.split(Config.AMAZON_TAG_RELEASE);
                    splitAmazon = splitAmazon[1].split("(<)");
                    version = splitAmazon[0].trim();
                    break;
                case FDROID:
                    String[] splitFDroid = source.split(Config.FDROID_TAG_RELEASE);
                    splitFDroid = splitFDroid[1].split("(<)");
                    version = splitFDroid[0].trim();
                    break;
            }
        }
        return version;
    }

    private static String getRecentChanges(UpdateFrom updateFrom, Boolean isAvailable, String source) {
        String recentChanges = "";
        if (isAvailable) {
            switch (updateFrom) {
                default:
                    String[] splitPlayStore = source.split(Config.PLAY_STORE_TAG_CHANGES);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < splitPlayStore.length; i++) {
                        sb.append(splitPlayStore[i].split("(<)")[0]).append("\n");
                    }
                    recentChanges = sb.toString();
                    break;
                case GITHUB:
                    break;
                case AMAZON:
                    break;
                case FDROID:
                    break;
            }
        }
        return recentChanges;
    }

    static Update getLatestAppVersion(UpdateFrom updateFrom, String url) {
        if (updateFrom == UpdateFrom.XML){
            RssParser parser = new RssParser(url);
            return parser.parse();
        } else {
            return new JSONParser(url).parse();
        }
    }


    static Intent intentToUpdate(Context context, UpdateFrom updateFrom, URL url) {
        Intent intent;

        if (updateFrom.equals(UpdateFrom.GOOGLE_PLAY)) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getAppPackageName(context)));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
        }

        return intent;
    }

    static void goToUpdate(Context context, UpdateFrom updateFrom, URL url) {

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(downloadReceiver, filter);

        descargar(context, url);

    }

    static Boolean isAbleToShow(Integer successfulChecks, Integer showEvery) {
        return successfulChecks % showEvery == 0;
    }

    static Boolean isNetworkAvailable(Context context) {
        Boolean res = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null) {
                res = networkInfo.isConnected();
            }
        }

        return res;
    }

    private static void descargar(Context context, URL url)
    {
        downloadManager = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url.toString()));

        String APK = new File(url.getPath()).getName();

        request.setTitle(APK);
        request.setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Guardar archivo
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,APK);
        }

        // Iniciamos la descarga
        Utils.toast(context.getString(R.string.appupdater_downloading_file) + " " + APK + " ...", context, LENGTH_LONG);
        id = downloadManager.enqueue(request);

    }

    private static void OpenNewVersion(Context context, File file) {

        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uriFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uriFile = FileProvider.getUriForFile(context, "com.grarak.kerneladiutor.provider", file);
        } else {
            uriFile = Uri.fromFile(file);
        }
        intent.setDataAndType(uriFile,"application/vnd.android.package-archive");
        context.startActivity(Intent.createChooser(intent, "Open_Apk"));

    }

    private static BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id, 0);
            Cursor cursor = downloadManager.query(query);

            if(cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));

                if(status == DownloadManager.STATUS_SUCCESSFUL) {

                    // Si la descarga es correcta abrimos el archivo para instalarlo
                    File file = new File(Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))).getPath());
                    OpenNewVersion(context, file);
                }
                else if(status == DownloadManager.STATUS_FAILED) {
                    Utils.toast(context.getString(R.string.appupdater_download_filed) + reason, context, LENGTH_LONG);
                }
            }
        }
    };

}
