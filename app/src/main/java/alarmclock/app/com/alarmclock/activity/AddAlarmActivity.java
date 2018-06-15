package alarmclock.app.com.alarmclock.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.model.ItemAlarm;
import alarmclock.app.com.alarmclock.model.MethodStop;
import alarmclock.app.com.alarmclock.model.PhoneCustom;
import alarmclock.app.com.alarmclock.model.UriCustom;
import alarmclock.app.com.alarmclock.receiver.AlarmReceiver;
import alarmclock.app.com.alarmclock.util.DatabaseHelper;
import alarmclock.app.com.alarmclock.util.SharePreferenceHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static alarmclock.app.com.alarmclock.activity.GetListImageActivity.EXTRA_NAME_IMAGE;
import static alarmclock.app.com.alarmclock.activity.GetListImageActivity.EXTRA_PATH_IMAGE;
import static alarmclock.app.com.alarmclock.util.Constant.ACTION_ALARM_CLOCK;
import static alarmclock.app.com.alarmclock.util.Constant.REQUEST_CODE_ADD_IMAGE_PAPER;
import static alarmclock.app.com.alarmclock.util.Constant.REQUEST_CODE_SELECT_CONTACT;
import static alarmclock.app.com.alarmclock.util.Constant.REQUEST_CODE_SELECT_SOUND;

/**
 * Created by Administrator on 5/9/2018.
 */

public class AddAlarmActivity extends BaseActivity {

    public static final String EXTRA_NAME_SOUND_SELECT = "EXTRA_NAME_SOUND_SELECT";
    public static final String EXTRA_PATH_SOUND_SELECT = "EXTRA_PATH_SOUND_SELECT";
    public static final String EXTRA_ITEM_ALARM = "EXTRA_ITEM_ALARM";
    public static final String TAG = AddAlarmActivity.class.getSimpleName();
    public static final int PERMISSION_REQUEST_CONTACT = 11231;
    @BindView(R.id.tvTime)
    TextView tvTime;

    @BindView(R.id.etTitle)
    EditText etTitle;


    @BindView(R.id.timePicker)
    TimePicker timePicker;

    @BindView(R.id.layoutWallPaper)
    View layoutWallPaper;

    @BindView(R.id.layoutContact)
    View layoutContact;

    @BindView(R.id.layoutImageWallPaper)
    View layoutImageWallPaper;

    @BindView(R.id.layoutAlarmTone)
    View layoutAlarmTone;

    @BindView(R.id.cbMo)
    CheckBox cbMo;

    @BindView(R.id.cbTu)
    CheckBox cbTu;

    @BindView(R.id.cbWe)
    CheckBox cbWe;

    @BindView(R.id.cbTh)
    CheckBox cbTh;

    @BindView(R.id.cbFr)
    CheckBox cbFr;

    @BindView(R.id.cbSa)
    CheckBox cbSa;

    @BindView(R.id.cbSu)
    CheckBox cbSu;

    @BindView(R.id.layoutMain)
    View layoutMain;

    @BindView(R.id.layoutVolume)
    View layoutVolume;

    @BindView(R.id.etAlarmTone)
    TextView etAlarmTone;

    @BindView(R.id.etAlarmWallPaper)
    TextView etAlarmWallPaper;

    @BindView(R.id.etSendSMS)
    TextView etSendSMS;


    @BindView(R.id.imageWallPaper)
    ImageView imageWallPaper;


    @BindView(R.id.imgDeleteWallPaper)
    ImageView imgDeleteWallPaper;


    @BindView(R.id.imgDeleteSMS)
    ImageView imgDeleteSMS;

    @BindView(R.id.imgDeleteTone)
    ImageView imgDeleteTone;


    @BindView(R.id.imgPlayStopSound)
    ImageView imgPlayStopSound;


    @BindView(R.id.seekbarVolume)
    SeekBar seekbarVolume;

    @BindView(R.id.spinnerMethod)
    Spinner spinnerMethod;


