package com.example.notebookv2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.notebookv2.data.ContractClass;
import com.example.notebookv2.data.NoteDBHelper;
import com.example.notebookv2.data.NoteProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    CustomAdapter customAdapter;
    private static final int NOTE_LOADER = 0;
    private Uri mCurrentUri;
    private final static int PICK_IMG = 1000;
    private final static int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton floatingActionButton = findViewById(R.id.action_button);
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,NoteEditActivity.class);
            startActivity(intent);
        });

        ListView listView = findViewById(R.id.list_view);
        customAdapter = new CustomAdapter(this,null);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this,NoteEditActivity.class);
            mCurrentUri = ContentUris.withAppendedId(ContractClass.NoteEntry.CONTENT_URI,id);
            intent.setData(mCurrentUri);
            startActivity(intent);
        });

        ImageButton cameraBut = findViewById(R.id.camera);

        cameraBut.setOnClickListener(v -> {
            try {

                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);

            }catch (Exception e){
                e.printStackTrace();
            }
        });

        ImageButton galleryBut = findViewById(R.id.imageHome);
        galleryBut.setOnClickListener(v -> {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED){
                    String[] strings = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(strings,PICK_IMG);
                }else {
                    addImageToDatabase();
                }
            }else {
                addImageToDatabase();
            }
        });

        getLoaderManager().initLoader(NOTE_LOADER,null,this);
    }

    private void addImageToDatabase() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("image/*");
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0]==
                        PackageManager.PERMISSION_GRANTED){
                    addImageToDatabase();
                }else {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={
                ContractClass.NoteEntry._ID,
                ContractClass.NoteEntry.COLUMN_NOTE_TILTE,
                ContractClass.NoteEntry.COLUMN_NOTE_SUBTITLE,
                ContractClass.NoteEntry.COLUMN_NOTE_DESC,
                ContractClass.NoteEntry.COLUMN_DATE,
                ContractClass.NoteEntry.COLUMN_IMAGE,
                ContractClass.NoteEntry.COLUMN_COLOR

        };
        return new CursorLoader(this, ContractClass.NoteEntry.CONTENT_URI,projection,null,null,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        customAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        customAdapter.swapCursor(null);
    }

}