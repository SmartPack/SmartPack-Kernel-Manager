/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
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

package com.grarak.kerneladiutor.tasker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.kerneladiutor.library.root.RootUtils;

/**
 * Created by willi on 28.07.15.
 */
public class RunProfileReceiver extends BroadcastReceiver {

    public static final String ACTION_FIRE_SETTING = "com.twofortyfouram.locale.intent.action.FIRE_SETTING";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ACTION_FIRE_SETTING.equals(intent.getAction()) || !RootUtils.rootAccess() || !RootUtils.busyboxInstalled())
            return;
        RootUtils.closeSU();

        BundleScrubber.scrub(intent);

        final Bundle bundle = intent.getBundleExtra(AddProfileActivity.EXTRA_BUNDLE);
        BundleScrubber.scrub(bundle);

        if (PluginBundleManager.isBundleValid(bundle)) {
            String commands = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_MESSAGE);
            if (commands != null) {
                String[] cs = commands.split(AddProfileActivity.DIVIDER);
                Log.i(Constants.TAG + ": " + getClass().getSimpleName(), "Applying " + cs[0]);
                Utils.toast(context.getString(R.string.applying_profile, cs[0]), context, Toast.LENGTH_LONG);

                if (cs.length > 1) {
                    RootUtils.SU su = new RootUtils.SU();
                    for (int i = 1; i < cs.length; i++) {
                        Log.i(Constants.TAG + ": " + getClass().getSimpleName(), "Run: " + cs[i]);
                        su.runCommand(cs[i]);
                    }
                    su.close();
                }
            }
        }
    }

}
