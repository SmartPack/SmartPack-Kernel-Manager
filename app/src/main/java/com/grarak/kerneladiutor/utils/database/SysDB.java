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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 31.01.15.
 */
public class SysDB extends BaseDB {

    private final String TABLE_BOOT = "bootTable";
    private final String KEY_SYS = "sys";
    private final String KEY_COMMAND = "command";

    private String sys;
    private String command;

    public SysDB(Context context) {
        super(context);
    }

    @Override
    public String getDBName() {
        return "sys.db";
    }

    @Override
    public String getTable() {
        return TABLE_BOOT;
    }

    @Override
    public String getDBCreate() {
        return "create table " + TABLE_BOOT + " ("
                + KEY_ID + " integer primary key autoincrement, " // long (0)
                + KEY_SYS + " text not null, " // string (1)
                + KEY_COMMAND + " string not null" // string (2)
                + ");";
    }

    @Override
    public String[] getAllKeys() {
        return new String[]{KEY_ID, KEY_SYS, KEY_COMMAND};
    }

    public void insertSys(String sys, String command) {
        this.sys = sys;
        this.command = command;
        insertItem();
    }

    @Override
    public boolean deleteItem(long rowId) {
        return super.deleteItem(rowId);
    }

    @Override
    public void saveItems(ContentValues contentValues) {
        contentValues.put(KEY_SYS, sys);
        contentValues.put(KEY_COMMAND, command);
    }

    @Override
    public DBItem cursorToDBItem(Cursor cursor) {
        return new SysItem(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
    }

    public List<SysItem> getAllSys() {
        List<DBItem> dbItems = getAllItems();
        List<SysItem> sysItems = new ArrayList<>();
        for (DBItem dbItem : dbItems) sysItems.add((SysItem) dbItem);
        return sysItems;
    }

    public static class SysItem extends DBItem {

        private final String sys;
        private final String command;

        public SysItem(long id, String sys, String command) {
            super(id);
            this.sys = sys;
            this.command = command;
        }

        public String getSys() {
            return sys;
        }

        public String getCommand() {
            return command;
        }
    }

}
