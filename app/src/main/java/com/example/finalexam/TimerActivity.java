package com.example.finalexam;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class TimerActivity extends AppCompatActivity {

    private EditText inputHours, inputMinutes, inputSeconds;
    private TextView timerDisplay;
    private Button startButton, pauseButton, resetButton;

    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis; // Time left in milliseconds for countdown

    private DatabaseHelper databaseHelper;  // Database helper instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        inputHours = findViewById(R.id.inputHours);
        inputMinutes = findViewById(R.id.inputMinutes);
        inputSeconds = findViewById(R.id.inputSeconds);
        timerDisplay = findViewById(R.id.timerDisplay);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        startButton.setOnClickListener(v -> startTimer());
        pauseButton.setOnClickListener(v -> pauseTimer());
        resetButton.setOnClickListener(v -> resetTimer());
    }

    private void startTimer() {
        if (!isTimerRunning) {
            int hours = Integer.parseInt(inputHours.getText().toString().isEmpty() ? "0" : inputHours.getText().toString());
            int minutes = Integer.parseInt(inputMinutes.getText().toString().isEmpty() ? "0" : inputMinutes.getText().toString());
            int seconds = Integer.parseInt(inputSeconds.getText().toString().isEmpty() ? "0" : inputSeconds.getText().toString());

            timeLeftInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000;
            if (timeLeftInMillis > 0) {
                countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeLeftInMillis = millisUntilFinished;
                        updateTimerDisplay();
                    }

                    @Override
                    public void onFinish() {
                        isTimerRunning = false;
                        updateButtons();
                        showTimeUpDialog(); // Show "Time's up!" dialog when timer finishes

                        // Save the duration and the end time into SQLite
                        saveTimerData();
                    }
                }.start();

                isTimerRunning = true;
                updateButtons();
            }
        }
    }

    private void pauseTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel();
            isTimerRunning = false;
            updateButtons();
        }
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        timeLeftInMillis = 0;
        updateTimerDisplay();
        updateButtons();
    }

    private void updateTimerDisplay() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerDisplay.setText(timeFormatted);
    }

    private void updateButtons() {
        startButton.setEnabled(!isTimerRunning);
        pauseButton.setEnabled(isTimerRunning);
        resetButton.setEnabled(true);
    }

    private void showTimeUpDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Time's Up!")
                .setMessage("The timer has finished.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void saveTimerData() {
        long durationInMillis = (inputHours.getText().toString().isEmpty() ? 0 : Integer.parseInt(inputHours.getText().toString())) * 3600 * 1000
                + (inputMinutes.getText().toString().isEmpty() ? 0 : Integer.parseInt(inputMinutes.getText().toString())) * 60 * 1000
                + (inputSeconds.getText().toString().isEmpty() ? 0 : Integer.parseInt(inputSeconds.getText().toString())) * 1000;

        // Get the current time in a formatted string
        String endTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", Calendar.getInstance().getTime()).toString();

        // Insert into SQLite database
        databaseHelper.insertTimerData(durationInMillis, endTime);
    }
}
