package com.example.quizapp.models;

import java.util.HashMap;
import java.util.Map;

public class User {
    // Keep uid, email, username
    public String uid;
    public String email;
    public String username;

    // Keep total stats
    public int totalQuizzes;
    public long totalScore; // Use long for sum to avoid overflow

    // REMOVE these old fields:
    // public int easyProgress;
    // public int mediumProgress;
    // public int hardProgress;

    // ADD this Map for storing the COUNT of correct answers per category:
    public Map<String, Integer> correctlyAnsweredCountMap;

    // ADD this Map for storing WHICH specific questions were answered correctly:
    public Map<String, Map<String, Boolean>> correctlyAnsweredQuestionsMap;


    // Default constructor (required for Firebase)
    public User() {
        // Initialize maps to prevent null pointer exceptions
        correctlyAnsweredCountMap = new HashMap<>();
        correctlyAnsweredQuestionsMap = new HashMap<>();
    }

    // Constructor used during signup
    public User(String uid, String email, String username) {
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.totalQuizzes = 0;
        this.totalScore = 0;
        // Initialize maps
        this.correctlyAnsweredCountMap = new HashMap<>();
        this.correctlyAnsweredQuestionsMap = new HashMap<>();
    }

    // --- Getters ---
    // Standard getters for basic info
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public int getTotalQuizzes() { return totalQuizzes; }
    public long getTotalScore() { return totalScore; }

    // Getter for the count map (ensures it's not null when read from Firebase)
    public Map<String, Integer> getCorrectlyAnsweredCountMap() {
        if (correctlyAnsweredCountMap == null) {
            correctlyAnsweredCountMap = new HashMap<>();
        }
        return correctlyAnsweredCountMap;
    }

    // Getter for the map of specific correct questions (ensures it's not null)
    public Map<String, Map<String, Boolean>> getCorrectlyAnsweredQuestionsMap() {
        if (correctlyAnsweredQuestionsMap == null) {
            correctlyAnsweredQuestionsMap = new HashMap<>();
        }
        return correctlyAnsweredQuestionsMap;
    }

    // --- Setters (needed for Firebase to write data back) ---
    public void setUid(String uid) { this.uid = uid; }
    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
    public void setTotalQuizzes(int totalQuizzes) { this.totalQuizzes = totalQuizzes; }
    public void setTotalScore(long totalScore) { this.totalScore = totalScore; }
    public void setCorrectlyAnsweredCountMap(Map<String, Integer> map) { this.correctlyAnsweredCountMap = map; }
    public void setCorrectlyAnsweredQuestionsMap(Map<String, Map<String, Boolean>> map) { this.correctlyAnsweredQuestionsMap = map; }
}