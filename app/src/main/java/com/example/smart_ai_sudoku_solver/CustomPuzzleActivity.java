package com.example.smart_ai_sudoku_solver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CustomPuzzleActivity extends AppCompatActivity {
    private SudokuGridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_puzzle);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridView = findViewById(R.id.sudoku_grid);

        for (int i = 1; i <= 9; i++) {
            Button btn = findViewById(getResources().getIdentifier("num_" + i, "id", getPackageName()));
            final int value = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gridView.fillSelectedCell(value);
                }
            });
        }
        findViewById(R.id.num_x).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridView.fillSelectedCell(0);
            }
        });

        findViewById(R.id.check_puzzle_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidPuzzle(gridView.getGrid())) {
                    Intent intent = new Intent(CustomPuzzleActivity.this, GameActivity.class);
                    int[] flatGrid = new int[81];
                    int[][] grid = gridView.getGrid();
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                            flatGrid[i * 9 + j] = grid[i][j];
                        }
                    }
                    intent.putExtra("puzzle", flatGrid);
                    startActivity(intent);
                } else {
                    Toast.makeText(CustomPuzzleActivity.this, "Invalid or unsolvable puzzle!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidPuzzle(int[][] grid) {
        // Implement validation logic (e.g., check for conflicts and ensure solvability)
        return true; // Placeholder; replace with actual validation
    }
}