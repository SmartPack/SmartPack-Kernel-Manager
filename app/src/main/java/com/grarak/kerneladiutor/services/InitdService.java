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

package com.grarak.kerneladiutor.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.kerneladiutor.library.root.RootUtils;

/**
 * Created by willi on 25.04.15.
 */
public class InitdService extends Service {

    private final Handler hand = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                toast(getString(R.string.starting_initd));
            }

            @Override
            protected String doInBackground(Void... params) {
                RootUtils.SU su = new RootUtils.SU();
                su.runCommand("for i in `ls /system/etc/init.d`;do chmod 755 $i;done");
                String output = su.runCommand("[ -d /system/etc/init.d ] && run-parts /system/etc/init.d");
                su.close();
                return output;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.i(Constants.TAG, "init.d: " + s);
                toast(getString(R.string.finishing_initd));
                stopSelf();
            }
        }.execute();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void toast(final String message) {
        if (Utils.getBoolean("applyonbootshowtoast", true, getApplicationContext()))
            hand.post(new Runnable() {
                @Override
                public void run() {
                    Utils.toast(getString(R.string.app_name) + ": " + message, InitdService.this);
                }
            });
    }

}
