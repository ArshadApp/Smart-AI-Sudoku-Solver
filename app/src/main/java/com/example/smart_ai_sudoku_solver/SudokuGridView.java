package com.example.smart_ai_sudoku_solver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SudokuGridView extends View {
    private int[][] grid = new int[9][9]; // 9x9 grid to store Sudoku puzzle
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Handler handler = new Handler(Looper.getMainLooper());

    public SudokuGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPuzzle(int[] puzzle) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j] = puzzle[i * 9 + j];
            }
        }
        invalidate();
    }

    public void fillSelectedCell(int value) {
        if (selectedRow != -1 && grid[selectedRow][selectedCol] != -1) {
            // Use Handler to ensure UI update happens on the main thread
            handler.post(() -> {
                grid[selectedRow][selectedCol] = value;
                invalidate(); // Redraw immediately
            });
        }
    }

    public Move getHint() {
        return new Move(0, 0, 5); // Simplified hint logic (replace with actual implementation)
    }

    public void solveAnimated() {
        // Implement solver with animation (placeholder for now)
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
        // Add bold 3x3 borders, numbers, and highlights here (implement as needed)
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
            invalidate();
            return true;
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