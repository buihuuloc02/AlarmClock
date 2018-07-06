package alarmclock.app.com.alarmclock.activity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.Random;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.model.ItemAlarm;
import alarmclock.app.com.alarmclock.model.UserSetting;
import alarmclock.app.com.alarmclock.util.DatabaseHelper;
import alarmclock.app.com.alarmclock.util.SharePreferenceHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static alarmclock.app.com.alarmclock.util.Constant.HOUR;
import static alarmclock.app.com.alarmclock.util.Constant.MINUTE;

/**
 * Created by Administrator on 5/9/2018.
 */

public class AlarmClockActivity extends BaseActivity implements SensorListener {

    private final static int TIME_VIBRATION = 1000 * 60;
    private final static int SET_TIME_VIBRATION_MINUTE = 3;// 3 minute
    public final static long TIME_VIBRATION_IN_MINUTE = TIME_VIBRATION * SET_TIME_VIBRATION_MINUTE;

    private static final int SHAKE_THRESHOLD = 1000;
    @BindView(R.id.tvTime)
    TextView tvTime;

    @BindView(R.id.btnStop)
    TextView btnStop;

    @BindView(R.id.tvNumberShake)
    TextView tvNumberShake;

    @BindView(R.id.tvNameAlarm)
    TextView tvNameAlarm;

    @BindView(R.id.tvHelp)
    TextView tvHelp;

    @BindView(R.id.imgClock1)
    View imgClock1;

    @BindView(R.id.imgClock2)
    View imgClock2;

    @BindView(R.id.layoutTextViewHelp)
    View layoutTextViewHelp;

    @BindView(R.id.layoutAlarmRetype)
    View layoutAlarmRetype;

    @BindView(R.id.layoutMain)
    View layoutMain;

    @BindView(R.id.imageWallPaper)
    ImageView imageWallPaper;
    @BindView(R.id.tvRandomText)
    TextView tvRandomText;

    @BindView(R.id.editResultRetype)
    EditText editResultRetype;

    @BindView(R.id.btnCheckResult)
    Button btnChecResult;

    @BindView(R.id.imgRefreshTextRandom)
    ImageView imgRefreshTextRandom;

    private int id;
    private DatabaseHelper dbHelper;
    private ItemAlarm itemAlarm;
    private int countShake = 0;
    private int mNumberShake = 1;
    private int mSpeekShake = SHAKE_THRESHOLD;
    private String strResultRandom = "";
    private int numberRandom = 6;

    private Handler handler = new Handler(Looper.getMainLooper());

    private SensorManager sensorMgr;
    private MediaPlayer mMediaPlayer;

    private PowerManager.WakeLock fullWakeLock;
    private PowerManager.WakeLock partialWakeLock;

    long lastUpdate;
    float x, y, z, last_x, last_y, last_z;

    private Bitmap theBitmap = null;
    private UserSetting mUserSetting;

    @OnClick({R.id.btnStop, R.id.imgRefreshTextRandom, R.id.btnCheckResult})
    public void OnButtonClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnStop:
                finish();
                stopVibration();
                stopSound();
                skillApp();
                break;
            case R.id.imgRefreshTextRandom:
                tvRandomText.setText("");
                strResultRandom = randomText(numberRandom);
                tvRandomText.setText(strResultRandom);
                break;
            case R.id.btnCheckResult:
                String s = editResultRetype.getText().toString();
                boolean result = compareTextRandom(s);
                if (!result) {
                    Toast.makeText(this, getResources().getString(R.string.text_input_incorreect), Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                    stopVibration();
                    stopSound();
                    skillApp();
                }
                break;
        }
    }

    @SuppressLint("ResourceType")
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
        id = getIntent().getIntExtra("id", 0);
        dbHelper = new DatabaseHelper(this);
        itemAlarm = dbHelper.getAlarmById(id);

