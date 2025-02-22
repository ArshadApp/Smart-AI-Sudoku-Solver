package com.example.smart_ai_sudoku_solver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private static final String PREF_DARK_MODE = "dark_mode";
    private static final String PREF_ANIMATION_SPEED = "animation_speed";
    private static final int DEFAULT_ANIMATION_SPEED = 400; // Default 0.4 seconds (400ms)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Set up window insets for edge-to-edge display using the root LinearLayout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize SharedPreferences
        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        // Find views
        SwitchCompat darkModeSwitch = findViewById(R.id.dark_mode_switch);
        SeekBar animationSeekBar = findViewById(R.id.animation_speed_seekbar);
        Button resetButton = findViewById(R.id.reset_button);

        // Load saved preferences with error handling
        boolean isDarkModeEnabled = prefs.getBoolean(PREF_DARK_MODE, false);
        int animationSpeed = prefs.getInt(PREF_ANIMATION_SPEED, DEFAULT_ANIMATION_SPEED);
        darkModeSwitch.setChecked(isDarkModeEnabled);
        animationSeekBar.setProgress(animationSpeed);

        // Set up Dark Mode switch listener with persistence
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_DARK_MODE, isChecked).apply();
            Toast.makeText(this, isChecked ? "Dark Mode Enabled" : "Dark Mode Disabled", Toast.LENGTH_SHORT).show();
            // Optionally recreate the activity or update the theme dynamically
            recreate(); // Recreate activity to apply theme change
        });

        // Set up Animation Speed SeekBar listener with persistence
        animationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    prefs.edit().putInt(PREF_ANIMATION_SPEED, progress).apply();
                    Toast.makeText(SettingsActivity.this, "Animation Speed: " + (progress / 1000.0) + "s", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used, but required by the interface
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not used, but required by the interface
            }
        });

        // Set up Reset Preferences button listener with persistence
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear().putInt(PREF_ANIMATION_SPEED, DEFAULT_ANIMATION_SPEED).apply();
                darkModeSwitch.setChecked(false);
                animationSeekBar.setProgress(DEFAULT_ANIMATION_SPEED);
                Toast.makeText(SettingsActivity.this, "Preferences Reset", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure SharedPreferences are applied correctly on destroy
        prefs.edit().apply();
    }
}