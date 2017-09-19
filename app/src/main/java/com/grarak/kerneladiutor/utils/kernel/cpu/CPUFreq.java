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
package com.grarak.kerneladiutor.utils.kernel.cpu;

import android.content.Context;
import android.util.SparseArray;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.CoreCtl;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.MPDecision;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.QcomBcl;
import com.grarak.kerneladiutor.utils.root.Control;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by willi on 19.04.16.
 */
public class CPUFreq {

    private static final String CPU_PRESENT = "/sys/devices/system/cpu/present";
    private static final String CUR_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq";
    private static final String AVAILABLE_FREQS = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_available_frequencies";
    public static final String TIME_STATE = "/sys/devices/system/cpu/cpufreq/stats/cpu%d/time_in_state";
    public static final String TIME_STATE_2 = "/sys/devices/system/cpu/cpu%d/cpufreq/stats/time_in_state";
    private static final String OPP_TABLE = "/sys/devices/system/cpu/cpu%d/opp_table";

    private static final String CPU_MAX_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_max_freq";
    private static final String CPU_MAX_FREQ_KT = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_max_freq_kt";
    private static final String HARD_CPU_MAX_FREQ = "/sys/kernel/cpufreq_hardlimit/scaling_max_freq";
    private static final String CPU_MIN_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_min_freq";
    private static final String HARD_CPU_MIN_FREQ = "/sys/kernel/cpufreq_hardlimit/scaling_min_freq";
    private static final String CPU_MAX_SCREEN_OFF_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/screen_off_max_freq";
    public static final String CPU_ONLINE = "/sys/devices/system/cpu/cpu%d/online";
    private static final String CPU_MSM_CPUFREQ_LIMIT = "/sys/kernel/msm_cpufreq_limit/cpufreq_limit";
    private static final String CPU_ENABLE_OC = "/sys/devices/system/cpu/cpu%d/cpufreq/enable_oc";
    public static final String CPU_LOCK_FREQ = "/sys/kernel/cpufreq_hardlimit/userspace_dvfs_lock";
    private static final String CPU_SCALING_GOVERNOR = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_governor";
    private static final String CPU_AVAILABLE_GOVERNORS = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_available_governors";
    private static final String CPU_GOVERNOR_TUNABLES = "/sys/devices/system/cpu/cpufreq/%s";
    private static final String CPU_GOVERNOR_TUNABLES_CORE = "/sys/devices/system/cpu/cpu%d/cpufreq/%s";

    private static int sCpuCount;
    private static int sBigCpu = -1;
    private static int sLITTLECpu = -1;
    public static int sCoreCtlMinCpu;
    private static SparseArray<List<Integer>> sFreqs = new SparseArray<>();
    private static String[] sGovernors;

    public static String getGovernorTunablesPath(int cpu, String governor) {
        if (Utils.existFile(Utils.strFormat(CPU_GOVERNOR_TUNABLES_CORE, cpu, governor))) {
            return CPU_GOVERNOR_TUNABLES_CORE.replace("%s", governor);
        } else {
            return Utils.strFormat(CPU_GOVERNOR_TUNABLES, governor);
        }
    }

    public static boolean isOffline(int cpu) {
        return getCurFreq(cpu) == 0;
    }

    public static void applyCpu(String path, String value, int min, int max, Context context) {
        boolean cpulock = Utils.existFile(CPU_LOCK_FREQ);
        if (cpulock) {
            run(Control.write("0", CPU_LOCK_FREQ), null, null);
        }
        boolean mpdecision = MPDecision.supported() && MPDecision.isMpdecisionEnabled();
        if (mpdecision) {
            MPDecision.enableMpdecision(false, null);
        }
        for (int i = min; i <= max; i++) {
            boolean offline = isOffline(i);
            if (offline) {
                onlineCpu(i, true, false, null);
            }
            run(Control.chmod("644", Utils.strFormat(path, i)), null, null);
            run(Control.write(value, Utils.strFormat(path, i)), null, null);
            run(Control.chmod("444", Utils.strFormat(path, i)), null, null);
            if (offline) {
                onlineCpu(i, false, false, null);
            }
        }
        if (mpdecision) {
            MPDecision.enableMpdecision(true, null);
        }
        if (cpulock) {
            run(Control.write("1", CPU_LOCK_FREQ), null, null);
        }
        if (context != null) {
            if (isBigLITTLE()) {
                List<Integer> bigCpus = getBigCpuRange();
                List<Integer> littleCpus = getLITTLECpuRange();
                run("#" + new ApplyCpu(path, value, min, max, bigCpus.toArray(new Integer[bigCpus.size()]),
                        littleCpus.toArray(new Integer[littleCpus.size()]),
                        sCoreCtlMinCpu).toString(), path + min, context);
            } else {
                run("#" + new ApplyCpu(path, value, min, max).toString(), path + min, context);
            }
        }
    }

