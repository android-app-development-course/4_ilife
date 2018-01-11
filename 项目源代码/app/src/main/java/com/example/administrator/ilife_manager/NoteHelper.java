package com.example.administrator.ilife_manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018/1/3/003.
 */

public class NoteHelper extends SQLiteOpenHelper {
    public NoteHelper(Context context)
    {
        super(context,"iLife3",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE notebook(_id INTEGER PRIMARY KEY AUTOINCREMENT,day VARCHAR(20)," +
                "time VARCHAR(20),content INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
