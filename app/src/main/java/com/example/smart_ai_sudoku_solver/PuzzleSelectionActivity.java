package com.example.smart_ai_sudoku_solver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
                v.setBackgroundTintList(getResources().getColorStateList(R.color.white));
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        return true;
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
            generateSudoku(grid);
            removeCells(grid, selectedDifficulty.equals("Easy") ? 40 : selectedDifficulty.equals("Medium") ? 50 :
                    selectedDifficulty.equals("Hard") ? 60 : 65);
            int[] flatGrid = new int[81];
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++) flatGrid[i * 9 + j] = grid[i][j];
            return flatGrid;
        }

        @Override
        protected void onPostExecute(int[] result) {
            progressDialog.dismiss();
            Intent intent = new Intent(PuzzleSelectionActivity.this, GameActivity.class);
            intent.putExtra("puzzle", result);
            startActivity(intent);
        }

        private void generateSudoku(int[][] grid) {
            // Simplified backtracking logic
        }

        private void removeCells(int[][] grid, int count) {
            // Implement removal with solvability check
        }
    }
}