package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    private ImageView backBtn;
    private TextView profileName, profileEmail;
    private TextView statQuizzesTaken, statAvgScore, statBestScore;
    private MaterialButton editProfileBtn, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        setupListeners();
        loadProfileData();
    }

    private void initializeViews() {
        backBtn = findViewById(R.id.backBtn);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        statQuizzesTaken = findViewById(R.id.statQuizzesTaken);
        statAvgScore = findViewById(R.id.statAvgScore);
        statBestScore = findViewById(R.id.statBestScore);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
    }

    private void setupListeners() {
        backBtn.setOnClickListener(v -> finish());

        editProfileBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Profile (Not Implemented)", Toast.LENGTH_SHORT).show();
        });

        logoutBtn.setOnClickListener(v -> {
            // TODO: Clear user session (SharedPreferences, Firebase Auth, etc.)

            // Navigate to AuthActivity and clear all previous activities
            Intent intent = new Intent(ProfileActivity.this, AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadProfileData() {
        // TODO: Load this data from SharedPreferences or a user database

        // Load user info
        profileName.setText("Coder"); // Sample data
        profileEmail.setText("coder@example.com"); // Sample data

        // Load stats
        statQuizzesTaken.setText("12"); // Sample data
        statAvgScore.setText("78%"); // Sample data
        statBestScore.setText("95%"); // Sample data
    }
}