package ch.ethz.vs.se.team7.carbonfootprintmonitor;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LocationManager locationManager;
    private LocationListener locationListener;
    public GoogleApiClient mApiClient;

    // debugging text views
    private TextView tvDetectedActivity;
    private TextView tvLocation;
    private TextView tvSpeed;

    private String username;
    private FloatingActionButton energyToggleButton;
    private boolean energyDisplayFlag;

    // table contents MainActivity
    private TextView legendTimeCO2;
    private TextView legendDistanceEnergy;
    private TextView walkValue1;
    private TextView walkValue2;
    private TextView bikeValue1;
    private TextView bikeValue2;
    private TextView carValue1;
    private TextView carValue2;
    private TextView tramValue1;
    private TextView tramValue2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Intent which was started by LoginActivity and username
        Intent intent = getIntent();
        username = intent.getStringExtra(LoginActivity.EXTRA_USERNAME);
        TextView welcomeUser = findViewById(R.id.welcome_user);
        welcomeUser.setText(getResources().getString(R.string.live_green, username));

        // Initialize debugging textViews
        tvSpeed = findViewById(R.id.textView_speed);
        tvLocation = findViewById(R.id.textView_location);

        // Initialize (CO2/energy) / (h/km) toggle button
        energyToggleButton = findViewById(R.id.button_toggle_energy);
        energyToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEnergyDisplay();
            }
        });

        // Initialize table TextViews
        legendTimeCO2 = findViewById(R.id.table_text_0_1);
        legendDistanceEnergy = findViewById(R.id.table_text_0_2);
        walkValue1 = findViewById(R.id.table_text_1_1);
        walkValue2 = findViewById(R.id.table_text_1_2);
        bikeValue1 = findViewById(R.id.table_text_2_1);
        bikeValue2 = findViewById(R.id.table_text_2_2);
        carValue1 = findViewById(R.id.table_text_3_1);
        carValue2 = findViewById(R.id.table_text_3_2);
        tramValue1 = findViewById(R.id.table_text_4_1);
        tramValue2 = findViewById(R.id.table_text_4_2);

        energyDisplayFlag = false;



        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        tvDetectedActivity = findViewById(R.id.textViewDetectedActivity);
        mApiClient.connect();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                tvLocation.setText(location.getLatitude() + ", " + location.getLongitude());
                tvSpeed.setText(getResources().getString(R.string.speed_unit_ms, location.getSpeed()));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
                return;
            } else {
                locationManager.requestLocationUpdates("gps", 0, 0, locationListener);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 1000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates("gps", 0, 0, locationListener);
                    return;
                }
        }
    }

    private void toggleEnergyDisplay() {
        //ToDo: switch actual values in text views
        if (energyDisplayFlag) {
            energyDisplayFlag = false;
            legendTimeCO2.setText(getResources().getString(R.string.legend_time));
            legendDistanceEnergy.setText(getResources().getString(R.string.legend_distance));
        }
        else {
            energyDisplayFlag = true;
            legendTimeCO2.setText(getResources().getString(R.string.legend_co2));
            legendDistanceEnergy.setText(getResources().getString(R.string.legend_energy));
        }
    }
}