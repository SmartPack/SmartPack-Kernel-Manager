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
package com.grarak.kerneladiutor.utils.tools.customcontrols;

import com.grarak.kerneladiutor.database.tools.customcontrols.Controls;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.List;
import java.util.Random;

/**
 * Created by willi on 02.07.16.
 */
public class Values {

    public static void run(String script, Controls.ControlItem controlItem, String... arguments) {
        StringBuilder args = new StringBuilder();
        for (String arg : arguments) {
            args.append(arg).append(" ");
        }
        controlItem.setArguments(args.toString().trim());
        RootUtils.runScript(script, arguments);
    }

    public static String getString(String script) throws CustomControlException {
        if (script == null) {
            throw new CustomControlException("Script is null!");
        }
        return RootUtils.runScript(script);
    }

    public static int getInt(String script) throws CustomControlException {
        if (script == null) {
            throw new CustomControlException("Script is null!");
        }
        String result = RootUtils.runScript(script);
        try {
            return Integer.parseInt(result);
        } catch (NumberFormatException ignored) {
            throw new CustomControlException("Integer script should return an integer!");
        }
    }

    public static boolean getBool(String script) throws CustomControlException {
        if (script == null) {
            throw new CustomControlException("Script is null!");
        }
        String result = RootUtils.runScript(script);
        if (result != null && !result.isEmpty() && result.matches("(1|0)")) {
            return result.equals("1");
        } else {
            throw new CustomControlException("Boolean script can only return 1 or 0 !");
        }
    }

    public static int getUniqueId(List<Controls.ControlItem> items) {
        Random random = new Random();
        int r = random.nextInt(10000) + 1;
        for (Controls.ControlItem item : items) {
            if (item.getUniqueId() == r) {
                return getUniqueId(items);
            }
        }
        return r;
    }

}
