package com.example.quizapp;

import android.content.Intent; // <-- 1. ADD THIS IMPORT
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth; // <-- 2. ADD THIS IMPORT
import com.google.firebase.auth.FirebaseUser; // <-- 3. ADD THIS IMPORT

public class AuthActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    // Make ViewPager public so fragments can access it
    public ViewPager2 viewPager;

    private FirebaseAuth mAuth; // <-- 4. ADD FIREBASE AUTH INSTANCE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- 5. ADD LOGIN CHECK AT THE VERY TOP ---
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, go straight to Dashboard
            Intent intent = new Intent(AuthActivity.this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Finish this activity so the user can't press "back" to it
            return;   // Stop the rest of this onCreate from running
        }
        // --- END OF LOGIN CHECK ---

        // If currentUser is null, proceed with setting up the login/signup UI
        setContentView(R.layout.activity_auth);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Set up ViewPager with adapter
        AuthPagerAdapter adapter = new AuthPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Login");
            } else {
                tab.setText("Signup");
            }
        }).attach();

        // Apply custom styling to tabs
        setupTabStyling();

        // The OnTabSelectedListener is no longer needed
        // The tab_selector.xml drawable handles the selected state automatically.
    }

    // This is the simplified styling method
    private void setupTabStyling() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                View tabView = tab.view;
                tabView.setBackground(ContextCompat.getDrawable(this, R.drawable.tab_selector));
            }
        }
    }

    // The updateTabBackground method is no longer needed and has been removed.
}