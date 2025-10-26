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
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private Spinner languageSpinner;
    private ImageView profileIcon;
    private TextView userNameText;
    private MaterialCardView easyCard, mediumCard, hardCard, gateCard, gkCard;
    private ProgressBar easyProgressBar, mediumProgressBar, hardProgressBar;
    private TextView easyProgress, mediumProgress, hardProgress;
    private View loadingOverlay; // Loading overlay for API calls

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private QuizRepository quizRepository;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase & Repository
        mAuth = FirebaseAuth.getInstance();
        quizRepository = new QuizRepository();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            // No user is logged in, send back to AuthActivity
            goToAuthActivity();
            return;
        }
        // Get reference to the specific user's data in Firebase
        mUserDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        initializeViews();
        setupLanguageSpinner();
        setupClickListeners();
        loadUserData();
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

        loadingOverlay = findViewById(R.id.loadingOverlay); // Add this ID to your XML
        loadingOverlay.setVisibility(View.GONE);
    }

    private void setupLanguageSpinner() {
        String[] languages = {"C", "C++", "Python", "Java", "JavaScript"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
        languageSpinner.setSelection(3); // Default to Java
    }

    private void setupClickListeners() {
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        easyCard.setOnClickListener(v -> startQuiz("Easy"));
        mediumCard.setOnClickListener(v -> startQuiz("Medium"));
        hardCard.setOnClickListener(v -> startQuiz("Hard"));
        gateCard.setOnClickListener(v -> startQuiz("GATE"));
        gkCard.setOnClickListener(v -> startQuiz("GK"));
    }

    private void startQuiz(String difficulty) {
        String language = (difficulty.equals("GK")) ? null : languageSpinner.getSelectedItem().toString();
        int limit = 10; // Fetch 10 questions

        String message = "Loading " + difficulty + " quiz...";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        setLoading(true);

        quizRepository.getQuestions(difficulty, language, limit, new QuizRepository.QuestionsCallback() {
            @Override
            public void onSuccess(List<Question> questions) {
                setLoading(false);
                if (questions == null || questions.isEmpty()) {
                    Toast.makeText(DashboardActivity.this, "No questions found for this category.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(DashboardActivity.this, QuizActivity.class);
                intent.putExtra("difficulty", difficulty);
                intent.putExtra("language", language != null ? language : "General");
                // Pass the entire list of questions to QuizActivity
                intent.putExtra("questions", (Serializable) questions);
                startActivity(intent);
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(DashboardActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadUserData() {
        setLoading(true);
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                if (currentUser != null) {
                    userNameText.setText("Hey " + currentUser.getUsername() + " 👋");
                    updateProgressBars();
                } else {
                    Toast.makeText(DashboardActivity.this, "Failed to load user profile.", Toast.LENGTH_SHORT).show();
                }
                setLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setLoading(false);
                Toast.makeText(DashboardActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProgressBars() {
        if (currentUser == null) return;

        easyProgressBar.setProgress(currentUser.getEasyProgress());
        easyProgress.setText(currentUser.getEasyProgress() + "%");

        mediumProgressBar.setProgress(currentUser.getMediumProgress());
        mediumProgress.setText(currentUser.getMediumProgress() + "%");

        hardProgressBar.setProgress(currentUser.getHardProgress());
        hardProgress.setText(currentUser.getHardProgress() + "%");
    }

    private void setLoading(boolean isLoading) {
        loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void goToAuthActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user data in case progress was updated
        if (mUserDatabase != null) {
            loadUserData();
        }
    }
}