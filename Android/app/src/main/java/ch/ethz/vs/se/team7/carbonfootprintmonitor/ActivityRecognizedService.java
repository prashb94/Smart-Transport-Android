package ch.ethz.vs.se.team7.carbonfootprintmonitor;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.Contract;
import ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.DbHandler;

public class ActivityRecognizedService extends IntentService {

    private DbHandler dbAdapter;

    private SQLiteDatabase activityRecordedDb;

    private LastKnownLocationAndSpeed lastKnownLocationAndSpeed;

    private String lastDetectedSpeed;

    private String lastDetectedLocation;

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
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, "Vehicle");
                        cv.put(Contract.ActivityRecordedEntry.COL_CONFIDENCE, activity.getConfidence());
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
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, "Bicycle");
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
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, "OnFoot");
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
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, "Running");
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
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, "StandingStill");
                        cv.put(Contract.ActivityRecordedEntry.COL_CONFIDENCE, activity.getConfidence());
                        cv.put(Contract.ActivityRecordedEntry.COL_SPEED, lastDetectedSpeed);
                        cv.put(Contract.ActivityRecordedEntry.COL_LOCATION, lastDetectedLocation);
                        addRecordToDb(cv);
                    }
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 30 ) {
                        ContentValues cv = new ContentValues();
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, "Tilting");
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
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, "Walking");
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
                        cv.put(Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED, "Unknown");
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