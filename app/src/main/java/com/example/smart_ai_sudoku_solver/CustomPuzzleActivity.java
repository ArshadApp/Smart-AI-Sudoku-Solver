package com.example.smart_ai_sudoku_solver;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CustomPuzzleActivity extends AppCompatActivity {

    private SudokuGridView gridView;
    private Button[] numberButtons = new Button[10];
    private Button checkButton;
    private int[] grid = new int[81]; // Flat 9x9 grid (81 cells)
    private Vibrator vibrator; // For haptic feedback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_puzzle);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridView = findViewById(R.id.sudoku_grid);
        checkButton = findViewById(R.id.check_puzzle_button);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Initialize number pad with animations and haptic feedback
        for (int i = 0; i < 10; i++) {
            int resId = getResources().getIdentifier("number_" + (i == 9 ? "x" : (i + 1)), "id", getPackageName());
            numberButtons[i] = findViewById(resId);
            final int value = (i == 9) ? 0 : (i + 1);
            numberButtons[i].setOnClickListener(v -> {
                animateButton(v);
                fillCell(value);
            });
        }

        // Set initial empty grid (all 0s)
        gridView.setPuzzle(grid); // All cells editable (0s indicate editable)

        checkButton.setOnClickListener(v -> {
            animateButton(v);
            if (isValidPuzzle()) {
                // Pass flat array to GameActivity
                int[] puzzle = grid.clone();
                startActivity(GameActivity.createIntent(this, puzzle));
            } else {
                Toast.makeText(this, "Invalid or unsolvable puzzle!", Toast.LENGTH_SHORT).show();
            }
        });

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void fillCell(int value) {
        int[] selected = gridView.getSelectedCell(); // Assuming this returns {row, col}
        if (selected == null) return;
        int index = selected[0] * 9 + selected[1]; // Convert 2D to 1D index
        grid[index] = value;
        gridView.fillSelectedCell(value); // Update grid view
    }

    private boolean isValidPuzzle() {
        // Check for conflicts in the current state
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int index = i * 9 + j;
                if (grid[index] != 0 && !isSafe(i, j, grid[index])) {
                    return false;
                }
            }
        }
        // Check if solvable (simplified version)
        int[] copy = grid.clone();
        return solveGrid(copy);
    }

    private boolean isSafe(int row, int col, int num) {
        // Check row
        for (int j = 0; j < 9; j++) {
            if (j != col && grid[row * 9 + j] == num) return false;
        }
        // Check column
        for (int i = 0; i < 9; i++) {
            if (i != row && grid[i * 9 + col] == num) return false;
        }
        // Check 3x3 box
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int r = startRow + i, c = startCol + j;
                if ((r != row || c != col) && grid[r * 9 + c] == num) return false;
            }
        }
        return true;
    }

    private boolean solveGrid(int[] grid) {
        for (int i = 0; i < 81; i++) {
            if (grid[i] == 0) {
                for (int num = 1; num <= 9; num++) {
                    if (isSafe(i / 9, i % 9, num)) {
                        grid[i] = num;
                        if (solveGrid(grid)) return true;
                        grid[i] = 0;
                    }
                }
                return false;
            }
        }
        return true;
    }

    private void animateButton(View view) {
        view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(100)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
        if (vibrator != null) {
            vibrator.vibrate(10);
        }
    }
}