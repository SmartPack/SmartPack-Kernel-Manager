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
import com.grarak.kerneladiutor.utils.tools.customcontrols.Items;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by willi on 04.07.16.
 */
public class ImportControl {

    private int mVersion;
    private JSONObject mControlJSON;

    public ImportControl(String path) {
        String json = Utils.readFile(path);
        if (json != null && !json.isEmpty()) {
            try {
                JSONObject main = new JSONObject(json);
                mVersion = main.getInt("version");
                JSONObject control = main.getJSONObject("control");

                String id = control.getString("id");
                if (id != null) {
                    Items.Control controlItem = Items.Control.getControl(id);
                    if (controlItem != null) {
                        ArrayList<Items.Setting> settings = Items.getSettings(controlItem);
                        for (Items.Setting setting : settings) {
                            if (setting.isRequired() && !control.has(setting.getId())) {
                                control = null;
                                break;
                            }
                        }
                    }
                    mControlJSON = control;
                }
            } catch (JSONException ignored) {
            }
        }
    }

    public HashMap<String, Object> getResults() {
        HashMap<String, Object> results = new HashMap<>();
        for (Iterator<String> iter = mControlJSON.keys(); iter.hasNext(); ) {
            try {
                String key = iter.next();
                results.put(key, mControlJSON.get(key));
            } catch (JSONException ignored) {
            }
        }
        results.put("uniqueId", 0);
        return results;
    }

    public boolean matchesVersion() {
        return mVersion == Controls.VERSION;
    }

    public boolean readable() {
        return mControlJSON != null;
    }

}
