package alarmclock.app.com.alarmclock.util;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import alarmclock.app.com.alarmclock.AlarmClockApplication;

/**
 * Created by Administrator on 6/1/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private final static String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        saveToken(refreshedToken);
    }
    public SharePreferenceHelper getSharePreferences() {
        return SharePreferenceHelper.getInstances(getApplicationContext());
    }

    private void saveToken(String refreshedToken) {
        SharePreferenceHelper  sharePreferenceHelper = getSharePreferences();
        sharePreferenceHelper.put(SharePreferenceHelper.Key.KEY_TOKEN,refreshedToken);
    }
}
