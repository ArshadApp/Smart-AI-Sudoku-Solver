package com.example.smart_ai_sudoku_solver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Stack;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";
    private SudokuGridView gridView;
    private TextView timerText, progressText;
    private Button[] numberButtons = new Button[10];
    private ImageButton hintButton, solveButton, undoButton, redoButton;
    private int[][] puzzle, currentGrid; // Using 9x9 2D array for consistency
    private Stack<SudokuGridView.Move> undoStack = new Stack<>();
    private Stack<SudokuGridView.Move> redoStack = new Stack<>();
    private Handler handler = new Handler();
    private int secondsElapsed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridView = findViewById(R.id.sudoku_grid);
        timerText = findViewById(R.id.timer_text);
        progressText = findViewById(R.id.progress_text);
        hintButton = findViewById(R.id.hint_button);
        solveButton = findViewById(R.id.solve_button);
        undoButton = findViewById(R.id.undo_button);
        redoButton = findViewById(R.id.redo_button);

        // Convert flat puzzle array to 9x9 grid (if passed as int[])
        int[] flatPuzzle = getIntent().getIntArrayExtra("puzzle");
        if (flatPuzzle != null && flatPuzzle.length == 81) {
            Log.d(TAG, "Received valid puzzle data: " + flatPuzzle.length + " cells");
            puzzle = convertTo2D(flatPuzzle);
        } else {
            Log.e(TAG, "Invalid or null puzzle data received");
            // Fallback: Use a sample puzzle
            puzzle = new int[][] {
                    {5, 3, 0, 0, 7, 0, 0, 0, 0},
                    {6, 0, 0, 1, 9, 5, 0, 0, 0},
                    {0, 9, 8, 0, 0, 0, 0, 6, 0},
                    {8, 0, 0, 0, 6, 0, 0, 0, 3},
                    {4, 0, 0, 8, 0, 3, 0, 0, 1},
                    {7, 0, 0, 0, 2, 0, 0, 0, 6},
                    {0, 6, 0, 0, 0, 0, 2, 8, 0},
                    {0, 0, 0, 4, 1, 9, 0, 0, 5},
                    {0, 0, 0, 0, 8, 0, 0, 7, 9}
            };
        }
        currentGrid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            currentGrid[i] = puzzle[i].clone();
        }
        gridView.setPuzzle(convertTo1D(puzzle)); // Assuming SudokuGridView uses flat array

        // Start timer
        startTimer();

        // Initialize number pad with animations and haptic feedback
        for (int i = 0; i < 10; i++) {
            int resId = getResources().getIdentifier("num_" + (i == 9 ? "x" : i + 1), "id", getPackageName());
            numberButtons[i] = findViewById(resId);
            final int value = (i == 9) ? 0 : (i + 1);
            numberButtons[i].setOnClickListener(v -> {
                animateButton(v);
                fillCell(value);
            });
        }

        // Button listeners with animations and haptic feedback
        hintButton.setOnClickListener(v -> {
            animateButton(v);
            showHint();
        });
        solveButton.setOnClickListener(v -> {
            animateButton(v);
            solvePuzzle();
        });
        undoButton.setOnClickListener(v -> {
            animateButton(v);
            undoMove();
        });
        redoButton.setOnClickListener(v -> {
            animateButton(v);
            redoMove();
        });

        updateProgress();
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void startTimer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                secondsElapsed++;
                int minutes = secondsElapsed / 60;
                int seconds = secondsElapsed % 60;
                timerText.setText(String.format("%02d:%02d", minutes, seconds));
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void fillCell(int value) {
        int[] selected = gridView.getSelectedCell(); // Assuming this method exists
        if (selected == null || !isEditable(selected[0], selected[1])) return;
        int oldValue = currentGrid[selected[0]][selected[1]]; // Fixed typo here
        currentGrid[selected[0]][selected[1]] = value;
        undoStack.push(new SudokuGridView.Move(selected[0], selected[1], oldValue));
        redoStack.clear();
        gridView.fillSelectedCell(value); // Update grid (assuming flat array)
        updateProgress();
    }

    private boolean isEditable(int row, int col) {
        return puzzle[row][col] == 0;
    }

    private void showHint() {
        SudokuGridView.Move hint = gridView.getHint(); // Now uses SudokuGridView.Move
        if (hint != null) {
            Toast.makeText(this, "Try " + hint.value + " at (" + (hint.row + 1) + "," + (hint.col + 1) + ")", Toast.LENGTH_LONG).show();
            gridView.highlightCell(hint.row, hint.col); // Assuming this method exists
        } else {
            Toast.makeText(this, "No valid hint available!", Toast.LENGTH_SHORT).show();
        }
    }

    private void solvePuzzle() {
        gridView.solveAnimated(); // Assuming this handles animation internally
    }

    private void undoMove() {
        if (!undoStack.isEmpty()) {
            SudokuGridView.Move move = undoStack.pop();
            redoStack.push(new SudokuGridView.Move(move.row, move.col, currentGrid[move.row][move.col]));
            currentGrid[move.row][move.col] = move.value;
            gridView.fillSelectedCell(move.value); // Update grid
            updateProgress();
        }
    }

    private void redoMove() {
        if (!redoStack.isEmpty()) {
            SudokuGridView.Move move = redoStack.pop();
            undoStack.push(new SudokuGridView.Move(move.row, move.col, currentGrid[move.row][move.col]));
            currentGrid[move.row][move.col] = move.value;
            gridView.fillSelectedCell(move.value); // Update grid
            updateProgress();
        }
    }

    private void updateProgress() {
        int filled = 0;
        for (int[] row : currentGrid) for (int cell : row) if (cell != 0) filled++;
        progressText.setText("Cells Filled: " + filled + "/81");
    }

    private void animateButton(View view) {
        view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(100)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(10);
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

    private int[][] convertTo2D(int[] flat) {
        int[][] grid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j] = flat[i * 9 + j];
            }
        }
        return grid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    // Add this static method to create an Intent with the puzzle
    public static Intent createIntent(Context context, int[] puzzle) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra("puzzle", puzzle);
        return intent;
    }
}