package ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prashanth on 11/3/2017.
 */

public class SQLQueryHelper{

    private static DbHandler dbHandler;
    private static SQLiteDatabase sqLiteDatabase;

    public SQLQueryHelper(Context context){
        if(context==null){
            Log.e("SQLHELPER", "NullContext! Pass context to constructor.");
        }
        dbHandler = new DbHandler(context);
        sqLiteDatabase = dbHandler.getReadableDatabase();
    }

    public static List<List<String>> getRecordsStringArray(String SQLQuery){
        Cursor cursor =  sqLiteDatabase.rawQuery(SQLQuery, null);
        Integer[] indices = new Integer[]{
                cursor.getColumnIndex(Contract.ActivityRecordedEntry.COL_TIMESTAMP),
                cursor.getColumnIndex(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED),
                cursor.getColumnIndex(Contract.ActivityRecordedEntry.COL_CONFIDENCE),
                cursor.getColumnIndex(Contract.ActivityRecordedEntry.COL_SPEED),
                cursor.getColumnIndex(Contract.ActivityRecordedEntry.COL_LOCATION)};

        List<List<String>> matrix = new ArrayList<List<String>>();
        int rowCount = 0;
        int colCount = 0;
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                matrix.get(rowCount).add(cursor.getString(indices[colCount]));
                colCount++;
            }while(cursor.moveToNext());
            rowCount++;
        }

        return matrix;
    }
}
