package alarmclock.app.com.alarmclock.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.adapter.AlarmAdapter;
import alarmclock.app.com.alarmclock.model.ItemAlarm;
import alarmclock.app.com.alarmclock.util.Constant;
import alarmclock.app.com.alarmclock.util.DatabaseHelper;
import alarmclock.app.com.alarmclock.util.RecyclerItemTouchHelper;
import alarmclock.app.com.alarmclock.util.SharePreferenceHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

public class MainActivity extends BaseActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.layoutMain)
    View layoutMain;

    @BindView(R.id.layoutAdmob)
    View layoutAdmob;

    @BindView(R.id.fabAdd)
    FloatingActionButton fadAdd;

    @BindView(R.id.recyclerViewAlarm)
    RecyclerView recyclerViewAlarm;

    @BindView(R.id.swipeRefeshLayout)
    SwipeRefreshLayout swipeRefeshLayout;

    @BindView(R.id.tvNodata)
    TextView tvNodata;


    @BindView(R.id.adView)
    AdView mAdView;


    @BindView(R.id.imgDefault)
    ImageView imgDefault;


    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ItemAlarm> itemAlarms;
    private AlarmAdapter alarmAdapter;
    private SharePreferenceHelper sharePreferenceHelper;
    private DatabaseHelper databaseHelper;
    private ItemAlarm itemAlarmCurrent;

    private float dX, dY;
    private int lastAction;
    private Handler handler = new Handler();
    boolean startActivity = false;
    NetworkChangeReceiver receiver;

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
        imgDefault.setVisibility(View.GONE);
        mAdView.setVisibility(View.GONE);
        MobileAds.initialize(getApplicationContext(), getString(R.string.banner_home_footer_app_id));//
        mAdView = (AdView) findViewById(R.id.adView);
        //mAdView.setAdUnitId(getString(R.string.app_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                imgDefault.setVisibility(View.GONE);
                mAdView.setVisibility(View.GONE);
            }
        });
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
        EventBus.getDefault().register(this);

    }

    @Subscribe
    public void getMessage(String msg) {
        if (msg == "internet_change") {
            boolean hasNetwork = false;
            if (isNetworkEnabled()) {
                hasNetwork = true;
            }
            setAdmobDefault(hasNetwork);
            //setLayoutRecycler();
            //setLayoutButtonAddAlarm();
        }
    }

    public void setAdmobDefault(boolean hasInternet) {
        imgDefault.setVisibility(hasInternet ? View.GONE : View.GONE);
        mAdView.setVisibility(hasInternet ? View.VISIBLE : View.GONE);
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
                intent.putExtra(AddAlarmActivity.EXTRA_ITEM_ALARM, itemAlarm.getId());
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
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerViewAlarm);

        //addAlarm();
    }

    public void getDataFromDatabase() {
        itemAlarms = new ArrayList<>();
        itemAlarms.addAll(databaseHelper.getAllAlarms());
        Collections.sort(itemAlarms, new TimeAlarmComparator());
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
        Intent intent = null;
        switch (id) {
            case R.id.actionSetting:
                intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                return true;
            case R.id.actionAbout:
                intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
        Log.d(TAG, str);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AlarmAdapter.AlarmHolder) {
            // get the removed item name to display it in snack bar
            String name = itemAlarms.get(viewHolder.getAdapterPosition()).getTitle();

            // backup of removed item for undo purpose
            final ItemAlarm deletedItem = itemAlarms.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            alarmAdapter.removeItem(viewHolder.getAdapterPosition());
            databaseHelper.deleteAlarmById(deletedItem.getId());
            initData();
            Toast.makeText(this, getResources().getString(R.string.text_delete_success), Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        //clearAlarm();
        initData();
        receiver = new NetworkChangeReceiver();
        final IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
        //setLayoutRecycler();
        //setLayoutButtonAddAlarm();
        setDisplayAdmob();

        //showRateApp();
    }

    private void showRateApp() {

        AppRate.with(getApplicationContext())
                .setInstallDays(0)
                .setLaunchTimes(0)
                .setRemindInterval(0)
                .setDebug(true)
                .setShowNeverButton(false)
                .setOnClickButtonListener(new OnClickButtonListener() {
                    @Override
                    public void onClickButton(int which) {

                        AppRate.with(getApplicationContext()).clearSettingsParam();
                        if (which == -1) {//Rate
                            AppRate.with(getApplicationContext()).setAgreeShowDialog(false); //not show again
                        }
                    }
                })
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);
    }

    @SuppressLint("ResourceAsColor")
    private void setLayoutButtonAddAlarm() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fadAdd.getLayoutParams();
        fadAdd.setVisibility(View.VISIBLE);
        int result = sharePreferenceHelper.getInt(SharePreferenceHelper.Key.KEY_PURCHASE, Constant.DO_NOT_PURCHASE);
        if (result == Constant.PURCHASED) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            //lp.removeRule(RelativeLayout.ABOVE, layoutAdmob.getId());
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            int margin = R.dimen.margin_10;
            lp.setMargins(margin, margin, margin, margin);
            layoutAdmob.setVisibility(View.GONE);
        } else {
            lp.addRule(RelativeLayout.ABOVE, layoutAdmob.getId());
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, layoutAdmob.getId());
        }
        fadAdd.setLayoutParams(lp);
    }

    public class TimeAlarmComparator implements Comparator<ItemAlarm> {
        public int compare(ItemAlarm left, ItemAlarm right) {
            Long t1 = left.getMilisecod();
            Long t2 = right.getMilisecod();
            return t1.compareTo(t2);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void setDisplayAdmob() {
        layoutAdmob.setVisibility(View.VISIBLE);
        int result = sharePreferenceHelper.getInt(SharePreferenceHelper.Key.KEY_PURCHASE, Constant.DO_NOT_PURCHASE);
        if (result == Constant.PURCHASED) {
            layoutAdmob.setVisibility(View.GONE);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void setLayoutRecycler() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fadAdd.getLayoutParams();

        int result = sharePreferenceHelper.getInt(SharePreferenceHelper.Key.KEY_PURCHASE, Constant.DO_NOT_PURCHASE);
        if (result == Constant.PURCHASED) {
        } else {
            lp.addRule(RelativeLayout.ABOVE, layoutAdmob.getId());
        }
        recyclerViewAlarm.setLayoutParams(lp);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
}
