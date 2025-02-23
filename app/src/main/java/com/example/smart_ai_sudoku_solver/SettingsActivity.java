package com.example.smart_ai_sudoku_solver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch darkModeSwitch;
    private SeekBar animationSpeedBar;
    private Button resetButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        animationSpeedBar = findViewById(R.id.animation_speed_bar);
        resetButton = findViewById(R.id.reset_button);
        prefs = getSharedPreferences("SudokuPrefs", MODE_PRIVATE);

        // Load saved settings
        darkModeSwitch.setChecked(prefs.getBoolean("darkMode", false));
        animationSpeedBar.setProgress(prefs.getInt("animationSpeed", 400));

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("darkMode", isChecked).apply();
            recreate(); // Apply theme
        });

        animationSpeedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int delay = 100 + progress; // 100ms to 1000ms
                prefs.edit().putInt("animationSpeed", progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        resetButton.setOnClickListener(v -> {
            prefs.edit().clear().putBoolean("darkMode", false).putInt("animationSpeed", 400).apply();
            darkModeSwitch.setChecked(false);
            animationSpeedBar.setProgress(400);
            recreate();
        });
    }
}