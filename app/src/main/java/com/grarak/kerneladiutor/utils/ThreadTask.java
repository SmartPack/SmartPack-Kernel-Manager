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
