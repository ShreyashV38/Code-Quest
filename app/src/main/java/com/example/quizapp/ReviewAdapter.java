package com.example.quizapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<QuizReviewQuestion> questionList;

    public ReviewAdapter(Context context, List<QuizReviewQuestion> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_question, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        QuizReviewQuestion question = questionList.get(position);

        holder.questionNumber.setText("Question " + (position + 1));
        holder.questionText.setText(question.getQuestionText());
        holder.yourAnswer.setText("Your Answer: " + question.getYourAnswer());
        holder.correctAnswer.setText("Correct Answer: " + question.getCorrectAnswer());
        holder.explanation.setText(question.getExplanation());

        // Set color based on correctness
        if (question.isCorrect()) {
            holder.yourAnswer.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else {
            holder.yourAnswer.setTextColor(Color.parseColor("#F44336")); // Red
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView questionNumber, questionText, yourAnswer, correctAnswer, explanation;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            questionNumber = itemView.findViewById(R.id.questionNumber);
            questionText = itemView.findViewById(R.id.questionText);
            yourAnswer = itemView.findViewById(R.id.yourAnswer);
            correctAnswer = itemView.findViewById(R.id.correctAnswer);
            explanation = itemView.findViewById(R.id.explanation);
        }
    }
}