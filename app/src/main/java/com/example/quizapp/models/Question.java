package com.example.quizapp.models;

import java.io.Serializable;

// Implement Serializable so we can pass lists of questions in Intents
public class Question implements Serializable {

    // These fields MUST match the JSON keys from your server
    private String id;
    private String question;
    private String codeSnippet;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int correctAnswer; // The index (0-3) of the correct answer
    private String explanation;
    private String difficulty;
    private String language;
    private String category;

    // Getters
    public String getId() { return id; }
    public String getQuestion() { return question; }
    public String getCodeSnippet() { return codeSnippet; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public int getCorrectAnswer() { return correctAnswer; }
    public String getExplanation() { return explanation; }
    public String getDifficulty() { return difficulty; }
    public String getLanguage() { return language; }
    public String getCategory() { return category; }

    /**
     * Helper method to get the text of an option by its index.
     * @param index 0=A, 1=B, 2=C, 3=D
     * @return The text of the corresponding option.
     */
    public String getOptionByIndex(int index) {
        switch (index) {
            case 0: return optionA;
            case 1: return optionB;
            case 2: return optionC;
            case 3: return optionD;
            default: return ""; // Return empty for invalid index
        }
    }
}