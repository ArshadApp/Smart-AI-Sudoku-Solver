package com.example.smart_ai_sudoku_solver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

public class PuzzleSelectionActivity extends AppCompatActivity {

    private Button easyButton, mediumButton, hardButton, expertButton, startButton;
    private String selectedDifficulty = "Easy";
    private int[][] generatedPuzzle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_selection);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Initialize buttons
        easyButton = findViewById(R.id.easy_button);
        mediumButton = findViewById(R.id.medium_button);
        hardButton = findViewById(R.id.hard_button);
        expertButton = findViewById(R.id.expert_button);
        startButton = findViewById(R.id.start_puzzle_button);

        // Difficulty selection
        View.OnClickListener difficultyListener = v -> {
            resetButtonBorders();
            v.setBackgroundResource(R.drawable.selected_button);
            selectedDifficulty = ((Button) v).getText().toString().split(" ")[0];
            animateButton(v);
        };

        easyButton.setOnClickListener(difficultyListener);
        mediumButton.setOnClickListener(difficultyListener);
        hardButton.setOnClickListener(difficultyListener);
        expertButton.setOnClickListener(difficultyListener);

        startButton.setOnClickListener(v -> {
            animateButton(v);
            new GeneratePuzzleTask().execute();
        });

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void resetButtonBorders() {
        easyButton.setBackgroundResource(R.drawable.default_button);
        mediumButton.setBackgroundResource(R.drawable.default_button);
        hardButton.setBackgroundResource(R.drawable.default_button);
        expertButton.setBackgroundResource(R.drawable.default_button);
    }

    private void animateButton(View view) {
        view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(100)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
    }

    private class GeneratePuzzleTask extends AsyncTask<Void, Void, int[]> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(PuzzleSelectionActivity.this, "", "Generating…", true);
            startButton.setEnabled(false);
        }

        @Override
        protected int[] doInBackground(Void... voids) {
            int[][] grid = new int[9][9];
            fillGrid(grid);
            removeNumbers(grid, getRemovalCount(selectedDifficulty));
            return convertTo1D(grid); // Convert 2D to flat 1D array
        }

        @Override
        protected void onPostExecute(int[] result) {
            dialog.dismiss();
            startButton.setEnabled(true);
            Intent intent = new Intent(PuzzleSelectionActivity.this, GameActivity.class);
            intent.putExtra("puzzle", result);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }

        private boolean fillGrid(int[][] grid) {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    if (grid[row][col] == 0) {
                        Integer[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};
                        Collections.shuffle(Arrays.asList(numbers));
                        for (int num : numbers) {
                            if (isSafe(grid, row, col, num)) {
                                grid[row][col] = num;
                                if (fillGrid(grid)) return true;
                                grid[row][col] = 0;
                            }
                        }
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean isSafe(int[][] grid, int row, int col, int num) {
            for (int x = 0; x < 9; x++) {
                if (grid[row][x] == num || grid[x][col] == num) return false;
            }
            int startRow = row - row % 3, startCol = col - col % 3;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (grid[i + startRow][j + startCol] == num) return false;
                }
            }
            return true;
        }

        private void removeNumbers(int[][] grid, int count) {
            Random rand = new Random();
            while (count > 0) {
                int row = rand.nextInt(9);
                int col = rand.nextInt(9);
                if (grid[row][col] != 0) {
                    int temp = grid[row][col];
                    grid[row][col] = 0;
                    if (isSolvable(grid)) count--;
                    else grid[row][col] = temp;
                }
            }
        }

        private boolean isSolvable(int[][] grid) {
            int[][] copy = new int[9][9];
            for (int i = 0; i < 9; i++) copy[i] = grid[i].clone();
            return fillGrid(copy); // Simplified check; ideally verify uniqueness too
        }

        private int getRemovalCount(String difficulty) {
            switch (difficulty) {
                case "Easy":
                    return 40;
                case "Medium":
                    return 50;
                case "Hard":
                    return 60;
                case "Expert":
                    return 65;
                default:
                    return 40;
            }
        }

        private int[] convertTo1D(int[][] grid) {
            int[] flat = new int[81];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    flat[i * 9 + j] = grid[i][j];
                }
            }
            return flat;
        }
    }
}