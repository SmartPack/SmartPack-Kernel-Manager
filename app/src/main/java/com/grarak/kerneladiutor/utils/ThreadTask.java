/*
 * Copyright (C) 2015 The Android Open Source Project
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
 * limitations under the License
 */
package com.grarak.kerneladiutor.utils;

import android.app.Activity;

/**
 * Created by willi on 17.08.16.
 */

public abstract class ThreadTask<A, R> {

    private final Activity mActivity;

    public ThreadTask(Activity activity) {
        mActivity = activity;
    }

    public void onPreExecute() {
    }

    public abstract R doInBackground(A arg);

    public void onPostExecute(R ret) {
    }

    public ThreadTask<A, R> execute(final A arg) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onPreExecute();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final R ret = doInBackground(arg);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onPostExecute(ret);
                            }
                        });
                    }
                }).start();
            }
        });
        return this;
    }

}
