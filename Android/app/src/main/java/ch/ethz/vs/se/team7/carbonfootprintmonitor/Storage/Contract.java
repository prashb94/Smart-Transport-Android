package ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage;

/**
 * Created by Prashanth on 10/23/2017
 */

import android.provider.BaseColumns;

public class Contract {

    public static final class ActivityRecordedEntry implements BaseColumns {
        public static final String TABLE_NAME = "recordedActivities";
        public static final String COL_TIMESTAMP = "timestamp";
        public static final String COL_ACTIVITY_RECORDED = "activityRecorded";
        public static final String COL_CONFIDENCE = "confidence";
        public static final String COL_SPEED = "speed";
        public static final String COL_LOCATION = "location";
    }
}
