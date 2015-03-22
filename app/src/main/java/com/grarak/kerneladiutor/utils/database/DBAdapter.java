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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grarak.kerneladiutor.utils.Constants;

/**
 * Created by willi on 13.12.14.
 */
public class DBAdapter extends SQLiteOpenHelper implements Constants {

    private final String DATABASE_CREATE;
    private final String DATABASE_TABLE;

    public DBAdapter(Context context, String DATABASE_NAME, String DATABASE_CREATE, String DATABASE_TABLE) {
        super(context, DATABASE_NAME, null, 2 /* Version */);
        this.DATABASE_CREATE = DATABASE_CREATE;
        this.DATABASE_TABLE = DATABASE_TABLE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading application's database from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data!");

        // Destroy old database:
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

        // Recreate new database:
        onCreate(db);
    }

}
