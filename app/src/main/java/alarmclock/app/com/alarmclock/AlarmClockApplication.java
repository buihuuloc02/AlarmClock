package alarmclock.app.com.alarmclock;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import alarmclock.app.com.alarmclock.util.MyFirebaseInstanceIDService;

import static alarmclock.app.com.alarmclock.util.MyFirebaseInstanceIDService.EXTRA_TOKEN;

/**
 * Created by Administrator on 5/16/2018.
 */

public class AlarmClockApplication extends Application {

    Context context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

        context = base;
        String android_id = getDeviceID();
        Log.d("android_id", android_id + "");
        Intent intent = new Intent(base, MyFirebaseInstanceIDService.class);
        intent.putExtra(EXTRA_TOKEN, android_id + "");
        startService(intent);

    }

    public String getDeviceID() {
        String deviceId = "";
        TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return deviceId;
        }
        if (mTelephony.getDeviceId() != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return deviceId;
            }
            deviceId = mTelephony.getDeviceId();
        } else {
            deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }
}
