package com.example.finalexam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "timer.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and columns
    private static final String TABLE_NAME = "timer_data";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_END_TIME = "end_time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DURATION + " INTEGER, "
                + COLUMN_END_TIME + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the table if it exists and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertTimerData(long duration, String endTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DURATION, duration);
        contentValues.put(COLUMN_END_TIME, endTime);

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    public List<TimerRecord> getAllTimerRecords() {
        List<TimerRecord> timerRecords = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int durationIndex = cursor.getColumnIndex(COLUMN_DURATION);
            int endTimeIndex = cursor.getColumnIndex(COLUMN_END_TIME);

            if (durationIndex != -1 && endTimeIndex != -1) {
                do {
                    long duration = cursor.getLong(durationIndex);
                    String endTime = cursor.getString(endTimeIndex);
                    timerRecords.add(new TimerRecord(duration, endTime));
                } while (cursor.moveToNext());
            } else {
                // Optionally, handle the case where columns are missing (e.g., notify user or skip this part).
            }
        }

        cursor.close();
        db.close();
        return timerRecords;
    }
}
