package DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBInstructions extends SQLiteOpenHelper {
    private static final int VERSAO = 1;
    private static final String DATABASE_NAME = "mynotes.db";

    public DBInstructions(Context context) {
        super(context, DATABASE_NAME, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE notes " +
                "(note_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "note_title VARCHAR(50), " +
                "note_text VARCHAR(200), " +
                "note_priority VARCHAR(10));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }
}