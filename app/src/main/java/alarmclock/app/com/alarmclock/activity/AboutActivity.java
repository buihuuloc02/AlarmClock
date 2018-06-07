package alarmclock.app.com.alarmclock.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.muddzdev.styleabletoastlibrary.StyleableToast;

import alarmclock.app.com.alarmclock.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 6/7/2018.
 */

public class AboutActivity extends BaseActivity {
    @BindView(R.id.tvVersionName)
    TextView tvVersionName;

    @BindView(R.id.layoutUpdate)
    View layoutUpdate;

    @BindView(R.id.layoutShare)
    View layoutShare;

    @BindView(R.id.layoutAbout)
    View layoutAbout;


    @SuppressLint("ResourceAsColor")
    @OnClick({R.id.layoutUpdate, R.id.layoutShare, R.id.layoutAbout})
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.layoutUpdate:
                new StyleableToast
                        .Builder(this)
                        .text(getString(R.string.text_check_version))
                        .textColor(Color.WHITE)
                        .backgroundColor(R.color.actionBarDarkgray)
                        .show();
                break;
            case R.id.layoutShare:
                openShareApp();
                break;
            case R.id.layoutAbout:
                openScreenAbout();
                break;
        }
    }

    private void openScreenAbout() {
        String url = getString(R.string.text_website);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void openShareApp() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        String sAux = "\nLet me recommend you this application\n\n";
        sAux = sAux + "https://play.google.com/store/apps/details?id=com.app.alarmclock.bhloc\n\n";
        i.putExtra(Intent.EXTRA_TEXT, sAux);
        startActivity(Intent.createChooser(i, "Choose one"));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        setTitle(getResources().getString(R.string.text_about_actionbar));
        setDataVersionName();
    }

    private void setDataVersionName() {
        tvVersionName.setText(getVersionName());
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
}
