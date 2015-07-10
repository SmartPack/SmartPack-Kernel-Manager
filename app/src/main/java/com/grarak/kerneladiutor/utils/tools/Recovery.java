/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.utils.tools;

import android.os.Environment;

import com.grarak.kerneladiutor.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 13.04.15.
 */
public class Recovery {

    public enum RECOVERY {
        CWM, TWRP
    }

    public enum RECOVERY_COMMAND {
        WIPE_DATA, WIPE_CACHE, FLASH_ZIP
    }

    private final RECOVERY_COMMAND recovery_command;
    private final File file;

    public Recovery(RECOVERY_COMMAND recovery_command) {
        this(recovery_command, null);
    }

    public Recovery(RECOVERY_COMMAND recovery_command, File file) {
        this.recovery_command = recovery_command;
        this.file = file;
    }

    public String getFile(RECOVERY recovery) {
        return recovery == RECOVERY.TWRP ? "openrecoveryscript" : "extendedcommand";
    }

    public List<String> getCommands(RECOVERY recovery) {
        RecoveryType recoveryType = recovery == RECOVERY.TWRP ? new TWRP() : new CWM();
        switch (recovery_command) {
            case WIPE_DATA:
                return recoveryType.getWipeData();
            case WIPE_CACHE:
                return recoveryType.getWipeCache();
            case FLASH_ZIP:
                return recoveryType.getFlashZip(file);
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
            if (zip.startsWith(internalStorage + "/"))
                return zip.replace(internalStorage + "/", Utils.getInternalStorage() + "/");

            String externalStorage = Utils.getExternalStorage();
            if (externalStorage != null && zip.startsWith(externalStorage + "/"))
                return zip.replace(externalStorage + "/", getExternalPath() + "/");

            return zip;
        }

    }

    private class CWM extends RecoveryType {

        @Override
        public List<String> getWipeData() {
            List<String> commands = new ArrayList<>();

            commands.add("format(\"/data\");");
            commands.add("format(\"" + Utils.getInternalStorage() + "/.android_secure\");");

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
