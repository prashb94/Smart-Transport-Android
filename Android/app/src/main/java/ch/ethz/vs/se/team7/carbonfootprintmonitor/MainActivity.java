package ch.ethz.vs.se.team7.carbonfootprintmonitor;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;


public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    public GoogleApiClient mApiClient;

    // debugging text views
    private TextView tvDetectedActivity;
    private TextView tvLocation;
    private TextView tvSpeed;

    private String username;
    private FloatingActionButton energyToggleButton;
    private FloatingActionButton gpsToggleButton;
    private boolean energyDisplayFlag;
    private boolean gpsOn;

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
        energyToggleButton = findViewById(R.id.fab_toggle_energy);
        energyToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        gpsOn = true;
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

    private void toggleGps() {
        if (gpsOn) {
            gpsOn = false;
            //ToDo: toggle off gps
            // set fab to red
            gpsToggleButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
            gpsToggleButton.setImageResource(R.mipmap.ic_gps_off);
        }
        else {
            gpsOn = true;
            //ToDo: toggle on gps
            // set fab to green
            gpsToggleButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            gpsToggleButton.setImageResource(R.mipmap.ic_gps_on);
        }
    }
}