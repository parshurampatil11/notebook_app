package com.example.notebookv2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ContractClass {

    public static final String CONTENT_AUTHORITY = "com.example.notebookv2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);


    public static final String PATH_NOTES = "note_table";

    public ContractClass(){

    }

    public static final class NoteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_NOTES);

        public static final String CONTENT_LIST_TYPE =

                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +CONTENT_AUTHORITY + "/" +PATH_NOTES;

        public static final String CONTENT_LIST_ITEM =

                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +CONTENT_AUTHORITY + "/"+PATH_NOTES;

        public static final String TABLE_NAME = "note_table";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_NOTE_TILTE = "note_title";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_NOTE_SUBTITLE = "note_subtitle";

        public static final String COLUMN_NOTE_DESC = "note_desc";

        public static final String COLUMN_IMAGE = "note_img";

        public static final String COLUMN_COLOR = "note_color";

    }
}
