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
package com.grarak.kerneladiutor.utils.kernel.led;

import android.content.Context;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 31.07.16.
 */
public class LED {

    private static final String RED_FADE = "/sys/class/leds/red/led_fade";
    private static final String RED_INTENSITY = "/sys/class/leds/red/led_intensity";
    private static final String RED_SPEED = "/sys/class/leds/red/led_speed";
    private static final String GREEN_RATE = "/sys/class/leds/green/rate";

    private static final LinkedHashMap<Integer, Boolean> sRedSpeed = new LinkedHashMap<>();
    private static final LinkedHashMap<Integer, Boolean> sGreenRate = new LinkedHashMap<>();

    private static final HashMap<String, LinkedHashMap<Integer, Boolean>> sSpeeds = new HashMap<>();

    static {
        sRedSpeed.put(R.string.stock, true);
        sRedSpeed.put(R.string.continuous_light, true);
        for (int i = 2; i < 21; i++) {
            sRedSpeed.put(i, false);
        }

        for (int i = 0; i < 4; i++) {
            sGreenRate.put(i, false);
        }

        sSpeeds.put(RED_SPEED, sRedSpeed);
        sSpeeds.put(GREEN_RATE, sGreenRate);
    }

    private static String SPEED;

    public static void setSpeed(int value, Context context) {
        run(Control.write(String.valueOf(value), SPEED), SPEED, context);
    }

    public static int getSpeed() {
        String value = Utils.readFile(SPEED);
        if (value.matches("\\d.+.(-)*")) {
            value = value.split("-")[0].trim();
        }
        return Utils.strToInt(value);
    }

    public static List<String> getSpeedMenu(Context context) {
        List<String> list = new ArrayList<>();
        for (int i : sSpeeds.get(SPEED).keySet()) {
            list.add(sSpeeds.get(SPEED).get(i) ? context.getString(i) : String.valueOf(i));
        }
        return list;
    }

    public static boolean hasSpeed() {
        if (SPEED != null) return true;
        for (String file : sSpeeds.keySet()) {
            if (Utils.existFile(file)) {
                SPEED = file;
                return true;
            }
        }
        return false;
    }

    public static void setIntensity(int value, Context context) {
        run(Control.write(String.valueOf(value), RED_INTENSITY), RED_INTENSITY, context);
    }

    public static int getIntensity() {
        String value = Utils.readFile(RED_INTENSITY);
        if (value.matches("\\d.+.(-)*")) {
            value = value.split("-")[0].trim();
        }
        return Utils.strToInt(value);
    }

    public static boolean hasIntensity() {
        return Utils.existFile(RED_INTENSITY);
    }

    public static void enableFade(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", RED_FADE), RED_FADE, context);
    }

    public static boolean isFadeEnabled() {
        return Utils.readFile(RED_FADE).startsWith("1");
    }

    public static boolean hasFade() {
        return Utils.existFile(RED_FADE);
    }

    public static boolean supported() {
        return hasFade() || hasIntensity() || hasSpeed() || Sec.supported();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.LED, id, context);
    }

}
