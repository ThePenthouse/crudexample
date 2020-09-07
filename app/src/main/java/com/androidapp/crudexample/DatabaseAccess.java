package com.androidapp.crudexample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseAccess {
    private SQLiteDatabase database;
    private DatabaseOpenHelper openHelper;
    private static volatile DatabaseAccess instance;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static synchronized DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public void save(Note note) {
        ContentValues values = new ContentValues();
        values.put("date", note.getTime());
        values.put("note", note.getText());
        database.insert(DatabaseOpenHelper.TABLE, null, values);
    }

    public void update(Note note) {
        ContentValues values = new ContentValues();
        values.put("date", new Date().getTime());
        values.put("note", note.getText());
        String date = Long.toString(note.getTime());
        database.update(DatabaseOpenHelper.TABLE, values, "date = ?", new String[]{date});
    }

    public void delete(Note note) {
        String date = Long.toString(note.getTime());
        database.delete(DatabaseOpenHelper.TABLE, "date = ?", new String[]{date});
    }

    public List getAllNotes() {
        List notes = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * From note ORDER BY date DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long time = cursor.getLong(0);
            String text = cursor.getString(1);
            notes.add(new Note(time, text));
            cursor.moveToNext();
        }
        cursor.close();
        return notes;
    }
}
