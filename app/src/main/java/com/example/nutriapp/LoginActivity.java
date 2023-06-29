package com.example.nutriapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignUp;
    // Create an instance of the DatabaseHelper
    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_login);

        // Initialize views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        // Create an instance of the DatabaseHelper
        databaseHelper = new DatabaseHelper(this);


        // Check if user is already logged in
        if (isLoggedIn()) {
            // User is already logged in, proceed to next activity
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }


        // Set click listener for login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // Set click listener for sign up text
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the registration page
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
    }

    boolean isLoggedIn() {
        return databaseHelper.getLastValidSessionUsername() != null;
    }

    private void login() {
        // Get the entered username and password
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        User user = new User(username, password);

        // Check if the entered username and password match the values in the database
        int validUserID =  databaseHelper.getCurrentSessionUserId(user);

        if (validUserID != -1) {
            User userSession = new User(validUserID);
            databaseHelper.createSession(userSession);

            CurrentUser currentUser = CurrentUser.getInstance();
            currentUser.setUser(userSession);

            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            finish();
        } else {
            // Invalid username or password, show an error message
            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private String hashPassword(String password) {
        // Generate a salt for bcrypt
        String salt = BCrypt.gensalt();

        // Hash the password using bcrypt
        return BCrypt.hashpw(password, salt);
    }
}

