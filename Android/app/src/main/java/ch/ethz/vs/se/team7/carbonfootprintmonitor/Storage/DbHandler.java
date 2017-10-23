package ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.Contract.ActivityRecordedEntry;


public class DbHandler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "recordedActivities.db";
    private static final int DATABASE_VERSION = 1;

    public DbHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE_QUERY = "CREATE TABLE "
                + ActivityRecordedEntry.TABLE_NAME + " ("
                + ActivityRecordedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ActivityRecordedEntry.COL_ACTIVITY_RECORDED + " TEXT NOT NULL, "
                + ActivityRecordedEntry.COL_CONFIDENCE + " DOUBLE NOT NULL, "
                + ActivityRecordedEntry.COL_SPEED + " DOUBLE NOT NULL, "
                + ActivityRecordedEntry.COL_LOCATION + " TEXT NOT NULL, "
                + ActivityRecordedEntry.COL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ");";

        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ActivityRecordedEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
