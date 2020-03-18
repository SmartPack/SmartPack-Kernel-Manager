/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
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

package com.smartpack.kernelmanager.utils.tools;

import android.content.Context;

import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.Control;
import com.smartpack.kernelmanager.utils.root.RootFile;

import java.io.File;
import java.util.List;

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
        String value = Utils.readFile(string);
        if (value.startsWith("HBM mode = 1") || value.startsWith("mode = 1")
                || value.startsWith("Boeffla sound status: 1") || value.startsWith("1")
                || value.equals("Y") || value.startsWith("Y")) {
            value = "1";
        } else if (value.startsWith("HBM mode = 0") || value.startsWith("mode = 0")
                || value.startsWith("Boeffla sound status: 0") || value.startsWith("0")
                || value.equals("N") || value.startsWith("N")) {
            value = "0";
        }
        return value.equals("1");
    }

    public static void exportPath(String path, String folder) {
        Utils.create(path, folder + "/" + path.replaceFirst("/", "").
                replace("/", "-"));
    }

    public static void delete(String string) {
        File file = new File(string);
        file.delete();
    }

    public static List<String> switchList() {
        RootFile file = new RootFile(switchFile().toString());
        if (!file.exists()) {
            file.mkdir();
        }
        return file.list();
    }

    public static List<String> genericList() {
        RootFile file = new RootFile(genericFile().toString());
        if (!file.exists()) {
            file.mkdir();
        }
        return file.list();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CUSTOMCONTROL, id, context);
    }

}