package com.example.quizapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class QuizActivity extends AppCompatActivity {

    private ImageView backBtn;
    private TextView questionCounter, categoryText, timerText;
    private ProgressBar quizProgress;
    private TextView questionText, codeSnippet;
    private MaterialCardView codeCard;
    private CardView optionA, optionB, optionC, optionD;
    private TextView optionAText, optionBText, optionCText, optionDText;
    private MaterialButton skipBtn, nextBtn;

    private CountDownTimer timer;
    private int currentQuestion = 1;
    private int totalQuestions = 10;
    private int selectedOption = -1;
    private int correctAnswers = 0;

    // Sample question data (replace with API/database data)
    private String difficulty;
    private String language;
    private long timePerQuestion = 30000; // 30 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get intent data
        difficulty = getIntent().getStringExtra("difficulty");
        language = getIntent().getStringExtra("language");

        // Initialize views
        initializeViews();

        // Setup listeners
        setupListeners();

        // Load first question
        loadQuestion();

        // Start timer
        startTimer();
    }

    private void initializeViews() {
        backBtn = findViewById(R.id.backBtn);
        questionCounter = findViewById(R.id.questionCounter);
        categoryText = findViewById(R.id.categoryText);
        timerText = findViewById(R.id.timerText);
        quizProgress = findViewById(R.id.quizProgress);

        questionText = findViewById(R.id.questionText);
        codeCard = findViewById(R.id.codeCard);
        codeSnippet = findViewById(R.id.codeSnippet);

        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);

        optionAText = findViewById(R.id.optionAText);
        optionBText = findViewById(R.id.optionBText);
        optionCText = findViewById(R.id.optionCText);
        optionDText = findViewById(R.id.optionDText);

        skipBtn = findViewById(R.id.skipBtn);
        nextBtn = findViewById(R.id.nextBtn);

        // Set category text
        categoryText.setText(difficulty + " • " + language);
    }

    private void setupListeners() {
        backBtn.setOnClickListener(v -> finish());

        optionA.setOnClickListener(v -> selectOption(0, optionA));
        optionB.setOnClickListener(v -> selectOption(1, optionB));
        optionC.setOnClickListener(v -> selectOption(2, optionC));
        optionD.setOnClickListener(v -> selectOption(3, optionD));

        skipBtn.setOnClickListener(v -> skipQuestion());
        nextBtn.setOnClickListener(v -> nextQuestion());
    }

    private void loadQuestion() {
        // Update question counter
        questionCounter.setText("Question " + currentQuestion + "/" + totalQuestions);

        // Update progress bar
        int progress = (currentQuestion * 100) / totalQuestions;
        quizProgress.setProgress(progress);

        // Reset selections
        resetOptions();
        selectedOption = -1;
        nextBtn.setEnabled(false);

        // TODO: Load actual question from database/API
        // For now, using sample data
        questionText.setText("What is the time complexity of binary search?");

        // Show/hide code snippet based on question type
        codeCard.setVisibility(View.GONE);
        // If question has code: codeCard.setVisibility(View.VISIBLE);
        // codeSnippet.setText("your code here");

        optionAText.setText("O(n)");
        optionBText.setText("O(log n)");
        optionCText.setText("O(n²)");
        optionDText.setText("O(1)");

        // Restart timer
        startTimer();
    }

    private void selectOption(int option, CardView selectedCard) {
        // Reset all options
        resetOptions();

        // Mark selected option
        selectedOption = option;
        selectedCard.setCardBackgroundColor(getResources().getColor(R.color.teal_700));

        // Change text color to white for selected option
        switch (option) {
            case 0: optionAText.setTextColor(Color.WHITE); break;
            case 1: optionBText.setTextColor(Color.WHITE); break;
            case 2: optionCText.setTextColor(Color.WHITE); break;
            case 3: optionDText.setTextColor(Color.WHITE); break;
        }

        // Enable next button
        nextBtn.setEnabled(true);
    }

    private void resetOptions() {
        optionA.setCardBackgroundColor(Color.WHITE);
        optionB.setCardBackgroundColor(Color.WHITE);
        optionC.setCardBackgroundColor(Color.WHITE);
        optionD.setCardBackgroundColor(Color.WHITE);

        optionAText.setTextColor(Color.parseColor("#2D2D3A"));
        optionBText.setTextColor(Color.parseColor("#2D2D3A"));
        optionCText.setTextColor(Color.parseColor("#2D2D3A"));
        optionDText.setTextColor(Color.parseColor("#2D2D3A"));
    }

    private void skipQuestion() {
        if (currentQuestion < totalQuestions) {
            currentQuestion++;
            loadQuestion();
        } else {
            finishQuiz();
        }
    }

    private void nextQuestion() {
        // Cancel timer
        if (timer != null) {
            timer.cancel();
        }

        // TODO: Check if answer is correct and update score
        // For now, assuming option B is correct
        if (selectedOption == 1) {
            correctAnswers++;
        }

        if (currentQuestion < totalQuestions) {
            currentQuestion++;
            loadQuestion();
        } else {
            finishQuiz();
        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(timePerQuestion, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                timerText.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));

                // Change color if time is running out
                if (seconds <= 10) {
                    timerText.setTextColor(Color.RED);
                } else {
                    timerText.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onFinish() {
                // Auto skip when time runs out
                Toast.makeText(QuizActivity.this, "Time's up!", Toast.LENGTH_SHORT).show();
                skipQuestion();
            }
        }.start();
    }

    // In QuizActivity.java

    private void finishQuiz() {
        if (timer != null) {
            timer.cancel();
        }

        // --- THIS IS THE LINE YOU WERE MISSING ---
        // We must declare the variable before we can use it.
        // TODO: Calculate actual time taken
        String timeTaken = "00:00"; // Placeholder
        // ------------------------------------

        // Remove the old Toast message
        // Toast.makeText(this, "Quiz Complete! Score: " + correctAnswers + "/" + totalQuestions,
        //         Toast.LENGTH_LONG).show();

        // Create the Intent to go to ResultsActivity
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("TOTAL_QUESTIONS", totalQuestions);
        intent.putExtra("CORRECT_ANSWERS", correctAnswers);
        intent.putExtra("WRONG_ANSWERS", totalQuestions - correctAnswers);
        intent.putExtra("TIME_TAKEN", timeTaken); // Now this line will work

        // TODO: Pass the list of questions/answers for the review screen
        // intent.putExtra("reviewData", (Serializable) yourQuestionList);

        startActivity(intent);
        finish(); // Finish QuizActivity so user can't go back
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}