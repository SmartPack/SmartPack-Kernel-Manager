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
        Log.i(TAG, "Delete id " + rowId);
        return database.delete(getTable(), KEY_ID + "=" + rowId, null) != 0;
    }

    protected List<DBItem> getAllItems() {
        List<DBItem> sysList = new ArrayList<>();

        Cursor cursor = database.query(getTable(), getAllKeys(), null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DBItem dbItem = cursorToDBItem(cursor);
            sysList.add(dbItem);
            cursor.moveToNext();
        }
        cursor.close();
        return sysList;
    }

    public void deleteAllItems() {
        List<DBItem> dbItems = getAllItems();
        for (DBItem dbItem : dbItems) deleteItem(dbItem.getId());
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
