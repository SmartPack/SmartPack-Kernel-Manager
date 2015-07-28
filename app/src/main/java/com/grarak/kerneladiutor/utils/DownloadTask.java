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
 * Created by willi on 02.07.15.
 */
public class DownloadTask extends AsyncTask<String, Integer, String> {

    private final Activity activity;
    private final OnDownloadListener onDownloadListener;
    private final PowerManager.WakeLock mWakeLock;
    private String path;
    private boolean downloading = true;

    public DownloadTask(Context context, OnDownloadListener onDownloadListener, String path) {
        activity = (Activity) context;
        this.onDownloadListener = onDownloadListener;
        this.path = path;

        new File(path).getParentFile().mkdirs();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
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
            output = new FileOutputStream(path);

            byte data[] = new byte[4096];
            int currentSize = 0;
            int count;
            while (true) {
                if (isCancelled()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onDownloadListener.onCancel();
                        }
                    });
                    input.close();
                    output.close();
                    connection.disconnect();
                    return null;
                }

                if (downloading) if ((count = input.read(data)) != -1) {
                    currentSize += count;
                    if (totalSize > 0) {
                        final int cs = currentSize;
                        final int ts = totalSize;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onDownloadListener.onUpdate(cs, ts);
                            }
                        });
                    }
                    output.write(data, 0, count);
                } else break;
            }
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            try {
                if (output != null) output.close();
                if (input != null) input.close();
            } catch (IOException ignored) {
            }

            if (connection != null) connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        mWakeLock.release();
        if (s == null) onDownloadListener.onSuccess(path);
        else onDownloadListener.onFailure(s);
    }

    public void pause() {
        downloading = false;
    }

    public void resume() {
        downloading = true;
    }

    public interface OnDownloadListener {
        void onUpdate(int currentSize, int totalSize);

        void onSuccess(String path);

        void onCancel();

        void onFailure(String error);
    }

}
