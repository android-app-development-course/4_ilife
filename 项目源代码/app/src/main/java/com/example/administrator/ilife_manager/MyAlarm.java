package com.example.administrator.ilife_manager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Display the alarm information
 */
public class MyAlarm extends Activity {

    /**
     * An identifier for this notification unique within your application
     */
    public static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_alarm);
        setReminder(true);

    }



    private void setReminder(boolean b) {

        // get the AlarmManager instance
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        // create a PendingIntent that will perform a broadcast
        //PendingIntent pi= PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(this,TimeReceiver.class), 0);
        PendingIntent pi = PendingIntent.getService(MyAlarm.this, 0, new Intent(this, RecordService.class), 0);

        if (b) {
            // just use current time as the Alarm time.

            Calendar c = Calendar.getInstance();

            //Intent intent = getIntent();
            //Calendars cs;

           // cs = (Calendars) intent.getSerializableExtra("Calendars");
            //c=cs.getC();

           // c.set(2017,12,29,19,40,0);
            c=Calendar.getInstance();//获取对象
            //启动闹钟
            am.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis()+1,500, pi);



            //c.getTimeInMillis()
            // schedule an alarm

        }
    }

}
