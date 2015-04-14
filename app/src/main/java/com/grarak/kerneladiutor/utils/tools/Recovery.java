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

import java.io.File;

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

    public String getCommand(RECOVERY recovery) {
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

        public abstract String getWipeData();

        public abstract String getWipeCache();

        public abstract String getFlashZip(File file);

    }

    private class CWM extends RecoveryType {

        @Override
        public String getWipeData() {
            return null;
        }

        @Override
        public String getWipeCache() {
            return null;
        }

        @Override
        public String getFlashZip(File file) {
            return null;
        }

    }

    private class TWRP extends RecoveryType {

        @Override
        public String getWipeData() {
            return null;
        }

        @Override
        public String getWipeCache() {
            return null;
        }

        @Override
        public String getFlashZip(File file) {
            return null;
        }

    }

}
