package alarmclock.app.com.alarmclock.activity;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import alarmclock.app.com.alarmclock.BuildConfig;
import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.model.ItemAlarm;
import alarmclock.app.com.alarmclock.model.UserSetting;
import alarmclock.app.com.alarmclock.util.DatabaseHelper;
import alarmclock.app.com.alarmclock.util.SharePreferenceHelper;

import static alarmclock.app.com.alarmclock.activity.MainActivity.EXTRA_NOTIFICATION;
import static alarmclock.app.com.alarmclock.util.Constant.HOUR;
import static alarmclock.app.com.alarmclock.util.Constant.MINUTE;
import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

/**
 * Created by Administrator on 5/10/2018.
 */

public class AlarmService extends JobIntentService {

    public final static String EXTRA_TIME = "EXTRA_TIME";
    public final static int SHOWED = 1;
    public final static int DO_NOT_SHOW = 0;
    public final static int SHOW_ITEM_FIRST = -1;
    private final static String TAG = AlarmService.class.getSimpleName();
    private NotificationManager alarmNotificationManager;
    private int hLast;
    private int mLast;
    private String timeReceive = "";
    private boolean isSended = false;
    private Context mContext;
//
//    public AlarmService() {
//        super(TAG);
//    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (intent.hasExtra(EXTRA_TIME)) {
            timeReceive = intent.getStringExtra(EXTRA_TIME);
        }
        mContext = getApplicationContext();
        doCheckAlarm();
    }

//    @Override
//    public void onHandleIntent(Intent intent) {
//        if(intent.hasExtra(EXTRA_TIME)) {
//            timeReceive = intent.getStringExtra(EXTRA_TIME);
//        }
//        doCheckAlarm();
//    }

    private void doCheckAlarm() {
        Calendar calendar = Calendar.getInstance();
        SharePreferenceHelper sharePreferenceHelper = SharePreferenceHelper.getInstances(this);
        hLast = sharePreferenceHelper.getInt(HOUR, 0);
        mLast = sharePreferenceHelper.getInt(MINUTE, 0);
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        if (h != hLast || m != mLast) {
            hLast = h;
            mLast = m;
            isSended = false;
            sharePreferenceHelper.put(HOUR, hLast);
            sharePreferenceHelper.put(MINUTE, mLast);
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Ringtone ringtone = RingtoneManager.getRingtone(this, alarmUri);
            // ringtone.play();

            //this will send a notification message
            ComponentName comp = new ComponentName(this.getPackageName(),
                    AlarmService.class.getSimpleName());
            Intent intent1 = new Intent(this, AlarmClockActivity.class);

            String strH = h + "";
            String strM = m + "";
            if (h < 10) {
                strH = "0" + h;
            }
            if (m < 10) {
                strM = "0" + m;
            }
            intent1.putExtra("TIME", strH + " : " + strM);

            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent intentService = new Intent(this, AlarmService.class);
            intentService.putExtra("TEXT", strH + " : " + strM);
            ItemAlarm itemAlarm = check(getDataFromDatabase(this), h + "", m + "");

            if (itemAlarm != null) {
                intent1.putExtra("id", itemAlarm.getId());
                if (checkHasRepeat(itemAlarm)) {
                    if (checkIsToday(itemAlarm, dayOfWeek) || checkDayCreateToDay(itemAlarm, dayOfMonth)) {
                        wakeDevice(this);
                        this.startActivity(intent1);
                        startWakefulService(this, intentService);
                        //this.setResultCode(Activity.RESULT_OK);
                    }
                } else {
                    wakeDevice(this);
                    DatabaseHelper databaseHelper = new DatabaseHelper(this);
                    //databaseHelper.addOrUpdateAlarm(itemAlarm);
                    //databaseHelper.deleteAlarmById(itemAlarm.getId());
                    this.startActivity(intent1);
                    startWakefulService(this, intentService);
                    // setResultCode(Activity.RESULT_OK);
                }
            }
        }
        if (BuildConfig.DEBUG) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(mContext, h + " " + m + "", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, h + " " + m + "");
                }
            });
        }

        ItemAlarm itemAlarm = findNextAlarmClock(getDataFromDatabase(this), h, m);
        if (itemAlarm != null) {
            setAlarm(itemAlarm);
            String strTime = getStringTimeMinute(itemAlarm);
            int showNotification = sharePreferenceHelper.getInt(SharePreferenceHelper.Key.KEY_SHOW_NOTIFICATION, DO_NOT_SHOW);
            if (showNotification == DO_NOT_SHOW) {
                sendNotification(this.getResources().getString(R.string.text_next), strTime);
                sharePreferenceHelper.put(SharePreferenceHelper.Key.KEY_SHOW_NOTIFICATION, SHOWED);
            }
            Log.d(TAG, itemAlarm.getHour() + " : " + itemAlarm.getMinute());
        } else {
            ArrayList<ItemAlarm> itemAlarms = getDataFromDatabase(this);

            if (itemAlarms != null && itemAlarms.size() > 0) {
                ItemAlarm itemNext = getFirstAlarmClock(itemAlarms);
                if (itemNext != null) {
                    setAlarm(itemNext);
                    int showNotification = sharePreferenceHelper.getInt(SharePreferenceHelper.Key.KEY_SHOW_NOTIFICATION, DO_NOT_SHOW);
                    if (showNotification == DO_NOT_SHOW) {
                        String strTime = getStringTimeMinute(itemNext);
                        sendNotification("",strTime);
                        sharePreferenceHelper.put(SharePreferenceHelper.Key.KEY_SHOW_NOTIFICATION, SHOWED);
                    }
                }
            }

        }
    }

    private void setAlarm(ItemAlarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarm.getHour()));
        calendar.set(Calendar.MINUTE, Integer.parseInt(alarm.getMinute()));
        calendar.set(Calendar.SECOND, 0);

        Intent myIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
        //      24*60*60*1000 , pendingIntent);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void sendNotification(String title, String msg) {
        SharePreferenceHelper sharePreferenceHelper = SharePreferenceHelper.getInstances(this);
        UserSetting mUserSetting = (UserSetting) sharePreferenceHelper.getObject(SharePreferenceHelper.Key.KEY_USER_SETTING, UserSetting.class);
        int selected = mUserSetting != null ? mUserSetting.getShowNotification() : 1;
        if(selected == 0){
            return;
        }
        Log.d(TAG, "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_NOTIFICATION, "EXTRA_NOTIFICATION");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String strTitle = title + " " + getResources().getString(R.string.text_alarm_clock);
        Notification n = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_noti)
                .setContentIntent(contentIntent)
                .setContentTitle(strTitle.trim())
                .setContentText(msg)
                //.setAutoCancel(true)
                .setSound(alarmSound)
