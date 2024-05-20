package com.vedaverse.vedaverse_final;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(() -> {
            // Check if the user is already authenticated
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                // User is already authenticated, move to Home activity
                Intent homeIntent = new Intent(Splash.this, Home.class);
                startActivity(homeIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            } else {
                // User is not authenticated, move to Login activity
                Intent loginIntent = new Intent(Splash.this, Login.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
            finish();
        }, SPLASH_TIME_OUT);
    }
}
