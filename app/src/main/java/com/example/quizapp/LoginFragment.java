package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private TextInputEditText emailInput, passwordInput;
    private MaterialButton loginBtn;
    private TextView forgotPassword, signupLink;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        emailInput = view.findViewById(R.id.email);
        passwordInput = view.findViewById(R.id.password);
        loginBtn = view.findViewById(R.id.loginBtn);
        forgotPassword = view.findViewById(R.id.forgotPassword);
        signupLink = view.findViewById(R.id.signupLink);
        progressBar = view.findViewById(R.id.loginProgressBar); // Add this ID to your XML

        progressBar.setVisibility(View.GONE);

        // Login button click listener
        loginBtn.setOnClickListener(v -> loginUser());

        // Forgot password click listener
        forgotPassword.setOnClickListener(v -> {
            // TODO: Navigate to forgot password screen
            Toast.makeText(getContext(), "Forgot Password clicked", Toast.LENGTH_SHORT).show();
        });

        // Signup link click listener
        signupLink.setOnClickListener(v -> {
            if (getActivity() != null) {
                ((AuthActivity) getActivity()).viewPager.setCurrentItem(1);
            }
        });

        return view;
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (validateInput(email, password)) {
            setLoading(true);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        setLoading(false);
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getActivity(), DashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            requireActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            loginBtn.setEnabled(true);
        }
    }

    private boolean validateInput(String email, String password) {
        // Validate email
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            emailInput.requestFocus();
            return false;
        }

        // Validate password
        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return false;
        }

        emailInput.setError(null);
        passwordInput.setError(null);
        return true;
    }
}