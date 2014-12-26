package com.grarak.kerneladiutor.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.grarak.kerneladiutor.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 13.12.14.
 */
public class SysDB implements Constants {

    private DBAdapter dbAdapter;
    private SQLiteDatabase database;

    private final String TABLE_BOOT = "bootTable";
    private final String KEY_ID = "_id";
    private final String KEY_SYS = "sys";
    private final String KEY_VALUE = "value";

    private final String DATABASE_NAME = "sys.db";

    private final String DATABASE_CREATE =
            "create table " + TABLE_BOOT
                    + " (" + KEY_ID + " integer primary key autoincrement, " // long (0)
                    + KEY_SYS + " text not null, " // string (1)
                    + KEY_VALUE + " string not null" // string (2)
                    + ");";

    public final String[] ALL_KEYS = new String[]{KEY_ID, KEY_SYS, KEY_VALUE};

    public SysDB(Context context) {
        dbAdapter = new DBAdapter(context, DATABASE_NAME, DATABASE_CREATE, TABLE_BOOT);
    }

    public void open() {
        database = dbAdapter.getWritableDatabase();
    }

    public void close() {
        dbAdapter.close();
    }

    public Sys insertSys(String sys, String value) {

        log("Saving " + sys + " as " + value);

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SYS, sys);
        contentValues.put(KEY_VALUE, value);

        long insertId = database.insert(TABLE_BOOT, null, contentValues);
        Cursor cursor = database.query(TABLE_BOOT,
                ALL_KEYS, KEY_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Sys toSys = cursorToSys(cursor);
        cursor.close();
        return toSys;
    }

    public boolean deleteSys(long rowId) {

        log("Delete id " + rowId);

        String where = KEY_ID + "=" + rowId;
        return database.delete(TABLE_BOOT, where, null) != 0;
    }

    public List<Sys> getAllSys() {
        List<Sys> sysList = new ArrayList<>();

        Cursor cursor = database.query(TABLE_BOOT, ALL_KEYS, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Sys sys = cursorToSys(cursor);
            sysList.add(sys);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return sysList;
    }

    private Sys cursorToSys(Cursor cursor) {
        Sys sys = new Sys();
        sys.setId(cursor.getLong(0));
        sys.setSys(cursor.getString(1));
        sys.setValue(cursor.getString(2));
        return sys;
    }

    private void log(String log) {
        Log.i(TAG, getClass().getName() + ": " + log);
    }

    public static class Sys {

        private long id;
        private String sys;
        private String value;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getSys() {
            return sys;
        }

        public String getValue() {
            return value;
        }

        public void setSys(String sys) {
            this.sys = sys;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

}
