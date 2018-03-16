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
package com.grarak.kerneladiutor.utils.tools;

import android.os.Environment;

import com.grarak.kerneladiutor.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 08.07.16.
 */
public class Recovery {

    public enum RECOVERY {
        CWM, TWRP
    }

    public enum RECOVERY_COMMAND {
        WIPE_DATA, WIPE_CACHE, FLASH_ZIP
    }

    private final RECOVERY_COMMAND mRecovery_command;
    private final File mFile;

    public Recovery(RECOVERY_COMMAND recovery_command) {
        this(recovery_command, null);
    }

    public Recovery(RECOVERY_COMMAND recovery_command, File file) {
        mRecovery_command = recovery_command;
        mFile = file;
    }

    public String getFile(RECOVERY recovery) {
        return recovery == RECOVERY.TWRP ? "openrecoveryscript" : "extendedcommand";
    }

    public List<String> getCommands(RECOVERY recovery) {
        RecoveryType recoveryType = recovery == RECOVERY.TWRP ? new TWRP() : new CWM();
        switch (mRecovery_command) {
            case WIPE_DATA:
                return recoveryType.getWipeData();
            case WIPE_CACHE:
                return recoveryType.getWipeCache();
            case FLASH_ZIP:
                return recoveryType.getFlashZip(mFile);
        }
        return null;
    }

    private abstract class RecoveryType {

        public abstract List<String> getWipeData();

        public abstract List<String> getWipeCache();

        public abstract List<String> getFlashZip(File file);

        public abstract String getExternalPath();

        public String formatFile(File file) {
            String zip = file.getAbsolutePath();

            String internalStorage = Environment.getExternalStorageDirectory().toString();
            if (zip.startsWith(internalStorage + "/")) {
                return zip.replace(internalStorage + "/", "/sdcard/");
            }

            String externalStorage = Utils.getExternalStorage();
            if (externalStorage != null && zip.startsWith(externalStorage + "/")) {
                return zip.replace(externalStorage + "/", getExternalPath() + "/");
            }

            return zip;
        }

    }

    private class CWM extends RecoveryType {

        @Override
        public List<String> getWipeData() {
            List<String> commands = new ArrayList<>();

            commands.add("format(\"/data\");");
            commands.add("format(\"/sdcard/.android_secure\");");

            return commands;
        }

        @Override
        public List<String> getWipeCache() {
            List<String> commands = new ArrayList<>();

            commands.add("format(\"/cache\");");
            commands.add("format(\"/data/dalvik-cache\");");
            commands.add("format(\"/cache/dalvik-cache\");");
            commands.add("format(\"/sd-ext/dalvik-cache\");");

            return commands;
        }

        @Override
        public List<String> getFlashZip(File file) {
            List<String> commands = new ArrayList<>();

            commands.add("assert(install_zip(\"" + formatFile(file) + "\"));");

            return commands;
        }

        @Override
        public String getExternalPath() {
            return "/storage/sdcard1";
        }
    }

    private class TWRP extends RecoveryType {

        @Override
        public List<String> getWipeData() {
            List<String> commands = new ArrayList<>();
            commands.add("wipe data");
            return commands;
        }

        @Override
        public List<String> getWipeCache() {
            List<String> commands = new ArrayList<>();

            commands.add("wipe cache");
            commands.add("wipe dalvik");

            return commands;
        }

        @Override
        public List<String> getFlashZip(File file) {
            List<String> commands = new ArrayList<>();

            commands.add("set tw_signed_zip_verify 0");
            commands.add("install " + formatFile(file));

            return commands;
        }

        @Override
        public String getExternalPath() {
            return "/external_sd";
        }
    }

}
