package alarmclock.app.com.alarmclock.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.util.SharePreferenceHelper;

/**
 * Created by Administrator on 5/9/2018.
 */

public class BaseActivity extends AppCompatActivity {


    Vibrator vibrator;
    private AlertDialog dialog;

    public SharePreferenceHelper getSharePreferences() {
        return SharePreferenceHelper.getInstances(getApplicationContext());
    }

    public void showDialog(Context context, String msg, boolean cancelable, String buttonPositiveButton, String buttonNegativeButton, CallBackDismiss callBackDismiss) {
        dialog = new AlertDialog.Builder(context).create();
        dialog.setCancelable(cancelable);
        dialog.setMessage(msg);
        if (buttonPositiveButton != null && !TextUtils.isEmpty(buttonPositiveButton)) {
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, buttonPositiveButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    callBackDismiss.callBackDismiss();
                }
            });
        }
        if (buttonNegativeButton != null && !TextUtils.isEmpty(buttonNegativeButton)) {
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, buttonNegativeButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    public interface CallBackDismiss {
        void callBackDismiss();
    }

    public void initVibration() {
        long[] mVibratePattern = new long[]{0, 400, 400, 400};
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect effect = VibrationEffect.createWaveform(mVibratePattern, 0); // '0' to repeat
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(mVibratePattern, 0);/// '0' to repeat
        }
    }

    public void stopVibration() {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    public boolean isNetworkEnabled() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    public String getVersionName(){
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionNumber = pinfo.versionCode;
        String versionName = pinfo.versionName;
        return versionName;
    }

    public int getVersionCode(){
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pinfo.versionCode;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

    }
}
