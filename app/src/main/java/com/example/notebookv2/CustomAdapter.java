package com.example.notebookv2;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.notebookv2.data.ContractClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomAdapter extends CursorAdapter {

    Context context;
    public CustomAdapter(Context context, Cursor cursor) {
        super(context, cursor,0);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameText = (TextView) view.findViewById(R.id.textTitle);
        TextView subText = (TextView) view.findViewById(R.id.textSubtitle);
        TextView dateText = (TextView) view.findViewById(R.id.textDate);
        ImageView imageSet = (ImageView) view.findViewById(R.id.imgSet);

        int nameColoumnIndex = cursor.getColumnIndex(ContractClass.NoteEntry.COLUMN_NOTE_TILTE);
        int subColoumnIndex = cursor.getColumnIndex(ContractClass.NoteEntry.COLUMN_NOTE_SUBTITLE);
        int imgColoumnIndex = cursor.getColumnIndex(ContractClass.NoteEntry.COLUMN_IMAGE);
        int dateColoumnIndex = cursor.getColumnIndex(ContractClass.NoteEntry.COLUMN_DATE);
        int colorColumnIndex = cursor.getColumnIndex(ContractClass.NoteEntry.COLUMN_COLOR);

        String title = cursor.getString(nameColoumnIndex);
        String sub = cursor.getString(subColoumnIndex);
        String date = cursor.getString(dateColoumnIndex);
        String color = cursor.getString(colorColumnIndex);

        byte[] bytes = cursor.getBlob(imgColoumnIndex);
        if(bytes!=null) {

            Bitmap bitmap = convertByteArrayToBitmap(bytes);
            imageSet.setImageBitmap(bitmap);
            imageSet.setVisibility(View.VISIBLE);
            dateText.setText(date);
            dateText.setBackgroundColor(Color.parseColor(color));
        }else{
            imageSet.setVisibility(View.GONE);
        }
        if(title.trim().isEmpty()){
            nameText.setVisibility(View.GONE);
            dateText.setText(date);
            dateText.setBackgroundColor(Color.parseColor(color));
        }else {
            nameText.setText(title);
            dateText.setText(date);
            nameText.setBackgroundColor(Color.parseColor(color));
            dateText.setBackgroundColor(Color.parseColor(color));
        }
        if(sub.trim().isEmpty()){
            dateText.setText(date);
            dateText.setBackgroundColor(Color.parseColor(color));
            subText.setVisibility(View.GONE);
        }else {
            subText.setText(sub);
            dateText.setText(date);
            subText.setBackgroundColor(Color.parseColor(color));
            dateText.setBackgroundColor(Color.parseColor(color));
        }
    }

    private Bitmap convertByteArrayToBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

}
