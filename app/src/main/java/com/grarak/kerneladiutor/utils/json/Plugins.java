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

package com.grarak.kerneladiutor.utils.json;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by willi on 15.08.15.
 */
public class Plugins {

    private JSONArray PLUGINS;

    public Plugins(String json) {
        try {
            PLUGINS = new JSONArray(json);
        } catch (JSONException ignored) {
        }
    }

    public String getDownloadLink(int position) {
        return getString("download", position);
    }

    public String getVersion(int position) {
        return getString("version", position);
    }

    public String getPackageName(int position) {
        return getString("packagename", position);
    }

    public String getDescription(int position) {
        return getString("description", position);
    }

    public String getName(int position) {
        return getString("name", position);
    }

    private String getString(String name, int position) {
        try {
            return PLUGINS.getJSONObject(position).getString(name);
        } catch (JSONException ignored) {
            return null;
        }
    }

    public int size() {
        return PLUGINS.length();
    }

    public boolean readable() {
        return PLUGINS != null && PLUGINS.length() != 0;
    }

}
