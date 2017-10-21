package ch.ethz.vs.se.team7.carbonfootprintmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Swe on 20.10.2017.
 * Login activity to get username
 */

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "ch.ethz.vs.se.team7.carbonfootprintmonitor.USERNAME";

    private FloatingActionButton loginButton;

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
