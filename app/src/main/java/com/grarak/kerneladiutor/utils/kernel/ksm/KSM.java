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
package com.grarak.kerneladiutor.utils.kernel.ksm;

import android.content.Context;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 28.06.16.
 */
public class KSM {

    private static KSM sInstance;

    public static KSM getInstance() {
        if (sInstance == null) {
            sInstance = new KSM();
        }
        return sInstance;
    }

    private static final String KSM = "/sys/kernel/mm/ksm";
    private static final String UKSM = "/sys/kernel/mm/uksm";
    private static final String FULL_SCANS = "/full_scans";
    private static final String PAGES_SHARED = "/pages_shared";
    private static final String PAGES_SHARING = "/pages_sharing";
    private static final String PAGES_UNSHARED = "/pages_unshared";
    private static final String PAGES_VOLATILE = "/pages_volatile";
    private static final String RUN = "/run";
    private static final String DEFERRED_TIMER = "/deferred_timer";
    private static final String PAGES_TO_SCAN = "/pages_to_scan";
    private static final String SLEEP_MILLISECONDS = "/sleep_millisecs";
    private static final String MAX_CPU_PERCENTAGE = "/max_cpu_percentage";

    private final List<String> mParent = new ArrayList<>();
    private final LinkedHashMap<String, Integer> mInfos = new LinkedHashMap<>();

    {
        mParent.add(KSM);
        mParent.add(UKSM);

        mInfos.put(FULL_SCANS, R.string.full_scans);
        mInfos.put(PAGES_SHARED, R.string.pages_shared);
        mInfos.put(PAGES_SHARING, R.string.pages_sharing);
        mInfos.put(PAGES_UNSHARED, R.string.pages_unshared);
        mInfos.put(PAGES_VOLATILE, R.string.pages_volatile);
    }

    private String PARENT;

    private KSM() {
        for (String file : mParent) {
            if (Utils.existFile(file)) {
                PARENT = file;
                break;
            }
        }
    }

    public void setMaxCpuPercentage(int value, Context context) {
        run(Control.write(String.valueOf(value), MAX_CPU_PERCENTAGE), MAX_CPU_PERCENTAGE, context);
    }

    public int getMaxCpuPercentage() {
        return Utils.strToInt(Utils.readFile(MAX_CPU_PERCENTAGE));
    }

    public boolean hasMaxCpuPercentage() {
        return Utils.existFile(MAX_CPU_PERCENTAGE);
    }

    public void setSleepMilliseconds(int ms, Context context) {
        run(Control.write(String.valueOf(ms), PARENT + SLEEP_MILLISECONDS), PARENT + SLEEP_MILLISECONDS, context);
    }

    public int getSleepMilliseconds() {
        return Utils.strToInt(Utils.readFile(PARENT + SLEEP_MILLISECONDS));
    }

    public boolean hasSleepMilliseconds() {
        return Utils.existFile(PARENT + SLEEP_MILLISECONDS);
    }

    public void setPagesToScan(int pages, Context context) {
        run(Control.write(String.valueOf(pages), PARENT + PAGES_TO_SCAN), PARENT + PAGES_TO_SCAN, context);
    }

    public int getPagesToScan() {
        return Utils.strToInt(Utils.readFile(PARENT + PAGES_TO_SCAN));
    }

    public boolean hasPagesToScan() {
        return Utils.existFile(PARENT + PAGES_TO_SCAN);
    }

    public void enableDeferredTimer(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", PARENT + DEFERRED_TIMER), PARENT + DEFERRED_TIMER, context);
    }

    public boolean isDeferredTimerEnabled() {
        return Utils.readFile(PARENT + DEFERRED_TIMER).equals("1");
    }

    public boolean hasDeferredTimer() {
        return Utils.existFile(PARENT + DEFERRED_TIMER);
    }

    public void enableKsm(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", PARENT + RUN), PARENT + RUN, context);
    }

    public boolean isEnabled() {
        return Utils.readFile(PARENT + RUN).equals("1");
    }

    public boolean hasEnable() {
        return Utils.existFile(PARENT + RUN);
    }

    public String getInfo(int position) {
        return Utils.readFile(PARENT + mInfos.keySet().toArray(new String[mInfos.size()])[position]);
    }

    public boolean hasInfo(int position) {
        return Utils.existFile(PARENT + mInfos.keySet().toArray(new String[mInfos.size()])[position]);
    }

    public String getInfoText(int position, Context context) {
        return context.getString(mInfos.get(mInfos.keySet().toArray(new String[mInfos.size()])[position]));
    }

    public int getInfosSize() {
        return mInfos.size();
    }

    public boolean supported() {
        return PARENT != null;
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.KSM, id, context);
    }

}
