package com.example.administrator.ilife_manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
//发送通知消息的服务
public class RecordService extends Service {
    private NotificationManager manager;

    public RecordService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent1, int flags, int startId) {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        //跳转意图
        Intent intent = new Intent(RecordService.this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        //通知栏显示内容
        builder.setTicker("notify_activity");
        //通知消息下拉是显示的文本内容
        builder.setContentText("钱都花光了吧？！今天又没学习吧？！");
        //通知栏消息下拉时显示的标题
        builder.setContentTitle("今天你记录了吗？");
        //接收到通知时，按手机的默认设置进行处理，声音，震动，灯
        builder.setDefaults(Notification.DEFAULT_ALL);
        //通知栏显示图标
        builder.setSmallIcon(R.drawable.record);
        builder.setContentIntent(pendingIntent);
        Notification notification=new Notification();
        notification = builder.build();
        //点击跳转后消失
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(1,notification);
        return super.onStartCommand(intent1, flags, startId);
    }





}
