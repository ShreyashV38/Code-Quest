package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.Serializable;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    private TextView scorePercentage, correctCount, wrongCount, timeTaken;
    private MaterialButton reviewAnswersBtn, backToHomeBtn;
    private FloatingActionButton shareBtn;

    private int totalQuestions = 0;
    private int correct = 0;
    private double percentage = 0;
    private ArrayList<QuizReviewQuestion> reviewList; // For passing to review screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        initializeViews();
        getDataFromIntent();
        // setDataToViews(); // This logic is already in getDataFromIntent
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
        totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 0);
        correct = intent.getIntExtra("CORRECT_ANSWERS", 0);
        int wrong = intent.getIntExtra("WRONG_ANSWERS", 0);
        String time = intent.getStringExtra("TIME_TAKEN");

        // Get the review list
        reviewList = (ArrayList<QuizReviewQuestion>) intent.getSerializableExtra("reviewData");

        // Set data to views
        correctCount.setText(String.valueOf(correct));
        wrongCount.setText(String.valueOf(wrong));
        timeTaken.setText(time);

        // Calculate and display percentage
        if (totalQuestions > 0) {
            percentage = ((double) correct / totalQuestions) * 100;
        }
        scorePercentage.setText(String.format("%.0f%%", percentage));
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

            // Pass the review data to the next activity
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