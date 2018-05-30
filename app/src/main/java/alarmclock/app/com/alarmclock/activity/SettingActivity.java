package alarmclock.app.com.alarmclock.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.util.SharePreferenceHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 5/28/2018.
 */

public class SettingActivity extends BaseActivity {


    @BindView(R.id.spinnerNumberShake)
    Spinner spinnerNumberShake;

    @BindView(R.id.spinnerSpeekShake)
    Spinner spinnerSpeekShake;


    @BindView(R.id.layoutMain)
    View layoutMain;

    private SharePreferenceHelper sharedPreferences;
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharePreferences();

        setTitle(getResources().getString(R.string.text_title_setting));

        initDataNumberShake();
        initDataSpeedShake();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Init spinner number shake
     */
    private void initDataNumberShake() {
        List<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            numbers.add(i + 1);
        }

        // Creating adapter for spinner
        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, numbers);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerNumberShake.setAdapter(dataAdapter);
        spinnerNumberShake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int number = i;
                sharedPreferences.put(SharePreferenceHelper.Key.NUMBERSHAKE, number);
                if (!isFirstLoad) {
                    Snackbar snackbar = Snackbar.make(layoutMain, "Updated!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        int selected = sharedPreferences.getInt(SharePreferenceHelper.Key.NUMBERSHAKE, 0);
        spinnerNumberShake.setSelection(selected);
    }

    /**
     * Init data speed shake
     */
    private void initDataSpeedShake() {
        List<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            numbers.add(1000 + (i * 100));
        }

        // Creating adapter for spinner
        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, numbers);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerSpeekShake.setAdapter(dataAdapter);
        spinnerSpeekShake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int number = i;
                sharedPreferences.put(SharePreferenceHelper.Key.SPEEKSHAKE, number);
                if (!isFirstLoad) {
                    Snackbar snackbar = Snackbar.make(layoutMain, getResources().getString(R.string.text_updated), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                isFirstLoad = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        int selected = sharedPreferences.getInt(SharePreferenceHelper.Key.SPEEKSHAKE, 0);
        spinnerSpeekShake.setSelection(selected);
    }
}
