package alarmclock.app.com.alarmclock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import alarmclock.app.com.alarmclock.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 6/7/2018.
 */

public class SplashActivity extends BaseActivity {

    @BindView(R.id.tvTitleName)
    TextView tvTitleName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        RunAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        }, 3000);
    }
    private void RunAnimation()
    {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.rotate);
        a.reset();
        tvTitleName.clearAnimation();
        tvTitleName.startAnimation(a);
    }
}
