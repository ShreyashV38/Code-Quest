package com.example.quizapp;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ReviewAnswersActivity extends AppCompatActivity {

    private ImageView backBtn;
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter adapter;
    private List<QuizReviewQuestion> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        initializeViews();
        setupListeners();
        loadReviewData();
        setupRecyclerView();
    }

    private void initializeViews() {
        backBtn = findViewById(R.id.backBtn);
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
    }

    private void setupListeners() {
        backBtn.setOnClickListener(v -> finish());
    }

    private void loadReviewData() {
        // TODO: Get the list of questions from the Intent
        // questionList = (List<QuizReviewQuestion>) getIntent().getSerializableExtra("reviewData");

        // For now, using sample data
        if (questionList == null) {
            questionList = new ArrayList<>();
            questionList.add(new QuizReviewQuestion(
                    "What is the time complexity of binary search?",
                    "O(n)",
                    "O(log n)",
                    "Binary search divides the array in half at each step, resulting in logarithmic time complexity."
            ));
            questionList.add(new QuizReviewQuestion(
                    "Which keyword is used to inherit a class in Java?",
                    "extends",
                    "extends",
                    "The 'extends' keyword is used for class inheritance, while 'implements' is used for interfaces."
            ));
            questionList.add(new QuizReviewQuestion(
                    "What is the default value of an integer in Java?",
                    "null",
                    "0",
                    "Primitive numeric types default to 0, while object references default to null."
            ));
        }
    }

    private void setupRecyclerView() {
        adapter = new ReviewAdapter(this, questionList);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(adapter);
    }
}