package com.example.quizapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.quizapp.models.Question; // Import Question model
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private long timePerQuestion = 30000; // 30 seconds
    private long timeLeftInMillis;
    private long totalTimeSpent = 0;

    // Data from Intent
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int totalQuestions;
    private Question currentQuestion;
    private int selectedOption = -1; // 0=A, 1=B, 2=C, 3=D
    private int correctAnswers = 0;

    // For Review Screen
    private ArrayList<QuizReviewQuestion> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get intent data
        String difficulty = getIntent().getStringExtra("difficulty");
        String language = getIntent().getStringExtra("language");
        questionList = (List<Question>) getIntent().getSerializableExtra("questions");

        // Check if question list is valid
        if (questionList == null || questionList.isEmpty()) {
            Toast.makeText(this, "Failed to load questions.", Toast.LENGTH_SHORT).show();
            finish(); // Go back
            return;
        }

        totalQuestions = questionList.size();
        reviewList = new ArrayList<>(); // Initialize review list

        initializeViews();
        categoryText.setText(difficulty + " • " + language); // Set category
        setupListeners();
        loadQuestion();
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
    }

    private void setupListeners() {
        backBtn.setOnClickListener(v -> finish());
        optionA.setOnClickListener(v -> selectOption(0, optionA));
        optionB.setOnClickListener(v -> selectOption(1, optionB));
        optionC.setOnClickListener(v -> selectOption(2, optionC));
        optionD.setOnClickListener(v -> selectOption(3, optionD));

        skipBtn.setOnClickListener(v -> processAnswer(-1, true)); // -1 for skip
        nextBtn.setOnClickListener(v -> {
            if (selectedOption != -1) {
                processAnswer(selectedOption, false);
            }
        });
    }

    private void loadQuestion() {
        if (currentQuestionIndex >= totalQuestions) {
            finishQuiz();
            return;
        }

        currentQuestion = questionList.get(currentQuestionIndex);

        // Update UI
        questionCounter.setText("Question " + (currentQuestionIndex + 1) + "/" + totalQuestions);
        quizProgress.setProgress(((currentQuestionIndex + 1) * 100) / totalQuestions);

        resetOptions();
        selectedOption = -1;
        nextBtn.setEnabled(false);
        skipBtn.setEnabled(true);
        enableOptionClicks(true);

        questionText.setText(currentQuestion.getQuestion());

        // Show/hide code snippet
        if (currentQuestion.getCodeSnippet() != null && !currentQuestion.getCodeSnippet().isEmpty()) {
            codeCard.setVisibility(View.VISIBLE);
            codeSnippet.setText(currentQuestion.getCodeSnippet());
        } else {
            codeCard.setVisibility(View.GONE);
        }

        // Set option text
        optionAText.setText(currentQuestion.getOptionA());
        optionBText.setText(currentQuestion.getOptionB());
        optionCText.setText(currentQuestion.getOptionC());
        optionDText.setText(currentQuestion.getOptionD());

        // Restart timer
        timeLeftInMillis = timePerQuestion;
        startTimer();
    }

    private void selectOption(int option, CardView selectedCard) {
        resetOptions();
        selectedOption = option;
        selectedCard.setCardBackgroundColor(getResources().getColor(R.color.teal_700)); // Selected color

        switch (option) {
            case 0: optionAText.setTextColor(Color.WHITE); break;
            case 1: optionBText.setTextColor(Color.WHITE); break;
            case 2: optionCText.setTextColor(Color.WHITE); break;
            case 3: optionDText.setTextColor(Color.WHITE); break;
        }
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

    private void processAnswer(int selectedAnswerIndex, boolean isSkipped) {
        if (timer != null) timer.cancel();

        // Disable buttons to prevent double-click
        nextBtn.setEnabled(false);
        skipBtn.setEnabled(false);
        enableOptionClicks(false);

        totalTimeSpent += (timePerQuestion - timeLeftInMillis); // Add time spent

        String yourAnswerText;
        String correctAnswerText = currentQuestion.getOptionByIndex(currentQuestion.getCorrectAnswer());
        boolean isCorrect = false;

        if (isSkipped) {
            yourAnswerText = "Skipped";
        } else {
            yourAnswerText = currentQuestion.getOptionByIndex(selectedAnswerIndex);
            if (selectedAnswerIndex == currentQuestion.getCorrectAnswer()) {
                correctAnswers++;
                isCorrect = true;
            }
        }

        // Show feedback (Green for correct, Red for wrong)
        showFeedback(selectedAnswerIndex, currentQuestion.getCorrectAnswer());

        // Add to review list
        reviewList.add(new QuizReviewQuestion(
                currentQuestion.getQuestion(),
                yourAnswerText,
                correctAnswerText,
                currentQuestion.getExplanation()
        ));

        // Delay for 1.5 seconds to show feedback, then load next
        new Handler().postDelayed(() -> {
            currentQuestionIndex++;
            loadQuestion();
        }, 1500);
    }

    private void showFeedback(int selectedIndex, int correctIndex) {
        // Mark correct answer in Green
        CardView correctCard = getCardByIndex(correctIndex);
        if (correctCard != null) {
            correctCard.setCardBackgroundColor(Color.parseColor("#4CAF50")); // Green
            getTextViewByIndex(correctIndex).setTextColor(Color.WHITE);
        }

        // If wrong answer was selected, mark it in Red
        if (selectedIndex != -1 && selectedIndex != correctIndex) {
            CardView selectedCard = getCardByIndex(selectedIndex);
            if (selectedCard != null) {
                selectedCard.setCardBackgroundColor(Color.parseColor("#F44336")); // Red
                getTextViewByIndex(selectedIndex).setTextColor(Color.WHITE);
            }
        }
    }

    // Helper to disable option clicks
    private void enableOptionClicks(boolean enabled) {
        optionA.setClickable(enabled);
        optionB.setClickable(enabled);
        optionC.setClickable(enabled);
        optionD.setClickable(enabled);
    }

    // Helper methods to get views by index
    private CardView getCardByIndex(int index) {
        switch(index) {
            case 0: return optionA;
            case 1: return optionB;
            case 2: return optionC;
            case 3: return optionD;
            default: return null;
        }
    }
    private TextView getTextViewByIndex(int index) {
        switch(index) {
            case 0: return optionAText;
            case 1: return optionBText;
            case 2: return optionCText;
            case 3: return optionDText;
            default: return null;
        }
    }

    private void startTimer() {
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                long seconds = millisUntilFinished / 1000;
                timerText.setText(String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60));

                if (seconds <= 10) {
                    timerText.setTextColor(Color.RED);
                } else {
                    timerText.setTextColor(Color.WHITE);
                }
            }
            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                Toast.makeText(QuizActivity.this, "Time's up!", Toast.LENGTH_SHORT).show();
                processAnswer(-1, true); // Auto-skip
            }
        }.start();
    }

    private void finishQuiz() {
        if (timer != null) timer.cancel();

        // Calculate total time
        long minutes = (totalTimeSpent / 1000) / 60;
        long seconds = (totalTimeSpent / 1000) % 60;
        String timeTaken = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        // --- ADD THIS: Create list of question IDs shown ---
        ArrayList<String> questionIdsShown = new ArrayList<>();
        for (Question q : questionList) {
            if (q != null && q.getId() != null) { // Add null checks for safety
                questionIdsShown.add(q.getId());
            }
        }
        // --- END ADD ---

        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("TOTAL_QUESTIONS", totalQuestions);
        intent.putExtra("CORRECT_ANSWERS", correctAnswers);
        intent.putExtra("WRONG_ANSWERS", totalQuestions - correctAnswers);
        intent.putExtra("TIME_TAKEN", timeTaken);
        intent.putExtra("reviewData", reviewList); // For the review screen

        // --- ADD THESE EXTRAS ---
        // Pass IDs for marking questions as seen
        intent.putExtra("questionIds", questionIdsShown);
        // Pass full question objects for progress calculation
        intent.putExtra("questions", (Serializable) questionList);
        // Pass difficulty and language for progress calculation keys
        // Need to get these from onCreate or store them as member variables
        String difficulty = getIntent().getStringExtra("difficulty"); // Re-get from original intent
        String language = getIntent().getStringExtra("language");     // Re-get from original intent
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("language", language);
        // --- END ADD ---

        startActivity(intent);
        finish(); // Finish this activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}