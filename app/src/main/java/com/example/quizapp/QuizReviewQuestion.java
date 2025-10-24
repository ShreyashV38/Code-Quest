package com.example.quizapp;

import java.io.Serializable;

// Implement Serializable if you plan to pass a list of these in an Intent
public class QuizReviewQuestion implements Serializable {
    private String questionText;
    private String yourAnswer;
    private String correctAnswer;
    private String explanation;

    // Constructor
    public QuizReviewQuestion(String questionText, String yourAnswer, String correctAnswer, String explanation) {
        this.questionText = questionText;
        this.yourAnswer = yourAnswer;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }

    // Getters
    public String getQuestionText() { return questionText; }
    public String getYourAnswer() { return yourAnswer; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getExplanation() { return explanation; }

    public boolean isCorrect() {
        return yourAnswer.equals(correctAnswer);
    }
}