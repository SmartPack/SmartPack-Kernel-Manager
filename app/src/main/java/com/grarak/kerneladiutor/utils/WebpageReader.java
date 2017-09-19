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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by willi on 06.07.16.
 */
public class WebpageReader {

    public interface WebpageListener {

        void onSuccess(String url, String raw, CharSequence html);

        void onFailure(String url);
    }

    private Activity mActivity;
    private HttpURLConnection mConnection;
    private WebpageListener mWebpageListener;

    public WebpageReader(Activity activity, WebpageListener webpageListener) {
        mActivity = activity;
        mWebpageListener = webpageListener;
    }

    public void get(final String link) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                try {
                    mConnection = (HttpURLConnection) new URL(link).openConnection();
                    mConnection.setRequestMethod("GET");
                    mConnection.setConnectTimeout(10000);
                    mConnection.setReadTimeout(10000);

                    InputStream inputStream;
                    if (mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        inputStream = mConnection.getInputStream();
                    } else {
                        inputStream = mConnection.getErrorStream();
                    }

                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }

                    success(link, sb.toString());
                } catch (IOException ignored) {
                    failure(link);
                } finally {
                    try {
                        if (reader != null) reader.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }).start();
    }

    private void success(final String url, final String result) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebpageListener.onSuccess(url, result, Utils.htmlFrom(result));
            }
        });
    }

    private void failure(final String url) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebpageListener.onFailure(url);
            }
        });
    }

    public void cancel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    mConnection.disconnect();
                }
            }
        }).start();
    }

}
