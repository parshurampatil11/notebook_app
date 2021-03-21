package com.example.notebookv2;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.AlertDialog;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;

import android.content.pm.PackageManager;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notebookv2.data.ContractClass;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private EditText mTitleNote,mSubtitleNote,mDesdNote;
    private TextView mDateText;
    private Uri mCurrentUri;
    private final int NOTE_LOADER = 0;
    private Button yellowBut, redBut, blueBut, blackBut, grayBut;
    private ImageButton popupImg;
    private final static int PICK_IMG = 1000;
    private final static int PERMISSION_CODE = 1001;
    private ImageView addImg;
    BottomSheetDialog bottomSheetDialog;
    String currentDateandTime;
    byte[] bytes;
    String date;
    private String selectedNoteColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageButton deleteBut = findViewById(R.id.deleteBut);
        selectedNoteColor = "#333333";

        Intent intent = getIntent();
        mCurrentUri = intent.getData();
        if(mCurrentUri==null){
            setTitle("");
            invalidateOptionsMenu();
            deleteBut.setVisibility(View.GONE);
        }else {
            setTitle("");
            getLoaderManager().initLoader(NOTE_LOADER,null,this);

        }

        mTitleNote = findViewById(R.id.note_title);
        mSubtitleNote = findViewById(R.id.note_subtitle);
        mDesdNote = findViewById(R.id.desc_note);
        mDateText = findViewById(R.id.date_text);
        addImg = findViewById(R.id.add_img);

        Button missBut = findViewById(R.id.missleneousBut);


        deleteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentUri!=null) {
                    showDialogBoxDelete1();
                }else {
                    Toast.makeText(getApplicationContext(),"First Add details",Toast.LENGTH_SHORT).show();
                }
            }

        });
        missBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewGroup viewGroup = findViewById(android.R.id.content);

                bottomSheetDialog = new BottomSheetDialog(NoteEditActivity.this);
                View view = getLayoutInflater().inflate(R.layout.extra_bit,null);

                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();
                grayBut = view.findViewById(R.id.grayBut);
                yellowBut = view.findViewById(R.id.yelloBut);
                redBut = view.findViewById(R.id.redBut);
                blueBut = view.findViewById(R.id.blueBut);
                blackBut = view.findViewById(R.id.blackBut);


                popupImg = view.findViewById(R.id.popupImag);

                grayBut.setOnClickListener(v1 -> {
                    selectedNoteColor = "#333333";
                    Toast.makeText(getApplicationContext(),"Color Set",Toast.LENGTH_SHORT).show();
                });
                yellowBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedNoteColor = "#FFFF00";
                        Toast.makeText(getApplicationContext(),"Color Set",Toast.LENGTH_SHORT).show();
                    }
                });
                redBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedNoteColor = "#FF0000";
                        Toast.makeText(getApplicationContext(),"Color Set",Toast.LENGTH_SHORT).show();
                    }
                });
                blueBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedNoteColor = "#0C3BE8";
                        Toast.makeText(getApplicationContext(),"Color Set",Toast.LENGTH_SHORT).show();
                    }
                });
                blackBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedNoteColor = "#FF000000";
                        Toast.makeText(getApplicationContext(),"Color Set",Toast.LENGTH_SHORT).show();
                    }
                });


                popupImg.setOnClickListener(v12 -> {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED) {
                            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                            requestPermissions(permission,PICK_IMG);
                        }else {
                            pickImageFromGallery();
                        }
                    }else {
                        pickImageFromGallery();
                    }
                });

            }
        });

       date = displayDate();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.add_item:
                insertNote();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void insertNote(){


        String noteTitle = mTitleNote.getText().toString().trim();
        String noteSubtitle = mSubtitleNote.getText().toString().trim();
        String noteDesc = mDesdNote.getText().toString().trim();
        String noteColor = selectedNoteColor;

        ContentValues values = new ContentValues();
        values.put(ContractClass.NoteEntry.COLUMN_NOTE_TILTE,noteTitle);
        values.put(ContractClass.NoteEntry.COLUMN_NOTE_SUBTITLE,noteSubtitle);
        values.put(ContractClass.NoteEntry.COLUMN_NOTE_DESC,noteDesc);
        values.put(ContractClass.NoteEntry.COLUMN_IMAGE,bytes);
        values.put(ContractClass.NoteEntry.COLUMN_DATE,date);
        values.put(ContractClass.NoteEntry.COLUMN_COLOR,noteColor);

        if(mCurrentUri == null){
            Uri newUri = getContentResolver().insert(ContractClass.NoteEntry.CONTENT_URI,values);
            if(newUri == null){
                Toast.makeText(this,"Insert Failed",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"Insert",Toast.LENGTH_SHORT).show();
            }
        }else {
            int rowAffect = getContentResolver().update(mCurrentUri,values,null,null);
            if(rowAffect==0){
                Toast.makeText(this,"Update Failed",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"Update",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String displayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd  HH:mm:ss", Locale.getDefault());
        currentDateandTime = sdf.format(new Date());
        mDateText.setText(currentDateandTime);
        return currentDateandTime;
    }

    private byte[] convertImageViewToByteArray(ImageView imageView){

        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private Bitmap convertByteArrayToBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ContractClass.NoteEntry._ID,
                ContractClass.NoteEntry.COLUMN_NOTE_TILTE,
                ContractClass.NoteEntry.COLUMN_NOTE_SUBTITLE,
                ContractClass.NoteEntry.COLUMN_NOTE_DESC,
                ContractClass.NoteEntry.COLUMN_IMAGE
        };
        return new CursorLoader(this,mCurrentUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(cursor==null || cursor.getCount()<1){
            return;
        }
        if (cursor.moveToFirst()){
            int mTitle = cursor.getColumnIndex(ContractClass.NoteEntry.COLUMN_NOTE_TILTE);
            int mSub = cursor.getColumnIndex(ContractClass.NoteEntry.COLUMN_NOTE_SUBTITLE);
            int mDesc = cursor.getColumnIndex(ContractClass.NoteEntry.COLUMN_NOTE_DESC);
            int mImg = cursor.getColumnIndex(ContractClass.NoteEntry.COLUMN_IMAGE);

            String title = cursor.getString(mTitle);
            String sub = cursor.getString(mSub);
            String desc = cursor.getString(mDesc);

            byte[] imgByte = cursor.getBlob(mImg);
            if(imgByte != null){
                Bitmap bitmap = convertByteArrayToBitmap(imgByte);
                addImg.setImageBitmap(bitmap);

            }
            mTitleNote.setText(title);
            mSubtitleNote.setText(sub);
            mDesdNote.setText(desc);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitleNote.setText("");
        mSubtitleNote.setText("");
        mDesdNote.setText("");
    }

    private void showDialogBoxDelete1(){

        ViewGroup viewGroup = findViewById(android.R.id.content);

        TextView deleteButDig,deleteCancelButDig;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_box,viewGroup,false);
        builder.setCancelable(false);
        builder.setView(view);

        deleteButDig = view.findViewById(R.id.deleteButDig);
        deleteCancelButDig = view.findViewById(R.id.deleteCancelButDig);

        AlertDialog alertDialog = builder.create();
        deleteButDig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    deleteData();
            }
        });

        deleteCancelButDig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void deleteData() {
        if(mCurrentUri!=null){
            int rowDeleted = getContentResolver().delete(mCurrentUri,null,null);

            if(rowDeleted==0){
                Toast.makeText(this,"Delete Failed",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"Delete Note Successfully",Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void pickImageFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,PICK_IMG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                }else {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK  && requestCode == PICK_IMG){
            addImg.setImageURI(data.getData());
            bytes = convertImageViewToByteArray(addImg);
        }
    }
}