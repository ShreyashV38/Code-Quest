package com.example.quizapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageView backBtn;
    private TextInputEditText emailInput;
    private MaterialButton sendResetBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        backBtn = findViewById(R.id.backBtn);
        emailInput = findViewById(R.id.forgotEmailInput);
        sendResetBtn = findViewById(R.id.sendResetBtn);
        progressBar = findViewById(R.id.forgotProgressBar);

        progressBar.setVisibility(View.GONE);

        // Set Click Listeners
        backBtn.setOnClickListener(v -> finish()); // Go back to the previous screen (Login)
        sendResetBtn.setOnClickListener(v -> sendPasswordReset());
    }

    private void sendPasswordReset() {
        String email = emailInput.getText().toString().trim();

        if (validateEmail(email)) {
            setLoading(true);
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        setLoading(false);
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Password reset email sent to " + email,
                                    Toast.LENGTH_LONG).show();
                            // Optionally finish this activity after success
                            // finish();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Failed to send email: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            emailInput.requestFocus();
            return false;
        }
        emailInput.setError(null);
        return true;
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            sendResetBtn.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            sendResetBtn.setEnabled(true);
        }
    }
}