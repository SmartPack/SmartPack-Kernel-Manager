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
 * Created by willi on 03.05.15.
 */
public class Thermal implements Constants {

    public static void activateThermald(boolean active, Context context) {
        if (active) Control.startService(THERMALD, true, context);
        else Control.stopService(THERMALD, true, context);
    }

    public static boolean isThermaldActive() {
        return Utils.isPropActive(THERMALD);
    }

    public static boolean hasThermald() {
        return Utils.hasProp(THERMALD);
    }

    public static boolean hasThermal() {
        if (hasThermald()) return true;
        return false;
    }

}
