package br.com.mynotes;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Controller.NoteController;
import DB.Connection;
import br.com.mynotes.adapters.NoteAdapter;
import br.com.mynotes.entities.Note;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> notes = new ArrayList<>();
    private Connection connection;
    private NoteController noteController;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.newNote) {
            Toast.makeText(this, "Criar nova anotação", Toast.LENGTH_SHORT).show();
            return true;
        }

        if(item.getItemId() == R.id.sortPriority) {
            Toast.makeText(this, "Ordenar por prioridade", Toast.LENGTH_SHORT).show();
            return true;
        }

        if(item.getItemId() == R.id.sortOrder) {
            Toast.makeText(this, "Ordenar por ordem", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.newNote) {
                    return true;
                }

                if(item.getItemId() == R.id.sortPriority) {
                    return true;
                }

                if(item.getItemId() == R.id.sortOrder) {
                    return true;
                }

                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        noteController = new NoteController(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(this, notes);
        recyclerView.setAdapter(noteAdapter);

        //createNotes();
        loadNotes();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void createNotes(){
        Model.Note note1 = new Model.Note("Note 1", "Note 1 text", "low");
        Model.Note note2 = new Model.Note("Note 2", "Note 2 text", "medium");
        Model.Note note3 = new Model.Note("Note 3", "Note 3 text", "high");

        this.noteController.create(note1);
        this.noteController.create(note2);
        this.noteController.create(note3);
    }

    private void loadNotes() {
        List<Model.Note> notesReturned = this.noteController.get();

        for (int i = 0; i < notesReturned.size(); i++) {
            this.notes.add(new Note(
                    notesReturned.get(i).getNote_title(),
                    notesReturned.get(i).getNote_text(),
                    notesReturned.get(i).getNote_priority()
            ));
        }

        noteAdapter.notifyDataSetChanged();
    }
}