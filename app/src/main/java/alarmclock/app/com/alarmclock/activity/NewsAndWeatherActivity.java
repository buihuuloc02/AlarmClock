package alarmclock.app.com.alarmclock.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import alarmclock.app.com.alarmclock.R;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 6/12/2018.
 */

public class NewsAndWeatherActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news_and_weather);
        setTitle(R.string.text_title_scree_news_and_weather);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
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

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
////Do Code Here
//// If want to block just return false
//            return false;
//        }
//        if (keyCode == KeyEvent.KEYCODE_MENU) {
////Do Code Here
//// If want to block just return false
//            return false;
//        }
//        if (keyCode == KeyEvent.KEYCODE_HOME) {
////Do Code Here
//// If want to block just return false
//            return false;
//        }
//        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
////Do Code Here
//// If want to block just return false
//            return false;
//        }
//        if (keyCode == KeyEvent.KEYCODE_SETTINGS) {
////Do Code Here
//// If want to block just return false
//            return false;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }

    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (blockedKeys.contains(event.getKeyCode())) {
//            Toast.makeText(this, "Volume", Toast.LENGTH_SHORT).show();
//            return true;
//        } else {
//            return super.dispatchKeyEvent(event);
//        }
//    }
}
