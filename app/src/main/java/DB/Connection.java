package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Connection {
    private SQLiteDatabase database;
    private final DBInstructions sqliteOpenHelper;

    public Connection(Context context) {
        sqliteOpenHelper = new DBInstructions(context);
    }

    public void connect() {
        database = sqliteOpenHelper.getWritableDatabase();
    }

    public void disconnect() {
        if (database != null && database.isOpen()) {
            sqliteOpenHelper.close();
        }
    }

    public long create(String table, ContentValues dados)
    {
        long insertId = database.insert(table,null,dados);
        return insertId;
    }

    public long update(String table, ContentValues dados, String restricao)
    {
        long insertId = database.update(table,dados,restricao,null);
        return insertId;
    }

    public int delete(String table, String query)
    {
        return database.delete(table , query, null);
    }

    public Cursor get(String query)
    {   Cursor cursor=null;
        cursor = database.rawQuery(query, null);
        return cursor;
    }
}
