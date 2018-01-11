package com.example.administrator.ilife_manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/12/23/023.
 */

public class MonthData extends SQLiteOpenHelper {
    public final static String monthTable="month_data";
    public final static int TIME=1,MONTH=2,EATTING=3,TRANSPORTATION=4,HOUSE=6,PURCHASE=5,ENTERTAIMENT=7;
    public final static int EDUCATION=8,HOSPITAL=9,FRIEND=10,OTHER=11,ACCIDENT=12,SALARY=13,REDPACKET=14;
    public final static int LIVINGEXPENSE=15,BONUS=16,REIMBURSE=17,PARTTIME=18,BORROW=19,INVEST=20,ACCIDENTMONEY=21,INPUT_OTHER=22,OUT_ALL=23,IN_ALL=24;
    public MonthData(Context context)
    {
        super(context,"iLife2",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE month_data(_id INTEGER PRIMARY KEY AUTOINCREMENT,time VARCHAR(20)," +
                "month VARCHAR(20),eatting INTEGER,transportation INTEGER,house INTEGER,purchase INTEGER," +
                "entertainment INTEGER,education INTEGER,hospital INTEGER,friend INTEGER,other INTEGER,accident INTEGER,"+
                "salary INTEGER,redpacket INTEGER,livingexpense INTEGER,bonus INTEGER,reimburse INTEGER,parttime INTEGER,borrow INTEGER,"+
                "invest INTEGER,accidentmoney INTEGER,input_other INTEGER,outputall INTEGER,incomeall Integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
