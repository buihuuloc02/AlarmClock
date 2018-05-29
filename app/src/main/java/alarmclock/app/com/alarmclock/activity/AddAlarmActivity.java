package alarmclock.app.com.alarmclock.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.model.ItemAlarm;
import alarmclock.app.com.alarmclock.model.UriCustom;
import alarmclock.app.com.alarmclock.util.DatabaseHelper;
import alarmclock.app.com.alarmclock.util.SharePreferenceHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 5/9/2018.
 */

public class AddAlarmActivity extends BaseActivity {

    public static final String EXTRA_ITEM_ALARM = "EXTRA_ITEM_ALARM";
    @BindView(R.id.tvTime)
    TextView tvTime;

    @BindView(R.id.etTitle)
    EditText etTitle;


    @BindView(R.id.timePicker)
    TimePicker timePicker;

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

    @BindView(R.id.etAlarmTone)
    TextView etAlarmTone;


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

    private void initItemAlarm() {
        itemAlarm = new ItemAlarm();
        itemAlarm.setHour(hour);
        itemAlarm.setStatus("0");
        itemAlarm.setMinute(minute);
        itemAlarm.setFormat(format);
        String str = etTitle.getText().toString();
        if (TextUtils.isEmpty(str)) {
            str = "Alarm clock!";
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
            uriCustomSelected.setName("None");
            uriCustomSelected.setUri(null);
        }
        itemAlarm.setNameTone(uriCustomSelected.getName());
        itemAlarm.setUriCustom(uriCustomSelected.getUri());
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

        showTimeDefault();

        mUriCustoms = getListAlarm();

        setDataUpdate(mItemAlarm);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        etAlarmTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdialog();
            }
        });


    }

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
            updateNameSound(uriCustomSelected);
        }else{// new alarm
            uriCustomSelected = new UriCustom();
            uriCustomSelected.setUri(null);
            uriCustomSelected.setName("None");
            updateNameSound(uriCustomSelected);
        }
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

    private void saveAlarmToSharePreference(ItemAlarm itemAlarm) {
        String times = sharedPreferences.getString(SharePreferenceHelper.Key.ALARMCLOCK, "");
        String str = itemAlarm.getHour() + "#" + itemAlarm.getMinute() + "#" + itemAlarm.getTitle();
        times += "##" + str;
        sharedPreferences.put(SharePreferenceHelper.Key.ALARMCLOCK, times);
    }

    /**
     * Function clear data
     */
    private void clearData() {
        int hour = timePicker.getCurrentHour();
        int min = timePicker.getCurrentMinute();
        showTime(hour, min);
        etTitle.setText("");

        String msg = mItemAlarm == null ? "Add success!" : "Update success!";
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
                progressDone();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * progress excute when click button in actionbar
     */
    private void progressDone() {
        initItemAlarm();
        saveToDatabase(itemAlarm);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
        calendar.set(Calendar.SECOND, 0);

        Intent myIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
        //      24*60*60*1000 , pendingIntent);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
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

        ListView listViewSound = (ListView) dialogListSound.findViewById(R.id.listview);
        ArrayList<String> val = new ArrayList<String>();
        val.addAll(mNameUris != null ? mNameUris : new ArrayList<>());
        listViewSound.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewSound.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, val));
        listViewSound.setClickable(true);

        int indexSelected = 0;
        if (uriCustomSelected != null) {
            indexSelected = indexToneInList(uriCustomSelected.getName());
        }
        listViewSound.setItemChecked(indexSelected, true);
        listViewSound.smoothScrollToPosition(indexSelected);
        listViewSound.setSelection(indexSelected);
        //now that the dialog is set up, it's time to show it
        TextView btnCancel = (TextView) v.findViewById(R.id.btnCancel);
        TextView btnOk = (TextView) v.findViewById(R.id.btnOk);

        btnCancel.setTextColor(getResources().getColorStateList(R.drawable.selector_button));
        btnOk.setTextColor(getResources().getColorStateList(R.drawable.selector_button));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSound();
                dialogListSound.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSound();
                dialogListSound.dismiss();
                uriCustomSelected = uriCustomSelected_temp;
                updateNameSound(uriCustomSelected);
            }
        });
        listViewSound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                indexSoundSelected = i;
                UriCustom uriCustom = mUriCustoms.get(i);
                uriCustomSelected_temp = uriCustom;
                Uri uri = uriCustom.getUri();
                if(uri != null) {
                    playSound(uri);
                }else{
                    stopSound();
                }

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
        mNameUris.add("None");
        ArrayList<UriCustom> alarms = new ArrayList<UriCustom>();
        UriCustom uri = new UriCustom();
        uri.setName("None");
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
    private void playSound(Uri uri) {
        try {
            stopSound();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    stopSound();
//                }
//            }, 3000);
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
        if (uriCustom != null) {
            etAlarmTone.setText(uriCustom.getName());
        }
    }

    /**
     * Find string in list
     *
     * @param nameFile: String
     * @return: int : index string in list
     */
    private int indexToneInList(String nameFile) {
        for (int i = 0;mUriCustoms != null && i < mUriCustoms.size(); i++) {
            UriCustom uriCustom = mUriCustoms.get(i);
            if (uriCustom.getName().equals(nameFile)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dbHelper != null){
            dbHelper.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbHelper = new DatabaseHelper(this);
    }
}

