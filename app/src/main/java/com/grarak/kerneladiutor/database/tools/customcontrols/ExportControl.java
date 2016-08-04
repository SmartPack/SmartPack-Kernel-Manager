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
package com.grarak.kerneladiutor.database.tools.customcontrols;

import com.grarak.kerneladiutor.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by willi on 03.07.16.
 */
public class ExportControl {

    private JSONObject mMain;

    public ExportControl(Controls.ControlItem item, int version) {
        mMain = new JSONObject();
        try {
            item.getItem().remove("uniqueId");
            item.getItem().remove("onboot");
            item.getItem().remove("arguments");
            mMain.put("version", version);
            mMain.put("control", item.getItem());
        } catch (JSONException ignored) {
        }
    }

    public boolean export(String name) {
        if (!name.endsWith(".json")) name += ".json";
        File exportFiles = new File(Utils.getInternalDataStorage() + "/controls");
        File file = new File(exportFiles.toString() + "/" + name);
        if (file.exists()) return false;
        exportFiles.mkdirs();
        Utils.writeFile(file.toString(), mMain.toString(), false, false);
        return true;
    }

}
