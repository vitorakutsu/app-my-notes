package Controller;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import DB.Connection;
import Model.Note;
import Service.NoteService;

public class NoteController {
    private NoteService notesService;

    public NoteController(Context context) {
        notesService = new NoteService(context);
    }

    public void create(Note note) {
        notesService.create(note);
    }

    @SuppressLint("Range")
    public List<Note> get(String priority, String sort){
        Cursor cursor = notesService.get(priority, sort);

        try{
            List<Note> notes = new ArrayList<>();
            if(cursor.moveToFirst()){
                do{
                    Note note = new Note();
                    note.setNote_id(cursor.getInt(cursor.getColumnIndex("note_id")));
                    note.setNote_title(cursor.getString(cursor.getColumnIndex("note_title")));
                    note.setNote_text(cursor.getString(cursor.getColumnIndex("note_text")));
                    note.setNote_priority(cursor.getString(cursor.getColumnIndex("note_priority")));
                    notes.add(note);
                }while(cursor.moveToNext());
            }

            return notes;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(int noteId) {
        notesService.delete(noteId);
    }
}
