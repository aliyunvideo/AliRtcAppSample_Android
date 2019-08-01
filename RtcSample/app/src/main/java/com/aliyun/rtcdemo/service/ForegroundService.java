package com.aliyun.rtcdemo.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import com.aliyun.rtcdemo.R;
import com.aliyun.rtcdemo.activity.AliRtcChatActivity;

import static android.support.v4.app.NotificationCompat.FLAG_NO_CLEAR;
import static android.support.v4.app.NotificationCompat.FLAG_ONGOING_EVENT;

public class ForegroundService extends Service {
    private static final int NOTIFICATION_FLAG = 0x11;

    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intentChart = new Intent(this,AliRtcChatActivity.class);
        if (intent != null && intent.getExtras() != null) {
            intentChart.putExtras(intent.getExtras());
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intentChart,0);
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        builder.setContentIntent(pendingIntent)
            .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.ic_launcher))
            .setContentTitle("音视频通话中")
            .setContentText("正在进行音视频通话")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setWhen(System.currentTimeMillis())
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setPriority(Notification.PRIORITY_HIGH);
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= FLAG_ONGOING_EVENT;
        notification.flags |= FLAG_NO_CLEAR;
        startForeground(120,notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }
}
