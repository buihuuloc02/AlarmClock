package alarmclock.app.com.alarmclock.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.settings.WeatherPreferenceActivity;
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

    public String getVersionName() {
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

    public int getVersionCode() {
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pinfo.versionCode;
    }
    public static boolean canSendSMS(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }
    public void sendSms(String phoneNumber, String message) {
        if(canSendSMS(this)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        }
    }

    public void askForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                + ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Contacts access needed");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setMessage("Llease confirm Contacts access");//TODO put real question
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(
                                new String[]
                                        {Manifest.permission.READ_CONTACTS}
                                , 1);
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS},
                        1);
            }
        } else {
            //Call whatever you want
            //myMethod();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //askForPermission();
//        WeatherPreferenceActivity weatherPreferenceActivity = new WeatherPreferenceActivity();
//        weatherPreferenceActivity.onCreate(savedInstanceState);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

    }
}
