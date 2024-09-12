package br.com.mynotes.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import androidx.core.content.ContextCompat;

import br.com.mynotes.R;
import Model.Note;

public class NoteUtils {

    public static String getPriority(String value) {
        switch (value) {
            case "Baixa":
                return "low";
            case "Média":
                return "medium";
            case "Alta":
                return "high";
            default:
                return value;
        }
    }

    public static String getPtBrPriority(String value) {
        switch (value) {
            case "low":
                return "Baixa";
            case "medium":
                return "Média";
            case "high":
                return "Alta";
            default:
                return value;
        }
    }

    public static int getColorForPriority(Context context, String priority) {
        switch (priority) {
            case "low":
                return ContextCompat.getColor(context, R.color.low_priority);
            case "medium":
                return ContextCompat.getColor(context, R.color.medium_priority);
            case "high":
                return ContextCompat.getColor(context, R.color.high_priority);
            default:
                return ContextCompat.getColor(context, android.R.color.transparent);
        }
    }

    public static GradientDrawable createNoteBackground(Context context, String priority) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(25);
        drawable.setColor(getColorForPriority(context, priority));
        return drawable;
    }

    public static String formatNoteTitle(String title) {
        return title.trim();
    }

    public static String formatNoteDescription(String description) {
        return description.trim();
    }

}
