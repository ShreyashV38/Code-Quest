package com.example.quizapp.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.quizapp.api.ApiService;
import com.example.quizapp.api.RetrofitClient;
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
     * Public interface for API call results
     */
    public interface QuestionsCallback {
        void onSuccess(List<Question> questions);
        void onError(String message);
    }

    /**
     * Fetches questions from the API.
     * This single method handles all categories (Easy, GATE, GK)
     * based on the difficulty string.
     */
    public void getQuestions(String difficulty, String language, int limit, QuestionsCallback callback) {

        Call<List<Question>> call;

        // Decide which API endpoint to call based on difficulty
        if ("GATE".equals(difficulty)) {
            call = apiService.getGateQuestions(language, limit);
        } else if ("GK".equals(difficulty)) {
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
                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
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
}