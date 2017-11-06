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

    public List<List<String>> getRecordsStringArray(String SQLQuery){
        Cursor cursor =  sqLiteDatabase.rawQuery(SQLQuery, null);
        String[] colNames = cursor.getColumnNames();
        List<List<String>> matrix = new ArrayList<List<String>>();
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                ArrayList<String> singleRow = new ArrayList<String>();
                for(int colCount = 0; colCount < colNames.length; colCount++){
                    singleRow.add(cursor.getString(cursor.getColumnIndex(colNames[colCount])));
                }
                matrix.add(singleRow);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return matrix;
    }
}
