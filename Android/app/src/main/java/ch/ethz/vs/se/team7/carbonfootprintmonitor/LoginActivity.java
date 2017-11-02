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


public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "ch.ethz.vs.se.team7.carbonfootprintmonitor.USERNAME";

    private FloatingActionButton loginButton;

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


    }
    // Opens MainActivity and passes username
    public void loginToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        EditText usernameText = findViewById(R.id.edit_name);
        String username = usernameText.getText().toString();
        intent.putExtra(EXTRA_USERNAME, username);
        startActivity(intent);
    }



}
