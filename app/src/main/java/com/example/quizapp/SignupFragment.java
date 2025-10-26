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
import com.example.quizapp.models.User; // Import your User model
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupFragment extends Fragment {

    private TextInputEditText emailInput, passwordInput, confirmPasswordInput, usernameInput;
    private MaterialButton signupBtn;
    private TextView loginLink;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_singup, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(); // Get root reference

        // Initialize views
        usernameInput = view.findViewById(R.id.signupUsername); // Add this ID to your XML
        emailInput = view.findViewById(R.id.signupEmail);
        passwordInput = view.findViewById(R.id.signupPassword);
        confirmPasswordInput = view.findViewById(R.id.confirmPassword);
        signupBtn = view.findViewById(R.id.signupBtn);
        loginLink = view.findViewById(R.id.loginLink);
        progressBar = view.findViewById(R.id.signupProgressBar); // Add this ID to your XML

        progressBar.setVisibility(View.GONE);

        // Signup button click listener
        signupBtn.setOnClickListener(v -> registerUser());

        // Login link click listener
        loginLink.setOnClickListener(v -> {
            if (getActivity() != null) {
                ((AuthActivity) getActivity()).viewPager.setCurrentItem(0);
            }
        });

        return view;
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();

        if (validateInput(email, password, confirmPassword, username)) {
            setLoading(true);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();
                                // Create new User object (using your User model)
                                User newUser = new User(uid, email, username);

                                // Save user to Realtime Database under "users" node
                                mDatabase.child("users").child(uid).setValue(newUser)
                                        .addOnCompleteListener(dbTask -> {
                                            setLoading(false);
                                            if (dbTask.isSuccessful()) {
                                                Toast.makeText(getContext(), "Signup successful!", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                requireActivity().finish();
                                            } else {
                                                // Failed to save to DB, but user was created.
                                                // You might want to handle this case (e.g., delete user from auth)
                                                Toast.makeText(getContext(), "Failed to save user data: " + dbTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            setLoading(false);
                            Toast.makeText(getContext(), "Signup Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            signupBtn.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            signupBtn.setEnabled(true);
        }
    }

    private boolean validateInput(String email, String password, String confirmPassword, String username) {
        if (username.isEmpty()) {
            usernameInput.setError("Username is required");
            usernameInput.requestFocus();
            return false;
        }

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

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            confirmPasswordInput.requestFocus();
            return false;
        }

        return true;
    }
}