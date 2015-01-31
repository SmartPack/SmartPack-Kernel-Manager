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
 * Created by willi on 26.12.14.
 */
public class IO implements Constants {

    public enum StorageType {
        INTERNAL, EXTERNAL
    }

    public static void setReadahead(StorageType type, int readahead, Context context) {
        Control.runCommand(String.valueOf(readahead), type == StorageType.INTERNAL ? IO_INTERNAL_READ_AHEAD :
                IO_EXTERNAL_READ_AHEAD, Control.CommandType.GENERIC, context);
    }

    public static int getReadahead(StorageType type) {
        String file = type == StorageType.INTERNAL ? IO_INTERNAL_READ_AHEAD
                : IO_EXTERNAL_READ_AHEAD;
        if (Utils.existFile(file)) {
            String values = Utils.readFile(file);
            if (values != null) return Utils.stringToInt(values);
        }
        return 0;
    }

    public static void setScheduler(StorageType type, String scheduler, Context context) {
        Control.runCommand(scheduler, type == StorageType.INTERNAL ? IO_INTERNAL_SCHEDULER :
                IO_EXTERNAL_SCHEDULER, Control.CommandType.GENERIC, context);
    }

    public static List<String> getSchedulers(StorageType type) {
        String file = type == StorageType.INTERNAL ? IO_INTERNAL_SCHEDULER
                : IO_EXTERNAL_SCHEDULER;
        if (Utils.existFile(file)) {
            String values = Utils.readFile(file);
            if (values != null) {
                String[] valueArray = values.split(" ");
                String[] out = new String[valueArray.length];

                for (int i = 0; i < valueArray.length; i++)
                    out[i] = valueArray[i].replace("[", "").replace("]", "");

                return new ArrayList<>(Arrays.asList(out));
            }
        }
        return null;
    }

    public static String getScheduler(StorageType type) {
        String file = type == StorageType.INTERNAL ? IO_INTERNAL_SCHEDULER
                : IO_EXTERNAL_SCHEDULER;
        if (Utils.existFile(file)) {
            String values = Utils.readFile(file);
            if (values != null) {
                String[] valueArray = values.split(" ");

                for (String value : valueArray)
                    if (value.contains("["))
                        return value.replace("[", "").replace("]", "");
            }
        }
        return "";
    }

    public static boolean hasExternalStorage() {
        return Utils.existFile(IO_EXTERNAL_READ_AHEAD)
                || Utils.existFile(IO_EXTERNAL_SCHEDULER);
    }

}
