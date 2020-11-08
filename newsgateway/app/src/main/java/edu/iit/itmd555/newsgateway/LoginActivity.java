package edu.iit.itmd555.newsgateway;

// list of imports
import android.content.Intent; // it provides the facility for performing late runtime binding between the code of applications
import android.content.SharedPreferences; // it provides the interface for accessing and modifying preference data
import android.graphics.Color; // it provides the methods for creating, converting and manipulating colors
import android.os.Bundle;
import android.util.Log;
import android.view.View; // it represents the basic building of the android app components
import android.widget.Button; // it provides the clickable button to the user to perform a action
import android.widget.CheckBox; // it provides the resources to add a checkbox
import android.widget.EditText; // provides the UI elements for entering and modifying text
import android.widget.TextView; // it provides the user-editable text
import android.widget.Toast; // it provides a quick message for the user
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


// LoginActivity class starts here
public class LoginActivity extends AppCompatActivity {

    // UI view variables declaration
    private Button loginButton,cancelButton;
    private EditText usernameEditText,passwordEditText;
    private CheckBox checkBoxRememberMe;
    private SharedPreferences mPrefs;
    private static final String PREFS_NAME = "PrefsFile";

    private TextView attemptsLeft;
    private int attemptsCounter = 3;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 0;


    // onCreate method starts here
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        cancelButton = findViewById(R.id.cancelButton);
        attemptsLeft = findViewById(R.id.loginAttemptsLeft);
        checkBoxRememberMe = findViewById(R.id.rememberMeCheckBox);
        //Initializing Views
        signInButton = findViewById(R.id.sign_in_button);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                signIn();
                                            }
                                        });

        mPrefs = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        getPreferencesData();

        // login Button functionality defined below
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameEditText.getText().toString().equals("admin") && passwordEditText.getText().toString().equals("admin"))
                {
                    // Remember Me logic
                    if(checkBoxRememberMe.isChecked()) {
                        Boolean boolIsChecked = checkBoxRememberMe.isChecked();
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putString("pref_name",usernameEditText.getText().toString());
                        editor.putString("pref_pass",passwordEditText.getText().toString());
                        editor.putBoolean("pref_check",boolIsChecked);
                        editor.apply();
                        Toast.makeText(getApplicationContext(),"Login Credientials saved!",Toast.LENGTH_LONG).show();

                    } else {
                        mPrefs.edit().clear().apply();
                    }
                    Toast.makeText(getApplicationContext(), "Redirecting...",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
                else{
                    attemptsCounter--;
                    Toast.makeText(getApplicationContext(), "Invalid username or password!!! Try again.",Toast.LENGTH_LONG).show();

                    attemptsLeft.setText("No. of Attempts left: "+String.valueOf(attemptsCounter));
                    attemptsLeft.setTextColor(Color.YELLOW);

                    // Disabling login button
                    if (attemptsCounter == 0) {
                        loginButton.setEnabled(false);
                        attemptsLeft.setTextColor(Color.RED);

                    }
                }
            }
        });

        // Cancel button functionality defined below
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }// onCreate method ends here

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

  /*  @Override
    protected void onStart() {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
        }
        super.onStart();
    }
*/


    // getPreferencesData method starts here
    private void getPreferencesData() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (sp.contains("pref_name")) {
            String u = sp.getString("pref_name", "not found.");
            usernameEditText.setText(u.toString());
        }
        if (sp.contains("pref_pass")) {
            String p = sp.getString("pref_pass", "not found.");
            passwordEditText.setText(p.toString());
        }
        if (sp.contains("pref_check")) {
            Boolean b = sp.getBoolean("pref_check", false);
            checkBoxRememberMe.setChecked(b);
        }
    } //getPreferencesData method ends here
}// LoginActivity class starts here

