package alarmclock.app.com.alarmclock.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import alarmclock.app.com.alarmclock.BuildConfig;
import alarmclock.app.com.alarmclock.model.ItemAlarm;
import alarmclock.app.com.alarmclock.util.DatabaseHelper;
import alarmclock.app.com.alarmclock.util.SharePreferenceHelper;

import static alarmclock.app.com.alarmclock.util.Constant.ACTION_ALARM_CLOCK;
import static alarmclock.app.com.alarmclock.util.Constant.HOUR;
import static alarmclock.app.com.alarmclock.util.Constant.MINUTE;

/**
 * Created by Administrator on 5/10/2018.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private final static String TAG = AlarmReceiver.class.getSimpleName();
    private int hLast;
    private int mLast;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "wakwclock");
        mWakeLock.acquire();
        SharePreferenceHelper sharePreferenceHelper = SharePreferenceHelper.getInstances(context);
        hLast = sharePreferenceHelper.getInt(HOUR, 0);
        mLast = sharePreferenceHelper.getInt(MINUTE, 0);
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        String strH = h + "";
            String strM = m + "";
            if (h < 10) {
                strH = "0" + h;
            }
            if (m < 10) {
                strM = "0" + m;
            }
//        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Intent intentService = new Intent(context, AlarmService.class);
//           intentService.putExtra(EXTRA_TIME, strH + " : " + strM);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(intentService);
//        } else {
//            //startService(myService);
//
//        }
        //startWakefulService(context, intentService);
        AlarmService.enqueueWork(context, AlarmService.class,1, intent);
        if (h != hLast || m != mLast) {
//            hLast = h;
//            mLast = m;
//            sharePreferenceHelper.put(HOUR, hLast);
//            sharePreferenceHelper.put(MINUTE, mLast);
//            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//            if (alarmUri == null) {
//                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            }
//            Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
//            // ringtone.play();
//
//            //this will send a notification message
//            ComponentName comp = new ComponentName(context.getPackageName(),
//                    AlarmService.class.getSimpleName());
//            Intent intent1 = new Intent(context, AlarmClockActivity.class);
//
//            String strH = h + "";
//            String strM = m + "";
//            if (h < 10) {
//                strH = "0" + h;
//            }
//            if (m < 10) {
//                strM = "0" + m;
//            }
//            intent1.putExtra("TIME", strH + " : " + strM);
//
//            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            Intent intentService = new Intent(context, AlarmService.class);
//            intentService.putExtra("TEXT", strH + " : " + strM);
//            ItemAlarm itemAlarm = check(getDataFromDatabase(context), h + "", m + "");
//
//            if (itemAlarm != null) {
//                intent1.putExtra("id", itemAlarm.getId());
//                if(checkHasRepeat(itemAlarm)) {
//                    if(checkIsToday(itemAlarm,dayOfWeek) || checkDayCreateToDay(itemAlarm, dayOfMonth)){
//                        wakeDevice(context);
//                        context.startActivity(intent1);
//                        startWakefulService(context, intentService);
//                        setResultCode(Activity.RESULT_OK);
//                    }
//                }else {
//                    wakeDevice(context);
//                     DatabaseHelper databaseHelper = new DatabaseHelper(context);
//                    //databaseHelper.addOrUpdateAlarm(itemAlarm);
//                    //databaseHelper.deleteAlarmById(itemAlarm.getId());
//                    context.startActivity(intent1);
//                    startWakefulService(context, intentService);
//                    setResultCode(Activity.RESULT_OK);
//                }
//            }


        }
        if(BuildConfig.DEBUG) {
            Toast.makeText(context, h + " " + m + "", Toast.LENGTH_SHORT).show();
            Log.d(TAG, h + " " + m + "");
        }
    }

//    private boolean checkDayCreateToDay(ItemAlarm itemAlarm, int dayOfMonth) {
//        if(itemAlarm.getDayCreate() == dayOfMonth){
//            return true;
//        }
//        return false;
//    }
//
//    public ArrayList<ItemAlarm> getDataFromDatabase(Context context) {
//        ArrayList<ItemAlarm> itemAlarms = new ArrayList<>();
//        DatabaseHelper databaseHelper = new DatabaseHelper(context);
//        itemAlarms.addAll(databaseHelper.getAllAlarms());
//        return itemAlarms;
//    }
//
//
//
//    private ItemAlarm check(ArrayList<ItemAlarm> itemAlarms, String h, String m) {
//        for (ItemAlarm itemAlarm : itemAlarms) {
//            if (h.equals(itemAlarm.getHour()) && m.equals(itemAlarm.getMinute()) && itemAlarm.getStatus().equals("0")) {
//                return itemAlarm;
//            }
//        }
//        return null;
//    }
//
//    private boolean checkIsToday(ItemAlarm itemAlarm, int dayOfwweek) {
//        if (dayOfwweek == Calendar.MONDAY) {
//            if (itemAlarm.getRepeatMo() == 1) {
//                return true;
//            }
//        }
//        if (dayOfwweek == Calendar.TUESDAY) {
//            if (itemAlarm.getRepeatTu() == 1) {
//                return true;
//            }
//        }
//        if (dayOfwweek == Calendar.WEDNESDAY) {
//            if (itemAlarm.getRepeatWe() == 1) {
//                return true;
//            }
//        }
//        if (dayOfwweek == Calendar.THURSDAY) {
//            if (itemAlarm.getRepeatTh() == 1) {
//                return true;
//            }
//        }
//        if (dayOfwweek == Calendar.FRIDAY) {
//            if (itemAlarm.getRepeatFr() == 1) {
//                return true;
//            }
//        }
//        if (dayOfwweek == Calendar.SATURDAY) {
//            if (itemAlarm.getRepeatSa() == 1) {
//                return true;
//            }
//        }
//        if (dayOfwweek == Calendar.SUNDAY) {
//            if (itemAlarm.getRepeatSu() == 1) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean checkHasRepeat(ItemAlarm itemAlarm) {
//        if (itemAlarm.getRepeatMo() == 1) {
//            return true;
//        }
//        if (itemAlarm.getRepeatTu() == 1) {
//            return true;
//        }
//        if (itemAlarm.getRepeatWe() == 1) {
//            return true;
//        }
//        if (itemAlarm.getRepeatTh() == 1) {
//            return true;
//        }
//        if (itemAlarm.getRepeatFr() == 1) {
//            return true;
//        }
//        if (itemAlarm.getRepeatSa() == 1) {
//            return true;
//        }
//        if (itemAlarm.getRepeatSu() == 1) {
//            return true;
//        }
//        return false;
//    }
//    public void wakeDevice(Context context) {
//
//        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
//        keyguardLock.disableKeyguard();
//    }
}
