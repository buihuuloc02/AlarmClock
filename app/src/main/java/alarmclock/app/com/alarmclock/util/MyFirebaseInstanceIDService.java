package alarmclock.app.com.alarmclock.util;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 6/1/2018.
 */

public class MyFirebaseInstanceIDService extends IntentService {
    private final static String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    public final static String EXTRA_TOKEN = "EXTRA_TOKEN";

    public MyFirebaseInstanceIDService(String name) {
        super(name);
    }

    public MyFirebaseInstanceIDService() {
        super("");
    }

    public SharePreferenceHelper getSharePreferences() {
        return SharePreferenceHelper.getInstances(getApplicationContext());
    }

    private void saveToken(String refreshedToken) {
        SharePreferenceHelper sharePreferenceHelper = getSharePreferences();
        sharePreferenceHelper.put(SharePreferenceHelper.Key.KEY_TOKEN, refreshedToken);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent.hasExtra(EXTRA_TOKEN)) {
            String token = intent.getStringExtra(EXTRA_TOKEN);
            saveToken(token);
            Log.d("android_id", token);
        }
    }
}
