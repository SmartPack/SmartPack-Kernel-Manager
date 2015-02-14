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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 31.01.15.
 */
public class ProfileDB extends BaseDB {

    private final String TABLE_BOOT = "profileTable";
    private final String KEY_NAME = "name";
    private final String KEY_SYS = "sys";
    private final String KEY_COMMANDS = "commands";

    private String name;
    private List<String> sys;
    private List<String> commands;

    private String split = "poiuytrewqadfghjkmnbvcsrtyu";

    public ProfileDB(Context context) {
        super(context);
    }

    @Override
    public String getDBName() {
        return "profiles.db";
    }

    @Override
    public String getTable() {
        return TABLE_BOOT;
    }

    @Override
    public String getDBCreate() {
        return "create table " + TABLE_BOOT + " ("
                + KEY_ID + " integer primary key autoincrement, " // long (0)
                + KEY_NAME + " text not null, " // string (1)
                + KEY_SYS + " text not null, " // string (2)
                + KEY_COMMANDS + " string not null" // string (3)
                + ");";
    }

    @Override
    public String[] getAllKeys() {
        return new String[]{KEY_ID, KEY_NAME, KEY_SYS, KEY_COMMANDS};
    }

    public void insertProfile(String name, List<String> sys, List<String> commands) {
        this.name = name;
        this.sys = sys;
        this.commands = commands;
        insertItem();

        Log.i(TAG, "ProfileDB: Saving " + name + " as " + commands);
    }

    @Override
    public boolean deleteItem(long rowId) {
        Log.i(TAG, "ProfileDB: Delete id " + rowId);
        return super.deleteItem(rowId);
    }

    @Override
    public void saveItems(ContentValues contentValues) {
        contentValues.put(KEY_NAME, name);
        String value = "";
        for (String sy : sys)
            value += value.isEmpty() ? sy : split + sy;
        contentValues.put(KEY_SYS, value);
        value = "";
        for (String command : commands)
            value += value.isEmpty() ? command : split + command;
        contentValues.put(KEY_COMMANDS, value);
    }

    @Override
    public DBItem cursorToDBItem(Cursor cursor) {
        return new ProfileItem(cursor.getLong(0), cursor.getString(1),
                new ArrayList<>(Arrays.asList(cursor.getString(2).split(split))),
                new ArrayList<>(Arrays.asList(cursor.getString(3).split(split))));
    }

    public List<ProfileItem> getAllProfiles() {
        List<DBItem> dbItems = getAllItems();
        List<ProfileItem> sysItems = new ArrayList<>();
        for (DBItem dbItem : dbItems) sysItems.add((ProfileItem) dbItem);
        return sysItems;
    }

    public ProfileItem getItem(long id) {
        for (ProfileItem profileItem : getAllProfiles())
            if (profileItem.getId() == id) return profileItem;
        return null;
    }

    public static class ProfileItem extends DBItem {

        private final String name;
        private final List<String> sys;
        private final List<String> commands;

        public ProfileItem(long id, String name, List<String> sys, List<String> commands) {
            super(id);
            this.name = name;
            this.sys = sys;
            this.commands = commands;
        }

        public String getName() {
            return name;
        }

        public List<String> getSys() {
            return sys;
        }

        public List<String> getCommands() {
            return commands;
        }
    }

}
