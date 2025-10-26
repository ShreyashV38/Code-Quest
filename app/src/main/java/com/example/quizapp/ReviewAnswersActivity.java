package com.example.quizapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
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
        loadReviewData(); // Load real data
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
        // Get the list of questions from the Intent
        questionList = (List<QuizReviewQuestion>) getIntent().getSerializableExtra("reviewData");

        // Fallback if data is missing for some reason
        if (questionList == null) {
            questionList = new ArrayList<>();
            Toast.makeText(this, "Could not load review data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        adapter = new ReviewAdapter(this, questionList);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(adapter);
    }
}