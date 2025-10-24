package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class DashboardActivity extends AppCompatActivity {

    private Spinner languageSpinner;
    private ImageView profileIcon;
    private TextView userNameText;
    private MaterialCardView easyCard, mediumCard, hardCard, gateCard, gkCard;
    private ProgressBar easyProgressBar, mediumProgressBar, hardProgressBar;
    private TextView easyProgress, mediumProgress, hardProgress;

    // Progress tracking (in real app, fetch from database/SharedPreferences)
    private int easyProgressValue = 0;
    private int mediumProgressValue = 0;
    private int hardProgressValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        initializeViews();

        // Setup language spinner
        setupLanguageSpinner();

        // Setup click listeners
        setupClickListeners();

        // Load user progress (from SharedPreferences or database)
        loadUserProgress();
    }

    private void initializeViews() {
        languageSpinner = findViewById(R.id.languageSpinner);
        profileIcon = findViewById(R.id.profileIcon);
        userNameText = findViewById(R.id.userNameText);

        easyCard = findViewById(R.id.easyCard);
        mediumCard = findViewById(R.id.mediumCard);
        hardCard = findViewById(R.id.hardCard);
        gateCard = findViewById(R.id.gateCard);
        gkCard = findViewById(R.id.gkCard);

        easyProgressBar = findViewById(R.id.easyProgressBar);
        mediumProgressBar = findViewById(R.id.mediumProgressBar);
        hardProgressBar = findViewById(R.id.hardProgressBar);

        easyProgress = findViewById(R.id.easyProgress);
        mediumProgress = findViewById(R.id.mediumProgress);
        hardProgress = findViewById(R.id.hardProgress);
    }

    private void setupLanguageSpinner() {
        // Create language options
        String[] languages = {"C", "C++", "Python", "Java", "JavaScript"};

        // Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                languages
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set adapter to spinner
        languageSpinner.setAdapter(adapter);

        // Default selection (Java)
        languageSpinner.setSelection(3);
    }

    private void setupClickListeners() {
        // Profile icon click
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        // Easy level click
        easyCard.setOnClickListener(v -> {
            String selectedLanguage = languageSpinner.getSelectedItem().toString();
            startQuiz("Easy", selectedLanguage);
        });

        // Medium level click
        mediumCard.setOnClickListener(v -> {
            String selectedLanguage = languageSpinner.getSelectedItem().toString();
            startQuiz("Medium", selectedLanguage);
        });

        // Hard level click
        hardCard.setOnClickListener(v -> {
            String selectedLanguage = languageSpinner.getSelectedItem().toString();
            startQuiz("Hard", selectedLanguage);
        });

        // GATE click
        gateCard.setOnClickListener(v -> {
            String selectedLanguage = languageSpinner.getSelectedItem().toString();
            startQuiz("GATE", selectedLanguage);
        });

        // General Knowledge click
        gkCard.setOnClickListener(v -> {
            // GK doesn't need language selection
            startQuiz("GK", "General");
        });
    }

    private void startQuiz(String difficulty, String language) {
        // TODO: Start quiz activity with selected difficulty and language
        String message = "Starting " + difficulty + " quiz in " + language;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("language", language);
        startActivity(intent);
    }

    private void loadUserProgress() {
        // TODO: Load from SharedPreferences or Database
        // For now, using sample data

        // Get stored values (replace with actual data retrieval)
        easyProgressValue = getProgressFromStorage("easy");
        mediumProgressValue = getProgressFromStorage("medium");
        hardProgressValue = getProgressFromStorage("hard");

        // Update UI
        updateProgressBars();
    }

    private int getProgressFromStorage(String level) {
        // TODO: Implement actual storage retrieval
        // Example using SharedPreferences:
        // SharedPreferences prefs = getSharedPreferences("QuizApp", MODE_PRIVATE);
        // return prefs.getInt(level + "_progress", 0);

        // Sample data for now
        return 0;
    }

    private void updateProgressBars() {
        // Update Easy level
        easyProgressBar.setProgress(easyProgressValue);
        easyProgress.setText(easyProgressValue + "%");

        // Update Medium level
        mediumProgressBar.setProgress(mediumProgressValue);
        mediumProgress.setText(mediumProgressValue + "%");

        // Update Hard level
        hardProgressBar.setProgress(hardProgressValue);
        hardProgress.setText(hardProgressValue + "%");
    }

    public void saveProgress(String level, int progress) {
        // TODO: Save to SharedPreferences or Database
        // Example:
        // SharedPreferences prefs = getSharedPreferences("QuizApp", MODE_PRIVATE);
        // prefs.edit().putInt(level + "_progress", progress).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload progress when returning to dashboard
        loadUserProgress();
    }
}