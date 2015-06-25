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

package com.grarak.kerneladiutor.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class GPU implements Constants {

    private static String GPU_2D_CUR_FREQ;
    private static String GPU_2D_MAX_FREQ;
    private static String GPU_2D_AVAILABLE_FREQS;
    private static String GPU_2D_SCALING_GOVERNOR;

    private static Integer[] mGpu2dFreqs;

    private static String GPU_CUR_FREQ;
    private static String GPU_MAX_FREQ;
    private static String GPU_AVAILABLE_FREQS;
    private static String GPU_SCALING_GOVERNOR;
    private static String[] GPU_AVAILABLE_GOVERNORS;

    private static Integer[] mGpuFreqs;

    public static void setAdrenoIdlerIdleWorkload(int value, Context context) {
        Control.runCommand(String.valueOf(value * 1000), ADRENO_IDLER_IDLEWORKLOAD, Control.CommandType.GENERIC, context);
    }

    public static int getAdrenoIdlerIdleWorkload() {
        return Utils.stringToInt(Utils.readFile(ADRENO_IDLER_IDLEWORKLOAD)) / 1000;
    }

    public static void setAdrenoIdlerIdleWait(int value, Context context) {
        Control.runCommand(String.valueOf(value), ADRENO_IDLER_IDLEWAIT, Control.CommandType.GENERIC, context);
    }

    public static int getAdrenoIdlerIdleWait() {
        return Utils.stringToInt(Utils.readFile(ADRENO_IDLER_IDLEWAIT));
    }

    public static void setAdrenoIdlerDownDiff(int value, Context context) {
        Control.runCommand(String.valueOf(value), ADRENO_IDLER_DOWNDIFFERENTIAL, Control.CommandType.GENERIC, context);
    }

    public static int getAdrenoIdlerDownDiff() {
        return Utils.stringToInt(Utils.readFile(ADRENO_IDLER_DOWNDIFFERENTIAL));
    }

    public static void activateAdrenoIdler(boolean active, Context context) {
        Control.runCommand(active ? "Y" : "N", ADRENO_IDLER_ACTIVATE, Control.CommandType.GENERIC, context);
    }

    public static boolean isAdrenoIdlerActive() {
        return Utils.readFile(ADRENO_IDLER_ACTIVATE).equals("Y");
    }

    public static boolean hasAdrenoIdler() {
        return Utils.existFile(ADRENO_IDLER_PARAMETERS);
    }

    public static void setSimpleGpuRampThreshold(int value, Context context) {
        Control.runCommand(String.valueOf(value * 1000), SIMPLE_RAMP_THRESHOLD, Control.CommandType.GENERIC, context);
    }

    public static int getSimpleGpuRampThreshold() {
        return Utils.stringToInt(Utils.readFile(SIMPLE_RAMP_THRESHOLD)) / 1000;
    }

    public static void setSimpleGpuLaziness(int value, Context context) {
        Control.runCommand(String.valueOf(value), SIMPLE_GPU_LAZINESS, Control.CommandType.GENERIC, context);
    }

    public static int getSimpleGpuLaziness() {
        return Utils.stringToInt(Utils.readFile(SIMPLE_GPU_LAZINESS));
    }

    public static void activateSimpleGpu(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", SIMPLE_GPU_ACTIVATE, Control.CommandType.GENERIC, context);
    }

    public static boolean isSimpleGpuActive() {
        return Utils.readFile(SIMPLE_GPU_ACTIVATE).equals("1");
    }

    public static boolean hasSimpleGpu() {
        return Utils.existFile(SIMPLE_GPU_PARAMETERS);
    }

    public static void setGpu2dGovernor(String governor, Context context) {
        if (GPU_2D_SCALING_GOVERNOR != null)
            Control.runCommand(governor, GPU_2D_SCALING_GOVERNOR, Control.CommandType.GENERIC, context);
    }

    public static List<String> getGpu2dGovernors() {
        return new ArrayList<>(Arrays.asList(GPU_GENERIC_GOVERNORS.split(" ")));
    }

    public static String getGpu2dGovernor() {
        if (GPU_2D_SCALING_GOVERNOR != null)
            if (Utils.existFile(GPU_2D_SCALING_GOVERNOR)) {
                String value = Utils.readFile(GPU_2D_SCALING_GOVERNOR);
                if (value != null) return value;
            }
        return "";
    }

    public static boolean hasGpu2dGovernor() {
        if (GPU_2D_SCALING_GOVERNOR == null)
            for (String file : GPU_2D_SCALING_GOVERNOR_ARRAY)
                if (Utils.existFile(file)) GPU_2D_SCALING_GOVERNOR = file;
        return GPU_2D_SCALING_GOVERNOR != null;
    }

    public static void setGpu2dMaxFreq(int freq, Context context) {
        if (GPU_2D_MAX_FREQ != null)
            Control.runCommand(String.valueOf(freq), GPU_2D_MAX_FREQ, Control.CommandType.GENERIC, context);
    }

    public static List<Integer> getGpu2dFreqs() {
        if (GPU_2D_AVAILABLE_FREQS != null)
            if (mGpu2dFreqs == null)
                if (Utils.existFile(GPU_2D_AVAILABLE_FREQS)) {
                    String value = Utils.readFile(GPU_2D_AVAILABLE_FREQS);
                    if (value != null) {
                        String[] freqs = value.split(" ");
                        mGpu2dFreqs = new Integer[freqs.length];
                        for (int i = 0; i < mGpu2dFreqs.length; i++)
                            mGpu2dFreqs[i] = Utils.stringToInt(freqs[i]);
                    }
                }
        return new ArrayList<>(Arrays.asList(mGpu2dFreqs));
    }

    public static boolean hasGpu2dFreqs() {
        if (GPU_2D_AVAILABLE_FREQS == null) {
            for (String file : GPU_2D_AVAILABLE_FREQS_ARRAY)
                if (Utils.existFile(file)) GPU_2D_AVAILABLE_FREQS = file;
        }
        String value = Utils.readFile(GPU_2D_AVAILABLE_FREQS);
        if (value == null) value = "";
        return !value.isEmpty();
    }

    public static int getGpu2dMaxFreq() {
        if (GPU_2D_MAX_FREQ != null) if (Utils.existFile(GPU_2D_MAX_FREQ)) {
            String value = Utils.readFile(GPU_2D_MAX_FREQ);
            if (value != null) return Utils.stringToInt(value);
        }
        return 0;
    }

    public static boolean hasGpu2dMaxFreq() {
        if (GPU_2D_MAX_FREQ == null) {
            for (String file : GPU_2D_MAX_FREQ_ARRAY)
                if (Utils.existFile(file)) GPU_2D_MAX_FREQ = file;
        }
        return GPU_2D_MAX_FREQ != null;
    }

    public static int getGpu2dCurFreq() {
        if (GPU_2D_CUR_FREQ != null) if (Utils.existFile(GPU_2D_CUR_FREQ)) {
            String value = Utils.readFile(GPU_2D_CUR_FREQ);
            if (value != null) return Utils.stringToInt(value);
        }
        return 0;
    }

    public static boolean hasGpu2dCurFreq() {
        if (GPU_2D_CUR_FREQ == null) {
            for (String file : GPU_2D_CUR_FREQ_ARRAY)
                if (Utils.existFile(file)) GPU_2D_CUR_FREQ = file;
        }
        return GPU_2D_CUR_FREQ != null;
    }

    public static void setGpuGovernor(String governor, Context context) {
        if (GPU_SCALING_GOVERNOR != null)
            Control.runCommand(governor, GPU_SCALING_GOVERNOR, Control.CommandType.GENERIC, context);
    }

    public static List<String> getGpuGovernors() {
        if (GPU_AVAILABLE_GOVERNORS == null)
            for (String file : GPU_AVAILABLE_GOVERNORS_ARRAY)
                if (GPU_AVAILABLE_GOVERNORS == null)
                    if (Utils.existFile(file)) {
                        String value = Utils.readFile(file);
                        if (value != null)
                            GPU_AVAILABLE_GOVERNORS = value.split(" ");
                    }
        return new ArrayList<>(Arrays.asList(GPU_AVAILABLE_GOVERNORS == null ? GPU_GENERIC_GOVERNORS
                .split(" ") : GPU_AVAILABLE_GOVERNORS));
    }

    public static String getGpuGovernor() {
        if (GPU_SCALING_GOVERNOR != null)
            if (Utils.existFile(GPU_SCALING_GOVERNOR)) {
                String value = Utils.readFile(GPU_SCALING_GOVERNOR);
                if (value != null) return value;
            }
        return "";
    }

    public static boolean hasGpuGovernor() {
        if (GPU_SCALING_GOVERNOR == null)
            for (String file : GPU_SCALING_GOVERNOR_ARRAY)
                if (Utils.existFile(file)) GPU_SCALING_GOVERNOR = file;
        return GPU_SCALING_GOVERNOR != null;
    }

    public static void setGpuMaxFreq(int freq, Context context) {
        if (GPU_MAX_FREQ != null)
            Control.runCommand(String.valueOf(freq), GPU_MAX_FREQ, Control.CommandType.GENERIC, context);
    }

    public static List<Integer> getGpuFreqs() {
        if (GPU_AVAILABLE_FREQS != null)
            if (mGpuFreqs == null)
                if (Utils.existFile(GPU_AVAILABLE_FREQS)) {
                    String value = Utils.readFile(GPU_AVAILABLE_FREQS);
                    if (value != null) {
                        String[] freqs = value.split(" ");
                        mGpuFreqs = new Integer[freqs.length];
                        for (int i = 0; i < mGpuFreqs.length; i++)
                            mGpuFreqs[i] = Utils.stringToInt(freqs[i]);
                    }
                }
        return new ArrayList<>(Arrays.asList(mGpuFreqs));
    }

    public static boolean hasGpuFreqs() {
        if (GPU_AVAILABLE_FREQS == null) {
            for (String file : GPU_AVAILABLE_FREQS_ARRAY)
                if (Utils.existFile(file)) GPU_AVAILABLE_FREQS = file;
        }
        return GPU_AVAILABLE_FREQS != null;
    }

    public static int getGpuMaxFreq() {
        if (GPU_MAX_FREQ != null) if (Utils.existFile(GPU_MAX_FREQ)) {
            String value = Utils.readFile(GPU_MAX_FREQ);
            if (value != null) return Utils.stringToInt(value);
        }
        return 0;
    }

    public static boolean hasGpuMaxFreq() {
        if (GPU_MAX_FREQ == null) {
            for (String file : GPU_MAX_FREQ_ARRAY)
                if (Utils.existFile(file)) GPU_MAX_FREQ = file;
        }
        return GPU_MAX_FREQ != null;
    }

    public static int getGpuCurFreq() {
        if (GPU_CUR_FREQ != null) if (Utils.existFile(GPU_CUR_FREQ)) {
            String value = Utils.readFile(GPU_CUR_FREQ);
            if (value != null) return Utils.stringToInt(value);
        }
        return 0;
    }

    public static boolean hasGpuCurFreq() {
        if (GPU_CUR_FREQ == null) {
            for (String file : GPU_CUR_FREQ_ARRAY)
                if (Utils.existFile(file)) GPU_CUR_FREQ = file;
        }
        return GPU_CUR_FREQ != null;
    }

    public static boolean hasGpuControl() {
        for (String[] files : GPU_ARRAY)
            for (String file : files) if (Utils.existFile(file)) return true;
        return false;
    }

}
