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

import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootFile;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.io.File;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 18, 2019
 * Largely based on the Initd.java from https://github.com/Grarak/KernelAdiutor
 * Ref: https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/grarak/kerneladiutor/utils/tools/Initd.java
 */

public class ScriptManager {

    private static final String SCRIPT = Utils.getInternalDataStorage() + "/scripts";

    public static void write(String file, String text) {
        RootFile f = new RootFile(SCRIPT + "/" + file);
        f.write(text, false);
    }

    public static void importScript(String string) {
        File file = new File(SCRIPT);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
        RootUtils.runCommand("cp " + string + " " + SCRIPT);
    }

    public static void delete(String file) {
        RootFile f = new RootFile(SCRIPT + "/" + file);
        f.delete();
    }

    public static String execute(String file) {
        return RootUtils.runCommand("sh " + SCRIPT + "/" + file);
    }

    public static String read(String file) {
        return Utils.readFile(SCRIPT + "/" + file);
    }

    public static String scriptExistsCheck(String file) {
        return SCRIPT + "/" + file;
    }

    public static List<String> list() {
        RootFile file = new RootFile(SCRIPT);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.list();
    }

}
