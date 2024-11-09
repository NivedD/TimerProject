package com.example.finalexam;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SoundSettingsActivity extends AppCompatActivity {

    private ListView soundListView;
    private Button previewButton, selectButton;
    private String[] soundOptions = {"Sound 1", "Sound 2", "Sound 3"};
    private int selectedSoundPosition = -1; // Track the selected sound
    private MediaPlayer mediaPlayer;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_settings);

        // Initialize UI elements
        soundListView = findViewById(R.id.soundListView);
        previewButton = findViewById(R.id.previewButton);
        selectButton = findViewById(R.id.selectButton);

        // Initialize SharedPreferences to save the selected sound
        sharedPreferences = getSharedPreferences("SoundPreferences", Context.MODE_PRIVATE);

        // Set up ListView with sound options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, soundOptions);
        soundListView.setAdapter(adapter);

        // Set item click listener to allow selecting a sound
        soundListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedSoundPosition = position; // Track which sound is selected
            Toast.makeText(SoundSettingsActivity.this, "Selected: " + soundOptions[position], Toast.LENGTH_SHORT).show();
        });

        // Preview button to play the selected sound
        previewButton.setOnClickListener(v -> {
            if (selectedSoundPosition != -1) {
                playSound(selectedSoundPosition);
            } else {
                Toast.makeText(SoundSettingsActivity.this, "Please select a sound first", Toast.LENGTH_SHORT).show();
            }
        });

        // Select button to save the selected sound
        selectButton.setOnClickListener(v -> {
            if (selectedSoundPosition != -1) {
                saveSelectedSound(selectedSoundPosition);
                Toast.makeText(SoundSettingsActivity.this, "Sound Selected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SoundSettingsActivity.this, "Please select a sound first", Toast.LENGTH_SHORT).show();
            }
        });

        // Load previously saved sound preference
        loadSavedSound();

        // Set up the back button in the action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadSavedSound() {
        selectedSoundPosition = sharedPreferences.getInt("selectedSound", -1);
        if (selectedSoundPosition != -1) {
            soundListView.setItemChecked(selectedSoundPosition, true); // Set the ListView item as checked
        }
    }

    private void playSound(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        // Play different sound based on the selection
        int soundResId = getSoundResourceId(position);
        if (soundResId != -1) {
            mediaPlayer = MediaPlayer.create(this, soundResId);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> mp.release()); // Release mediaPlayer when done
        }
    }

    private int getSoundResourceId(int position) {
        switch (position) {
            case 0:
                return R.raw.sound1; // Replace with actual sound resource IDs
            case 1:
                return R.raw.sound2;
            case 2:
                return R.raw.sound3;
            default:
                return -1;
        }
    }

    private void saveSelectedSound(int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedSound", position);
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Release media player resources when activity is stopped
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release media player resources when activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    // Handle back button in the action bar
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to TimerActivity when back button is pressed
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
