package ch.ethz.vs.se.team7.carbonfootprintmonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.app.Dialog;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity{

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

    //alertDialog for Transport Types
    Button vehicle;
    String[] listOfTransportTypes;
    int transportTypeInt;
    String transportTypeString;

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



//***************************************************************
        vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        typeStringBuilder.append(" selected");
                        Toast.makeText(getApplicationContext(), typeStringBuilder, Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });




 //**************************************************************/

        // Initialize (CO2/energy) / (h/km) toggle button
        energyToggleButton = findViewById(R.id.button_toggle_energy);
        energyToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity -> onClick", "energyToggleButton");
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

    }

    private void toggleEnergyDisplay() {
        //TODO: switch actual values in text views
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
            //Should have try catch here?
            default: energyConsumption = 0;
                break;
        }
        return energyConsumption;
    }
    private double convertToCO2(int transportType, double distance) {
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
            //Should have try catch here?
            default: co2Consumption= 0;
                break;
        }
        return co2Consumption;
    }

}