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
package com.smartpack.kernelmanager.services;

import android.content.Intent;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.MainActivity;
import com.smartpack.kernelmanager.utils.kernel.cpu.CPUFreq;
import com.smartpack.kernelmanager.utils.kernel.gpu.GPUFreq;
import com.smartpack.kernelmanager.utils.root.RootUtils;

/**
 * Created by willi on 21.07.16.
 */
public class DashClock extends DashClockExtension {

    private boolean mRunning = false;
    private ExtensionData extensionData;

    @Override
    protected void onUpdateData(int reason) {
        final String status = getString(R.string.app_name);
        final int cores = CPUFreq.getInstance(this).getCpuCount();

        if (extensionData == null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            extensionData = new ExtensionData()
                    .visible(true)
                    .icon(R.drawable.ic_launcher_preview)
                    .clickIntent(intent);
        }
        if (!mRunning) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            StringBuilder message = new StringBuilder();
                            if (RootUtils.rootAccess()) {
                                CPUFreq cpuFreq = CPUFreq.getInstance(DashClock.this);
                                GPUFreq gpuFreq = GPUFreq.getInstance();

                                StringBuilder cpu = new StringBuilder();
                                for (int i = 0; i < cores; i++) {
                                    int freq = cpuFreq.getCurFreq(i) / 1000;
                                    if (i != 0) cpu.append(" | ");
                                    cpu.append(freq == 0 ? getString(R.string.offline) : freq);
                                }
                                if (cpu.length() > 0) {
                                    message.append(getString(R.string.cpu)).append(": ")
                                            .append(cpu.toString()).append("\n");
                                }
                                message.append(getString(R.string.cpu_governor)).append(": ")
                                        .append(cpuFreq.getGovernor(false)).append("\n");

                                if (gpuFreq.hasCurFreq()) {
                                    message.append(getString(R.string.gpu)).append(": ")
                                            .append(gpuFreq.getCurFreq() / 1000000)
                                            .append(getString(R.string.mhz));
                                }
                            } else {
                                message.append(getString(R.string.no_root));
                            }

                            publishUpdate(extensionData.status(status).expandedBody(message.toString()));
                            Thread.sleep(5000);
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }).start();
        }
        mRunning = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RootUtils.closeSU();
    }

}