    private String hour;
    private String minute;
    private String format;
    private SharePreferenceHelper sharedPreferences;
    private ItemAlarm itemAlarm;
    private DatabaseHelper dbHelper;
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private ItemAlarm mItemAlarm;
    private ArrayList<UriCustom> mUriCustoms;
    private ArrayList<String> mNameUris;
    private Dialog dialogListSound;
    private MediaPlayer mediaPlayer;
    private int indexSoundSelected = 0;
    private UriCustom uriCustomSelected;
    private UriCustom uriCustomSelected_temp;
    private String pathImageSelected;
    private String nameImageSelected;
    private PhoneCustom mPhoneCustom;
    private Bitmap theBitmap = null;
    private int volumeSeekbar = 50;
    private boolean isPlayingSound = false;
    AudioManager audioManager;
    private int mIndexMethodStopSelected = 0;
    private List<MethodStop> listMethods;
    private ArrayList<String> nameMethods;

    @OnClick({R.id.imgPlayStopSound, R.id.imgDeleteWallPaper, R.id.imgDeleteSMS, R.id.imgDeleteTone})
    public void onClick(View v) {
        int id = v.getId();
        Snackbar snackbar = null;
        switch (id) {
            case R.id.imgPlayStopSound:
                setPlayOrStopSound();
                break;
            case R.id.imgDeleteWallPaper:
                snackbar = Snackbar
                        .make(layoutMain, getString(R.string.text_confirm_delete_image), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.text_button_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pathImageSelected = "";
                                nameImageSelected = getString(R.string.text_none);
                                updateNameImage(nameImageSelected);
                            }
                        });
                // Changing message text color
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();
                break;
            case R.id.imgDeleteTone:
                snackbar = Snackbar
                        .make(layoutMain, getString(R.string.text_confirm_delete_tone), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.text_button_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                uriCustomSelected = new UriCustom();
                                uriCustomSelected.setUri(null);
                                uriCustomSelected.setName(getResources().getString(R.string.text_none));
                                updateNameSound(uriCustomSelected);
                                setDisplayLayoutVolume(uriCustomSelected);
                                if (isPlayingSound) {
                                    setPlayOrStopSound();
                                }
                                stopSound();
                            }
                        });
                // Changing message text color
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();
                break;
            case R.id.imgDeleteSMS:
                snackbar = Snackbar
                        .make(layoutMain, getString(R.string.text_confirm_delete_sms), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.text_button_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mPhoneCustom = new PhoneCustom();
                                mPhoneCustom.setName(getString(R.string.text_none));
                                updateNameContact(mPhoneCustom);
                            }
                        });
                // Changing message text color
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();
                break;
        }
    }

    private void setPlayOrStopSound() {
        imgPlayStopSound.setImageResource(0);
        if (isPlayingSound) {
            stopSound();
            imgPlayStopSound.setBackgroundResource(R.drawable.ic_play);
        } else {
            if (uriCustomSelected != null && uriCustomSelected.getUri() != null) {
                playSound(uriCustomSelected.getUri(), volumeSeekbar);
            }
            imgPlayStopSound.setBackgroundResource(R.drawable.ic_stop);
        }
        isPlayingSound = !isPlayingSound;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm_clock);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharePreferences();
        dbHelper = new DatabaseHelper(this);

        if (getIntent().hasExtra(EXTRA_ITEM_ALARM)) {
            int idAlarm = getIntent().getIntExtra(EXTRA_ITEM_ALARM, 0);
            mItemAlarm = dbHelper.getAlarmById(idAlarm);
        }
        setTitle(R.string.text_title_add_alarm);
        if (mItemAlarm != null) {
            setTitle(R.string.text_title_update_alarm);
        }
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                hour = String.valueOf(i);
                minute = String.valueOf(i1);
                showTime(i, i1);
            }
        });

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        showTimeDefault();

        mUriCustoms = getListAlarm();

        initDataSpinnerMethodStop();

        setDataUpdate(mItemAlarm);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        layoutAlarmTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showdialog();
                if (isPlayingSound) {
                    setPlayOrStopSound();
                }
                stopSound();
                Intent intent = new Intent(AddAlarmActivity.this, GetListMp3Activity.class);
                if (uriCustomSelected != null) {
                    intent.putExtra(GetListMp3Activity.EXTRA_NAME_SOUND, uriCustomSelected.getName());
                }
                startActivityForResult(intent, REQUEST_CODE_SELECT_SOUND);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        layoutImageWallPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlayingSound) {
                    setPlayOrStopSound();
                }
                stopSound();
                Intent intent = new Intent(AddAlarmActivity.this, GetListImageActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_IMAGE_PAPER);
                overridePendingTransition(R.anim.enter, R.anim.exit);

            }
        });

        layoutContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlayingSound) {
                    setPlayOrStopSound();
                }
                stopSound();
                askForContactPermission();

            }
        });

        seekbarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                volumeSeekbar = i;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        volumeSeekbar, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbarVolume.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_screen_add_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.actionDone:
                stopSound();
                progressDone();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set data for these field if action is update alarm
     *
     * @param mItemAlarm: Object type Item alarm
     */
    private void setDataUpdate(ItemAlarm mItemAlarm) {
        if (mItemAlarm != null) {// update alarm
            int h = Integer.parseInt(mItemAlarm.getHour());
            int m = Integer.parseInt(mItemAlarm.getMinute());
            showTime(h, m);

            timePicker.setCurrentHour(h);
            timePicker.setCurrentMinute(m);

            String title = mItemAlarm.getTitle();
            etTitle.setText(title);

            setUncheckAll();

            if (mItemAlarm.getRepeatMo() == 1) {
                cbMo.setChecked(true);
            }
            if (mItemAlarm.getRepeatTu() == 1) {
                cbTu.setChecked(true);
            }
            if (mItemAlarm.getRepeatWe() == 1) {
                cbWe.setChecked(true);
            }
            if (mItemAlarm.getRepeatTh() == 1) {
                cbTh.setChecked(true);
            }
            if (mItemAlarm.getRepeatFr() == 1) {
                cbFr.setChecked(true);
            }
            if (mItemAlarm.getRepeatSa() == 1) {
                cbSa.setChecked(true);
            }
            if (mItemAlarm.getRepeatSu() == 1) {
                cbSu.setChecked(true);
            }

            uriCustomSelected = new UriCustom();
            uriCustomSelected.setUri(mItemAlarm.getUriCustom());
            uriCustomSelected.setName(mItemAlarm.getNameTone());

            pathImageSelected = mItemAlarm.getPathImageWallPaper();
            nameImageSelected = mItemAlarm.getNameImageWallPaper();
            updateNameSound(uriCustomSelected);
            setDisplayLayoutVolume(uriCustomSelected);
            updateNameImage(nameImageSelected);

            imageWallPaper.setImageResource(0);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    setDisplayImageWallPapper(pathImageSelected);
                }
            }).start();
            volumeSeekbar = mItemAlarm.getVolume();

            mPhoneCustom = new PhoneCustom();
            mPhoneCustom.setName(mItemAlarm.getNameContact());
            mPhoneCustom.setNumber(mItemAlarm.getNumberContact());
            updateNameContact(mPhoneCustom);

            seekbarVolume.setProgress(volumeSeekbar);
            mIndexMethodStopSelected = mItemAlarm.getMethodStop();
            spinnerMethod.setSelection(mIndexMethodStopSelected);
        } else {// new alarm
            uriCustomSelected = new UriCustom();
            uriCustomSelected.setUri(null);
            uriCustomSelected.setName(getResources().getString(R.string.text_none));

            pathImageSelected = "";
            nameImageSelected = getResources().getString(R.string.text_none);

            updateNameSound(uriCustomSelected);
            setDisplayLayoutVolume(uriCustomSelected);
            updateNameImage(nameImageSelected);
            imageWallPaper.setImageResource(0);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    setDisplayImageWallPapper(pathImageSelected);
                }
            }).start();
            seekbarVolume.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));

            mPhoneCustom = new PhoneCustom();
            mPhoneCustom.setName(getString(R.string.text_none));
            updateNameContact(mPhoneCustom);
        }
    }

    private void setDisplayLayoutVolume(UriCustom uriCustomSelected) {
        if (uriCustomSelected != null) {
            if (uriCustomSelected.getUri() == null) {
                layoutVolume.setVisibility(View.GONE);
            } else {
                layoutVolume.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initDataSpinnerMethodStop() {
        listMethods = new ArrayList<MethodStop>();
        nameMethods = new ArrayList<String>();
        MethodStop methodNormal = new MethodStop();
        methodNormal.setIndex(0);
        methodNormal.setName(getString(R.string.text_method_normal));
        nameMethods.add(getString(R.string.text_method_normal));
        listMethods.add(methodNormal);

        MethodStop methodShake = new MethodStop();
        methodShake.setIndex(1);
        methodShake.setName(getString(R.string.text_method_shake));
        nameMethods.add(getString(R.string.text_method_shake));
        listMethods.add(methodShake);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nameMethods);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerMethod.setAdapter(dataAdapter);
        spinnerMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mIndexMethodStopSelected = i;
                if (isPlayingSound) {
                    setPlayOrStopSound();
                }
                stopSound();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (isPlayingSound) {
                    setPlayOrStopSound();
                }
                stopSound();
            }
        });

        // int selected = sharedPreferences.getInt(SharePreferenceHelper.Key.NUMBERSHAKE, 0);
        //int selected = mUserSetting != null ? mUserSetting.getNumberShake() : 0;
        // spinnerNumberShake.setSelection(selected);
    }

    public void pickContactNumber() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_CONTACT);
    }

    private void showTimeDefault() {
        Calendar calendar = Calendar.getInstance();
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        showTime(h, m);
        hour = h + "";
        minute = m + "";
    }

    public void showTime(int hour, int min) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        String strH = hour > 9 ? hour + "" : "0" + hour;
        String strM = min > 9 ? min + "" : "0" + min;
        tvTime.setText(strH + ":" + strM + " " + format);
    }

    private void saveToDatabase(ItemAlarm itemAlarm) {
        if (mItemAlarm != null) {
            itemAlarm.setId(mItemAlarm.getId());
            dbHelper.addOrUpdateAlarm(itemAlarm);
        } else {
            dbHelper.addAlarm(itemAlarm);
        }
    }

    /**
     * Function clear data
     */
    private void clearData() {
        int hour = timePicker.getCurrentHour();
        int min = timePicker.getCurrentMinute();
        showTime(hour, min);
        etTitle.setText("");

        String msg = mItemAlarm == null ? getResources().getString(R.string.text_add_success) : getResources().getString(R.string.text_update_success);
        Snackbar snackbar = Snackbar.make(layoutMain, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
        showTimeDefault();
        setUncheckAll();
    }

    /**
     * Function set uncheck all checkbox
     */
    private void setUncheckAll() {
        cbMo.setChecked(false);
        cbTu.setChecked(false);
        cbWe.setChecked(false);
        cbTh.setChecked(false);
        cbFr.setChecked(false);
        cbSa.setChecked(false);
        cbSu.setChecked(false);
    }

    /**
     * Function set check all checkbox
     */
    private void setCheckAll() {
        cbMo.setChecked(true);
        cbTu.setChecked(true);
        cbWe.setChecked(true);
        cbTh.setChecked(true);
        cbFr.setChecked(true);
        cbSa.setChecked(true);
        cbSu.setChecked(true);
    }

    /**
     * Init item alarm
     */
    private void initItemAlarm() {
        itemAlarm = new ItemAlarm();
        itemAlarm.setHour(hour);
        itemAlarm.setStatus("0");
        itemAlarm.setMinute(minute);
        itemAlarm.setFormat(format);
        String str = etTitle.getText().toString();
        if (TextUtils.isEmpty(str)) {
            str = getResources().getString(R.string.text_alarm_clock);
        }
        itemAlarm.setTitle(str);

        itemAlarm.setStatus("0");
        if (mItemAlarm != null) {
            itemAlarm.setStatus(mItemAlarm.getStatus());
        }
        itemAlarm.setRepeatMo(cbMo.isChecked() ? 1 : 0);
        itemAlarm.setRepeatWe(cbWe.isChecked() ? 1 : 0);
        itemAlarm.setRepeatTu(cbTu.isChecked() ? 1 : 0);
        itemAlarm.setRepeatTh(cbTh.isChecked() ? 1 : 0);
        itemAlarm.setRepeatFr(cbFr.isChecked() ? 1 : 0);
        itemAlarm.setRepeatSa(cbSa.isChecked() ? 1 : 0);
        itemAlarm.setRepeatSu(cbSu.isChecked() ? 1 : 0);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        itemAlarm.setDayCreate(day);
        itemAlarm.setMonthCreate(month);
        if (uriCustomSelected == null) {
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            uriCustomSelected = new UriCustom();
            uriCustomSelected.setName(getResources().getString(R.string.text_none));
            uriCustomSelected.setUri(null);
        }
        itemAlarm.setNameTone(uriCustomSelected.getName());
        itemAlarm.setUriCustom(uriCustomSelected.getUri());


        itemAlarm.setMilisecod((Integer.parseInt(hour) * 60) + Integer.parseInt(minute));

        itemAlarm.setPathImageWallPaper(pathImageSelected);
        itemAlarm.setNameImageWallPaper(nameImageSelected);
        itemAlarm.setVolume(volumeSeekbar);

        itemAlarm.setNameContact(mPhoneCustom.getName());
        itemAlarm.setNumberContact(mPhoneCustom.getNumber());
        itemAlarm.setMethodStop(mIndexMethodStopSelected);
    }

    /**
     * progress excute when click button in actionbar
     */
    private void progressDone() {
        initItemAlarm();
        saveToDatabase(itemAlarm);
        Calendar calendar = Calendar.getInstance();
        ArrayList itemAlarms = new ArrayList<>();
        itemAlarms.addAll(dbHelper.getAllAlarms());
        ItemAlarm itemAlarmSmall = findTimeAlarmClockSmallest(itemAlarms);
        if (itemAlarmSmall != null) {
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(itemAlarmSmall.getHour()));
            calendar.set(Calendar.MINUTE, Integer.parseInt(itemAlarmSmall.getMinute()));
            calendar.set(Calendar.SECOND, 0);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
            calendar.set(Calendar.SECOND, 0);
        }

        Intent myIntent = new Intent(this, AlarmReceiver.class);
        myIntent.setAction(ACTION_ALARM_CLOCK);
        pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);

        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
        //      24*60*60*1000 , pendingIntent);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d(TAG, "setExact");
        } else {
            Log.d(TAG, "set");
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        clearData();
        onBackPressed();

    }

    /**
     * Function show dialog select tone alarm
     */
    @SuppressLint("ResourceType")
    private void showdialog() {

        dialogListSound = new Dialog(this);
        dialogListSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogListSound.setTitle("Select Item");
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.view_dialog_add, null, false);
        dialogListSound.setContentView(v);
        dialogListSound.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!


        int indexSelected = 0;
        if (uriCustomSelected != null) {
            indexSelected = indexToneInList(uriCustomSelected.getName());
        }
        if (indexSelected == 0 && uriCustomSelected != null) {
            if (!uriCustomSelected.getName().toLowerCase().equals(getResources().getString(R.string.text_none).toLowerCase())) {
                mNameUris.add(uriCustomSelected.getName());
                mUriCustoms.add(uriCustomSelected);
                indexSelected = mNameUris.size() - 1;
            }
        }

        ListView listViewSound = (ListView) dialogListSound.findViewById(R.id.listview);
        ArrayList<String> val = new ArrayList<String>();
        val.addAll(mNameUris != null ? mNameUris : new ArrayList<>());
        listViewSound.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewSound.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, val));
        listViewSound.setClickable(true);

        listViewSound.setItemChecked(indexSelected, true);
        listViewSound.smoothScrollToPosition(indexSelected);
        listViewSound.setSelection(indexSelected);

        //now that the dialog is set up, it's time to show it
        TextView btnCancel = (TextView) v.findViewById(R.id.btnCancel);
        TextView btnOk = (TextView) v.findViewById(R.id.btnOk);
        TextView btnAdd = (TextView) v.findViewById(R.id.btnAdd);

        btnCancel.setTextColor(getResources().getColorStateList(R.drawable.selector_button));
        btnOk.setTextColor(getResources().getColorStateList(R.drawable.selector_button));
        btnAdd.setTextColor(getResources().getColorStateList(R.drawable.selector_button));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogListSound.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogListSound.dismiss();
                uriCustomSelected = uriCustomSelected_temp;
                updateNameSound(uriCustomSelected);
                setDisplayLayoutVolume(uriCustomSelected);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSound();
                Intent intent = new Intent(AddAlarmActivity.this, GetListMp3Activity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_SOUND);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        listViewSound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                indexSoundSelected = i;
                UriCustom uriCustom = mUriCustoms.get(i);
                uriCustomSelected_temp = uriCustom;
                Uri uri = uriCustom.getUri();
                if (uri != null) {
                    playSound(uri, -1);
                } else {
                    stopSound();
                }

            }
        });
        dialogListSound.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                stopSound();
            }
        });
        dialogListSound.show();
    }

    /**
     * get list tone alarm of system
     *
     * @return array list
     */
    private ArrayList<UriCustom> getListAlarm() {

        RingtoneManager ringtoneMgr = new RingtoneManager(this);
        ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
        Cursor alarmsCursor = ringtoneMgr.getCursor();
        int alarmsCount = alarmsCursor.getCount();
        if (alarmsCount == 0 && !alarmsCursor.moveToFirst()) {
            return null;
        }
        mNameUris = new ArrayList<>();

        // add item NONE to list
        mNameUris.add(getResources().getString(R.string.text_none));
        ArrayList<UriCustom> alarms = new ArrayList<UriCustom>();
        UriCustom uri = new UriCustom();
        uri.setName(getResources().getString(R.string.text_none));
        uri.setUri(null);
        alarms.add(uri);
        // end

        while (!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {
            int currentPosition = alarmsCursor.getPosition();
            UriCustom uriCustom = new UriCustom();
            uriCustom.setUri(ringtoneMgr.getRingtoneUri(currentPosition));
            uriCustom.setSelected(false);
            String name = RingtoneManager.getRingtone(this, ringtoneMgr.getRingtoneUri(currentPosition)).getTitle(this);
            mNameUris.add(name);
            uriCustom.setName(name);
            alarms.add(uriCustom);
        }
        //alarmsCursor.close();

        return alarms;
    }

    /**
     * Stop sound playing
     */
    private void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    /**
     * Play sound by uri
     *
     * @param uri: Uri
     */
    private void playSound(Uri uri, int volume) {
        try {
            stopSound();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            if (volume != -1) {
//                int maxVolume = 100;
//                int currVolume = volume;
//                float log1 = 1 - (float) (Math.log(maxVolume - currVolume) / Math.log(maxVolume));
//                mediaPlayer.setVolume(log1, log1);
            }
            mediaPlayer.start();
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    /**
     * update text for edittext sound
     *
     * @param uriCustom: UriCustom
     */
    private void updateNameSound(UriCustom uriCustom) {
        imgDeleteTone.setVisibility(View.GONE);
        if (uriCustom != null) {
            etAlarmTone.setText(uriCustom.getName());
            if (uriCustom.getUri() != null) {
                imgDeleteTone.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateNameImage(String str) {
        imgDeleteWallPaper.setVisibility(View.GONE);
        if (str != null) {
            etAlarmWallPaper.setText(str);
        }
        if (!TextUtils.isEmpty(pathImageSelected)) {
            imgDeleteWallPaper.setVisibility(View.VISIBLE);
        }
    }

    private void updateNameContact(PhoneCustom phoneCustom) {
        imgDeleteSMS.setVisibility(View.GONE);
        if (phoneCustom != null) {
            String display = phoneCustom.getName();
            if (phoneCustom.getNumber() != null) {
                display += "(" + phoneCustom.getNumber() + ")";
                imgDeleteSMS.setVisibility(View.VISIBLE);
            }
            etSendSMS.setText(display);
        }
    }

    private void setDisplayImageWallPapper(String path) {
        if (!TextUtils.isEmpty(path)) {
            new AsyncTaskLoadImage().execute(path);
        }
    }

    /**
     * Find string in list
     *
     * @param nameFile: String
     * @return: int : index string in list
     */
    private int indexToneInList(String nameFile) {
        for (int i = 0; mUriCustoms != null && i < mUriCustoms.size(); i++) {
            UriCustom uriCustom = mUriCustoms.get(i);
            if (uriCustom.getName().equals(nameFile)) {
                return i;
            }
        }
        return 0;
    }

    private ItemAlarm findTimeAlarmClockSmallest(ArrayList<ItemAlarm> itemAlarms) {
        Collections.sort(itemAlarms, new TimeAlarmComparator());
        if (itemAlarms != null && itemAlarms.size() > 0) {
            return itemAlarms.get(0);
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

    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
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
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                pickContactNumber();
            }
        } else {
            pickContactNumber();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickContactNumber();

                } else {
                    Toast.makeText(this, "No permission for contacts", Toast.LENGTH_SHORT).show();

                }
                return;
            }

        }
    }

    class AsyncTaskLoadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String path = strings[0];
            if (!TextUtils.isEmpty(path)) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Glide.
                                with(getApplicationContext())
                                .load(path)
                                .into(imageWallPaper);
                    }
                };
                mainHandler.post(myRunnable);
            }
            return theBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                layoutMain.setBackground(null);
                layoutMain.setBackgroundColor(Color.TRANSPARENT);
                BitmapDrawable bd = new BitmapDrawable(layoutMain.getContext().getResources(), bitmap);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    layoutMain.setBackground(bd);
