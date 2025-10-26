package com.example.quizapp.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.quizapp.api.ApiService;
import com.example.quizapp.api.RetrofitClient;
import com.example.quizapp.models.MarkSeenRequest; // <-- Ensure this import is present
import com.example.quizapp.models.Question;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizRepository {

    private static final String TAG = "QuizRepository";
    private ApiService apiService;

    public QuizRepository() {
        // Get the singleton ApiService instance from RetrofitClient
        this.apiService = RetrofitClient.getApiService();
    }

    /**
     * Public interface for API call results (for getting questions)
     */
    public interface QuestionsCallback {
        void onSuccess(List<Question> questions);
        void onError(String message);
    }

    /**
     * Fetches questions from the API.
     * Handles all categories (Easy, GATE, GK) based on the difficulty string.
     */
    public void getQuestions(String difficulty, String language, int limit, QuestionsCallback callback) {

        Call<List<Question>> call;

        // Decide which API endpoint to call based on difficulty
        if ("GATE".equals(difficulty)) {
            call = apiService.getGateQuestions(language, limit);
        } else if ("GK".equals(difficulty)) {
            // This now calls the external API via server.js
            call = apiService.getGkQuestions(limit);
        } else {
            // For "Easy", "Medium", "Hard"
            call = apiService.getQuestions(difficulty, language, limit);
        }

        // Execute the call asynchronously
        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(@NonNull Call<List<Question>> call, @NonNull Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "Successfully fetched " + response.body().size() + " questions.");
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "API Error: " + response.code() + " - " + response.message();
                    try {
                        // Try to get more detailed error from response body
                        if (response.errorBody() != null) {
                            errorMsg += " Body: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    Log.e(TAG, errorMsg);
                    callback.onError("Failed to load questions. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Question>> call, @NonNull Throwable t) {
                String errorMsg = "Network Failure: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }


    // ============================================
    // 🔥 THIS METHOD WAS MISSING
    // ============================================
    /**
     * Marks a list of question IDs as seen by sending them to the server.
     * This is a "fire and forget" call, but we log the result.
     */
    public void markQuestionsAsSeen(List<String> questionIds) {
        // Ensure the list is not null or empty
        if (questionIds == null || questionIds.isEmpty()) {
            Log.w(TAG, "markQuestionsAsSeen called with null or empty list.");
            return;
        }

        // Create the request body object (Make sure MarkSeenRequest.java exists)
        MarkSeenRequest request = new MarkSeenRequest(questionIds);

        // Make the API call using the ApiService instance (Make sure ApiService.java has the endpoint)
        apiService.markQuestionsAsSeen(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Successfully marked " + questionIds.size() + " questions as seen on server.");
                } else {
                    // Log error details if the server responded with an error
                    String errorMsg = "Failed to mark questions as seen. Code: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " Body: " + response.errorBody().string();
                        }
                    } catch (Exception e) { /* Ignore */ }
                    Log.e(TAG, errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Log error if the network request itself failed
                Log.e(TAG, "Network failure while marking questions as seen.", t);
            }
        });
    }
    // ============================================
    // END OF MISSING METHOD
    // ============================================

}