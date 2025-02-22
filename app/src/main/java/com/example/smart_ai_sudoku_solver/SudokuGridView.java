package com.example.smart_ai_sudoku_solver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class SudokuGridView extends View {
    private int[][] grid = new int[9][9];
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SudokuGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPuzzle(int[] puzzle) {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) grid[i][j] = puzzle[i * 9 + j];
        invalidate();
    }

    public void fillSelectedCell(int value) {
        if (selectedRow != -1 && grid[selectedRow][selectedCol] != -1) {
            grid[selectedRow][selectedCol] = value;
            invalidate();
        }
    }

    public Move getHint() {
        return new Move(0, 0, 5); // Simplified hint logic
    }

    public void solveAnimated() {
        // Implement solver with animation
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        float cellSize = getWidth() / 9f;
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2f);
        for (int i = 0; i <= 9; i++) {
            canvas.drawLine(0, i * cellSize, getWidth(), i * cellSize, paint);
            canvas.drawLine(i * cellSize, 0, i * cellSize, getHeight(), paint);
        }
        // Add bold 3x3 borders, numbers, and highlights
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            selectedRow = (int) (event.getY() / ((float) getHeight() / 9));
            selectedCol = (int) (event.getX() / ((float) getWidth() / 9));
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