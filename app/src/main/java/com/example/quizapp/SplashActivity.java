package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // You don't need setContentView() if you're using the Android 12+ splash theme.
        // But if you want a custom layout (like text or animation), you can uncomment below:
         setContentView(R.layout.activity_splash);

        // Delay for 2 seconds, then move to AuthActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
            startActivity(intent);
            finish(); // prevent going back to splash
        }, SPLASH_DURATION);
    }
}
