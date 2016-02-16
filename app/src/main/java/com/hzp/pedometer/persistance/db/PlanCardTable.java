package com.hzp.pedometer.persistance.db;

import android.provider.BaseColumns;

/**
 * @author 何志鹏 on 2016/2/15.
 * @email hoholiday@hotmail.com
 */
public final class PlanCardTable {
    public PlanCardTable(){
    }

    public static abstract class PlanCardTableEntry implements BaseColumns{
        public static final String TABLE_NAME = "plan_cards";
        public static final String COLUMNS_NAME_ID = "card_id";
        public static final String COLUMNS_NAME_MODIFY_TIME = "modify_time";
        public static final String COLUMNS_NAME_STEP_COUNT = "step_count";
    }
}
