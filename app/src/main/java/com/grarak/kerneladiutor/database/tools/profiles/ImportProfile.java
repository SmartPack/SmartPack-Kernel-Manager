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
package com.grarak.kerneladiutor.database.tools.profiles;

import com.grarak.kerneladiutor.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by willi on 15.07.16.
 */
public class ImportProfile {

    private int mVersion;
    private JSONArray mCommands;

    public ImportProfile(String path) {
        String json = Utils.readFile(path);
        if (json != null && !json.isEmpty()) {
            try {
                JSONObject main = new JSONObject(json);
                mVersion = main.getInt("version");
                JSONObject profile = main.getJSONObject("profile");

                mCommands = profile.getJSONArray("commands");
                for (int i = 0; i < mCommands.length(); i++) {
                    JSONObject command = mCommands.getJSONObject(i);
                    if (!command.has("path") || !command.has("command")) {
                        mCommands = null;
                        break;
                    }
                }
            } catch (JSONException ignored) {
            }
        }
    }

    public LinkedHashMap<String, String> getResults() {
        LinkedHashMap<String, String> results = new LinkedHashMap<>();
        for (int i = 0; i < mCommands.length(); i++) {
            try {
                JSONObject command = mCommands.getJSONObject(i);
                results.put(command.getString("path"), command.getString("command"));
            } catch (JSONException ignored) {
            }
        }
        return results;
    }

    public boolean matchesVersion() {
        return Profiles.VERSION == mVersion;
    }

    public boolean readable() {
        return mCommands != null;
    }

}