    public static class ApplyCpu {
        private String mJson;
        private String mPath;
        private String mValue;
        private int mMin;
        private int mMax;

        // big.LITTLE
        private List<Integer> mBigCpus;
        private List<Integer> mLITTLECpus;
        private int mCoreCtlMin;

        private ApplyCpu(String path, String value, int min, int max) {
            try {
                JSONObject main = new JSONObject();
                init(main, path, value, min, max);
                mJson = main.toString();
            } catch (JSONException ignored) {
            }
        }

        private ApplyCpu(String path, String value, int min, int max, Integer[] bigCpus,
                         Integer[] littleCpus, int corectlmin) {
            try {
                JSONObject main = new JSONObject();
                init(main, path, value, min, max);

                // big.LITTLE
                JSONArray bigCpusArray = new JSONArray();
                for (int cpu : bigCpus) {
                    bigCpusArray.put(cpu);
                }
                main.put("bigCpus", bigCpusArray);
                mBigCpus = Arrays.asList(bigCpus);

                JSONArray LITTLECpusArray = new JSONArray();
                for (int cpu : littleCpus) {
                    LITTLECpusArray.put(cpu);
                }
                main.put("LITTLECpus", LITTLECpusArray);
                mLITTLECpus = Arrays.asList(littleCpus);

                main.put("corectlmin", mCoreCtlMin = corectlmin);

                mJson = main.toString();
            } catch (JSONException ignored) {
            }
        }

        private void init(JSONObject main, String path, String value, int min, int max)
                throws JSONException {
            main.put("path", mPath = path);
            main.put("value", mValue = value);
            main.put("min", mMin = min);
            main.put("max", mMax = max);
        }

        public ApplyCpu(String json) {
            try {
                JSONObject main = new JSONObject(json);
                mPath = getString(main, "path");
                mValue = getString(main, "value");
                mMin = getInt(main, "min");
                mMax = getInt(main, "max");

                // big.LITTLE
                Integer[] bigCpus = getIntArray(main, "bigCpus");
                if (bigCpus != null) {
                    mBigCpus = Arrays.asList(bigCpus);
                }

                Integer[] LITTLECpus = getIntArray(main, "LITTLECpus");
                if (LITTLECpus != null) {
                    mLITTLECpus = Arrays.asList(LITTLECpus);
                }

                mCoreCtlMin = getInt(main, "corectlmin");

                mJson = json;
            } catch (JSONException ignored) {
            }
        }

        private Integer[] getIntArray(JSONObject jsonObject, String key) {
            try {
                JSONArray array = jsonObject.getJSONArray(key);
                Integer[] integers = new Integer[array.length()];
                for (int i = 0; i < integers.length; i++) {
                    integers[i] = array.getInt(i);
                }
                return integers;
            } catch (JSONException ignored) {
                return null;
            }
        }

        private String getString(JSONObject jsonObject, String key) {
            try {
                return jsonObject.getString(key);
            } catch (JSONException ignored) {
                return null;
            }
        }

        private int getInt(JSONObject jsonObject, String key) {
            try {
                return jsonObject.getInt(key);
            } catch (JSONException ignored) {
                return -1;
            }
        }

        public int getCoreCtlMin() {
            return mCoreCtlMin;
        }

        public List<Integer> getLITTLECpuRange() {
            return mLITTLECpus;
        }