//                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0))
                .build();


        alarmNotificationManager.notify(1, n);

        Log.d(TAG, "Notification sent.");
    }

    private boolean checkDayCreateToDay(ItemAlarm itemAlarm, int dayOfMonth) {
        if (itemAlarm.getDayCreate() == dayOfMonth) {
            return true;
        }
        return false;
    }

    public ArrayList<ItemAlarm> getDataFromDatabase(Context context) {
        ArrayList<ItemAlarm> itemAlarms = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        itemAlarms.addAll(databaseHelper.getAllAlarms());
        return itemAlarms;
    }


    private ItemAlarm check(ArrayList<ItemAlarm> itemAlarms, String h, String m) {
        for (ItemAlarm itemAlarm : itemAlarms) {
            if (h.equals(itemAlarm.getHour()) && m.equals(itemAlarm.getMinute()) && itemAlarm.getStatus().equals("0")) {
                return itemAlarm;
            }
        }
        return null;
    }

    private boolean checkIsToday(ItemAlarm itemAlarm, int dayOfwweek) {
        if (dayOfwweek == Calendar.MONDAY) {
            if (itemAlarm.getRepeatMo() == 1) {
                return true;
            }
        }
        if (dayOfwweek == Calendar.TUESDAY) {
            if (itemAlarm.getRepeatTu() == 1) {
                return true;
            }
        }
        if (dayOfwweek == Calendar.WEDNESDAY) {
            if (itemAlarm.getRepeatWe() == 1) {
                return true;
            }
        }
        if (dayOfwweek == Calendar.THURSDAY) {
            if (itemAlarm.getRepeatTh() == 1) {
                return true;
            }
        }
        if (dayOfwweek == Calendar.FRIDAY) {
            if (itemAlarm.getRepeatFr() == 1) {
                return true;
            }
        }
        if (dayOfwweek == Calendar.SATURDAY) {
            if (itemAlarm.getRepeatSa() == 1) {
                return true;
            }
        }
        if (dayOfwweek == Calendar.SUNDAY) {
            if (itemAlarm.getRepeatSu() == 1) {
                return true;
            }
        }
        return false;
    }

    private boolean checkHasRepeat(ItemAlarm itemAlarm) {
        if (itemAlarm.getRepeatMo() == 1) {
            return true;
        }
        if (itemAlarm.getRepeatTu() == 1) {
            return true;
        }
        if (itemAlarm.getRepeatWe() == 1) {
            return true;
        }
        if (itemAlarm.getRepeatTh() == 1) {
            return true;
        }
        if (itemAlarm.getRepeatFr() == 1) {
            return true;
        }
        if (itemAlarm.getRepeatSa() == 1) {
            return true;
        }
        if (itemAlarm.getRepeatSu() == 1) {
            return true;
        }
        return false;
    }

    public void wakeDevice(Context context) {

        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();
    }

    private ItemAlarm findNextAlarmClock(ArrayList<ItemAlarm> itemAlarms, int hour, int minute) {
        Collections.sort(itemAlarms, new TimeAlarmComparator());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        long timcurrent = hour * 60 + minute;
        for (ItemAlarm itemAlarm : itemAlarms) {
            if (itemAlarm.getMilisecod() >= timcurrent && itemAlarm.getStatus().endsWith("0")) {
                return itemAlarm;
            }
        }
        return null;
    }

    private ItemAlarm getFirstAlarmClock(ArrayList<ItemAlarm> itemAlarms) {
        Collections.sort(itemAlarms, new TimeAlarmComparator());
        for (ItemAlarm itemAlarm : itemAlarms) {
            if (itemAlarm.getStatus().endsWith("0")) {
                return itemAlarm;
            }
        }
        return null;
    }

    public class TimeAlarmComparator implements Comparator<ItemAlarm> {
        public int compare(ItemAlarm left, ItemAlarm right) {
            Long t1 = left.getMilisecod();
            Long t2 = right.getMilisecod();
            return t1.compareTo(t2);
        }
    }

    private String getStringTimeMinute(ItemAlarm itemAlarm) {
        int h = Integer.parseInt(itemAlarm.getHour());
        int m = Integer.parseInt(itemAlarm.getMinute());
        String strH = h + "";
        String strM = m + "";
        if (h < 10) {
            strH = "0" + h;
        }
        if (m < 10) {
            strM = "0" + m;
        }
        return strH + " : " + strM;
    }

}