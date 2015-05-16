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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 15.04.15.
 */
public class CommandDB extends JsonDB {

    /**
     * Read the JSON file from cache
     *
     * @param context needed to get the cache directory
     */
    public CommandDB(Context context) {
        super(context.getFilesDir() + "/commands.json", 1);
    }

    @Override
    public DBJsonItem getItem(JSONObject item) {
        return new CommandItem(item);
    }

    public void putCommand(String path, String command) {
        try {
            JSONObject items = new JSONObject();
            items.put("path", path);
            items.put("command", command);
            putItem(items);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<CommandItem> getAllCommands() {
        List<CommandItem> items = new ArrayList<>();
        for (DBJsonItem jsonItem : getAllItems())
            items.add((CommandItem) jsonItem);
        return items;
    }

    public static class CommandItem extends DBJsonItem {

        public CommandItem(JSONObject object) {
            item = object;
        }

        public String getCommand() {
            return getString("command");
        }

        public String getPath() {
            return getString("path");
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
