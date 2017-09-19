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
package com.grarak.kerneladiutor.database;

import android.content.Context;

import com.grarak.kerneladiutor.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 15.04.15.
 */
public class Settings extends Provider {

    /**
     * Read the JSON file from cache
     *
     * @param context needed to get the cache directory
     */
    public Settings(Context context) {
        super(context.getFilesDir() + "/settings.json", 1);

        if (Utils.existFile(context.getFilesDir() + "/commands.json", false)) {
            new File(context.getFilesDir() + "/commands.json").delete();
        }
    }

    @Override
    public DBJsonItem getItem(JSONObject item) {
        return new SettingsItem(item);
    }

    public void putSetting(String category, String setting, String id) {
        try {
            JSONObject items = new JSONObject();
            items.put("category", category);
            items.put("setting", setting);
            items.put("id", id);
            putItem(items);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<SettingsItem> getAllSettings() {
        List<SettingsItem> items = new ArrayList<>();
        for (DBJsonItem jsonItem : getAllItems()) {
            items.add((SettingsItem) jsonItem);
        }
        return items;
    }

    public static class SettingsItem extends DBJsonItem {

        private SettingsItem(JSONObject object) {
            super(object);
        }

        public String getSetting() {
            return getString("setting");
        }

        public String getCategory() {
            return getString("category");
        }

        public String getId() {
            return getString("id");
        }

    }

}
