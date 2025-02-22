package com.example.smart_ai_sudoku_solver;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.ProgressDialog;
import android.widget.Toast;

public class PuzzleSelectionActivity extends AppCompatActivity {
    private String selectedDifficulty = "Easy";
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_selection);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button easyButton = findViewById(R.id.easy_button);
        Button mediumButton = findViewById(R.id.medium_button);
        Button hardButton = findViewById(R.id.hard_button);
        Button expertButton = findViewById(R.id.expert_button);
        startButton = findViewById(R.id.start_puzzle_button);

        View.OnClickListener difficultyListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                easyButton.setBackgroundTintList(null);
                mediumButton.setBackgroundTintList(null);
                hardButton.setBackgroundTintList(null);
                expertButton.setBackgroundTintList(null);
                v.setBackgroundTintList(getResources().getColorStateList(R.color.black));
                selectedDifficulty = ((Button) v).getText().toString().split(" ")[0];
            }
        };

        easyButton.setOnClickListener(difficultyListener);
        mediumButton.setOnClickListener(difficultyListener);
        hardButton.setOnClickListener(difficultyListener);
        expertButton.setOnClickListener(difficultyListener);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GeneratePuzzleTask().execute();
            }
        });
    }

    private class GeneratePuzzleTask extends AsyncTask<Void, Void, int[]> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(PuzzleSelectionActivity.this);
            progressDialog.setMessage("Generating…");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected int[] doInBackground(Void... voids) {
            int[][] grid = new int[9][9];
            if (!generateSudoku(grid)) {
                return null; // Fail gracefully if generation fails
            }
            removeCells(grid, selectedDifficulty.equals("Easy") ? 40 : selectedDifficulty.equals("Medium") ? 50 :
                    selectedDifficulty.equals("Hard") ? 60 : 65);
            int[] flatGrid = new int[81];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    flatGrid[i * 9 + j] = grid[i][j];
                }
            }
            return flatGrid;
        }

        @Override
        protected void onPostExecute(int[] result) {
            progressDialog.dismiss();
            if (result != null) {
                Intent intent = new Intent(PuzzleSelectionActivity.this, GameActivity.class);
                intent.putExtra("puzzle", result);
                startActivity(intent);
            } else {
                Toast.makeText(PuzzleSelectionActivity.this, "Failed to generate puzzle. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }

        private boolean generateSudoku(int[][] grid) {
            // Backtracking to generate a valid 9x9 Sudoku grid
            if (!fillGrid(grid, 0, 0)) {
                return false; // Failed to generate a valid grid
            }
            return true;
        }

        private boolean fillGrid(int[][] grid, int row, int col) {
            if (row == 9) {
                row = 0;
                if (++col == 9) return true;
            }
            if (grid[row][col] != 0) return fillGrid(grid, row + 1, col);

            int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};
            java.util.Collections.shuffle(java.util.Arrays.asList(numbers));

            for (int num : numbers) {
                if (isSafe(grid, row, col, num)) {
                    grid[row][col] = num;
                    if (fillGrid(grid, row + 1, col)) return true;
                    grid[row][col] = 0; // Backtrack
                }
            }
            return false;
        }

        private boolean isSafe(int[][] grid, int row, int col, int num) {
            // Check row
            for (int x = 0; x < 9; x++) {
                if (grid[row][x] == num) return false;
            }
            // Check column
            for (int x = 0; x < 9; x++) {
                if (grid[x][col] == num) return false;
            }
            // Check 3x3 box
            int startRow = row - row % 3;
            int startCol = col - col % 3;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (grid[i + startRow][j + startCol] == num) return false;
                }
            }
            return true;
        }

        private void removeCells(int[][] grid, int count) {
            java.util.Random rand = new java.util.Random();
            int removed = 0;
            while (removed < count) {
                int row = rand.nextInt(9);
                int col = rand.nextInt(9);
                if (grid[row][col] != 0) {
                    grid[row][col] = 0;
                    removed++;
                }
            }
            // Ensure the puzzle is solvable (simplified check)
            if (!isSolvable(grid)) {
                // Revert the last removal and try again (simplified for brevity)
                // In a real implementation, you'd implement a more robust check
                grid[row][col] = 1; // Revert last removal (example fix)
            }
        }

        private boolean isSolvable(int[][] grid) {
            // Simplified solvability check: try to solve and verify a solution exists
            int[][] tempGrid = new int[9][9];
            for (int i = 0; i < 9; i++) {
                tempGrid[i] = grid[i].clone();
            }
            return solveSudoku(tempGrid, 0, 0);
        }

        private boolean solveSudoku(int[][] grid, int row, int col) {
            if (row == 9) {
                return true;
            }
            if (col == 9) {
                return solveSudoku(grid, row + 1, 0);
            }
            if (grid[row][col] != 0) {
                return solveSudoku(grid, row, col + 1);
            }
            for (int num = 1; num <= 9; num++) {
                if (isSafe(grid, row, col, num)) {
                    grid[row][col] = num;
                    if (solveSudoku(grid, row, col + 1)) return true;
                    grid[row][col] = 0;
                }
            }
            return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        return true;
    }
}