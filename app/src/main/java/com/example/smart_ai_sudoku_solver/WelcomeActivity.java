package com.example.smart_ai_sudoku_solver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private TextView titleText;
    private Button playNowButton, customPuzzleButton, settingsButton;
    private MediaPlayer clickSound;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Initialize views
        titleText = findViewById(R.id.title_text);
        playNowButton = findViewById(R.id.play_now_button);
        customPuzzleButton = findViewById(R.id.custom_puzzle_button);
        settingsButton = findViewById(R.id.settings_button);

        // Initialize sound and vibrator
        clickSound = MediaPlayer.create(this, R.raw.click);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Fade in title
        titleText.setAlpha(0f);
        titleText.animate().alpha(1f).setDuration(1000).start();

        // Slide up buttons
        playNowButton.setTranslationY(200f);
        customPuzzleButton.setTranslationY(200f);
        playNowButton.animate().translationY(0f).setDuration(800).start();
        customPuzzleButton.animate().translationY(0f).setDuration(800).start();

        // Button click listeners
        playNowButton.setOnClickListener(v -> {
            animateButton(v);
            startActivity(new Intent(this, PuzzleSelectionActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        customPuzzleButton.setOnClickListener(v -> {
            animateButton(v);
            startActivity(new Intent(this, CustomPuzzleActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        settingsButton.setOnClickListener(v -> {
            animateButton(v);
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // Long press tooltip for Play Now
        playNowButton.setOnLongClickListener(v -> {
            Toast.makeText(this, "Start a new Sudoku puzzle!", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void animateButton(View view) {
        view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
        clickSound.start();
        vibrator.vibrate(10);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clickSound.release();
    }
}