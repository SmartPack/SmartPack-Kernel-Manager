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
import android.util.Log;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by willi on 21.04.16.
 */
public class Temperature {

    private static final HashMap<String, Integer> sCPUTemps = new HashMap<>();

    private static final String THERMAL_ZONE0 = "/sys/class/thermal/thermal_zone0/temp";

    static {
        sCPUTemps.put("/sys/devices/platform/omap/omap_temp_sensor.0/temperature", 1000);
        sCPUTemps.put("/proc/mtktscpu/mtktscpu_temperature", 1000);
    }

    private static TempJson TEMP_JSON;

    private static String CPU_NODE;
    private static int CPU_OFFSET;

    private static String GPU_NODE;
    private static int GPU_OFFSET;

    public static String getGPU(Context context) {
        double temp = getGPUTemp();
        boolean useFahrenheit = Utils.useFahrenheit(context);
        if (useFahrenheit) temp = Utils.celsiusToFahrenheit(temp);
        return Utils.roundTo2Decimals(temp) + context.getString(useFahrenheit ? R.string.fahrenheit
                : R.string.celsius);
    }

    private static double getGPUTemp() {
        return (double) Utils.strToInt(Utils.readFile(GPU_NODE)) / GPU_OFFSET;
    }

    public static boolean hasGPU() {
        if (TEMP_JSON != null && TEMP_JSON.getGPU() != null) {
            GPU_NODE = TEMP_JSON.getGPU();
            if (Utils.existFile(GPU_NODE)) {
                GPU_OFFSET = TEMP_JSON.getGPUOffset();
                if (GPU_OFFSET != 1 && Utils.readFile(GPU_NODE).length() == 2) {
                    GPU_OFFSET = 1;
                }
                return true;
            }
            GPU_NODE = null;
        }
        return false;
    }

    public static String getCPU(Context context) {
        double temp = getCPUTemp();
        boolean useFahrenheit = Utils.useFahrenheit(context);
        if (useFahrenheit) temp = Utils.celsiusToFahrenheit(temp);
        return Utils.roundTo2Decimals(temp) + context.getString(useFahrenheit ? R.string.fahrenheit
                : R.string.celsius);
    }

    private static double getCPUTemp() {
        return (double) Utils.strToInt(Utils.readFile(CPU_NODE)) / CPU_OFFSET;
    }

    public static boolean hasCPU() {
        if (TEMP_JSON != null && TEMP_JSON.getCPU() != null) {
            CPU_NODE = TEMP_JSON.getCPU();
            if (Utils.existFile(CPU_NODE)) {
                CPU_OFFSET = TEMP_JSON.getCPUOffset();
                if (Utils.readFile(CPU_NODE).length() == 2) {
                    CPU_OFFSET = 1;
                }
                return true;
            }
            return false;
        }
        for (String node : sCPUTemps.keySet()) {
            if (Utils.existFile(node)) {
                CPU_NODE = node;
                CPU_OFFSET = sCPUTemps.get(CPU_NODE);
                return true;
            }
        }
        if (CPU_NODE == null && Utils.existFile(THERMAL_ZONE0)) {
            CPU_NODE = THERMAL_ZONE0;
            CPU_OFFSET = 1000;
        }
        return CPU_NODE != null;
    }

    public static boolean supported(Context context) {
        if (TEMP_JSON == null) {
            TEMP_JSON = new TempJson(context);
            if (!TEMP_JSON.supported()) {
                TEMP_JSON = null;
            }
        }
        return hasCPU() || hasGPU();
    }

    private static class TempJson {

        private static final String TAG = TempJson.class.getSimpleName();

        private JSONObject mDeviceJson;

        private TempJson(Context context) {
            try {
                JSONArray tempArray = new JSONArray(Utils.readAssetFile(context, "temp.json"));
                for (int i = 0; i < tempArray.length(); i++) {
                    JSONObject device = tempArray.getJSONObject(i);
                    if (Device.getBoard().equalsIgnoreCase(device.getString("board"))) {
                        mDeviceJson = device;
                        break;
                    }
                }
            } catch (JSONException ignored) {
                Log.e(TAG, "Can't read temp.json");
            }
        }

        private int getGPUOffset() {
            try {
                return mDeviceJson.getInt("gpu-offset");
            } catch (JSONException ignored) {
                return 1;
            }
        }

        private int getCPUOffset() {
            try {
                return mDeviceJson.getInt("cpu-offset");
            } catch (JSONException ignored) {
                return 1;
            }
        }

        public String getGPU() {
            try {
                return mDeviceJson.getString("gpu");
            } catch (JSONException ignored) {
                return null;
            }
        }

        public String getCPU() {
            try {
                return mDeviceJson.getString("cpu");
            } catch (JSONException ignored) {
                return null;
            }
        }

        public boolean supported() {
            return mDeviceJson != null;
        }

    }

}
