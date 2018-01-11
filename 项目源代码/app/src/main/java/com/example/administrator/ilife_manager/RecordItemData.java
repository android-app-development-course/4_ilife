package com.example.administrator.ilife_manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/12/19/019.
 */

public class RecordItemData extends SQLiteOpenHelper {
    public final static String MoneyTable="account_book";
    public final static String type="type",instruction="instruction",money="money",time="time";
    public RecordItemData(Context context)
    {
        super(context,"iLife",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE account_book(_id INTEGER PRIMARY KEY AUTOINCREMENT,type Integer," +
                "instruction VARCHAR(20),money INTEGER,time INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
