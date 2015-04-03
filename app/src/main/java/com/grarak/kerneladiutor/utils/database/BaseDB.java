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
import android.database.sqlite.SQLiteDatabase;

import com.grarak.kerneladiutor.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 31.01.15.
 */
public abstract class BaseDB implements Constants {

    private DBAdapter dbAdapter;
    protected SQLiteDatabase database;

    protected final String KEY_ID = "_id";

    public BaseDB(Context context) {
        dbAdapter = new DBAdapter(context, getDBName(), getDBCreate(), getTable());
    }

    public void create() {
        database = dbAdapter.getWritableDatabase();
    }

    public void close() {
        dbAdapter.close();
    }

    public DBItem insertItem() {
        ContentValues contentValues = new ContentValues();
        saveItems(contentValues);

        Cursor cursor = database.query(getTable(), getAllKeys(), KEY_ID + " = "
                + database.insert(getTable(), null, contentValues), null, null, null, null);
        cursor.moveToFirst();
        DBItem dbItem = cursorToDBItem(cursor);
        cursor.close();
        return dbItem;
    }

    public boolean deleteItem(long rowId) {
        return database.delete(getTable(), KEY_ID + "=" + rowId, null) != 0;
    }

    protected List<DBItem> getAllItems() {
        List<DBItem> sysList = new ArrayList<>();

        Cursor cursor = database.query(getTable(), getAllKeys(), null, null, null, null, null);
        if (cursor.moveToFirst())
            do {
                DBItem dbItem = cursorToDBItem(cursor);
                sysList.add(dbItem);
            } while (cursor.moveToNext());
        cursor.close();
        return sysList;
    }

    public abstract String getDBName();

    public abstract String getDBCreate();

    public abstract String getTable();

    public abstract void saveItems(ContentValues contentValues);

    public abstract DBItem cursorToDBItem(Cursor cursor);

    public abstract String[] getAllKeys();

    public static class DBItem {

        private final long id;

        public DBItem(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }
    }

}
