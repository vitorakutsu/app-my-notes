package Model;

public class Note {
    private int note_id;
    private String note_title;
    private String note_text;
    private String note_priority;
    private String image_url;

    public Note(){}

    public Note(int note_id, String note_title, String note_text, String note_priority, String image_url) {
        this.note_id = note_id;
        this.note_title = note_title;
        this.note_text = note_text;
        this.note_priority = note_priority;
        this.image_url = image_url;
    }

    public Note(String note_title, String note_text, String note_priority, String image_url) {
        this.note_title = note_title;
        this.note_text = note_text;
        this.note_priority = note_priority;
        this.image_url = image_url;
    }

    public int getNote_id() {
        return note_id;
    }

    public void setNote_id(int note_id) {
        this.note_id = note_id;
    }

    public String getNote_title() {
        return note_title;
    }

    public void setNote_title(String note_title) {
        this.note_title = note_title;
    }

    public String getNote_text() {
        return note_text;
    }

    public void setNote_text(String note_text) {
        this.note_text = note_text;
    }

    public String getNote_priority() {
        return note_priority;
    }

    public void setNote_priority(String note_priority) {
        this.note_priority = note_priority;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
