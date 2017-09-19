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
public class DownloadTask {

    public interface OnDownloadListener {
        void onUpdate(String url, int currentSize, int totalSize);

        void onSuccess(String url, String path);

        void onCancel(String url);

        void onFailure(String url);
    }

    private final Activity mActivity;
    private final OnDownloadListener onDownloadListener;
    private PowerManager.WakeLock mWakelock;
    private HttpURLConnection mConnection;
    private boolean mCancelled;
    private boolean mPause;

    public DownloadTask(Activity activity, OnDownloadListener onDownloadListener) {
        mActivity = activity;
        this.onDownloadListener = onDownloadListener;
    }

    public void get(final String link, final String path) {
        PowerManager pm = (PowerManager) mActivity.getSystemService(Context.POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakelock.acquire();

        new File(path).getParentFile().mkdirs();

        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream input = null;
                FileOutputStream fileOutput = null;

                try {
                    mConnection = (HttpURLConnection) new URL(link).openConnection();
                    mConnection.connect();

                    if (mConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        failure(link, path);
                        releaseWakelock();
                        return;
                    }

                    int totalSize = mConnection.getContentLength();
                    input = mConnection.getInputStream();
                    fileOutput = new FileOutputStream(path);

                    byte data[] = new byte[4096];
                    int currentSize = 0;
                    int count;
                    while (true) {
                        if (!mPause) {
                            if ((count = input.read(data)) != -1) {
                                currentSize += count;
                                if (totalSize > 0) {
                                    final int cs = currentSize;
                                    final int ts = totalSize;
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!mActivity.isFinishing()) {
                                                onDownloadListener.onUpdate(link, cs, ts);
                                            }
                                        }
                                    });
                                }
                                fileOutput.write(data, 0, count);
                            } else {
                                success(link, path);
                                break;
                            }
                        }
                    }
                } catch (IOException ignored) {
                    if (mCancelled) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onDownloadListener.onCancel(link);
                            }
                        });
                    } else {
                        failure(link, path);
                    }
                    mCancelled = false;
                } finally {
                    try {
                        if (fileOutput != null) fileOutput.close();
                        if (input != null) input.close();
                    } catch (IOException ignored) {
                    }
                }

                releaseWakelock();
            }
        }).start();
    }

    private void success(final String url, final String path) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onDownloadListener.onSuccess(url, path);
            }
        });
    }

    private void failure(final String url, final String path) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onDownloadListener.onFailure(url);
                new File(path).delete();
            }
        });
    }

    public void pause() {
        mPause = true;
    }

    public void resume() {
        mPause = false;
    }

    public void cancel() {
        mCancelled = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mConnection.disconnect();
            }
        }).start();
    }

    private void releaseWakelock() {
        mWakelock.release();
    }

}
