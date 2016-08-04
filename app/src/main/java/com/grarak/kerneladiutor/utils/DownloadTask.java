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

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by willi on 08.07.16.
 */
public class DownloadTask extends AsyncTask<String, Integer, String> {

    private final Activity mActivity;
    private final OnDownloadListener onDownloadListener;
    private final PowerManager.WakeLock mWakeLock;
    private final String mPath;
    private boolean mDownloading = true;
    private boolean mCancelled;

    public DownloadTask(Activity activity, OnDownloadListener onDownloadListener, String path) {
        mActivity = activity;
        this.onDownloadListener = onDownloadListener;
        mPath = path;

        new File(path).getParentFile().mkdirs();

        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.acquire();
    }

    @Override
    protected String doInBackground(String... strings) {
        InputStream input = null;
        FileOutputStream output = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            int totalSize = connection.getContentLength();
            input = connection.getInputStream();
            output = new FileOutputStream(mPath);

            byte data[] = new byte[4096];
            int currentSize = 0;
            int count;
            while (true) {
                if (mCancelled) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new File(mPath).delete();
                            onDownloadListener.onCancel();
                        }
                    });
                    input.close();
                    output.close();
                    connection.disconnect();
                    return null;
                }

                if (mDownloading) {
                    if ((count = input.read(data)) != -1) {
                        currentSize += count;
                        if (totalSize > 0) {
                            final int cs = currentSize;
                            final int ts = totalSize;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onDownloadListener.onUpdate(cs, ts);
                                }
                            });
                        }
                        output.write(data, 0, count);
                    } else {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException ignored) {
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        mWakeLock.release();
        if (s == null) {
            onDownloadListener.onSuccess(mPath);
        } else {
            onDownloadListener.onFailure(s);
        }
    }

    public void pause() {
        mDownloading = false;
    }

    public void resume() {
        mDownloading = true;
    }

    public void cancel() {
        mCancelled = true;
    }

    public interface OnDownloadListener {
        void onUpdate(int currentSize, int totalSize);

        void onSuccess(String path);

        void onCancel();

        void onFailure(String error);
    }

}
