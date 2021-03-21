package com.example.notebookv2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NoteProvider extends ContentProvider {

    private static final String LOG_TAG = NoteProvider.class.getSimpleName();

    private NoteDBHelper noteDBHelper;

    private static final int NOTES = 100;

    private static final int NOTES_ID = 101;

    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        mUriMatcher.addURI(ContractClass.CONTENT_AUTHORITY,ContractClass.PATH_NOTES,NOTES);
        mUriMatcher.addURI(ContractClass.CONTENT_AUTHORITY,ContractClass.PATH_NOTES+"/#",NOTES_ID);
    }

    @Override
    public boolean onCreate() {
        noteDBHelper = new NoteDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = noteDBHelper.getReadableDatabase();
        Cursor cursor;
        int match = mUriMatcher.match(uri);
        switch (match){
            case NOTES:
                cursor = db.query(ContractClass.NoteEntry.TABLE_NAME,projection,
                        selection,selectionArgs,null,null,sortOrder);
                break;
            case NOTES_ID:
                selection = ContractClass.NoteEntry._ID+ "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(ContractClass.NoteEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query Unkonwn query"+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = mUriMatcher.match(uri);
        switch (match){
            case NOTES:
                return ContractClass.NoteEntry.CONTENT_LIST_TYPE;
            case NOTES_ID:
                return ContractClass.NoteEntry.CONTENT_LIST_ITEM;
            default:
                throw new IllegalStateException("Unkonwn URI"+uri+" with match"+match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = mUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                return insertNote(uri, values);
            default:
                throw new IllegalArgumentException("Insertion not Supported" + uri);
        }
    }

    private Uri insertNote(Uri uri,ContentValues values){
        
        SQLiteDatabase db = noteDBHelper.getWritableDatabase();

        long newRow = db.insert(ContractClass.NoteEntry.TABLE_NAME,null,values);
        Toast.makeText(getContext(),"Row Added",Toast.LENGTH_SHORT).show();
        if(newRow==-1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,newRow);

    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = noteDBHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowdeleted;
        switch (match){

            case NOTES:
                rowdeleted = db.delete(ContractClass.NoteEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case NOTES_ID:
                selection = ContractClass.NoteEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowdeleted = db.delete(ContractClass.NoteEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported "+uri);

        }
        if (rowdeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowdeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = mUriMatcher.match(uri);
        switch (match){
            case NOTES:
                return noteUpdate(uri,values,selection,selectionArgs);
            case NOTES_ID:
                selection = ContractClass.NoteEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return noteUpdate(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not Supported");
        }

    }

    private int noteUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = noteDBHelper.getWritableDatabase();
        int rowUpdated = db.update(ContractClass.NoteEntry.TABLE_NAME,values,selection,selectionArgs);
        if(rowUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowUpdated;
    }
}
