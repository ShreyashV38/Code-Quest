package com.example.quizapp.api;

import com.example.quizapp.models.Question;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import com.example.quizapp.models.MarkSeenRequest;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    /**
     * Fetch questions based on difficulty and language
     * Authorization header will be added automatically by AuthInterceptor
     *
     * Example: GET /questions?difficulty=Easy&language=Java&limit=10
     */
    @GET("questions")
    Call<List<Question>> getQuestions(
            @Query("difficulty") String difficulty,
            @Query("language") String language,
            @Query("limit") int limit
    );

    /**
     * Fetch GATE questions
     * Example: GET /questions/gate?language=Java&limit=10
     */
    @GET("questions/gate")
    Call<List<Question>> getGateQuestions(
            @Query("language") String language,
            @Query("limit") int limit
    );

    /**
     * Fetch General Knowledge questions
     * Example: GET /questions/gk?limit=10
     */
    @GET("questions/gk")
    Call<List<Question>> getGkQuestions(
            @Query("limit") int limit
    );

    /**
     * Get random questions (for practice mode)
     * Example: GET /questions/random?count=10
     */
    @GET("questions/random")
    Call<List<Question>> getRandomQuestions(
            @Query("count") int count
    );

    /**
     * Alternative: If you want to manually pass Firebase token
     * Use @Header annotation
     */
    @GET("questions/protected")
    Call<List<Question>> getProtectedQuestions(
            @Header("Authorization") String firebaseToken,
            @Query("difficulty") String difficulty
    );
    @POST("user/mark-seen") // Matches your server.js route
    Call<Void> markQuestionsAsSeen(@Body MarkSeenRequest request);
}