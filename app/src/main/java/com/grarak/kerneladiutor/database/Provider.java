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

import com.grarak.kerneladiutor.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 15.04.15.
 */
public abstract class Provider {

    /**
     * JSON Objects
     */
    private JSONObject mDatabaseMain;
    private JSONArray mDatabaseItems;

    /**
     * JSON file location
     */
    private final String mPath;

    private int mVersion;

    /**
     * JSON Database is used to store large amount of datasets
     *
     * @param path    location of the JSON file
     * @param version If version doesn't match with the dataset, remove all saved datas
     */
    public Provider(String path, int version) {
        mPath = path;
        mVersion = version;
        try {
            String json = Utils.readFile(path, false);
            if (json != null) {
                mDatabaseMain = new JSONObject(json);
                if (mDatabaseMain.getInt("version") == version) {
                    mDatabaseItems = mDatabaseMain.getJSONArray("database");
                }
            }
        } catch (JSONException ignored) {
        }

        if (mDatabaseItems == null) {
            mDatabaseItems = new JSONArray();
        }
        try {
            mDatabaseMain = new JSONObject();
            mDatabaseMain.put("version", version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a dataset
     *
     * @param items the dataset will put into the JSONArray
     */
    protected void putItem(JSONObject items) {
        mDatabaseItems.put(items);
    }

    /**
     * Read all sets
     *
     * @return all sets in a list
     */
    protected List<DBJsonItem> getAllItems() {
        List<DBJsonItem> items = new ArrayList<>();
        try {
            for (int i = 0; i < length(); i++) {
                items.add(getItem(mDatabaseItems.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }

    public abstract DBJsonItem getItem(JSONObject item);

    public void delete(int position) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < length(); i++) {
                if (i != position) {
                    jsonArray.put(mDatabaseItems.getJSONObject(i));
                }
            }
            mDatabaseItems = jsonArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int length() {
        return mDatabaseItems.length();
    }

    public int getVersion() {
        return mVersion;
    }

    /**
     * Write the dataset as JSON file
     */
    public void commit() {
        try {
            mDatabaseMain.put("database", mDatabaseItems);
            Utils.writeFile(mPath, mDatabaseMain.toString(), false, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class DBJsonItem {

        private JSONObject mItem;

        protected DBJsonItem() {
            mItem = new JSONObject();
        }

        protected DBJsonItem(JSONObject item) {
            mItem = item;
        }

        public JSONObject getItem() {
            return mItem;
        }

        public int getInt(String name) {
            try {
                return getItem().getInt(name);
            } catch (JSONException ignored) {
                return 0;
            }
        }

        public String getString(String name) {
            try {
                return getItem().getString(name);
            } catch (JSONException ignored) {
                return null;
            }
        }

    }

}
