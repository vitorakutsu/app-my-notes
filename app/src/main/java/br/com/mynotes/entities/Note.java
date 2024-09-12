package br.com.mynotes.entities;

public class Note {
    private String title;
    private String text;
    private String priority;

    public Note(String title, String text, String priority) {
        this.title = title;
        this.text = text;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getPriority() {
        return priority;
    }
}

