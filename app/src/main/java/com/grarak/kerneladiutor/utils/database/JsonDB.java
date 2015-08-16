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

import com.kerneladiutor.library.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 15.04.15.
 */
public abstract class JsonDB {

    /**
     * JSON Objects
     */
    private JSONObject databaseMain;
    private JSONArray databaseItems;

    /**
     * JSON file location
     */
    private final String path;

    /**
     * JSON Database is used to store large amount of datasets
     *
     * @param path    location of the JSON file
     * @param version If version doesn't match with the dataset, remove all saved datas
     */
    public JsonDB(String path, int version) {
        this.path = path;
        try {
            String json = Tools.readFile(path, false);
            if (json != null) {
                databaseMain = new JSONObject(json);
                if (databaseMain.getInt("version") == version)
                    databaseItems = databaseMain.getJSONArray("database");
            }
        } catch (JSONException ignored) {
        }

        if (databaseItems == null) databaseItems = new JSONArray();
        try {
            databaseMain = new JSONObject();
            databaseMain.put("version", version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a dataset
     *
     * @param items the dataset will put into the JSONArray
     */
    public void putItem(JSONObject items) {
        databaseItems.put(items);
    }

    /**
     * Read all sets
     *
     * @return all sets in a list
     */
    public List<DBJsonItem> getAllItems() {
        List<DBJsonItem> items = new ArrayList<>();
        try {
            for (int i = 0; i < length(); i++)
                items.add(getItem(databaseItems.getJSONObject(i)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }

    public abstract DBJsonItem getItem(JSONObject item);

    public void delete(int position) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < length(); i++)
                if (i != position) jsonArray.put(databaseItems.getJSONObject(i));
            databaseItems = jsonArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int length() {
        return databaseItems.length();
    }

    /**
     * Write the dataset as JSON file
     */
    public void commit() {
        try {
            databaseMain.put("database", databaseItems);
            Tools.writeFile(path, databaseMain.toString(), false, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class DBJsonItem {

        protected JSONObject item;

        public DBJsonItem() {
            item = new JSONObject();
        }

        public JSONObject getItem() {
            return item;
        }

    }

}
