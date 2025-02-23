package com.example.smart_ai_sudoku_solver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SudokuGridView extends View {

    private int[] puzzle = new int[81]; // Flat 9x9 grid (81 cells)
    private int cellSize;
    private Paint cellPaint, textPaint, borderPaint, boldBorderPaint, highlightPaint;
    private int selectedRow = -1, selectedCol = -1;

    public SudokuGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    private void initPaints() {
        cellPaint = new Paint();
        cellPaint.setColor(Color.WHITE); // White background for cells
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK); // Black text for numbers
        textPaint.setTextSize(40f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true); // Improve text rendering
        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(2f);
        boldBorderPaint = new Paint();
        boldBorderPaint.setColor(Color.BLACK);
        boldBorderPaint.setStrokeWidth(4f);
        highlightPaint = new Paint();
        highlightPaint.setColor(Color.YELLOW);
        highlightPaint.setStyle(Paint.Style.STROKE);
        highlightPaint.setStrokeWidth(4f); // Thicker highlight for visibility
    }

    public void setPuzzle(int[] puzzle) {
        this.puzzle = puzzle.clone();
        invalidate(); // Ensure the view redraws after setting the puzzle
    }

    public void fillSelectedCell(int value) {
        if (selectedRow != -1 && selectedCol != -1) {
            puzzle[selectedRow * 9 + selectedCol] = value;
            invalidate();
        }
    }

    public int[] getSelectedCell() {
        if (selectedRow == -1 || selectedCol == -1) return null;
        return new int[]{selectedRow, selectedCol};
    }

    public void highlightCell(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        invalidate();
    }

    public Move getHint() {
        // Simplified hint: Find an empty cell and suggest a valid number
        for (int i = 0; i < 81; i++) {
            if (puzzle[i] == 0) {
                int row = i / 9, col = i % 9;
                for (int num = 1; num <= 9; num++) {
                    if (isSafe(row, col, num)) {
                        return new Move(row, col, num);
                    }
                }
            }
        }
        return null;
    }

    public void solveAnimated() {
        // Placeholder for animated solve (implement later with Handler for animation)
        int[] copy = puzzle.clone();
        if (solve(copy)) {
            // Simulate animation (e.g., update puzzle gradually)
            puzzle = copy;
            invalidate();
        }
    }

    private boolean solve(int[] grid) {
        for (int i = 0; i < 81; i++) {
            if (grid[i] == 0) {
                for (int num = 1; num <= 9; num++) {
                    if (isSafe(i / 9, i % 9, num)) {
                        grid[i] = num;
                        if (solve(grid)) return true;
                        grid[i] = 0;
                    }
                }
                return false;
            }
        }
        return true;
    }

    private boolean isSafe(int row, int col, int num) {
        // Check row
        for (int j = 0; j < 9; j++) {
            if (j != col && puzzle[row * 9 + j] == num) return false;
        }
        // Check column
        for (int i = 0; i < 9; i++) {
            if (i != row && puzzle[i * 9 + col] == num) return false;
        }
        // Check 3x3 box
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int r = startRow + i, c = startCol + j;
                if ((r != row || c != col) && puzzle[r * 9 + c] == num) return false;
            }
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(size, size); // Ensure square grid
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellSize = w / 9; // Calculate cell size based on the smaller dimension
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzle == null || puzzle.length != 81) return;

        // Draw the grid background (white)
        canvas.drawColor(Color.WHITE);

        // Draw each cell
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int index = i * 9 + j;
                int x = j * cellSize;
                int y = i * cellSize;
                canvas.drawRect(x, y, x + cellSize, y + cellSize, cellPaint); // Draw white cell background

                // Draw the number if it exists
                if (puzzle[index] != 0) {
                    String text = String.valueOf(puzzle[index]);
                    float textX = x + cellSize / 2f;
                    float textY = y + cellSize / 2f - (textPaint.descent() + textPaint.ascent()) / 2;
                    canvas.drawText(text, textX, textY, textPaint);
                }

                // Draw thin grid lines
                canvas.drawLine(x, y, x + cellSize, y, borderPaint); // Top
                canvas.drawLine(x, y, x, y + cellSize, borderPaint); // Left
                canvas.drawLine(x + cellSize, y, x + cellSize, y + cellSize, borderPaint); // Right
                canvas.drawLine(x, y + cellSize, x + cellSize, y + cellSize, borderPaint); // Bottom
            }
        }

        // Draw bold lines for 3x3 boxes
        for (int i = 0; i <= 9; i += 3) {
            canvas.drawLine(i * cellSize, 0, i * cellSize, getHeight(), boldBorderPaint); // Vertical
            canvas.drawLine(0, i * cellSize, getWidth(), i * cellSize, boldBorderPaint); // Horizontal
        }

        // Draw highlight for selected cell
        if (selectedRow != -1 && selectedCol != -1) {
            int x = selectedCol * cellSize;
            int y = selectedRow * cellSize;
            canvas.drawRect(x, y, x + cellSize, y + cellSize, highlightPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            selectedRow = (int) (event.getY() / cellSize);
            selectedCol = (int) (event.getX() / cellSize);
            if (selectedRow >= 0 && selectedRow < 9 && selectedCol >= 0 && selectedCol < 9) {
                invalidate();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    // Move class nested in SudokuGridView (public for GameActivity access)
    public static class Move {
        int row, col, value;

        Move(int row, int col, int value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }
    }
}