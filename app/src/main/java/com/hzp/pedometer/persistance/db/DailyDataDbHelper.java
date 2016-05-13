package com.hzp.pedometer.persistance.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author 何志鹏 on 2016/2/15.
 * @email hoholiday@hotmail.com
 */
public class DailyDataDbHelper extends SQLiteOpenHelper {

    private static final String LONG_TYPE = " LONG";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DailyDataTable.TABLE_NAME + " (" +
                    DailyDataTable.DailyDataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +COMMA_SEP+
                    DailyDataTable.DailyDataEntry.COLUMNS_NAME_MODIFY_TIME + LONG_TYPE +COMMA_SEP +
                    DailyDataTable.DailyDataEntry.COLUMNS_NAME_START_TIME + LONG_TYPE +COMMA_SEP +
                    DailyDataTable.DailyDataEntry.COLUMNS_NAME_END_TIME + LONG_TYPE +COMMA_SEP +
                    DailyDataTable.DailyDataEntry.COLUMNS_NAME_STEP_COUNT + INT_TYPE  +
                    DailyDataTable.DailyDataEntry.COLUMNS_NAME_MILES + DOUBLE_TYPE  +
                    DailyDataTable.DailyDataEntry.COLUMNS_NAME_CALORIE + DOUBLE_TYPE  +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DailyDataTable.TABLE_NAME;




    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "DailyData.db";

    public DailyDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
