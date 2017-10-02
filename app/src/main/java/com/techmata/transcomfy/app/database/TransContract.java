package com.techmata.transcomfy.app.database;

import android.provider.BaseColumns;

/**
 * Created by SparkSoft on 18/03/2015.
 */
public class TransContract {
    /* Inner class that defines the table contents */
    public static abstract class UserDetails implements BaseColumns {
        public static final String TABLE_NAME = "user_details";
        public static final String COLUMN_NAME_DETAIL_ID = "detail_id";
        public static final String COLUMN_NAME_DETAIL_NAME = "detail_name";
        public static final String COLUMN_NAME_DETAIL_VALUE = "detail_value";
    }
    public static abstract class UserProjects implements BaseColumns {
        public static final String TABLE_NAME = "user_projects";
        public static final String COLUMN_NAME_PROJECT_ID = "project_id";
        public static final String COLUMN_NAME_PROJECT_NAME = "project_name";
    }
    public static abstract class Trips implements BaseColumns {
        public static final String TABLE_NAME = "trips";
        public static final String COLUMN_NAME_TRIP_ID = "trip_id";
        public static final String COLUMN_NAME_START_MILEAGE = "start_mileage";
        public static final String COLUMN_NAME_STOP_MILEAGE = "stop_mileage";
        public static final String COLUMN_NAME_TRIP_DATE = "trip_date";
        public static final String COLUMN_NAME_TRIP_TIME = "trip_time";
        public static final String COLUMN_NAME_DATE= "date";
        public static final String COLUMN_NAME_VEHICLE_DRIVER = "vehicle_driver";
        public static final String COLUMN_NAME_START_TIME = "start_time";
        public static final String COLUMN_NAME_STOP_TIME = "stop_time";
        public static final String COLUMN_NAME_TRIP_CREATOR = "trip_creator";
        public static final String COLUMN_NAME_START_COORD = "start_coord";
        public static final String COLUMN_NAME_START_NAME = "start_name";
        public static final String COLUMN_NAME_END_COORD = "end_coord";
        public static final String COLUMN_NAME_END_NAME = "end_name";
        public static final String COLUMN_NAME_GROUP = "group_data";
        public static final String COLUMN_NAME_APPROVAL = "approval";
    }
    public static abstract class Features implements BaseColumns {
        public static final String TABLE_NAME = "features";
        public static final String COLUMN_NAME_FEATURE_ID = "feature_id";
        public static final String COLUMN_NAME_FEATURE_NAME = "feature_name";
        public static final String COLUMN_NAME_FEATURE_TYPE = "feature_type";
    }

}
