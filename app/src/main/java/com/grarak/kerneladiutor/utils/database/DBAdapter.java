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
    private static int DATABASE_VERSION = 1;

    public DBAdapter(Context context, String DATABASE_NAME, String DATABASE_CREATE, String DATABASE_TABLE) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.DATABASE_CREATE = DATABASE_CREATE;
        this.DATABASE_TABLE = DATABASE_TABLE;
    }

    @Override
    public void onCreate(SQLiteDatabase _db) {
        _db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading application's database from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data!");

        // Destroy old database:
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

        // Recreate new database:
        onCreate(_db);
    }

}
