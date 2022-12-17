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

import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.topjohnwu.superuser.io.SuFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 18, 2019
 * Largely based on the Initd.java from https://github.com/Grarak/KernelAdiutor
 * Ref: https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/grarak/kerneladiutor/utils/tools/Initd.java
 */

public class Scripts {

    public static String mScriptName = null;

    public static List<String> mOutput = null;

    public static boolean mApplying;

    public static File scriptFile() {
        return SuFile.open(Utils.getInternalDataStorage(), "/scripts");
    }

    public static void write(String file, String text) {
        if (scriptFile().exists() && scriptFile().isFile()) {
            scriptFile().delete();
        }
        scriptFile().mkdirs();
        Utils.create(text, SuFile.open(scriptFile(), file).getAbsolutePath());
    }

    public static void importScript(String string) {
        if (scriptFile().exists() && scriptFile().isFile()) {
            scriptFile().delete();
        }
        scriptFile().mkdirs();
        Utils.create(Utils.readFile(string), SuFile.open(scriptFile().getAbsolutePath(),
                SuFile.open(string).getName()).getAbsolutePath());
    }

    public static void delete(String file) {
        Utils.delete(SuFile.open(scriptFile(), file).getAbsolutePath());
    }

    public static void execute(String file) {
        RootUtils.runAndGetLiveOutput("sh " + SuFile.open(scriptFile(), file).getAbsolutePath(), mOutput);
    }

    public static String read(String file) {
        return Utils.readFile(SuFile.open(scriptFile(), file).getAbsolutePath());
    }

    public static String scriptExistsCheck(String file) {
        return SuFile.open(scriptFile(), file).getAbsolutePath();
    }

    public static List<String> list() {
        List<String> mList = new ArrayList<>();
        if (scriptFile().exists()) {
            for (File file : Objects.requireNonNull(scriptFile().listFiles())) {
                mList.add(file.getName());
            }
        }
        return mList;
    }

}
