package ch.ethz.vs.se.team7.carbonfootprintmonitor;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String EXTRA_USERNAME = "ch.ethz.vs.se.team7.carbonfootprintmonitor.USERNAME";

    //public static int enableMonitoring = 1;

    private FloatingActionButton loginButton;

    public GoogleApiClient mApiClient;

    private Button viewDbButton;

    private Intent intent;

    private PendingIntent pendingIntent;

    //private WriteToDatabase startMonitoring;

    private LocationAndSpeed asyncUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Sign In Button
        loginButton = findViewById(R.id.button_sign_in);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginToMain(view);
            }
        });

        //View Database button
        viewDbButton = findViewById(R.id.viewDbButton);
        viewDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewDatabase(view);
            }
        });
        //startMonitoring = new WriteToDatabase();
        asyncUpdate = new LocationAndSpeed(this);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleMonitoring);
        toggle.setBackgroundColor(Color.GRAY);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    //TODO: Monitoring flag checks
                    Log.d("CUR_CONTEXT","TOGGLE=ON");
                    mApiClient = new GoogleApiClient.Builder(LoginActivity.this).addApi(ActivityRecognition.API).addConnectionCallbacks(LoginActivity.this).addOnConnectionFailedListener(LoginActivity.this).build();
                    mApiClient.connect();
                    asyncUpdate.execute();
                    //startMonitoring.execute(1);
                } else {
                    // The toggle is disabled
                    Log.d("CUR_CONTEXT","TOGGLE=OFF");
                    mApiClient.disconnect();
                    pendingIntent.cancel();
                    asyncUpdate.cancel(true);
                    //startMonitoring.cancel(true);
                }
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        intent = new Intent(LoginActivity.this, ActivityRecognizedService.class);
        pendingIntent = PendingIntent.getService(LoginActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("CUR_CONTEXT","CREATE_PENDING_INTENT");
        //Granularity of activity updates = 1000. TODO: Add settings option to set granularity
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 100, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    // Opens MainActivity and passes username
    public void loginToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        EditText usernameText = findViewById(R.id.edit_name);
        String username = usernameText.getText().toString();
        intent.putExtra(EXTRA_USERNAME, username);
        startActivity(intent);
    }

    public void viewDatabase(View view) {
        Context thisActivity = LoginActivity.this;
        Class destinationActivity = ShowRecords.class;
        Intent intent = new Intent(thisActivity, destinationActivity);
        startActivity(intent);
    }


    /*
    public class WriteToDatabase extends AsyncTask<Integer, Void, Void> implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

        private Intent intent;

        private PendingIntent pendingIntent;


        @Override
        protected Void doInBackground(Integer... integers) {
            System.out.println("ASYNC TASK STARTED IN BACKGROUND!");
            if(isCancelled()){
                System.out.println("ASYNCTASK ISCANCELLED! DISCONNECTING GOOGLE API CLIENT!");
                mApiClient.disconnect();
                pendingIntent.cancel();
            }
            int enableMonitoringFlag = integers[0].intValue();
            if(enableMonitoringFlag == 1 && !isCancelled()) {
                System.out.println("GOOGLE API CLIENT CREATED IN BACKGROUND! FLAG = " + enableMonitoringFlag);
                mApiClient = new GoogleApiClient.Builder(LoginActivity.this).addApi(ActivityRecognition.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
                mApiClient.connect();
            }
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            intent = new Intent(LoginActivity.this, ActivityRecognizedService.class);
            pendingIntent = PendingIntent.getService(LoginActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            System.out.println("CREATING PENDING INTENT FOR GOOGLEAPI CLIENT NOW!");
            //Granularity of activity updates = 1000. TODO: Add settings option to set granularity
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 100, pendingIntent);
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

    }
    */


}
