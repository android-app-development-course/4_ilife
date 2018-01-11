package com.example.administrator.ilife_manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

//编辑时间的界面！！！！！
public class TimeRecord extends AppCompatActivity {
    private TextView edt_time;
    private Button ok,editRemindTime;
    private java.util.Calendar c;
    final java.util.Calendar ca = java.util.Calendar.getInstance();
    int mHour=ca.get(java.util.Calendar.HOUR_OF_DAY);
    int mMinute=ca.get(java.util.Calendar.MINUTE);
    TimePickerDialog.OnTimeSetListener mtimeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_record);
        init();
        InputTime();
    }

    public void init(){


        edt_time=(TextView) findViewById(R.id.remindTimeTV);
        ok=(Button) findViewById(R.id.ok);
        editRemindTime=(Button)findViewById(R.id.editRemindTime);
        mtimeListener=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                mHour=hour;
                mMinute=minute;
                edt_time.setText(mHour+" : "+mMinute);
            }
        };

        editRemindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog timeDialog=new TimePickerDialog(TimeRecord.this,mtimeListener,mHour,mMinute,true);
                timeDialog.show();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Savetime();
            }
        });
    }



    public void InputTime(){
        String str=edt_time.getText().toString();
        c= java.util.Calendar.getInstance();
        c.set(2017,12,29,19,35,15);

    }
    public void Savetime(){
        //打开保存成功的界面
        Intent intent=new Intent(this,RecordService.class);
        startService(intent);
        finish();

    }

}
