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

    private static String GPU_3D_CUR_FREQ;
    private static String GPU_3D_MAX_FREQ;
    private static String GPU_3D_AVAILABLE_FREQS;
    private static String GPU_3D_SCALING_GOVERNOR;
    private static String[] GPU_3D_AVAILABLE_GOVERNORS;

    private static Integer[] mGpu3dFreqs;

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
                            mGpu2dFreqs[i] = Integer.parseInt(freqs[i]);
                    }
                }
        return new ArrayList<>(Arrays.asList(mGpu2dFreqs));
    }

    public static boolean hasGpu2dFreqs() {
        if (GPU_2D_AVAILABLE_FREQS == null) {
            for (String file : GPU_2D_AVAILABLE_FREQS_ARRAY)
                if (Utils.existFile(file)) GPU_2D_AVAILABLE_FREQS = file;
        }
        return GPU_2D_AVAILABLE_FREQS != null;
    }

    public static int getGpu2dMaxFreq() {
        if (GPU_2D_MAX_FREQ != null) if (Utils.existFile(GPU_2D_MAX_FREQ)) {
            String value = Utils.readFile(GPU_2D_MAX_FREQ);
            if (value != null) return Integer.parseInt(value);
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
            if (value != null) return Integer.parseInt(value);
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

    public static void setGpu3dGovernor(String governor, Context context) {
        if (GPU_3D_SCALING_GOVERNOR != null)
            Control.runCommand(governor, GPU_3D_SCALING_GOVERNOR, Control.CommandType.GENERIC, context);
    }

    public static List<String> getGpu3dGovernors() {
        if (GPU_3D_AVAILABLE_GOVERNORS == null)
            for (String file : GPU_3D_AVAILABLE_GOVERNORS_ARRAY)
                if (GPU_3D_AVAILABLE_GOVERNORS == null)
                    if (Utils.existFile(file)) {
                        String value = Utils.readFile(file);
                        if (value != null)
                            GPU_3D_AVAILABLE_GOVERNORS = value.split(" ");
                    }
        return new ArrayList<>(Arrays.asList(GPU_3D_AVAILABLE_GOVERNORS == null ? GPU_GENERIC_GOVERNORS
                .split(" ") : GPU_3D_AVAILABLE_GOVERNORS));
    }

    public static String getGpu3dGovernor() {
        if (GPU_3D_SCALING_GOVERNOR != null)
            if (Utils.existFile(GPU_3D_SCALING_GOVERNOR)) {
                String value = Utils.readFile(GPU_3D_SCALING_GOVERNOR);
                if (value != null) return value;
            }
        return "";
    }

    public static boolean hasGpu3dGovernor() {
        if (GPU_3D_SCALING_GOVERNOR == null)
            for (String file : GPU_3D_SCALING_GOVERNOR_ARRAY)
                if (Utils.existFile(file)) GPU_3D_SCALING_GOVERNOR = file;
        return GPU_3D_SCALING_GOVERNOR != null;
    }

    public static void setGpu3dMaxFreq(int freq, Context context) {
        if (GPU_3D_MAX_FREQ != null)
            Control.runCommand(String.valueOf(freq), GPU_3D_MAX_FREQ, Control.CommandType.GENERIC, context);
    }

    public static List<Integer> getGpu3dFreqs() {
        if (GPU_3D_AVAILABLE_FREQS != null)
            if (mGpu3dFreqs == null)
                if (Utils.existFile(GPU_3D_AVAILABLE_FREQS)) {
                    String value = Utils.readFile(GPU_3D_AVAILABLE_FREQS);
                    if (value != null) {
                        String[] freqs = value.split(" ");
                        mGpu3dFreqs = new Integer[freqs.length];
                        for (int i = 0; i < mGpu3dFreqs.length; i++)
                            mGpu3dFreqs[i] = Integer.parseInt(freqs[i]);
                    }
                }
        return new ArrayList<>(Arrays.asList(mGpu3dFreqs));
    }

    public static boolean hasGpu3dFreqs() {
        if (GPU_3D_AVAILABLE_FREQS == null) {
            for (String file : GPU_3D_AVAILABLE_FREQS_ARRAY)
                if (Utils.existFile(file)) GPU_3D_AVAILABLE_FREQS = file;
        }
        return GPU_3D_AVAILABLE_FREQS != null;
    }

    public static int getGpu3dMaxFreq() {
        if (GPU_3D_MAX_FREQ != null) if (Utils.existFile(GPU_3D_MAX_FREQ)) {
            String value = Utils.readFile(GPU_3D_MAX_FREQ);
            if (value != null) return Integer.parseInt(value);
        }
        return 0;
    }

    public static boolean hasGpu3dMaxFreq() {
        if (GPU_3D_MAX_FREQ == null) {
            for (String file : GPU_3D_MAX_FREQ_ARRAY)
                if (Utils.existFile(file)) GPU_3D_MAX_FREQ = file;
        }
        return GPU_3D_MAX_FREQ != null;
    }

    public static int getGpu3dCurFreq() {
        if (GPU_3D_CUR_FREQ != null) if (Utils.existFile(GPU_3D_CUR_FREQ)) {
            String value = Utils.readFile(GPU_3D_CUR_FREQ);
            if (value != null) return Integer.parseInt(value);
        }
        return 0;
    }

    public static boolean hasGpu3dCurFreq() {
        if (GPU_3D_CUR_FREQ == null) {
            for (String file : GPU_3D_CUR_FREQ_ARRAY)
                if (Utils.existFile(file)) GPU_3D_CUR_FREQ = file;
        }
        return GPU_3D_CUR_FREQ != null;
    }

    public static boolean hasGpuControl() {
        for (String[] files : GPU_ARRAY)
            for (String file : files) if (Utils.existFile(file)) return true;
        return false;
    }

}
