package com.example.smart_ai_sudoku_solver;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {
    private SudokuGridView gridView;
    private TextView timerText;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int seconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        timerText = findViewById(R.id.timer_text);

        gridView = findViewById(R.id.sudoku_grid);
        int[] puzzle = getIntent().getIntArrayExtra("puzzle");
        if (puzzle != null && puzzle.length == 81) {
            gridView.setPuzzle(puzzle);
        } else {
            Toast.makeText(this, "Invalid puzzle data. Returning to selection.", Toast.LENGTH_SHORT).show();
            finish(); // Return to PuzzleSelectionActivity if puzzle is invalid
            return;
        }

        for (int i = 1; i <= 9; i++) {
            Button btn = findViewById(getResources().getIdentifier("num_" + i, "id", getPackageName()));
            final int value = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gridView.fillSelectedCell(value); // Optimized to use Handler in SudokuGridView
                }
            });
        }
        Button eraseButton = findViewById(R.id.num_x);
        eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridView.fillSelectedCell(0); // Optimized to use Handler in SudokuGridView
            }
        });

        findViewById(R.id.hint_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Move hint = gridView.getHint();
                Toast.makeText(GameActivity.this, "Try " + hint.value + " at (" + hint.row + "," + hint.col + ")", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.solve_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridView.solveAnimated();
            }
        });

        handler.post(new Runnable() {
            @Override
            public void run() {
                timerText.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
                seconds++;
                handler.postDelayed(this, 1000);
            }
        });
    }
}