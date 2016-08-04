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

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 31.07.16.
 */
public class LED {

    private static final String LED_RED_FADE = "/sys/class/leds/red/led_fade";
    private static final String LED_RED_INTENSITY = "/sys/class/leds/red/led_intensity";
    private static final String LED_RED_SPEED = "/sys/class/leds/red/led_speed";

    public static void setSpeed(int value, Context context) {
        run(Control.write(String.valueOf(value), LED_RED_SPEED), LED_RED_SPEED, context);
    }

    public static int getSpeed() {
        return Utils.strToInt(Utils.readFile(LED_RED_SPEED));
    }

    public static boolean hasSpeed() {
        return Utils.existFile(LED_RED_SPEED);
    }

    public static void setIntensity(int value, Context context) {
        run(Control.write(String.valueOf(value), LED_RED_INTENSITY), LED_RED_INTENSITY, context);
    }

    public static int getIntensity() {
        return Utils.strToInt(Utils.readFile(LED_RED_INTENSITY));
    }

    public static boolean hasIntensity() {
        return Utils.existFile(LED_RED_INTENSITY);
    }

    public static void enableFade(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", LED_RED_FADE), LED_RED_FADE, context);
    }

    public static boolean isFadeEnabled() {
        return Utils.readFile(LED_RED_FADE).equals("1");
    }

    public static boolean hasFade() {
        return Utils.existFile(LED_RED_FADE);
    }

    public static boolean supported() {
        return hasFade() || hasIntensity() || hasSpeed();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.LED, id, context);
    }

}
