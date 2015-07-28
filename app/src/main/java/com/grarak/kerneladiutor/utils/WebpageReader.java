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

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by willi on 03.04.15.
 */
public class WebpageReader extends AsyncTask<String, Void, String> {

    private final WebpageCallback webpageCallback;

    public WebpageReader(WebpageCallback webpageCallback) {
        this.webpageCallback = webpageCallback;
    }

    @Override
    protected String doInBackground(String... params) {
        InputStream is = null;
        BufferedReader br = null;
        HttpURLConnection connection = null;
        StringBuilder sb = new StringBuilder();

        try {
            String line;
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            is = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                if (isCancelled()) {
                    is.close();
                    br.close();
                    connection.disconnect();
                    return "";
                }
                sb.append(line).append("\n");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(Constants.TAG, "Failed to read url: " + params[0]);
        } finally {
            try {
                if (is != null) is.close();
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (connection != null) connection.disconnect();
        }
        return sb.toString().trim();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        webpageCallback.onCallback(s, Html.fromHtml(s).toString());
    }

    public interface WebpageCallback {
        void onCallback(String raw, String html);
    }

}
