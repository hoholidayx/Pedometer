package com.hzp.pedometer.persistance.db;

import android.provider.BaseColumns;

/**
 * @author 何志鹏 on 2016/2/15.
 * @email hoholiday@hotmail.com
 */
public final class DailyDataTable {
    public static final String TABLE_NAME = "daily_data";

    public DailyDataTable(){
    }

    public static abstract class DailyDataEntry implements BaseColumns{
        public static final String COLUMNS_NAME_MODIFY_TIME = "modify_time";
        public static final String COLUMNS_NAME_START_TIME = "start_time";
        public static final String COLUMNS_NAME_END_TIME = "end_time";
        public static final String COLUMNS_NAME_STEP_COUNT = "step_count";
        public static final String COLUMNS_NAME_MILES = "miles";
        public static final String COLUMNS_NAME_CALORIE= "calorie";
    }
}
