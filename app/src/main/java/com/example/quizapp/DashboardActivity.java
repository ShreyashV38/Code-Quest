package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.models.Question;
import com.example.quizapp.models.User;
import com.example.quizapp.repository.QuizRepository;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.Serializable;
import java.util.HashMap; // <-- ADD THIS LINE
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    // --- Views ---
    private Spinner languageSpinner;
    private View profileIcon;
    private TextView userNameText;
    private MaterialCardView easyCard, mediumCard, hardCard, gateCard, gkCard;
    private ProgressBar easyProgressBar, mediumProgressBar, hardProgressBar;
    private TextView easyProgress, mediumProgress, hardProgress;
    private View loadingOverlay; // For API calls

    // --- Firebase & Repository ---
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private QuizRepository quizRepository;
    private User currentUser;

    // --- State ---
    private String selectedLanguage = "Java"; // Default language
    private final int QUESTIONS_PER_LEVEL = 50; // Target for progress calculation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase & Repository
        mAuth = FirebaseAuth.getInstance();
        quizRepository = new QuizRepository();

        // Check login status and get DB reference
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            goToAuthActivity(); // Redirect if not logged in
            return;
        }
        mUserDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        // Setup UI
        initializeViews();
        setupLanguageSpinner(); // Sets up adapter and listener
        setupClickListeners();
        loadUserData(); // Fetches user data and updates UI
    }

    private void initializeViews() {
        languageSpinner = findViewById(R.id.languageSpinner);
        profileIcon = findViewById(R.id.profileButton);
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
        loadingOverlay = findViewById(R.id.loadingOverlay); // Make sure this ID exists in XML
        loadingOverlay.setVisibility(View.GONE); // Hide initially
    }

    private void setupLanguageSpinner() {
        String[] languages = {"C", "C++", "Python", "Java", "JavaScript"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Set initial selection and update tracked variable
        int initialSelection = 3; // Java
        languageSpinner.setSelection(initialSelection);
        selectedLanguage = languages[initialSelection];

        // Add listener to update progress when language changes
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newLanguage = parent.getItemAtPosition(position).toString();
                if (!newLanguage.equals(selectedLanguage)) {
                    selectedLanguage = newLanguage;
                    updateProgressBars(); // Update UI when language changes
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {} // Do nothing
        });
    }

    private void setupClickListeners() {
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        // Set listeners for each card to call startQuiz
        easyCard.setOnClickListener(v -> startQuiz("Easy"));
        mediumCard.setOnClickListener(v -> startQuiz("Medium"));
        hardCard.setOnClickListener(v -> startQuiz("Hard"));
        gateCard.setOnClickListener(v -> startQuiz("GATE"));
        gkCard.setOnClickListener(v -> startQuiz("GK"));
    }

    private void startQuiz(String difficulty) {
        // Use the tracked 'selectedLanguage' variable
        String language = (difficulty.equals("GK")) ? null : selectedLanguage;
        int limit = 10; // Number of questions per quiz

        String message = "Loading " + difficulty + " quiz" +
                (language != null ? " for " + language : "") + "...";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        setLoading(true); // Show loading overlay

        // Call the repository to get questions
        quizRepository.getQuestions(difficulty, language, limit,
                new QuizRepository.QuestionsCallback() {
                    @Override
                    public void onSuccess(List<Question> questions) {
                        setLoading(false); // Hide loading overlay
                        if (questions == null || questions.isEmpty()) {
                            Toast.makeText(DashboardActivity.this,
                                    "No questions found for " +
                                            (language != null ? language + " - " : "") + difficulty + ".",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Start QuizActivity with the fetched questions
                        Intent intent = new Intent(DashboardActivity.this, QuizActivity.class);
                        intent.putExtra("difficulty", difficulty);
                        intent.putExtra("language", language != null ? language : "General"); // Pass language name
                        intent.putExtra("questions", (Serializable) questions); // Pass question list
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String message) {
                        setLoading(false); // Hide loading overlay
                        Toast.makeText(DashboardActivity.this,
                                "Error loading questions: " + message,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadUserData() {
        if (mUserDatabase == null) return; // Should not happen if logged in

        // Use addListenerForSingleValueEvent to fetch data once
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class); // Deserialize into User object
                if (currentUser != null) {
                    userNameText.setText("Hey " + currentUser.getUsername() + " 👋");
                    updateProgressBars(); // Update UI after fetching data
                } else {
                    // Handle case where user data might be missing in DB
                    Toast.makeText(DashboardActivity.this,
                            "Failed to load user profile data.",
                            Toast.LENGTH_SHORT).show();
                    // Maybe set default progress?
                    updateProgressBars(); // Call even if user is null to set defaults
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this,
                        "Database error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                updateProgressBars(); // Set defaults on error
            }
        });
    }

    private void updateProgressBars() {
        // Handle case where user data might not be loaded yet or is null
        Map<String, Integer> countMap = (currentUser != null)
                ? currentUser.getCorrectlyAnsweredCountMap()
                : new HashMap<>(); // Use empty map if no user data

        // Construct keys based on the currently selected language
        String easyKey = selectedLanguage + "_Easy_CorrectCount";
        String mediumKey = selectedLanguage + "_Medium_CorrectCount";
        String hardKey = selectedLanguage + "_Hard_CorrectCount";

        // Get the count of correctly answered questions for each level
        int easyCount = countMap.getOrDefault(easyKey, 0);
        int mediumCount = countMap.getOrDefault(mediumKey, 0);
        int hardCount = countMap.getOrDefault(hardKey, 0);

        // Calculate percentage based on target number of questions per level
        int easyPercent = (int) Math.round(((double) easyCount / QUESTIONS_PER_LEVEL) * 100);
        int mediumPercent = (int) Math.round(((double) mediumCount / QUESTIONS_PER_LEVEL) * 100);
        int hardPercent = (int) Math.round(((double) hardCount / QUESTIONS_PER_LEVEL) * 100);

        // Ensure percentage is within 0-100 range
        easyPercent = Math.max(0, Math.min(100, easyPercent));
        mediumPercent = Math.max(0, Math.min(100, mediumPercent));
        hardPercent = Math.max(0, Math.min(100, hardPercent));

        // Update the ProgressBar and TextView for each level
        easyProgressBar.setProgress(easyPercent);
        easyProgress.setText(easyPercent + "%");

        mediumProgressBar.setProgress(mediumPercent);
        mediumProgress.setText(mediumPercent + "%");

        hardProgressBar.setProgress(hardPercent);
        hardProgress.setText(hardPercent + "%");
    }

    private void setLoading(boolean isLoading) {
        // Show or hide the loading overlay
        loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void goToAuthActivity() {
        // Navigate back to login/signup screen
        Intent intent = new Intent(this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finish this activity so user can't go back to it
    }

    // No need for onResume to call loadUserData if spinner listener handles updates
    // and initial load happens in onCreate. If you want to refresh every time
    // the screen becomes visible, uncomment this.
    // @Override
    // protected void onResume() {
    //     super.onResume();
    //     // Reload user data in case progress was updated elsewhere
    //     if (mUserDatabase != null) {
    //         loadUserData();
    //     }
    // }
}