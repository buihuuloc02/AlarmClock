package alarmclock.app.com.alarmclock.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Administrator on 6/9/2018.
 */

public class BootReceiver extends BroadcastReceiver {
    private final static String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Boot complete!");
        AlarmService.enqueueWork(context, AlarmService.class, 1, intent);
    }
}