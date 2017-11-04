package ch.ethz.vs.se.team7.carbonfootprintmonitor;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;
import android.widget.Toast;

import ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.Contract;
import ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.DbHandler;

public class ShowRecords extends AppCompatActivity {

    SQLiteDatabase database;
    private DbHandler dbAdapter;

    TableLayout tableLayout;
    TableRow row;
    TextView firstCol;
    TextView secondCol;
    TextView thirdCol;
    TextView fourthCol;
    TextView fifthCol;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_records);
        tableLayout=(TableLayout)findViewById(R.id.dbTable);
        dbAdapter = new DbHandler(this);
        displayDB();
    }

    private void displayDB() {
        try {
            database = dbAdapter.getReadableDatabase();
            //database = openOrCreateDatabase(DbHandler.DATABASE_NAME, Context.MODE_PRIVATE, null);
        }
        catch (Exception e){
            Log.e("OPEN_DB", String.valueOf(e));
        }

        if(database!=null)
        {
            try {
                Cursor cursor = database.rawQuery("SELECT * FROM " + Contract.ActivityRecordedEntry.TABLE_NAME, null);
                Integer index0 = cursor.getColumnIndex(Contract.ActivityRecordedEntry.COL_TIMESTAMP);
                Integer index1 = cursor.getColumnIndex(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED);
                Integer index2 = cursor.getColumnIndex(Contract.ActivityRecordedEntry.COL_CONFIDENCE);
                Integer index3 = cursor.getColumnIndex(Contract.ActivityRecordedEntry.COL_SPEED);
                Integer index4 = cursor.getColumnIndex(Contract.ActivityRecordedEntry.COL_LOCATION);

                if(cursor.getCount() > 0)
                {
                    cursor.moveToFirst();
                    do
                    {
                        row = new TableRow(this);
                        row.setLayoutParams(new LayoutParams(
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT));

                    /*Setting up the first column parameters*/
                        firstCol=new TextView(this);
                        firstCol.setText(cursor.getString(index0));
                        firstCol.setTextColor(Color.GREEN);
                        firstCol.setLayoutParams(new LayoutParams(
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT));
                        row.addView(firstCol); //adding column to row

                    /*Setting up the second column parameters*/
                        secondCol=new TextView(this);
                        secondCol.setText(cursor.getString(index1));
                        secondCol.setTextColor(Color.RED);
                        secondCol.setLayoutParams(new LayoutParams(
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT));
                        row.addView(secondCol); //adding column to row

                    /*Setting up the third column parameters*/
                        thirdCol=new TextView(this);
                        thirdCol.setText(cursor.getString(index2));
                        thirdCol.setTextColor(Color.MAGENTA);
                        thirdCol.setLayoutParams(new LayoutParams(
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT));
                        row.addView(thirdCol); //adding column to row

                    /*Setting up the fourth column parameters*/
                        fourthCol=new TextView(this);
                        fourthCol.setText(cursor.getString(index3));
                        fourthCol.setTextColor(Color.MAGENTA);
                        fourthCol.setLayoutParams(new LayoutParams(
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT));
                        row.addView(fourthCol); //adding column to row

                    /*Setting up the fifth column parameters*/
                        fifthCol=new TextView(this);
                        fifthCol.setText(cursor.getString(index4));
                        fifthCol.setTextColor(Color.MAGENTA);
                        fifthCol.setLayoutParams(new LayoutParams(
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT));
                        row.addView(fifthCol); //adding column to row

                    /*Adding the row to the tablelayout*/
                        tableLayout.addView(row,new TableLayout.LayoutParams(
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT));

                    }while(cursor.moveToNext());
                    cursor.close();
                    database.close();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "No records yet!", Toast.LENGTH_LONG).show();
                }
            }  catch (Exception e){
                Log.e("QUERY_DB", String.valueOf(e));
            }

        }
    }
}
