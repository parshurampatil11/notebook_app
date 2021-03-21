package com.example.notebookv2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "notebook.db";

    public static final int DATABASE_VERSION = 2;

    public NoteDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_TABLE = "CREATE TABLE " + ContractClass.NoteEntry.TABLE_NAME + " ("
                + ContractClass.NoteEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ContractClass.NoteEntry.COLUMN_NOTE_TILTE + " TEXT NOT NULL, "
                + ContractClass.NoteEntry.COLUMN_NOTE_SUBTITLE + " TEXT, "
                + ContractClass.NoteEntry.COLUMN_NOTE_DESC + " TEXT, "
                + ContractClass.NoteEntry.COLUMN_IMAGE + " BLOB, "
                + ContractClass.NoteEntry.COLUMN_DATE + " TEXT, "
                + ContractClass.NoteEntry.COLUMN_COLOR + " TEXT );";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ ContractClass.NoteEntry.TABLE_NAME);
        onCreate(db);
    }
}
