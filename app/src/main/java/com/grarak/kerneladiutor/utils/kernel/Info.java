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

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by willi on 22.12.14.
 */
public class Info implements Constants {

    public static String getMemInfo() {
        return Utils.readFile(PROC_MEMINFO);
    }

    public static String getCpuInfo() {
        return Utils.readFile(PROC_CPUINFO);
    }

    public static String getKernelVersion() {
        return Utils.readFile(PROC_VERSION);
    }

}
