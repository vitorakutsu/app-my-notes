package br.com.mynotes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Controller.NoteController;
import Model.Note;
import br.com.mynotes.adapters.NoteAdapter;
import br.com.mynotes.utils.NoteUtils;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> notes = new ArrayList<>();
    private NoteController noteController;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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
        imageButton.setOnClickListener(v -> showPopupMenu(v));

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(this, notes);
        recyclerView.setAdapter(noteAdapter);

        loadNotes(null, null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void showNewNoteModal() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.note_modal);

        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );

        EditText titleEditText = dialog.findViewById(R.id.et_title);
        EditText descriptionEditText = dialog.findViewById(R.id.et_description);
        Spinner prioritySpinner = dialog.findViewById(R.id.spinner_priority);
        Button confirmButton = dialog.findViewById(R.id.btn_confirm);
        Button cancelButton = dialog.findViewById(R.id.btn_cancel);

        confirmButton.setOnClickListener(v -> {
            String title = NoteUtils.formatNoteTitle(titleEditText.getText().toString());
            String description = NoteUtils.formatNoteDescription(descriptionEditText.getText().toString());
            String priority = prioritySpinner.getSelectedItem().toString().trim();

            if (title.isEmpty() || description.isEmpty() || priority.isEmpty()) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Campos Vazio")
                        .setMessage("Por favor, preencha todos os campos.")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return;
            }

            Note newNote = new Note(title, description, NoteUtils.getPriority(priority));
            noteController.create(newNote);

            notes.add(newNote);
            noteAdapter.notifyDataSetChanged();

            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void showNoteDetailsModal(Note note) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.note_detail);

        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );

        TextView titleTextView = dialog.findViewById(R.id.noteTitle);
        TextView descriptionTextView = dialog.findViewById(R.id.noteDescription);
        TextView priorityTextView = dialog.findViewById(R.id.notePriority);
        Button closeButton = dialog.findViewById(R.id.btn_close);

        titleTextView.setText(NoteUtils.formatNoteTitle(note.getNote_title()));
        descriptionTextView.setText(NoteUtils.formatNoteDescription(note.getNote_text()));
        priorityTextView.setText(NoteUtils.getPtBrPriority(note.getNote_priority()));
        priorityTextView.setTextColor(NoteUtils.getColorForPriority(MainActivity.this, note.getNote_priority()));

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.newNote) {
                showNewNoteModal();
                return true;
            } else if (item.getItemId() == R.id.sortByLowPriority) {
                notes.clear();
                loadNotes("low", null);
                return true;
            } else if (item.getItemId() == R.id.sortByMediumPriority) {
                notes.clear();
                loadNotes("medium", null);
                return true;
            } else if (item.getItemId() == R.id.sortByHighPriority) {
                notes.clear();
                loadNotes("high", null);
                return true;
            } else if (item.getItemId() == R.id.sortByTitle) {
                notes.clear();
                loadNotes(null, "note_title");
                return true;
            } else if (item.getItemId() == R.id.sortByDescription) {
                notes.clear();
                loadNotes(null, "note_text");
                return true;
            } else if (item.getItemId() == R.id.clearNotes) {
                notes.clear();
                loadNotes(null, null);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    private void loadNotes(String priority, String sort) {
        List<Note> notesReturned = noteController.get(priority, sort);
        for (Note note : notesReturned) {
            notes.add(new Note(
                    note.getNote_id(),
                    note.getNote_title(),
                    note.getNote_text(),
                    note.getNote_priority()
            ));
        }
        noteAdapter.notifyDataSetChanged();
    }
}
