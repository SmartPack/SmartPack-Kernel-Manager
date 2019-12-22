/*
 * Copyright (C) 2019-2020 sunilpaulmathew <sunil.kde@gmail.com>
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
 */

package com.smartpack.kernelmanager.utils;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.io.File;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 15, 2019
 */

public class CustomControls {

    public static File switchFile() {
        return new File(Utils.getInternalDataStorage() + "/controls/switch");
    }

    public static File genericFile() {
        return new File(Utils.getInternalDataStorage() + "/controls/generic");
    }

    public static boolean hasCustomControl(String string) {
        return Utils.existFile(string);
    }

    public static String getGenericValue(String string) {
        return Utils.readFile(string);
    }

    public static void setGenericValue(String value, String string, Context context) {
        run(Control.write(String.valueOf(value), string), string, context);
    }

    public static void enableSwitch(boolean enable, String string, Context context) {
        run(Control.write(enable ? "1" : "0", string), string, context);
    }

    public static boolean isSwitchEnabled(String string) {
        return Utils.readFile(string).startsWith("1")
                || Utils.readFile(string).startsWith("Y");
    }

    public static String getItemPath(String string) {
        return Utils.readFile(string);
    }

    public static void exportPath(String path, String folder) {
        RootUtils.runCommand("echo " + path + " > " + folder + "/" + path.replaceFirst("/", "").
                replace("/", "-"));
    }

    public static void delete(String string) {
        File file = new File(string);
        file.delete();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CUSTOMCONTROL, id, context);
    }

}