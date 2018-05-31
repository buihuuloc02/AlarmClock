package alarmclock.app.com.alarmclock.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.util.SharePreferenceHelper;

import static alarmclock.app.com.alarmclock.activity.AlarmClockActivity.TIME_VIBRATION_IN_MINUTE;

/**
 * Created by Administrator on 5/9/2018.
 */

public class BaseActivity extends AppCompatActivity {

    private AlertDialog dialog;
    Vibrator vibrator;
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

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(TIME_VIBRATION_IN_MINUTE, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(TIME_VIBRATION_IN_MINUTE);
        }
    }

    public void stopVibration(){
        if(vibrator != null){
            vibrator.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
