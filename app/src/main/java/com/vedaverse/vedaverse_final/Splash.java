package com.vedaverse.vedaverse_final;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(() -> {
            Intent loginIntent = new Intent(Splash.this, Login.class);
            startActivity(loginIntent);
            overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
            finish();
        }, SPLASH_TIME_OUT);
    }
}
