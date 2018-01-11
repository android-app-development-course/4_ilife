package com.example.administrator.ilife_manager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class editNote extends AppCompatActivity {

    NoteHelper noteHelper;
    Button returnBtn;
    TextView saveBtn;
    EditText content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note);
        noteHelper=new NoteHelper(this);
        saveBtn=(TextView)findViewById(R.id.noteSaveBtn);
        returnBtn=(Button)findViewById(R.id.editNoteReturnBtn) ;
        content=(EditText)findViewById(R.id.noteContentET) ;


        final Calendar ca = Calendar.getInstance();
        final int mYear = ca.get(Calendar.YEAR);
        final int mMonth = ca.get(Calendar.MONTH)+1;//得到实际月份
        final int mDay = ca.get(Calendar.DAY_OF_MONTH);
        final int mHour=ca.get(Calendar.HOUR_OF_DAY);
        final int mMinute=ca.get(Calendar.MINUTE);



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db=noteHelper.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("content",content.getText().toString());
                values.put("time",""+mYear+mMonth+mDay+mHour+mMinute);
                db.insert("notebook",null,values);
                db.close();
                editNote.super.onBackPressed();
            }
        });
    }


}
