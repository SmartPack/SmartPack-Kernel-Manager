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

package com.grarak.kerneladiutor.utils.database;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 15.04.15.
 */
public class ProfileDB extends JsonDB {

    public ProfileDB(Context context) {
        super(context.getFilesDir() + "/profiles.json", 1);
    }

    @Override
    public DBJsonItem getItem(JSONObject item) {
        return new ProfileItem(item);
    }

    public boolean containProfile(String name) {
        List<ProfileItem> profiles = getAllProfiles();

        for (ProfileItem profile : profiles) {
            if (profile.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public int getProfileId(String name) {

        List<ProfileItem> profiles = getAllProfiles();
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public void putProfile(String name, LinkedHashMap<String, String> commands) {
        try {

            JSONObject items = new JSONObject();
            items.put("name", name);

            JSONArray commandArray = new JSONArray();
            for (int i = 0; i < commands.size(); i++) {
                JSONObject item = new JSONObject();
                item.put("path", commands.keySet().toArray()[i]);
                item.put("command", commands.values().toArray()[i]);
                commandArray.put(item);
            }

            items.put("commands", commandArray);

            putItem(items);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<ProfileItem> getAllProfiles() {
        List<ProfileItem> items = new ArrayList<>();
        for (DBJsonItem jsonItem : getAllItems())
            items.add((ProfileItem) jsonItem);
        return items;
    }

    public static class ProfileItem extends DBJsonItem {

        public ProfileItem(JSONObject object) {
            item = object;
        }

        public String getName() {
            return getString("name");
        }

        public List<String> getPath() {
            return getList("path");
        }

        public List<String> getCommands() {
            return getList("command");
        }

        private List<String> getList(String name) {
            List<String> list = new ArrayList<>();
            try {
                JSONArray items = item.getJSONArray("commands");
                for (int i = 0; i < items.length(); i++) {
                    list.add(items.getJSONObject(i).getString(name));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }

        private String getString(String name) {
            try {
                return getItem().getString(name);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

}
