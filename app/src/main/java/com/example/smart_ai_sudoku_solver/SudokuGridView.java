package com.example.smart_ai_sudoku_solver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SudokuGridView extends View {
    private static final String TAG = "SudokuGridView";
    private int[][] grid = new int[9][9]; // 9x9 grid to store Sudoku puzzle
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Handler handler = new Handler(Looper.getMainLooper());

    public SudokuGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPuzzle(int[] puzzle) {
        if (puzzle == null || puzzle.length != 81) {
            Log.e(TAG, "Invalid puzzle data: null or wrong length");
            return;
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j] = puzzle[i * 9 + j];
            }
        }
        Log.d(TAG, "Puzzle set successfully");
        invalidate();
    }

    public void fillSelectedCell(int value) {
        if (selectedRow != -1 && selectedCol != -1 && grid[selectedRow][selectedCol] == 0) {
            handler.post(() -> {
                grid[selectedRow][selectedCol] = value;
                invalidate(); // Redraw immediately
                Log.d(TAG, "Filled cell [" + selectedRow + "][" + selectedCol + "] with " + value);
            });
        } else {
            Log.w(TAG, "Cannot fill cell: invalid selection or cell not empty");
        }
    }

    public Move getHint() {
        // Simplified hint logic (replace with actual implementation)
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == 0) {
                    return new Move(i, j, 5); // Example hint: place 5 in first empty cell
                }
            }
        }
        return new Move(0, 0, 0); // No hint if grid is full
    }

    public void solveAnimated() {
        // Placeholder for animated solving (implement actual logic)
        Log.d(TAG, "Solving puzzle animated (placeholder)");
    }

    public int[][] getGrid() {
        return grid; // Method to return the grid
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float cellSize = getWidth() / 9f;
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2f);
        for (int i = 0; i <= 9; i++) {
            canvas.drawLine(0, i * cellSize, getWidth(), i * cellSize, paint);
            canvas.drawLine(i * cellSize, 0, i * cellSize, getHeight(), paint);
        }
        drawNumbers(canvas, cellSize);
    }

    private void drawNumbers(Canvas canvas, float cellSize) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(40f);
        paint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] != 0) {
                    canvas.drawText(String.valueOf(grid[i][j]),
                            j * cellSize + cellSize / 2,
                            i * cellSize + cellSize / 2 + 15, // Adjust for text centering
                            paint);
                }
            }
        }
        // Highlight selected cell (if any)
        if (selectedRow != -1 && selectedCol != -1) {
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4f);
            canvas.drawRect(selectedCol * cellSize, selectedRow * cellSize,
                    (selectedCol + 1) * cellSize, (selectedRow + 1) * cellSize, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            selectedRow = (int) (event.getY() / (getHeight() / 9));
            selectedCol = (int) (event.getX() / (getWidth() / 9));
            if (selectedRow >= 0 && selectedRow < 9 && selectedCol >= 0 && selectedCol < 9) {
                Log.d(TAG, "Selected cell [" + selectedRow + "][" + selectedCol + "]");
                invalidate();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}

class Move {
    int row, col, value;

    Move(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }
}