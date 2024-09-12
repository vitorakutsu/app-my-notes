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
        long id = connection.create("notes", contentValues);
        connection.disconnect();
        note.setNote_id((int) id);
    }

    public Cursor get(String priority, String sortOrder) {
        connection.connect();

        String whereClause = "";
        if (priority != null && !priority.isEmpty()) {
            whereClause = "note_priority = '" + priority + "'";
        }

        String orderByClause = "";
        if (sortOrder != null && !sortOrder.isEmpty()) {
            orderByClause = "ORDER BY " + sortOrder;
        }

        String query = "SELECT * FROM notes";
        if (!whereClause.isEmpty()) {
            query += " WHERE " + whereClause;
        }
        if (!orderByClause.isEmpty()) {
            query += " " + orderByClause;
        }

        Cursor cursor = connection.get(query);
        return cursor;
    }


    public void delete(int noteId) {
        connection.connect();
        connection.delete("notes", "note_id = " + noteId);
        connection.disconnect();
    }
}
