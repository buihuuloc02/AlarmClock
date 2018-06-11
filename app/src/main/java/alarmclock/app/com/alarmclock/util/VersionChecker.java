package alarmclock.app.com.alarmclock.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.ContextThemeWrapper;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;

import alarmclock.app.com.alarmclock.R;

/**
 * Created by Administrator on 6/11/2018.
 */

public class VersionChecker extends AsyncTask<String, String, JSONObject> {
    private static final String TAG = VersionChecker.class.getSimpleName();
    private String latestVersion;
    private String currentVersion;
    private Context context;

    public VersionChecker(String currentVersion, Context context) {
        this.currentVersion = currentVersion;
        this.context = context;
    }

    public void showForceUpdateDialog(boolean hasNewVersion) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context,
                R.style.AppTheme));

        String title = context.getResources().getString(R.string.text_notice);
        String msg = context.getResources().getString(R.string.text_current_version_lastest);
        if (hasNewVersion) {
            title = context.getResources().getString(R.string.text_title_notice_has_version);
            msg = context.getResources().getString(R.string.text_message_new_version);
        }
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setCancelable(false);
        if (hasNewVersion) {
            alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.text_button_update), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.text_button_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
        } else {
            alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.text_button_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
        }
        alertDialogBuilder.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected JSONObject doInBackground(String... params) {

        try {
            latestVersion = currentVersion();

        } catch (IOException e) {
        }
        return new JSONObject();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String currentVersion() throws IOException {
        org.jsoup.nodes.Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=com.app.alarmclock.bhloc&hl=en")
                .timeout(30000)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com")
                .get();
        Elements element = document.select(".htlgb").select(".htlgb");
        if (element.size() >= 5) {
            latestVersion = document.select(".htlgb").select(".htlgb").get(5).ownText();
        }
        return latestVersion;

    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (latestVersion != null) {
            boolean hasNewVersion = false;
            if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                hasNewVersion = true;
            }
            showForceUpdateDialog(hasNewVersion);
        }
        super.onPostExecute(jsonObject);
    }
}
