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

package com.grarak.kerneladiutor.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 10.07.15.
 */
public class Entropy implements Constants {

    public static void setWrite(int value, Context context) {
        Control.runCommand(String.valueOf(value), PROC_RANDOM_ENTROPY_WRITE, Control.CommandType.GENERIC, context);
    }

    public static int getWrite() {
        return Utils.stringToInt(Utils.readFile(PROC_RANDOM_ENTROPY_WRITE));
    }

    public static void setRead(int value, Context context) {
        Control.runCommand(String.valueOf(value), PROC_RANDOM_ENTROPY_READ, Control.CommandType.GENERIC, context);
    }

    public static int getRead() {
        return Utils.stringToInt(Utils.readFile(PROC_RANDOM_ENTROPY_READ));
    }

    public static int getPoolsize() {
        return Utils.stringToInt(Utils.readFile(PROC_RANDOM_ENTROPY_POOLSIZE));
    }

    public static int getAvailable() {
        return Utils.stringToInt(Utils.readFile(PROC_RANDOM_ENTROPY_AVAILABLE));
    }

    public static boolean hasEntropy() {
        return Utils.existFile(PROC_RANDOM);
    }

}
