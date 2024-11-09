// TimerHistoryActivity.java
package com.example.finalexam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class TimerHistoryActivity extends AppCompatActivity {

    private ListView timerHistoryListView;
    private DatabaseHelper databaseHelper;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_history);

        timerHistoryListView = findViewById(R.id.timerHistoryListView);
        databaseHelper = new DatabaseHelper(this);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(TimerHistoryActivity.this, TimerActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
        });

        displayTimerHistory();
    }

    private void displayTimerHistory() {
        List<TimerRecord> timerRecords = databaseHelper.getAllTimerRecords();

        if (!timerRecords.isEmpty()) {
            List<String> displayRecords = new ArrayList<>();

            for (TimerRecord record : timerRecords) {
                long duration = record.getDuration() / 1000;
                int hours = (int) (duration / 3600);
                int minutes = (int) ((duration % 3600) / 60);
                int seconds = (int) (duration % 60);
                String formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                String displayText = "Duration: " + formattedDuration + ", End Time: " + record.getEndTime();
                displayRecords.add(displayText);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayRecords);
            timerHistoryListView.setAdapter(adapter);
        }
        // If no records exist, do nothing; the ListView will remain empty.
    }
}
