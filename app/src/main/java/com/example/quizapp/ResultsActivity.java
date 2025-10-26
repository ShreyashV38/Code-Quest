package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.models.Question; // Need Question model
import com.example.quizapp.models.User;     // Need User model
import com.example.quizapp.repository.QuizRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable; // Needed for List<Question>
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultsActivity extends AppCompatActivity {

    // --- Views ---
    private TextView scorePercentage, correctCount, wrongCount, timeTaken;
    private MaterialButton reviewAnswersBtn, backToHomeBtn;
    private FloatingActionButton shareBtn;

    // --- Data from Quiz ---
    private int totalQuestions = 0;
    private int correct = 0;
    private double percentage = 0;
    private ArrayList<QuizReviewQuestion> reviewList;
    private String quizDifficulty;
    private String quizLanguage;
    private ArrayList<String> questionIdsShown; // IDs shown in this quiz
    private List<Question> questionList;       // Full question objects shown

    // --- Firebase & Repository ---
    private QuizRepository quizRepository;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Initialize Repo and Firebase Auth
        quizRepository = new QuizRepository();
        mAuth = FirebaseAuth.getInstance();

        // Get reference to current user's data in Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mUserDatabase = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        } else {
            // Handle case where user is somehow logged out? Go back to login?
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_LONG).show();
            // Optionally, redirect to AuthActivity
            // Intent intent = new Intent(this, AuthActivity.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // startActivity(intent);
            // finish();
            // return; // Prevent rest of onCreate from running
        }

        initializeViews();
        getDataFromIntent(); // Reads intent data and triggers progress/seen update
        setupListeners();
    }

    private void initializeViews() {
        scorePercentage = findViewById(R.id.scorePercentage);
        correctCount = findViewById(R.id.correctCount);
        wrongCount = findViewById(R.id.wrongCount);
        timeTaken = findViewById(R.id.timeTaken);
        reviewAnswersBtn = findViewById(R.id.reviewAnswersBtn);
        backToHomeBtn = findViewById(R.id.backToHomeBtn);
        shareBtn = findViewById(R.id.shareBtn);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();

        // --- Get all data passed from QuizActivity ---
        totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 0);
        correct = intent.getIntExtra("CORRECT_ANSWERS", 0);
        int wrong = intent.getIntExtra("WRONG_ANSWERS", 0);
        String time = intent.getStringExtra("TIME_TAKEN");
        reviewList = (ArrayList<QuizReviewQuestion>) intent.getSerializableExtra("reviewData");
        questionIdsShown = intent.getStringArrayListExtra("questionIds"); // IDs for marking seen
        questionList = (List<Question>) intent.getSerializableExtra("questions"); // Full objects for progress calc
        quizDifficulty = intent.getStringExtra("difficulty");
        quizLanguage = intent.getStringExtra("language");
        // --- End Get Data ---

        // Basic validation
        if (reviewList == null || questionIdsShown == null || questionList == null || quizDifficulty == null || quizLanguage == null) {
            Log.e("ResultsActivity", "Error receiving data from QuizActivity intent.");
            Toast.makeText(this, "Error processing results.", Toast.LENGTH_SHORT).show();
            // Handle error state, maybe finish()?
        }

        // Set UI text
        correctCount.setText(String.valueOf(correct));
        wrongCount.setText(String.valueOf(wrong));
        timeTaken.setText(time != null ? time : "00:00"); // Add null check for safety
        if (totalQuestions > 0) {
            percentage = ((double) correct / totalQuestions) * 100;
        } else {
            percentage = 0;
        }
        scorePercentage.setText(String.format("%.0f%%", percentage));

        // --- Mark questions as seen ---
        if (questionIdsShown != null && !questionIdsShown.isEmpty()) {
            quizRepository.markQuestionsAsSeen(questionIdsShown);
        }

        // --- Trigger progress update ---
        updateFirebaseProgress();
    }

    private void updateFirebaseProgress() {
        // Double-check necessary data exists and user is logged in
        if (mUserDatabase == null || quizDifficulty == null || quizLanguage == null || questionList == null || reviewList == null) {
            Log.e("ResultsActivity", "Cannot update progress: Missing necessary data or DB reference.");
            return;
        }

        // Only update progress for standard levels (Easy, Medium, Hard)
        if (!quizDifficulty.equals("Easy") && !quizDifficulty.equals("Medium") && !quizDifficulty.equals("Hard")) {
            Log.d("ResultsActivity", "Skipping progress update for category: " + quizDifficulty);
            return; // Don't update for GATE or GK
        }

        // Keys for Firebase maps
        final String countMapKey = quizLanguage + "_" + quizDifficulty + "_CorrectCount"; // e.g., "Java_Easy_CorrectCount"
        final String questionsMapKey = quizLanguage + "_" + quizDifficulty; // e.g., "Java_Easy"

        // Use a transaction for safe read-modify-write operations on Firebase data
        mUserDatabase.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                User user = mutableData.getValue(User.class); // Get current user data

                // If user data doesn't exist, abort the transaction
                if (user == null) {
                    Log.e("ResultsActivity", "User data is null in transaction for " + mUserDatabase.getKey());
                    return Transaction.success(mutableData); // Abort but succeed transaction
                }

                // Get current progress maps (using getters ensures they are initialized)
                Map<String, Integer> currentCounts = user.getCorrectlyAnsweredCountMap();
                Map<String, Map<String, Boolean>> currentCorrectQuestions = user.getCorrectlyAnsweredQuestionsMap();

                // Get the map for the specific language/difficulty, creating it if it doesn't exist
                Map<String, Boolean> specificCorrectMap = currentCorrectQuestions.get(questionsMapKey);
                if (specificCorrectMap == null) {
                    specificCorrectMap = new HashMap<>();
                }

                int currentCorrectCount = currentCounts.getOrDefault(countMapKey, 0);
                int newlyCorrectCount = 0; // Counter for NEW correct answers in this quiz

                // Iterate through the results of THIS quiz
                for (int i = 0; i < reviewList.size(); i++) {
                    QuizReviewQuestion reviewItem = reviewList.get(i);

                    // Ensure we have a corresponding question object
                    if (i < questionList.size()) {
                        Question questionItem = questionList.get(i);
                        if (questionItem != null && questionItem.getId() != null && reviewItem.isCorrect()) {
                            String questionId = questionItem.getId();

                            // Check if this correctly answered question is NEW
                            if (!specificCorrectMap.containsKey(questionId) || !specificCorrectMap.get(questionId)) {
                                // Mark it as correct in our local map for this transaction
                                specificCorrectMap.put(questionId, true);
                                newlyCorrectCount++; // Increment count of new correct answers
                            }
                        }
                    }
                }

                // If any new questions were answered correctly, update the user object
                if (newlyCorrectCount > 0) {
                    Log.d("ResultsActivity", "Adding " + newlyCorrectCount + " new correct answers for " + countMapKey);
                    currentCounts.put(countMapKey, currentCorrectCount + newlyCorrectCount);
                    currentCorrectQuestions.put(questionsMapKey, specificCorrectMap);

                    // Update the maps within the user object
                    user.setCorrectlyAnsweredCountMap(currentCounts);
                    user.setCorrectlyAnsweredQuestionsMap(currentCorrectQuestions);
                }

                // --- Update Overall Stats ---
                user.setTotalQuizzes(user.getTotalQuizzes() + 1);
                // Add the percentage score of THIS quiz to the running total score
                user.setTotalScore(user.getTotalScore() + (long) Math.round(percentage));
                // --- End Update Overall Stats ---


                // Set the modified user object back to Firebase within the transaction
                mutableData.setValue(user);
                return Transaction.success(mutableData); // Commit the changes
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    Log.i("ResultsActivity", "Progress transaction completed successfully for " + questionsMapKey);
                } else {
                    Log.e("ResultsActivity", "Progress transaction failed for " + questionsMapKey + ": " + (databaseError != null ? databaseError.getMessage() : "Unknown error"));
                }
            }
        });
    }

    private void setupListeners() {
        backToHomeBtn.setOnClickListener(v -> {
            Intent homeIntent = new Intent(ResultsActivity.this, DashboardActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });

        reviewAnswersBtn.setOnClickListener(v -> {
            if (reviewList == null || reviewList.isEmpty()) {
                Toast.makeText(this, "No review data available.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent reviewIntent = new Intent(ResultsActivity.this, ReviewAnswersActivity.class);
            reviewIntent.putExtra("reviewData", reviewList);
            startActivity(reviewIntent);
        });

        shareBtn.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = "I just scored " + String.format("%.0f%%", percentage) +
                    " on the CodeQuest App! (" + correct + "/" + totalQuestions + " correct)";
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My CodeQuest Score");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(shareIntent, "Share your score via"));
        });
    }
}