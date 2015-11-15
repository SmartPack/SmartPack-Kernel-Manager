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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 27.12.14.
 */
public class LMK implements Constants {

    public static void setMinFree(String minFree, Context context) {
        Control.runCommand(minFree, LMK_MINFREE, Control.CommandType.GENERIC, context);
    }

    public static int getMinFree(List<String> minfrees, int position) {
        return Utils.stringToInt(minfrees.get(position));
    }

    public static List<String> getMinFrees() {
        String value = Utils.readFile(LMK_MINFREE);
        if (value != null) return new ArrayList<>(Arrays.asList(value.split(",")));
        return null;
    }

    public static boolean hasAdaptive() {
        return Utils.existFile(LMK_ADAPTIVE);
    }

    public static void setAdaptive(boolean enable, Context context) {
        Control.runCommand(enable ? "1" : "0", LMK_ADAPTIVE, Control.CommandType.GENERIC, context);
    }

    public static boolean getAdaptive() {
        return Utils.readFile(LMK_ADAPTIVE).equals("1");
    }
}
