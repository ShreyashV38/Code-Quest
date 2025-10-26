package com.example.quizapp.models;

public class User {
    private String uid;
    private String email;
    private String username;
    private int easyProgress;
    private int mediumProgress;
    private int hardProgress;
    private int totalQuizzes;
    private int totalScore;
    private long createdAt;

    // Empty constructor for Firebase
    public User() {
    }

    public User(String uid, String email, String username) {
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.easyProgress = 0;
        this.mediumProgress = 0;
        this.hardProgress = 0;
        this.totalQuizzes = 0;
        this.totalScore = 0;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public int getEasyProgress() { return easyProgress; }
    public int getMediumProgress() { return mediumProgress; }
    public int getHardProgress() { return hardProgress; }
    public int getTotalQuizzes() { return totalQuizzes; }
    public int getTotalScore() { return totalScore; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setUid(String uid) { this.uid = uid; }
    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
    public void setEasyProgress(int easyProgress) { this.easyProgress = easyProgress; }
    public void setMediumProgress(int mediumProgress) { this.mediumProgress = mediumProgress; }
    public void setHardProgress(int hardProgress) { this.hardProgress = hardProgress; }
    public void setTotalQuizzes(int totalQuizzes) { this.totalQuizzes = totalQuizzes; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}