package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import android.widget.TextView;
import android.widget.Toast;

public class SignupFragment extends Fragment {

    private TextInputLayout passwordLayout, confirmPasswordLayout;
    private TextInputEditText emailInput, passwordInput, confirmPasswordInput;
    private MaterialButton signupBtn;
    private TextView loginLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_singup, container, false);

        // Initialize views
        emailInput = view.findViewById(R.id.signupEmail);
        passwordInput = view.findViewById(R.id.signupPassword);
        confirmPasswordInput = view.findViewById(R.id.confirmPassword);
        signupBtn = view.findViewById(R.id.signupBtn);
        loginLink = view.findViewById(R.id.loginLink);

        // The password toggle (eye icon) is automatically handled by Material Design
        // when you set app:endIconMode="password_toggle" in the XML
        // Both password fields will have the eye icon!

        // Signup button click listener
        signupBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

//            if (validateInput(email, password, confirmPassword)) {
                // TODO: Implement your signup logic here
                Toast.makeText(getContext(), "Signup successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                startActivity(intent);
                requireActivity().finish();
//            }
        });

        // Login link click listener
        loginLink.setOnClickListener(v -> {
            // Get the parent activity (AuthActivity) and tell its ViewPager to switch tabs
            if (getActivity() != null) {
                // Switch to the 1st tab (index 0) which is Login
                ((AuthActivity) getActivity()).viewPager.setCurrentItem(0); // <-- UPDATED LINE
            }
        });

        return view;
    }

//    private boolean validateInput(String email, String password, String confirmPassword) {
//        // Clear previous errors
//        emailInput.setError(null);
//        passwordInput.setError(null);
//        confirmPasswordInput.setError(null);
//
//        // Validate email
//        if (email.isEmpty()) {
//            emailInput.setError("Email is required");
//            emailInput.requestFocus();
//            return false;
//        }
//
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            emailInput.setError("Please enter a valid email");
//            emailInput.requestFocus();
//            return false;
//        }
//
//        // Validate password
//        if (password.isEmpty()) {
//            passwordInput.setError("Password is required");
//            passwordInput.requestFocus();
//            return false;
//        }
//
//        if (password.length() < 6) {
//            passwordInput.setError("Password must be at least 6 characters");
//            passwordInput.requestFocus();
//            return false;
//        }
//
//        // Check password strength (optional)
//        if (!isPasswordStrong(password)) {
//            passwordInput.setError("Password should contain letters and numbers");
//            passwordInput.requestFocus();
//            return false;
//        }
//
//        // Validate confirm password
//        if (confirmPassword.isEmpty()) {
//            confirmPasswordInput.setError("Please confirm your password");
//            confirmPasswordInput.requestFocus();
//            return false;
//        }
//
//        if (!password.equals(confirmPassword)) {
//            confirmPasswordInput.setError("Passwords do not match");
//            confirmPasswordInput.requestFocus();
//            return false;
//        }
//
//        return true;
//    }
//
//    private boolean isPasswordStrong(String password) {
//        // Check if password contains at least one letter and one number
//        boolean hasLetter = false;
//        boolean hasDigit = false;
//
//        for (char c : password.toCharArray()) {
//            if (Character.isLetter(c)) {
//                hasLetter = true;
//            }
//            if (Character.isDigit(c)) {
//                hasDigit = true;
//            }
//            if (hasLetter && hasDigit) {
//                return true;
//            }
//        }
//
//        return hasLetter && hasDigit;
//    }
}