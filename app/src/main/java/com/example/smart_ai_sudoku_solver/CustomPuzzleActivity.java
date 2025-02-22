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
                int[][] grid = gridView.getGrid();
                if (isValidPuzzle(grid)) {
                    Intent intent = new Intent(CustomPuzzleActivity.this, GameActivity.class);
                    int[] flatGrid = new int[81];
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
        if (grid == null || grid.length != 9 || grid[0].length != 9) {
            return false;
        }

        // Check for conflicts (duplicates in rows, columns, and 3x3 boxes)
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int num = grid[row][col];
                if (num != 0) { // Skip empty cells
                    if (!isSafe(grid, row, col, num)) {
                        return false;
                    }
                }
            }
        }

        // Check if the puzzle is solvable and has a unique solution
        int[][] tempGrid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            tempGrid[i] = grid[i].clone();
        }

        // Try to solve the puzzle
        if (!solveSudoku(tempGrid, 0, 0)) {
            return false; // Puzzle is not solvable
        }

        // Check for unique solution (simplified: solve again after modifying one cell)
        int[][] tempGrid2 = new int[9][9];
        for (int i = 0; i < 9; i++) {
            tempGrid2[i] = grid[i].clone();
        }
        // Find an empty cell to modify
        boolean foundEmpty = false;
        int emptyRow = -1, emptyCol = -1;
        for (int i = 0; i < 9 && !foundEmpty; i++) {
            for (int j = 0; j < 9; j++) {
                if (tempGrid2[i][j] == 0) {
                    emptyRow = i;
                    emptyCol = j;
                    foundEmpty = true;
                    break;
                }
            }
        }
        if (foundEmpty) {
            tempGrid2[emptyRow][emptyCol] = 1; // Try a number
            if (solveSudoku(tempGrid2, 0, 0)) {
                return false; // Multiple solutions exist
            }
        }

        return true; // Valid and unique solution
    }

    private boolean isSafe(int[][] grid, int row, int col, int num) {
        // Check row
        for (int x = 0; x < 9; x++) {
            if (x != col && grid[row][x] == num) return false;
        }
        // Check column
        for (int x = 0; x < 9; x++) {
            if (x != row && grid[x][col] == num) return false;
        }
        // Check 3x3 box
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ((i + startRow != row || j + startCol != col) && grid[i + startRow][j + startCol] == num) {
                    return false;
                }
            }
        }
        return true;
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