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

public class LoginFragment extends Fragment {

    private TextInputLayout passwordLayout;
    private TextInputEditText emailInput, passwordInput;
    private MaterialButton loginBtn;
    private TextView forgotPassword, signupLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize views
        emailInput = view.findViewById(R.id.email);
        passwordInput = view.findViewById(R.id.password);
        passwordLayout = (TextInputLayout) passwordInput.getParent().getParent();
        loginBtn = view.findViewById(R.id.loginBtn);
        forgotPassword = view.findViewById(R.id.forgotPassword);
        signupLink = view.findViewById(R.id.signupLink);

        // The password toggle (eye icon) is automatically handled by Material Design
        // when you set app:endIconMode="password_toggle" in the XML
        // No additional code needed for the eye button functionality!

        // Login button click listener
        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

//            if (validateInput(email, password)) {
                // TODO: Implement your login logic here
                Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                startActivity(intent);
                requireActivity().finish();
//            }
        });

        // Forgot password click listener
        forgotPassword.setOnClickListener(v -> {
            // TODO: Navigate to forgot password screen
            Toast.makeText(getContext(), "Forgot Password clicked", Toast.LENGTH_SHORT).show();
        });

        // Signup link click listener
        signupLink.setOnClickListener(v -> {
            // Get the parent activity (AuthActivity) and tell its ViewPager to switch tabs
            if (getActivity() != null) {
                // Switch to the 2nd tab (index 1) which is Signup
                ((AuthActivity) getActivity()).viewPager.setCurrentItem(1); // <-- UPDATED LINE
            }
        });

        return view;
    }

    private boolean validateInput(String email, String password) {
        // Validate email
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

        return true;
    }
}