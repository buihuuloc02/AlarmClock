package alarmclock.app.com.alarmclock.activity;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.model.ItemAlarm;
import alarmclock.app.com.alarmclock.util.DatabaseHelper;
import alarmclock.app.com.alarmclock.util.SharePreferenceHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 5/9/2018.
 */

public class AlarmClockActivity extends BaseActivity implements SensorListener {

    private static final int SHAKE_THRESHOLD = 1000;
    @BindView(R.id.tvTime)
    TextView tvTime;

    @BindView(R.id.btnStop)
    Button btnStop;
    @BindView(R.id.tvNumberShake)
    TextView tvNumberShake;

    @BindView(R.id.imgClock1)
    View imgClock1;

    @BindView(R.id.imgClock2)
    View imgClock2;

    private int id;
    private DatabaseHelper dbHelper;
    private ItemAlarm itemAlarm;
    private int countShake = 0;
    private int mNumberShake = 1;
    private int mSpeekShake = SHAKE_THRESHOLD;


    private Handler handler = new Handler();

    private SensorManager sensorMgr;
    MediaPlayer mMediaPlayer;

    @OnClick({R.id.btnStop})
    public void OnButtonClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnStop:
                stopVibration();
                btnStop.setVisibility(View.GONE);
                mMediaPlayer.stop();
                imgClock1.setVisibility(View.VISIBLE);
                imgClock2.setVisibility(View.GONE);
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                skillApp();
                finish();
                // Ringtone ringtone = RingtoneManager.getRingtone(getActivity());
                //ringtone.stop();
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_alarm);

        ButterKnife.bind(this);
        createWakeLocks();

        String s = getIntent().getStringExtra("TIME");
        id = getIntent().getIntExtra("id", 0);
        dbHelper = new DatabaseHelper(this);
        itemAlarm = dbHelper.getAlarmById(id);
        tvTime.setText(s);


        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fullWakeLock.isHeld()) {
            fullWakeLock.release();
        }
        if (partialWakeLock.isHeld()) {
            partialWakeLock.release();
        }
        mNumberShake = getSharePreferences().getInt(SharePreferenceHelper.Key.NUMBERSHAKE, 0) + 1;
        mSpeekShake = getSharePreferences().getInt(SharePreferenceHelper.Key.SPEEKSHAKE, 0) + 1;

        String str = String.format(getResources().getString(R.string.text_confirm_number_shake), mNumberShake + "");
        tvNumberShake.setText(str);

        mSpeekShake = 1000 + (mSpeekShake * 100);
        sensorMgr.registerListener(this,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);

        handler.postDelayed(runnable, 60000);
        vibaration();
        playSound(this, getAlarmUri());
    }

    private void playSound(Context context, Uri alert) {
        try {
            stopSound();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(this, alert);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            finish();
            stopVibration();
            stopSound();
            skillApp();
        }
    };

    private void stopSound() {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(this, alarmUri);
        ringtone.stop();

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer = null;
        }
    }

    private Uri getAlarmUri() {
        if (itemAlarm != null) {
            return itemAlarm.getUriCustom();
        }
        return null;
    }

    long lastUpdate;
    float x, y, z, last_x, last_y, last_z;

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                x = values[SensorManager.DATA_X];
                y = values[SensorManager.DATA_Y];
                z = values[SensorManager.DATA_Z];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > mSpeekShake) {

                    if (countShake < mNumberShake) {
                        countShake++;
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String str = String.format(getResources().getString(R.string.text_confirm_number_shake), (mNumberShake - countShake)+"");

                        tvNumberShake.setText(str);
                    }
                    else{
                        stopVibration();
                        btnStop.setVisibility(View.GONE);
                        mMediaPlayer.stop();
                        imgClock1.setVisibility(View.VISIBLE);
                        imgClock2.setVisibility(View.GONE);
                        skillApp();
//                    Intent intent = new Intent(this, MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
                        finish();
                    }
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(int i, int i1) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopVibration();
        stopSound();
        partialWakeLock.acquire();
    }

    private void skillApp() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        super.onDestroy();
    }

    PowerManager.WakeLock fullWakeLock;
    PowerManager.WakeLock partialWakeLock;

    // Called from onCreate
    protected void createWakeLocks() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        fullWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "Loneworker - FULL WAKE LOCK");
        partialWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wakwclock");
        wakeDevice();
    }

    // Called whenever we need to wake up the device
    public void wakeDevice() {
        fullWakeLock.acquire();

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();
    }
}