//                } else {
//                    layoutMain.setBackgroundDrawable(bd);
//                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_SOUND) {
            if (resultCode == Activity.RESULT_OK) {
                String name = data.getStringExtra(EXTRA_NAME_SOUND_SELECT);
                String path = data.getStringExtra(EXTRA_PATH_SOUND_SELECT);
                UriCustom uriCustom = new UriCustom();
                uriCustom.setUri(Uri.parse(path));
                uriCustom.setName(name);
                stopSound();
                uriCustomSelected = uriCustom;
                updateNameSound(uriCustomSelected);
                setDisplayLayoutVolume(uriCustomSelected);
            }
        } else if (requestCode == REQUEST_CODE_ADD_IMAGE_PAPER) {
            if (resultCode == Activity.RESULT_OK) {
                String name = data.getStringExtra(EXTRA_NAME_IMAGE);
                String path = data.getStringExtra(EXTRA_PATH_IMAGE);
                pathImageSelected = path;
                nameImageSelected = name;
                updateNameImage(name);
                imageWallPaper.setImageResource(0);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        theBitmap = null;

                        setDisplayImageWallPapper(pathImageSelected);
                    }
                }).start();

            }
        } else if (requestCode == REQUEST_CODE_SELECT_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();

                Cursor phone = getContentResolver().query(contactData, null, null, null, null);
                if (phone.moveToFirst()) {

                    String contactId = phone.getString(phone.getColumnIndex(ContactsContract.Contacts._ID));
                    String hasNumber = phone.getString(phone.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String contactName = phone.getString(phone.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String num = "";
                    if (Integer.valueOf(hasNumber) == 1) {
                        Cursor numbers = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        while (numbers.moveToNext()) {
                            num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        }
                    } else {
                        Toast.makeText(this, getString(R.string.text_contact_no_number), Toast.LENGTH_LONG).show();
                    }
                    mPhoneCustom = new PhoneCustom();
                    mPhoneCustom.setName(contactName);
                    mPhoneCustom.setNumber(num);
                    updateNameContact(mPhoneCustom);

                }


            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSound();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbHelper = new DatabaseHelper(this);
    }


}

