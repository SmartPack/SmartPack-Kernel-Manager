/*
 * Copyright (C) 2022-2023 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.kernelmanager.utils.kernel.screen;

import android.content.Context;

import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.Control;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 06, 2022
 */
public class RGB {

    private static final String MTK_RGB = "/sys/devices/platform/mtk_disp_mgr.0/rgb";

    public static boolean hasMTKRGBControl() {
        return Utils.existFile(MTK_RGB);
    }

    public static int getMTKRed() {
        return Utils.strToInt(getMTKRGB().split(" ")[0]);
    }

    public static int getMTKGreen() {
        return Utils.strToInt(getMTKRGB().split(" ")[1]);
    }

    public static int getMTKBlue() {
        return Utils.strToInt(getMTKRGB().split(" ")[2]);
    }

    public static String getMTKRGB() {
        return Utils.readFile(MTK_RGB);
    }

    public static void setMTKRed(int red, Context context) {
        String value = red + " " + getMTKGreen() + " " + getMTKBlue();
        run(Control.write(value, MTK_RGB), MTK_RGB, context);
    }

    public static void setMTKGreen(int green, Context context) {
        String value = getMTKRed() + " " + green + " " + getMTKBlue();
        run(Control.write(value, MTK_RGB), MTK_RGB, context);
    }

    public static void setMTKBlue(int blue, Context context) {
        String value = getMTKRed() + " " + getMTKGreen() + " " + blue;
        run(Control.write(value, MTK_RGB), MTK_RGB, context);
    }

    public static boolean supported() {
        return hasMTKRGBControl();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SCREEN, id, context);
    }

}