package com.example.quizapp.models;

import java.util.List;

public class MarkSeenRequest {
    private List<String> questionIds; // Name MUST match server expectation

    public MarkSeenRequest(List<String> questionIds) {
        this.questionIds = questionIds;
    }
    // No getter needed if only used for sending
}