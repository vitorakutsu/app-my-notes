package Service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import DB.Connection;
import Model.Note;

public class NoteService {
    private Connection connection;

    public NoteService(Context context) {
        connection = new Connection(context);
    }

    public void create(Note note) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("note_title", note.getNote_title());
        contentValues.put("note_text", note.getNote_text());
        contentValues.put("note_priority", note.getNote_priority());

        connection.connect();
        connection.create("notes", contentValues);
    }

    public Cursor get() {
        connection.connect();
        Cursor cursor = connection.get("SELECT * FROM notes");
        return cursor;
    }
}
