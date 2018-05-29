package alarmclock.app.com.alarmclock.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.adapter.AlarmAdapter;
import alarmclock.app.com.alarmclock.model.ItemAlarm;
import alarmclock.app.com.alarmclock.util.DatabaseHelper;
import alarmclock.app.com.alarmclock.util.SharePreferenceHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


    @BindView(R.id.fabAdd)
    FloatingActionButton fadAdd;

    @BindView(R.id.recyclerViewAlarm)
    RecyclerView recyclerViewAlarm;

    @BindView(R.id.swipeRefeshLayout)
    SwipeRefreshLayout swipeRefeshLayout;

    @BindView(R.id.tvNodata)
    TextView tvNodata;


    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ItemAlarm> itemAlarms;
    private AlarmAdapter alarmAdapter;
    private SharePreferenceHelper sharePreferenceHelper;
    private DatabaseHelper databaseHelper;
    private ItemAlarm itemAlarmCurrent;

    private float dX, dY;
    private int lastAction;
    private Handler handler = new Handler();



    @OnClick({R.id.fabAdd})
    public void OnButtonClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fabAdd:
                Intent intent = new Intent(this, AddAlarmActivity.class);
                startActivity(intent);
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sharePreferenceHelper = getSharePreferences();
        swipeRefeshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onRefresh() {
                initData();
            }
        });
        databaseHelper = new DatabaseHelper(this);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544/6300978111");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        //mAdView.setAdUnitId(getString(R.string.app_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        fadAdd.setOnTouchListener((View view, MotionEvent event) -> {
            //Toast.makeText(MainActivity.this, event.getActionMasked() +"", Toast.LENGTH_SHORT).show();

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dX = view.getX() - event.getRawX();
                    dY = view.getY() - event.getRawY();
                    lastAction = MotionEvent.ACTION_DOWN;
                    break;

                case MotionEvent.ACTION_MOVE:
                    float detalx = view.getX() - (event.getRawX() + dX);
                    float detaly = view.getY() - (event.getRawY() + dY);
                    view.setY(event.getRawY() + dY);
                    view.setX(event.getRawX() + dX);
                    if (detalx > 50.0 && detaly > 50.0) {

                        lastAction = MotionEvent.ACTION_MOVE;
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    if (lastAction == MotionEvent.ACTION_DOWN) {
                        Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                case MotionEvent.ACTION_BUTTON_PRESS:


                default:
                    Log.d("xx", event.getActionMasked() + " dx = " + dX + " dy = " + dY);
                    return false;
            }
            return true;
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initData() {
        layoutManager = new LinearLayoutManager(this);
        recyclerViewAlarm.setLayoutManager(layoutManager);

        itemAlarms = new ArrayList<>();

        //getDataFromShareReference();

        getDataFromDatabase();
        //update();

        alarmAdapter = new AlarmAdapter(this, itemAlarms, new AlarmAdapter.OnItemClick() {
            @Override
            public void OnClickItemAlarm(ItemAlarm itemAlarm, int position) {
                Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
                intent.putExtra(AddAlarmActivity.EXTRA_ITEM_ALARM,itemAlarm.getId());
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }

            @Override
            public void OnClickItemAlarmDelete(ItemAlarm itemAlarm, int position) {
                showDialog(MainActivity.this, "Are you sure delete?", false, "OK", "Cancel", new CallBackDismiss() {
                    @Override
                    public void callBackDismiss() {
                        databaseHelper.deleteAlarmById(itemAlarm.getId());
                        //clearAlarm();
                        initData();
                        Toast.makeText(MainActivity.this, "Delete success!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void OnClickItemSwitchChange(ItemAlarm itemAlarm, int position, boolean checked) {
                itemAlarm.setStatus(checked ? "0" : "1");
                databaseHelper.addOrUpdateAlarm(itemAlarm);
            }
        });

        recyclerViewAlarm.setAdapter(alarmAdapter);
        swipeRefeshLayout.setRefreshing(false);
        if (itemAlarms.size() > 0) {
            tvNodata.setVisibility(View.GONE);
            recyclerViewAlarm.setVisibility(View.VISIBLE);
        } else {
            tvNodata.setVisibility(View.VISIBLE);
            recyclerViewAlarm.setVisibility(View.GONE);
        }

        //addAlarm();
    }

    public void getDataFromShareReference() {
        String str = sharePreferenceHelper.getString(SharePreferenceHelper.Key.ALARMCLOCK, "");
        if (!TextUtils.isEmpty(str)) {
            String[] arr = str.split("##");
            if (arr.length > 0) {
                for (int i = 0; i < arr.length; i++) {
                    String alarm = arr[i];
                    String[] temp = alarm.split("#");
                    if (temp.length >= 3) {
                        ItemAlarm itemAlarm = new ItemAlarm();
                        itemAlarm.setHour(temp[0]);
                        itemAlarm.setMinute(temp[1]);
                        itemAlarm.setTitle(temp[2]);
                        itemAlarms.add(itemAlarm);
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        //clearAlarm();
        initData();
        //handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void getDataFromDatabase() {
        itemAlarms = new ArrayList<>();
        itemAlarms.addAll(databaseHelper.getAllAlarms());
    }

    private void update() {
        if (itemAlarms != null && itemAlarms.size() > 0) {
            ItemAlarm itemAlarm = itemAlarms.get(0);
            itemAlarm.setStatus("2");
            databaseHelper.addOrUpdateAlarm(itemAlarm);
            itemAlarms = new ArrayList<>();
            itemAlarms.addAll(databaseHelper.getAllAlarms());
        }
    }

    public boolean checkHasAlarm() {

        int hours = new Time(System.currentTimeMillis()).getHours();
        int m = new Time(System.currentTimeMillis()).getMinutes();
        for (ItemAlarm itemAlarm : itemAlarms) {
            if (itemAlarm.getHour().equals(hours + "")) {
                if (itemAlarm.getMinute().equals(m + "")) {
                    itemAlarmCurrent = itemAlarm;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionSetting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    boolean startActivity = false;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (checkHasAlarm()) {

                if (!startActivity) {

                    Intent myintent = new Intent(MainActivity.this, AlarmClockActivity.class);
                    myintent.putExtra("TIME", itemAlarmCurrent.getHour() + " : " + itemAlarmCurrent.getMinute());
                    startActivity = true;
                    startActivity(myintent);
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

    public void clearAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent updateServiceIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingUpdateIntent = PendingIntent.getService(this, 0, updateServiceIntent, 0);

        // Cancel alarms
        try {
            alarmManager.cancel(pendingUpdateIntent);
        } catch (Exception e) {
            Log.e("MainActivity", "AlarmManager update was not canceled. " + e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar1 = Calendar.getInstance();
        int h = calendar1.get(Calendar.HOUR_OF_DAY);
        int m = calendar1.get(Calendar.MINUTE);
        String str = "";
        for (ItemAlarm itemAlarm : itemAlarms) {
            str += " \n" + itemAlarm.getStatus();
            if (itemAlarm.getStatus().equals("0")) {


                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(itemAlarm.getHour()));
                calendar.set(Calendar.MINUTE, Integer.parseInt(itemAlarm.getMinute()));

                Intent myIntent = new Intent(this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }

            }
        }
        Log.d("alarm", str);
    }
}
