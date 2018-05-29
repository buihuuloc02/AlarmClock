package alarmclock.app.com.alarmclock.activity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import alarmclock.app.com.alarmclock.R;

/**
 * Created by Administrator on 5/10/2018.
 */

public class AlarmService extends IntentService {
    private NotificationManager alarmNotificationManager;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        String str  =intent.getStringExtra("TEXT");
        sendNotification(str);
    }

    private void sendNotification(String msg) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, AlarmClockActivity.class), 0);

        Notification n = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_noti)
                //.setContentIntent(contentIntent)
                .setContentTitle("Alarm Clock!")
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0))
                .setAutoCancel(true).build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        alarmNotificationManager.notify(1, n);
        Log.d("AlarmService", "Notification sent.");
    }
}