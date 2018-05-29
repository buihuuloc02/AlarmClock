package alarmclock.app.com.alarmclock;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by Administrator on 5/16/2018.
 */

public class AlarmClockApplication  extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
