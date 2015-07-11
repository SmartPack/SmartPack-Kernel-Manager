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

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.kernel.CPU;
import com.grarak.kerneladiutor.utils.kernel.GPU;
import com.grarak.kerneladiutor.utils.root.RootUtils;

/**
 * Created by willi on 25.03.15.
 */
public class DashClockService extends DashClockExtension {

    private boolean running = false;
    private ExtensionData extensionData;

    @Override
    protected void onUpdateData(int reason) {
        final String status = getString(R.string.app_name);
        final int cores = CPU.getCoreCount();

        if (extensionData == null)
            extensionData = new ExtensionData().visible(true).icon(R.drawable.ic_launcher_preview);
        if (!running) new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String body = "";
                        if (RootUtils.rootAccess()) {
                            String cpu = "";
                            for (int i = 0; i < cores; i++) {
                                if (!cpu.isEmpty()) cpu += " | ";
                                int freq = CPU.getCurFreq(i) / 1000;
                                cpu += freq == 0 ? "Offline" : freq;
                            }
                            if (!cpu.isEmpty()) body += "CPU: " + cpu + "\n";
                            body += "GOVERNOR: " + CPU.getCurGovernor(true) + "\n";

                            if (GPU.hasGpuCurFreq())
                                body += "GPU: " + (GPU.getGpuCurFreq() / 1000000) + "MHz";
                        } else body = getString(R.string.no_root);

                        publishUpdate(extensionData.status(status).expandedBody(body));
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();
        running = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RootUtils.closeSU();
    }

}
