package br.com.mynotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private String imagePath;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 102 && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                Uri savedImageUri = saveImageToExternalStorage(imageBitmap);
                if (savedImageUri != null) {
                    this.imagePath = getRealPathFromURI(savedImageUri);
                } else {
                    Toast.makeText(this, "Erro ao salvar a imagem da galeria", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao processar a imagem da galeria", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 103 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Uri imageUri = saveImageToExternalStorage(imageBitmap);
                if (imageUri != null) {
                    this.imagePath = getRealPathFromURI(imageUri);
                } else {
                    Toast.makeText(this, "Erro ao salvar a imagem da câmera", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 103) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permissões necessárias não foram concedidas", Toast.LENGTH_SHORT).show();
            }
        }
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
        }

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
        Button btnTakePicture = dialog.findViewById(R.id.btn_Take_Picture);

        btnTakePicture.setOnClickListener(v -> {
            showImageOptions();
        });

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

            Note newNote = new Note(title, description, NoteUtils.getPriority(priority), this.imagePath);
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
        ImageView noteImageView = dialog.findViewById(R.id.imageNote);
        Button closeButton = dialog.findViewById(R.id.btn_close);

        titleTextView.setText(NoteUtils.formatNoteTitle(note.getNote_title()));
        descriptionTextView.setText(NoteUtils.formatNoteDescription(note.getNote_text()));
        priorityTextView.setText(NoteUtils.getPtBrPriority(note.getNote_priority()));
        priorityTextView.setTextColor(NoteUtils.getColorForPriority(MainActivity.this, note.getNote_priority()));

        if(note.getImage_url() != null){
            File imgFile = new File(note.getImage_url());
            if(imgFile.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(note.getImage_url());
                if(bitmap != null){
                    noteImageView.setImageBitmap(bitmap);
                }
                else{
                    noteImageView.setImageResource(android.R.drawable.ic_menu_camera);
                }
            } else {
                noteImageView.setImageResource(android.R.drawable.ic_menu_camera);
            }
        }
        else{
            noteImageView.setImageResource(android.R.drawable.ic_menu_camera);
        }


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
                    note.getNote_priority(),
                    note.getImage_url()
            ));
        }
        noteAdapter.notifyDataSetChanged();
    }

    private void showImageOptions() {
        final CharSequence[] options = {"Tirar Foto", "Escolher da Galeria", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Escolha uma opção");
        builder.setItems(options, (dialog, which) -> {
            if (options[which].equals("Tirar Foto")) {
                openCamera();
            } else if (options[which].equals("Escolher da Galeria")) {
                openGallery();
            } else if (options[which].equals("Cancelar")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 102);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 103);
    }


    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = null;
        String path = null;

        try {
            cursor = getContentResolver().query(contentUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(column_index);
            } else {
                path = contentUri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    private Uri saveImageToExternalStorage(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return Uri.fromFile(file);
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}
