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
package com.smartpack.kernelmanager.utils.root;

/*
 * Originally created by willi on 30.12.15.
 * Modified by sunilpaulmathew <sunil.kde@gmail.com> on April 18, 2021
 */
public class RootFile {

    public static void delete(String mFile) {
        RootUtils.runCommand(("rm -r '" + mFile + "'"));
    }

    public static void execute(String mFile) {
        RootUtils.runCommand("sh " + mFile);
    }

    public static boolean exists(String mFile) {
        String output = RootUtils.runAndGetOutput("[ -e " + mFile + " ] && echo true");
        return !output.isEmpty() && output.equals("true");
    }

    public boolean isEmpty(String mFile) {
        return "false".equals(RootUtils.runAndGetOutput("find '" + mFile + "' -mindepth 1 | read || echo false"));
    }

    public static void mv(String mFile, String newPath) {
        RootUtils.runCommand("mv -f '" + mFile + "' '" + newPath + "'");
    }

    public static String read(String mFile) {
        return RootUtils.runAndGetOutput("cat '" + mFile + "'");
    }

    public static void write(String mFile, String text, boolean append) {
        String[] array = text.split("\\r?\\n");
        if (!append) delete(mFile);
        for (String line : array) {
            RootUtils.runCommand("echo '" + line + "' >> " + mFile);
        }
        RootUtils.chmod(mFile, "755");
    }

}