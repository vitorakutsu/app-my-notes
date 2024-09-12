package br.com.mynotes.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Controller.NoteController;
import Model.Note;
import br.com.mynotes.MainActivity;
import br.com.mynotes.R;
import br.com.mynotes.utils.NoteUtils;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private Context context;
    private NoteController noteController;

    private static final int LONG_PRESS_DURATION = 2000;

    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
        this.noteController = new NoteController(context);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Note note = notes.get(position);
        holder.titleTextView.setText(note.getNote_title());
        holder.descriptionTextView.setText(note.getNote_text());

        String priority = NoteUtils.getPriority(note.getNote_priority());
        GradientDrawable drawable = NoteUtils.createNoteBackground(context, priority);
        holder.itemView.setBackground(drawable);

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    startDeleteTimer(holder, position);
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    // Detect a short click (tap up) and show note details
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).showNoteDetailsModal(note);
                    } else {
                        Toast.makeText(context, "Contexto inválido", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

            private Handler handler = new Handler();
            private Runnable deleteRunnable;

            private void startDeleteTimer(NoteViewHolder holder, int position) {
                deleteRunnable = new Runnable() {
                    @Override
                    public void run() {
                        int adapterPosition = holder.getAdapterPosition();
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            deleteNoteAt(adapterPosition);
                        }
                    }
                };
                handler.postDelayed(deleteRunnable, LONG_PRESS_DURATION);
            }

            private void cancelDeleteTimer() {
                if (deleteRunnable != null) {
                    handler.removeCallbacks(deleteRunnable);
                }
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startDeleteTimer(holder, holder.getAdapterPosition());
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        cancelDeleteTimer();
                        return true;
                }
                return false;
            }
        });
    }

    private void deleteNoteAt(int position) {
        Note noteToDelete = notes.get(position);
        noteController.delete(noteToDelete.getNote_id());
        notes.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(context, "Anotação excluída", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_title);
            descriptionTextView = itemView.findViewById(R.id.note_text);
        }
    }
}
