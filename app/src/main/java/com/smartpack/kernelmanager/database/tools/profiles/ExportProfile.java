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
package com.smartpack.kernelmanager.database.tools.profiles;

import android.content.Context;
import android.os.Environment;

import com.smartpack.kernelmanager.utils.Utils;
import com.topjohnwu.superuser.io.SuFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by willi on 12.07.16.
 */
public class ExportProfile {

    private final JSONObject mMain;

    public ExportProfile(Profiles.ProfileItem item, int version) {
        mMain = new JSONObject();
        try {
            mMain.put("version", version);
            item.getItem().remove("name");
            mMain.put("profile", item.getItem());
        } catch (JSONException ignored) {
        }
    }

    public boolean export(String name, Context context) {
        if (!name.endsWith(".json")) name += ".json";
        File exportFiles = SuFile.open(Environment.getExternalStorageDirectory(), "SP/profiles");
        File file = SuFile.open(exportFiles.toString() + "/" + name);
        if (file.exists()) return false;
        exportFiles.mkdirs();
        Utils.writeFile(file.toString(), mMain.toString(), false, false);
        return true;
    }

}