        public List<Integer> getBigCpuRange() {
            return mBigCpus;
        }

        public boolean isBigLITTLE() {
            return getBigCpuRange() != null && getLITTLECpuRange() != null;
        }

        public int getMax() {
            return mMax;
        }

        public int getMin() {
            return mMin;
        }

        public String getValue() {
            return mValue;
        }

        public String getPath() {
            return mPath;
        }

        public String toString() {
            return mJson;
        }
    }

    public static void setGovernor(String governor, int min, int max, Context context) {
        applyCpu(CPU_SCALING_GOVERNOR, governor, min, max, context);
    }

    public static String getGovernor(boolean forceRead) {
        return getGovernor(getBigCpu(), forceRead);
    }

    public static String getGovernor(int cpu, boolean forceRead) {
        boolean offline = forceRead && isOffline(cpu);
        if (offline) {
            onlineCpu(cpu, true, false, null);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String value = "";
        if (Utils.existFile(Utils.strFormat(CPU_SCALING_GOVERNOR, cpu))) {
            value = Utils.readFile(Utils.strFormat(CPU_SCALING_GOVERNOR, cpu));
        }

        if (offline) {
            onlineCpu(cpu, false, false, null);
        }
        return value;
    }

    public static List<String> getGovernors() {
        if (sGovernors == null) {
            boolean offline = isOffline(0);
            if (offline) {
                onlineCpu(0, true, false, null);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (Utils.existFile(Utils.strFormat(CPU_AVAILABLE_GOVERNORS, 0))) {
                String value = Utils.readFile(Utils.strFormat(CPU_AVAILABLE_GOVERNORS, 0));
                if (value != null) {
                    sGovernors = value.split(" ");
                }
            }

            if (offline) {
                onlineCpu(0, false, false, null);
            }
        }
        if (sGovernors == null) return getGovernors();
        return Arrays.asList(sGovernors);
    }

    private static int getFreq(int cpu, String path, boolean forceRead) {
        boolean offline = forceRead && isOffline(cpu);
        if (offline) {
            onlineCpu(cpu, true, false, null);

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int freq = 0;
        String value = Utils.readFile(Utils.strFormat(path, cpu));
        if (value != null) freq = Utils.strToInt(value);

        if (offline) {
            onlineCpu(cpu, false, false, null);
        }
        return freq;
    }

    public static void setMaxScreenOffFreq(int freq, int min, int max, Context context) {
        applyCpu(CPU_MAX_SCREEN_OFF_FREQ, String.valueOf(freq), min, max, context);
    }

    public static int getMaxScreenOffFreq(boolean forceRead) {
        return getMaxScreenOffFreq(getBigCpu(), forceRead);
    }

    public static int getMaxScreenOffFreq(int cpu, boolean forceRead) {
        return getFreq(cpu, CPU_MAX_SCREEN_OFF_FREQ, forceRead);
    }

    public static boolean hasMaxScreenOffFreq() {
        return hasMaxScreenOffFreq(getBigCpu());
    }

    public static boolean hasMaxScreenOffFreq(int cpu) {
        return Utils.existFile(Utils.strFormat(CPU_MAX_SCREEN_OFF_FREQ, cpu));
    }

    public static void setMinFreq(int freq, int min, int max, Context context) {
        int maxFreq = getMaxFreq(min, false);
        if (maxFreq != 0 && freq > maxFreq) {
            setMaxFreq(freq, min, max, context);
        }
        if (MSMPerformance.hasCpuMinFreq()) {
            for (int i = min; i <= max; i++) {
                MSMPerformance.setCpuMinFreq(freq, i, context);
            }
        }
        applyCpu(CPU_MIN_FREQ, String.valueOf(freq), min, max, context);
        if (Utils.existFile(HARD_CPU_MIN_FREQ)) {
            run(Control.write(String.valueOf(freq), HARD_CPU_MIN_FREQ), HARD_CPU_MIN_FREQ, context);
        }
    }

    public static int getMinFreq(boolean forceRead) {
        return getMinFreq(getBigCpu(), forceRead);
    }

    public static int getMinFreq(int cpu, boolean forceRead) {
        return getFreq(cpu, CPU_MIN_FREQ, forceRead);
    }

    public static void setMaxFreq(int freq, int min, int max, Context context) {
        if (Utils.existFile(CPU_MSM_CPUFREQ_LIMIT) && freq > Utils.strToInt(Utils.readFile(CPU_MSM_CPUFREQ_LIMIT))) {
            run(Control.write(String.valueOf(freq), CPU_MSM_CPUFREQ_LIMIT), CPU_MSM_CPUFREQ_LIMIT, context);
        }
        int minFreq = getMinFreq(min, false);
        if (minFreq != 0 && freq < minFreq) {
            setMinFreq(freq, min, max, context);
        }
        if (Utils.existFile(Utils.strFormat(CPU_ENABLE_OC, 0))) {
            for (int i = min; i <= max; i++) {
                run(Control.write("1", Utils.strFormat(CPU_ENABLE_OC, i)),
                        Utils.strFormat(CPU_ENABLE_OC, i), context);
            }
        }
        if (MSMPerformance.hasCpuMaxFreq()) {
            for (int i = min; i <= max; i++) {
                MSMPerformance.setCpuMaxFreq(freq, i, context);
            }
        }
        if (Utils.existFile(Utils.strFormat(CPU_MAX_FREQ_KT, 0))) {
            applyCpu(CPU_MAX_FREQ_KT, String.valueOf(freq), min, max, context);
        } else {
            applyCpu(CPU_MAX_FREQ, String.valueOf(freq), min, max, context);
        }
        if (Utils.existFile(HARD_CPU_MAX_FREQ)) {
            run(Control.write(String.valueOf(freq), HARD_CPU_MAX_FREQ), HARD_CPU_MAX_FREQ, context);
        }
    }

    public static int getMaxFreq(boolean forceRead) {
        return getMaxFreq(getBigCpu(), forceRead);
    }

    public static int getMaxFreq(int cpu, boolean forceRead) {
        if (Utils.existFile(Utils.strFormat(CPU_MAX_FREQ_KT, cpu))) {
            return getFreq(cpu, CPU_MAX_FREQ_KT, forceRead);
        } else {
            return getFreq(cpu, CPU_MAX_FREQ, forceRead);
        }
    }

    public static List<String> getAdjustedFreq(Context context) {
        return getAdjustedFreq(getBigCpu(), context);
    }

    public static List<String> getAdjustedFreq(int cpu, Context context) {
        List<String> freqs = new ArrayList<>();
        if (getFreqs(cpu) != null) {
            for (int freq : getFreqs(cpu)) {
                freqs.add((freq / 1000) + context.getString(R.string.mhz));
            }
        }
        return freqs;
    }

    public static List<Integer> getFreqs() {
        return getFreqs(getBigCpu());
    }

    public static List<Integer> getFreqs(int cpu) {
        if (sFreqs.indexOfKey(cpu) < 0) {
            if (Utils.existFile(Utils.strFormat(OPP_TABLE, cpu))
                    || Utils.existFile(Utils.strFormat(TIME_STATE, cpu))
                    || Utils.existFile(Utils.strFormat(TIME_STATE_2, cpu))) {
                String file;
                if (Utils.existFile(Utils.strFormat(OPP_TABLE, cpu))) {
                    file = Utils.strFormat(OPP_TABLE, cpu);
                } else if (Utils.existFile(Utils.strFormat(TIME_STATE, cpu))) {
                    file = Utils.strFormat(TIME_STATE, cpu);
                } else {
                    file = Utils.strFormat(TIME_STATE_2, cpu);
                }
                String value = Utils.readFile(file);
                if (value != null) {
                    String[] valueArray = value.trim().split("\\r?\\n");
                    List<Integer> freqs = new ArrayList<>();
                    for (String freq : valueArray) {
                        long freqInt = Utils.strToLong(freq.split(" ")[0]);
                        if (file.endsWith("opp_table")) {
                            freqInt /= 1000;
                        }
                        freqs.add((int) freqInt);
                    }
                    sFreqs.put(cpu, freqs);
                }
            } else if (Utils.existFile(Utils.strFormat(AVAILABLE_FREQS, cpu))) {
                int readcpu = cpu;
                boolean offline = isOffline(cpu);
                if (offline) {
                    onlineCpu(cpu, true, false, null);
                }
                if (!Utils.existFile(Utils.strFormat(Utils.strFormat(AVAILABLE_FREQS, cpu)))) {
                    readcpu = 0;
                }
                String values;
                if ((values = Utils.readFile(Utils.strFormat(AVAILABLE_FREQS, readcpu))) != null) {
                    String[] valueArray = values.split(" ");
                    List<Integer> freqs = new ArrayList<>();
                    for (String freq : valueArray) {
                        freqs.add(Utils.strToInt(freq));
                    }
                    sFreqs.put(cpu, freqs);
                }
                if (offline) {
                    onlineCpu(cpu, false, false, null);
                }
            }
        }
        if (sFreqs.indexOfKey(cpu) < 0) {
            return null;
        }
        List<Integer> freqs = sFreqs.get(cpu);
        Collections.sort(freqs);
        return freqs;
    }

    public static int getCurFreq(int cpu) {
        if (Utils.existFile(Utils.strFormat(CUR_FREQ, cpu))) {
            String value = Utils.readFile(Utils.strFormat(CUR_FREQ, cpu));
            if (value != null) {
                return Utils.strToInt(value);
            }
        }
        return 0;
    }

    public static void onlineCpu(int cpu, boolean online, boolean onlineSys, Context context) {
        onlineCpu(cpu, online, ApplyOnBootFragment.CPU, onlineSys, context);
    }

    public static void onlineCpu(int cpu, boolean online, String category, boolean onlineSys,
                                 Context context) {
        if (!onlineSys) {
            if (QcomBcl.supported()) {
                QcomBcl.online(online, category, context);
            }
            if (CoreCtl.hasMinCpus() && getBigCpuRange().contains(cpu)) {
                CoreCtl.setMinCpus(online ? getBigCpuRange().size() : sCoreCtlMinCpu, cpu, category,
                        context);
            }
            if (MSMPerformance.hasMaxCpus()) {
                MSMPerformance.setMaxCpus(online ? getBigCpuRange().size() : -1, online ?
                        getLITTLECpuRange().size() : -1, category, context);
            }
        }
        Control.runSetting(Control.chmod("644", Utils.strFormat(CPU_ONLINE, cpu)),
                category, Utils.strFormat(CPU_ONLINE, cpu) + "chmod644", context);
        Control.runSetting(Control.write(online ? "1" : "0", Utils.strFormat(CPU_ONLINE, cpu)),
                category, Utils.strFormat(CPU_ONLINE, cpu), context);
        Control.runSetting(Control.chmod("444", Utils.strFormat(CPU_ONLINE, cpu)),
                category, Utils.strFormat(CPU_ONLINE, cpu) + "chmod444", context);
    }

    public static List<Integer> getLITTLECpuRange() {
        List<Integer> list = new ArrayList<>();
        if (!isBigLITTLE()) {
            for (int i = 0; i < getCpuCount(); i++) {
                list.add(i);
            }
        } else if (getLITTLECpu() == 0) {
            for (int i = 0; i < getBigCpu(); i++) {
                list.add(i);
            }
        } else {
            for (int i = getLITTLECpu(); i < getCpuCount(); i++) {
                list.add(i);
            }
        }
        return list;
    }

    public static List<Integer> getBigCpuRange() {
        List<Integer> list = new ArrayList<>();
        if (!isBigLITTLE()) {
            for (int i = 0; i < getCpuCount(); i++) {
                list.add(i);
            }
        } else if (getBigCpu() == 0) {
            for (int i = 0; i < getLITTLECpu(); i++) {
                list.add(i);
            }
        } else {
            for (int i = getBigCpu(); i < getCpuCount(); i++) {
                list.add(i);
            }
        }
        return list;
    }

    public static int getLITTLECpu() {
        isBigLITTLE();
        return sLITTLECpu < 0 ? 0 : sLITTLECpu;
    }

    public static int getBigCpu() {
        isBigLITTLE();
        return sBigCpu < 0 ? 0 : sBigCpu;
    }

    public static boolean isBigLITTLE() {
        if (sBigCpu == -1 || sLITTLECpu == -1) {
            if (getCpuCount() <= 4 && !is8996()
                    || (Device.getBoard().startsWith("mt6") && !Device.getBoard().startsWith("mt6595"))
                    || Device.getBoard().startsWith("msm8929")) return false;

            if (is8996()) {
                sBigCpu = 2;
                sLITTLECpu = 0;
            } else {
                List<Integer> cpu0Freqs = getFreqs(0);
                List<Integer> cpu4Freqs = getFreqs(4);
                if (cpu0Freqs != null && cpu4Freqs != null) {
                    int cpu0Max = cpu0Freqs.get(cpu0Freqs.size() - 1);
                    int cpu4Max = cpu4Freqs.get(cpu4Freqs.size() - 1);
                    if (cpu0Max > cpu4Max
                            || (cpu0Max == cpu4Max && cpu0Freqs.size() > cpu4Freqs.size())) {
                        sBigCpu = 0;
                        sLITTLECpu = 4;
                    } else {
                        sBigCpu = 4;
                        sLITTLECpu = 0;
                    }
                }
            }

            if (sBigCpu == -1 || sLITTLECpu == -1) {
                sBigCpu = -2;
                sLITTLECpu = -2;
            }
        }

        return sBigCpu >= 0 && sLITTLECpu >= 0;
    }

    private static boolean is8996() {
        String board = Device.getBoard();
        return board.equalsIgnoreCase("msm8996") || board.equalsIgnoreCase("msm8996pro");
    }

    public static int getCpuCount() {
        if (sCpuCount == 0 && Utils.existFile(CPU_PRESENT)) {
            try {
                String output = Utils.readFile(CPU_PRESENT);
                sCpuCount = output.equals("0") ? 1 : Integer.parseInt(output.split("-")[1]) + 1;
            } catch (Exception ignored) {
            }
        }
        if (sCpuCount == 0) {
            sCpuCount = Runtime.getRuntime().availableProcessors();
        }
        return sCpuCount;
    }

    public static float[] getCpuUsage() {
        try {
            Usage[] prevUsage = getUsages();
            Thread.sleep(500);
            Usage[] usage = getUsages();

            if (prevUsage != null && usage != null) {
                float[] pers = new float[prevUsage.length];
                for (int i = 0; i < prevUsage.length; i++) {
                    float prevIdle = prevUsage[i].getIdle();
                    float prevUp = prevUsage[i].getUptime();

                    float idle = usage[i].getIdle();
                    float up = usage[i].getUptime();

                    float cpu = (up - prevUp) / ((up + idle) - (prevUp + prevIdle)) * 100;
                    pers[i] = cpu < 0 ? 0 : cpu > 100 ? 100 : cpu;
                }
                return pers;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Usage[] getUsages() {
        String stat = Utils.readFile("/proc/stat");
        if (stat == null) return null;
        String[] stats = stat.split("\\r?\\n");

        Usage[] usage = new Usage[getCpuCount() + 1];
        for (int i = 0; i < usage.length; i++) {
            if (i >= stats.length) return null;
            usage[i] = new Usage(stats[i]);
        }
        return usage;
    }

    private static class Usage {

        private long[] stats;

        private Usage(String stats) {
            if (stats == null) return;

            String[] values = stats.replaceAll("\\s{2,}", " ").trim().split(" ");
            this.stats = new long[values.length - 1];
            for (int i = 0; i < this.stats.length; i++) {
                this.stats[i] = Utils.strToLong(values[i + 1]);
            }
        }

        public long getUptime() {
            if (stats == null) return 0;
            long l = 0;
            for (int i = 0; i < stats.length - 2; i++) {
                if (i != 3 && i != 4) l += stats[i];
            }
            return l;
        }

        private long getIdle() {
            try {
                return stats == null ? 0 : stats[3] + stats[4];
            } catch (ArrayIndexOutOfBoundsException e) {
                return 0;
            }
        }

    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU, id, context);
    }

}
