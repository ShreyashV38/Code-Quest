package com.example.quizapp.api;

import android.util.Log;
import com.google.android.gms.tasks.Tasks; // NEW IMPORT
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import java.io.IOException;
import java.util.concurrent.ExecutionException; // NEW IMPORT
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class FirebaseApiManager {

    private static final String TAG = "FirebaseApiManager";
    private FirebaseAuth mAuth;

    public FirebaseApiManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    /**
     * OkHttp Interceptor to add Firebase token to all API requests
     */
    public static class AuthInterceptor implements Interceptor {

        private FirebaseAuth mAuth;

        public AuthInterceptor() {
            mAuth = FirebaseAuth.getInstance();
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) {
                // No user, proceed without token
                Log.w(TAG, "Interceptor: No user logged in, proceeding without token.");
                return chain.proceed(originalRequest);
            }

            try {
                // THIS IS THE FIX: Synchronously wait for the token
                // This runs on OkHttp's background thread, so blocking is OK.
                Log.d(TAG, "Interceptor: Getting fresh token...");
                GetTokenResult tokenResult = Tasks.await(user.getIdToken(true));
                String token = tokenResult.getToken();

                if (token != null) {
                    // Add the Authorization header to the request
                    Request newRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .build();
                    Log.i(TAG, "Interceptor: Token added to request.");
                    return chain.proceed(newRequest);
                } else {
                    Log.w(TAG, "Interceptor: Token was null, proceeding without token.");
                    return chain.proceed(originalRequest);
                }

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Interceptor: Failed to get token synchronously", e);
                // Propagate the error
                throw new IOException("Failed to get Firebase token", e);
            }
        }
    }

    // This async method is still useful for other parts of the app
    public void getAuthToken(TokenCallback callback) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            callback.onError("User not logged in");
            return;
        }

        user.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();
                        callback.onSuccess(token);
                    } else {
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Failed to get token";
                        callback.onError(error);
                    }
                });
    }

    public interface TokenCallback {
        void onSuccess(String token);
        void onError(String error);
    }
}