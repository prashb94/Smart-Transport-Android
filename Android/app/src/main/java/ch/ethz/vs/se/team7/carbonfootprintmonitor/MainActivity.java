package ch.ethz.vs.se.team7.carbonfootprintmonitor;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.Contract;
import ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.DbHandler;
import ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.SQLQueries;
import ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.SQLQueryHelper;

import static ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.SQLQueries.CREATE_TABLE_QUERY;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // measurement interval in milliseconds
    public static final int MEASUREMENT_INTERVAL = 1000;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private SQLQueryHelper sqlHelper;

    public final int VEHICLE_TYPE_TRAM = 0;
    public final int VEHICLE_TYPE_BUS = 1;
    public final int VEHICLE_TYPE_REGIONAL_TRAIN = 2;
    public final int VEHICLE_TYPE_CABLE_CAR = 3;
    public final int VEHICLE_TYPE_BOAT = 4;
    public final int VEHICLE_TYPE_SBAHN = 5;
    public final int VEHICLE_TYPE_CAR = 6;
    public final int VEHICLE_TYPE_WALK = 7;
    public final int VEHICLE_TYPE_BIKE = 8;

    public final int TIME_DAILY = 0;
    public final int TIME_WEEKLY = 1;
    public final int TIME_MONTHLY = 2;


    private String username;
    private FloatingActionButton energyToggleButton;
    private FloatingActionButton gpsToggleButton;
    private Button dailyButton;
    private Button weeklyButton;
    private Button monthlyButton;
    private boolean energyDisplayFlag;
    private boolean gpsOn;
    private SQLiteDatabase sqLiteDatabase;
    // defines current state of time aggregation (daily, weekly, monthly)
    private int currentTimeOption;
    // table contents MainActivity
    private TextView legendTimeCO2;
    private TextView legendDistanceEnergy;
    private TextView viewWalkValue1;
    private TextView viewWalkValue2;
    private TextView viewBikeValue1;
    private TextView viewBikeValue2;
    private TextView viewCarValue1;
    private TextView viewCarValue2;
    private TextView viewTramValue1;
    private TextView viewTramValue2;

    Map<Integer, ArrayList<Double>> dataTable;

    // table contents as numbers for 1 = time and 2 = distance
    private double walkValueTime;
    private double walkValueDistance;
    private double bikeValueTime;
    private double bikeValueDistance;
    private double carValueTime;
    private double carValueDistance;
    private double tramValueTime;
    private double tramValueDistance;

    //Google Api client and Intent for location and speed
    public GoogleApiClient mApiClient;
    private LocationAndSpeed asyncUpdate;
    private PendingIntent pendingIntent;
    private Intent intent;
    private Button viewDbButton;

    int transportTypeInt;
    String transportTypeString;

    //Variables for storing CO2 emissions and Primary Energy usage.
    double cO2Emissions;
    double primaryEnergyUsage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
        // Get Intent which was started by LoginActivity and username
        Intent intent = getIntent();
        username = intent.getStringExtra(LoginActivity.EXTRA_USERNAME);
        TextView welcomeUser = findViewById(R.id.welcome_user);
        welcomeUser.setText(getResources().getString(R.string.live_green, username));

        //Initiate SQLQueryHelper
        sqlHelper = new SQLQueryHelper(MainActivity.this);

        // Initialize (CO2/energy) / (h/km) toggle button
        energyToggleButton = findViewById(R.id.fab_toggle_energy);
        energyToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity -> onClick", "energyToggleButton");
                toggleEnergyDisplay();
            }
        });

        // Initialize gps toggle button
        gpsToggleButton = findViewById(R.id.fab_gps);
        gpsToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleGps();
            }
        });

        // Initialize time changing buttons
        dailyButton = findViewById(R.id.button_day);
        dailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTimeDisplay(TIME_DAILY);
            }
        });

        weeklyButton = findViewById(R.id.button_week);
        weeklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTimeDisplay(TIME_WEEKLY);
            }
        });

        monthlyButton = findViewById(R.id.button_month);
        monthlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTimeDisplay(TIME_MONTHLY);
            }
        });

        // Initialize table TextViews
        legendTimeCO2 = findViewById(R.id.table_text_0_1);
        legendDistanceEnergy = findViewById(R.id.table_text_0_2);
        viewWalkValue1 = findViewById(R.id.table_text_1_1);
        viewWalkValue2 = findViewById(R.id.table_text_1_2);
        viewBikeValue1 = findViewById(R.id.table_text_2_1);
        viewBikeValue2 = findViewById(R.id.table_text_2_2);
        viewCarValue1 = findViewById(R.id.table_text_3_1);
        viewCarValue2 = findViewById(R.id.table_text_3_2);
        viewTramValue1 = findViewById(R.id.table_text_4_1);
        viewTramValue2 = findViewById(R.id.table_text_4_2);

        energyDisplayFlag = false;
        currentTimeOption = TIME_DAILY;
        gpsOn = false;
        gpsToggleButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
        gpsToggleButton.setImageResource(R.mipmap.ic_gps_off);
        //View Database button
        viewDbButton = findViewById(R.id.viewDbButton);
        viewDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewDatabase(view);
            }
        });

        //DEBUG
        /*
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int numberOfRecords = sharedPreferences.getInt("nRecords", 50);
        int tolerancePercentage = sharedPreferences.getInt("tolerance", 10);

        System.out.println("------------------GOT------------------\nNumberOfRecords = " + numberOfRecords + "\ntolerance = " + tolerancePercentage);
        String testQuery = "SELECT * FROM " + Contract.ActivityRecordedEntry.TABLE_NAME;
        String customSQLQuery = "SELECT COUNT(" + Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED + "), " +  Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED + ", AVG(CAST(" + Contract.ActivityRecordedEntry.COL_SPEED + " as float))" +
                " FROM ( SELECT * FROM " + Contract.ActivityRecordedEntry.TABLE_NAME + " LIMIT 100) " + " GROUP BY " + Contract.ActivityRecordedEntry.COL_ACTIVITY_RECORDED;

        DbHandler dbHandler = new DbHandler(this);
        sqLiteDatabase = dbHandler.getReadableDatabase();
        Cursor cursor =  sqLiteDatabase.rawQuery(customSQLQuery, null);
        String[] colNames = cursor.getColumnNames();
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                for(int colCount = 0; colCount < colNames.length; colCount++){
                    System.out.print(cursor.getString(cursor.getColumnIndex(colNames[colCount])) + "         ");
                }
                System.out.println("");
            }while(cursor.moveToNext());
        }
        cursor.close(); */
    }

    private void toggleEnergyDisplay() {
        if (energyDisplayFlag) {
            energyDisplayFlag = false;
            legendTimeCO2.setText(getResources().getString(R.string.legend_time));
            legendDistanceEnergy.setText(getResources().getString(R.string.legend_distance));
            refreshDataTable();
        }
        else {
            energyDisplayFlag = true;
            legendTimeCO2.setText(getResources().getString(R.string.legend_co2));
            legendDistanceEnergy.setText(getResources().getString(R.string.legend_energy));
            refreshDataTable();
        }
    }

    private void toggleGps() {
        if (gpsOn) {
            gpsOn = false;
            // set fab to red
            gpsToggleButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
            gpsToggleButton.setImageResource(R.mipmap.ic_gps_off);
            Log.d("CUR_CONTEXT","TOGGLE=OFF");
            try {
                mApiClient.disconnect();
                pendingIntent.cancel();
                asyncUpdate.cancel(true);
            } catch (Exception e){
                Log.e("CANCEL_ERR", String.valueOf(e));
            }
        }
        else {
            gpsOn = true;
            // set fab to green
            gpsToggleButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            gpsToggleButton.setImageResource(R.mipmap.ic_gps_on);
            //TODO: Monitoring flag checks
            Log.d("CUR_CONTEXT","TOGGLE=ON");
            mApiClient = new GoogleApiClient.Builder(MainActivity.this).addApi(ActivityRecognition.API).addConnectionCallbacks(MainActivity.this).addOnConnectionFailedListener(MainActivity.this).build();
            mApiClient.connect();
            asyncUpdate = new LocationAndSpeed(MainActivity.this);
            asyncUpdate.execute();

        }
    }



    private void changeTimeDisplay(int timeOption) {
        switch (timeOption) {
            case TIME_DAILY:
                currentTimeOption = TIME_DAILY;
                break;
            case TIME_WEEKLY:
                currentTimeOption = TIME_WEEKLY;
                break;
            case TIME_MONTHLY:
                currentTimeOption = TIME_MONTHLY;
                break;
            default:
                try {
                    throw new Exception("Time option not defined!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        refreshDataTable();
    }

    /** Refresh data table in MainActivity to display numbers */
    private void refreshDataTable() {
        // read out data from DB and write aggregated data
        getAggregatedValues(getDBValues());

        if (energyDisplayFlag) {
            viewWalkValue1.setText(convertToCO2(VEHICLE_TYPE_WALK, walkValueDistance));
            viewWalkValue2.setText(convertToEnergy(VEHICLE_TYPE_WALK, walkValueDistance));
            viewBikeValue1.setText(convertToCO2(VEHICLE_TYPE_BIKE, bikeValueDistance));
            viewBikeValue2.setText(convertToEnergy(VEHICLE_TYPE_BIKE, bikeValueDistance));
            viewCarValue1.setText(convertToCO2(VEHICLE_TYPE_CAR, carValueDistance));
            viewCarValue2.setText(convertToEnergy(VEHICLE_TYPE_CAR, carValueDistance));
            viewTramValue1.setText(convertToCO2(VEHICLE_TYPE_TRAM, tramValueDistance));
            viewTramValue2.setText(convertToEnergy(VEHICLE_TYPE_TRAM, tramValueDistance));
        }
        else {
            viewWalkValue1.setText(convertTime(walkValueTime));
            viewWalkValue2.setText(String.format(getResources().getString(R.string.distance_unit_m), walkValueDistance));
            viewBikeValue1.setText(convertTime(bikeValueTime));
            viewBikeValue2.setText(String.format(getResources().getString(R.string.distance_unit_m), bikeValueDistance));
            viewCarValue1.setText(convertTime(carValueTime));
            viewCarValue2.setText(String.format(getResources().getString(R.string.distance_unit_m), carValueDistance));
            viewTramValue1.setText(convertTime(tramValueTime));
            viewTramValue2.setText(String.format(getResources().getString(R.string.distance_unit_m), tramValueDistance));
        }

    }

    private String convertTime(double time) {
        int hours = (int) (time / 3600);
        int minutes = (int) (time - hours * 3600) / 60;
        return String.format("%dh%dmin", hours, minutes);
    }

    private List<List<String>> getDBValues() {
        switch (currentTimeOption) {
            case TIME_DAILY:
                return sqlHelper.getRecordsStringArray(SQLQueries.SELECT_DAILY_VALUES);
            case TIME_WEEKLY:
                return sqlHelper.getRecordsStringArray(SQLQueries.SELECT_WEEKLY_VALUES);
            case TIME_MONTHLY:
                return sqlHelper.getRecordsStringArray(SQLQueries.SELECT_MONTHLY_VALUES);
            default:
                try {
                    throw new Exception("Time option not defined in getDBVAlues!");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
        }
    }

    private void getAggregatedValues(List<List<String>> dbValues) {
        // reset values
        walkValueTime = 0;
        walkValueDistance = 0;
        bikeValueTime = 0;
        bikeValueDistance = 0;
        carValueTime = 0;
        carValueDistance = 0;
        tramValueTime = 0;
        tramValueDistance = 0;

        List<Location> walkLocations = new ArrayList<>();
        List<Location> bikeLocations = new ArrayList<>();
        List<Location> carLocations = new ArrayList<>();
        List<Location> tramLocations = new ArrayList<>();

        for (int i = 0; i < dbValues.size(); i++) {
            List<String> currentItem = dbValues.get(i);
            for (int j = 0; j < currentItem.size(); j++) {
                switch (Integer.parseInt(currentItem.get(1))) {
                    case DetectedActivity.ON_FOOT:
                    case DetectedActivity.WALKING:
                    case DetectedActivity.RUNNING:
                    case DetectedActivity.STILL:
                    case DetectedActivity.UNKNOWN:
                        walkValueTime += MEASUREMENT_INTERVAL / 1000;
                        List<String> locationWalk = Arrays.asList(currentItem.get(4).split("\\s*,\\s*"));
                        Location tempLocationWalk = new Location("");
                        tempLocationWalk.setLatitude(Double.parseDouble(locationWalk.get(0)));
                        tempLocationWalk.setLongitude(Double.parseDouble(locationWalk.get(1)));
                        walkLocations.add(tempLocationWalk);
                        break;
                    case DetectedActivity.ON_BICYCLE:
                        bikeValueTime += MEASUREMENT_INTERVAL / 1000;
                        List<String> locationBike = Arrays.asList(currentItem.get(4).split("\\s*,\\s*"));
                        Location tempLocationBike = new Location("");
                        tempLocationBike.setLatitude(Double.parseDouble(locationBike.get(0)));
                        tempLocationBike.setLongitude(Double.parseDouble(locationBike.get(1)));
                        bikeLocations.add(tempLocationBike);
                        break;
                    case DetectedActivity.IN_VEHICLE:
                        char carOrTram = currentItem.get(2).charAt(currentItem.get(2).length()-1);
                        if (carOrTram == 'R'){
                            //CAR
                            List<String> locationCar = Arrays.asList(currentItem.get(4).split("\\s*,\\s*"));
                            Location templocationCar = new Location("");
                            templocationCar.setLatitude(Double.parseDouble(locationCar.get(0)));
                            templocationCar.setLongitude(Double.parseDouble(locationCar.get(1)));
                            carLocations.add(templocationCar);
                            carValueTime += MEASUREMENT_INTERVAL / 1000;
                        }
                        else{
                            //TRAM
                            List<String> locationTram = Arrays.asList(currentItem.get(4).split("\\s*,\\s*"));
                            Location templocationTram = new Location("");
                            templocationTram.setLatitude(Double.parseDouble(locationTram.get(0)));
                            templocationTram.setLongitude(Double.parseDouble(locationTram.get(1)));
                            carLocations.add(templocationTram);
                            tramValueTime += MEASUREMENT_INTERVAL / 1000;
                        }
                        break;
                }
            }
            if (walkLocations.size() > 1) {
                for (int s = 0; s < walkLocations.size() - 1; s++) {
                    Location location1 = walkLocations.get(s);
                    Location location2 = walkLocations.get(s + 1);
                    if (location1.getLatitude() == 0.0 || location1.getLongitude() == 0.0 || location2.getLatitude() == 0.0 || location2.getLongitude() == 0.0) {
                        continue;
                    }
                    walkValueDistance += location1.distanceTo(location2);
                }
            }
            // ToDo: Add all locations (distances of all means of transport)
        }


    }


    //Methods to convert kilometer and transport type to Primary Energy consumption
    //and CO2 emissions.
    private String convertToEnergy(int transportType, double distance) {
        /*0 = tram
        * 1 = local bus
        * 2 = regional train
        * 3 = cable car
        * 4 = boat
        * 5 = S-Bahn
        * Comment: We can add more vehicles later on.
        *
        * Primary Energy consumption is measured in ml gasoline/pkm.
        * BUT, now multiplies by 34.2 to get it converted into MJ.
        * */
        double energyConsumption;
        switch (transportType) {
            case 0: energyConsumption = distance * 33.5; //Tram
                break;
            case 1: energyConsumption = distance * 47.2; //Local Bus
                break;
            case 2: energyConsumption = distance * 35.9; //Regional Train
                break;
            case 3: energyConsumption = distance * 118.0; //Cable car
                break;
            case 4: energyConsumption = distance * 51.4; //Boat
                break;
            case 5: energyConsumption = distance * 20.4; //S-bahn
                break;
            case 6: energyConsumption = distance * 100.9; //Car
                break;
            // For walk and bike
            default: energyConsumption = 0;
                break;
        }
        //Multiplies to get answer in MJ, instead of ml gasoline/km.
        double result = Math.round((energyConsumption * 34.2 * 0.001) * 100) / 100;
        return String.format(getResources().getString(R.string.energy_unit_MJ), result);
        // 34.2? -> Energy density of gasoline
    }

    private String convertToCO2(int transportType, double distance) {
        // CO2 g/pkm
        double co2Consumption;
        switch (transportType) {
            case 0: co2Consumption = distance * 24.9; //Tram
                break;
            case 1: co2Consumption = distance * 100.3; //Local Bus
                break;
            case 2: co2Consumption = distance * 9.7; //Regional Train
                break;
            case 3: co2Consumption = distance * 63.9; //Cable car
                break;
            case 4: co2Consumption = distance * 119.2; //Cable car
                break;
            case 5: co2Consumption = distance * 8.2; //S-bahn
                break;
            case 6: co2Consumption = distance * 201.0; //Car
                break;
            // For walk and bike
            default: co2Consumption= 0;
                break;
        }
        double result = Math.round(co2Consumption * 100) / 100;
        return String.format(getResources().getString(R.string.co2_unit_g), result);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        intent = new Intent(MainActivity.this, ActivityRecognizedService.class);
        pendingIntent = PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("CUR_CONTEXT","CREATE_PENDING_INTENT");
        //Granularity of activity updates = 1000. TODO: Add settings option to set granularity
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, MEASUREMENT_INTERVAL, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void viewDatabase(View view) {
        Context thisActivity = MainActivity.this;
        Class destinationActivity = ShowRecords.class;
        Intent intent = new Intent(thisActivity, destinationActivity);
        startActivity(intent);
    }

    //Menu for shared preferences
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}