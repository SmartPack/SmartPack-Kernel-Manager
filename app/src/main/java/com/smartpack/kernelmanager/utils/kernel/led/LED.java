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
package com.smartpack.kernelmanager.utils.kernel.led;

import android.content.Context;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.Control;

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

    private static final String BACKLIGHT_MAX = "/sys/class/leds/lcd-backlight/max_brightness";
    private static final String BACKLIGHT_MIN = "/sys/module/mdss_fb/parameters/backlight_min";
    private static final String DRM_BACKLIGHT_MIN = "/sys/module/msm_drm/parameters/backlight_min";
    private static final String DRM_BACKLIGHT_MAX = "/sys/module/msm_drm/parameters/backlight_max";
    private static final String CHARGING_LIGHT = "/sys/class/leds/charging/max_brightness";
    private static final String CHARGING_LIGHT_2 = "/sys/class/sec/led/led_intensity";

    private static final String LED_FADE = "/sys/class/sec/led/led_fade";

    private static final String LED_BRIGHTNESS = "/sys/devices/soc.0/78b6000.i2c/i2c-0/0-0030/leds";
    private static final String LED_BRIGHTNESS_B = LED_BRIGHTNESS + "/B/max_brightness";
    private static final String LED_BRIGHTNESS_G = LED_BRIGHTNESS + "/G/max_brightness";
    private static final String LED_BRIGHTNESS_R = LED_BRIGHTNESS + "/R/max_brightness";
    private static final String LED_BRIGHTNESS_W = LED_BRIGHTNESS + "/W/max_brightness";

    private static final String WHITE_LED_BRIGHTNESS = "/sys/devices/soc/qpnp-flash-led-25/leds/led:torch_0/max_brightness";
    private static final String YELLOW_LED_BRIGHTNESS = "/sys/devices/soc/qpnp-flash-led-25/leds/led:torch_1/max_brightness";

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
    private String ENABLE_FILE;
    private String FADE_FILE;
    private String MAX_BACKLIGHT;
    private String MIN_BACKLIGHT;

    private LED() {
        for (String file : mSpeeds.keySet()) {
            if (Utils.existFile(file)) {
                SPEED = file;
                break;
            }
        }
        if (Utils.existFile(CHARGING_LIGHT)) {
            ENABLE_FILE = CHARGING_LIGHT;
        } else if (Utils.existFile(CHARGING_LIGHT_2)) {
            ENABLE_FILE = CHARGING_LIGHT_2;
        }
        if (Utils.existFile(LED_FADE)) {
            FADE_FILE = LED_FADE;
        } else if (Utils.existFile(RED_FADE)) {
            FADE_FILE = RED_FADE;
        }
        if (Utils.existFile(BACKLIGHT_MAX)) {
            MAX_BACKLIGHT = BACKLIGHT_MAX;
        } else if (Utils.existFile(DRM_BACKLIGHT_MAX)) {
            MAX_BACKLIGHT = DRM_BACKLIGHT_MAX;
        }
        if (Utils.existFile(BACKLIGHT_MIN)) {
            MIN_BACKLIGHT = BACKLIGHT_MIN;
        } else if (Utils.existFile(DRM_BACKLIGHT_MIN)) {
            MIN_BACKLIGHT = DRM_BACKLIGHT_MIN;
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
        run(Control.write(enable ? "1" : "0", FADE_FILE), FADE_FILE, context);
    }

    public boolean isFadeEnabled() {
        return Utils.readFile(FADE_FILE).startsWith("1");
    }

    public boolean hasFade() {
        return FADE_FILE != null;
    }

    public void setBacklightMax(String value, Context context) {
        run(Control.write(String.valueOf(value), MAX_BACKLIGHT), MAX_BACKLIGHT, context);
    }

    public String getBacklightMax() {
        return Utils.readFile(MAX_BACKLIGHT);
    }

    public boolean hasBacklightMax() {
        return MAX_BACKLIGHT != null;
    }

    public void setBacklightMin(String value, Context context) {
        run(Control.write(String.valueOf(value), MIN_BACKLIGHT), MIN_BACKLIGHT, context);
    }

    public String getBacklightMin() {
        return Utils.readFile(MIN_BACKLIGHT);
    }

    public boolean hasBacklightMin() {
        return MIN_BACKLIGHT != null;
    }

    public void setcharginglight(int value, Context context) {
        run(Control.write(String.valueOf(value), ENABLE_FILE), ENABLE_FILE, context);
    }

    public int getcharginglight() {
        String value = Utils.readFile(ENABLE_FILE);
        if (value.matches("\\d.+.(-)*")) {
            value = value.split("-")[0].trim();
        }
        return Utils.strToInt(value);
    }

    public boolean hascharginglight() {
       return ENABLE_FILE != null;
    }

    public void setLEDBrightnessB(int value, Context context) {
        run(Control.write(String.valueOf(value), LED_BRIGHTNESS_B), LED_BRIGHTNESS_B, context);
    }

    public static int getLEDBrightnessB() {
        return Utils.strToInt(Utils.readFile(LED_BRIGHTNESS_B));
    }

    public static boolean hasLEDBrightnessB() {
       return Utils.existFile(LED_BRIGHTNESS_B);
    }

    public void setLEDBrightnessG(int value, Context context) {
        run(Control.write(String.valueOf(value), LED_BRIGHTNESS_G), LED_BRIGHTNESS_G, context);
    }

    public static int getLEDBrightnessG() {
        return Utils.strToInt(Utils.readFile(LED_BRIGHTNESS_G));
    }

    public static boolean hasLEDBrightnessG() {
       return Utils.existFile(LED_BRIGHTNESS_G);
    }

    public void setLEDBrightnessR(int value, Context context) {
        run(Control.write(String.valueOf(value), LED_BRIGHTNESS_R), LED_BRIGHTNESS_R, context);
    }

    public static int getLEDBrightnessR() {
        return Utils.strToInt(Utils.readFile(LED_BRIGHTNESS_R));
    }

    public static boolean hasLEDBrightnessR() {
       return Utils.existFile(LED_BRIGHTNESS_R);
    }

    public void setLEDBrightnessW(int value, Context context) {
        run(Control.write(String.valueOf(value), LED_BRIGHTNESS_W), LED_BRIGHTNESS_W, context);
    }

    public static int getLEDBrightnessW() {
        return Utils.strToInt(Utils.readFile(LED_BRIGHTNESS_W));
    }

    public static boolean hasLEDBrightnessW() {
       return Utils.existFile(LED_BRIGHTNESS_W);
    }

    public void setWhiteLED(int value, Context context) {
        run(Control.write(String.valueOf(value), WHITE_LED_BRIGHTNESS), WHITE_LED_BRIGHTNESS, context);
    }

    public static int getWhiteLED() {
        return Utils.strToInt(Utils.readFile(WHITE_LED_BRIGHTNESS));
    }

    public static boolean hasWhiteLED() {
        return Utils.existFile(WHITE_LED_BRIGHTNESS);
    }

    public void setYellowLED(int value, Context context) {
        run(Control.write(String.valueOf(value), YELLOW_LED_BRIGHTNESS), YELLOW_LED_BRIGHTNESS, context);
    }

    public static int getYellowLED() {
        return Utils.strToInt(Utils.readFile(YELLOW_LED_BRIGHTNESS));
    }

    public static boolean hasYellowLED() {
        return Utils.existFile(YELLOW_LED_BRIGHTNESS);
    }

    public boolean supported() {
        return hasFade() || hasBacklightMax() || hasBacklightMin() || hascharginglight()
                || hasLEDBrightnessB() || hasLEDBrightnessG() || hasLEDBrightnessR()
                || hasLEDBrightnessW() || hasIntensity() || hasSpeed() || Sec.supported()
                || hasWhiteLED() || hasYellowLED();
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.LED, id, context);
    }

}
