package alarmclock.app.com.alarmclock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import alarmclock.app.com.alarmclock.service.AlarmService;

import static alarmclock.app.com.alarmclock.service.AlarmService.ACTION_BOOT_COMPLETE;

/**
 * Created by Administrator on 6/9/2018.
 */

public class BootReceiver extends BroadcastReceiver {
    private final static String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Boot complete!");
        intent.putExtra(ACTION_BOOT_COMPLETE, "ACTION_BOOT_COMPLETE");
        AlarmService.enqueueWork(context, AlarmService.class, 1, intent);
    }
}
