package ch.ethz.vs.se.team7.carbonfootprintmonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.app.AlertDialog;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;

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

    //alertDialog for Transport Types
    Button vehicle;
    String[] listOfTransportTypes;
    int transportTypeInt;
    String transportTypeString;

    //Variables for storing CO2 emissions and Primary Energy usage.
    double cO2Emissions;
    double primaryEnergyUsage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Intent which was started by LoginActivity and username
        Intent intent = getIntent();
        username = intent.getStringExtra(LoginActivity.EXTRA_USERNAME);
        TextView welcomeUser = findViewById(R.id.welcome_user);
        welcomeUser.setText(getResources().getString(R.string.live_green, username));

        //AlertDialog. How often should we be able to ask this question?
        vehicle = (Button) findViewById(R.id.vehicleDialogBtn);

        //AlertDialog Items.
        listOfTransportTypes = getResources().getStringArray(R.array.transport_types);



        //Button for AlertDialog, where current vehicle is chosen.
        vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creates the AlertDialog.
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("What vehicle are you using?");
                builder.setSingleChoiceItems(listOfTransportTypes, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int chosenTranportType) {
                        //String is not needed as our convertion method uses INT.
                        transportTypeString = listOfTransportTypes[chosenTranportType];
                        transportTypeInt = chosenTranportType;

                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder typeStringBuilder = new StringBuilder();
                        typeStringBuilder.append(transportTypeString);
                        typeStringBuilder.append(" Selected");

                        //Just to make it clear what was picked, we show the user what he vehicle he
                        // picked using a Toast(small message pop-up message in android.
                        Toast.makeText(getApplicationContext(), typeStringBuilder, Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        // Initialize debugging textViews
        tvSpeed = findViewById(R.id.textView_speed);
        tvLocation = findViewById(R.id.textView_location);

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


    //Methods to convert kilometer and transport type to Primary Energy consumtion
    //and CO2 emissions.
    private double convertToEnergy(int transportType, double distance) {
        /*0 = tram
        * 1 = local bus
        * 2 = regional train
        * 3 = cable car
        * 4 = boat
        * 5 = S-Bahn
        * Comment: We can add more veicles later on.
        *
        * Primary Energy consumtion is measured in ml gasonline/pkm.
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
            //Should have try catch here?
            default: energyConsumption = 0;
                break;
        }
        return Math.round((energyConsumption * 34.2 * 0.001) * 100) / 100; //Multiplies to get answer in MJ, instead of ml gasoline/km.
        // 34.2? -> Energy density of gasoline
    }
    private double convertToCO2(int transportType, double distance) {
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
            //Should have try catch here?
            default: co2Consumption= 0;
                break;
        }
        return Math.round(co2Consumption * 100) / 100;
    }

}