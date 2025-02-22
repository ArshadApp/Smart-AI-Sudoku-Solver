package com.example.smart_ai_sudoku_solver;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button playNowButton = findViewById(R.id.play_now_button);
        Button customPuzzleButton = findViewById(R.id.custom_puzzle_button);
        Button settingsButton = findViewById(R.id.settings_button);
        View titleView = findViewById(R.id.title_text);

        // Animations
        titleView.setAlpha(0f);
        titleView.animate().alpha(1f).setDuration(1000).start();
        playNowButton.setTranslationY(200f);
        customPuzzleButton.setTranslationY(200f);
        playNowButton.animate().translationY(0f).setDuration(800).start();
        customPuzzleButton.animate().translationY(0f).setDuration(800).start();

        // Button interactions
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final MediaPlayer clickSound = MediaPlayer.create(this, R.raw.click);

        playNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    }
                }).start();
                clickSound.start();
                vibrator.vibrate(10);
                startActivity(new Intent(WelcomeActivity.this, PuzzleSelectionActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        playNowButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(WelcomeActivity.this, "Start a new Sudoku puzzle!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        customPuzzleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    }
                }).start();
                clickSound.start();
                vibrator.vibrate(10);
                startActivity(new Intent(WelcomeActivity.this, CustomPuzzleActivity.class));
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, SettingsActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> WelcomeActivity.super.onBackPressed())
                .setNegativeButton("No", null)
                .show();
    }
}