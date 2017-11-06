package ch.ethz.vs.se.team7.carbonfootprintmonitor;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.Contract;
import ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.DbHandler;
import ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.SQLQueryHelper;

public class ActivityRecognizedService extends IntentService {

    private DbHandler dbAdapter;

    private SQLiteDatabase activityRecordedDb;

    private LastKnownLocationAndSpeed lastKnownLocationAndSpeed;

    private String lastDetectedSpeed;

    private String lastDetectedLocation;

    private SharedPreferences sharedPreferences;

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("CUR_CONTEXT","IN ON INTENT HANDLED!" + intent);

        dbAdapter = new DbHandler(this);

        activityRecordedDb = dbAdapter.getWritableDatabase();


        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result.getProbableActivities());
        }
    }

    void addRecordToDb(ContentValues record){
        try{
            activityRecordedDb.beginTransaction();
            activityRecordedDb.insert(Contract.ActivityRecordedEntry.TABLE_NAME, null, record);
            activityRecordedDb.setTransactionSuccessful();
        }catch (SQLException e){
            Log.e("Sql Exception", String.valueOf(e.getStackTrace()));
        }finally {
            Log.d("DB_ADD","RECORD ADDED TO DATABASE!");
            activityRecordedDb.endTransaction();
        }
    }

    private String carTramDifferentiator() {
        /*
        While travelling in a tram, the activity observed was blocks of - "Standing Still" and blocks of "In Vehicle" with a certain maximum speed.
        The logic used here is that such a pattern indicates with a high probability that the user is in a tram. Integrating google maps is an option but that too comes with the limit of experimental error.
        Eg: The car could be travelling along a tram route and during traffic, leading to an inference of tram. This approach too comes with a certain error, but can be finetuned with the provided parameters
        to reduce the error as much as possible.
        First, to observe a tram pattern as described above, we aggregate a set number of past records (@param) and figure out the blocks in this pattern and convert them to percentages, while also
        calculating the average speed.
        Now we define additionally a tolerance (@param) that can be used to finetune the differentiator. If there are equal blocks of "Standing Still" and "Vehicle" or nearly equal (Plus or minus tolerance)
        and if the current speed is close to the average speed in this observation interval we can say with a high probability that the user is in a Tram/Bus. This probability depends on the country/location
        and the combination of parameters.
         */
        try{
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            int numberOfRecords = sharedPreferences.getInt("nRecords", 50);
            int tolerancePercentage = sharedPreferences.getInt("tolerance", 10);
            int histStillValues = 0;
            int histVehicleValues = 0;
            double avgSpeed = 0;

            SQLiteDatabase sqLiteDatabase;


            String customSQLQuery = "SELECT COUNT(" + Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED + "), " +  Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED
                    + ", AVG(CAST(" + Contract.ActivityRecordedEntry.COL_SPEED + " as float))" +
                    " FROM ( SELECT * FROM " + Contract.ActivityRecordedEntry.TABLE_NAME + " LIMIT" + numberOfRecords + ") "
                    + " GROUP BY " + Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED;

            DbHandler dbHandler = new DbHandler(this);
            sqLiteDatabase = dbHandler.getReadableDatabase();
            Cursor cursor =  sqLiteDatabase.rawQuery(customSQLQuery, null);
            String[] colNames = cursor.getColumnNames();

            // Iterate over returned records, namely number of times Standing Still and number of times in Vehicle, while accumulating the average speed
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                do{
                    switch(Integer.parseInt(cursor.getString(cursor.getColumnIndex(colNames[1])))){
                        case DetectedActivity.ON_FOOT:
                        case DetectedActivity.WALKING:
                        case DetectedActivity.RUNNING:
                        case DetectedActivity.STILL:
                        case DetectedActivity.UNKNOWN:
                            avgSpeed += Double.parseDouble(cursor.getString(cursor.getColumnIndex(colNames[2])));
                            histStillValues += Integer.parseInt(cursor.getString(cursor.getColumnIndex(colNames[0])));
                            break;
                        case DetectedActivity.IN_VEHICLE:
                            histVehicleValues += Integer.parseInt(cursor.getString(cursor.getColumnIndex(colNames[0])));
                            break;

                    }
                }while(cursor.moveToNext());
            }

            cursor.close();
            sqLiteDatabase.close();

            if(histStillValues == 0 && histVehicleValues == 0){
                return "CAR";
            }

            //In a tram only 2 possible cases - Standing Still, or moving. Average speed across these 2 cases.
            avgSpeed = avgSpeed/2;

            double histStillPercentage = (histStillValues/(histStillValues+histVehicleValues)) * 100;
            double histVehiclePercentage = (histVehicleValues/(histStillValues+histVehicleValues)) * 100;

            boolean overlapHist = ((histStillPercentage + tolerancePercentage) >= histVehiclePercentage)
                    || ((histStillPercentage - tolerancePercentage) <= histVehiclePercentage);

            boolean overlapSpeed = ((tolerancePercentage * Double.parseDouble(lastDetectedSpeed)/100) + Double.parseDouble(lastDetectedSpeed) >= avgSpeed);

            if(overlapHist && overlapSpeed){
                return "TRAM";
            }
            else
            {
                return "CAR";
            }
        } catch (Exception e){
            Log.e("ERROR DIFF TRAM/CAR", e.getStackTrace().toString());
            return "CAR";
        }

    }
    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {

        lastKnownLocationAndSpeed = new LastKnownLocationAndSpeed();

        lastDetectedSpeed = lastKnownLocationAndSpeed.getLastDetectedSpeed();

        lastDetectedLocation = lastKnownLocationAndSpeed.getLastDetectedLocation();

        for( DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 30 ) {
                        ContentValues cv = new ContentValues();
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, Integer.toString(DetectedActivity.IN_VEHICLE));
                        String carTramDifferentiator =  carTramDifferentiator();
                        cv.put(Contract.ActivityRecordedEntry.COL_CONFIDENCE, String.valueOf(activity.getConfidence()) + carTramDifferentiator);
                        cv.put(Contract.ActivityRecordedEntry.COL_SPEED, lastDetectedSpeed);
                        cv.put(Contract.ActivityRecordedEntry.COL_LOCATION, lastDetectedLocation);
                        addRecordToDb(cv);
                    }
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 30 ) {
                        ContentValues cv = new ContentValues();
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, Integer.toString(DetectedActivity.ON_BICYCLE));
                        cv.put(Contract.ActivityRecordedEntry.COL_CONFIDENCE, activity.getConfidence());
                        cv.put(Contract.ActivityRecordedEntry.COL_SPEED, lastDetectedSpeed);
                        cv.put(Contract.ActivityRecordedEntry.COL_LOCATION, lastDetectedLocation);
                        addRecordToDb(cv);
                    }
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 30 ) {
                        ContentValues cv = new ContentValues();
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, Integer.toString(DetectedActivity.ON_FOOT));
                        cv.put(Contract.ActivityRecordedEntry.COL_CONFIDENCE, activity.getConfidence());
                        cv.put(Contract.ActivityRecordedEntry.COL_SPEED, lastDetectedSpeed);
                        cv.put(Contract.ActivityRecordedEntry.COL_LOCATION, lastDetectedLocation);
                        addRecordToDb(cv);
                    }
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 30 ) {
                        ContentValues cv = new ContentValues();
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, Integer.toString(DetectedActivity.RUNNING));
                        cv.put(Contract.ActivityRecordedEntry.COL_CONFIDENCE, activity.getConfidence());
                        cv.put(Contract.ActivityRecordedEntry.COL_SPEED, lastDetectedSpeed);
                        cv.put(Contract.ActivityRecordedEntry.COL_LOCATION, lastDetectedLocation);
                        addRecordToDb(cv);
                    }
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 30 ) {
                        ContentValues cv = new ContentValues();
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, Integer.toString(DetectedActivity.STILL));
                        cv.put(Contract.ActivityRecordedEntry.COL_CONFIDENCE, activity.getConfidence());
                        cv.put(Contract.ActivityRecordedEntry.COL_SPEED, lastDetectedSpeed);
                        cv.put(Contract.ActivityRecordedEntry.COL_LOCATION, lastDetectedLocation);
                        addRecordToDb(cv);
                    }
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 30 ) {
                        ContentValues cv = new ContentValues();
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, Integer.toString(DetectedActivity.WALKING));
                        cv.put(Contract.ActivityRecordedEntry.COL_CONFIDENCE, activity.getConfidence());
                        cv.put(Contract.ActivityRecordedEntry.COL_SPEED, lastDetectedSpeed);
                        cv.put(Contract.ActivityRecordedEntry.COL_LOCATION, lastDetectedLocation);
                        addRecordToDb(cv);
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 30 ) {
                        ContentValues cv = new ContentValues();
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, Integer.toString(DetectedActivity.UNKNOWN));
                        cv.put(Contract.ActivityRecordedEntry.COL_CONFIDENCE, activity.getConfidence());
                        cv.put(Contract.ActivityRecordedEntry.COL_SPEED, lastDetectedSpeed);
                        cv.put(Contract.ActivityRecordedEntry.COL_LOCATION, lastDetectedLocation);
                        addRecordToDb(cv);
                    }
                    break;
                }
            }
        }
    }
}