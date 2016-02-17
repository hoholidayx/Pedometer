package com.hzp.pedometer.persistance.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hzp.pedometer.entity.DailyData;

import java.util.Date;

/**
 * @author 何志鹏 on 2016/2/16.
 * @email hoholiday@hotmail.com
 */
public class DailyDataManager {
    private static DailyDataManager instance;

    private Context context;
    private DailyDataDbHelper dbHelper;
    private SQLiteDatabase database;

    private DailyDataManager() {
    }

    public static DailyDataManager getInstance() {
        if (instance == null) {
            synchronized (DailyDataManager.class) {
                if (instance == null) {
                    instance = new DailyDataManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;

        dbHelper = new DailyDataDbHelper(context);
        new Thread() {
            @Override
            public void run() {
                database = dbHelper.getWritableDatabase();
            }
        }.start();
    }

    public enum SortOrder {
        MODIFY_TIME_DESC(DailyDataTable.DailyDataEntry.COLUMNS_NAME_MODIFY_TIME + " DESC");

        private String value;

        SortOrder(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public void saveData(long modifyTime, long startTime, long endTime, int stepCount) {
        ContentValues values = new ContentValues();
        values.put(DailyDataTable.DailyDataEntry.COLUMNS_NAME_MODIFY_TIME, modifyTime);
        values.put(DailyDataTable.DailyDataEntry.COLUMNS_NAME_START_TIME, startTime);
        values.put(DailyDataTable.DailyDataEntry.COLUMNS_NAME_END_TIME, endTime);
        values.put(DailyDataTable.DailyDataEntry.COLUMNS_NAME_STEP_COUNT, stepCount);

        database.insert(DailyDataTable.TABLE_NAME, null, values);
    }

    public void saveData(DailyData data) {
        saveData(data.getModifyTime(), data.getStartTime(), data.getEndTime(), data.getStepCount());
    }

    public DailyData[] getDataList(long startTime, long endTime, SortOrder order) {
        String[] projection = {
                DailyDataTable.DailyDataEntry.COLUMNS_NAME_MODIFY_TIME,
                DailyDataTable.DailyDataEntry.COLUMNS_NAME_START_TIME,
                DailyDataTable.DailyDataEntry.COLUMNS_NAME_END_TIME,
                DailyDataTable.DailyDataEntry.COLUMNS_NAME_STEP_COUNT
        };

        String selection =
                DailyDataTable.DailyDataEntry.COLUMNS_NAME_START_TIME +
                        " >= ?  and" +
                        DailyDataTable.DailyDataEntry.COLUMNS_NAME_END_TIME +
                        "<= ?";
        String[] selectionArgs = {
                String.valueOf(startTime),
                String.valueOf(endTime)
        };

        Cursor c = database.query(
                DailyDataTable.TABLE_NAME,
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                order.toString()                          // The sort order
        );

        return getDataFromCursor(c);
    }

    public void closeDatabase() {
        database.close();
    }

    private DailyData[] getDataFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return null;
        }
        DailyData[] list = new DailyData[cursor.getCount()];
        int i = 0;
        cursor.moveToFirst();

        do {
            DailyData dailyData = new DailyData();
            dailyData
                    .setModifyTime(cursor.getLong(
                            cursor.getColumnIndexOrThrow(
                                    DailyDataTable.DailyDataEntry.COLUMNS_NAME_MODIFY_TIME)))
                    .setStartTime(cursor.getLong(
                            cursor.getColumnIndexOrThrow(
                                    DailyDataTable.DailyDataEntry.COLUMNS_NAME_START_TIME)))
                    .setEndTime(cursor.getLong(
                            cursor.getColumnIndexOrThrow(
                                    DailyDataTable.DailyDataEntry.COLUMNS_NAME_END_TIME)))
                    .setStepCount(cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DailyDataTable.DailyDataEntry.COLUMNS_NAME_STEP_COUNT)));

            list[i] = dailyData;

        } while (cursor.moveToNext());

        return list;
    }
}
