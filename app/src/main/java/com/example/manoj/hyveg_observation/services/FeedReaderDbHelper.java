package com.example.manoj.hyveg_observation.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DynamicForm.db";

    public static final int DATABASE_VERSION = 1;

    public FeedReaderDbHelper(Context paramContext) { super(paramContext, DATABASE_NAME, null, DATABASE_VERSION); }

    public void deleteDB(SQLiteDatabase paramSQLiteDatabase) {
        paramSQLiteDatabase.execSQL(SQLHelper.SQL_CREATE_FROM_ENTRIES);
        paramSQLiteDatabase.execSQL(SQLHelper.SQL_CREATE_FROM_DATA_ENTRIES);
        paramSQLiteDatabase.execSQL(SQLHelper.SQL_CREATE_CHOICE_ENTRIES);
        paramSQLiteDatabase.execSQL(SQLHelper.SQL_CREATE_DECIMAL_ENTRIES);
//        paramSQLiteDatabase.execSQL(SQLHelper.SQL_CREATE_IMAGES_ENTRIES);
    }

    public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
        paramSQLiteDatabase.execSQL(SQLHelper.SQL_CREATE_FROM_ENTRIES);
        paramSQLiteDatabase.execSQL(SQLHelper.SQL_CREATE_FROM_DATA_ENTRIES);
        paramSQLiteDatabase.execSQL(SQLHelper.SQL_CREATE_CHOICE_ENTRIES);
        paramSQLiteDatabase.execSQL(SQLHelper.SQL_CREATE_DECIMAL_ENTRIES);
//        paramSQLiteDatabase.execSQL(SQLHelper.SQL_CREATE_IMAGES_ENTRIES);
    }

    public void onDowngrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) { onUpgrade(paramSQLiteDatabase, paramInt1, paramInt2); }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {
        paramSQLiteDatabase.execSQL(SQLHelper.SQL_DELETE_FROM_ENTRIES);
        paramSQLiteDatabase.execSQL(SQLHelper.SQL_DELETE_FROM_DATA_ENTRIES);
        paramSQLiteDatabase.execSQL(SQLHelper.SQL_DELETE_CHOICE_ENTRIES);
        paramSQLiteDatabase.execSQL(SQLHelper.SQL_DELETE_DECIMAL_ENTRIES);
//        paramSQLiteDatabase.execSQL(SQLHelper.SQL_DELETE_CREATE_IMAGES_ENTRIES);
        onCreate(paramSQLiteDatabase);
    }
}
