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

    private static LED sInstance;

    public static LED getInstance() {
        if (sInstance == null) {
            sInstance = new LED();
        }
        return sInstance;
    }

    private static final String RED_FADE = "/sys/class/leds/red/led_fade";
    private static final String RED_INTENSITY = "/sys/class/leds/red/led_intensity";
    private static final String RED_SPEED = "/sys/class/leds/red/led_speed";
    private static final String GREEN_RATE = "/sys/class/leds/green/rate";

    private static final String DISPLAY_BACKLIGHT = "/sys/class/leds/lcd-backlight/max_brightness";
    private static final String BACKLIGHT_MIN = "/sys/module/mdss_fb/parameters/backlight_min";
    private static final String CHARGING_LIGHT = "/sys/class/leds/charging/max_brightness";

    private static final String LED_FADE = "/sys/class/sec/led/led_fade";

    private final LinkedHashMap<Integer, Boolean> mRedSpeed = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, Boolean> mGreenRate = new LinkedHashMap<>();

    private final HashMap<String, LinkedHashMap<Integer, Boolean>> mSpeeds = new HashMap<>();

    {
        mRedSpeed.put(R.string.stock, true);
        mRedSpeed.put(R.string.continuous_light, true);
        for (int i = 2; i < 21; i++) {
            mRedSpeed.put(i, false);
        }

        for (int i = 0; i < 4; i++) {
            mGreenRate.put(i, false);
        }

        mSpeeds.put(RED_SPEED, mRedSpeed);
        mSpeeds.put(GREEN_RATE, mGreenRate);
    }

    private String SPEED;

    private LED() {
        for (String file : mSpeeds.keySet()) {
            if (Utils.existFile(file)) {
                SPEED = file;
                break;
            }
        }
    }

    public void setSpeed(int value, Context context) {
        run(Control.write(String.valueOf(value), SPEED), SPEED, context);
    }

    public int getSpeed() {
        String value = Utils.readFile(SPEED);
        if (value.matches("\\d.+.(-)*")) {
            value = value.split("-")[0].trim();
        }
        return Utils.strToInt(value);
    }

    public List<String> getSpeedMenu(Context context) {
        List<String> list = new ArrayList<>();
        for (int i : mSpeeds.get(SPEED).keySet()) {
            list.add(mSpeeds.get(SPEED).get(i) ? context.getString(i) : String.valueOf(i));
        }
        return list;
    }

    public boolean hasSpeed() {
        return SPEED != null;
    }

    public void setIntensity(int value, Context context) {
        run(Control.write(String.valueOf(value), RED_INTENSITY), RED_INTENSITY, context);
    }

    public int getIntensity() {
        String value = Utils.readFile(RED_INTENSITY);
        if (value.matches("\\d.+.(-)*")) {
            value = value.split("-")[0].trim();
        }
        return Utils.strToInt(value);
    }

    public boolean hasIntensity() {
        return Utils.existFile(RED_INTENSITY);
    }

    public void enableFade(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", RED_FADE), RED_FADE, context);
    }

    public boolean isFadeEnabled() {
        return Utils.readFile(RED_FADE).startsWith("1");
    }

    public boolean hasFade() {
        return Utils.existFile(RED_FADE);
    }

    public void enableLEDFade(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", LED_FADE), LED_FADE, context);
    }

    public boolean isLEDFadeEnabled() {
        return Utils.readFile(LED_FADE).startsWith("1 - LED fading is enabled");
    }

    public boolean hasLEDFade() {
        return Utils.existFile(LED_FADE);
    }

    public void setdisplaybacklight(int value, Context context) {
        run(Control.write(String.valueOf(value), DISPLAY_BACKLIGHT), DISPLAY_BACKLIGHT, context);
    }

    public static int getdisplaybacklight() {
        return Utils.strToInt(Utils.readFile(DISPLAY_BACKLIGHT));
    }

    public static boolean hasdisplaybacklight() {
        return Utils.existFile(DISPLAY_BACKLIGHT);
    }

    public void setBacklightMin(int value, Context context) {
        run(Control.write(String.valueOf(value), BACKLIGHT_MIN), BACKLIGHT_MIN, context);
    }

    public static int getBacklightMin() {
        return Utils.strToInt(Utils.readFile(BACKLIGHT_MIN));
    }

    public static boolean hasBacklightMin() {
        return Utils.existFile(BACKLIGHT_MIN);
    }

    public void setcharginglight(int value, Context context) {
        run(Control.write(String.valueOf(value), CHARGING_LIGHT), CHARGING_LIGHT, context);
    }

    public static int getcharginglight() {
        return Utils.strToInt(Utils.readFile(CHARGING_LIGHT));
    }

    public boolean hascharginglight() {
       return Utils.existFile(CHARGING_LIGHT);
    }

    public boolean supported() {
        return hasFade() || hasLEDFade() || hasdisplaybacklight() || hasBacklightMin() || hascharginglight() || hasIntensity() || hasSpeed() || Sec.supported();
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.LED, id, context);
    }

}
