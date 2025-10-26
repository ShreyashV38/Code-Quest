package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.api.RetrofitClient; // Import RetrofitClient
import com.example.quizapp.models.User; // Import User model
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ImageView backBtn;
    private TextView profileName, profileEmail;
    private TextView statQuizzesTaken, statAvgScore, statBestScore;
    private MaterialButton editProfileBtn, logoutBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            goToAuthActivity();
            return;
        }
        mUserDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

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
        progressBar = findViewById(R.id.profileProgressBar); // Add this ID to your XML
    }

    private void setupListeners() {
        backBtn.setOnClickListener(v -> finish());

        editProfileBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Profile (Not Implemented)", Toast.LENGTH_SHORT).show();
        });

        logoutBtn.setOnClickListener(v -> {
            // Sign out from Firebase
            mAuth.signOut();

            // Note: Your RetrofitClient is designed to get a fresh token
            // for each request, so no client-side clearing is needed.

            // Navigate to AuthActivity and clear all previous activities
            goToAuthActivity();
        });
    }

    private void loadProfileData() {
        setLoading(true);
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                if (currentUser != null) {
                    profileName.setText(currentUser.getUsername());
                    profileEmail.setText(currentUser.getEmail());

                    statQuizzesTaken.setText(String.valueOf(currentUser.getTotalQuizzes()));

                    // Calculate Avg Score
                    if (currentUser.getTotalQuizzes() > 0) {
                        double avgScore = (double) currentUser.getTotalScore() / currentUser.getTotalQuizzes();
                        // Assuming totalScore is sum of percentages
                        statAvgScore.setText(String.format(Locale.getDefault(), "%.0f%%", avgScore));
                    } else {
                        statAvgScore.setText("0%");
                    }

                    // TODO: You need to add 'bestScore' to your User model
                    // and update it in Firebase to show it here.
                    statBestScore.setText("0%"); // Placeholder
                }
                setLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setLoading(false);
                Toast.makeText(ProfileActivity.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void goToAuthActivity() {
        Intent intent = new Intent(ProfileActivity.this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}