//        if(itemAlarm != null){
//            if(itemAlarm.getMethodStop() == 2){
//                setTheme(R.style.Theme_UserDialog);
//            }
//        }
        setContentView(R.layout.activity_alarm);

        ButterKnife.bind(this);
        createWakeLocks();

        String s = getIntent().getStringExtra("TIME");
        tvTime.setText(s);

        mUserSetting = (UserSetting) getSharePreferences().getObject(SharePreferenceHelper.Key.KEY_USER_SETTING, UserSetting.class);

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        btnStop.setTextColor(getResources().getColorStateList(R.drawable.selector_button_text_black));
        setDisplayTextView();
        setDisplayLayoutAlarm();
        strResultRandom = randomText(numberRandom);
        tvRandomText.setText(strResultRandom);
    }


    class AsyncTaskLoadImage extends AsyncTask<String, Void, Bitmap> {

        String pathImage = "";

        @Override
        protected Bitmap doInBackground(String... strings) {

            String path = strings[0];
            pathImage = path;
            if (!TextUtils.isEmpty(path)) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {

                        Glide.
                                with(getApplicationContext()).
                                load(path).
                                into(imageWallPaper);

                    }
                };
                mainHandler.post(myRunnable);
            }
            return theBitmap;
        }

        @SuppressLint("ResourceType")
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inPreferQualityOverSpeed = true;
            if (!TextUtils.isEmpty(pathImage)) {
                bitmap = BitmapFactory.decodeFile(pathImage, options);
            }
            if (bitmap != null) {
                layoutMain.setBackground(null);
                layoutMain.setBackgroundColor(Color.TRANSPARENT);
                BitmapDrawable bd = new BitmapDrawable(layoutMain.getContext().getResources(), bitmap);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    layoutMain.setBackground(bd);
                } else {
                    layoutMain.setBackgroundDrawable(bd);
                }
                // set text color tvNameAlarm
                int w = tvNameAlarm.getWidth();
                int h = tvNameAlarm.getHeight();
                int x = (int) tvNameAlarm.getX() + w / 2;
                int y = (int) tvNameAlarm.getY() + h / 2;
                int pixel = bitmap.getPixel(x, y);
                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);
                int RGB = android.graphics.Color.rgb(255 - redValue, 255 - greenValue, 255 - blueValue);
                tvNameAlarm.setTextColor(RGB);
                // set text color tvTime
                w = tvTime.getWidth();
                h = tvTime.getHeight();
                x = (int) tvTime.getX() + w / 2;
                y = (int) tvTime.getY() + h / 2;
                pixel = bitmap.getPixel(x, y);
                redValue = Color.red(pixel);
                blueValue = Color.blue(pixel);
                greenValue = Color.green(pixel);
                RGB = android.graphics.Color.rgb(255 - redValue, 255 - greenValue, 255 - blueValue);
                tvTime.setTextColor(RGB);

                // set text color tvHelp
                w = tvHelp.getWidth();
                h = tvHelp.getHeight();
                x = (int) tvHelp.getX() + w / 2;
                y = (int) tvHelp.getY() + h / 2;
                pixel = bitmap.getPixel(x, y);
                redValue = Color.red(pixel);
                blueValue = Color.blue(pixel);
                greenValue = Color.green(pixel);
                RGB = android.graphics.Color.rgb(255 - redValue, 255 - greenValue, 255 - blueValue);
                tvHelp.setTextColor(RGB);

                // set text color tvNumberShake
                w = tvNumberShake.getWidth();
                h = tvNumberShake.getHeight();
                x = (int) tvNumberShake.getX() + w / 2;
                y = (int) tvNumberShake.getY() + h / 2;
                pixel = bitmap.getPixel(x, y);
                redValue = Color.red(pixel);
                blueValue = Color.blue(pixel);
                greenValue = Color.green(pixel);
                RGB = android.graphics.Color.rgb(255 - redValue, 255 - greenValue, 255 - blueValue);
                tvNumberShake.setTextColor(RGB);

                // set text color btnStop
                w = btnStop.getWidth();
                h = btnStop.getHeight();
                x = (int) btnStop.getX() + w / 2;
                y = (int) btnStop.getY() + h / 2;
                pixel = bitmap.getPixel(x, y);
                redValue = Color.red(pixel);
                blueValue = Color.blue(pixel);
                greenValue = Color.green(pixel);
                RGB = android.graphics.Color.rgb(255 - redValue, 255 - greenValue, 255 - blueValue);
                if (redValue >= 127 && blueValue >= 127 && greenValue >= 127) {
                    btnStop.setBackgroundResource(R.drawable.shape_background_border);
                    btnStop.setTextColor(getResources().getColorStateList(R.drawable.selector_button_text_black));

                } else {
                    btnStop.setBackgroundResource(R.drawable.shape_background_border_white);
                    btnStop.setTextColor(getResources().getColorStateList(R.drawable.selector_button_text_white));
                }
                //
            } else {
                tvNameAlarm.setTextColor(Color.BLACK);
                tvTime.setTextColor(Color.BLACK);
                tvHelp.setTextColor(Color.BLACK);
                tvNumberShake.setTextColor(Color.BLACK);
                btnStop.setBackgroundResource(R.drawable.shape_background_border);
                btnStop.setTextColor(getResources().getColorStateList(R.drawable.selector_button_text_black));
            }
        }
    }

    @SuppressLint("ResourceType")
    private void setTextColorForTextView() {
        // tvNameAlarm.setTextColor(Color.BLACK);
        tvTime.setTextColor(Color.BLACK);
        tvHelp.setTextColor(Color.BLACK);
        tvNumberShake.setTextColor(Color.BLACK);
        btnStop.setBackgroundResource(R.drawable.shape_background_border);
        btnStop.setTextColor(getResources().getColorStateList(R.drawable.selector_button_text_black));
        if (itemAlarm != null) {
            if (!TextUtils.isEmpty(itemAlarm.getPathImageWallPaper())) {
                // tvNameAlarm.setTextColor(Color.WHITE);
                tvTime.setTextColor(Color.WHITE);
                tvHelp.setTextColor(Color.WHITE);
                tvNumberShake.setTextColor(Color.WHITE);
                btnStop.setBackgroundResource(R.drawable.shape_background_border_white);
                btnStop.setTextColor(getResources().getColorStateList(R.drawable.selector_button_text_white));
            }
        }
    }

    private void setDisplayLayoutAlarm() {
        if (itemAlarm != null) {
            layoutTextViewHelp.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.GONE);
            layoutAlarmRetype.setVisibility(View.GONE);
            if (itemAlarm.getMethodStop() == 0) {
                btnStop.setVisibility(View.VISIBLE);
            } else if (itemAlarm.getMethodStop() == 1) {
                btnStop.setVisibility(View.GONE);
            } else if (itemAlarm.getMethodStop() == 2) {
                layoutTextViewHelp.setVisibility(View.GONE);
                layoutAlarmRetype.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setDisplayTextView() {
        if (itemAlarm != null) {
            String str = getString(R.string.text_click_stop);
            if (itemAlarm.getMethodStop() == 1) {
                str = getString(R.string.text_shake);
                tvNumberShake.setVisibility(View.VISIBLE);
            } else {
                tvNumberShake.setVisibility(View.GONE);
                str = getString(R.string.text_click_stop);
            }
            tvHelp.setText(str);
        }
    }

    private void setTextTitleAlarm() {
        if (itemAlarm != null) {
            String str = itemAlarm.getTitle();
            tvNameAlarm.setText(str);
        }
    }

    private void playSound(Context context, Uri alert) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = 100;
        int currVolume = itemAlarm.getVolume();
        float log1 = (float) (Math.log(maxVolume - currVolume) / Math.log(maxVolume));
        try {
            stopSound();
            if (alert != null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(this, alert);
                float volume = 1 - log1;
                Log.d("volume", volume + "");
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        currVolume, 0);
                // mMediaPlayer.setVolume(volume, volume);
                mMediaPlayer.prepare();
                mMediaPlayer.setLooping(true);
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sendSmsToContact(itemAlarm);
            finish();
            stopVibration();
            stopSound();
            skillApp();

        }
    };

    private void sendSmsToContact(ItemAlarm itemAlarm) {
        if (itemAlarm != null) {
            if (!TextUtils.isEmpty(itemAlarm.getNumberContact())) {
                String number = itemAlarm.getNumberContact();
                String message = getString(R.string.text_message_send_contact);
                String h = itemAlarm.getHour().length() == 1 ? "0" + itemAlarm.getHour() : itemAlarm.getHour();
                String m = itemAlarm.getMinute().length() == 1 ? "0" + itemAlarm.getMinute() : itemAlarm.getMinute();
                String time = h + ":" + m;
                message = String.format(message, time);
                sendSms(number, message);
            }
        }
    }

    /**
     * compare text random
     *
     * @param s: String
     * @return: true/false
     */
    private boolean compareTextRandom(String s) {

        String strRandom = tvRandomText.getText().toString();
        if (s.equals(strRandom)) {
            return true;
        }
        return false;
    }

    /**
     * Random text with lenght
     *
     * @param numberRandom: int
     * @return: String
     */
    private String randomText(int numberRandom) {
        String result = "";
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(numberRandom);
        char tempChar;
        for (int i = 0; i < numberRandom; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        result = randomStringBuilder.toString();
        return result;
    }

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
                        String str = String.format(getResources().getString(R.string.text_confirm_number_shake), (mNumberShake - countShake) + "");

                        tvNumberShake.setText(str);
                    } else {
                        finish();
                        stopVibration();
                        stopSound();
                        skillApp();
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

    /**
     * Method: skill app
     */
    private void skillApp() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        super.onDestroy();
    }

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

    @Override
    protected void onUserLeaveHint() {

    }

    @Override
    protected void onPause() {
        getSharePreferences().put(HOUR, 0);
        getSharePreferences().put(MINUTE, 0);
        super.onPause();
        stopVibration();
        stopSound();

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
        if (mUserSetting != null) {
            mNumberShake = mUserSetting.getNumberShake() + 1;
            mSpeekShake = mUserSetting.getSpeedShake() + 1;
        } else {
            mNumberShake = 1;
            mSpeekShake = 1;
        }

        @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String str = String.format(getResources().getString(R.string.text_confirm_number_shake), String.valueOf(mNumberShake));
        tvNumberShake.setText(str);
        setTextTitleAlarm();

        mSpeekShake = 1000 + (mSpeekShake * 100);

        if (itemAlarm != null && itemAlarm.getMethodStop() == 1) {
            sensorMgr.registerListener(this,
                    SensorManager.SENSOR_ACCELEROMETER,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        handler.postDelayed(runnable, TIME_VIBRATION_IN_MINUTE);
        initVibration();
        playSound(this, getAlarmUri());
        if (itemAlarm != null && itemAlarm.getPathImageWallPaper() != null) {
            {
                //setTextColorForTextView();
                new AsyncTaskLoadImage().execute(itemAlarm.getPathImageWallPaper());
            }
        } else {
            imageWallPaper.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
